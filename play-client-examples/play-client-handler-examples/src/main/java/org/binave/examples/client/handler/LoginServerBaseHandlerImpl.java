package org.binave.examples.client.handler;

import org.binave.examples.protoc.Msg;
import org.binave.play.protoc.BaseProtocHandler;
import org.binave.play.tag.Skenlr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端登陆发送或响应
 *
 * @author by bin jin on 2017/5/22.
 * @since 1.8
 */
@Skenlr.implement
public class LoginServerBaseHandlerImpl extends BaseProtocHandler<Msg.S_LoginServer_10013> {

    private static Logger log = LoggerFactory.getLogger(LoginServerBaseHandlerImpl.class);

    @Override
    public Object call(long id, int pool, Msg.S_LoginServer_10013 resp) {

        // 客户端主动发送
        if (resp == null) {
            Msg.C_LoginServer_10012.Builder builder = Msg.C_LoginServer_10012.newBuilder();

            log.info("[client]>>> id={}, pool={}", id, pool);

            builder.setAccountName("xx_" + id);
            builder.setToken("1234567890," + pool); // 放到 redis 中
            builder.setAccountId(id);
            builder.setChannelId(pool);
            builder.setClientVersion("clientId=" + pool);
            builder.setPlatform("plat");
            builder.setDeviceId("deviceId=" + (id - pool));
            // 登陆
            return builder;
        }

        // 回复
        long roleId = resp.getRoleId();
        int statusCode = resp.getStatusCode();
        int serializedSize = resp.getSerializedSize();

        log.info("[client]<<< id={}, pool={}, roleId={}, statusCode={}, serializedSize={}",
                id, pool, roleId, statusCode, serializedSize);

        // 模拟发送 sell grid
        Msg.C_SellGrid_12035.Builder builder = Msg.C_SellGrid_12035.newBuilder();
        builder.addGridList(1);
        builder.addGridList(2);
        builder.addGridList(3);
        builder.setTabId(1);// 页签 id: 1，道具，2，宝石，3，碎片
        return builder;
    }

}
