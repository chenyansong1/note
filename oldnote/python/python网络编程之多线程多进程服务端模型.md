---
title:  python网络编程之多线程多进程服务端模型
categories: python   
toc: true  
tags: [python]
---



# 1.基本服务器模型

![](http://ols7leonh.bkt.clouddn.com//assert/img/python/process_thread/1.png)


  1. 只能同时支持一路
  2. 因为创建的socket文件描述符是阻塞的，所以如果该socket一直没有消息，那么我们程序一直处于等待中

# 2.多进程服务器模型

![](http://ols7leonh.bkt.clouddn.com//assert/img/python/process_thread/2.png)

* 多进程模型对系统的开销比较大
* 该模型需要注意：
    1. 子进程的回收
    2. 子进程的创建数量
 

# 3.多进程服务器模型程序实现
```
#!/usr/bin/python
 
import time
import os
import sys
from socket import *
 
def client_deal(client,client_info):                                #
        while True:
                msg = client.recv(1024)
                if msg == '':
                        client.close()
                        break
                print("recv="+msg+" from :",client_info)
                if msg == 'q' or msg == 'Q':
                        client.close()
                        break
 
 
def client_process(client,client_info):
        pid = os.fork()
        if pid == 0:                                    #子进程
                ppid = os.fork()                    #子进程中又开启了一个子进程
                if ppid == 0:
                        client_deal(client,client_info)    #用该进程去处理对应的客户端请求
                else:
                        print("child exit")
                        sys.exit()
        else:
                client.close()
                os.wait()
                print("wait child exit")
 
 
if __name__ == '__main__':
        tcp_server = socket(AF_INET, SOCK_STREAM)
        tcp_server.bind(('localhost',5001))
        tcp_server.listen(5)
 
        while True:
                print("wait client......")
                client,client_info = tcp_server.accept()
                print(client_info)
                client_process(client,client_info)
        tcp_server.close()
```
 
 

--------------------------------------------------------------

# 4.多线程服务器模型
1. 接收到一个请求，创建一个相应的线程
2. 如果该请求结束，关闭该请求，退出执行线程

![](http://ols7leonh.bkt.clouddn.com//assert/img/python/process_thread/3.png)
 
# 5.多线程服务器模型程序实现
```
#!/usr/bin/python
 
 
from socket import *
import time
import os
import sys
import threading
 
 
 
 
def client_deal(client,client_info):
        while True:
                msg = client.recv(1024)
                if msg == '':
                        client.close()
                        break
                print("recv="+msg+" from :",client_info)
                if msg == 'q' or msg == 'Q':
                        client.close()
                        break
 
 
 
def thread_process(client,client_info):
        p_thread = threading.Thread(target=client_deal,args=(client,client_info))
        p_thread.setDaemon(True)
        p_thread.start()
        print("start on thread")
 
 
 
if __name__ == "__main__":
        tcp_server = socket(AF_INET,SOCK_STREAM)
        tcp_server.bind(("localhost",5001))
        tcp_server.listen(5)
        while True:
                print("pid="+str(os.getpid())+" wait client")
                client,client_info = tcp_server.accept()                    #每接受到一个客户端的时候，就去开启一个线程
                print(client_info)
                thread_process(client,client_info)                #开启一个线程
 
        tcp_server.close()

```




