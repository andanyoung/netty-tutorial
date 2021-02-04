package cn.andanyoung.netty.official.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class TimeDecoder extends ByteToMessageDecoder { // (1)

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("TimeDecoder 收到了数据");
        if (in.readableBytes() < 4) {
            return; // (3)
        }
        out.add(in.readBytes(4)); // (4)

    }
}