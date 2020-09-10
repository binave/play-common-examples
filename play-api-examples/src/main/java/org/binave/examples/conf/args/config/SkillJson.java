package org.binave.examples.conf.args.config;


import lombok.*;
import org.binave.play.config.args.ConfigEditor;

/**
 *
 * table
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SkillJson implements ConfigEditor {

    private int ID;//SkillId+Level-1
    private int SkillId;
    private int Name;
    private String EffectID;
    private String Icon;
    private int Level;
    private int FragmentNum;
    private int RoleType;
    private int Desc;
    private int Distance;
    private int CoolDown;
    private int BuffID;
    private String IconSkill;
    private String IconSkillCD;
    private String IconSkillHover;
    private String AtlasName;
    private String SelfStartBreak;
    private String SelfAttakingBreak;
    private String SelfEndBreak;
    private String StartBreak;
    private String AttakingBreak;
    private String EndBreak;
    private int SkillType;
    private String SkillPrompt;

    //服务器本地用，加载完数据时赋值
    private long version;
    private int btnIndex;
    private int weaponType;

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public int getKey() {
        return SkillId;
    }

    @Override
    public int getExtKey() {
        return Level;
    }

    @Override
    public void init() {

    }
}