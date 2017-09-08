package com.qee.protocal.message;

import lombok.Data;
import lombok.ToString;

/**
 * Created by zhuqi on 2017/9/7.
 */
@Data
@ToString
public class NettyCustomMessage {

    /**
     * 消息头
     */
    private NettyCustomHeader customHeader;

    /**
     * 消息体
     */
    private Object bodyMessage;


}
