package com.qee.protocal.test;

import com.qee.protocal.server.NettyProtocalServer;

/**
 * Created by zhuqi on 2017/9/8.
 */
public class ServerTest {
    public static void main(String[] args) {
        NettyProtocalServer server = new NettyProtocalServer("127.0.0.1", 8888);
        try {
            server.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
