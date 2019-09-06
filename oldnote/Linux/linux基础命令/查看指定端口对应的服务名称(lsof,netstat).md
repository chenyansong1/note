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



# lsof

常用示例：

 

1.显示开启文件/home/oracle/10.2.0/db_1/bin/tnslsnr的进程

 ```shell
[root@svr-db-test ~]# lsof /home/oracle/10.2.0/db_1/bin/tnslsnr
COMMAND  PID   USER  FD   TYPE DEVICE   SIZE     NODE NAME
tnslsnr 3520 oracle txt    REG  253,5 431062 11408866 /home/oracle/10.2.0/db_1/bin/tnslsnr
 ```

2.知道22端口现在运行什么程序

```shell
[root@svr-db-test ~]# lsof -i :22
COMMAND  PID USER   FD   TYPE  DEVICE SIZE NODE NAME
sshd    3101 root    3u  IPv6    8670       TCP *:ssh (LISTEN)
sshd    4545 root    3u  IPv6 4237972       TCP 203.aibo.com:ssh->win-avbmq9e8ka7.gdgg.local:nsjtp-ctrl (ESTABLISHED)
```

3.显示init进程现在打开的文件

```shell
[root@svr-db-test ~]# lsof -c init
COMMAND PID USER   FD   TYPE DEVICE    SIZE   NODE NAME
init      1 root  cwd    DIR  253,0    4096      2 /
init      1 root  rtd    DIR  253,0    4096      2 /
init      1 root  txt    REG  253,0   43496 524446 /sbin/init
init      1 root  mem    REG  253,0  130448 917826 /lib64/ld-2.5.so
init      1 root  mem    REG  253,0 1678480 917827 /lib64/libc-2.5.so
init      1 root  mem    REG  253,0   23520 917686 /lib64/libdl-2.5.so
init      1 root  mem    REG  253,0  247528 917844 /lib64/libsepol.so.1
init      1 root  mem    REG  253,0   95480 917845 /lib64/libselinux.so.1
init      1 root   10u  FIFO   0,16           2311 /dev/initctl
```

 

4. 看进程号为1的进程打开了哪些文件

```shell
[root@svr-db-test ~]# lsof -p 1
COMMAND PID USER   FD   TYPE DEVICE    SIZE   NODE NAME
init      1 root  cwd    DIR  253,0    4096      2 /
init      1 root  rtd    DIR  253,0    4096      2 /
init      1 root  txt    REG  253,0   43496 524446 /sbin/init
init      1 root  mem    REG  253,0  130448 917826 /lib64/ld-2.5.so
init      1 root  mem    REG  253,0 1678480 917827 /lib64/libc-2.5.so
init      1 root  mem    REG  253,0   23520 917686 /lib64/libdl-2.5.so
init      1 root  mem    REG  253,0  247528 917844 /lib64/libsepol.so.1
init      1 root  mem    REG  253,0   95480 917845 /lib64/libselinux.so.1
init      1 root   10u  FIFO   0,16           2311 /dev/initctl
```



5. 显示归属3520的进程情况

 ```shell
[root@svr-db-test ~]# lsof -g 3520
COMMAND  PID PGID   USER   FD   TYPE             DEVICE      SIZE     NODE NAME
tnslsnr 3520 3520 oracle  cwd    DIR              253,5      4096 11059201 /home/oracle
tnslsnr 3520 3520 oracle  rtd    DIR              253,0      4096        2 /
tnslsnr 3520 3520 oracle    9u  unix 0xffff81021b7d6980              15666 /var/tmp/.oracle/s#3520.1
tnslsnr 3520 3520 oracle   10u  unix 0xffff81021b7d66c0              15668 /var/tmp/.oracle/s#3520.2
 ```




