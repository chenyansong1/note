
---
title: netty的第一个helloworld
categories: socket   
tags: [java,netty]
---


# 服务端线程模型

一种比较流行的做法是服务端监听线程和IO线程分离，类似于Reactor的多线程模型，它的工作原理图如下：
![](http://ols7leonh.bkt.clouddn.com//assert/img/java/netty/server_1.png)

另外一种描述:


![](http://ols7leonh.bkt.clouddn.com//assert/img/java/netty/server_2.png)

bossGroup线程组实际就是Acceptor线程池，负责处理客户端的TCP连接请求，如果系统只有一个服务端端口需要监听，则建议bossGroup线程组线程数设置为1。
 
workerGroup是真正负责I/O读写操作的线程组，通过ServerBootstrap的group方法进行设置，用于后续的Channel绑定。



# 服务端启动类
```
package cn.itcast_03_netty.sendstring.server;

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
			//装配bootstrap
			serverBootstrap.group(eventLoopGroup)
			.channel(NioServerSocketChannel.class)//指定通道类型为NioServerSocketChannel，一种异步模式，OIO阻塞模式为OioServerSocketChannel
			.localAddress("localhost",port)//设置InetSocketAddress让服务器监听某个端口已等待客户端连接。
			.childHandler(new ChannelInitializer<Channel>() {//设置childHandler执行所有的连接请求
						@Override
						protected void initChannel(Channel ch) throws Exception {
							ch.pipeline().addLast(new EchoServerHandler());//注册handler
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

# 服务端回调方法（处理器）
```
package cn.itcast_03_netty.sendstring.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

import cn.itcast_03_netty.sendobject.bean.Person;

//继承了ChannelInboundHandlerAdapter
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("server 读取数据……");
		//读取数据
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
		System.out.println("server 读取数据完毕..");
        ctx.flush();//刷新后才将数据发出到SocketChannel
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)	throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}


```

# 客户端现场模型

![](http://ols7leonh.bkt.clouddn.com//assert/img/java/netty/client_1.png)


另外一种描述:


![](http://ols7leonh.bkt.clouddn.com//assert/img/java/netty/client_2.png)

# 客户端启动类
```
package cn.itcast_03_netty.sendstring.client;

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

# 客户端回调方法（处理器）
```
package cn.itcast_03_netty.sendstring.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import cn.itcast_03_netty.sendobject.bean.Person;

//继承了SimpleChannelInboundHandler
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
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)	throws Exception {
		System.out.println("client exceptionCaught..");
		// 释放资源
		ctx.close();
	}
}

```


参考:
[Netty系列之Netty线程模型](http://www.infoq.com/cn/articles/netty-threading-model)
[Netty工作原理架构图](http://www.jianshu.com/p/03bb8a945b37)


 












