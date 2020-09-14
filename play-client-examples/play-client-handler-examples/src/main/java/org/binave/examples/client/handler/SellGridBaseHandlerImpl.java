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
public class SellGridBaseHandlerImpl extends BaseProtocHandler<Msg.S_SellGrid_12036> {

    @Override
    public Object call(long id, int pool, Msg.S_SellGrid_12036 resp) {

        if (resp == null) {
            Msg.C_SellGrid_12035.Builder builder = Msg.C_SellGrid_12035.newBuilder();

            log.info("[client]>>>: send");

            builder.setTabId(1); // 页签 id: 1，道具，2，宝石，3，碎片
            builder.addGridList(1); // 格子 id list
            builder.addGridList(2);
            return builder;
        }

        log.info("[client]<<<: status={}", resp.getStatusCode());
        return null;
    }

}
