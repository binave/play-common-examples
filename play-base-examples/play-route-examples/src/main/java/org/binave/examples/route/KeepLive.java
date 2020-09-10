package org.binave.examples.route;

import org.binave.examples.route.impl.TcpLiveSenderImpl;
import io.netty.channel.Channel;
import org.binave.play.route.NettyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.Callable;

/**
 * 处理超时
 *
 * @author by bin jin on 2017/5/6.
 * @since 1.8
 */
public class KeepLive implements Callable {

    private static Logger log = LoggerFactory.getLogger(KeepLive.class);

    /**
     * 根据最后一次活跃时间，
     * 清除长时间不活跃的链接
     */
    private void trim() {
        // IdleStateHandler.class
        Iterator<Channel> iterator = TcpLiveSenderImpl.channelIterator();

        while (iterator.hasNext()) {
            Channel channel = iterator.next();

            // 判断是否没有心跳了，没有干掉
            Long lastActiveTime = NettyUtil.get(channel, MessageHandler.LAST_ACTIVE_TIME);
            if (lastActiveTime == null) {
                // 没有设置过时间
                log.error("");
            } else if (System.currentTimeMillis() - lastActiveTime > 2 * 60 * 1000) {
                // 超时，干掉
                log.error("");
                channel.close(); // 貌似会自动删除
//                iterator.remove();
            }
        }
    }

    @Override
    public Object call() throws Exception {
        trim();
        return null;
    }
}
