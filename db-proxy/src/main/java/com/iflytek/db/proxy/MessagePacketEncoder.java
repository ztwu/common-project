package com.iflytek.db.proxy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessagePacketEncoder extends MessageToByteEncoder<Object>{
    public MessagePacketEncoder(){

    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out)
            throws Exception {
        System.out.println("encode");
        System.out.println(msg);
        try {
            //在这之前可以实现编码工作。
            out.writeBytes((byte[])msg);
        }finally {

        }
    }
}