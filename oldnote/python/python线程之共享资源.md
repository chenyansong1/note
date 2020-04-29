---
title:  python线程之共享资源
categories: python   
toc: true  
tags: [python]
---



# 1.程序实现
```
#!/usr/bin/python
 
import threading
import time
 
tmp = 0
 
def func():
        global tmp                #声明使用全局变量
        for i in range(10,15):
                tmp = i                            #改变共享的资源
                print("before {0}, tmp={1}".format(threading.currentThread().getName(),str(tmp)))
                time.sleep(1)
                print("after {0}, tmp={1}".format(threading.currentThread().getName(),str(tmp)))
 
if __name__ == "__main__":
        p = threading.Thread(target=func,args=())
        p.start()
        for i in range(0,3):
                tmp = i
                print("before {0}, tmp={1}".format(threading.currentThread().getName(),str(tmp)))
                time.sleep(1)
                print("after {0}, tmp={1}".format(threading.currentThread().getName(),str(tmp)))
 
        p.join()
```

打印结果
```
before Thread-1, tmp=10
before MainThread, tmp=0
after Thread-1, tmp=0
before Thread-1, tmp=11
after MainThread, tmp=11
before MainThread, tmp=1
after Thread-1, tmp=1
before Thread-1, tmp=12
after MainThread, tmp=12
before MainThread, tmp=2
after Thread-1, tmp=2
after MainThread, tmp=2
before Thread-1, tmp=13
after Thread-1, tmp=13
before Thread-1, tmp=14
after Thread-1, tmp=14
[Finished in 5.3s]
```






