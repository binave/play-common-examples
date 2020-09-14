package org.binave.examples.route;

import lombok.extern.slf4j.Slf4j;
import org.binave.examples.route.impl.TcpLiveSenderImpl;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.binave.common.collection.IndexMap;
import org.binave.play.data.args.Access;
import org.binave.play.route.api.BaseHandler;
import org.binave.play.route.NettyUtil;
import org.binave.play.route.args.DataPacket;
import org.binave.play.tag.Skenlr;

import javax.xml.ws.http.HTTPException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author by bin jin on 2017/4/26.
 */
@Slf4j
public class MessageHandler extends ChannelInboundHandlerAdapter {

    /**
     * 所有的 handler
     * 由于静态注入在之前，所以 {@link #handlerSet} 可以在实例化时有值
     */
    @Skenlr.inject
    private static Set<BaseHandler> handlerSet;

    // 最后活动时间
    static String LAST_ACTIVE_TIME = "LAT";

    // 用户 id

    // 心跳指令号
    private final static int ALIVE = 10010;

    private final static int LOGIN = 10012;

    private static Map<Integer, BaseHandler> handlerMap;

    MessageHandler() {
        init();
    }


    /**
     * 将获得的接口实现进行分类
     * 会在添加或削减 handler 之后自动运行，无需额外处理
     */
    void init() {

        Map<Integer, BaseHandler> logicHandlerMap = new IndexMap<>();

        for (BaseHandler handler : handlerSet)
            logicHandlerMap.put(handler.tab(), handler);

        // 保证原子性
        handlerMap = logicHandlerMap;
    }

    /**
     * 处理数据
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        try {
            ByteBuf buf = (ByteBuf) msg;

            // 更新最后活动时间
            NettyUtil.set(ctx, LAST_ACTIVE_TIME, System.currentTimeMillis());

            // 获得业务逻辑指令号
            int codeNum = buf.readInt();

            // 如果是心跳
            if (codeNum == ALIVE) { // 支持匹配多个
                if (log.isInfoEnabled()) log.info(""); // todo 灰度日志
                // 下发心跳回应
                NettyUtil.flush(codeNum, null, ctx);
                return;
            }

            ByteBuf bodyBuf = buf.readBytes(buf);
            // 数据的长度已经在 decoder 中处理
            byte[] msgBody = new byte[buf.readableBytes()];

            // 读取指令正文
            bodyBuf.readBytes(msgBody);

            Channel channel = ctx.channel();

            if (codeNum == LOGIN) {

                // 走登陆流程，其中 pool 必须 -1
                DataPacket dataPacket = call(new DataPacket(0, -1, 0, codeNum, msgBody));

                log.info("[channelRead]: {}", dataPacket);

                // 只有成功的才可以执行
                if (dataPacket.getStatus() == 0) {

                    // 放入 map
                    Channel oldChannel = TcpLiveSenderImpl.putChannelId(
                            new Access(
                                    dataPacket.getId(),
                                    dataPacket.getPool()
                            ), channel
                    );

                    // 旧的连接
                    if (oldChannel != null) {
                        // 如果有必要，加入日期
                        log.error("[channelRead] already login, id={}, pool={}", dataPacket.getId(), dataPacket.getPool());
                        // todo 重登录，下发关闭指令
                        oldChannel.close();
                    }

                } else {
                    log.error("[channelRead] static code={}", dataPacket.getStatus());
                    throw new HTTPException(dataPacket.getStatus());
                }

                // 下发指令
                TcpLiveSenderImpl.flushOne(dataPacket);

            } else {

                // 拿到 session
                Access access = TcpLiveSenderImpl.getAccess(channel);

                if (access == null || !channel.equals(TcpLiveSenderImpl.findChannel(access))) {
                    log.error(access == null ? "[channelRead] No access found, {}" : "[channelRead] {}", access);
                    throw new RuntimeException("No access found.");
                }

                DataPacket dataPacket = call(new DataPacket(access.getId(), access.getPool(), 0, codeNum, msgBody));
                TcpLiveSenderImpl.flushOne(dataPacket); // 支持发给其他 client
            }

        } catch (HTTPException e) {
            e.printStackTrace();
            // 客户端参数错误 400
            NettyUtil.flush(e.getStatusCode(), null, ctx);
        } catch (Exception e) {
            // 服务端错误
            e.printStackTrace();
            // 500
            NettyUtil.flush(HttpURLConnection.HTTP_INTERNAL_ERROR, null, ctx);
        }
    }

    private DataPacket call(DataPacket dataPacket) {
        int codeNum = dataPacket.getCodeNum();

        // 拿到处理 handler
        BaseHandler handler = handlerMap.get(codeNum);

        log.info("{call}: {}", dataPacket);

        if (handler == null) {
            log.error("client args error: {}", dataPacket);
            throw new HTTPException(HttpURLConnection.HTTP_BAD_REQUEST);
        }

        // 处理业务逻辑
        return handler.call(dataPacket);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // todo 记录异常
        cause.printStackTrace();
        // todo 下发错误代码
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // todo 记录动作
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // todo 记录动作
        super.handlerRemoved(ctx);
    }


}
