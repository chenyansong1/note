---
title: sentinel(redis高可用解决方案)
categories: redis   
toc: true  
tags: [redis]
---



由一个或多个sentinel实例组成的sentinel系统可以监视任意多个主服务器，以及这些主服务器属下的所有从服务器；并在被监视的主服务器进入下线状态时，自动将下线主服务器属下的某个从服务器升级为新的主服务器，然后由新的主服务器代替已下线的主服务器继续处理命令请求。如图所示：


![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/redis/sentinel/1.png)
 

它是如何工作的？

> 启动

```
redis-sentinel sentinel.conf 或者
redis-server sentinel.conf --sentinel

```

> 初始化服务器

sentinel本质上只是一个运行在特殊模式下的redis服务器，但它不使用数据库，所以并不会载入RDB文件或者AOF文件来还原数据库状态。初始化过程：

* 使用sentinel专用代码初始化sentinel状态
* 创建连向主服务器的网络连接


连接建立之后，sentinel将成为主服务器的客户端，它可以向主服务器发送命令，并从命令回复中获取相关的信息。对于每个被sentinel监视的主服务器来说，sentinel会创建两个连向主服务器的异步网络连接：

* 命令连接，向主服务发送命令，并接收命令回复
* 订阅连接，订阅主服务器的__sentinel__：hello频道


注：如果发现主服务器下有从服务器，也会为从服务器创建命令连接和订阅连接。而sentinel与sentinel之间则只创建命令连接。

> 获取服务器信息

sentinel默认会以每十秒一次的频率，通过命令连接向被监视的主从服务器发送INFO命令，并通过分析INFO命令的回复来获取主服务器的当前信息。

> 发送和接收消息

在默认情况下，sentinel会以每两秒一次的频率，通过命令连接向所有被监视的主服务器和从服务器发送以下格式的命令：
```
PUBLISH __sentinel__:hello "<s_ip>,<s_port>,<s_runid>,<s_epoch>,<m_name>,<m_ip>,<m_port>,<m_epoch>"
```

当sentinel与一个主服务器或者从服务器建立起订阅连接之后，sentinel就会通过订阅连接，向服务器发送以下命令：

```
SUBSCRIBE __sentinel__:hello
```

sentinel对__sentinel__:hello频道的订阅会一直持续到sentinel与服务器的连接断开为止。
 
也就是说，对于每个与sentinel连接的服务器，既通过命令连接向服务器的__sentinel__:hello频道发送信息，又通过订阅连接从服务器的__sentinel__:hello频道接收信息。对于监视同一个服务器的多个sentinel来说，一个sentinel发送的信息会被其他sentinel接收到，这些信息会被用于更新其他sentinel对发送信息sentinel的认知，也会被用于更新其他sentinel对被监视服务器的认知。

> 检测服务器状态

检测主观下线状态
 
sentinel以每秒一次的频率向实例(包括主服务器、从服务器、其他sentinel)发送PING命令，并根据实例对PING命令的回复来判断实例是否在线，当一个实例在指定的时长中连续向sentinel发送无效回复时，sentinel会将这个实例判断为主观下线。
 
检测客观下线状态，进行故障转移
 
当sentinel将一个主服务器判断为主观下线时，它会向同样监视这个主服务器的其他sentinel进行询问，看它们是否同意这个主服务器已经进入主观下线状态；当sentinel收集到足够多的主观下线投票之后，它会将主服务器判断为客观下线，并选取一个领头sentinel发起一次针对主服务器的故障转移操作。
注：更多细节请查阅<<redis设计与实现>> 第16章内容...



转自:
[sentinel(redis高可用解决方案)](https://segmentfault.com/a/1190000004647243)


可以参考:
[Redis Sentinel机制与用法（一）](https://segmentfault.com/a/1190000002680804)
[Redis Sentinel 机制与用法（二）](https://segmentfault.com/a/1190000002685515)


