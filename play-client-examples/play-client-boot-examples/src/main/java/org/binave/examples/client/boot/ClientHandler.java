package org.binave.examples.client.boot;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundInvoker;
import lombok.extern.slf4j.Slf4j;
import org.binave.common.collection.IndexMap;
import org.binave.common.util.MonitorUtil;
import org.binave.play.route.NettyUtil;
import org.binave.play.route.api.BaseHandler;
import org.binave.play.route.args.DataPacket;
import org.binave.play.tag.Skenlr;

import java.security.SecureRandom;
import java.util.*;

/**
 * see http://netty.io/wiki/
 * http://netty.io/4.1/xref/io/netty/example/objectecho/package-summary.html
 *
 * @author by bin jin on 2017/5/22.
 * @since 1.8
 */
@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private ChannelOutboundInvoker invoker;

    // 用户 id
    private long id = 2;
    private int pool = 1;

    // 登陆用
    private int startNum = 10013;

    private long useTime;

    private boolean status;

    /**
     * 所有的 handler
     * 由于静态注入在之前，所以 {@link #handlerSet} 可以在实例化时有值
     */
    @Skenlr.inject
    private static Set<BaseHandler> handlerSet;

    private static Map<Integer, BaseHandler> handlerMap;

    private static Random random = new SecureRandom();

    ClientHandler() {

        Map<Integer, BaseHandler> logicHandlerMap = new IndexMap<>();

        for (BaseHandler handler : handlerSet) {
            int codeNum = handler.tab();
            String fullName = handler.getClass().getName();
            logicHandlerMap.put(codeNum, handler);
            log.info("[init] RESPONSE {}:{}", codeNum, fullName);
        }
        handlerMap = logicHandlerMap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // Send the first message if this handler is a client-side handler.

        // 缓存链接
        this.invoker = ctx;

        // 拿到登陆处理器
        BaseHandler baseHandler = handlerMap.get(startNum);
        DataPacket dataPacket = baseHandler.call(new DataPacket(id, pool, 0, startNum, null));

        log.info("[channelActive]: {}", dataPacket);

        // init
        useTime = System.currentTimeMillis();

        // 先模拟登陆
        flush(dataPacket);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // Echo back the received object to the server.

        ByteBuf buf = (ByteBuf) msg;

        int reqCodeNum = buf.readInt();

        log.info("[channelRead]: code={}, use time={}", reqCodeNum, MonitorUtil.timeMillisFormat(System.currentTimeMillis() - useTime));

        if (reqCodeNum == 500) {
            log.error("[channelRead]: server error");
            status = true;
            return;
        }

        BaseHandler handler = handlerMap.get(reqCodeNum);

        // 拿到 handler
        if (handler != null) {

            ByteBuf bodyBuf = buf.readBytes(buf);

            // 数据的长度已经在 decoder 中处理
            byte[] reqMsgBody = new byte[buf.readableBytes()];

            // 读取指令正文
            bodyBuf.readBytes(reqMsgBody);

            // 处理回复
            DataPacket dataPacket = handler.call(new DataPacket(id, pool, 0, handler.tab(), reqMsgBody));

            // 如果这边回复之后没有其他的要做的
            if (dataPacket.getData() == null) {
                log.info("[channelRead] stop.");

                // 随机拿到一个发送指令
                do {
                    handler = (BaseHandler) handlerMap.values().
                            toArray()[random.nextInt(handlerMap.size())];
                } while (handler.tab() == startNum); // 除了登陆指令

                log.info("random tab={}", handler.tab());

                // 获得新的请求
                dataPacket = handler.call(new DataPacket(id, pool, 0, handler.tab(), null));

            }

            log.info("[channelRead]: will flush: {}", dataPacket);

            // init
            useTime = System.currentTimeMillis();

            flush(dataPacket);

        } else {
            status = true;
            log.error("[channelRead]: can not find code={}", reqCodeNum);
        }

    }

    /**
     * channelRead 结束后调用
     */
    public void channelReadComplete(ChannelHandlerContext ctx) {
        log.info("[channelReadComplete] complete.");
        if (status) ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("[exceptionCaught]",cause);
        ctx.close();
    }

    /**
     * 发送消息
     */
    private void flush(DataPacket dataPacket) {
        NettyUtil.flush(
                dataPacket.getCodeNum(),
                dataPacket.getData(),
                this.invoker
        );
    }

}
