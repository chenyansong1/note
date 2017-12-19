
---
title: socket实现远程对象方法调用
categories: java   
tags: [java,rpc]
---




# 1.调用过程图示

![](http://ols7leonh.bkt.clouddn.com//assert/img/java/socket/1.png)

# 2.代码实现

服务端

```
package cn.itcast.bigdata.socket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
public class ServiceServer {
    public static void main(String[] args) throws Exception {
        // 创建一个serversocket，绑定到本机的8899端口上
        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress("localhost", 8899));
        // 接受客户端的连接请求;accept是一个阻塞方法，会一直等待，到有客户端请求连接才返回
        while (true) {
            Socket socket = server.accept();
            //因为服务端要处理多个客户端的socket请求，所以这里使用的是：来一个socket请求，然后将new一个线程去处理该socket
            new Thread(new ServiceServerTask(socket)).start();
        }
    }
}

```
线程类

```java
package cn.itcast.bigdata.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.Socket;

public class ServiceServerTask implements Runnable{
	Socket socket ;
	InputStream in=null;
	OutputStream out = null;
	
	public ServiceServerTask(Socket socket) {
		this.socket = socket;
	}

	//业务逻辑：跟客户端进行数据交互
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		 try {
			//从socket连接中获取到与client之间的网络通信输入输出流 
			in = socket.getInputStream();
			out = socket.getOutputStream();
			
			
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			//从网络通信输入流中读取客户端发送过来的数据
			//注意：socketinputstream的读数据的方法都是阻塞的 
			String params = br.readLine();
			
			String className = "";//class全路径名称
			String methodName = "";//反射要调用的方法名
			String data = "";//传递到方法的参数
			
			//将socket传过来的参数分解（cn.itcast.bigdata.socket.GetDataServiceImpl:getData:chenyansong）
			if(params!=null&&!params.equals("")&&params.length()>=3){
				String[] paramArr = params.split(":");
				className = paramArr[0];
				methodName = paramArr[1];
				data = paramArr[2];
			}
			
			Class cl = Class.forName(className);
			Method method = cl.getMethod(methodName, String.class);
			String result = (String) method.invoke(cl.newInstance(), data);//反射调用传递过来的方法
			
			//将调用结果写到sokect的输出流中，以发送给客户端
			PrintWriter pw = new PrintWriter(out);
			pw.println(result);
			pw.flush();
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				in.close();
				out.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}


```



客户端

```
package cn.itcast.bigdata.socket;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ServiceClient {

	public static void main(String[] args) throws Exception {
		
		// 向服务器发出请求建立连接
		Socket socket = new Socket("localhost", 8899);
		// 从socket中获取输入输出流
		InputStream inputStream = socket.getInputStream();
		OutputStream outputStream = socket.getOutputStream();

		PrintWriter pw = new PrintWriter(outputStream);
		pw.println("cn.itcast.bigdata.socket.GetDataServiceImpl:getData:chenyansong");   //传递到对端的数据，server端会对参数进行split
		pw.flush();

		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String result = br.readLine();
		System.out.println(result);
		
		inputStream.close();
		outputStream.close();
		socket.close();
	}
}

```


反射实现类
```
//注意：反射的类要有空的构造函数，如果没写构造函数，默认是有一个空的构造函数的
package cn.itcast.bigdata.socket;
public class GetDataServiceImpl implements GetDataService{
    
    public String getData(String param){
        
        return "ok-"+param;
    }
}

```

接口：GetDataService
```
package cn.itcast.bigdata.socket;
public interface GetDataService {
    
}
```















