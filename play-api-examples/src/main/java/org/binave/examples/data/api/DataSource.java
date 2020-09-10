package org.binave.examples.data.api;


import org.binave.play.data.args.Dao;

import java.util.List;

/**
 * 数据获取
 * 根据业务进行变化
 *
 * @author by bin jin on 2017/4/12.
 */
public interface DataSource {

    /**
     * pool 版本
     */
    long getVersion(int pool);

    <T extends Dao> List<T> getBeans(int pool, Class<T> clazz);

    <T extends Dao> List<T> getBeans(int pool, Class<T> clazz, String whereCondition);

    <T extends Dao> T getBeanById(int pool, Class<T> clazz, long id);

    long count(int pool, Class<?> clazz);

    boolean updateBean(Dao e);

}
