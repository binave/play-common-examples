package org.binave.examples.conf.args;

import lombok.*;
import org.binave.play.config.args.ConfigEditor;
import org.binave.examples.exception.NotForSaleException;

/**
 * 道具配置
 *
 * @author by bin jin on 2017/3/25.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseItemConf implements Sell, ConfigEditor {

    // 配置 id
    private int id;

    private long version;

    // 是否能够出售
    private int canSell;

    // 出售给商店的价格
    private int sellCoin;

    // 堆叠上限
    private int maxPile;

    private int TimeLimit;

    @Override
    public int getId() {
        return id;
    }

    // 获得出售给商店的价格
    @Override
    public int getSalePrice() throws NotForSaleException {
        if (canSell != 1) throw new NotForSaleException(id);
        return sellCoin;
    }

    @Override
    public int getKey() {
        return id;
    }

}
