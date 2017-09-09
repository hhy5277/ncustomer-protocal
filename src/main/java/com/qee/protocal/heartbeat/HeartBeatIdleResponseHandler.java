package com.qee.protocal.heartbeat;

import com.qee.protocal.constant.NettyMessageConstant;
import com.qee.protocal.message.NettyCustomHeader;
import com.qee.protocal.message.NettyCustomMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhuqi on 2017/9/9.
 */
public class HeartBeatIdleResponseHandler extends IdleStateHandler {
    public HeartBeatIdleResponseHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
        super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
    }

    public HeartBeatIdleResponseHandler(long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit) {
        super(readerIdleTime, writerIdleTime, allIdleTime, unit);
    }

    public HeartBeatIdleResponseHandler(boolean observeOutput, long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit) {
        super(observeOutput, readerIdleTime, writerIdleTime, allIdleTime, unit);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        if(evt.state() == IdleState.ALL_IDLE){
            System.out.println("the client [ "+ctx.channel().remoteAddress().toString()+" ] is idle");
            NettyCustomMessage nettyCustomMessage = new NettyCustomMessage();
            NettyCustomHeader customHeader = new NettyCustomHeader();
            customHeader.setType(NettyMessageConstant.HEART_BEAT_CHECK_PONG_TYPE);
            nettyCustomMessage.setCustomHeader(customHeader);
            nettyCustomMessage.setBodyMessage("you are not operating");
            ctx.writeAndFlush(nettyCustomMessage);
        }
    }
}
