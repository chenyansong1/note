---
title: 通道channel
categories: socket   
tags: [socket,NIO]
---



![](http://ols7leonh.bkt.clouddn.com//assert/img/java/socket/selector_channel.png)



channel(通道），他就像自来水管道一样，网络数据通过Channel读取和写入，通道与流不同之处在于通道是双向的，而流只是一个方向上移动（一个流必须是InputStream或者OutpuStream的子类），而通道可以用于读、写或者二者同时进行，事实上通道分为两大类，一类是网络读写的（SelectableChannel），一类是用于文件操作的（FileChannel），我们使用的SocketChannel和ServerSocketChannel都是SelectableChannel的子类



# 1.Channel的API




```
package java.nio.channels;
public interface Channel{
   public boolean isOpen( );
   public void close( ) throws IOException;
}

```


# 2.socket通道
&emsp;Socket通道有三个，分别是ServerSocketChannel、SocketChannel和DatagramChannel，而它们又分别对 应java.net包中的Socket对象ServerSocket、Socket和DatagramSocket；Socket通道被实例化时，都会创 建一个对等的Socket对象。Socket通道可以运行非阻塞模式并且是可选择的，非阻塞I/O与可选择性是紧密相连的，这也正是管理阻塞的API要在 SelectableChannel中定义的原因。设置非阻塞非常简单，只要调用configureBlocking(false)方法即可。如果需要中 途更改阻塞模式，那么必须首先获得blockingLock()方法返回的对象的锁

## 2.1.ServerSocketChannel
&emsp;ServerSocketChannel是一个<font color=red>基于通道的socket监听器。但它没有bind()方法，因此需要取出对等的Socket对象并使用它来 绑定到某一端口以开始监听连接</font>。在非阻塞模式下，当没有传入连接在等待时，其accept()方法会立即返回null。正是这种检查连接而不阻塞的能力实 现了可伸缩性并降低了复杂性，选择性也因此得以实现。

```
ByteBuffer buffer = ByteBuffer.wrap("Hello World".getBytes());
    ServerSocketChannel ssc = ServerSocketChannel.open();
    ssc.socket().bind(new InetSocketAddress(12345));
    ssc.configureBlocking(false);
 
    for (;;) {
        System.out.println("Waiting for connections");
        SocketChannel sc = ssc.accept();
        if (sc == null)
            TimeUnit.SECONDS.sleep(2000);
        else {
            System.out.println("Incoming connection from:" + sc.socket().getRemoteSocketAddress());
            buffer.rewind();
            sc.write(buffer);
            sc.close();
        }
       }
```


## 2.2.SocketChannel

&emsp;相对于ServerSocketChannel，它扮演客户端，发起到监听服务器的连接，连接成功后，开始接收数据。要注意的是，调用它的open()方法仅仅是打开但并未连接，要建立连接需要紧接着调用connect()方法；也可以两步合为一步，调用open(SocketAddress remote)方法。你会发现connect()方法并未提供timout参数，作为替代方案，你可以用isConnected()、isConnectPending()或finishConnect()方法来检查连接状态。 


 


