package org.binave.examples.conf.args.item;

import org.binave.examples.conf.args.BaseEquip;
import lombok.*;

/**
 * table
 *
 * @since 1.8
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Dna extends BaseEquip {
    private int EquipID;
    private int EquipPos;// 装备部位

    private int BasicAttribute1 = -1;// 基础属性对应的属性ID
    private int BasicAttribute2 = -1;//

    private int MaxGemHoleNum;// 装备当前品质宝石孔数上限

    public static int getAttributeValue(int equipPos, boolean isMainAttribute, int grade, int intensifyLevel, int attributeType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getKey() {
        return EquipID;
    }

    @Override
    public void init() {

    }

    @Override
    public int getExtKey() {
        return getGrade();
    }
}
