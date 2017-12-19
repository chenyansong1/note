---
title:  python线程之同步之Queue队列
categories: python   
toc: true  
tags: [python]
---



# 1. 简介
&emsp;Queue模块中提供了同步的、线程安全的队列类，包括FIFO（先进先出）队列Queue，LIFO（后入先出）队列LifoQueue，和优先级队列PriorityQueue，这些队列都实现了原子操作，能够在多线程中直接使用，可以使用队列来实现线程间的同步，内部实现使用了mutex和condition
```
#创建队列： 
number_queue = Queue.Queue()

#访问： 
number_queue.put(N)        
n = number_queue.get()
```

# 2.帮助文档
```
In [8]: import Queue                                            #导入模块
 
In [9]: qu = Queue.                                    #模块下的所有的函数（各种队列类型）
Queue.Empty          Queue.LifoQueue      Queue.Queue          Queue.heapq         
Queue.Full           Queue.PriorityQueue  Queue.deque         
 
In [9]: qu = Queue.Queue()                            #返回一个先进先出队列
 
In [10]: qu.                                       #先进先出队列的方法
qu.all_tasks_done    qu.get               qu.maxsize           qu.not_full          qu.qsize             qu.unfinished_tasks
qu.empty             qu.get_nowait        qu.mutex             qu.put               qu.queue            
qu.full              qu.join              qu.not_empty         qu.put_nowait        qu.task_done        
 
```

# 3.举例
```
#!/usr/bin/python
 
 
import threading, Queue
import time
 
 
g_que = Queue.Queue()                                #创建先进先出队列
 
def thread_func():
        global g_que                                    #声明使用全局变量
        while True:
                msg = g_que.get()                            #子线程取出数据，然后打印
                print(msg)
 
 
if __name__ == "__main__":
        p = threading.Thread(target=thread_func,args=())
        p.setDaemon(True)
        p.start()
        tmp = 0
 
        while True:
                tmp+=1
                g_que.put(tmp)                        #主线程向队列中放入数据
                time.sleep(1)
        p.join()
```




