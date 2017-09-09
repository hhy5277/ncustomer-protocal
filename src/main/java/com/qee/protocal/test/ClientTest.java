package com.qee.protocal.test;

import com.qee.protocal.client.NettyProtocalClient;

/**
 * Created by zhuqi on 2017/9/8.
 */
public class ClientTest {
    public static void main(String[] args) {
        NettyProtocalClient nettyProtocalClient = new NettyProtocalClient("127.0.0.1", 8888);
        try {
            nettyProtocalClient.connect();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

  /*  public static void main(String[] args) {
        final int [] port ={7777,6666,9999,4444};

        for( int i=0;i<4;i++){
            final int k=i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NettyProtocalClient nettyProtocalClient = new NettyProtocalClient(port[k],"127.0.0.1", 8888);
                    try {
                        nettyProtocalClient.connect();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }*/
}
