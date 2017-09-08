package com.qee.protocal.decoder;

import com.qee.protocal.factory.JavaByteFactory;
import com.qee.protocal.factory.NettyMarshallingFactory;
import com.qee.protocal.message.NettyCustomHeader;
import com.qee.protocal.message.NettyCustomMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import io.netty.util.CharsetUtil;

import java.nio.ByteOrder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuqi on 2017/9/7.
 */
public class ByteBuf2NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    // private NettyMarshallingDecoder marshallingDecoder = NettyMarshallingFactory.buildNettyMarshallingDecoder();

    public ByteBuf2NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    public ByteBuf2NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    public ByteBuf2NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, boolean failFast) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
    }

    public ByteBuf2NettyMessageDecoder(ByteOrder byteOrder, int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, boolean failFast) {
        super(byteOrder, maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        //调用父类decode ,得到整包消息
        ByteBuf readBuf = (ByteBuf) super.decode(ctx, in);
        if (readBuf == null) {
            return null;
        }
        NettyCustomMessage customMessage = new NettyCustomMessage();
        NettyCustomHeader customHeader = new NettyCustomHeader();
        customHeader.setCode(readBuf.readInt());
        customHeader.setLength(readBuf.readInt());
        customHeader.setSessionId(readBuf.readLong());
        customHeader.setType(readBuf.readByte());
        customHeader.setPrimary(readBuf.readByte());

        int attachmentSize = readBuf.readByte();
        if (attachmentSize > 0) {
            Map<String, Object> attachment = new HashMap<String, Object>();
            for (int i = 0; i < attachmentSize; i++) {
                int keySize = readBuf.readInt();
                byte[] keyByte = new byte[keySize];
                readBuf.readBytes(keyByte);
                String key = new String(keyByte, CharsetUtil.UTF_8.name());

                Object value = JavaByteFactory.decode(readBuf);
                //Object value = marshallingDecoder.decode(ctx, readBuf);
                attachment.put(key, value);
            }
            customHeader.setAttachment(attachment);
        }

        customMessage.setCustomHeader(customHeader);
        if (readBuf.readableBytes() > 0) {
            Object body = JavaByteFactory.decode(readBuf);
            //Object body = marshallingDecoder.decode(ctx, readBuf);
            customMessage.setBodyMessage(body);
        }

        return customMessage;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.getStackTrace();
        super.exceptionCaught(ctx, cause);
    }
}
