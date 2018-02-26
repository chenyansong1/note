
---
title: NIO代码示例
categories: socket   
tags: [socket,NIO]
---


# 1.阻塞式IO和非阻塞式IO的区别
Block IO会对每个连接创建一个线程，因此这极大限制了JVM创建线程的数量（当然线程池可缓解这个问题，但是也仅仅是缓解），如图所示

![](http://ols7leonh.bkt.clouddn.com//assert/img/java/socket/NIO_1.png)


NIO会通过专门的Selector来管理请求，然后可由一个线程来处理请求，如图所示：

![](http://ols7leonh.bkt.clouddn.com//assert/img/java/socket/NIO_2.png)

说明:

* 客户端和服务端通信是通过channel进行的(即:上图中的每一个read/write就相当于一个channel)
* channel需要注册到selector中进行统一管理
* selector中保存的是Map<key,channel>,通过key就可以取出对应的channel
* 循环取出key,得到channel,然后判断该channel上的是accept,or read or write操作(详见下面的:服务端代码)


![](http://ols7leonh.bkt.clouddn.com//assert/img/java/socket/selector_channel.png)



# 2.NIO服务端时序列图

![](http://ols7leonh.bkt.clouddn.com//assert/img/java/socket/NIO_3.png)





## 2.1.步骤说明
步骤1：打开ServerSocketChannel，用于监听客户端的连接，它是所有客户端连接的父通道，代码示例如下：

```
ServerSocketChannel acceptorSvr = ServerSocketChannel.open();
```
 
绑定监听端口，设置客户端连接方式为非阻塞模式，示例代码如下

```
acceptorSvr.socket().bind(new InetSocketAddress(InetAddress.getByName(“IP”), port));
acceptorSvr.configureBlocking(false);

```

步骤3：创建Reactor线程，打开多路复用器并启动服务端监听线程，通常情况下，可以采用线程池的方式创建Reactor线程。示例代码如下：

```
Selector selector = Selector.open();
New Thread(new ReactorTask()).start();
```
步骤4：将ServerSocketChannel注册到Reactor线程的多路复用器Selector上，监听ACCEPT状态位，示例代码如下：

```
SelectionKey key = acceptorSvr.register( selector, SelectionKey.OP_ACCEPT, ioHandler);
```
步骤5：多路复用器在线程run方法的无限循环体内轮询准备就绪的Key，通常情况下需要设置一个退出状态检测位，用于优雅停机。代码如下：

```
int num = selector.select();
Set selectedKeys = selector.selectedKeys();
Iterator it = selectedKeys.iterator();
while (it.hasNext()) {
     SelectionKey key = (SelectionKey)it.next();
     // ... deal with I/O event ...
}
```
步骤6：多路复用器监听到有新的客户端接入，处理新的接入请求，完成TCP三次握手后，与客户端建立物理链路，示例代码如下：

```
channel.configureBlocking(false);
channel.socket().setReuseAddress(true);
```
步骤8：将新接入的客户端连接注册到Reactor线程的多路复用器上，监听读操作位，用来读取客户端发送的网络消息，示例代码如下：

```
SelectionKey key = socketChannel.register( selector, SelectionKey.OP_READ, ioHandler);
```
步骤9：异步读取客户端请求消息到服务端缓冲区，示例代码如下：

```
int  readNumber =  channel.read(receivedBuffer);
```
步骤10：对ByteBuffer进行解码，如果有半包消息指针Reset，继续读取后续的报文，将解码成功的消息封装成Task，投递到业务线程池中，进行业务逻辑编排，示例代码如下：

```
Object message = null;
while(buffer.hasRemain())
{
       byteBuffer.mark();
       Object message = decode(byteBuffer);
       if (message == null)
       {
          byteBuffer.reset();
          break;
       }
       messageList.add(message );
}
if (!byteBuffer.hasRemain())
byteBuffer.clear();
else
    byteBuffer.compact();
if (messageList != null & !messageList.isEmpty())
{
for(Object messageE : messageList)
   handlerTask(messageE);
}
```
步骤11：将POJO对象encode成ByteBuffer，调用SocketChannel的异步write接口，将消息异步发送给客户端，
示例代码如下：如果发送区TCP缓冲区满，会导致写半包，此时，需要注册监听写操作位，循环写，直到整包消息写入TCP缓冲区。

```
socketChannel.write(buffer);
```

# 3.服务端代码

```
package cn.itcast.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
	private int flag = 1;
	private int blockSize = 4096;
	private ByteBuffer sendbuffer = ByteBuffer.allocate(blockSize);
	private ByteBuffer receivebuffer = ByteBuffer.allocate(blockSize);
	Selector selector;
	

	public NIOServer(int port) throws IOException {
		//开启一个channel
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		//设置为非阻塞的
		serverSocketChannel.configureBlocking(false);
		
		ServerSocket serverSocket = serverSocketChannel.socket();
		//绑定IP和端口
		serverSocket.bind(new InetSocketAddress(port));
		
		//打开选择器
		selector = Selector.open();
		
		//注册channel到selector中（并指定事件类型）
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		System.out.println("Server start ->"+port);
		
	}

	//监听
	public void listen() throws IOException{
		
		while(true){
			int n = selector.select();//会将失效的SelectionKey清除掉，如果没有通道已经准备好，线程可能会在这时阻塞，通常会有一个超时值
			if(n==0){
				continue;
			}
			Set<SelectionKey> selectedKeysSet = selector.selectedKeys();
			
			Iterator<SelectionKey> iterator = selectedKeysSet.iterator();
			while(iterator.hasNext()){
				SelectionKey selectionKey = iterator.next();
				//// Remove key from selected set; it's been handled
				iterator.remove();
				
				//业务逻辑
				handleKey(selectionKey);
			}
		}
		
	}
	
	//处理业务逻辑
	public void handleKey(SelectionKey selectionKey) throws IOException{
		
		ServerSocketChannel server = null;
		SocketChannel client = null;
		String reciveText;
		String sendText;
		int count = 0;
		
		if(selectionKey.isAcceptable()){//可接收的channel
			//从select中去取对应key的信道（channel）
			server = (ServerSocketChannel) selectionKey.channel();
			//接收客户端连接
			client = server.accept();
			//阻塞为false
			client.configureBlocking(false);
			//注册读事件
			client.register(selector, SelectionKey.OP_READ);
			
		}else if(selectionKey.isReadable()){//读事件
			//获取和客户端的channel连接
			client = (SocketChannel) selectionKey.channel();
			count = client.read(receivebuffer);//读到缓冲区中
			
			if(count>0){
				//接收到客户端的数据
				reciveText = new String(receivebuffer.array(),0,count);
				System.out.println("服务端接收到客户端的数据");
				
				//注册服务端的写事件
				client.register(selector, SelectionKey.OP_WRITE);
			}
			
		}else if(selectionKey.isWritable()){//写事件
			//清空写的缓冲区
			sendbuffer.clear();
			
			//获取和客户端的channel连接
			client = (SocketChannel) selectionKey.channel();
			
			sendText = "msg send to client"+flag++;
			
			//将数据放入到缓冲区中
			sendbuffer.put(sendText.getBytes());
			
			//将buff翻转，这样在读的时候，会从起始的地方读取
			sendbuffer.flip();
			
			//发送数据到客户端
			client.write(sendbuffer);
			
			System.out.println("服务器端发送数据给客户端："+sendText);
		}
	}
	
	public static void main(String[] args) throws IOException {
		int port = 7080;
		NIOServer server = new NIOServer(port);
		//监听
		server.listen();	
	}
}

```



# 4.客户端代码

```

package cn.itcast.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOClient {
	
	private static int flag = 1;
	private static int blockSize = 4096;
	private static ByteBuffer sendbuffer = ByteBuffer.allocate(blockSize);
	private static ByteBuffer receivebuffer = ByteBuffer.allocate(blockSize);

	private final static InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1", 7080);
	/**
	 * @throws IOException 
	
	 */
	public static void main(String[] args) throws IOException {

		SocketChannel socketChannel = SocketChannel.open();
		//设置为非阻塞
		socketChannel.configureBlocking(false);
		
		//打开选择器
		Selector selector = Selector.open();
		
		//向selector注册连接事件
		socketChannel.register(selector, SelectionKey.OP_CONNECT);
		
		//连接
		socketChannel.connect(serverAddress);
		
		Set<SelectionKey> selectionKeySet;
		Iterator<SelectionKey> iterator;
		SelectionKey selectKey;
		SocketChannel client;
		String receiveText;
		String sendText;
		int count = 0;
		
		while(true){
			selector.select();//这是一个阻塞的方法，当有至少一个channel被选择或者线程被中断的时候，会阻塞结束
			/* It returns only after at least one channel is selected, this selector's wakeup method is invoked, 
			or the current thread is interrupted, whichever comes first. 
			*/
			selectionKeySet = selector.selectedKeys();
			iterator = selectionKeySet.iterator();
			while(iterator.hasNext()){
				selectKey = iterator.next();
				if(selectKey.isConnectable()){//连接事件
					System.out.println("client connet...");
					client = (SocketChannel) selectKey.channel();;
					if(client.isConnectionPending()){//如果是正在连接就完成连接
						client.finishConnect();//完成连接
						System.out.println("客户端完成连接操作....");
						
						//先清空buff
						sendbuffer.clear();
						//向buff中添加数据
						sendbuffer.put("Hello Server".getBytes());
						sendbuffer.flip();//将缓冲区翻转，为读该缓冲区做准备
						client.write(sendbuffer);
					}
					//注册读事件
					client.register(selector, SelectionKey.OP_READ);
					
				}else if(selectKey.isReadable()){//读server端数据
					client = (SocketChannel) selectKey.channel();;
					receivebuffer.clear();
					count = client.read(receivebuffer);
					
					if(count>0){//服务端有发送数据过来
						receiveText = new String(receivebuffer.array(),0,count);
						System.out.println("客户端接收到服务端的数据"+receiveText);
						
						//注册写事件
						client.register(selector, selectKey.OP_WRITE);
					}
				}else if(selectKey.isWritable()){//写事件
					sendbuffer.clear();
					client = (SocketChannel) selectKey.channel();
					sendText = "Msg send to Server->"+flag++;
					sendbuffer.put(sendText.getBytes());
					sendbuffer.flip();
					
					client.write(sendbuffer);
					
					System.out.println("客户端发送数据给服务端："+sendText);
					
					//注册读事件
					client.register(selector, SelectionKey.OP_READ);
				}
			}
			
			//清除
			selectionKeySet.clear();
		}
	}
}

```

另外一种简单的代码如下：

```
/Users/chenyansong/Documents/note/oldnote/java/socket/NIO代码_other_version

```
 
