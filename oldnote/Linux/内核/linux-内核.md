[TOC]



![image-20180817215238591](https://github.com/chenyansong1/note/blob/master/images/linux/command/kernel.png?raw=true)





从上图，我们可以看到Linux的简单的架构

> 1. 内核：Kernel
> 2. Library：API库文件（他们是将一组分散的内核命令组合起来，形成具有某种特殊功能的接口，所以也叫API，库文件一般是以.so 结尾的（shared object共享对象））
> 3. shell是操作库的入口





# linux下overcommit_memory的问题

转自：<https://blog.csdn.net/houjixin/article/details/46412557>

**背景**

​    公司的redis有时background save db不成功，通过log发现下面的告警，很可能由它引起的：

```
[13223] 17 Mar 13:18:02.207 # WARNING overcommit_memory is set to 0! Background save may fail under low memory condition. To fix this issue add 'vm.overcommit_memory = 1' to /etc/sysctl.conf and then reboot or run the command 'sysctl vm.overcommit_memory=1' for this to take effect.

```

于是通过搜索，也有人跟我遇到同样的问题，基本可以确定是由它引起的。

**内核参数overcommit_memory** 

它是 内存分配策略

可选值：0、1、2。

* 0， 表示内核将检查是否有足够的可用内存供应用进程使用；如果有足够的可用内存，内存申请允许；否则，内存申请失败，并把错误返回给应用进程。
* 1， 表示内核允许分配所有的物理内存，而不管当前的内存状态如何。
* 2， 表示内核允许分配超过所有物理内存和交换空间总和的内存

**什么是Overcommit和OOM**

​    Linux对大部分申请内存的请求都回复"yes"，以便能跑更多更大的程序。因为申请内存后，并不会马上使用内存。这种技术叫做Overcommit。当linux发现内存不足时，会发生OOM killer(OOM=out-of-memory)。它会选择杀死一些进程(用户态进程，不是内核线程)，以便释放内存。

​    当oom-killer发生时，linux会选择杀死哪些进程？选择进程的函数是oom_badness函数(在mm/oom_kill.c中)，该函数会计算每个进程的点数(0~1000)。点数越高，这个进程越有可能被杀死。每个进程的点数跟oom_score_adj有关，而且oom_score_adj可以被设置(-1000最低，1000最高)。

**解决方法：**

​     很简单，按提示的操作（将vm.overcommit_memory 设为1）即可：

​     有三种方式修改内核参数，但要有root权限：

   （1）编辑/etc/sysctl.conf ，改vm.overcommit_memory=1，然后sysctl -p 使配置文件生效

  （2）sysctl vm.overcommit_memory=1

  （3）echo 1 > /proc/sys/vm/overcommit_memory

