package org.binave.examples.conf.args;

import org.binave.examples.exception.NotForSaleException;

/**
 * 标记为可出售的物品
 * （无法出售的物品则不继承此接口）
 *
 * @author by bin jin on 2017/3/27.
 */
public interface Sell {

    /**
     * 获得出售给商店的价格，如果是非卖品，则抛出异常
     */
    int getSalePrice() throws NotForSaleException;

}
