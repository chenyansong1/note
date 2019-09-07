[TOC]

# RIP概述

RIP现有v1和v2两个版本，无论是v1还是v2版本，RIP协议都是一个基于UDP协议的应用层协议，也就说RIP协议所传递的路由信息都封装在UDP数据报中，所使用的源端口和目的端口都是UDP端口520，在经过PI封装的时候，v1和v2有一些区别，v1的目的IP地址为255.255.255.255（有限广播），v2的目的IP地址是组播地址224.0.0.9,源IP为发送RIP报文的路由器接口IP地址

![image-20190907093917839](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190907093917839.png)

![image-20190907094042451](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190907094042451.png)

![image-20190907094531707](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190907094531707.png)

* RIP协议的特点
  1. RIP属于典型的距离向量路由协议，管理距离都为120
  2. RIP以到目的网络的最少跳数作为路由选择度量标准，即基于跳数hops，而不是在链路的带宽，延迟，费用等基础上进行选择，RIP的跳数计数限制为15跳，16跳即表示不可达，这限制了网络规模
  3. RIP会向邻居发送自己的路由表信息，但是并不能保证这些路由信息的正确与否，所以RIP也称作为谣言路由
  4. 运行RIP协议的路由器都将以周期性的时间间隔，把自己完整的路由表作为更新消息，发送给所有的邻居路由器，默认更新周期为30秒
  5. 当网络拓扑结构发生变化，某台路由器的某条路由条目发生改变，网络中的所有路由器需要全部更新他们的路由表，而使得网络重新达到稳定，这个时间称为网络收敛时间(从不稳定状态进入到稳定状态)，RIP协议的收敛时间较长，收敛速度慢
  6. RIPv2使用非常广泛，他简单，可靠，便于配置，但是只适用于小型的同构网络

# RIP路由表形成过程

进过两个30秒，3台路由器达到同步一致，稳定

![image-20190907100210928](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190907100210928.png)

# 路由自环问题及解决方法

* 路由自环的产生

![image-20190907101139395](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190907101139395.png)

1. 在RC上10.1.4.1/24网络down掉了。但是还没有到路由的更新时间，所以RC不会将这个路由信息发送给邻居RB
2. 此时RB正好发送了RIP协议到RC，告诉RC我这边有到10.1.4.1/24网络的路由，然后RC就会更改自己的路由信息，然后的down改为RB的路由信息，这样就会产生了误解行为
3. 这样就会形成自环，因为有TTL值的存在，最终IP数据包会被丢弃，但是还是浪费了网络带宽

* 解决方法

1. 定义最大代价值
2. 水平分隔
3. 触发更新：网络变化，立即触发更新
4. 路由毒化
5. 反向毒化
6. 抑制时间：发过来的代价值比原来发过来的代价值要高，那么等待180秒

在RIP协议中，定义了以下几种时间，用于RIP协议的工作中：

* 更新时间Update Time --30s
* 失效时间Invalid Time---180s
* 清空时间Flushed Time----240s
* 抑制时间（Holddown Time)----180s



# RIPv2的配置

参见；路由器_RIPv2配置.md

![image-20190907104101637](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190907104101637.png)

![image-20190907104039148](/Users/chenyansong/Library/Application Support/typora-user-images/image-20190907104039148.png)