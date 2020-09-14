
package org.binave.examples.config.impl;

import lombok.extern.slf4j.Slf4j;
import org.binave.examples.conf.api.TokenVerifier;
import org.binave.examples.data.args.GlobalCount;
import org.binave.examples.data.args.Resource;
import org.binave.examples.config.ProtoStuffCodec;
import org.binave.examples.config.args.Resources;
import org.binave.common.util.CharUtil;
import org.binave.common.util.EnvLoader;
import org.binave.common.util.FutureTime;
import org.binave.common.util.MonitorUtil;
import org.binave.play.config.api.ConfLoader;
import org.binave.play.config.api.Ration;
import org.binave.play.config.api.UpdateNotify;
import org.binave.play.config.args.ConfigEditor;
import org.binave.play.config.args.UpdateNotice;
import org.binave.play.config.factory.RationFactory;
import org.binave.play.config.JsonUtil;
import org.binave.play.data.api.Cache;
import org.binave.play.data.api.DBConnect;
import org.binave.play.data.api.DBTransact;
import org.binave.play.data.args.Dao;
import org.binave.play.data.cache.factory.CacheFactory;
import org.binave.play.data.db.factory.DBConnectFactory;
import org.binave.play.data.db.impl.TypeSqlSourceImpl;
import org.binave.play.tag.Skenlr;
import redis.clients.jedis.Jedis;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * 配置模块，用于获取配置
 *
 * @author by bin jin on 2017/2/26.
 */
@Skenlr.implement
@Slf4j
public class GlobalConfLoaderTokenVerifierImpl implements ConfLoader, TokenVerifier {

    // 配置 Map
    private Map<String, List<String>> varMap = new HashMap<>();

    // 配置 数据库链接
    private DBTransact<Dao> confConnect;

    // 缓存
    private Cache cache;

    // 局部 id 分配器
    private static Ration ration = RationFactory.createRation(1000);

    // 分配器起始位置
    private static int idHead;
    private static int idSize = 17; // 补给大小

    /**
     * 所有通知
     */
    @Skenlr.inject
    private Set<UpdateNotify> updateNotifySet;

    // 定时任务，模拟配置更新。保证最高优先级
    public void init() {

        log.info("[-- init --]");

        //// 从本地配置文件中读取键值对 ////
        varMap.putAll(EnvLoader.getProperties("config.properties"));
        // 从环境变量中获得配置，覆盖上一个配置，获得更高的优先级
        varMap.putAll(EnvLoader.envProperties());

        // todo 如果刷新配置，需要使用写时复制

        //////// 获得配置数据库
        confConnect = DBConnectFactory.createDBTransact(
                varMap.get("conf_jdbc_url").get(0), new TypeSqlSourceImpl()
        );

        /////// redis 配置，暂时先不支持更新
        cache = CacheFactory.createCache(
                new Jedis(varMap.get("conf_redis_url").get(0)),
                "config",
                FutureTime.HOUR_OF_DAY, 2, // 每天两点
                new ProtoStuffCodec()
        );

        // 补给
        getIdFromDB(idSize);

        initMap();
        refresh();

    }


    public void refresh() {
        // 刷新配置
        long ver = adder();

        // 首次初始化配置
        for (UpdateNotify updateNotify : updateNotifySet) {

//            // 更新
//            if (updateNotify.tab().contains("BaseEquip")) {
//                Set<String> strings = new HashSet<>();
//                strings.add("BaseEquip");
//                updateNotify.notify(new UpdateNotice(
//                        ver, true, strings
//                ));
//            } else if (updateNotify.tab().contains("SkillJson")) {
//                Set<String> strings = new HashSet<>();
//                strings.add("SkillJson");
//                updateNotify.notify(new UpdateNotice(
//                        ver, true, strings
//                ));
//            }

            // 全部初始化
            updateNotify.notify(new UpdateNotice(ver, true, updateNotify.tab()));

        }
    }

    // 从数据库里面拿到 id todo 需要定时任务监测 availablePermits 是否为 0，如果为 0 ，则进行获取，以防卡死
    void getIdFromDB(int size) {

        // 数据库行锁，保证只有一个服务器的一个线程在进行下面的操作
        long stamp = confConnect.lock();

        log.info("[getIdFromDB] stamp={}", stamp);

        try {
            // 默认为 id 分配器
            GlobalCount gCount = confConnect.get(stamp, GlobalCount.class, "id = 1");

            int maxId = gCount.getCount();
            log.info("[getIdFromDB] head={}", maxId);

            // 拿到 size 个 id
            gCount.setCount(maxId + size);

            log.info("[getIdFromDB] tail={}", gCount.getCount());

            // 更新回数据库
            confConnect.update(stamp, gCount);

            ration.supply(size); // 局部数据
            idHead = maxId; // 保存左端点

        } catch (SQLException e) {
            // 出错回滚
            confConnect.rollback(stamp);
        } finally {
            // 必须解锁
            confConnect.unlock(stamp);
        }
    }

    // key 需要集中管理，此处暂时放置这里
    private final static String CONF_KEY = "GCLA_C";

