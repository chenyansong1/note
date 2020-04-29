---
title: Linux一些重要的配置文件之查看所有登录用户who和查看当前登录用户whoami
categories: Linux   
toc: true  
tags: [Linux重要配置文件]
---



```
#查看登录的所有用户
[root@lamp01 ~]# who
root     pts/1        2017-02-12 14:04 (192.168.0.221)
root     pts/0        2017-02-12 13:18 (192.168.0.221)
#登录用户名    登录终端    tty本地终端    pts远程终端    登录时间    IP地址
#本地登录是没有ip地址的


[root@lamp01 ~]# w
 14:06:22 up  4:54,  2 users,  load average: 0.00, 0.00, 0.00
USER     TTY      FROM              LOGIN@   IDLE   JCPU   PCPU WHAT
root     pts/1    192.168.0.221    14:04    0.00s  0.01s  0.00s w
root     pts/0    192.168.0.221    13:18    1:44   0.02s  0.02s -bash


#查看当前用户
[root@lamp01 ~]# whoami
root


#超管和普通用户的区别
[root@lamp01 ~]# su - chenyansong
[chenyansong@lamp01 ~]$
[chenyansong@lamp01 ~]$ logout
[root@lamp01 ~]# 


#角色切换
功能描述：从一个用户切换到另外一个用户
语法：su - 用户名
 

[chenyansong@linux-study ~]$ su - root
 
#注意：“-”表示的是带环境变量的切换，从普通用户切换到root需要密码，但是从root切换到普通用户，不需要密码

```









