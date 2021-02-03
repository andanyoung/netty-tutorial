package cn.andanyoung.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class EchoServer {

  public static void main(String[] args) throws IOException {

    // 创建通道管理器(Selector)
    Selector selector = Selector.open();

    // 创建通道ServerSocketChannel TCP/IP ，DatagramChannel则模拟包导向的无连接协议（如UDP/IP）。
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    // 将通道设置为非阻塞
    serverSocketChannel.configureBlocking(false);
    // 将ServerSocketChannel对应的ServerSocket绑定到指定端口(port)
    serverSocketChannel.bind(new InetSocketAddress(6666));

    /**
     * 将通道(Channel)注册到通道管理器(Selector)，并为该通道注册selectionKey.OP_ACCEPT事件
     * 注册该事件后，当事件到达的时候，selector.select()会返回， 如果事件没有到达selector.select()会一直阻塞。
     */
    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

    while (true) {
      // 当注册事件到达时，方法返回，否则该方法会一直阻塞
      selector.select();

      // 获取监听事件
      Set<SelectionKey> selectionKeys = selector.selectedKeys();
      Iterator<SelectionKey> iterator = selectionKeys.iterator();

      // 迭代处理
      while (iterator.hasNext()) {

        // 获取事件
        SelectionKey selectionKey = iterator.next();

        // 移除事件，避免重复处理
        iterator.remove();

        // 检查是否是一个就绪的可以被接受的客户端请求连接
        if (selectionKey.isAcceptable()) {
          handleAccept(selector, selectionKey);
        } else if (selectionKey.isReadable()) {
          // 检查套接字是否已经准备好读数据
          handleRead(selector, selectionKey);
        }
      }
    }
  }

  /** 处理客户端连接成功事件 */
  private static void handleAccept(Selector selector, SelectionKey key) throws IOException {

    // 获取客户端连接通道
    ServerSocketChannel server = (ServerSocketChannel) key.channel();
    SocketChannel socketChannel = server.accept();
    socketChannel.configureBlocking(false);
    int fd = 0;
    // int fd = ((SelChImpl) server).getFDVal();
    System.out.println("accept new conn: " + socketChannel.getRemoteAddress() + ", fd" + fd);

    // 给通道设置读事件，客户端监听到读事件后，进行读取操作
    socketChannel.register(selector, SelectionKey.OP_READ);
  }

  /** 监听到读事件，读取客户端发送过来的消息 */
  private static void handleRead(Selector selector, SelectionKey selectionKey) throws IOException {
    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

    // 从通道读取数据到缓冲区
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    int length = socketChannel.read(buffer);

    if (length == -1) {

      // 如果read（）方法返回-1，说明客户端关闭了连接，那么客户端已经接收到了与自己发送字节数相等的数据，可以安全地关闭
      selectionKey.cancel();
      socketChannel.close();
      System.out.println("socket disconnect");
    } else if (length > 0) {

      buffer.flip(); // 反转此缓冲区  make buffer ready for read
      byte[] bytes = new byte[buffer.remaining()]; // 返回当前位置与上界之间的元素数
      buffer.get(bytes);
      String content = new String(bytes, StandardCharsets.UTF_8);
      if ("quit".equalsIgnoreCase(content)) {
        selectionKey.cancel();
        socketChannel.close();
      } else {
        System.out.println(content);
      }
    }

    // 信息通过通道发送给客户端
    String msg = "Hello Client!";
    socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
  }
}
