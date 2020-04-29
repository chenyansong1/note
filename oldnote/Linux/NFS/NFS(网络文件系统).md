---
title: NFS(网络文件系统)
categories: NFS   
toc: true  
tags: [NFS]
---



# 1.什么是NFS?
&emsp;NFS(Network File System) 网络文件系统，他的主要功能是通过网络（一般是局域网）让不同的主机系统之间可以共享文件或目录。NFS适用于中小型网站的数据共享，而大型的网站可能会用到更复杂的分布式文件系统，例如：Moosefs(mfs),glusterfs,FastDFS

# 2.应用场景
&emsp;NFS网络文件系统一般被用来存储共享视屏、图片、附件等静态资源文件，一般是把网站用户上传的文件都放到NFS共享里，例如：BBS产品的图片、附件、头像，注意网站BBS程序不要放到NFS共享里，然后前端所有的节点访问这些静态资源时都会读取NFS存储上的资源。


# 3.为什么需要共享存储角色？
&emsp;下面通过图解展示集群架构需要共享存储服务的理由：例如：A用户传图片到Web1服务器，然后让B用户访问这张图片，结果B用户访问的请求分发到了Web2上，因为Web2上没有这张图片，结果无法看到A用户上传的图片，如果此时有一个共享存储，A用户上传图片无论分发到Web1还是Web2，最终都存储到共享存储上，此时，B用户访问图片时，无论分发到Web1还是Web2上，最终也都会去共享存储上访问，这样可以访问到资源了。
下面是原理图：

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/1.png)


# 4.NFS系统原理

## 4.1原理图
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/2.png)
 
## 4.2查看挂载信息

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/3.png) 

## 4.3客户端和服务端通信原理图

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/4.png)
 
 
注意：NFS的rpc服务在CentOS5.x下名称为portmap，而在CentOS6.x下名称为rpcbind


## 4.4比喻：NFS工作流程图

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/5.png)

# 5.安装

## 5.1检查是否已经安装rpcbind 和nfs-utils

```
[root@nfs-server ~]# rpm -qa nfs-utils rpcbind
#默认是没有安装nfs软件包（CentOS5默认是会安装）

```

## 5.2 安装软件包
```
yum install nfs-utils rpcbind -y

[chenyansong@lamp01 ~]$ rpm -aq nfs-utils rpcbind
rpcbind-0.2.0-12.el6.i686
nfs-utils-1.2.3-70.el6_8.1.i686
#出现上述两个包表示安装成功

```

# 6.服务端启动

## 6.1启动rpcbind
### 6.1.1查看rpcbind状态
```
[root@nfs-server ~]# /etc/init.d/rpcbind status
rpcbind is stopped

```

### 6.1.2启动
```
[root@nfs-server ~]# /etc/init.d/rpcbind start
Starting rpcbind:                                          [  OK  ]
[root@nfs-server ~]# /etc/init.d/rpcbind status
rpcbind (pid  2159) is running...

```

### 6.1.3查看对应的端口（默认端口是111）
```
[root@lamp01 chenyansong]# netstat -lntup|grep rpcbind
tcp        0      0 0.0.0.0:111                 0.0.0.0:*                   LISTEN      1181/rpcbind       
tcp        0      0 :::111                      :::*                        LISTEN      1181/rpcbind       
udp        0      0 0.0.0.0:111                 0.0.0.0:*                               1181/rpcbind       
udp        0      0 0.0.0.0:932                 0.0.0.0:*                               1181/rpcbind       
udp        0      0 :::111                      :::*                                    1181/rpcbind       
udp        0      0 :::932                      :::*                                    1181/rpcbind       
[root@lamp01 chenyansong]#


#还可以通过lsof 查看
[root@nfs-server ~]# lsof -i :111 

```


### 6.1.4开机自启动
```
#编辑：/etc/rc.local ,添加启动的命令 /etc/init.d/rpcbind start

[root@lamp01 chenyansong]# cat /etc/rc.local
#!/bin/sh
#
# This script will be executed *after* all the other init scripts.
# You can put your own initialization stuff in here if you don't
# want to do the full Sys V style init stuff.
 
touch /var/lock/subsys/local
 
#### NFS ######
/etc/init.d/rpcbind start

```



### 6.1.5查看房源关系映射rpcinfo
```
rpcinfo -p localhost
#查看rpc映射信息，-p表示协议

[root@lamp01 chenyansong]# rpcinfo -p localhost
   program vers proto   port  service
    100000    4   tcp    111  portmapper
    100000    3   tcp    111  portmapper
    100000    2   tcp    111  portmapper
    100000    4   udp    111  portmapper
    100000    3   udp    111  portmapper
    100000    2   udp    111  portmapper
    100024    1   udp  55295  status
    100024    1   tcp  35345  status
[root@lamp01 chenyansong]#
 

```

