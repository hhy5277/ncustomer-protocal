package com.qee.protocal.client;


import com.qee.protocal.template.NettyProtocalTemplate;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhuqi on 2017/9/8.
 */
public abstract class NettyProtocalClientTemplate extends NettyProtocalTemplate {
    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);

    private Bootstrap bootstrap;

    private EventLoopGroup eventLoopGroup;

    private EventLoopGroup business;

    private String host;

    private int port;

    private int localPort;

    public NettyProtocalClientTemplate(String host, int port) {
        this(7777, host, port);
    }

    public NettyProtocalClientTemplate(int localPort, String host, int port) {
        this.host = host;
        this.port = port;
        this.localPort = localPort;
    }

    public void connect() throws InterruptedException {
        try {
            bootstrap = new Bootstrap();
            eventLoopGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);
            business = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);

            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            Map<String, ChannelHandler> ioHandlerMap = addIOChannelHandlers();
                            Map<String, ChannelHandler> businessHandlerMap = addBusinessChannelHandlers();

                            addChannelHandler(ch, ioHandlerMap, false, null);
                            addChannelHandler(ch, businessHandlerMap, true, business);

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
