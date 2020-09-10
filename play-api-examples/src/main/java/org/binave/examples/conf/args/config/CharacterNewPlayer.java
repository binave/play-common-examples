package org.binave.examples.conf.args.config;

import lombok.*;
import org.binave.play.config.args.ConfigEditor;

/**
 * Map
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CharacterNewPlayer implements ConfigEditor {

    private long version;

    private int ID;// 角色id
    private String Skill;
    private int Level;// 初始化等级
    private int Gold;// 初始化金币
    private int Diamond;// 初始化钻石
    private int BornCity;// 出生所在主城ID
    private int BornEquipWeapon;// 初始化主武器
    private String BornPackage;// 初始化新手宝箱

    private int Energy;// 初始体力

    private int BornHat;
    private int BornJacket;
    private int BornPants;
    private int BornBelt;
    private int BornShoes;
    private int BornNecklace;
    private int BornEquipSubWeapon;


    @Override
    public int getId() {
        return ID;
    }

    @Override
    public int getKey() {
        return ID;
    }

    @Override
    public int getExtKey() {
        return -1;
    }

    @Override
    public void init() {

    }

}
