package com.qee.protocal.test;


/**
 * Created by zhuqi on 2017/9/11.
 */
public class ClientTemplateTest {
    public static void main(String[] args) {
        NettyClientTemplateInstance instance = new NettyClientTemplateInstance("127.0.0.1", 8888);
        try {
            instance.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
