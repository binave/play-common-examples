
package org.binave.examples.route.impl;

import org.binave.examples.route.api.Sender;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.binave.play.data.args.Access;
import org.binave.play.route.args.DataPacket;
import org.binave.play.route.NettyUtil;
import org.binave.play.tag.Skenlr;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 额外发送数据
 *
 * @author by bin jin on 2017/4/27.
 */
@Skenlr.implement
public class TcpLiveSenderImpl implements Sender {

    private final static String USER_ACCESS = "USER_ACCESS";

    // 在线组
    private static ChannelGroup liveGroup = new DefaultChannelGroup(
            GlobalEventExecutor.INSTANCE
    );

    private static Map<Access, ChannelId> accessChannelIdMap = new ConcurrentHashMap<>();

    public static Access getAccess(Channel channel) {
        return NettyUtil.get(channel, USER_ACCESS);
    }

    /**
     * 获得 Channel
     */
    public static Channel findChannel(Access access) {

        if (access == null) return null;

        ChannelId channelId = accessChannelIdMap.get(access);

        if (channelId == null) return null;

        return liveGroup.find(channelId);
    }

    /**
     * 加入
     */
    public static Channel putChannelId(Access access, Channel channel) {

        ChannelId channelId = accessChannelIdMap.put(access, channel.id());

        // 放入标记
        NettyUtil.set(channel, USER_ACCESS, access);

        // 放入在线组
        liveGroup.add(channel);

        // 如果重登录，返回旧的链接
        return channelId != null ? liveGroup.find(channelId) : null;
    }

    /**
     * Channel 迭代器
     */
    public static Iterator<Channel> channelIterator() {
        return liveGroup.iterator();
    }

    public static void flushOne(DataPacket dataPacket) {

        Channel channel = findChannel(new Access(dataPacket.getId(), dataPacket.getPool()));

        if (channel == null) throw new IllegalArgumentException("DataPacket: " + dataPacket);

        NettyUtil.flush(
                dataPacket.getCodeNum(),
                dataPacket.getData(),
                channel
        );
    }

    @Override
    public void flush(DataPacket dataPacket) {
        flushOne(dataPacket);
    }

    public static void flushAll(DataPacket dataPacket) {
        NettyUtil.flushs(
                dataPacket.getCodeNum(),
                dataPacket.getData(),
                liveGroup
        );
    }

    @Override
    public void flushs(DataPacket dataPacket) {
        flushAll(dataPacket);
    }

    @Override
    public int size() {
        return liveGroup.size();
    }

    @Override
    public String tab() {
        return this.getClass().getName();
    }
}
