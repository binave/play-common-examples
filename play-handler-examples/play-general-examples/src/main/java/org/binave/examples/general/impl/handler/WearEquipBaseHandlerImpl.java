package org.binave.examples.general.impl.handler;

import org.binave.examples.data.args.Player;
import org.binave.examples.protoc.Msg;
import org.binave.examples.protoc.impl.BasePlayerHandler;
import org.binave.play.tag.Skenlr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author by bin jin on 2017/5/24.
 * @since 1.8
 */
@Skenlr.implement
public class WearEquipBaseHandlerImpl extends BasePlayerHandler<Msg.C_WearEquip_12001> {

    private static Logger log = LoggerFactory.getLogger(WearEquipBaseHandlerImpl.class);

    @Override
    public Object call(Player player, Msg.C_WearEquip_12001 req) {

        long equipId = req.getId();
        int type = req.getType();

        log.info("[server] player={}, equipId={}, type=", player, equipId, type);

        // 将客户端发送的消息推送回去
        Msg.S_WearEquip_12002.Builder builder = Msg.S_WearEquip_12002.newBuilder();
        builder.setId(equipId);
        builder.setStatusCode(0);
        builder.setType(type);
        return builder;
    }

}
