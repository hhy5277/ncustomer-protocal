package com.qee.protocal.service;

import io.netty.channel.Channel;

/**
 * Created by zhuqi on 2017/9/11.
 */
public abstract class BusinessService {
    public abstract void doSimpleBussiness(Channel channel);
}
