---
title:  python进程之进程间通讯——无名管道
categories: python   
toc: true  
tags: [python]
---




* 管道：是一种半双工的通信机制，他一端用来读，另一端用来写，管道只能用来在具有公共祖先的两个进程之间通信，管道通信消息先进先出的原理，<font color=red>并且数据只能被读取一次，当此段数据被读取后就会被清空，管道的实质是内存的一页（page）</font>
* 相关函数：os.pipe()：返回读写通道文件描述符组成的元组（read_end，write_end 读端和写端)
* 管道示意图
![](http://ols7leonh.bkt.clouddn.com//assert/img/python/pipe/1.png)

* 管道通讯示意图：

![](http://ols7leonh.bkt.clouddn.com//assert/img/python/pipe/2.png)

* 公共祖先：父进程和子进程，具有相同的父进程之间
* 无名管道实例：
![](http://ols7leonh.bkt.clouddn.com//assert/img/python/pipe/3.png)
 
* 使用上面的方式，我们可以处理一堆父子进程，如果我们这里有N个子进程，而且全双工通讯，那么我们如何处理呢？
 * 如果要全双工通信（读写同时进行），那么要创建两条管道，因为如果父子进程之间只有一个管道的话，父进程写了之后要读，那么，就会有父进程读取到的是自己写的内容，而一旦内容被读取之后，管道中的内容就没有了，子进程就不会读取到，所以就会出现错乱的情况，解决的办法就是创建两条管道，父进程在一条管道中读，在另一条管道中写，同时子进程在一条管道中写，在另一条管道中读。但是这样的解决方式也是有弊端的，就是每增加一个子进程，就会有2条管道增加，而管道的数量是有上限的。

 * 全双工：在父进程和每一个子进程之间创建两个管道，一条管道用于写，一条管道用于读
 * 弊端：当两个没有关系的进程不能实现通讯，如下：

 ![](http://ols7leonh.bkt.clouddn.com//assert/img/python/pipe/4.png)


```
#!/usr/bin/python
 
import os
import time
 
p = os.pipe()                    #start a pipe
pid = os.fork()        #开始一个子进程
 
if pid == 0:#only read ,so close write end
        os.close(p[1])
        while True:
                msg = os.read(p[0],1024)
                print(msg)
                if msg == 'q':
                        os.close(p[0])
                        break
else:
        os.close(p[0])   #only write ,so close read end
        while True:
                str1 = raw_input(">")
                os.write(p[1],str1) #write 'str1' to p[1]
                if str1 == 'q':
                        os.close(p[1])
                        os.wait()       #wait subprocess exit
                        break
                time.sleep(1)   #sleep 1s

```


