package com.qee.protocal.authentication;

import com.qee.protocal.constant.NettyMessageConstant;
import com.qee.protocal.message.NettyCustomHeader;
import com.qee.protocal.message.NettyCustomMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhuqi on 2017/9/8.
 */
public class AuthorityCertificationResponseHanlder extends ChannelInboundHandlerAdapter {

    private Map<String, Boolean> authority = new ConcurrentHashMap<String, Boolean>();

    private String[] ipList = new String[]{"127.0.0.1"};

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        NettyCustomMessage customMessage = (NettyCustomMessage) msg;
        NettyCustomMessage response;
        if (customMessage.getCustomHeader() != null && customMessage.getCustomHeader().getType() == NettyMessageConstant.CUSTOMER_AUTH_CERTI_TYPE) {
            String remoteAddress = ctx.channel().remoteAddress().toString();
            if (authority.containsKey(remoteAddress)) { //重复登陆
                response = buildAuthorCertiResponseMessage((byte) -1);
            } else {
                InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
                boolean isAuth = false;
                for (String ip : ipList) {
                    if (ip.equals(inetSocketAddress.getAddress().getHostAddress())) {
                        isAuth = true;
                        break;
                    }
                }
                if (isAuth) {
                    response = buildAuthorCertiResponseMessage((byte) 0);
                    authority.put(remoteAddress, true);
                } else {
                    response = buildAuthorCertiResponseMessage((byte) -1);
                }
            }
            System.out.println("the client [" + remoteAddress + "] is connecting ,status:" + response);
            ctx.writeAndFlush(response);
            return;
        }
        ctx.fireChannelRead(msg);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.getStackTrace();
        String remoteAddress = ctx.channel().remoteAddress().toString();
        authority.remove(remoteAddress);
        ctx.channel().close();
        ctx.fireExceptionCaught(cause);
    }

    private NettyCustomMessage buildAuthorCertiResponseMessage(byte body) {
        NettyCustomMessage message = new NettyCustomMessage();
        NettyCustomHeader customHeader = new NettyCustomHeader();
        customHeader.setType(NettyMessageConstant.SERVER_AUTH_CERTI_TYPE);
        message.setCustomHeader(customHeader);
        message.setBodyMessage(body);
        return message;
    }

}

