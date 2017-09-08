package com.qee.protocal.encoder;

import com.qee.protocal.factory.JavaByteFactory;
import com.qee.protocal.factory.NettyMarshallingFactory;
import com.qee.protocal.message.NettyCustomMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by zhuqi on 2017/9/7.
 */

/**
 * 编码器 ，NettyCustomMessage to  ByteBuf
 */
public class NettyMessage2ByteBufEncoder extends MessageToMessageEncoder<NettyCustomMessage> {

    private NettyMarshallingEncoder nettyMarshallingEncoder;

    public NettyMessage2ByteBufEncoder() {
        // this.nettyMarshallingEncoder = NettyMarshallingFactory.buildNettyMarshallingEncoder();

    }

    protected void encode(ChannelHandlerContext ctx, NettyCustomMessage msg, List<Object> out) throws Exception {

        if (msg == null || msg.getCustomHeader() == null) {
            throw new Exception("the encode message is null");
        }

        ByteBuf sendBuf = Unpooled.buffer();
        sendBuf.writeInt(msg.getCustomHeader().getCode());
        sendBuf.writeInt(msg.getCustomHeader().getLength());
        sendBuf.writeLong(msg.getCustomHeader().getSessionId());
        sendBuf.writeByte(msg.getCustomHeader().getType());
        sendBuf.writeByte(msg.getCustomHeader().getPrimary());

        //attachment ,

        if (msg.getCustomHeader().getAttachment() != null) {
            sendBuf.writeByte(msg.getCustomHeader().getAttachment().size());
            String key = null;
            byte[] keyArray = null;
            for (Map.Entry<String, Object> entryKey : msg.getCustomHeader().getAttachment().entrySet()) {
                key = entryKey.getKey();
                keyArray = key.getBytes(CharsetUtil.UTF_8.name());
                sendBuf.writeInt(keyArray.length);
                sendBuf.writeBytes(keyArray);
                ByteBuf value = JavaByteFactory.encode(entryKey.getValue());
                sendBuf.writeBytes(value);
                // nettyMarshallingEncoder.encode(ctx, entryKey.getValue(), sendBuf);
            }
        } else {
            sendBuf.writeByte(0);
        }


        if (msg.getBodyMessage() != null) {
            ByteBuf value = JavaByteFactory.encode(msg.getBodyMessage());
            sendBuf.writeBytes(value);
            //nettyMarshallingEncoder.encode(ctx, msg.getBodyMessage(), sendBuf);
        }

        //在第5个字节开始的int 是长度，重新设置
        sendBuf.setInt(4, sendBuf.readableBytes());

        out.add(sendBuf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.getStackTrace();
        super.exceptionCaught(ctx, cause);
    }
}
