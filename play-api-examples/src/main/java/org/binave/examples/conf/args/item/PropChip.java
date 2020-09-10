package org.binave.examples.conf.args.item;

import org.binave.examples.conf.args.BaseEquip;
import lombok.*;

/**
 * Map
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PropChip extends BaseEquip {

    private int CanUse;//是否可以使用 1可以 2不可以
    private int ChipCount;// 合成所需数量
    private int ComposeProp;// 合成物品

    @Override
    public int getExtKey() {
        return -1;
    }

    @Override
    public void init() {

    }
}
