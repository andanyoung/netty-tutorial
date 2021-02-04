package cn.andanyoung.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class ConnectStatusHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("Client connected:"+ctx.channel().remoteAddress() + " ID : "+ ctx.channel().id());
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Client exceptionCaught:"+ctx.channel().remoteAddress() + " ID : "+ ctx.channel().id());
        ctx.close();
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //这里执行客户端断开连接后的操作

        System.out.println("Client disConnect:"+ctx.channel().remoteAddress() + " ID : "+ ctx.channel().id());
        super.channelInactive(ctx);
    }
}
