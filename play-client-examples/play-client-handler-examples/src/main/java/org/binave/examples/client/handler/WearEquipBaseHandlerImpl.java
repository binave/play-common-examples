package org.binave.examples.client.handler;

import lombok.extern.slf4j.Slf4j;
import org.binave.examples.protoc.Msg;
import org.binave.play.protoc.BaseProtocHandler;
import org.binave.play.tag.Skenlr;

/**
 * @author by bin jin on 2017/5/24.
 * @since 1.8
 */
@Skenlr.implement
@Slf4j
public class WearEquipBaseHandlerImpl extends BaseProtocHandler<Msg.S_WearEquip_12002> {

    @Override
    public Object call(long id, int pool, Msg.S_WearEquip_12002 resp) {

        // 发送
        if (resp == null) {
            log.info("[client]>>> send");
            // 客户端发起
            Msg.C_WearEquip_12001.Builder builder = Msg.C_WearEquip_12001.newBuilder();
            builder.setType(1); //1：主武器，2：副武器
            builder.setId(100); //装备唯一id
            return builder;
        }

        // 接收
        int statusCode = resp.getStatusCode();
        log.info("[client]<<< code={}", statusCode);
        return null;

    }

}
