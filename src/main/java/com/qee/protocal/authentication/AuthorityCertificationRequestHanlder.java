package com.qee.protocal.authentication;

import com.qee.protocal.constant.NettyMessageConstant;
import com.qee.protocal.message.NettyCustomHeader;
import com.qee.protocal.message.NettyCustomMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by zhuqi on 2017/9/8.
 */
public class AuthorityCertificationRequestHanlder extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(buildAuthorityCertificationMsg());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyCustomMessage message = (NettyCustomMessage) msg;
        if (message != null && message.getCustomHeader() != null && message.getCustomHeader().getType() == NettyMessageConstant.CUSTOMER_AUTH_CERTI_TYPE) {
            byte authResult = (Byte) message.getBodyMessage();
            if (authResult != (byte) 0) { //握手失败。关闭链接
                ctx.close();
                return;
            }
            System.out.println("authority certification is success .....");
            ctx.fireChannelRead(msg);
        } else {
            ctx.fireChannelRead(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.getStackTrace();
        ctx.channel().close();
        System.out.println(cause.getStackTrace());
        ctx.fireExceptionCaught(cause);
    }


    protected NettyCustomMessage buildAuthorityCertificationMsg() {
        NettyCustomMessage message = new NettyCustomMessage();
        NettyCustomHeader customHeader = new NettyCustomHeader();
        customHeader.setType(NettyMessageConstant.CUSTOMER_AUTH_CERTI_TYPE);
        message.setCustomHeader(customHeader);
        return message;
    }

}
