---
title: rsync
categories: rsync   
toc: true  
tags: [rsync]
---

[TOC]

# 1.什么是rsync
&emsp;Rsync是一款开源的、快速的额、多功能的、可实现全量及增量的本地货远程数据同步备份的优秀工具
&emsp;Rsync全称为（Remote synchronization）,Rsync具有可使本地和远程两台主机之间的数据块快速复制同步镜像、远程备份的功能，这个功能类似于ssh的scp命令，但又优于scp命令的功能，scp每次都是全量拷贝，而rsync可以增量拷贝。利用Rsync还可以实现删除文件和目录的功能，这又相当于rm命令
&emsp;在同步备份数据时，默认情况下，Rsync通过其独特的“quick check”算法，他仅同步大小或者最后修改时间发生变化的文件或目录，当然也可以根据权限、属组等属性的变化同步，但需要指定相应的参数，甚至可以实现只同步一个文件里有变化的内容部分，所以，可以实现快速的同步备份数据

## 1.1官方文档

https://www.samba.org/ftp/rsync/rsync.html

# 2. rsync工作场景说明

* 定时同步：cron + rsync
* 实时数据同步：rsync + inotify 或sersync


![](E:\git-workspace\note\img\linux\rsync\1.png)


# 3. rsync工作方式

Rsync大致使用三种主要的传输数据的方式，man rsync看：

![](E:\git-workspace\note\img\linux\rsync\2.png)


## 3.1.单个主机本地传输数据（此时类似于cp命令的功能）

```
rsync -avz /etc/hosts /tmp/ 
#等价于cp


mkdir /null
rsync -avz --delete /null/ /tmp/
#等价于:
rm -rf /tmp/

```

## 3.2.借助rcp，ssh等通道来传输数据（此时类似于scp命令的功能）

![](E:\git-workspace\note\img\linux\rsync\3.png)

## 3.3以守护进程（socket）的方式传输数据（这个是Rsync自身的重要的功能）
见7.2节详细的例子
/tmp/ 表示tmp下内容，如果是/tmp则表示是tmp目录以及tmp下的所有内容

# 4.创建配置文件
 man rsyncd.conf 可以看配置的详细说明
```
#Rsync server
uid = rsync
gid = rsync
use chroot = no
max connections = 2000
timeout = 600
pid file = /var/run/rsyncd.pid
lock file = /var/run/rsyncd.lock
log file = /var/log/rsyncd.log
ignore errors
read only = false
list = false
hosts allow = 192.0.0.0/24
hosts deny = 0.0.0.0/32
auth users = rsync_backup
secrets file = /etc/rsync.passwd
 
#####################
[backup]
comment = backup server
path = /backup

```

![](E:\git-workspace\note\img\linux\rsync\4.png)

# 5.服务端配置步骤
## 5.1.vim /etc/rsyncd.conf 加入一堆配置文件

## 5.2.创建rsync用户，及共享的目录/backup
```
a)useradd rsync -s /sbin/nologin -M
b)id rsync
c)mkdir /backup
d)chown -R rsync /backup

#注意：这个用户（rsync）要和/etc/rsyncd.conf中的uid和gid对应，rsync服务账户要有对被同步目录(/backup)的写入更新权限。

```

## 5.3.创建密码文件
```
a)echo “rsync_backup:oldboy”>/etc/rsync.password
b)chmod 600 /etc/rsync.password

```

## 5.4rsync --daemon
```
a)netstat -lntup|grep rsync
b)ps -ef|grep rsync|grep -v grep

```

## 5.5加入开机自启动
```
a)echo “rsync --daemon” >>etc/rc.local
b)cat /etc/rc.local

```


# 6.客户端配置
## 6.1创建密码文件

```
echo “oldboy”>/etc/rsync.password
chmod 600 /etc/rsync.password

```


## 6.2 rsync
### 6.2.1 push(推)

![](E:\git-workspace\note\img\linux\rsync\5.png)


```
rsync -avz /tmp/ rsync_backup@192.168.0.103::backup --password-file=/etc/rsync.password
```

![](E:\git-workspace\note\img\linux\rsync\6.png)


### 6.2.2 pull（拉）

```
rsync -avz rsync_backup@192.168.0.103::backup  /tmp/  --password-file=/etc/rsync.password

```

![](E:\git-workspace\note\img\linux\rsync\7.png)


### 6.2.3注意推拉都是客户端的操作

# 7. rsync常用参数

```
-v --verbose 详细模式输出，传输时的进度等信息
-z --compress 传输时进行压缩以提高传输效率，--compress-level=NUM可按级别压缩
-a --archive 归档模式，表示以递归的方式传输文件，并保持所有文件属性。
-r --recursive  对子目录以递归模式，及目录下的所有目录都同样传输，注意是小写r
-t --time 保持文件时间属性
-o --owner 保持文件属主信息
-p --perms 保持文件权限
-g --group 保持文件属组信息
-P --progress 显示同步过程及传输时的进度等信息
-D --device  保持设备文件信息
-l --links  保持软链接
-e --rsh=COMMAND 使用的信道协议，指定替代rsh的shell程序，例如：ssh
-exclude=PATTERN 指定排除不需要传输的文件模式
-exclude-from=file （文件名所在的目录文件）

```


