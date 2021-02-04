package cn.andanyoung.netty.demo;

import cn.andanyoung.netty.demo.handler.ConnectStatusHandler;
import cn.andanyoung.netty.demo.handler.HelloServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 我们可以通过 `ServerBootstrap` 来引导我们启动一个简单的 Netty 服务端，为此，你必须要为其指定下面三类属性：
 *
 * 1. **线程组**（*一般需要两个线程组，一个负责处理客户端的连接，一个负责具体的 IO 处理*）
 * 2. **IO 模型**（*BIO/NIO*）
 * 3. **自定义
 * `ChannelHandler`（*处理客户端发过来的数据并返回数据给客户端*）
 */
public final class HelloServer {

  private final int port;

  public HelloServer(int port) {
    this.port = port;
  }

  public static void main(String[] args) throws InterruptedException {
    new HelloServer(8080).start();
  }

  private void start() throws InterruptedException {
    // 1.bossGroup 用于接收连接，workerGroup 用于具体的处理
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      // 2.创建服务端启动引导/辅助类：ServerBootstrap
      ServerBootstrap b = new ServerBootstrap();
      // 3.给引导类配置两大线程组,确定了线程模型
      b.group(bossGroup, workerGroup)
          // (非必备)打印日志
          .handler(new LoggingHandler(LogLevel.INFO))
          // 4.指定 IO 模型
          .channel(NioServerSocketChannel.class)
              //设置客户端handle
          .childHandler(
              new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                  ChannelPipeline p = ch.pipeline();
                  // 5.可以自定义客户端消息的业务处理逻辑
                  p.addLast(new HelloServerHandler());
                  p.addLast(new ConnectStatusHandler());
                }
              });
      // 6.绑定端口,调用 sync 方法阻塞知道绑定完成
      ChannelFuture f = b.bind(port).sync();
      // 7.阻塞等待直到服务器Channel关闭(closeFuture()方法获取Channel 的CloseFuture对象,然后调用sync()方法)
      f.channel().closeFuture().sync();
    } finally {
      // 8.优雅关闭相关线程组资源
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}
