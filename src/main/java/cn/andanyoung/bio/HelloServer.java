package cn.andanyoung.bio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class HelloServer {
  private static final Logger logger = LoggerFactory.getLogger(HelloServer.class);

  public static void main(String[] args) {
    HelloServer helloServer = new HelloServer();
    helloServer.start(6666);
  }

  public void start(int port) {

    ThreadFactory threadFactory = Executors.defaultThreadFactory();
    ExecutorService threadPool =
        new ThreadPoolExecutor(
            10, 100, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(100), threadFactory);

    // 1.创建 ServerSocket 对象并且绑定一个端口
    try (ServerSocket server = new ServerSocket(port); ) {
      Socket socket;
      // 2.通过 accept()方法监听客户端请求， 这个方法会一直阻塞到有一个连接建立
      while ((socket = server.accept()) != null) {
        logger.info("client connected");
        Socket finalSocket = socket;
        threadPool.execute(
            () -> {
              try (ObjectInputStream objectInputStream =
                      new ObjectInputStream(finalSocket.getInputStream());
                  ObjectOutputStream objectOutputStream =
                      new ObjectOutputStream(finalSocket.getOutputStream())) {
                // 3.通过输入流读取客户端发送的请求信息
                Message message = (Message) objectInputStream.readObject();
                logger.info("server receive message:" + message.getContent());
                message.setContent("new content");
                // 4.通过输出流向客户端发送响应信息
                objectOutputStream.writeObject(message);
                objectOutputStream.flush();
              } catch (IOException | ClassNotFoundException e) {
                logger.error("occur exception:", e);
              }
            });
      }

    } catch (IOException e) {
      logger.error("occur IOException:", e);
    }
  }
}
