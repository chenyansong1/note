---
title: Linux基础命令之netstat(网络连接，路由，端口信息)
categories: Linux   
toc: true  
tags: [Linux基础命令]
---


```
 
#查看52113端口的进程名
[root@linux-study network-scripts]# netstat -lntup|grep 52113
-l list
-n 数字(显示IP地址和端口)
-t tcp
-r 路由
-u udp
p programing name 进程名



#查看本机所有的网络连接
netstat    -an

#查看本机路由表
[root@lamp01 chenyansong]# netstat -rn
Kernel IP routing table
Destination     Gateway         Genmask         Flags   MSS Window  irtt Iface
192.168.0.0     0.0.0.0         255.255.255.0   U         0 0          0 eth0
169.254.0.0     0.0.0.0         255.255.0.0     U         0 0          0 eth0
0.0.0.0         192.168.0.1     0.0.0.0         UG        0 0          0 eth0



```


