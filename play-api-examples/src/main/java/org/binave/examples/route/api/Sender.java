package org.binave.examples.route.api;

import org.binave.play.route.args.DataPacket;

/**
 * 消息发送代理
 *
 * @author by bin jin on 2017/5/9.
 * @since 1.8
 */
public interface Sender {

    /**
     * 下发数据
     */
    void flush(DataPacket dataPacket);

    /**
     * 群发数据
     */
    void flushs(DataPacket dataPacket);

    /**
     * 群数量
     */
    int size();

    /**
     * 实现者标识
     */
    String tab();

}
