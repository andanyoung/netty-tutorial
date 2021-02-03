package cn.andanyoung.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class CallbackServer {

  public static void main(String[] args) throws Exception {
    CallbackServer server = new CallbackServer();
    server.run();
  }

  public void run() throws IOException {
    ServerSocketChannel serverChannel = ServerSocketChannel.open();
    serverChannel.configureBlocking(false);
    serverChannel.socket().bind(new InetSocketAddress("127.0.0.1", 8000));

    Selector selector = Selector.open();
    serverChannel.register(selector, SelectionKey.OP_ACCEPT);

    while (true) {
      selector.select();
      Iterator ite = selector.selectedKeys().iterator();

      while (ite.hasNext()) {
        SelectionKey key = (SelectionKey) ite.next();

        if (key.isAcceptable()) {
          ServerSocketChannel s = (ServerSocketChannel) key.channel();
          SocketChannel clientSocket = s.accept();
          System.out.println("Got a new Connection");

          clientSocket.configureBlocking(false);

          SelectionKey newKey = clientSocket.register(selector, SelectionKey.OP_WRITE);

          CommonClient client = new CommonClient(clientSocket, newKey);
          newKey.attach(client);

          System.out.println("client waiting");
        } else if (key.isReadable()) {
          CommonClient client = (CommonClient) key.attachment();
          client.onRead();
        } else if (key.isWritable()) {
          CommonClient client = (CommonClient) key.attachment();
          client.onWrite();
        }

        ite.remove();
      }
    }
  }
}
