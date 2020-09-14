
package org.binave.examples.general.impl;

import lombok.extern.slf4j.Slf4j;
import org.binave.play.config.api.ConfLoader;
import org.binave.play.config.api.UpdateNotify;
import org.binave.play.config.args.Config;
import org.binave.play.config.args.UpdateNotice;
import org.binave.play.config.factory.ConfPoolFactory;
import org.binave.play.config.util.SpaceConfMap;
import org.binave.play.tag.Skenlr;

import java.util.Collection;

/**
 * 更新订阅实现
 *
 * 存储不重复 id 的配置
 *
 * @author by bin jin on 2017/4/7.
 */
@Skenlr.implement
@Slf4j
public class SpaceMapUpdateNotifyImpl implements UpdateNotify {

    private static SpaceConfMap confMap = ConfPoolFactory.createSpaceConfMap();

    public static <Conf extends Config> Conf get(int id) {
        return confMap.get(id);
    }

    public static Collection<? extends Config> get(String token) {
        return confMap.get(token);
    }

    @Skenlr.inject
    private ConfLoader loader;

    /**
     * 由订阅模块给订阅者发送通知
     */
    @Override
    public void notify(UpdateNotice notice) {
        log.info("[notify]: notice={}, conf size={}", notice, confMap.size());
        // 更新配置通知

        confMap.reload(loader, notice.getVersion(), notice.isOverride(), notice.getTokens());
    }

    private String[] tabs = new String[]{/*"BaseEquip",*/ "PropChip", "SkillHurt" /*, "CharacterNewPlayer"*//*, "AttributeTable"*/};

    /**
     * 订阅列表
     * 上传订阅列表（订阅了啥）
     *
     * map
     */
    @Override
    public String[] tab() {
        return tabs;
    }
}