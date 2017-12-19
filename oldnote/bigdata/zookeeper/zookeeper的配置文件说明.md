---
title: zookeeper的配置文件说明
categories: hadoop   
tags: [zookeeper]
---




# 1.基本配置
最低配置要求中必须配置的参数
```
clientPort
#监听客户端连接的端口。

tickTime
#基本事件单元，这个时间是作为Zookeeper服务器之间或客户端与服务器之间维持心跳的时间间隔，每隔tickTime时间就会发送一个心跳；最小 的session过期时间为2倍tickTime 　　

dataDir
#存储内存中数据库快照的位置，如果不设置参数，更新事务的日志将被存储到默认位置。

/*
应该谨慎的选择日志存放的位置，使用专用的日志存储设备能够大大提高系统的性能，如果将日志存储在比较繁忙的存储设备上，那么将会很大程度上影像系统性能
*/

```

# 2.高级配置
高级配置参数中可选配置参数
```
dataLogdDir
#这个操作让管理机器把事务日志写入“dataLogDir”所指定的目录中，而不是“dataDir”所指定的目录。这将允许使用一个专用的日志设备，帮助我们避免日志和快照的竞争


maxClientCnxns
/*这个操作将限制连接到Zookeeper的客户端数量，并限制并发连接的数量，通过IP来区分不同的客户端。
此配置选项可以阻止某些类别的Dos攻击。将他设置为零或忽略不进行设置将会取消对并发连接的限制。
例如，此时我们将maxClientCnxns的值设为1，如下所示：
#set maxClientCnxns
   maxClientCnxns=1
启动Zookeeper之后，首先用一个客户端连接到Zookeeper服务器上。之后如果有第二个客户端尝试对Zookeeper进行连接，
或者有某些隐式的对客户端的连接操作，将会触发Zookeeper的上述配置
*/


minSessionTimeout和maxSessionTimeout 
#即最小的会话超时和最大的会话超时时间。在默认情况下，minSession=2*tickTime；maxSession=20*tickTime
```

# 3.集群配置
```
initLimit=10
#此配置表示，允许follower(相对于Leaderer言的“客户端”)连接并同步到Leader的初始化连接时间，以tickTime为单位。当初始化连接时间超过该值，则表示连接失败。
 
syncLimit=5
#此配置项表示Leader与Follower之间发送消息时，请求和应答时间长度。如果follower在设置时间内不能与leader通信，那么此follower将会被丢弃。
 
server.A=B：C：D
/*
#Example
server.0=hadoop:2288:3388
server.1=hadoop0:2288:3388
server.2=hadoop1:2288:3388

A：其中 A 是一个数字，表示这个是服务器的编号；
B：是这个服务器的 ip 地址；
C：Leader选举的端口；
D：Zookeeper服务器之间的通信端口。
*/
 

 
myid和zoo.cfg
/* 
除了修改 zoo.cfg 配置文件，集群模式下还要配置一个文件 myid，这个文件在 dataDir 目录下，
这个文件里面就有一个数据就是 A 的值，Zookeeper 启动时会读取这个文件，拿到里面的数据与 zoo.cfg 里面的配置信息比较从而判断到底是那个 server
*/
```
