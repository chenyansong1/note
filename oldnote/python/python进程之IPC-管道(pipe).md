---
title:  python进程之IPC-管道(pipe)
categories: python   
toc: true  
tags: [python]
---


* Unix中的进程间通信方式之一是通过管道实现的，管道分为有名管道和无名管道，<font color=red>对于有名管道FIFO，可以实现没有亲缘关系的进程间通信，而对于无名管道，可以实现父子进程间的通信</font>
* 管道这种IPC存在的意义是为了实现进程间消息的传递。无名管道是Unix最初的IPC形式，但是由于无名管道的局限性，<font color=red>后来出现了有名管道FIFO，这种管道由于可以在文件系统中创建一个名字，所以可以被没有亲缘关系的进程访问</font>
 
* 管道打开后的标识是以<font color=red>文件描述符的形式提供的，可以使用Unix系统中的read和write系统调用访问</font>
 
* 管道的实现形式有多种，在一些系统中，管道被实现为全双工的，在管道的一端既可以读也可以写，但是Posix.1和Unix 98只要求半双工管道，在Linux系统中，管道是半双工的。
 
Unix中的无名管道是通过 pipe 函数创建的，该函数创建了一个半双工的管道。
```
#include <unistd.h>

int pipe(int fd[2]);

返回值：成功返回0，出错返回-1
```
函数通过参数fd[2]返回两个描述符，<font color=red>fd[0]表示管道的读端，fd[1]表示管道的写端</font>

![](http://ols7leonh.bkt.clouddn.com//assert/img/python/IPC_PIPE/1.png)


管道一般是由一个父进程创建，然后被用来在父子进程间进行通信：

![](http://ols7leonh.bkt.clouddn.com//assert/img/python/IPC_PIPE/2.png)

 在父子进程通过管道进行通信的程序中，一般在父进程中先创建一个管道，然后 fork 出一个子进程，然后在两个进程中关闭不写和不读的两端。
 
<font color=red>由于Unix中的管道默认实现是单向的，为了实现双向的，可以用两个单向的管道模拟</font>

 ![](http://ols7leonh.bkt.clouddn.com//assert/img/python/IPC_PIPE/3.png)
 
 
 






