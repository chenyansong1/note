---
title:  python网络编程之UDP客户端和服务端实现
categories: python   
toc: true  
tags: [python]
---



# 1.UDP服务端和客户端模型：

![](http://ols7leonh.bkt.clouddn.com//assert/img/python/net_udp/1.png)
 

# 2.UDP服务器
```
from socket import *
udp_server = socket(AF_INET, SOCK_DGRAW) #创建udp socket
 
udp_server.bind(('localhost', 5002)) #绑定本地IP与端口
 
recv_data, addr = udp_server.recvfrom(1024) #接收数据和client地址信息
udp_server.close()

```
 

# 3.UDP客户端
```
from socket import *
udp_client = socket(AF_INET, SOCK_DGRAW) #创建udp socket
 
udp_client.sendto("hello python", ('localhost', 5002)) #向指定的地址发送数据
 
udp_client.close() #关闭连接
```
 
 
# 4.实例
```
#服务端
#!/usr/bin/python
 
from socket import *
import time
 
udp_server = socket(AF_INET, SOCK_DGRAM)
udp_server.bind(("localhost",5002))
 
while True:
        recv_data,addr = udp_server.recvfrom(1024)
        print(recv_data,addr)
 
        if recv_data == 'q':
                break
        udp_server.sendto("server send data:"+recv_data, addr)
 
udp_server.close()


-----------------------------------------------------------------

#客户端
#!/usr/bin/python
 
from socket import *
import time
 
udp_client = socket(AF_INET, SOCK_DGRAM)
 
while True:
        msg = raw_input(">")
        udp_client.sendto(msg, ('localhost',5002))
        if msg == 'q':
                break
        server_data,addr = udp_client.recvfrom(1024)
        print(server_data,addr)
 
udp_client.close()

```
 
# 5.UDP传输的问题
1. 丢包问题
2. 包到达顺序的问题
3. 不能知道对方是否关闭的问题

# 6.udp客户端中的connect()
如果在执行的过程中服务端关闭，然后客户端还在发送数据，但是客户端并不知道服务端关闭了，他还是会不停的发送数据，只不过此时的发送的数据将全部丢包而已。通过connect知道数据是否发送成功 
```
#!/usr/bin/python
 
from socket import *
import time
 
udp_client = socket(AF_INET, SOCK_DGRAM)
udp_client.connect(('localhost',5002))                                    #在客户端中使用connect
 
while True:
        msg = raw_input(">")
        udp_client.sendto(msg, ('localhost',5002))
        if msg == 'q':
                break
        server_data,addr = udp_client.recvfrom(1024)
        print(server_data,addr)
 
 
udp_client.close()


#如果服务端关闭，客户端会抛出异常
Traceback (most recent call last):
  File "udp_client.py", line 14, in <module>
    server_data,addr = udp_client.recvfrom(1024)
socket.error: [Errno 111] Connection refused

```
# 7.通过tcpdump查看发送记录

 
![](http://ols7leonh.bkt.clouddn.com//assert/img/python/net_udp/2.png)



