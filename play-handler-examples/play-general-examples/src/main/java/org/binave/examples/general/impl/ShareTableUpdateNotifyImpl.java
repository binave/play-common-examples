
package org.binave.examples.general.impl;

import com.google.common.collect.HashBasedTable;
import org.binave.play.config.api.ConfLoader;
import org.binave.play.config.api.UpdateNotify;
import org.binave.play.config.args.UpdateNotice;
import org.binave.play.config.factory.RefreshFactory;
import org.binave.play.config.util.Refresh;
import org.binave.play.tag.Skenlr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 订阅者实现
 *
 * @author by bin jin on 2017/2/26.
 */
@Skenlr.implement
public class ShareTableUpdateNotifyImpl implements UpdateNotify {

    private static Logger log = LoggerFactory.getLogger(ShareTableUpdateNotifyImpl.class);

    /**
     * 引用私有变量
     * token 随便写的，用于更新配置
     */
    private Refresh tableConfPoolImpl = RefreshFactory.createConfTable(HashBasedTable::create);

    @Skenlr.inject
    private ConfLoader loader;

    /**
     * 由订阅模块给订阅者发送通知
     */
    @Override
    public void notify(UpdateNotice notice) {
        log.info("[notify] notice={}, ConfPool size={}: ", notice, tableConfPoolImpl.size());
        // 此处直接将 session 当作配置更新通知

        tableConfPoolImpl.reload(loader, notice.getVersion(), notice.isOverride(),
                notice.getTokens());
    }

    private String[] tabs = new String[]{"SkillJson", "DnaIntensify", "Dna"};

    /**
     * 订阅列表，订阅 table 类更新
     * 所有使用者只需一个订阅即可
     * 上传订阅列表
     *
     * table
     */
    @Override
    public String[] tab() {
        return tabs;
    }
}
