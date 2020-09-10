package org.binave.examples.general.impl.handler;

import org.binave.examples.data.args.Player;
import org.binave.examples.general.impl.SpaceMapUpdateNotifyImpl;
import org.binave.examples.protoc.Msg;
import org.binave.examples.protoc.impl.BasePlayerHandler;
import org.binave.play.config.args.Config;
import org.binave.play.config.factory.ConfPoolFactory;
import org.binave.play.config.util.ConfTable;
import org.binave.play.tag.Skenlr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.Random;

/**
 * @author by bin jin on 2017/5/25.
 * @since 1.8
 */
@Skenlr.implement
public class TakeChapterRewardBaseHandlerImpl extends BasePlayerHandler<Msg.C_TakeChapterReward_14016> {

    private static Logger log = LoggerFactory.getLogger(TakeChapterRewardBaseHandlerImpl.class);

    /**
     * 引用私有变量
     * 此更新订阅由
     */
    private ConfTable DnaTable = ConfPoolFactory.createConfTable("Dna");

    private Random ran = new SecureRandom();

    @Override
    public Object call(Player player, Msg.C_TakeChapterReward_14016 req) {

        long time = System.currentTimeMillis();

        log.info("[server] {}, chapterId={}, rewardIndex={}", player, req.getChapterId(), req.getRewardIndex());

        // 随机获得一个配置
        Collection<? extends Config> configList = ran.nextBoolean() ? SpaceMapUpdateNotifyImpl.get("PropChip") :
                SpaceMapUpdateNotifyImpl.get("SkillHurt");

        int size = configList.size();
        log.info("[server] use time {}", System.currentTimeMillis() - time);

        Config[] configs = configList.toArray(new Config[configList.size()]);
        log.info("[server] use time {}, config size={}", System.currentTimeMillis() - time, size);

        Msg.S_TakeChapterReward_14017.Builder builder = Msg.S_TakeChapterReward_14017.newBuilder();

        builder.setStatusCode(0);
        builder.setChapterId((int) player.getId());
        builder.setRewardIndex(player.getPool());

        if (size > 0) {
            log.info("[server] use time {}, will loop config", System.currentTimeMillis() - time);

            for (int i = 0; i < ran.nextInt(10) + 10; i++) {
                Config config = configs[ran.nextInt(size)];

                int itemId = config.getId();
                int key = config.getKey();
                int itemNum = ran.nextInt(9) + 1;

                log.info("[server] use time {}, itemId={}, key={} itemNum={}, {}", System.currentTimeMillis() - time,
                        itemId, key, itemNum, config);

                Msg.ItemInfo.Builder itemInfo = Msg.ItemInfo.newBuilder();
                itemInfo.setItemID(itemId);
                itemInfo.setItemNum(itemNum);

                builder.addBonusInfo(itemInfo);
            }
        }

        log.info("[server] use time {}, cache size={}", System.currentTimeMillis() - time, DnaTable.size());

        return builder;
    }

}
