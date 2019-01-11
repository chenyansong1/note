转自：https://www.jianshu.com/p/5629e331f58d




[TOC]

# Htop安装

top作为日常管理工作中最常用也是最重要的Linux系统监控工具之一，可以动态观察系统进程状况

### htop介绍

htop是Linux系统下一个基本文本模式的、交互式的进程查看器，主要用于控制台或shell中，可以替代top，或者说是top的高级版。

htop命令优点:

```
1) 快速查看关键性能统计数据，如CPU（多核布局）、内存/交换使用；
2) 可以横向或纵向滚动浏览进程列表，以查看所有的进程和完整的命令行；
3) 杀掉进程时可以直接选择而不需要输入进程号；
4) 通过鼠标操作条目；
5) 比top启动得更快
```

### htop安装(centos7)

1.首先，在CentOS 7上启用epel版本。

```
[root@hk-fa ~]# yum -y install epel-release
Loaded plugins: fastestmirror
Loading mirror speeds from cached hostfile
 * base: mirrors.aliyuncs.com
 * epel: mirrors.aliyuncs.com
 * extras: mirrors.aliyuncs.com
 * updates: mirrors.aliyuncs.com
Resolving Dependencies
--> Running transaction check
---> Package epel-release.noarch 0:7-9 will be installed
--> Finished Dependency Resolution

Dependencies Resolved

//...............

Installed:
  epel-release.noarch 0:7-9                                                                                                                                                                                                                                                   

Complete!
```

2.使用yum install命令安装CentOS htop

```
[root@hk-fa ~]# yum -y install htop
Loaded plugins: fastestmirror
Loading mirror speeds from cached hostfile
 * base: mirrors.aliyuncs.com
 * epel: mirrors.aliyuncs.com
 * extras: mirrors.aliyuncs.com
 * updates: mirrors.aliyuncs.com
Resolving Dependencies
--> Running transaction check
---> Package htop.x86_64 0:2.0.2-1.el7 will be installed
--> Finished Dependency Resolution

Dependencies Resolved

================
//..........

Installed:
  htop.x86_64 0:2.0.2-1.el7                                                                                                                                                                                                                                                   

Complete!
```

3.运行htop命令



![1547189600189](E:\git-workspace\note\images\linux\command\htop.png)



### htop常用功能键

```
F1 : 查看htop使用说明
F2 : 设置
F3 : 搜索进程
F4 : 过滤器，按关键字搜索
F5 : 显示树形结构
F6 : 选择排序方式
F7 : 减少nice值，这样就可以提高对应进程的优先级
F8 : 增加nice值，这样可以降低对应进程的优先级
F9 : 杀掉选中的进程
F10 : 退出htop

/ : 搜索字符
h : 显示帮助
l ：显示进程打开的文件: 如果安装了lsof，按此键可以显示进程所打开的文件
u ：显示所有用户，并可以选择某一特定用户的进程
s : 将调用strace追踪进程的系统调用
t : 显示树形结构

H ：显示/隐藏用户线程
I ：倒转排序顺序
K ：显示/隐藏内核线程    
M ：按内存占用排序
P ：按CPU排序    
T ：按运行时间排序

上下键或PgUP, PgDn : 移动选中进程
左右键或Home, End : 移动列表    
Space(空格) : 标记/取消标记一个进程。命令可以作用于多个进程，例如 "kill"，将应用于所有已标记的进程
```

### 参考资料

[Linux下超级命令htop的学习使用](https://link.jianshu.com?t=http://www.cnblogs.com/lizhenghn/p/3728610.html)

 

 

 

 

 