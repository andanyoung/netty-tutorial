package cn.andanyoung.netty.official.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 把int值转为bytes数组
     *
     * @param num
     * @return
     */
    public static byte[] int2Bytes(int num) {
        byte[] bytes = new byte[4];
        // 通过移位运算，截取低8位的方式，将int保存到byte数组
        bytes[0] = (byte) (num >>> 24);
        bytes[1] = (byte) (num >>> 16);
        bytes[2] = (byte) (num >>> 8);
        bytes[3] = (byte) num;
        return bytes;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) { // (1)

        Integer intValue = (int) (System.currentTimeMillis() / 1000L + 2208988800L);
        byte[] bytes = int2Bytes(intValue);
        System.out.println("发送数据："+intValue);
        System.out.println("总共需要发送" + bytes.length + "次");
        for (int i = 1; i <= bytes.length; i++) {
            System.out.println("发送第" + i + "次");
            final ByteBuf time = ctx.alloc().buffer(1);
            time.writeBytes(bytes, i - 1, 1);
            ctx.writeAndFlush(time);

            try {
                // 通过sleep模拟发送不完整数据
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}
