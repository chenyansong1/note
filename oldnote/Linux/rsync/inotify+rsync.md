---
title: inotify+rsync
categories: inotify   
toc: true  
tags: [inotify]
---



# 1.应用场景：实现nfs数据的热备份

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/rsync/inotify/1.png)


 
# 2.原理

inotify（sersync）工具会实时监控/data的增删改查，然后使用rsync进行同步，如下：

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/rsync/inotify/2.png)


# 3.在nfs上做一个rsync的客户端

## 3.1创建/etc/rsync.password

```
echo chenyansong>/etc/rsync.password
  cat /etc/rsync.password
 
chmod 600 /etc/rsync.password
  ll /etc/rsync.password 

```

## 3.2推送/data数据

```
rsync -avz /data  rsync_backup@192.168.0.103::backup --password-file=/etc/rsync.password

```


# 4.安装inotify工具
&emsp;Inotify是一种强大的、细粒度的、异步的文件系统事件监控机制，Linux内核从2.6.13起，加入了Inotify的支持，通过Inotify可以监控文件系统中的添加、删除、修改等各种事件，利用这个内核接口，第三方软件就可以架空文件系统下文件的各种变化情况，而Inotify-tools真实实施这样监控的软件，类似的还有国人在金山公司开发的sersync


## 4.1查看当前系统是否支持Inotify
```
a. uname -r

b. ls -l /proc/sys/fs/inotify/

[root@lamp01 chenyansong]# ls -l /proc/sys/fs/inotify/
总用量 0
-rw-r--r-- 1 root root 0 2月  14 17:14 max_queued_events
-rw-r--r-- 1 root root 0 2月  14 17:14 max_user_instances
-rw-r--r-- 1 root root 0 2月  14 17:14 max_user_watches

#显示上面三个文件则表示支持
```


## 4.2下载源码包

```
wget https://github.com/downloads/rvoicilas/inotify-tools/inotify-tools-3.14.tar.gz
#如果不行就直接将后面的地址放到浏览器中下载，然后通过win scp 上传即可
```


## 4.3解压安装

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/rsync/inotify/3.png)
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/rsync/inotify/4.png)
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/rsync/inotify/5.png)
 
 

# 5.Inotify各个目录文件的含义
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/rsync/inotify/6.png)
 
# 6.软件工具
&emsp;一共安装了2个工具（命令），即inotifywait和inotifywatch
&emsp;inotifywait：在被监控的文件或目录上等待特定的文件系统事件（open,close,delete等）发生，执行后处于阻塞状态，适合在shell脚本中使用。
&emsp;inotifywatch：收集被监视的文件系统使用度统计数据，指文件系统事件发生的次数统计。

## 6.1 inotifywait

### 6.1.1查看帮助文档

在/usr/local/inotify-tools 目录下 bin/inotifywait --help

### 6.1.2inotifywait命令常用参数详解
```
-r --recursive #监视一个目录下的所有子目录
-q --quiet  #打印很少的信息，仅仅打印监控事件的信息
-m --monitor #始终保持事件的监听状态(接收到一个事情而不退出，无限期地执行。默认的行为是接收到一个事情后立即退出)
-excludei  #排除文件或目录时不区分大小写
-exclude   #排除文件或者目录,大小写敏感
--timefmt  #指定时间的输出格式,用于–format选项中的%T格式
 
--format   #
%w 表示发生事件的目录
%f 表示发生事件的文件
%e 表示发生的事件
%Xe 事件以“X”分隔
%T 使用由–timefmt定义的时间格式
 
-e    #指定监控事件，如果省略，所有的事件将被监控

```

### 6.1.3可以监听的事件：inotify-tools/bin/inotifywait --help  中的 -e 表示事件
|access|文件读取|
|-|-|
|modify|文件更改|
|attrib|文件属性更改，如权限，时间戳等|
|close_write|以可写模式打开的文件被关闭，不代表此文件一定已经写入数据|
|close_nowrite|以只读模式打开的文件被关闭|
|close|文件被关闭，不管它是如何打开的|
|open|文件打开|
|moved_to|一个文件或目录移动到监听的目录，即使是在同一目录内移动，此事件也触发|
|moved_from|一个文件或目录移出监听的目录，即使是在同一目录内移动，此事件也触发|
|move|包括moved_to和 moved_from|
|move_self|文件或目录被移除，之后不再监听此文件或目录。|
|create|文件或目录创建|
|delete|文件或目录删除|
|delete_self|文件或目录移除，之后不再监听此文件或目录|
|unmount|文件系统取消挂载，之后不再监听此文件系统。|

### 6.1.4 写一个测试案例

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/rsync/inotify/7.png)

然后重新开启一个窗口：

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/rsync/inotify/8.png)
 
打印结果如下：

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/rsync/inotify/9.png)

### 6.1.5 监控多个事件（-e）

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/rsync/inotify/10.png)

### 6.1.6写成shell 脚本
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/rsync/inotify/11.png)

 
放入后台(&表示放入后台，这样就不会出现阻塞)

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/rsync/inotify/12.png)

写入rc.local中去：

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/rsync/inotify/13.png)
 

## 6.2inotifywatch

自行Google

# 7.优化

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/rsync/inotify/14.png)
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/rsync/inotify/15.png)
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/rsync/inotify/16.png)
 
# 8.优缺点

> 优点:

* 配合rsync实现实时数据同步

> 缺点:

* 如果并发大于200个文件(10-100K),同步就会有延迟
* 我们前面写的脚本,每次都是全部全部推送一次,但确实是增量的
* 监控到事件后,调用rsync同步是单进程的(加&并发),sersync是多进程同步,既然有了inotify-tools,为什么还要开发sersync?
 sersync功能多:1.配置文件,2.真正的守护进程socket,3.可以对失败文件定时重传,4,第三方的HTTP接口,5.默认多线程同步

# 9.数据实时同步方案

drbd 是基于数据块的同步

高并发数据实时同步方案小结:
* inotify(sersync)+rsync 文件级别
* drbd 文件系统级别
* 第三方软件的同步功能:mysql ,oracle,mongodb
* 程序双写
* 业务逻辑解决











