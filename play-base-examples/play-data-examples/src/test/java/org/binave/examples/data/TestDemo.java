package org.binave.examples.data;

import org.binave.common.util.CodecUtil;
import org.binave.common.util.FutureTime;
import org.binave.play.data.api.Cache;
import org.binave.play.data.cache.ConsistentCachePool;
import org.binave.play.data.cache.factory.CacheFactory;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.util.MurmurHash;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author by bin jin on 2017/4/12.
 */
public class TestDemo {

    @Test
    public void test1() {
        MurmurHash murmurHash;

        ConsistentCachePool pool = new ConsistentCachePool(CodecUtil.ConsistentHash.MURMUR3);

        List<Cache> caches = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            caches.add(CacheFactory.createCache("" + i, new Jedis("i0" + i), "test1", FutureTime.HOUR_OF_DAY, 2, null));
        }

        pool.put(true, caches);

        System.out.println(pool.toString().replaceAll("}", "}\n"));

        for (int i = 0; i < 50; i++) {
            Cache cache = pool.getPoolCache(i);
            System.out.println(cache);
        }

    }


}
