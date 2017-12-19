---
title:  python线程之同步之信号量
categories: python   
toc: true  
tags: [python]
---



# 1.简介
&emsp;信号量semaphore：管理一个内置的计数器，每当调用acquire()时-1, 调用release()时+1，计数器不能小于0；当计数器为0时，acquire()将阻塞线程至锁定状态，知道其他线程调用release()使计数器大于0，信号量同步机制适用于访问像服务器这样的有限资源（<font color=red>即只能够让有限个(N)资源访问</font>）
* 创建信号量：sem = threading.Semaphore(n) ;同时允许N个线程对公共资源进行访问


# 2.举例1(只让一个资源访问)
```
#!/usr/bin/python
 
import threading
import time
 
tmp = 0
 
g_sem = threading.Semaphore(1)
 
def func():
        global tmp
        global g_sem
        print("{0} before get sem ".format(threading.currentThread().getName()))
        g_sem.acquire()
        print("{0} geted sem ".format(threading.currentThread().getName()))
        time.sleep(3)
        print("{0} release sem ".format(threading.currentThread().getName()))
        g_sem.release()
 
 
if __name__ == "__main__":
        p = threading.Thread(target=func,args=())
        p.setDaemon(True)
        p.start()
        print("{0} before get sem ".format(threading.currentThread().getName()))
        g_sem.acquire()                                                                                    
        print("{0} geted sem ".format(threading.currentThread().getName()))
        time.sleep(5)
        print("{0} release sem ".format(threading.currentThread().getName()))
        g_sem.release()                                                                #主线程释放资源
 
        p.join()

-----------------------------------------------------------------------
#结果
[root@backup python]# python se.py  
MainThread before get sem
MainThread geted sem
Thread-1 before get sem                                    #主线程拿到了资源，然后子线程会等待主线程释放资源（即同一个时刻只有一个线程访问资源）
MainThread release sem
Thread-1 geted sem
Thread-1 release sem
[root@backup python]#

```

 
# 3.举例2(只让两个资源访问)
```
#!/usr/bin/python
 
import threading
import time
 
tmp = 0
 
g_sem = threading.Semaphore(2)                                    #初始化了两个信号量
 
def func():
        global tmp
        global g_sem
        print("{0} before get sem ".format(threading.currentThread().getName()))
        g_sem.acquire()
        print("{0} geted sem ".format(threading.currentThread().getName()))
        time.sleep(3)
        print("{0} release sem ".format(threading.currentThread().getName()))
        g_sem.release()
 
 
if __name__ == "__main__":
        p = threading.Thread(target=func,args=())
        p.setDaemon(True)
        p.start()
        print("{0} before get sem ".format(threading.currentThread().getName()))
        g_sem.acquire()
        print("{0} geted sem ".format(threading.currentThread().getName()))
        time.sleep(5)
        print("{0} release sem ".format(threading.currentThread().getName()))
        g_sem.release()
 
        p.join()
-----------------------------------------------------
#结果
[root@backup python]# python se2.py
MainThread before get sem
MainThread geted sem                                          #主线程可以获取资源
Thread-1 before get sem
Thread-1 geted sem                                            #子线程也是可以获取资源的
Thread-1 release sem
MainThread release sem
[root@backup python]# 
 
```

# 4.release()使信号量加1
当调用release函数的时候，信号量就会加1，那么此时就会有N+1个线程可以去访问资源，即：
```
threading.Semaphore(2)    
#等价于：
g_sem = threading.Semaphore(1)
g_sem.release() 

```






