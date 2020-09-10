package org.binave.examples.conf.args;

import lombok.*;

/**
 * 格子
 * 赋予物品数量
 *
 * @author by bin jin on 2017/3/27.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Grid<Item> {

    // 物品
    private Item item;

    // 物品的数量
    private int count;

}
