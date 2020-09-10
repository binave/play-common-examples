package org.binave.examples.conf.args.config;

import lombok.*;
import org.binave.play.config.args.ConfigEditor;

/**
 * Map
 *
 * 攻击动画帧回调表
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SkillHurt implements ConfigEditor {

    private int ID;//动画帧事件回调ID
    private int Effect1;//技能效果类型
    private int Gethit;//受击状态
    private int EffectsHit;//受击特效
    private int Collision;//碰撞ID

    private long version;

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
