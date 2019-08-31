[TOC]

# rip协议配置

rip协议，每隔周期性的时间，将自己的整张路由表，发送给自己的邻居

![image-20190831145514811](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831145514811.png)

下面是上图的网络情况示意

![image-20190831145802070](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831145802070.png)

```shell
#c2811a
config t
hostname c2811a
interface fastEthernet 0/0
ip address 192.168.1.1 255.255.255.0
no shutdown
exit

interface fastEthernet 0/1
ip address 192.168.3.1 255.255.255.0
no shutdown
exit

interface lookback 0
ip address 172.16.1.1 255.255.255.0
no shutdown


#c2811b
config t
hostname c2811b
interface fastEthernet 0/0
ip address 192.168.1.2 255.255.255.0
no shutdown
exit

interface fastEthernet 0/1
ip address 192.168.2.1 255.255.255.0
no shutdown
exit

interface lookback 0
ip address 172.16.2.1 255.255.255.0
no shutdown


#c2811c
config t
hostname c2811c
interface fastEthernet 0/0
ip address 192.168.2.2 255.255.255.0
no shutdown
exit

interface fastEthernet 0/1
ip address 192.168.3.2 255.255.255.0
no shutdown
exit

interface lookback 0
ip address 172.16.3.1 255.255.255.0
no shutdown
```

各个路由器的直连的路由信息如下

![image-20190831150717068](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831150717068.png)

上面的信息可知，路由器只是知道与他直连的路由信息，下面我们需要启动RIP协议，让路由器知道非直连网路的路由信息

````shell
#c2811a
config t
router rip 
#设置版本为2
version 2
#不做自动汇总
no auto-summary
#公布网络
network 192.168.1.0
network 192.168.3.0
network 172.16.1.0

#c2811b
config t
router rip 
#设置版本为2
version 2
#不做自动汇总
no auto-summary
#公布网络
network 192.168.1.0
network 192.168.2.0
network 172.16.2.0

#c2811c
config t
router rip 
#设置版本为2
version 2
#不做自动汇总
no auto-summary
#公布网络
network 192.168.2.0
network 192.168.3.0
network 172.16.3.0
````

通过配置rip协议，路由器能够自动学习到的网络如下

![image-20190831152019440](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831152019440.png)

![image-20190831152040862](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831152040862.png)

查看路由协议

```shell
show ip protocols
```

![image-20190831152246017](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831152246017.png)



# RIP认证

在各个厂家产品的rip协议验证中，没有一个统一的标准认证方法，需要了解相关的产品信息，此处是Cisco的rip认证配置

![image-20190831153531365](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190831153531365.png)

配置上图的IP地址

```shell
#R1
config t
hostname R1
interfacce serial 1/0
ip address 192.168.1.1 255.255.255.0
clock rate 64000
no shutdown
exit

interface loopback 0
ip address 172.16.1.1 255.255.255.0
no shutdown

#R2
config t
hostname R2
interfacce serial 1/0
ip address 192.168.1.2 255.255.255.0
clock rate 64000
no shutdown
exit

interface loopback 0
ip address 172.16.2.1 255.255.255.0
no shutdown
```

接下来配置rip验证

```shell
#R1
config t
#定义一个钥匙串，名称为r1key
key chain r1key
#在钥匙串r1key中，定义一个编号为1的钥匙
key 1
#定义钥匙串r1key中编号为1的钥匙的密码为watermelon
key-string watermelon
exit

interface serial 1/0
#设定在该接口上RIP的验证模式为明文
ip rip authentication mode text
#如果使用ip rip authentication mode md5 ,则该接口上RIP的验证模式为md5密文

#指定rip验证的时候使用的钥匙串为r1key
ip rip authentication key-chain r1key
exit

#启用rip协议
router rip
version 2
no auto-summary
network 192.168.1.0
network 172.16.1.0


#R2
config t
#定义一个钥匙串，名称为r2key
key chain r2key
#在钥匙串r2key中，定义一个编号为1的钥匙
key 1
#定义钥匙串r1key中编号为1的钥匙的密码为watermelon
key-string watermelon
exit

interface serial 1/0
#设定在该接口上RIP的验证模式为明文
ip rip authentication mode text
#如果使用ip rip authentication mode md5 ,则该接口上RIP的验证模式为md5密文

#指定rip验证的时候使用的钥匙串为r1key
ip rip authentication key-chain r2key
exit

#启用rip协议
router rip
version 2
no auto-summary
network 192.168.1.0
network 172.16.2.0
```

