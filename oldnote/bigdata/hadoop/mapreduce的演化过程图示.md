---
title: mapreduce的演化过程图示
categories: hadoop   
toc: true  
tag: [mapreduce,hadoop]
---

# 1.单机词频统计

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/hadoop/mapreduce/1.png)

maptask.jar是一个已经写好的程序jar文件，可以读取词频文件，然后计算统计结果
Driver程序是一个启动maptask.jar的启动程序，如：java    maptask.jar    /home/words.txt    -Xmx=200m -Xms=200m 

# 2.分布式（多机）词频统计
如果词频文件很大，那么读取文件并进行计算的时间将会很长，于是有了下面的：<font color=red>将词频文件分成多个小文件，分发到多个机器上，同时执行maptask.jar，最后将多个机器上的执行结果合并</font>

## 2.1.将客户端的统计程序（maptask.jar）分发到2台服务器上

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/hadoop/mapreduce/2.png)

在真实的业务场景中，词频文件（日志文件）是通过负载均衡直接分发到不同的服务器上，所以词频文件就直接存在于各个服务器上

## 2.2.客户端向各个服务器发送启动maptask.jar程序的命令

客户端向各个服务器发送命令【java   -cp   cn.it.bigdata.Task   maptask.jar   /home/words.txt   -Xms=2000m   -Xmx=2000m】

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/hadoop/mapreduce/3.png)


## 2.3.合并统计
将各个机器计算的结果放到另一个机器上（服务器3）合并，并将最后合并结果保存

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/hadoop/mapreduce/4.png)

# 3.总结

![mapreduce的演化过程图示5](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/hadoop/mapreduce/5.png "mapreduce的演化过程图示5")

1. node manager是负责管理启动maptask.jar程序的，其要为程序准备硬件资源（如CPU、内存等）
2. resource manager是管理node manager的
3. node manager和resource manager组成了yarn（管理一台机器上的硬件资源，负责资源的分配）
4. maptask.jar 和 reduc.jar共同组成了mapreduce的map阶段和reduce阶段
5. 而词频文件是存放在一个指定的文件系统上的（可以是localFS，HDFS，GFS等）
