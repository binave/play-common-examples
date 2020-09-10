package org.binave.examples.protoc.impl;

import com.google.protobuf.GeneratedMessageLite;
import org.binave.examples.data.api.PlayerSource;
import org.binave.examples.data.args.Player;
import org.binave.common.util.CharUtil;
import org.binave.play.data.args.Access;
import org.binave.play.protoc.BaseProtocHandler;
import org.binave.play.tag.Skenlr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获得用户
 *
 * @author by bin jin on 2017/5/13.
 * @since 1.8
 */
abstract public class BasePlayerHandler<Message extends GeneratedMessageLite> extends BaseProtocHandler<Message> {

    private static Logger log = LoggerFactory.getLogger(BasePlayerHandler.class);

    abstract public Object call(Player player, Message message);

    /**
     * 需要引用此模块的模块进行标注
     */
    @Skenlr.inject
    private static PlayerSource playerSource;

    @Override
    public Object call(long id, int pool, Message message) {

        Player p = playerSource.get(new Access(id, pool));

        try {
            return call(p, message);
        } catch (RuntimeException e) {
            log.error(CharUtil.format("handler id={}, pool={} error", id, pool), e);
            throw e;
        }
    }
}