## 6.2启动nfs
### 6.2.1查看nfs状态
```
[root@nfs-server ~]# /etc/init.d/nfs status
rpc.svcgssd 已停
rpc.mountd is stopped
nfsd is stopped
rpc.rquotad is stopped

```

### 6.2.2 启动
```
[root@nfs-server ~]# /etc/init.d/nfs start
Starting NFS services:                                     [  OK  ]
Starting NFS quotas:                                       [  OK  ]
Starting NFS mountd:                                       [  OK  ]
Starting NFS daemon:                                       [  OK  ]
正在启动 RPC idmapd：                                      [确定]

#再次查看状态
[root@lamp01 chenyansong]# /etc/init.d/nfs status
rpc.svcgssd 已停
rpc.mountd (pid 7221) 正在运行...
nfsd (pid 7236 7235 7234 7233 7232 7231 7230 7229) 正在运行...
rpc.rquotad (pid 7217) 正在运行...

```

### 6.2.3查看映射关系
```
#rpcinfo -p localhost 产生了很多端口映射
[root@lamp01 chenyansong]# rpcinfo -p localhost 
   program vers proto   port  service
    100000    4   tcp    111  portmapper
    100000    3   tcp    111  portmapper
    100000    2   tcp    111  portmapper
    100000    4   udp    111  portmapper
    100000    3   udp    111  portmapper
    100000    2   udp    111  portmapper
    100024    1   udp  55295  status
    100024    1   tcp  35345  status
    100011    1   udp    875  rquotad
    100011    2   udp    875  rquotad
    100011    1   tcp    875  rquotad
    100011    2   tcp    875  rquotad
    100005    1   udp  37316  mountd
    100005    1   tcp  34211  mountd
    100005    2   udp  44362  mountd
    100005    2   tcp  49558  mountd
    100005    3   udp  40356  mountd
    100005    3   tcp  41507  mountd
    100003    2   tcp   2049  nfs
    100003    3   tcp   2049  nfs
    100003    4   tcp   2049  nfs
    100227    2   tcp   2049  nfs_acl
    100227    3   tcp   2049  nfs_acl
    100003    2   udp   2049  nfs
    100003    3   udp   2049  nfs
    100003    4   udp   2049  nfs
    100227    2   udp   2049  nfs_acl
    100227    3   udp   2049  nfs_acl
    100021    1   udp  53347  nlockmgr
    100021    3   udp  53347  nlockmgr
    100021    4   udp  53347  nlockmgr
    100021    1   tcp  56689  nlockmgr
    100021    3   tcp  56689  nlockmgr
    100021    4   tcp  56689  nlockmgr
[root@lamp01 chenyansong]#
 
```

### 6.2.4配置开机自启动

&emsp;因为rpcbind 要在nfs前面启动，所以将他们配置成为开机自启动，两种方式
* 在chkconfig 中配置, 要让rpcbind在nfs前面启动
* 在rc.local中配置（推荐）
我们一般在rc.local中配置，我们一眼就能够看到我们配置了哪些文件，相比较chkconfig更加的清晰。

```
[root@lamp01 chenyansong]# cat /etc/rc.local
#!/bin/sh
#
# This script will be executed *after* all the other init scripts.
# You can put your own initialization stuff in here if you don't
# want to do the full Sys V style init stuff.
 
touch /var/lock/subsys/local
 
#### NFS ######
/etc/init.d/rpcbind start
/etc/init.d/nfs start

```

# 7.nfs服务常见进程说明
```
[root@lamp01 chenyansong]# ps -ef|egrep "rpc|nfs"

```

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/6.png)
 

# 8.服务端配置详解及实战配置

## 8.1详解/etc/exports
```
#nfs的配置文件，默认是空的
[root@lamp01 chenyansong]# ll /etc/exports
-rw-r--r--. 1 root root 0 1月  12 2010 /etc/exports
[root@lamp01 chenyansong]# cat /etc/exports
[root@lamp01 chenyansong]#
 
```

### 8.1.1/etc/exports书写格式
```
#格式：
NFS 共享目录 NFS客户端地址1（参1，参2....）客户端地址2（参1，参2....）
NFS 共享目录 NFS客户端地址1（参1，参2....）
eg:   /data  192.168.0.0/24(rw,sync,all_squash)

```

### 8.1.2格式说明

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/7.png)



### 8.1.3参数权限图解

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/8.png)
 


### 8.1.4man export

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/9.png)

 

### 8.1.5语法检查reload
&emsp;配置了/etc/exports之后，我们要进行语法检查，来判断配置是否正确：/etc/init.d/nfs reload

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/10.png)

reload平滑过渡，用来检查语法

 
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/11.png)


配NFS生产重要技巧:
1. 确保所有客户端服务器对NFS共享目录具备相同的用户访问权限
1.1. all_squash把所有客户端都压缩成固定的匿名用户(UID相同)
1.2. 就是anonuid,annongid指定的UID和GID的用户
2. 所有的客户端和服务端都需要有一个相同的UID和GID的用户,及nfsnobody(UID必须相同)


