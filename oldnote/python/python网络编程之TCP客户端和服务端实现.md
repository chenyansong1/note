---
title:  python网络编程之TCP客户端和服务端实现
categories: python   
toc: true  
tags: [python]
---



# 1.socket模块

* 创建socket： int socket(int domain, int type int protocol)
* domain：协议域，决定了socket的地址类型，在通信中必须采用对应的地址
 AF_INET:ipv4协议，ipv4地址与端口号组合(AF:address family)
 AF_LOCAL：使用一个绝对路径名作为地址（进程间通信）
 AF_PACKET：来处理以太网包，他能修改以太网包头
* type：socket类型：
 SOCK_STREAM：TCP套接字
 SOCK_DGRAM：UDP套接字
 SOCK_RAW：原始套接字
* protocol：指定协议，常用协议有IPPROTO_TCP/IPPROTO_UDP、分别对应tcp传输协议、udp传输协议
* type和protocol不能随意组合，第三个参数不设置的时候，默认为何第二个参数对应的协议一致


# 2.socket中常用方法

|命令|说明|
|-|-|
|ss = socket(family,type[,proto]) |创建一个套接字，返回一个socket对象|
|ss.binds((ip, port)) |绑定ip地址和端口                              |
|ss.listen(n) |最大接受连接数                                        |
|ss.accept() |接受TCP客户端连接，返回一个连接和客户单信息的元组     |
|ss.connect((ip,port)) |主动初始化TCP连接服务器                       |
|ss.recv(buffers[,flags]) |返回接受数据（tcp）                       |
|ss.send(data[,flags]) |发送数据（tcp）                               |
|recvfrom(buffersize[,flags]) |返回接受数据和发送端的地址信息（udp)  |
|sendto(data[,flags],address) |向指定的ip和端口发送数据（udp）       |
|ss.close() |关闭套接字|
 
 
 
 

# 3.Tcp客户端和服务器端模型

