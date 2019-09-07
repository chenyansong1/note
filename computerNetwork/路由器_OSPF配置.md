[TOC]

# PTP类型单区域OSPF配置

![image-20190831161448913](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831161448913.png)



```shell
#c2811a
config t
hostname c2811a
interface lookback 0
ip address 10.10.10.10 255.255.255.255
no shutdown
exit

interface serial 0/0/0
ip address 192.168.1.1 255.255.255.0
#dce使用
clock rate 64000
exit
interface serial 0/0/1
ip address 192.168.3.1 255.255.255.0
no shutdown


#c2811b
config t
hostname c2811b
interface lookback 0
ip address 20.20.20.20 255.255.255.255
no shutdown
exit

interface serial 0/0/0
ip address 192.168.1.2 255.255.255.0
exit
interface serial 0/0/1
ip address 192.168.2.1 255.255.255.0
clock rate 64000
no shutdown


#c2811c
config t
hostname c2811b
interface lookback 0
ip address 30.30.30.30 255.255.255.255
no shutdown
exit

interface serial 0/0/0
ip address 192.168.3.2 255.255.255.0
clock rate 64000
exit
interface serial 0/0/1
ip address 192.168.2.2 255.255.255.0
no shutdown
```

配置之后，我们查看直连路由

![image-20190831162425227](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831162425227.png)

![image-20190831162836410](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831162836410.png)

下面开始配置OSPF

```shell
#c2811a
config t
#本地OSPF的进程编号，路由器之间可以不同
route ospf 10
#通配符掩码，如果是0的位置，必须匹配
#area 1 ：表示某一个区域
network 10.10.10.10 0.0.0.0 area 1
network 192.168.1.0 0.0.0.255 area 1	
network 192.168.3.0 0.0.0.255 area 1


#c2811b
config t
route ospf 10
network 20.20.20.20 0.0.0.0 area 1
network 192.168.1.0 0.0.0.255 area 1	
network 192.168.2.0 0.0.0.255 area 1

#c2811c
config t
route ospf 10
network 30.30.30.30 0.0.0.0 area 1
network 192.168.2.0 0.0.0.255 area 1	
network 192.168.3.0 0.0.0.255 area 1
```

通过上面的配置之后，我们重新查看路由器学习到的路由信息

![image-20190831163229796](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831163229796.png)

![image-20190831163334504](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831163334504.png)



查看路由使用的协议

```shell
show ip protocols
```

![image-20190831163959341](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831163959341.png)

查看邻居的情况

```shell
show ip ospf neighbor
#full:表示两台路由器的链路状态数据库已经同步
#在同一区域内的各台路由器，他们的链路状态数据库是完全一致的
#在ospf中，默认各台路由器的优先级均为0
```

![image-20190831164847653](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831164847653.png)

![image-20190907201748467](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190907201748467.png)

查看链路状态数据库

```shell
show ip ospf database
```

![image-20190831165124839](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831165124839.png)

![image-20190907202017373](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190907202017373.png)

查看ospf的接口情况

```shell
show ip ospf interface serial 0/0/0
#network type point-to-point
#在ptp类型的网络中，两台路由器既是邻居关系又是邻接关系
```

![image-20190831165449896](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831165449896.png)

代价值问题

serial的代价值为$10^8/(1.544Mbps)=64$,lookback口的代价值为$10^8/100Mbps=1$

![image-20190831170227569](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831170227569.png)

![image-20190831170502055](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831170502055.png)

ospf的管理距离都是110



查看链路带宽

```shell
show interface serial 0/0/0
#链路带宽：link bandwidth
```

代价值的计算方式:  $10^8/链路带宽$

![image-20190831171142622](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831171142622.png)



# BMA类型单区域OSPF配置

BMA:广播多路访问类型

![image-20190831205700197](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831205700197.png)

## 配置各台路由器接口的IP地址

```shell
#c2811a
config t
hostname c2811a
interface fastEthernet 0/0
ip address 192.168.1.1 255.255.255.0
no shutdown
exit
interface loopback 0
ip address 10.10.10.10 255.255.255.255.255


#c2811b
config t
hostname c2811b
interface fastEthernet 0/0
ip address 192.168.1.2 255.255.255.0
no shutdown
exit
interface loopback 0
ip address 20.20.20.20 255.255.255.255.255


#c2811c
config t
hostname c2811b
interface fastEthernet 0/0
ip address 192.168.1.3 255.255.255.0
no shutdown
exit
interface loopback 0
ip address 30.30.30.30 255.255.255.255.255
```



## 配置BMA类型的OSPF

在各台路由器上配置OSPF路由协议，要求OSPF区域为1

为了保证链路状态数据库的稳定，一经选出的DR，BDR在一般情况下不再做改变，即使有更大的路由器ID的加入，也不做链路状态数据库的更改，所以下面我们要从路由器ID最大的路由器开始进行配置，然后是次之的路由器

```shell
#c2811c
config t
router ospf 10
network 30.30.30.30 0.0.0.0 area 1
network 192.168.1.0 0.0.0.255 area 1

#c2811b
config t
router ospf 10
network 20.20.20.20 0.0.0.0 area 1
network 192.168.1.0 0.0.0.255 area 1

#c2811a
config t
router ospf 10
network 10.10.10.10 0.0.0.0 area 1
network 192.168.1.0 0.0.0.255 area 1
```

在各台路由器上查看路由表

```shell
show ip route 
```

![image-20190831210612776](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831210612776.png)

![image-20190831211354591](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831211354591.png)

查看邻居的情况

```shell
show ip ospf neighbor
#full表示路由器的链路状态数据库已经同步为一致
#为了减少链路状态信息的泛洪，可以推选出DR和BDR，进行链路状态信息的交换，各台路由器只需要将自己的链路状态信息交给DR和BRD就行，不用到所有的路由器都发送一份，当DR和BDR收集齐之后，再统一分发给其他路由器，这样就可以减少通信量
#BDR 备份指定路由器，谁的路由器的ID第二大，作为BDR
#DR 指定路由器，谁的路由器的ID最大，作为DR
```

![image-20190831211731968](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831211731968.png)

查看链路状态数据库

```shell
show ip ospf database 
```

![image-20190831213349798](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831213349798.png)

![image-20190907203646177](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190907203646177.png)

查看接口

![image-20190907203957498](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190907203957498.png)

# 多区域OSPF配置

![image-20190907204045002](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190907204045002.png)

OSPF是面向接口的

![image-20190907204222064](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190907204222064.png)

路由来源O IA（OSPF inter area ，OSPF区域间路由）表示通过OSPF学习到的**跨区域路由**

![image-20190907204458705](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190907204458705.png)

查看链路状态数据库

![image-20190907204746431](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190907204746431.png)

我们查看B路由器的链路数据库

![image-20190907205009352](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190907205009352.png)

区域边界状态数据库上，是他连接的区域的链路数据库的一个汇总



