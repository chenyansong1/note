[TOC]

# 概述

增强内部网关路由协议EIGRP是一种内部网关路由协议，他综合了距离矢量和链路状态两者的特点

## EIGRP具有的特点

1. 快速收敛：EIGRP使用DUAL算法来实现收敛
2. 减少带宽占用：EIGRP不做周期性的更新
3. 无缝连接数据链路层协议和拓扑结构：EIGRP不需要对OSI参考模型的二层协议做特别的配置
4. EIGRP保证网络不会产生环路，而且配置起来很简单，支持VLSM
5. EIGRP使用组播地址224.0.0.10和单播地址，直接使用IP协议进行封装，IP协议类型为88，EIGRP验证仅支持MD5验证，可以进行手工路由汇总
6. EIGRP协议在路由计算中要对网络带宽，网络延时，信道占用率和信道可信度等因素进行全面的考虑
7. 没有区域的概念，中小型网络中使用，而OSPF使用在大型网络中

## EIGRP的三张表

![image-20190908100122810](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908100122810.png)

## EIGRP的代价计算

 RIP只是根据跳数，OSPF只是根据带宽，而EIGRP会根据带宽，延迟，可靠性，负载计算

![image-20190908100519060](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908100519060.png)

![image-20190908100622186](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908100622186.png)



# 配置

![image-20190908100727062](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908100727062.png)

![image-20190908101004408](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908101004408.png)