package com.qee.protocal.test;

import com.qee.protocal.authentication.AuthorityCertificationRequestHanlder;
import com.qee.protocal.client.NettyProtocalClientTemplate;
import com.qee.protocal.decoder.ByteBuf2NettyMessageDecoder;
import com.qee.protocal.encoder.NettyMessage2ByteBufEncoder;
import com.qee.protocal.heartbeat.HeartBeatCheckRequestHandler;
import com.qee.protocal.model.Pair;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuqi on 2017/9/11.
 */
public class NettyClientTemplateInstance extends NettyProtocalClientTemplate {

    public NettyClientTemplateInstance(String host, int port) {
        super(host, port);
    }

    @Override
    public void doSimpleBussiness(Channel channel) {

    }

    @Override
    public List<Pair<String, ChannelHandler>> addIOChannelHandlers() {
        List<Pair<String, ChannelHandler>> ret = new ArrayList<>();
        ret.add(new Pair("log", new LoggingHandler(LogLevel.INFO)));
        ret.add(new Pair("decoder", new ByteBuf2NettyMessageDecoder(6 * 1024, 4, 4, -8, 0, true)));
        ret.add(new Pair("encoder", new NettyMessage2ByteBufEncoder()));
        ret.add(new Pair("timeout", new ReadTimeoutHandler(50)));
        ret.add(new Pair("authority", new AuthorityCertificationRequestHanlder()));
        ret.add(new Pair("hearbeat", new HeartBeatCheckRequestHandler()));
        return ret;
    }

    @Override
    public List<Pair<String, ChannelHandler>> addBusinessChannelHandlers() {
        return null;
    }

    @Override
    public void start() throws InterruptedException {
        super.connect();
    }

    @Override
    public void stop() {

    }
}
