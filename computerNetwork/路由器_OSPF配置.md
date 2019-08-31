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
route ospf 10
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
#在opf中，默认各台路由器的优先级均为0
```

![image-20190831164847653](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831164847653.png)

查看链路状态数据库

```shell
show ip ospf database
```

![image-20190831165124839](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831165124839.png)

查看ospf的接口情况

```shell
show ip ospf interface serial 0/0/0
#network type point-to-point
#在ptp类型的网络中，两台路由器既是邻居关系又是邻接关系
```

![image-20190831165449896](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831165449896.png)

代价值问题

![image-20190831170227569](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831170227569.png)

![image-20190831170502055](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831170502055.png)

查看链路带宽

```shell
show interface serial 0/0/0
```

代价值的计算方式:  $10^8/链路带宽$

![image-20190831171142622](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831171142622.png)