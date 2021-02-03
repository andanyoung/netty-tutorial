package cn.andanyoung.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class EpollServer {
  public static void main(String[] args) {
    try {
      ServerSocketChannel ssc = ServerSocketChannel.open();
      ssc.socket().bind(new InetSocketAddress("127.0.0.1", 8000));
      ssc.configureBlocking(false);
      System.out.println(System.identityHashCode(ssc));

      Selector selector = Selector.open();
      // 注册 channel，并且指定感兴趣的事件是 Accept
      ssc.register(selector, SelectionKey.OP_ACCEPT);

      ByteBuffer readBuff = ByteBuffer.allocate(1024);
      ByteBuffer writeBuff = ByteBuffer.allocate(128);
      writeBuff.put("received".getBytes());
      writeBuff.flip();

      while (true) {
        int nReady = selector.select();
        Set<SelectionKey> keys = selector.selectedKeys();
        Iterator<SelectionKey> it = keys.iterator();

        while (it.hasNext()) {
          SelectionKey key = it.next();
          it.remove();

          if (key.isAcceptable()) {
            // 创建新的连接，并且把连接注册到selector上，而且，
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            System.out.println(System.identityHashCode(serverSocketChannel));
            // 声明这个channel只对读操作感兴趣。
            SocketChannel socketChannel = ssc.accept();

            System.out.println(socketChannel);
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
          } else if (key.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            readBuff.clear();
            socketChannel.read(readBuff);

            readBuff.flip();
            System.out.println("received : " + new String(readBuff.array()));
            System.out.println(System.identityHashCode(socketChannel));
            System.out.println(key.isWritable());

            key.interestOps(SelectionKey.OP_WRITE);
          } else if (key.isWritable()) {
            writeBuff.rewind();
            SocketChannel socketChannel = (SocketChannel) key.channel();
            socketChannel.write(writeBuff);
            System.out.println(System.identityHashCode(socketChannel));
            key.interestOps(SelectionKey.OP_READ);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
