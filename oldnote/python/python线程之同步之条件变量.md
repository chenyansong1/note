---
title:  python线程之同步之条件变量
categories: python   
toc: true  
tags: [python]
---



# 1.生产和消费的平衡
* 线程1负责产生数据，线程2负责分发数据，线程1和线程2同时需要对公共数据进行管理，理想的状态：1产生数据后，2能分发，1和2合理调度，保证公共数据不为0，同时也不会堆积过多

![](http://ols7leonh.bkt.clouddn.com//assert/img/python/condition/1.png)

* 如果使用lock机制，因为调度的关系，造成下面两种情况：
 1. 线程1可能一直被调度，导致线程2无法调度，这样造成数据堆积
 2. 线程2可能一直被调度，导致线程1无法调度，这样造成数据不足
如何来解决？

 
 
# 2.条件变量
允许线程阻塞等待另一个线程发送信号唤醒，条件变量被用来阻塞一个线程，当条件不满足时，线程解开相应的互斥锁并等待条件发生变化，如果其他线程改变了条件变量，并且使用条件变量唤醒一个或多个正被此条件变量阻塞的线程，这些线程将重新锁定互斥锁并重新测试条件是否满足，条件变量被用来进行线程间的同步

```
#thread1
con = threading.Condition()
while true:
    do_something
    con.acquire()    #获取锁
    con.notify()    #唤醒等待线程
    con.release()    #释放锁

#thread2
con.acquire():
while true:
    con.wait()        #等待唤醒,释放锁
    do_something
con.release()        #释放锁
```

* 条件变量实质：某一时刻只有一个线程访问公共资源，其他线程做其他任务或者休眠
* 使用条件变量实现生产者和消费者

![](http://ols7leonh.bkt.clouddn.com//assert/img/python/condition/2.png)
 
 
# 3.程序实现
```
#!/usr/bin/python
 
import threading
import time
 
tmp = 0
 
g_cond = threading.Condition()
 
def thread_func():
        global g_cond
        global tmp
 
        while True:
                g_cond.acquire()
                if tmp >= 10:                    #当tmp>=10的时候，去消费
                        tmp -= 1
                        print("sub tmp={0}".format(str(tmp)))
                else:
                        g_cond.wait()
                        print("wake up by another thread")
                g_cond.release()
 
 
if __name__ == "__main__":
        p = threading.Thread(target=thread_func,args=())
        p.setDaemon(True)
        p.start()
 
        while True:
                g_cond.acquire()
                if tmp >= 15:                      #当tmp>=15的时候，停止生产，同时通知sub thread 去消费
                        print("notify sub thread.....")
                        g_cond.notify()                    #唤醒wait的线程
                else:
                        tmp += 1
                        print("main tmp={0}".format(str(tmp)))
                g_cond.release()
                time.sleep(1)
 
        p.join()
```



