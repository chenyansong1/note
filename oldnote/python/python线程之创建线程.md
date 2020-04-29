---
title:  python线程之创建线程
categories: python   
toc: true  
tags: [python]
---


# 1.线程模型
```
import threading
def func():   #线程函数
    print "call thread_func"
def test():
    t1 = threading.Thread(target=func, args=())        #创建线程
    t1.start()    #启动线程
    return t1

if __name__=="__main__":
    p = test()
    p.join()    #等待线程退出

```

* target 是线程要执行的函数
* args是函数的参数（注意是一个元组）
 


# 2.threading.Thread帮助文档
```
In [6]: import threading
In [10]: help(threading.Thread)
Help on class Thread in module threading:
 
class Thread(_Verbose)
 |  Method resolution order:
 |      Thread
 |      _Verbose
 |      __builtin__.object
 | 
 |  Methods defined here:
 | 
 |  __init__(self, group=None, target=None, name=None, args=(), kwargs=None, verbose=None)                #初始化构造函数
 | 
 |  __repr__(self)
 | 
 |  getName(self)
 | 
 |  isAlive(self)
 | 
 |  isDaemon(self)
 | 
 |  is_alive = isAlive(self)
 | 
 |  join(self, timeout=None)
 | 
 |  run(self)                                  
 | 
 |  setDaemon(self, daemonic)                    #设置为守护线程
 | 
 |  setName(self, name)
 | 
 |  start(self)                              #启动线程

```

# 3.使用target传参的方式创建线程
```
#!/usr/bin/python
 
import threading
import time
 
def thread_func():
        for i in range(10):
                print("call thread_func")
                time.sleep(1)
 
def thread_run():
        t1 = threading.Thread(target=thread_func,args=())            #使用target的方式来指定线程执行的函数
        t1.start()
        return t1 
 
if __name__ == "__main__":
        p1 = thread_run()
        for i in range(10):
                print("in main_func i={0}".format(str(i)))
                time.sleep(1)
        p1.join()

------------------------------------------------
将start方法换成run，run方法将不是多线程了，那么会顺序执行 
将   t1.start()    换成：
t1.run()

```





# 4.主线程和子线程共享资源tmp
在主线程中创建一个tmp，看在子线程中是否共享tmp
```
#!/usr/bin/python
 
import threading
import time
 
tmp = 0                                  #子线程和主线程共享的资源tmp
 
def thread_func():
        for i in range(10):
                print("call thread_func,tmp={0}".format(tmp))                    #使用共享资源
                time.sleep(1)
 
def thread_run():
        t1 = threading.Thread(target=thread_func,args=())
        t1.start()
        return t1
 
if __name__ == "__main__":
        p1 = thread_run()
        for i in range(10):
                tmp = i                            #改变共享资源
                print("in main_func i={0}".format(str(i)))
                time.sleep(1)
        p1.join()

```
 执行结果：
```
call thread_func,tmp=0
in main_func i=0
in main_func i=1
call thread_func,tmp=1
in main_func i=2
call thread_func,tmp=2
call thread_func,tmp=2
in main_func i=3
call thread_func,tmp=3
in main_func i=4
call thread_func,tmp=4
call thread_func,tmp=4
call thread_func,tmp=4
call thread_func,tmp=4
call thread_func,tmp=4
[Finished in 10.2s]
```

# 5.守护线程的作用：setDaemon(True)
设置守护线程的作用：当主线程退出的时候，会将子线程也结束，不管此时子线程是否执行完成，这就是守护线程的作用

# 6.join()
 将join注释掉之后，主线程将不会等待子线程结束之后，再结束，而是直接退出，那么在主线程退出之后，子线程由于守护线程的存在也会退出

如果没有守护线程，那么子线程将不会退出（在主线程退出时）



# 7.通过Thread类的方式创建线程
## 7.1.threading模块的方法  

|方法|说明|
|-|-|                                            
|.activeCount()	|返回当前活动线程个数             |
|.currentThread()	|返回当前线程对象             |
|.enumerate()	|返回当前活动线程对象组成的列表   |
|.Thread()	|返回线程对象                         |
|.Timer(n,func)	|返回一个N秒后自动执行的线程函数  |
|.settrace(p_callback)	|start()前调用该回调函数  |
|.setprofil(func)	|start()后调用该回调函数      |
|.setDaemon()	|设置守护进程                     |
|.join()	|等待线程结束                         |



## 7.2.编程实现
```
#!/usr/bin/python
 
import threading
import time
 
class my_thread(threading.Thread):                    #使用继承线程类Thread方式
        def __init__(self,thread_name):
                threading.Thread.__init__(self,name=thread_name)
        def run(self):
                for i in range(10):
                        time.sleep(1)
                        print(i)
 
 
if __name__=="__main__":
        p1 = my_thread("thread1")
        p1.setDaemon(True)
        p1.start()
        time.sleep(3)
        print("main thread.........end......")
        p1.join()

```




