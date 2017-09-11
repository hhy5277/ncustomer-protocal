package com.qee.protocal.test;

/**
 * Created by zhuqi on 2017/9/11.
 */
public class ServerTemplateTest {
    public static void main(String[] args) {
      NettyServerTemplateInstance instance = new NettyServerTemplateInstance("127.0.0.1",8888);
        try {
            instance.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
