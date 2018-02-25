---
title: zookeeper四字命令
categories: hadoop   
tags: [zookeeper]
---



* 定义
长度为4个英文字母的管理命令,比如stat就是其中一个

* 使用方式
	* Telnet
		* Telnet ip port
		* 命令执行
	* nc
		* echo 命令|nc ip port

# conf
用于输出基本配置信息,也可以查看某些运行时参数
```
telnet localhost 2181 ,然后执行conf
#或者
echo conf|nc localhost 2181

```
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/4_command/1.png)


# cons
用于输出当前客户端所有连接的详细信息,包括客户端ip,会话id等
```
telnet localhost 2181 ,然后执行cons
#or
echo cons | nc localhost 2181
```
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/4_command/2.png)

 
# crst

用于重置客户端连接统计信息
```
telnet localhost 2181 ,然后执行 crst
#or
echo crst | nc localhost 2181
```

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/4_command/3.png)


# dump

用于输出当前集群的所有会话信息,包括会话id以及临时节点等信息
如果dump的是leader节点,则还会有会话的超时时间
```
telnet localhost 2181  ,然后执行dump
#or
echo dump | nc localhost 2181
```
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/4_command/4.png)


# envi

用于输出运行时的环境信息
```
telnet localhost 2181 ,然后执行envi
#or
echo envi | nc localhost 2181
```


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/4_command/5.png)



# ruok

用于输出当前zk服务器运行时是否正常,仅仅代表2181端口和四字命令流程执行正常,不能完全代表zk运行正常,最有效的命令是stat
```
telnet localhost 2181 ,然后执行ruok
#or
echo ruok | nc localhost 2181
```


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/4_command/6.png)


# stat
用于获取服务器端的运行状态:zk版本 打包信息,运行时角色,集群节点等

```
telnet localhost 2181 ,然后执行stat
#or
echo stat | nc localhost 2181

```

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/4_command/7.png)



# srvr
与stat功能类似,但是不输出连接信息
```
telnet localhost 2181 ,然后执行srvr
#or
echo srvr | nc localhost 2181

```


# srst
重置服务器的统计信息
```
telnet localhost 2181 ,然后执行srst
#or
echo srst | nc localhost 2181

```
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/4_command/8.png)



# wchs

用于输出当前服务器上管理的watcher的概要信息,通过zk构造器创建的默认watcher不在此统计范围

```
telnet localhost 2181 ,然后执行wchs
#or
echo wchs | nc localhost 2181

```
![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/4_command/9.png)



# wchc
用于输出当前服务器上管理的watcher的详细信息，以会话单位为组，通过zk构造器创建的默认watcher,不在此统计范围

```
telnet localhost 2181 ,然后执行wchc
#or
echo wchc | nc localhost 2181

```

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/4_command/10.png)


# wchp
与wchc类似，但是以节点路径分组，通过zk构造器创建的默认watcher不在此统计范围

```
telnet localhost 2181 ,然后执行wchp
#or
echo wchp | nc localhost 2181

```

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/4_command/11.png)




# mntr
与stat类似，但是比stat更详细，包括请求的延迟情况,服务区内存数据库大小,集群同步等情况

```
telnet localhost 2181 ,然后执行mntr
#or
echo mntr | nc localhost 2181

```

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/4_command/12.png)

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/zookeeper/4_command/13.png)


参见：

http://blog.csdn.net/hackerwin7/article/details/43559991
http://orchome.com/724
