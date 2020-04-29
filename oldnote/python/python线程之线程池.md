---
title:  python线程之线程池
categories: python   
toc: true  
tags: [python]
---



# 1. 简介
* 线程池：用来解决线程生命周期开销问题和资源不足问题，通过对多个任务重用线程，线程创建的开销就被分摊到了多个任务上了，线程池中的所有线程主动从工作队列中寻找需要执行的工作。
* 如何实现线程池

![](https://github.com/chenyansong1/note/blob/master/img/python/thread_pool/1.png?raw=true)

# 2.原理图
1. 在线程池中创建了5个线程
2. 创建了N个任务列表
3. 让线程池中的每个线程循环去取任务列表中的任务

![](https://github.com/chenyansong1/note/blob/master/img/python/thread_pool/2.png?raw=true)
![](https://github.com/chenyansong1/note/blob/master/img/python/thread_pool/3.png?raw=true)




# 3. 代码实现
```
[root@backup python]# cat pool.py
#!/usr/bin/python
 
import sys,Queue,threading
 
import time
 
 #创建具体的线程
class _Thread(threading.Thread):
        def __init__(self,workQueue,resultQueue,timeout=1,**kwargs):
                threading.Thread.__init__(self,kwargs=kwargs)
                self.timeout = timeout
                self.setDaemon(True)
                self.workQueue = workQueue
                self.resultQueue = resultQueue
 
        def run(self):
                while True:
                        try:
                                callable,args,kwargs = self.workQueue.get(timeout=self.timeout)
                                #print(self.workQueue.get(timeout=self.timeout))
 
                                res = callable(args,kwargs)
                                print(res+" | "+self.getName())
                                self.resultQueue.put(res+" | "+self.getName())
 
                        except Queue.Empty:                    #任务队列中的值为空，则结束循环
                                break
                        except:
                                print(sys.exc_info())
                                raise
 
 #线程池
class ThreadPool:
        def __init__(self,num_of_threads=2):
                self.workQueue = Queue.Queue()                            #工作任务队列
                self.resultQueue = Queue.Queue()                        #结果队列
                self.threads = []                                                    #线程池
                self.__createThreadPool(num_of_threads)
 
        def __createThreadPool(self,num_of_threads):
                for i in range(num_of_threads):
                        thread = _Thread(self.workQueue,self.resultQueue)
                        self.threads.append(thread)                            #创建的线程加入线程池
 
 
        def wait_for_complete(self):
                while len(self.threads):
                        thread = self.threads.pop()
                        if thread.isAlive():
                                thread.join()
 
        def start(self):
                for th in self.threads:
                        th.start()
 
        def add_job(self,callable,*args,**kwargs):                                        #向任务队列中添加任务
                self.workQueue.put((callable,args,kwargs))                        #任务由函数来描述，args,kwargs是向函数传递的参数
 
 #具体的任务
def test_job(id,sleep=0.001):
        time.sleep(0.1)
        return str(id)
 
 
 
#测试函数
def test():
        print("start testing.........")
        tp = ThreadPool(5)                                #在线程池中创建了5个线程
 
        for i in range(50):
                tp.add_job(test_job,i,i)
        tp.start()
        tp.wait_for_complete()
 
        while tp.resultQueue.qsize():
                print(tp.resultQueue.get())
 
        print("end testing.........")
 
 
if __name__ == "__main__":
        test()

```

 



