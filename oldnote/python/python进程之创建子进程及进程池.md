---
title:  python进程之创建子进程及进程池
categories: python   
toc: true  
tags: [python]
---




# 1.python创建进程

```
import os
ret = os.fork()        #创建进程,返回两个值,(如果是子进程,那么返回0,如果是父进程,那么返回子进程的pid)
if(ret == 0):
    print "child process"
    print os.getpid()        #子进程的pid
else:
    print "Parent process ret=%d" % ret
    print os.getpid()

print os.getpid()        #父进程和子进程都执行的代码

```

* 父进程创建子进程后，子进程会继承父进程的代码段，数据空间、堆和栈
* 通过fork()函数的返回值区分父进程和子进程
* 如果父进程没有回收子进程，那么当父进程退出后，由系统回收子进程资源

 

# 2.子进程回收函数：os.wait()

```
import os
ret = os.fork()        #创建进程,返回两个值,(如果是子进程,那么返回0,如果是父进程,那么返回子进程的pid)
if(ret == 0):
    print "child process"
    print os.getpid()        #子进程的pid
else:
    print "Parent process ret=%d" % ret
    print os.getpid()
    os.wait()        #父进程等待子进程退出

print os.getpid()        #父进程和子进程都执行的代码
```

1. 父进程不显示调用os.wait()，当父进程退出，系统的第一个进程会把子进程回收
2. 父进程显示的调用os.wait()，子进程退出后，就会被父进程回收
3. 父进程没有显示调用os.wait()，而且父进程是一个守护或者死循环（如果父进程退出，那么子进程会由系统进程回收，但是此时父进程没有退出，所以系统就不会管理子进程），那么他创建的子进程退出后，就会成为僵尸进程，当父进程不断的创建进程的时候，就会出现错误
4. 如果父进程先于子进程退出，那么守护进程init会将子进程回收，但是如果父进程在子进程的后面退出，并且此时在父进程中没有os.wait()，那么就会使子进程成为僵尸进程，即子进程的内存将不会被回收。
os.wait函数用于等待子进程结束(只适用于UNIX兼容系统)。该函数返回包含两个元素的元组，包括已完成的子进程号pid，以及子进程的退出状态，返回状态为0，表明子进程成功完成。返回状态为正整数表明子进程终止时出错。如没有子进程，会引发OSError错误。os.wait要求父进程等待它的任何一个子进程结束执行，然后唤醒父进程。

要指示父进程等候一个指定的子进程终止，可在父进程中使用os.waitpid函数(只适用于unix兼容系统)。它可等候一个指定进程结束，然后返回一个双元素元组，其中包括子进程的pid和子进程的退出状态。函数调用将pid作为第一个参数传递，并将一个选项作为第二个选项，如果第一个参数大于 0，则waitpid会等待该pid结束，如果第一个参数是-1，则会等候所有子进程，也就和os.wait一样


# 3.图形展示父进程和子进程的执行过程

![](http://ols7leonh.bkt.clouddn.com//assert/img/python/process/3.png)


# 4.multiprocessing 方式创建子进程
fork 方式是仅在linux 下才有的接口， 在windows下并没有， 那么在windows下如何实现多进程呢， 这就用到了multiprocessing
multiprocessing 模块的Process 对象表示的是一个进程对象， 可以创建子进程并执行制定的函数


```
from multiprocessing import Process
import os
 
def pro_do(name, func):
    print "This is child process %d from parent process %d, and name is  %s which is used for %s" %(os.getpid(), os.getppid(), name, func)
 
if __name__ == "__main__":
    print "Parent process id %d" %(os.getpid())
    #process 对象指定子进程将要执行的操作方法(pro_do), 以及该函数的对象列表args(必须是tuple格式， 且元素与pro_do的参数一一对应)
    pro = Process(target=pro_do, args=("test", "dev"))
    print "start child process"
    #启动子进程
    pro.start()
    #是否阻塞方式执行， 如果有， 则阻塞方式， 否则非阻塞
    pro.join() #if has this, it's synchronous operation or asynchronous operation
    print "Process end"

```
执行结果
```
Parent process id 4878
start child process
This is child process 4879 from parent process 4878, and name is  test which is used for dev
Process end

```

如果没有pro.join()， 则表示非阻塞方式运行， 那么最终的Process end的输出位置就有可能出现在pro_do 方法执行之前了
```
Parent process id 4903
start child process
Process end
This is child process 4904 from parent process 4903, and name is  test which is used for dev
```
通过multiprocessing 的process对象创建多进程， 还可以从主进程中向子进程传递参数， 例如上面例子中的pro_do的参数


# 5.Pool 进程池
```
from multiprocessing import Pool
import os, time
 
def pro_do(process_num):
    print "child process id is %d" %(os.getpid())
    time.sleep(6 - process_num)
    print "this is process %d" %(process_num)
 
if __name__ == "__main__":
    print "Current process is %d" %(os.getpid())
    p = Pool()
    for i in range(5):
        p.apply_async(pro_do, (i,))  #增加新的进程
    p.close() # 禁止在增加新的进程
    p.join()
    print "pool process done"

```

输出:
```
Current process is 19138
child process id is 19139
child process id is 19140
this is process 1
child process id is 19140
this is process 0
child process id is 19139
this is process 2
child process id is 19140
this is process 3
this is process 4
pool process done

```

其中
```
   child process id is 19139
   child process id is 19140
```
是立即输出的， 后面的依次在等待了sleep的时间后输出 ， 之所以立即输出了上面两个是因为诶Pool 进程池默认是按照cpu的数量开启子进程的， 我是在虚拟机中运行， 只分配了两核， 所以先立即启动两个子进程， 剩下的进程要等到前面的进程执行完成后才能启动。 不过也可以在p=Poo() 中使用Pool(5)来指定启动的子进程数量， 这样输出就是下面的了：


```
Current process is 19184
child process id is 19185
child process id is 19186
child process id is 19188
child process id is 19189
child process id is 19187
this is process 4
this is process 3
this is process 2
this is process 1
this is process 0
pool process done

```
且
```
Current process is 19184
child process id is 19185
child process id is 19186
child process id is 19188
child process id is 19189
child process id is 19187

```
都是立即输出的







