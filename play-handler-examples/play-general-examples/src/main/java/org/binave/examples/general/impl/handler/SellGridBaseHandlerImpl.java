package org.binave.examples.general.impl.handler;

import org.binave.examples.conf.args.BaseItemConf;
import org.binave.examples.data.args.Player;
import org.binave.examples.exception.NotForSaleException;
import org.binave.examples.general.impl.SpaceMapUpdateNotifyImpl;
import org.binave.examples.protoc.Msg.*;
import org.binave.play.config.args.Config;
import org.binave.play.config.factory.ConfPoolFactory;
import org.binave.play.config.util.ConfTable;
import org.binave.play.tag.Skenlr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * 以格子为单位批量卖出道具
 *
 * @author by bin jin on 2017/4/27.
 */
@Skenlr.implement
public class SellGridBaseHandlerImpl extends BasePlayerHandler<C_SellGrid_12035> {

    private static Logger log = LoggerFactory.getLogger(SellGridBaseHandlerImpl.class);

    /**
     * 引用私有变量
     * 此更新订阅由
     */
    private ConfTable SkillJsonTable = ConfPoolFactory.createConfTable("SkillJson");

    private Random ran = new SecureRandom();

    @Override
    public Object call(Player player, C_SellGrid_12035 req) {

        long time = System.currentTimeMillis();

        int tab = req.getTabId(); // 1，道具，2，宝石，3，碎片
        List<Long> gridList = req.getGridListList(); // 暂时拿来当作配置 id

        log.info("[server] use time {}, {}, tab={}, gridList={}", System.currentTimeMillis() - time, player, tab, gridList);

        for (Long grid : gridList) {
            Config config = null;
            if (tab == 1) {
                log.info("[server] use time {}, space map", System.currentTimeMillis() - time);
                config = SpaceMapUpdateNotifyImpl.get(Math.toIntExact(grid));
            } else if (tab == 2) {
                log.info("[server] use time {}, share conf table", System.currentTimeMillis() - time);
                config = SkillJsonTable.get(1, Math.toIntExact(grid));
            } else log.error("[server] use time {}, tab={}", System.currentTimeMillis() - time, tab);

            log.info("[server] use time {}, config={}", System.currentTimeMillis() - time, config);
        }

        log.info("[server] use time {}, show gridList over.", System.currentTimeMillis() - time);

        // 模拟通过名称拿到配置结合进行遍历
        Collection<? extends Config> configs = SpaceMapUpdateNotifyImpl.get("PropChip");

        log.info("[server] use time {}, will loop config", System.currentTimeMillis() - time);

        if (configs != null) {
            for (Config ic : configs) {
                if (ran.nextInt(2) != 0) continue;
                try {
                    log.info("[server] use time {}, loop class {}, id={}, price={}", System.currentTimeMillis() - time,
                            ic.getClass().getSimpleName(), ic.getKey(), ((BaseItemConf) ic).getSalePrice());
                } catch (NotForSaleException ignored) {
                }
            }
        } else log.info("[server] use time {}, PropChip is empty", System.currentTimeMillis() - time);

        // todo 处理逻辑
        S_SellGrid_12036.Builder builder = S_SellGrid_12036.newBuilder();
        builder.setStatusCode(0);

        return builder; // null 只发送 OK
    }

}
