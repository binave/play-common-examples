package org.binave.examples.login.impl.handler;

import org.binave.examples.conf.api.TokenVerifier;
import org.binave.examples.data.args.Player;
import org.binave.examples.protoc.Msg.*;
import org.binave.examples.protoc.impl.BasePlayerHandler;
import org.binave.examples.route.api.MessageTag;
import org.binave.play.route.args.DataPacket;
import org.binave.play.tag.Skenlr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 登陆
 *
 * @since 1.8
 */
@Skenlr.implement
public class LoginServerBaseHandlerImpl extends BasePlayerHandler<C_LoginServer_10012> implements MessageTag {

    private static Logger log = LoggerFactory.getLogger(LoginServerBaseHandlerImpl.class);

    @Skenlr.inject
    private TokenVerifier tokenVerifier;

    @Override
    public Object call(Player player, C_LoginServer_10012 message) {

        log.info("[server] message not null: {}", (message == null));

        // 先进行判断：人满了、服务器有问题，黑名单等
        if (message == null) return getDataPacket(-1, -1, SERVER_CLOSED);

        String pbTokenText = message.getToken(); // 通过之后拿到 token，去 redis 验证
        long id = message.getAccountId();

        // 验证成功后从 redis 拿到 pool 信息
        int pool = tokenVerifier.verify(String.valueOf(id), pbTokenText);

        log.info("[server] id={}, pool={}", id, pool);

        // token 验证失败
        if (pool < 1) return getDataPacket(-1, -1, ENTER_GAME_FAILED);

        // 验证 ip，手机型号等等

        // 发给路由模块
        return getDataPacket(id, pool, SUCCESS);
    }

    /**
     * 下发指令
     */
    private DataPacket getDataPacket(long id, int pool, int statusCode) {
        S_LoginServer_10013.Builder builder = S_LoginServer_10013.newBuilder();
        builder.setStatusCode(statusCode);
        builder.setRoleId(-1);
        return createDataPacket(id, pool, statusCode, builder);
    }
}
