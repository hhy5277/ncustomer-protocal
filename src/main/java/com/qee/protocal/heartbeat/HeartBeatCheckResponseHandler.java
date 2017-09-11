package com.qee.protocal.heartbeat;

import com.qee.protocal.constant.NettyMessageConstant;
import com.qee.protocal.message.NettyCustomHeader;
import com.qee.protocal.message.NettyCustomMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by zhuqi on 2017/9/8.
 */
public class HeartBeatCheckResponseHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyCustomMessage customMessage = (NettyCustomMessage) msg;
        if (customMessage.getCustomHeader() != null && customMessage.getCustomHeader().getType() == NettyMessageConstant.HEART_BEAT_CHECK_PING_TYPE) {
            System.out.println("the server recieve the client [ " + ctx.channel().remoteAddress().toString() + " ] heart beat check package,");

            NettyCustomMessage sendPongMessage = new NettyCustomMessage();
            NettyCustomHeader customHeader = new NettyCustomHeader();
            customHeader.setType(NettyMessageConstant.HEART_BEAT_CHECK_PONG_TYPE);
            sendPongMessage.setCustomHeader(customHeader);
            ctx.writeAndFlush(customMessage);
            return;
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getStackTrace());
        cause.getStackTrace();
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("the client [ " + ctx.channel().remoteAddress().toString() + " ] is close ....,then close channel");
        ctx.channel().close();
    }


}