![](http://ols7leonh.bkt.clouddn.com//assert/img/python/net_tcp/1.png)


# 4.TCP客户端和服务端实现
* tcp服务端编写：监听一个端口，建立请求后接受数据并回复bye,然后关闭连接，继续等待其他客户端的连接
* 最简单的服务端程序：

```
from socket import *
 
tcp_server = socket(AF_INET, SOCK_STREAM) #创建socket
tcp_server.bind(("", 5001)) #绑定本地IP与端口
tcp_server.listen(5) #设置监听的最大连接数

client, client_info = tcp_server.accept() #等待连接
recv_data = client.recv(1024) #接收数据

client.close()
tcp_server.close() #关闭连接

```
* 最简单的客户端程序：
```
from socket import *
tcp_client = socket(AF_INET, SOCK_STREAM) #创建socket
 
tcp_client.connect(('localhost',5001)) #连接服务器
tcp_client.send("hello python")  #发送数据
tcp_client.close()  #关闭连接
```

## 4.1.最简单的实现
```
#客户端代码
#!/usr/bin/python
 
from socket import *
import time
 
tcp_server = socket(AF_INET, SOCK_STREAM)                    #建立一个tcp连接
tcp_server.bind(("localhost",5001))                                #绑定一个端口，注意是元组
tcp_server.listen(3)
 
print("wait client connect.........")
client,client_info = tcp_server.accept()                            #服务端等待接受（阻塞再次）
recv_data = client.recv(1024)            
 
print(recv_data)
 
client.close()
tcp_server.close()

-----------------------------------------------------------

#服务端代码
#!/usr/bin/python
 
from socket import *
import time
 
tcp_client = socket(AF_INET, SOCK_STREAM)
tcp_client.connect(("localhost",5001))                    #指定连接的地址和端口
 
tcp_client.send("hello chenyansong")                     #发送数据
 
tcp_client.close()

```

## 4.2.客户端输入、服务端连续读取
```
#服务端
#!/usr/bin/python
 
from socket import *
import time
 
tcp_server = socket(AF_INET, SOCK_STREAM)
tcp_server.bind(("localhost",5001))
tcp_server.listen(1)
 
while True:
        print("wait client connect.........")
 
        client,client_info = tcp_server.accept()                    #读取客户端的一个连接
        print(client_info)                                    #打印客户端连接信息的元组：('127.0.0.1', 35890)
 
        while True:
                recv_data = client.recv(1024)                        #获取一个连接数据
                print("recv=" + recv_data)
                if recv_data == 'q':
                        client.close()                            #关闭一个连接
                        break
 
tcp_server.close()


---------------------------------------------------------

#客户端
#!/usr/bin/python
 
from socket import *
import time
 
tcp_client = socket(AF_INET, SOCK_STREAM)
tcp_client.connect(("localhost",5001))
 
while True:
        msg = raw_input(">")                        #输入内容
        tcp_client.send(msg)
        if msg == 'q':                                 #退出
                break
 
tcp_client.close()

```

## 4.3.客户端抛出异常，服务端的处理方式
如果客户端程序异常终止，然后就会想服务器端发送“”（空），所以在服务器端要进行判断
```
#服务端
#!/usr/bin/python
 
from socket import *
import time
 
tcp_server = socket(AF_INET, SOCK_STREAM)
tcp_server.bind(("localhost",5001))
tcp_server.listen(3)
 
while True:
        print("wait client connect.........")
 
        client,client_info = tcp_server.accept()
        print(client_info)
 
        while True:
                recv_data = client.recv(1024)
                if recv_data == '':                                #添加判断，服务端接受的数据，是否是空，如果是，则关闭连接的客户端
                        client.close()
                        break
 
                print("recv=" + recv_data)
                if recv_data == 'q':
                        client.close()
                        break
 
tcp_server.close()


----------------------------------------------------------------
 
#客户端，同上

```

## 4.4.客户端会发送空数据
如果发送的数据是空，那么客户端将不会将数据发送，因为在客户端抛出异常的时候，会请求断开链接，那么此时客户端会发送空数据（此时会发送），所以为了防止误判，客户端正常的发送空数据，将不会发送的
```
#客户端
#!/usr/bin/python
 
from socket import *
import time
 
tcp_client = socket(AF_INET, SOCK_STREAM)
tcp_client.connect(("localhost",5001))
 
while True:
        msg = raw_input(">")
        if msg == 'o':
                tcp_client.send('')                            #将不会发送
        else:
                tcp_client.send(msg)
 
        if msg == 'q':
                break
 
tcp_client.close()


```
## 4.5.服务端发送数据到客户端
```
#服务端
#!/usr/bin/python
 
from socket import *
import time
 
tcp_server = socket(AF_INET, SOCK_STREAM)
tcp_server.bind(("localhost",5001))
tcp_server.listen(3)
 
while True:
        print("wait client connect.........")
 
        client,client_info = tcp_server.accept()
        print(client_info)
 
        while True:
                recv_data = client.recv(1024)
                if recv_data == '':
                        client.close()
                        break
 
                print("recv=" + recv_data)
 
                client.send("server send :"+recv_data)                    #向客户端发送数据
 
                if recv_data == 'q':
                        client.close()
                        break
 
tcp_server.close()



-----------------------------------------------------------------------


#客户端
#!/usr/bin/python
 
from socket import *
import time
 
tcp_client = socket(AF_INET, SOCK_STREAM)
tcp_client.connect(("localhost",5001))
 
while True:
        msg = raw_input(">")
        if msg == 'o':
                tcp_client.send('')
        else:
                tcp_client.send(msg)
 
        if msg == 'q':
                break
 
        server_data = tcp_client.recv(1024)                        #接收来自服务端的数据
        print(server_data)
 
tcp_client.close()


```

 
## 4.6.服务端断开，客户端的处理情况

当服务端断开与客户端的连接的时候（客户端输入Q），服务端会发送一个空数据，然后客户端检测，断开链接
```
#服务端
#!/usr/bin/python
 
from socket import *
import time
 
tcp_server = socket(AF_INET, SOCK_STREAM)
tcp_server.bind(("localhost",5001))
tcp_server.listen(3)
 
while True:
        print("wait client connect.........")
 
        client,client_info = tcp_server.accept()
        print(client_info)
 
        while True:
                recv_data = client.recv(1024)
                if recv_data == '':
                        client.close()
                        break
 
                print("recv=" + recv_data)
 
                client.send("server send :"+recv_data)
 
                if recv_data == 'q' or recv_data =='Q':                #当接收到客户端的数据为“Q"的时候，服务端断开与客户端的连接
                        client.close()
                        break
 
tcp_server.close()


---------------------------------------------------------------------------


#客户端1
#!/usr/bin/python
 
from socket import *
import time
 
tcp_client = socket(AF_INET, SOCK_STREAM)
tcp_client.connect(("localhost",5001))
 
while True:
        msg = raw_input(">")
        if msg == 'o':
                tcp_client.send('')
        else:
                tcp_client.send(msg)
 
        if msg == 'q':
                break
 
        server_data = tcp_client.recv(1024)
        print(server_data)
 
        if server_data == '':                                    #当接收到服务端的空数据的时候，就说明服务端断开了连接，所以此时break
                print("socket close.....")
                break
 
tcp_client.close()


----------------------------------------------------------------------------
 
#客户端2： 当客户端没有接受数据时，使用异常判断来解决
#!/usr/bin/python
 
from socket import *
import time
 
tcp_client = socket(AF_INET, SOCK_STREAM)
tcp_client.connect(("localhost",5001))
 
while True:
        msg = raw_input(">")
        try:
                if msg == 'o':
                        tcp_client.send('')
                else:
                        tcp_client.send(msg)
 
                if msg == 'q':
                        break
        except error, emsg:                                #使用异常检测的方式去处理服务端断开的连接
                print(emsg)
                break
 
tcp_client.close()

```
 



