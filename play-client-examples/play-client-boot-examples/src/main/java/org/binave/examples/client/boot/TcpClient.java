package org.binave.examples.client.boot;

import org.binave.examples.route.api.MessageTag;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.binave.play.route.MessageDecoder;
import org.binave.play.tag.Skenlr;

/**
 * see http://netty.io/wiki/
 * http://netty.io/4.1/xref/io/netty/example/objectecho/package-summary.html
 *
 * @author by bin jin on 2017/5/22.
 * @since 1.8
 */
public class TcpClient {

    @Skenlr.bootstrap
    public void main() throws Exception {

        String host = "127.0.0.1";
        int port = 8980;

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new MessageDecoder(MessageTag.CUSTOM_HEAD_SIZE), new ClientHandler());
                }

            });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync(); // (5)

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
        }
    }

}
