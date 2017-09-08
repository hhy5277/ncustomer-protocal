package com.qee.protocal.client;

import com.qee.protocal.authentication.AuthorityCertificationRequestHanlder;
import com.qee.protocal.decoder.ByteBuf2NettyMessageDecoder;
import com.qee.protocal.encoder.NettyMessage2ByteBufEncoder;
import com.qee.protocal.heartbeat.HeartBeatCheckRequestHandler;
import com.qee.protocal.server.NettyProtocalServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhuqi on 2017/9/8.
 */
public class NettyProtocalClient {
    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);

    private Bootstrap bootstrap;

    private EventLoopGroup eventLoopGroup;

    private String host;

    private int port;

    private int localPort;

    public NettyProtocalClient(String host, int port) {
        this(7777, host, port);
    }

    public NettyProtocalClient(int localPort, String host, int port) {
        this.host = host;
        this.port = port;
        this.localPort = localPort;
    }

    public void connect() throws InterruptedException {
        try {
            bootstrap = new Bootstrap();
            eventLoopGroup = new NioEventLoopGroup();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<io.netty.channel.Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
                                    .addLast("log", new LoggingHandler(LogLevel.INFO))
                                    .addLast("decoder", new ByteBuf2NettyMessageDecoder(6 * 1024, 4, 4, -8, 0, true))
                                    .addLast("encoder", new NettyMessage2ByteBufEncoder())
                                    .addLast("timeout", new ReadTimeoutHandler(50))
                                    .addLast("authority", new AuthorityCertificationRequestHanlder())
                                    .addLast("hearbeat", new HeartBeatCheckRequestHandler());


                        }
                    });
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port), new InetSocketAddress("127.0.0.1", localPort)).sync();
            future.channel().closeFuture().sync();
        } finally {
            if (eventLoopGroup != null) {
                eventLoopGroup.shutdownGracefully().sync();
            }
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                        connect();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }
}
