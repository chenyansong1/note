---
title: 选择器Selector
categories: socket   
tags: [socket,NIO]
---

# 1.通道、选择键、选择器之间的关系示意图


![](http://ols7leonh.bkt.clouddn.com//assert/img/java/socket/channel_selector_selectkey.png)


![](/Users/chenyansong/Desktop/nio_2.png)


Selector（多路复用器），他是NIO编程的基础，非常重要，多路复用器提供选择已经就绪的任务的能力，简单说，就是Selector会不断的轮询注册在其上的通道（Channel），如果某个通道发生了读写操作，这个通道就处于就绪状态，会被Selector轮询出来，然后通过SelectorKey可以取得就绪的Channel集合（可能是多个Channel，所以是集合），从而进行后续的IO操作

一个多路复用器（Selector）可以负责成千上万Channel通道，没有上限，这也是JDK替代了传统的Selector实现，获取连接句柄没有限制，也就意味着我们只要一个线程负责Selector的轮询，就可以接入成千上万哥客户端，这是JDK NIO库的巨大进步

Selector线程就类似于一个管理者（Master），管理了成千上万个管道，然后轮询那个管道的数据已经准备好，通知CPU执行IO的读取或者写入操作

Selector模式：当IO事件（管道）注册到选择器以后，Selector会分配给每个管道一个key值，相当于标签，Selector选择器是以轮询的方式进行查找注册的所有IO事件（管道），当我们的IO事件（管道）准备就绪后，Selector就会识别，会通过key值找到相应的管道，进行相关的数据处理操作（从管道里读或者写数据，写到我们的数据缓冲区中）

每个管道都会对选择器进行注册不同的事件状态，以便选择器查找

* SelectorKey.OP_CONNECT
* SelectorKey.OP_ACCEPT
* SelectorKey.OP_READ
* SelectorKey.OP_WRITE





# 2.Selector
```
public abstract class Selector{
   public static Selector open( ) throws IOException        #实例化
   public abstract boolean isOpen( );
   public abstract void close( ) throws IOException;
   public abstract SelectionProvider provider( );
	/*
	当再次调用select( )方法时（或者一个正在进行的select()调用结束时），已取消的键的集合中的被取消的键将被清理掉，并且相应的注销也将完成。
	通道会被注销，而新的SelectionKey将被返回。依赖于特定的select( )方法调用，如果没有通道已经准备好，线程可能会在这时阻塞，通常会有一个超时值
	*/
   public abstract int select( ) throws IOException;
   public abstract int select (long timeout) throws IOException;
   public abstract int selectNow( ) throws IOException;
   public abstract void wakeup( );
   public abstract Set keys( );
   public abstract Set selectedKeys( );
}

```


# 3.SelectionKey
```
public abstract class SelectionKey{
   public static final int OP_READ
   public static final int OP_WRITE
   public static final int OP_CONNECT
   public static final int OP_ACCEPT
   public abstract SelectableChannel channel( );
   public abstract Selector selector( );
   public final boolean isReadable( )
   public final boolean isWritable( )
   public final boolean isConnectable( )
   public final boolean isAcceptable( )
   public final Object attach (Object ob)
   public final Object attachment( )
}

```


# 4.Example
```
Selector selector = Selector.open( );
channel1.register (selector, SelectionKey.OP_READ);    #将通道注册到选择器上，并指定对应的事件
channel2.register (selector, SelectionKey.OP_WRITE);
channel3.register (selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
// Wait up to 10 seconds for a channel to become ready
readyCount = selector.select (10000);    #阻塞方法，知道过10s或者至少有一个通道的I/O操作准备好
```
 