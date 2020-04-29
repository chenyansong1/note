---
title:  python线程之线程同步之互斥锁
categories: python   
toc: true  
tags: [python]
---



# 1.概念介绍
* 线程互斥：是指某一个资源同时只允许一个访问者对其进行修改，具有唯一性和排他性
* 线程死锁：死锁就是系统进入到一种阻塞的，无法移动的状态
* 死锁出现的情况：一个线程占有锁之后又去占有锁
    1. 占有锁后，忘记释放
    2. 占有锁，然后调用其他函数又去占有锁
* 线程死锁避免：代码书写小心，保证占有和释放成对出现
* 线程饿死：一个线程长时间的得不到需要的资源而不能执行的现象
* 线程饿死避免：线程队列，或者结合其他同步方式

# 2.互斥锁程序实现
```
#!/usr/bin/python

import threading
import time

tmp = 0

g_lock = threading.Lock()

def func():
        global tmp
        global g_lock

        for i in range(20,25):
                g_lock.acquire()                        #加锁
                tmp -= 1
                print(threading.currentThread().getName()+":tmp={0}".format(str(tmp)))
                g_lock.release()                        #释放锁
                time.sleep(1)

if __name__=="__main__":
        p = threading.Thread(target=func,args=())
        p.setDaemon(True)
        p.start()

        for i in range(0,5):
                g_lock.acquire()                    #加锁
                tmp += 1
                print(threading.currentThread().getName()+":tmp={0}".format(str(tmp)))
                g_lock.release()                    #释放锁
                time.sleep(1)

        p.join()
```

打印结果
```
Thread-1:tmp=-1
MainThread:tmp=0
MainThread:tmp=1
Thread-1:tmp=0
MainThread:tmp=1
Thread-1:tmp=0
MainThread:tmp=1
Thread-1:tmp=0
MainThread:tmp=1
Thread-1:tmp=0
[Finished in 5.3s]

```



