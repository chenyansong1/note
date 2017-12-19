---
title:  python进程之进程间通讯——消息队列Queue
categories: python   
toc: true  
tags: [python]
---




# 1. 简介
* Queue：是一个消息队列，队列的长度可为无限或者有限
* 用于父子进程通讯，两个没有关系的进程不能使用Queue通信
* 使用实例： from mulitprocessing import Queue

![](http://ols7leonh.bkt.clouddn.com//assert/img/python/queue/1.png)

* 进程之间同步：lock.acquire
* 当多个进程同时对队列写的时候需要进行同步，保证一个时刻只有一个进程对队列进行写操作
 
# 2.查看Queue帮助
```
In [36]: from multiprocessing import Queue
In [37]: help(Queue)
Help on function Queue in module multiprocessing:
 
Queue(maxsize=0)
    Returns a queue object

```
# 3.Queue常用方法
```
In [38]: q = Queue()
 
In [39]: q.
q.cancel_join_thread  
q.empty               
q.get                         #取出队列中的消息       
q.join_thread         
q.put_nowait
q.close               
q.full                
q.get_nowait          
q.put                        #向队列中放入消息     
q.qsize

```
# 4.Queue举例
```
#!/usr/bin/python
 
import os
from multiprocessing import Queue
 
Qmsg = Queue()                #创建一个队列
pid = os.fork()                    #启动子进程
 
if pid==0:                                #说明是子进程
        msg = Qmsg.get()                #从队列中获取数据
        print(msg)
else:                                #父进程
        Qmsg.put("msg 1")                #向队列中放入数据
        os.wait()                            #等待子进程退出


-----------------------------------------------------------------------------------------------------------------
[root@backup python]# vim queue2.py
 
import time
#!/usr/bin/python
 
import os
from multiprocessing import Queue,Process
import time
 
Qmsg = Queue()                                       #创建队列
 
def child_func(name):
        print("child pid={0}".format(os.getpid()))        #打印子进程的pid
        msg = Qmsg.get()                                    #获取队列数据
        print("name={0},msg={1}".format(name,msg))
 
print("Main pid = {0}".format(os.getpid()))        #打印父进程的pid
 
p = Process(target=child_func,args=("chilid_1",))        #创建一个子进程，并传递参数
Qmsg.put("msg_1")                                #向队列中放入数据
p.start()                                            #开启进程
p.join()                            #等待子进程结束
 
#执行                                                                                                                                                                                                          
[root@backup python]# python queue2.py
Main pid = 3742
child pid=3744
name=chilid_1,msg=msg_1

```
将start方法换成run方法，那么child_func相当于在主进程中执行，此时并没有开一个子进程

![](http://ols7leonh.bkt.clouddn.com//assert/img/python/queue/2.png)

# 5.Lock锁的使用
```
In [39]: from multiprocessing import Lock                    #导入模块
In [40]: help(Lock)
Help on function Lock in module multiprocessing:
 
Lock()
    Returns a non-recursive lock object
 
 
In [41]: l = Lock()
In [43]: l.                                        #其中的方法
l.acquire  l.release                         #获取锁、释放锁

```




 
# 6.解决多进程之间的互斥问题
加上1s的延时，则当前进程会被挂起，去执行其他的进程，所以打印的进程消息将不会有序 ，挂起之后，子进程得到锁的机会是随机的，但是在一个子进程中，因为有锁的存在，所以的能够保证一个进程中的所有的内容会一起执行，以下是解决方案：
```
[root@backup python]# cat queue3.py      
#!/usr/bin/python
 
import os                                            #导入相应的模块
from multiprocessing import Queue,Process,Lock
import time
 
Qmsg = Queue()
lock = Lock()
 
 
def child_func(name):
        lock.acquire()                                                    #加锁
 
        Qmsg.put("child_"+str(name)+"_msg_1:"+"Pid="+str(os.getpid))
        time.sleep(1)
        Qmsg.put("child_"+str(name)+"_msg_2:"+"Pid="+str(os.getpid))
 
        lock.release()                                                #释放锁，这样就能保证每一个子进程中的两次put是相邻放入到队列中的
 
 
listp = []
for i in range(10):
        p = Process(target=child_func, args=(i,))                        #创建子进程
        p.start()                                    #开启子进程
        listp.append(p)
 
while True:
        msg = Qmsg.get()                                    #获取队列消息
        print(msg)                                #打印
 
for i in range(10):
        listp[i].join()                           #等待子进程结束
-------------------------------------------------------------------------------------------------
#执行结果
[root@backup python]# python queue3.py
child_0_msg_1:Pid=<built-in function getpid>                    #child_i_msg 总是在一起执行
child_0_msg_2:Pid=<built-in function getpid>
child_3_msg_1:Pid=<built-in function getpid>
child_3_msg_2:Pid=<built-in function getpid>
child_4_msg_1:Pid=<built-in function getpid>
child_4_msg_2:Pid=<built-in function getpid>
child_5_msg_1:Pid=<built-in function getpid>
child_5_msg_2:Pid=<built-in function getpid>
child_2_msg_1:Pid=<built-in function getpid>
child_2_msg_2:Pid=<built-in function getpid>
child_1_msg_1:Pid=<built-in function getpid>
child_1_msg_2:Pid=<built-in function getpid>
child_6_msg_1:Pid=<built-in function getpid>
child_6_msg_2:Pid=<built-in function getpid>
child_7_msg_1:Pid=<built-in function getpid>
child_7_msg_2:Pid=<built-in function getpid>
child_9_msg_1:Pid=<built-in function getpid>
child_9_msg_2:Pid=<built-in function getpid>
child_8_msg_1:Pid=<built-in function getpid>
child_8_msg_2:Pid=<built-in function getpid>
^CTraceback (most recent call last):
  File "queue3.py", line 28, in <module>
    msg = Qmsg.get()
  File "/usr/lib/python2.6/multiprocessing/queues.py", line 91, in get
    res = self._recv()
KeyboardInterrupt

```



# 7.使用Queue实现最简单的本地CS架构
读取客户端输入，并将数据保存到文件中，并在Server端回显
* 基本思路：
 1. 创建小心队列
 2. 创建子进程
 3. 子进程读取输入并放到消息队列中
 4. 父进程读取并处理消息

 
![](http://ols7leonh.bkt.clouddn.com//assert/img/python/queue/3.png)
![](http://ols7leonh.bkt.clouddn.com//assert/img/python/queue/4.png)
 