## 7.1无差异同步（--delete）

```
rsync -avz  --delete  /tm/  rsync:rsync_backup@192.168.0.104/back  --password-file=/etc/rsync.password
#本地有，远端就有，本地没有，就删除远端的

/*
使用--delete参数，一定要备份覆盖端的数据（备份服务端或者是客户端），这样避免将数据删除
使用--delete很危险，慎用！
*/

```

## 7.2 --exclude

![](E:\git-workspace\note\img\linux\rsync\8.png)

![](E:\git-workspace\note\img\linux\rsync\9.png)

![](E:\git-workspace\note\img\linux\rsync\10.png)

 跨地域数据同步：1.使用一条专线连接两地或者2.使用VPN形成局域网，然后同步

# 8.定时备份（cron + rsync）

## 8.1准备要同步的目录（ip+date 命名）

```
[root@nfs-server /]# mkdir /backup
[root@nfs-server /]# mkdir /backup/`ifconfig eth0|awk -F '[ :]+' 'NR==2 {print $4}'`_$(date +%F) -p 

```

![](E:\git-workspace\note\img\linux\rsync\11.png)


然后将ip+date的目录推到服务端即可

## 8.2准备要同步的文件（目录下的文件）

```
[root@nfs-server backup]# cp /var/spool/cron/root /backup/192.168.0.104_2016-07-29/cron_root_$(date +%F)
```

## 8.3通过脚本方式实现

&emsp;将所有的脚本文件放在一个特定的目录下（这里是/server/scripts），这是一个好的习惯

![](E:\git-workspace\note\img\linux\rsync\12.png)

![](E:\git-workspace\note\img\linux\rsync\13.png)


## 8.4测试脚本

```

/bin/sh /server/scripts/bak.sh

```

## 8.5写进定时任务

```
crontab -e
00 01 * * * /bin/sh /server/scripts/bak.sh >/dev/null 2>&1

```

# 9.排错

## 9.1rsync服务端排错思路

1. 查看rsync服务配置文件路径是否正确，正确的配置路径为：/etc/rsyncd.conf
2. 查看配置文件里host alow ,host deny 允许的ip网段是否是允许客户端访问的ip网段
3. 查看配置文件中path参数里的路径是否存在，权限是否正确（正常应为配置文件中的UID参数对应的属主和组）
4. 查看rsync服务是否启动：ps -ef|grep rsync 端口是否存在：netstat -lntup|grep 873
5. 查看iptables 防火墙和selinux是否开启允许rsync服务通过，也可考虑关闭
6. 查看服务端rsync配置的密码文件是否为600的权限，密码文件格式是否正确，正确格式为：用户名：密码 ，文件路径和配置   文件里的secrect files 参数对应
7. 如果是推送数据，要查看配置rsyncd.conf文件中用户是否对模块下的目录有可读的权限（chown -R rsync /backup）


## 9.2rsync客户端排错思路

1. 查看客户端rsync配置的密码文件是否为600的权限，密码文件的格式是否正确，注意：需要密码，并且和服务端的密码一致
2. 使用Telnet连接rsync服务器的ip 地址 873端口：telnet 192.168.0.104 873
3. 客户端执行命令是：rsync -avzP rsync_backup@192.168.0.104::backup/test/  /test/ --password-file=/etc/rsync.password


## 9.3常见的错误FAQ

### 9.3.1 auth failed on module xxxx

![](E:\git-workspace\note\img\linux\rsync\14.png)


### 9.3.2服务端没有指定共享目录（如：backup目录）

![](E:\git-workspace\note\img\linux\rsync\15.png)

### 9.3.3 password file must not be other-accessible

![](E:\git-workspace\note\img\linux\rsync\16.png)

### 9.3.4chroot failed

![](E:\git-workspace\note\img\linux\rsync\17.png)

 

chown -R rsync /backup/

### 9.3.5 从客户端推送报错

![](E:\git-workspace\note\img\linux\rsync\18.png)

### 9.3.6 客户端@ERROR：chdir failed

![](E:\git-workspace\note\img\linux\rsync\19.png)

### 9.3.7查看log
```
cat /var/log/rsyncd.log

```


# 10.优缺点

> 优点:

* 增量备份同步,支持socket(daeme),集中备份

> 缺点:

* 大量小文件同步的时候,比对时间较长,有的时候,rsync进程停止,解决:
a.打包同步,
b.drbd(文件系统复制block块)
* 同步大文件,10G这样的大文件有时也会有问题,中断,未完成同步前视隐藏文件,同步完成改为正常文件

