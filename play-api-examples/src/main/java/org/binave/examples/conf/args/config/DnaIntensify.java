package org.binave.examples.conf.args.config;

import lombok.*;
import org.binave.play.config.args.ConfigEditor;

/**
 * table
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DnaIntensify implements ConfigEditor {

    private long version;
    private int ID;
    private int EquipPos;
    private int EquipGrade;
    private int level;
    private int IntensifyLevelMax;
    private int StoneId;
    private int StoneNum;
    private int Gold;
    private int BreakthroughStoneId;
    private int BreakthroughStoneNum;
    private int BreakthroughGold;
    private int BreakthroughRoleLevel;


    @Override
    public int getId() {
        return ID;
    }

    @Override
    public int getKey() {
        return EquipPos;
    }

    @Override
    public int getExtKey() {
        return level;
    }

    @Override
    public void init() {

    }

    // private int Attribute1Addon;//不用了，改用系数表公式计算
    // private int Attribute2Addon;//不用了，改用系数表公式计算

    // private int IntensifyHp;// 强化属性加成生命
    // private int IntensifyAttack;// 强化属性加成攻击
    // private int IntensifyDefence;// 强化属性加成防御
    // private int IntensifyCritPercent = 0;// 暴击率
    // private int IntensifyAntiCritPercent = 0;// 抗暴率
    // private int IntensifyCritDamagePercent = 0;// 暴击伤害
    // private int IntensifyHitPercent = 0;// 命中率
    // private int IntensifyDodgePercent = 0;// 闪避率
    // private int IntensifyAddDamagePercent = 0;// 追加伤害
    // private int IntensifyAvoidDamagePercent = 0;// 追加免伤

}

