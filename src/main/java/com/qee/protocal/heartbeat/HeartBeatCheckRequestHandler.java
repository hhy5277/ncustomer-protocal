package com.qee.protocal.heartbeat;


import com.qee.protocal.constant.NettyMessageConstant;
import com.qee.protocal.message.NettyCustomHeader;
import com.qee.protocal.message.NettyCustomMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhuqi on 2017/9/8.
 */
public class HeartBeatCheckRequestHandler extends ChannelInboundHandlerAdapter {

    private volatile ScheduledFuture<?> scheduledFuture;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyCustomMessage customMessage = (NettyCustomMessage) msg;
        if (customMessage.getCustomHeader() != null && customMessage.getCustomHeader().getType() == NettyMessageConstant.SERVER_AUTH_CERTI_TYPE) {
            scheduledFuture = ctx.executor().scheduleAtFixedRate(new HeartBeatCheckTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
            System.out.println("the client [ " + ctx.channel().localAddress().toString() + " ] send heart beat ...........");
        } else if (customMessage.getCustomHeader() != null && customMessage.getCustomHeader().getType() == NettyMessageConstant.HEART_BEAT_CHECK_PONG_TYPE) {
            System.out.println("the client [ " + ctx.channel().localAddress().toString() + " ] recieve heart beat .............");
        } else {
            ctx.fireChannelRead(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getStackTrace());
        cause.getStackTrace();
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            scheduledFuture = null;
        }
        ctx.fireExceptionCaught(cause);
    }

    class HeartBeatCheckTask implements Runnable {

        private ChannelHandlerContext context;

        public HeartBeatCheckTask(ChannelHandlerContext context) {
            this.context = context;
        }

        @Override
        public void run() {
            NettyCustomMessage customMessage = new NettyCustomMessage();
            NettyCustomHeader customHeader = new NettyCustomHeader();
            customHeader.setType(NettyMessageConstant.HEART_BEAT_CHECK_PING_TYPE);
            customMessage.setCustomHeader(customHeader);
            context.writeAndFlush(customMessage);
            System.out.println("the client [ " + context.channel().localAddress().toString() + " ] send heart beat to server ....");

        }
    }
}


