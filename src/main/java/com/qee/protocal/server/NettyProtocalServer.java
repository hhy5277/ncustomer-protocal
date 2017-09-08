package com.qee.protocal.server;

import com.qee.protocal.authentication.AuthorityCertificationResponseHanlder;
import com.qee.protocal.decoder.ByteBuf2NettyMessageDecoder;
import com.qee.protocal.encoder.NettyMessage2ByteBufEncoder;
import com.qee.protocal.heartbeat.HeartBeatCheckResponseHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;

/**
 * Created by zhuqi on 2017/9/8.
 */
public class NettyProtocalServer {
    private ServerBootstrap serverBootstrap;

    private EventLoopGroup boss;

    private EventLoopGroup worker;

    private String host;


    private int port;

    public NettyProtocalServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException {
        try {
            serverBootstrap = new ServerBootstrap();
            boss = new NioEventLoopGroup(1);
            worker = new NioEventLoopGroup();

            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
                                    .addLast("log",new LoggingHandler(LogLevel.INFO))
                                    .addLast("decoder", new ByteBuf2NettyMessageDecoder(6 * 1024, 4, 4, -8, 0, true))
                                    .addLast("encoder", new NettyMessage2ByteBufEncoder())
                                    .addLast("timeout", new ReadTimeoutHandler(50))
                                    .addLast("authority", new AuthorityCertificationResponseHanlder())
                                    .addLast("hearbeat", new HeartBeatCheckResponseHandler());

                        }
                    });
            ChannelFuture future = serverBootstrap.bind(new InetSocketAddress(host, port)).sync();
            future.channel().closeFuture().sync();
        } finally {
            if (boss != null) {
                boss.shutdownGracefully();
            }
            if (worker != null) {
                worker.shutdownGracefully();
            }
        }
    }
}






