    /**
     * 需要提供 http 等调用更新
     */
    private void initMap() {

        try {
            log.info("[initMap]: ready");

            long timePoint = System.currentTimeMillis();

            // 从缓存中读取
            Resources resources = cache.get(CONF_KEY, Resources.class);

            // 全量读取 json 配置
            List<Resource> resourceList = resources != null ?
                    // 从缓存中获得
                    resources.getResources() :
                    // 从数据库查询
                    ((DBConnect) confConnect).list(Resource.class, null);

            // 树莓派上，redis 要比 数据库快一倍，但是依然要 500 毫秒以上。
            log.info("[initMap] use time: {}", MonitorUtil.timeMillisFormat(System.currentTimeMillis() - timePoint));

            // 对配置进行组装
            Map<String, Resource> resourceCache = new HashMap<>();
            for (Resource resource : resourceList) {
                resourceCache.put(
                        resource.getResKey(), resource
                );
            }

            // 放入内部缓存
            jsonMap = resourceCache;

            // 执行模拟配置刷新线程，之后更新，需要写入缓存
            ExecutorService exec = Executors.newCachedThreadPool();
            exec.submit((Callable) () -> {
                while (true) {
                    try {
                        Thread.sleep(1000 * 60 * 5);
                    } catch (InterruptedException ignored) {
                    }
                    refresh();
                }
            });

            // 放入 redis 缓存
            if (resources == null) {
                resources = new Resources();
                resources.setResources(resourceList);
                cache.put(CONF_KEY, resources);
            }

            log.info("[initMap]: size={}", jsonMap.size());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * json 缓存
     */
    private Map<String, Resource> jsonMap = new HashMap<>(0);

    /**
     * class 缓存
     */
    private Map<String, Class<? extends ConfigEditor>> classMap = new HashMap<>();

    /**
     * 从 给定路径下搜索 class
     * 从 entity_paths 下搜索 class
     * 未来会加入当前的 classloader 配置
     */
    @Deprecated
    private Class<? extends ConfigEditor> getType(String simpleName) {
        Class<? extends ConfigEditor> type = classMap.get(simpleName);
        if (type == null) {
            for (String path : varMap.get("entity_paths")) {
                try {
                    type = (Class<? extends ConfigEditor>) Thread.
                            currentThread().
                            getContextClassLoader().
                            // like: org.apache.json.Clear
                                    loadClass(path + '.' + simpleName);
                } catch (ClassNotFoundException e) {
                    log.error("class not found: {}.{}", path, simpleName);
                }
                if (type != null) {
                    classMap.put(simpleName, type);
                    return type;
                }
            }
            return null;
        }
        return type;
    }

    // 获取配置
    @Override
    public List<? extends ConfigEditor> loadLogicConfig(String token) {

        if (token == null || !Pattern.matches("^[_A-Za-z]([_A-Za-z0-9])*", token)) {
            return new ArrayList<>(0);
        }

        token = token.replaceAll("\\.json$", "");

        Class<? extends ConfigEditor> type = getType(token);

        if (type == null) {
            log.info("[loadLogicConfig]: token={}", token);
            return new ArrayList<>(0);
        }

        Resource resource = jsonMap.get(token + ".json");

        if (resource == null) return new ArrayList<>(0);

        log.info("[loadLogicConfig]: token={}", token);

        // e.g. name
        return JsonUtil.getCaseInsensitive(
                type,
                resource.getData(),
                "entities"
        );
    }

    /**
     * 加载时需要根据分隔符进行分割
     * todo 需要对接其他配置落地方式
     */
    @Override
    public List<String> loadBaseConfig(String token) {
        List<String> list = varMap.get(token);
        // 拿不到直接抛出异常，便于调试
        if (list == null) throw new IllegalArgumentException("config not found. key=" + token);
        return list;
    }

    /**
     * 原来用于 id 分配器
     */
    private long adder() {
        long id;
        // 尝试获得 id 库存，如果没有获得会卡住
        if ((id = ration.consume()) == 0) getIdFromDB(idSize);
        log.info("[adder] ava={}, id={}", id, (idHead + id));
        return idHead + id;
    }

    private final static String TOKEN_PREFIX = "TK_";
    private final static String TOKEN_REGEX = ".*,[1-9][0-9]*$"; // 验证后面是不是 ，号 + 数字

    /**
     * token 验证实现 todo 未测试
     *
     * @return token 中的 pool id
     */
    @Override
    public int verify(String key, String token) {
        String value = cache.get(TOKEN_PREFIX + key, String.class);

        // 没有，返回 -1
        if (value == null) return -1;

        // 无论是否错误，都删除
        cache.remove(TOKEN_PREFIX + key);

        if (!value.equals(token) || !Pattern.matches(TOKEN_REGEX, token)) {
            // 输出日志错误
            return -2;
        }

        try {
            // 拿到最后的数字
            return CharUtil.getInteger(value, -1);
        } catch (IllegalArgumentException ignored) {
            return -1; // 未取到
        }

    }
}
