package com.qee.protocal.template;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.Map;

/**
 * Created by zhuqi on 2017/9/10.
 */
public abstract class NettyProtocalTemplate {


    /**
     * 添加 io channel handler
     *
     * @return
     */
    public abstract Map<String, ChannelHandler> addIOChannelHandlers();


    /**
     * 添加 业务 channel handler
     *
     * @return
     */
    public abstract Map<String, ChannelHandler> addBusinessChannelHandlers();

    /**
     * 启动服务
     */
    public abstract void start() throws InterruptedException;

    /**
     * 关闭服务
     */
    public abstract void stop();


    public void addChannelHandler(Channel channel, Map<String, ChannelHandler> handlerMap, boolean isBusinessHandler, EventLoopGroup business) {
        if (handlerMap != null && handlerMap.size() > 0) {
            ChannelPipeline pipeline = channel.pipeline();
            for (Map.Entry<String, ChannelHandler> entry : handlerMap.entrySet()) {
                String handlerName = entry.getKey();
                ChannelHandler channelHandler = entry.getValue();
                if (isBusinessHandler) {
                    pipeline.addLast(business, handlerName, channelHandler);

                } else {
                    pipeline.addLast(handlerName, channelHandler);

                }
            }
        }
    }


}
