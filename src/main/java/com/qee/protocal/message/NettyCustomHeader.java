package com.qee.protocal.message;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

/**
 * Created by zhuqi on 2017/9/7.
 */
@Data
@ToString
public class NettyCustomHeader {
    /**
     * code 2字节：netty协议消息， 1字节：主版本号 1字节：副版本号  4
     */
    private int code = 0xABCD0101;

    /**
     * 消息长度 ：消息头 和消息题 32
     */
    private int length;

    /**
     * 回话ID, 全局唯一 64
     */
    private long sessionId;

    /**
     * 业务请求消息  1:业务请求消息  2:业务响应消息  3:握手请求消息 4：握手应答消息 5：心跳请求消息  6：心跳应答消息
     */
    private byte type;

    /**
     * 优先级别
     */
    private byte primary;

    /**
     * 附件
     */
    Map<String, Object> attachment;

}
