package org.binave.examples.route;

import lombok.extern.slf4j.Slf4j;
import org.binave.examples.route.api.MessageTag;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.binave.play.config.api.ConfLoader;
import org.binave.play.route.MessageDecoder;
import org.binave.play.tag.Skenlr;

/**
 * 启动类
 *
 * @author by bin jin on 2017/4/26.
 */
@Slf4j
public class TcpServer extends Thread {

    /**
     * 配置模块
     */
    @Skenlr.inject
    private static ConfLoader loader;

    /**
     * see http://netty.io/wiki
     * see http://netty.io/4.1/xref/io/netty/example/objectecho/ObjectEchoServer.html
     */
    @Override
    @Skenlr.bootstrap
    public void run() {

        // 拿到端口配置，拿不到先行报错
        int port = Integer.valueOf(loader.loadBaseConfig("tcp_port").get(0));

        log.info("[run] get port: {}", port);
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup, workerGroup).
                    channel(NioServerSocketChannel.class).
                    // boss option
                    option(ChannelOption.SO_BACKLOG /* 初始化服务端可连接队列大小 */, 1024).
                    option(ChannelOption.SO_REUSEADDR  /* 允许重复使用（共享）本地地址和端口 */, true).
                    option(ChannelOption.SO_RCVBUF /* 接受缓冲区大小，保存网络协议站内收到的数据，直到应用程序读取成功 */, 1024 * 256).
                    // PooledByteBufAllocator 可以重复利用之前分配的内存空间。(5.x 默认是此选项)
                            option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).
                    // worker childOption
                    childOption(ChannelOption.SO_KEEPALIVE /* 如果在两小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文 */, true).
                    childOption(ChannelOption.SO_LINGER /* 阻塞 close() 的调用时间，直到数据完全发送 */, 0).
                    childOption(ChannelOption.SO_SNDBUF /* 发送缓冲区大小，保存发送数据，直到发送成功 */, 1024 * 256).
                    childOption(ChannelOption.TCP_NODELAY /* 禁止使用 Nagle 算法导致小数据包合并之后发送。避免延时 */, true).
                    childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).
                    handler(new LoggingHandler(LogLevel.INFO));

            bootstrap.childHandler(
                    new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline cp = ch.pipeline();
                            // 解析带有长度属性的包
                            cp.addLast(
                                    new LengthFieldBasedFrameDecoder(
                                            1024 * 1024,
                                            0,
                                            4,
                                            4,
                                            0
                                    ),
                                    new MessageDecoder(MessageTag.CUSTOM_HEAD_SIZE), // 处理积累缓冲
                                    new MessageHandler()
                            );
                        }
                    }
            );

            log.info("[run]: ready");
            bootstrap.bind(port).
                    sync().
                    channel().
                    closeFuture().
                    sync();


        } catch (InterruptedException e) {
            e.printStackTrace();

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
