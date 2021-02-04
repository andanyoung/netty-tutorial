package cn.andanyoung.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class ConnectStatusHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("Client connected:"+ctx.channel().remoteAddress() + " ID : "+ ctx.channel().id());
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        //这里执行客户端断开连接后的操作

        System.out.println("Client disConnect:"+ctx.channel().remoteAddress() + " ID : "+ ctx.channel().id());
    }
}
