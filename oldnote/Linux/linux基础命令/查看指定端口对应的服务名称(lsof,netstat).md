---
title: Linux基础命令之查看指定端口对应的服务名称(lsof,netstat)
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



```
#已知一个端口为333, 如何查看端口对应的是什么服务?

#使用lsof
[root@linux-study network-scripts]# lsof -i :52113
[root@linux-study network-scripts]# lsof -i tcp:52113


#使用netstat
[root@linux-study network-scripts]# netstat -lntup|grep 52113
-l list
-n 数字
-t tcp
-u udp
p programing name 进程名

```

