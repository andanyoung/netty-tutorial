# [【Netty 教程系列】](https://blog.csdn.net/agonie201218/category_10790738.html)

简单用 3 点概括一下 Netty 吧！

1. **Netty 是一个基于 NIO 的 client-server(客户端服务器)框架，使用它可以快速简单地开发网络应用程序。**
2. 它极大地简化并简化了 TCP 和 UDP 套接字服务器等网络编程,并且性能以及安全性等很多方面甚至都要更好。
3. 支持多种协议如 FTP，SMTP，HTTP 以及各种二进制和基于文本的传统协议。

用官方的总结就是：**Netty 成功地找到了一种在不妥协可维护性和性能的情况下实现易于开发，性能，稳定性和灵活性的方法。**

## Netty 特点

根据官网的描述，我们可以总结出下面一些特点：

- 统一的 API，支持多种传输类型，阻塞和非阻塞的。
- 简单而强大的线程模型。
- 自带编解码器解决 TCP 粘包/拆包问题。
- 自带各种协议栈。
- 真正的无连接数据包套接字支持。
- 比直接使用 Java 核心 API 有更高的吞吐量、更低的延迟、更低的资源消耗和更少的内存复制。
- 安全性不错，有完整的 SSL/TLS 以及 StartTLS 支持。
- 社区活跃
- 成熟稳定，经历了大型项目的使用和考验，而且很多开源项目都使用到了 Netty 比如我们经常接触的 Dubbo、RocketMQ 等等。
- …

## Netty架构总览

下面是Netty的模块设计部分：

![Netty架构总览](https://img-blog.csdnimg.cn/20210202172452301.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Fnb25pZTIwMTIxOA==,size_16,color_FFFFFF,t_70#pic_center)

- Netty提供了通用的传输API（TCP/UDP…）；
- 多种网络协议（HTTP/WebSocket…）；
- 基于事件驱动的IO模型；
- 超高性能的零拷贝…

上面说的这些模块和功能只是Netty的一部分，具体的组件在后面的部分会有较为详细的介绍。

## 使用 Netty 能做什么？

这个应该是老铁们最关心的一个问题了，凭借自己的了解，简单说一下，理论上 NIO 可以做的事情 ，使用 Netty 都可以做并且更好。Netty 主要用来做**网络通信** :

1. **作为 RPC 框架的网络通信工具** ： 我们在分布式系统中，不同服务节点之间经常需要相互调用，这个时候就需要 RPC 框架了。不同服务指点的通信是如何做的呢？可以使用 Netty 来做。比如我调用另外一个节点的方法的话，至少是要让对方知道我调用的是哪个类中的哪个方法以及相关参数吧！
2. **实现一个自己的 HTTP 服务器** ：通过 Netty 我们可以自己实现一个简单的 HTTP 服务器，这个大家应该不陌生。说到 HTTP 服务器的话，作为 Java 后端开发，我们一般使用 Tomcat 比较多。一个最基本的 HTTP 服务器可要以处理常见的 HTTP Method 的请求，比如 POST 请求、GET 请求等等。
3. **实现一个即时通讯系统** ： 使用 Netty 我们可以实现一个可以聊天类似微信的即时通讯系统，这方面的开源项目还蛮多的，可以自行去 Github 找一找。
4. **消息推送系统** ：市面上有很多消息推送系统都是基于 Netty 来做的。
5. …

## 哪些开源项目用到了 Netty？

我们平常经常接触的 Dubbo、RocketMQ、Elasticsearch、gRPC 等等都用到了 Netty。

可以说大量的开源项目都用到了 Netty，所以掌握 Netty 有助于你更好的使用这些开源项目并且让你有能力对其进行二次开发。

实际上还有很多很多优秀的项目用到了 Netty,Netty 官方也做了统计，统计结果在这里：https://netty.io/wiki/related-projects.html 。

![Netty架构总览](https://img-blog.csdnimg.cn/img_convert/4b56c653e76170dc605885cee7582b73.png)



## Netty特性

### 强大的数据容器

Netty使用自建的Buffer API实现ByteBuf，而不是使用JDK NIO的ByteBuffer来表示一个连续的字节序列。与JDK NIO的
ByteBuffer相比，Netty的ByteBuf有更加明显的优势，这些优势可以弥补Java原生ByteBuffer的底层缺点，并提供
更加方便的编程模型：

- 正常情况下，ByteBuf比ByteBuffer的性能更好；
- 实现了ReferenceCounted引用计数接口，优化了内存的使用；
- 容量可以动态增长，如StringBuilder之于String；
- 在读和写这两种模式切换时，无需像ByteBuffer一样调用flip方法，更易于操作；

# [【Netty 教程系列】](https://blog.csdn.net/agonie201218/category_10790738.html)