![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/12.png)
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/13.png)
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/14.png)
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/15.png)
 
 

## 8.2服务器端配置步骤
### 8.2.1安装软件
```
yum install nfs-utils rpcbind -y
```

### 8.2.2启动服务（注意先后顺序）
```
/etc/init.d/rpcbind status
/etc/inti.d/rpcbind start
rpcinfo -p localhost   //查看是否有映射信息
/etc/inti.d/nfs status
/etc/inti.d/nfs start
rpcinfo -p localhost

```


### 8.2.3设置开机自启动
```
#两种方法：
#1.在chkconfig中配置
chkconfig nfs on
chkconfig rpcbind on

#2.在rc.local中配置（推荐）
[root@lamp01 chenyansong]# cat /etc/rc.local
#!/bin/sh
#
# This script will be executed *after* all the other init scripts.
# You can put your own initialization stuff in here if you don't
# want to do the full Sys V style init stuff.
 
#### NFS ######
/etc/init.d/rpcbind start
/etc/init.d/nfs start        #配置
```


### 8.2.4配置nfs服务(/etc/exports)
```
echo “/data 192.168.0.0/24(rw,sync,all_squash)”>>/etc/exports
mkdir -p /data
chown -R nfsnobody.nfsnobody /data
#(查看nfs默认使用的用户以及共享的参数:cat /var/lib/nfs/etab )

```

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/16.png)
 
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/17.png)

### 8.2.5重新加载服务(优雅重启)
```
/etc/init.d/nfs reload ====== exports -r
```

### 8.2.6检查或测试挂载
```
showmount -e localhost
mount -t nfs 192.168.0.104:/data /mnt

```

# 9.客户端配置详解及实战配置
## 9.1安装软件
```
yum install nfs-utils rpcbind -y

```

## 9.2启动服务rpcbind
```
/etc/init.d/rpcbind status
/etc/inti.d/rpcbind start
```

## 9.3测试服务端共享情况
```
showmount -e server_ip
```


## 9.4挂载
```
mkdir -p /mnt
mount -t nfs server_ip:/data /mnt
```

## 9.5测试读写
```
touch /mnt/test.txt
#然后在服务器端观察是否创建成功

```

## 9.6开机自启动(rc.local)
```
vim /etc/rc.local
 
/etc/init.d/rpcbind start
mount -t nfs 192.168.1.102:/data /mnt
#这样开机将自动挂载
```

# 10.FAQ

## 10.1 RPC:Program not registered
可能的原因是：nfs没有启动或者是nfs较rpcbind早启动，导致nfs没有向rpcbind注册

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/18.png)


## 10.2PRC:Port mapper failure

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/19.png)
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/20.png)
 

解决方法:  
1.ping 192.168.0.104;
2 telnet rpc服务的端口（111）： 192.168.0.104 111 


## 10.3写数据写不了（Permission denied）

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/21.png)
 

可能的原因：服务器端共享的/data目录没有设置权限，参见：8.2.4节


# 11.几个重要的文件
## 11.1 /etc/exports
```
#/etc/exports :NFS服务器配置文件，配置NFS具体共享服务的地点，默认内容为空，以行为单位
[root@nfs-server data]# cat /etc/exports
/data 192.168.0.0/24(rw,sync,all_squash)

```


## 11.2. /usr/sbin/exportfs
```
NFS服务的管理命令，例如：可以加载NFS配置生效（等价优雅重启：/etc/init.d/nfs reload），还可以直接配置NFS目录
exportfs -rv =======/etc/init.d/nfs reload

```

## 11.3 /usr/sbin/showmount
```
showmount - show mount information for an NFS server
#分别在客户端和服务器端，查看NFS配置及挂载情况
NFS服务器端：showmount -e localhost
NFS客户端：showmount -e server_ip


```

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/22.png)

 

## 11.4 /var/lib/nsf/etab
```
#NFS服务端可以通过/cat /var/lib/nfs/etab查看NFS服务端配置的参数细节（有很多没有配置但是默认就有的NFS参数）
[root@nfs-server data]# cat /var/lib/nfs/etab
/data   192.168.0.0/24(rw,sync,wdelay,hide,nocrossmnt,secure,root_squash,all_squash,no_subtree_check,
secure_locks,acl,anonuid=65534,anongid=65534,sec=sys,rw,root_squash,all_squash)
```


## 11.5 /proc/mounts

在NFS客户端可以通过cat /proc/mounts查看mount的挂载参数细节，如下：

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/23.png)

 
 # 12.mount 挂载
## 12.1NFS客户端挂载的格式
```
mount -t nfs 192.168.0.104:/data /mnt 
#此命令在客户端执行,/mnt(必须存在)

```

## 12.2执行挂载的过程

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/NFS/24.png)



