---
title: netty中handler的执行顺序
categories: socket   
tags: [java,netty]
---

# 1.handler处理器简介

&emsp;Handler在netty中，无疑占据着非常重要的地位。Handler与Servlet中的filter很像，通过Handler可以完成通讯报文的解码编码、拦截指定的报文、统一对日志错误进行处理、统一对请求进行计数、控制Handler执行与否。一句话，没有它做不到的只有你想不到的。
&emsp;Netty中的所有handler都实现自ChannelHandler接口。按照输出输出来分，分为ChannelInboundHandler、ChannelOutboundHandler两大类。ChannelInboundHandler对从客户端发往服务器的报文进行处理，一般用来执行解码、读取客户端数据、进行业务处理等；ChannelOutboundHandler对从服务器发往客户端的报文进行处理，一般用来进行编码、发送报文到客户端。
&emsp;Netty中，可以注册多个handler。ChannelInboundHandler按照注册的先后顺序执行；ChannelOutboundHandler按照注册的先后顺序逆序执行，如下图所示，按照注册的先后顺序对Handler进行排序,request进入Netty后的执行顺序为：

![](http://ols7leonh.bkt.clouddn.com//assert/img/java/netty/hander.png)

# 2.Example代码
## 2.1.服务端启动类
```
package cn.itcast_03_netty.sendorder.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import cn.itcast_03_netty.sendobject.coder.PersonDecoder;

/**
 * • 配置服务器功能，如线程、端口 • 实现服务器处理程序，它包含业务逻辑，决定当有一个请求连接或接收数据时该做什么
 * 
 * @author wilson
 *
 */
public class EchoServer {

	private final int port;

	public EchoServer(int port) {
		this.port = port;
	}

	public void start() throws Exception {
		EventLoopGroup eventLoopGroup = null;
		try {
			//server端引导类
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			//连接池处理数据
			eventLoopGroup = new NioEventLoopGroup();
			serverBootstrap.group(eventLoopGroup)
			.channel(NioServerSocketChannel.class)//指定通道类型为NioServerSocketChannel，一种异步模式，OIO阻塞模式为OioServerSocketChannel
			.localAddress("localhost",port)//设置InetSocketAddress让服务器监听某个端口已等待客户端连接。
			.childHandler(new ChannelInitializer<Channel>() {//设置childHandler执行所有的连接请求
				@Override
				protected void initChannel(Channel ch) throws Exception {
					// 注册两个InboundHandler，执行顺序为注册顺序，所以应该是InboundHandler1 InboundHandler2
					// 注册两个OutboundHandler，执行顺序为注册顺序的逆序，所以应该是OutboundHandler2 OutboundHandler1
					ch.pipeline().addLast(new EchoInHandler1());
					ch.pipeline().addLast(new EchoInHandler2());
					ch.pipeline().addLast(new EchoOutHandler1());
					ch.pipeline().addLast(new EchoOutHandler2()); 
					
				}
					});
			// 最后绑定服务器等待直到绑定完成，调用sync()方法会阻塞直到服务器完成绑定,然后服务器等待通道关闭，因为使用sync()，所以关闭操作也会被阻塞。
			ChannelFuture channelFuture = serverBootstrap.bind().sync();
			System.out.println("开始监听，端口为：" + channelFuture.channel().localAddress());
			channelFuture.channel().closeFuture().sync();
		} finally {
			eventLoopGroup.shutdownGracefully().sync();
		}
	}

	public static void main(String[] args) throws Exception {
		new EchoServer(20000).start();
	}
}

```

## 2.2.服务端回调方法（处理器）
```
######################################  EchoInHandler1  #################################################################
package cn.itcast_03_netty.sendorder.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

import cn.itcast_03_netty.sendobject.bean.Person;

public class EchoInHandler1 extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("in1");
		 // 通知执行下一个InboundHandler
        ctx.fireChannelRead(msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();//刷新后才将数据发出到SocketChannel
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)	throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}

################################## EchoInHandler2 #####################################################################
package cn.itcast_03_netty.sendorder.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

import cn.itcast_03_netty.sendobject.bean.Person;

public class EchoInHandler2 extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("in2");
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");
        System.out.println("接收客户端数据:" + body);
        //向客户端写数据
        System.out.println("server向client发送数据");
        String currentTime = new Date(System.currentTimeMillis()).toString();
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.write(resp);
        
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();//刷新后才将数据发出到SocketChannel
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}


#################################  EchoOutHandler1   ######################################################################

package cn.itcast_03_netty.sendorder.server;
 
import java.util.Date;
 
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
 
public class EchoOutHandler1 extends ChannelOutboundHandlerAdapter {
@Override
    // 向client发送消息
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("out1");
        /*System.out.println(msg);*/
 
        String currentTime = new Date(System.currentTimeMillis()).toString();
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.write(resp);
        ctx.flush();
       }
}

################################  EchoOutHandler2     #######################################################################
package cn.itcast_03_netty.sendorder.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class EchoOutHandler2 extends ChannelOutboundHandlerAdapter {

	 @Override
	    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
	        System.out.println("out2");
	        // 执行下一个OutboundHandler
	        /*System.out.println("at first..msg = "+msg);
	        msg = "hi newed in out2";*/
	        super.write(ctx, msg, promise);
	    }
}

```


## 2.3.客户端启动类
```
package cn.itcast_03_netty.sendorder.client;
 
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
 
import java.net.InetSocketAddress;
 
import cn.itcast_03_netty.sendobject.coder.PersonEncoder;
 
/**
* • 连接服务器 • 写数据到服务器 • 等待接受服务器返回相同的数据 • 关闭连接
*
* @author wilson
*
*/
public class EchoClient {
 
private final String host;
private final int port;
 
public EchoClient(String host, int port) {
  this.host = host;
  this.port = port;
}
 
public void start() throws Exception {
  EventLoopGroup nioEventLoopGroup = null;
  try {
   // 客户端引导类
   Bootstrap bootstrap = new Bootstrap();
   // EventLoopGroup可以理解为是一个线程池，这个线程池用来处理连接、接受数据、发送数据
   nioEventLoopGroup = new NioEventLoopGroup();
   bootstrap.group(nioEventLoopGroup)//多线程处理
     .channel(NioSocketChannel.class)//指定通道类型为NioServerSocketChannel，一种异步模式，OIO阻塞模式为OioServerSocketChannel
     .remoteAddress(new InetSocketAddress(host, port))//地址
     .handler(new ChannelInitializer<SocketChannel>() {//业务处理类
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
         ch.pipeline().addLast(new EchoClientHandler());//注册handler
        }
       });
   // 链接服务器
   ChannelFuture channelFuture = bootstrap.connect().sync();
   channelFuture.channel().closeFuture().sync();
  } finally {
   nioEventLoopGroup.shutdownGracefully().sync();
  }
}
 
public static void main(String[] args) throws Exception {
  new EchoClient("localhost", 20000).start();
}
}
```
## 2.4.客户端回调方法（处理器）
```
package cn.itcast_03_netty.sendorder.client;
 
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import cn.itcast_03_netty.sendobject.bean.Person;
 
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
// 客户端连接服务器后被调用
@Override
public void channelActive(ChannelHandlerContext ctx) throws Exception {
  System.out.println("客户端连接服务器，开始发送数据……");
  byte[] req = "QUERY TIME ORDER".getBytes();//消息
  ByteBuf firstMessage = Unpooled.buffer(req.length);//发送类
  firstMessage.writeBytes(req);//发送
  ctx.writeAndFlush(firstMessage);//flush
}
 
// • 从服务器接收到数据后调用
@Override
protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
  System.out.println("client 读取server数据..");
  // 服务端返回消息后
  ByteBuf buf = (ByteBuf) msg;
  byte[] req = new byte[buf.readableBytes()];
  buf.readBytes(req);
  String body = new String(req, "UTF-8");
  System.out.println("服务端数据为 :" + body);
}
 
// • 发生异常时被调用
@Override
public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
  System.out.println("client exceptionCaught..");
  // 释放资源
  ctx.close();
}
}

```


# 3.总结
在使用Handler的过程中，需要注意：
1、ChannelInboundHandler之间的传递，通过调用 ctx.fireChannelRead(msg) 实现；调用ctx.write(msg) 将传递到ChannelOutboundHandler。
2、ctx.write()方法执行后，需要调用flush()方法才能令它立即执行。
3、流水线pipeline中outhandler不能放在最后，否则不生效
4、Handler的消费处理放在最后一个处理。