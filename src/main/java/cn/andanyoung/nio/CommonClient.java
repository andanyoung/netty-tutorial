package cn.andanyoung.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class CommonClient {

  private SocketChannel clientSocket;
  private ByteBuffer recvBuffer;
  private SelectionKey key;
  private Callback callback;

  private String msg;

  public CommonClient(SocketChannel clientSocket, SelectionKey key) {
    this.clientSocket = clientSocket;
    this.key = key;
    recvBuffer = ByteBuffer.allocate(8);

    try {
      this.clientSocket.configureBlocking(false);
      key.interestOps(SelectionKey.OP_WRITE);
    } catch (IOException e) {
    }
  }

  public void close() {
    try {
      clientSocket.close();
      key.cancel();
    } catch (IOException e) {
    }
    ;
  }

  // an rpc to notify client to send a number
  public void sendMessage(String msg, Callback cback) {
    this.callback = cback;

    try {
      try {
        recvBuffer.clear();
        recvBuffer.put(msg.getBytes());
        recvBuffer.flip();
        clientSocket.write(recvBuffer);

        key.interestOps(SelectionKey.OP_READ);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (Exception e) {
    }
  }

  // when key is writable, resume the fiber to continue
  // to write.
  public void onWrite() {
    sendMessage(
        "divident",
        new Callback() {
          @Override
          public void onSucceed(int data) {
            int a = data;
            sendMessage(
                "divisor",
                new Callback() {
                  @Override
                  public void onSucceed(int data) {
                    int b = data;

                    sendMessage(String.valueOf(a / b), null);
                  }
                });
          }
        });
  }

  public void onRead() {
    int res = 0;
    try {
      try {
        recvBuffer.clear();

        // read may fail even SelectionKey is readable
        // when read fails, the fiber should suspend, waiting for next
        // time the key is ready.
        int n = clientSocket.read(recvBuffer);
        while (n == 0) {
          n = clientSocket.read(recvBuffer);
        }

        if (n == -1) {
          close();
          return;
        }

        System.out.println("received " + n + " bytes from client");
      } catch (IOException e) {
        e.printStackTrace();
      }

      recvBuffer.flip();
      res = getInt(recvBuffer);

      // when read ends, we are no longer interested in reading,
      // but in writing.
      key.interestOps(SelectionKey.OP_WRITE);
    } catch (Exception e) {
    }

    this.callback.onSucceed(res);
  }

  public int getInt(ByteBuffer buf) {
    int r = 0;
    while (buf.hasRemaining()) {
      r *= 10;
      r += buf.get() - '0';
    }

    return r;
  }
}
