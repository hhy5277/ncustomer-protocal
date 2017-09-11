package com.qee.protocal.template;

import com.qee.protocal.model.Pair;
import com.qee.protocal.service.BusinessService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;

import java.util.List;
import java.util.Map;

/**
 * Created by zhuqi on 2017/9/10.
 */
public abstract class NettyProtocalTemplate extends BusinessService {


    /**
     * 添加 io channel handler
     *
     * @return
     */
    public abstract List<Pair<String, ChannelHandler>> addIOChannelHandlers();


    /**
     * 添加 业务 channel handler
     *
     * @return
     */
    public abstract List<Pair<String, ChannelHandler>> addBusinessChannelHandlers();

    /**
     * 启动服务
     */
    public abstract void start() throws InterruptedException;

    /**
     * 关闭服务
     */
    public abstract void stop();


    public void addChannelHandler(Channel channel, List<Pair<String, ChannelHandler>> handlers, boolean isBusinessHandler, EventLoopGroup business) {
        if (handlers != null && handlers.size() > 0) {
            ChannelPipeline pipeline = channel.pipeline();
            for (Pair<String, ChannelHandler> pair : handlers) {
                String handlerName = pair.getFirst();
                ChannelHandler channelHandler = pair.getSecond();
                if (isBusinessHandler) {
                    pipeline.addLast(business, handlerName, channelHandler);

                } else {
                    pipeline.addLast(handlerName, channelHandler);

                }
            }
        }
    }


}
