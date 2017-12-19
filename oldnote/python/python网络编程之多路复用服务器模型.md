---
title:  python网络编程之多路复用服务器模型
categories: python   
toc: true  
tags: [python]
---



# 1.select 模块的作用
&emsp;select系统调用用来检测多个文件描述符状态变化，程序会一直在select中等待直到超时或者被监视文件描述符中的一个或者多个状态发生变化

# 2.select函数
```
select(rlist, wlist, xlist[,timeout])                      #返回值：（rlist, wlist, xlist）
#rlist：读取socket列表，判断是否有可以读的socket
#wlist：写入socket列表，判断是否有可以写的socket
#xlist：异常socket列表，判断是否有异常的socket
#如果socket可以读，可以写，或者异常，select返回相应的socket列表

```
# 3.select工作模型

![](http://ols7leonh.bkt.clouddn.com//assert/img/python/select/1.png)
 

# 4.select如何判断可读、可写、异常
* select如何判断可读
 1. 将监测的socket加入到rlist中，然后调用select等待数据
 2. 如果有客户端连接或者对方数据，那么select就会立刻返回
 3. 如果是新的连接调用accept接受新的socket，并将该socket计入到rlist或者wlist中
 4. 如果有数据，那么就接受数据
* select如何判断可以写
 1. 将检测到的socket加入到wlist，调用select等待
 2. 如果socket可以写，返回可以写的socket列表
 3. 调用send方法，发送数据

* select如何判断异常
 1. 将监测到的socket加入到xlist，调用select等待
 2. 如果socket有异常，返回异常的xlist列表
 3. 处理异常的socket


# 5.select编程模型

![](http://ols7leonh.bkt.clouddn.com//assert/img/python/select/2.png)

* read操作：需要将可读的socket全部读出
* write操作：将可以写的socket根据自己需要发送相关消息和数据
 1. socket一般都是可写的，所以要根据自己需求决定是否每次写完之后是否将wlist中的socket移除
 2. 因为网络问题，socket并不是一致处于可写状态，如果使用UDP，不去判断socket是否可写，可能会写失败

 
 
# 6.程序实现
```
#服务端
[root@backup python]# cat select.py
#!/usr/bin/python
 
from socket import *
import time
import os
import sys
import threading
import select
import Queue
 
 
if __name__ == '__main__':
        tcp_server = socket(AF_INET, SOCK_STREAM)
        tcp_server.bind(('localhost',5002))
        tcp_server.listen(5)
 
        msg_list = {} #client queue
        in_list = [] #socket readline
        out_list = [] #socket writelist
        in_list.append(tcp_server)
 
        while True:
                ilist,olist,elist = select.select(in_list,out_list,in_list,1)
 
                if not (ilist or olist or elist):
                        print("Wait Timeout : in_list = "+len(in_list))
                        continue
 
                for c in ilist:                            #读处理
                        if c == tcp_server:
                                client,client_info = c.accept()
                                in_list.append(client)
                                msg_list[client]=Queue.Queue()
                        else:
                                recv_data = c.recv(1024)
                                if recv_data:
                                        print("recv= "+recv_data)
                                        msg_list[c].put(recv_data)                #将数据放入消息队列中
                                        if c not in out_list:
                                                out_list.append(c)
                                else:                                        #如果接受的数据为空的话（可能client异常了），那么从ilist、outlist中删除
                                        if c in out_list:
                                                out_list.remove(c)
                                        in_list.remove(c)
                                        c.close()
                                        del msg_list[c]
                                        print("a client exit")
 
                for c in elist:                        #异常处理
                        if c in in_list:
                                in_list.remove(c)
                        if c in out_list:
                                out_list.remove(c)
                        c.close()
                        del msg_list[c]
 
                for c in olist:                        #写处理
                        if c in msg_list:
                                try:
                                        msg = msg_list.get_nowait()        #从队列中取出数据
                                except Queue.Empty:
                                        out_list.remove(c)
                                else:
                                        print("send msg= "+msg)
                                        c.send(msg)                #发送数据
 
        tcp_server.close()


```

 
 
# 7.select多路服务器优点和缺点
* 优点
    1. 不需要频繁的创建和销毁线程和进程，节省了系统的开销和负担
    2. select采用轮询方式处理收发数据，处理效率高于多进程和多线程模型
* 缺点
   1. 单个进程监控的最多文件描述符是有限的（系统默认1024个）
   2. 需要维护一个文件描述符列表
   3. 对于文件描述符扫描是线性的，当每次对这个结构进行扫描时时间会增加
   4. 内核把文件描述符消息通知给用户空间，空间需要拷贝

   
# 8.select和多线程模型


![](http://ols7leonh.bkt.clouddn.com//assert/img/python/select/3.png)


# 9.epoll模型
1. 是Linux下多路复用IO接口select、poll的增强版本
2. 它所支持的文件描述符的上限是最大可以打开文件的数目
3. epoll只会对“活跃”的socket进行操作，不会因为文件描述符的增加导致效率线性的下降
4. epoll是通过内核于用户空间mmap同一块内存实现，使用mmap加速内核于用户空间的消息传递
 




