package org.binave.examples.conf.args;

import lombok.*;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEquip extends BaseItemConf {

    private int Grade;
    private String IconAtlas;
    private String Icon;
    private int Name;
    private int Desc;
    private int Type;

    public int getFirstSkillIdByBtnIndex(int roleType, int btnIndex) {
        return 0;
    }

    public List<Integer> getSkillListByBtnIndex(int roleType, int btnIndex, int skillLevel) {
        throw new UnsupportedOperationException();
    }

    public static int getAttributeValue(int grade, int type, int intensifyLevel, int addonPercent,
                                        int... attributeIds) {
        if (Objects.isNull(attributeIds))
            return 0;
        if (attributeIds.length == 0)
            return 0;
        return -1;
    }

    @Override
    public int getExtKey() {
        return -1;
    }

    @Override
    public void init() {

    }

}
