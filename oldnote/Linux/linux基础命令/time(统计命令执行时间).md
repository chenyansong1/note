---
title: Linux基础命令之time(统计命令执行时间)
categories: Linux   
toc: true  
tags: [Linux基础命令]
---


# 1.两种类型的time
```
[root@MySQL ~]#  type -a time
time is a shell keyword
time is /usr/bin/time

```

# 2.time显示的字段含义
```
[root@MySQL shell]# time ls
cat1.txt  cat3.txt  cat.txt  menu.sh  sg.sh  test.sh
real    0m0.007s
user    0m0.000s
sys     0m0.006s
[root@MySQL shell]#
 
```

输出的信息分别显示了该命令所花费的real时间、user时间和sys时间
* real时间是指挂钟时间，也就是命令开始执行到结束的时间。这个短时间包括其他进程所占用的时间片，和进程被阻塞时所花费的时间。
* user时间是指进程花费在用户模式中的CPU时间，这是唯一真正用于执行进程所花费的时间，其他进程和花费阻塞状态中的时间没有计算在内。
* sys时间是指花费在内核模式中的CPU时间，代表在内核中执系统调用所花费的时间，这也是真正由进程使用的CPU时间。


# 3./usr/bin/time 
&emsp;shell内建也有一个time命令，当运行time时候是调用的系统内建命令，应为系统内建的功能有限，所以需要时间其他功能需要使用time命令可执行二进制文件/usr/bin/time。


## 3.1.写入指定文件-o
```
#使用-o选项将执行时间写入到文件中：
/usr/bin/time -o outfile.txt ls

```


## 3.2.追加到指定文件
```
#使用-a选项追加信息：
/usr/bin/time -a -o outfile.txt ls

```

## 3.2.格式化输出

```
[root@lamp01 chenyansong]/usr/bin/time -f "time: %U" ls
outfile.txt  test2.txt  test3.txt         test.gar.gz    test_soft.txt    test_sort.txt  time_te.txt
tardir       test333    test_exec.tar.gz  test_hard.txt  test_sort_2.log  test.txt
time: 0.00
[root@lamp01 chenyansong]


```
格式化可选的参数表

|参数|描述|
|-|-|
|%E|real时间，显示格式为[小时:]分钟:秒|
|%U|user时间|
|%S|sys时间|
|%C|进行计时的命令名称和命令行参数,如:ls|
|%D|进程非共享数据区域，以KB为单位|
|%x|命令退出状态|
|%k|进程接收到的信号数量|
|%w|进程被交换出主存的次数|
|%Z|系统的页面大小，这是一个系统常量，不用系统中常量值也不同|
|%P|进程所获取的CPU时间百分百，这个值等于user+system时间除以总共的运行时间|
|%K|进程的平均总内存使用量（data+stack+text），单位是KB|
|%w|进程主动进行上下文切换的次数，例如等待I/O操作完成|
|%c|进程被迫进行上下文切换的次数（由于时间片到期）|