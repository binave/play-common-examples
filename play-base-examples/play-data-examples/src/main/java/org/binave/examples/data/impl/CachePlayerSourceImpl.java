package org.binave.examples.data.impl;

import org.binave.examples.config.ProtoStuffCodec;
import org.binave.examples.data.api.DataSource;
import org.binave.examples.data.args.Player;
import org.binave.examples.data.api.PlayerSource;
import org.binave.common.util.CodecUtil;
import org.binave.common.util.FutureTime;
import org.binave.play.config.api.ConfLoader;
import org.binave.play.data.api.Cache;
import org.binave.play.data.args.Access;
import org.binave.play.data.cache.ConsistentCachePool;
import org.binave.play.data.cache.factory.CacheFactory;
import org.binave.play.tag.Skenlr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * 用户缓存管理信息
 *
 * 在本地维护最活跃的部分缓存，
 * 在 redis 上维护包括本地在内的更多的缓存。
 *
 * todo 实现定时任务调度接口
 *
 * @author by bin jin on 2017/3/17.
 */
@Skenlr.implement
public class CachePlayerSourceImpl implements PlayerSource {

    private static Logger log = LoggerFactory.getLogger(CachePlayerSourceImpl.class);

    // 缓存池
    private static ConsistentCachePool cachePool;

    // 本地缓存
    private Cache localCache;

    public void init() {

        log.info("init");

        // 使用 MURMUR3
        cachePool = new ConsistentCachePool(CodecUtil.ConsistentHash.MURMUR3);
        localCache = CacheFactory.createCache();
        refresh();
    }

    @Skenlr.inject
    private ConfLoader confLoader;

    /**
     * 刷新
     * todo 需要实现定时任务调度接口
     */
    public void refresh() {

        log.info("refresh");

        List<Cache> cacheList = new ArrayList<>();

        for (String i : confLoader.loadBaseConfig("player_redis_pool")) {

            List<String> conf = confLoader.loadBaseConfig("player_redis_url_" + i);

            Jedis jedis = new Jedis(conf.get(0));
            jedis.select(Integer.valueOf(conf.get(1))); // 设置 DB

            // 如果无法使用
            if (!"PONG".equals(jedis.ping())) {
                // todo 日志提示链接有问题
                log.error("connect error, url: {}", conf.get(0));
                continue;
            }

            cacheList.add(
                    CacheFactory.createBoundedCache(
                            "PLAYER_" + i,
                            jedis,
                            FutureTime.HOUR_OF_DAY, 2, // 每天 2 点缓存失效
                            new ProtoStuffCodec()
                    )
            );
        }

        if (cacheList.isEmpty()) throw new IllegalArgumentException("config is empty.");

        // 覆盖
        cachePool.put(true, cacheList);

        log.info("[refresh]: cache size={}", cachePool.size());
    }

    /**
     * 修剪
     * todo 需要实现定时任务调度接口
     */
    public void trim(int live) {
        localCache.trim(live);
        cachePool.trim(live);
    }

    // 由于是根索引，并且其中包含主接口实现，所以由框架进行赋值

    @Skenlr.inject
    private DataSource dataSource;

    @Override
    public Player get(Access access) {

        log.info("[get]: {}", access);

        int pool = access.getPool();

        if (pool < 0) return null;

        long id = access.getId();

        Player p = localCache.get(id, Player.class);
        // 本地缓存中没有
        if (p == null) {
            Cache cache = cachePool.getPoolCache(id);
            p = cache.get(id, Player.class);
            // redis 中没有
            if (p == null) {
                p = dataSource.getBeanById(pool, Player.class, id);
                if (p != null) cache.put(id, p);
                // 没拿到出现问题
            } else cache.put(id, p);
            if (p != null) localCache.put(id, p); // 放入本地缓存
        }
        log.info("[get]: {}", p);
        return p;
    }

    @Override
    public boolean update(Player player) {
        boolean status = dataSource.updateBean(player);
        if (status) {
            localCache.put(player.getId(), player);
            cachePool.getPoolCache(player.getId()).put(player.getId(), player);
        }
        log.info("[update] put: {} {}", player, (status ? "SUCCESS" : "FAIL"));
        return status;
    }
}
