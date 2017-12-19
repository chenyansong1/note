---
title:  python线程之同步之Event事件
categories: python   
toc: true  
tags: [python]
---



# 1.简介
&emsp;一个线程通知事件，其他线程等待事件
```
#创建Event:
thread_event = threading.Event()

#等待：
thread_event.wait()

#唤醒：
thread_event.set()
```

# 2.举例
```
#!/usr/bin/python
 
import threading
import time
 
 
g_event = threading.Event()
 
def thread_func():
        global g_event
 
        while True:
                if g_event.isSet():
                        g_event.clear()                            
                print("Thread_1 start Wait")
                g_event.wait()                                        #等待主线程中的set()，将其唤醒
                print("Thread_1 end Wait")
 
 
if __name__ == "__main__":
        p = threading.Thread(target=thread_func,args=())
        p.setDaemon(True)
        p.start()
 
        while True:
                time.sleep(1)
                g_event.set()
        p.join()

```
 
 



