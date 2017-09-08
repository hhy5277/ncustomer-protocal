package com.qee.protocal.factory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.*;

/**
 * Created by zhuqi on 2017/9/8.
 */
public class JavaByteFactory {


    public static Object decode(ByteBuf byteBuf) {
        if (byteBuf == null || byteBuf.readableBytes() <= 0) {
            return null;
        }
        int valueSize = byteBuf.readInt();
        byte[] value = new byte[valueSize];
        byteBuf.readBytes(value);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(value);
        ObjectInputStream inputStream = null;
        try {
            inputStream = new ObjectInputStream(byteArrayInputStream);
            return inputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;


    }

    public static ByteBuf encode(Object object) {
        if (object == null) {
            return null;
        }
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutput);
            objectOutputStream.writeObject(object);
            byte[] bytes = byteOutput.toByteArray();

            ByteBuf buffer = Unpooled.buffer(bytes.length + 4);
            buffer.writeInt(bytes.length);
            buffer.writeBytes(bytes);
            return buffer;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
