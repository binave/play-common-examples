
package org.binave.examples.data.impl;

import lombok.extern.slf4j.Slf4j;
import org.binave.examples.data.api.DataSource;
import org.binave.common.api.SourceBy;
import org.binave.common.collection.IndexMap;
import org.binave.play.config.api.UpdateNotify;
import org.binave.play.config.api.ConfLoader;
import org.binave.play.config.args.UpdateNotice;
import org.binave.play.data.api.DBConnect;
import org.binave.play.data.args.Dao;
import org.binave.play.data.args.TypeSql;
import org.binave.play.data.db.factory.DBConnectFactory;
import org.binave.play.data.db.impl.TypeSqlSourceImpl;
import org.binave.play.tag.Skenlr;

import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 获得落地数据
 * 基于数据库实现的落地数据获取
 *
 * @author by bin jin on 2017/4/11.
 */
@Skenlr.implement
@Slf4j
public class BeanUpdateNotifyDataSourceImpl implements DataSource, UpdateNotify {

    /**
     * 分 pool，pool 对应的连接池不由 DBConnectImpl 维护
     */
    private Map<Integer, DBConnect<Dao>> dbConnectMap = new IndexMap<>();

    /**
     * 配置模块
     */
    @Skenlr.inject
    private ConfLoader loader;

    private DBConnect<Dao> getDBConnect(int pool) {
        DBConnect<Dao> dbConnect = dbConnectMap.get(pool);
        if (dbConnect == null) throw new RuntimeException("pool is empty: " + pool);
        return dbConnect;
    }

    /**
     * todo 支持中途刷新
     */
    public void init() {

        log.info("[init]");

        // 获得每个 pool 对应的 jdbc url
        List<String> poolUrls = loader.loadBaseConfig("jdbc_player_pool");
        notify(new UpdateNotice(0, true, poolUrls.toArray(new String[poolUrls.size()])));
    }

    @Override
    public long getVersion(int pool) {
        return dbConnectMap.get(pool).getVersion();
    }

    @Override
    public <T extends Dao> List<T> getBeans(int pool, Class<T> clazz) {
        return getBeans(pool, clazz, null);
    }

    // 获得目标对象
    @Override
    public <T extends Dao> List<T> getBeans(int pool, Class<T> clazz, String whereCondition) {
        try {
            if (log.isInfoEnabled())
                log.info("[getBeans] pool={}, type={}, whereCondition={}", pool, clazz.getSimpleName(), whereCondition);
            return getDBConnect(pool).list(clazz, whereCondition);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 通过 id 获得对象
    @Override
    public <T extends Dao> T getBeanById(int pool, Class<T> clazz, long id) {
        try {
            if (log.isInfoEnabled())
                log.info("[getBeanById] pool={}, type={}, id={}", pool, clazz.getSimpleName(), id);
            return getDBConnect(pool).get(clazz, "id = " + id);
        } catch (SQLException e) {
            log.error("{}", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public long count(int pool, Class<?> clazz) {
        return 0;
    }

    // 更新或新建对象，负责 id 分配
    @Override
    public boolean updateBean(Dao dao) {
        if (dao == null) return false;
        try {

            // 测试是否是新用户
            if (dao.getId() == 0 || dao.getPool() == 0)
                return false;
//            {
//                log.info("[updateBean] dao={}", dao);
//                dao.setId(allocator.adder());
//                dao.setPool(allocator.createPoolId());
//                getDBConnect(dao.getPool()).add(dao);
//                return true;
//            } else
            // todo 在日志中打印 sql，异步处理老的数据
            return 1 == getDBConnect(dao.getPool()).update(dao);
        } catch (SQLException e) {
            if (e.getMessage().contains("for key 'nikeName' Query: INSERT INTO")) {
                e.printStackTrace();
                return false;
            }

            throw new RuntimeException(e);
        }

    }

    // TypeSql 源头
    private SourceBy<Class<? extends Dao>, TypeSql> sqlSource = new TypeSqlSourceImpl();

    /**
     * 更新数据库链接
     */
    @Override
    public synchronized void notify(UpdateNotice notice) {

        String[] tokens = notice.getTokens();

        // todo 临时过滤
        if (tokens == null || tokens.length == 0 || !Pattern.matches("[0-9]+", tokens[0])) return;

        boolean override = notice.isOverride();
        long version = notice.getVersion();

        boolean update = false;

        Map<Integer, DBConnect<Dao>> connectMap = new IndexMap<>();

        // 不是覆盖就是追加
        if (!override) connectMap.putAll(dbConnectMap);

        for (String poolText : tokens) {

            List<String> r = loader.loadBaseConfig("jdbc_player_url_" + poolText);
            if (r == null || r.isEmpty()) continue;
            update = true;
            DBConnect<Dao> dbConnect = DBConnectFactory.createDBConnect(r.get(0), sqlSource);
            dbConnect.setVersion(version); // 对于后续事务版本的逻辑支持
            connectMap.put(Integer.valueOf(poolText), dbConnect);
        }

        if (!update) throw new RuntimeException("db connect config is empty");

        // 替换配置
        dbConnectMap = connectMap;

        if (!override) // 更新版本号
            for (DBConnect dbConnect : dbConnectMap.values()) {
                if (dbConnect.getVersion() != version)
                    dbConnect.setVersion(version);
            }
    }

    private String[] tabs = new String[]{"jdbc_player"};

    @Override
    public String[] tab() {
        return tabs;
    }
}
