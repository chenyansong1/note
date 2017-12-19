---
title: 选择器Selector
categories: socket   
tags: [socket,NIO]
---

# 1.通道、选择键、选择器之间的关系示意图


![](http://ols7leonh.bkt.clouddn.com//assert/img/java/socket/channel_selector_selectkey.png)

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
 