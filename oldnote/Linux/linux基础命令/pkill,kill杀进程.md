---
title: Linux基础命令之pkill,kill杀进程
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



# pkill 进程名
```
[root@lamp01 tardir]lsof -i:3306
COMMAND  PID  USER   FD   TYPE DEVICE SIZE/OFF NODE NAME
mysqld  4385 mysql   10u  IPv4  25845      0t0  TCP *:mysql (LISTEN)

[root@lamp01 tardir]pkill mysqld
#再次查看
[root@lamp01 tardir]lsof -i:3306

```

# killall 进程名
&emsp;因为killall 是比较平滑的杀进程，所以我们要连续的杀，知道提示“没有进程被杀死”
```
[root@lamp01 tardir]lsof -i:3306           
COMMAND  PID  USER   FD   TYPE DEVICE SIZE/OFF NODE NAME
mysqld  4555 mysql   10u  IPv4  26148      0t0  TCP *:mysql (LISTEN)
[root@lamp01 tardir]killall mysqld
[root@lamp01 tardir]killall mysqld
mysqld: 没有进程被杀死
[root@lamp01 tardir]
```

# kill 进程号
```
[root@lamp01 tardir]ps -ef|grep sshd
root      1332     1  0 Feb12 ?        00:00:00 /usr/sbin/sshd    #1332就是sshd的进程号
root      2960  1332  0 07:29 ?        00:00:04 sshd: root@pts/0 
root      4757  2962  0 15:36 pts/0    00:00:00 grep sshd

kill 1332
#or
kill `cat /var/run/sshd.pid`

#平滑处理
kill -HUP `cat /var/run/sshd.pid`
kill -USR2 `cat /var/run/sshd.pid`
```

# 批量杀死进程

```shell
$ ps -ef | grep rtprecv | grep -v grep | awk '{print $2}' | xargs kill -9
```

