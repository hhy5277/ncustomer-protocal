package com.qee.protocal.server;

import com.qee.protocal.model.Pair;
import com.qee.protocal.template.NettyProtocalTemplate;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuqi on 2017/9/8.
 */
public abstract class NettyProtocalServerTemplate extends NettyProtocalTemplate {


    protected ServerBootstrap serverBootstrap;

    protected EventLoopGroup boss;

    protected EventLoopGroup worker;

    protected EventLoopGroup business;

    protected String host;


    protected int port;

    public NettyProtocalServerTemplate() {
    }

    public NettyProtocalServerTemplate(String host, int port) {
        this.host = host;
        this.port = port;
    }


    @Override
    public void start() throws InterruptedException {
        try {
            serverBootstrap = new ServerBootstrap();
            boss = new NioEventLoopGroup(1);
            worker = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);
            business = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);


            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            List<Pair<String, ChannelHandler>> ioHandlers = addIOChannelHandlers();
                            List<Pair<String, ChannelHandler>> businessHandlers = addBusinessChannelHandlers();

                            addChannelHandler(ch, ioHandlers, false, null);
                            addChannelHandler(ch, businessHandlers, true, business);

                        }
                    });
            ChannelFuture future = serverBootstrap.bind(new InetSocketAddress(host, port)).sync();
            future.channel().closeFuture().sync();
        } finally {
            stop();
        }
    }

    @Override
    public void stop() {
        if (boss != null) {
            boss.shutdownGracefully();
        }
        if (worker != null) {
            worker.shutdownGracefully();
        }
        if (business != null) {
            business.shutdownGracefully();
        }
    }
}






















