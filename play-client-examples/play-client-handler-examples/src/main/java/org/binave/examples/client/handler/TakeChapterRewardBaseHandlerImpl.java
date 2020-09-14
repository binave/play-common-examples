package org.binave.examples.client.handler;

import lombok.extern.slf4j.Slf4j;
import org.binave.examples.protoc.Msg;
import org.binave.play.protoc.BaseProtocHandler;
import org.binave.play.tag.Skenlr;

/**
 * @author by bin jin on 2017/5/25.
 * @since 1.8
 */
@Skenlr.implement
@Slf4j
public class TakeChapterRewardBaseHandlerImpl extends BaseProtocHandler<Msg.S_TakeChapterReward_14017> {

    @Override
    public Object call(long id, int pool, Msg.S_TakeChapterReward_14017 resp) {

        if (resp == null) {
            Msg.C_TakeChapterReward_14016.Builder builder = Msg.C_TakeChapterReward_14016.newBuilder();
            log.info("[client]>>> id={}, pool={}", id, pool);
            builder.setChapterId((int) id);
            builder.setRewardIndex(pool); // 没啥用
            return builder;
        }

        for (Msg.ItemInfo itemInfo : resp.getBonusInfoList()) {
            log.info(
                    "[client]<<< itemId={}, itemNum={}, serializedSize={}",
                    itemInfo.getItemID(), itemInfo.getItemNum(), itemInfo.getSerializedSize()
            );
        }

        log.info("[client]<<< chapterId={}", resp.getChapterId());
        return null;
    }

}
