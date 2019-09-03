[TOC]

# VLAN技术简介

虚拟局域网是一种将局域网LAN从逻辑上划分成一个个网段，从而实现虚拟工作组的数据交换技术

* 基于网络性能考虑：可以把一个LAN划分成多个逻辑的VLAN，每个VLAN是一个广播域，这样广播被限制在一个VLAN内
* 基于组织结构考虑：网络管理者将一个无聊的LAN按照组织结构划分成不同的LAN
* 值得注意的是：既然VLAN隔离了广播风暴，同时也隔离了不同的VLAN之间的通信，**所以不同的VLAN之间的通讯需要有三层的设备（如三层交换机，路由器）来完成转发**

![image-20190903204418342](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190903204418342.png)



# vlan划分方式

![image-20190903205140569](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190903205140569.png)



# 单交换机配置

## 配置要求

![image-20190827204720494](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190827204720494.png)

![image-20190827212059765](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190827212059765.png)

在创建VLAN之前，交换机的所有口默认都是属于VLAN1的，如下图

![image-20190827204916656](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190827204916656.png)

## 创建VLAN10和VLAN20，并命名

```shell
Switch#configure t
Enter configuration commands, one per line.  End with CNTL/Z.
Switch(config)#vlan
Switch(config)#vlan 10
Switch(config-vlan)#name scb
Switch(config-vlan)#exit
Switch(config)#vlan 20
Switch(config-vlan)#name cwb
Switch(config-vlan)#"^Z
Switch#
%SYS-5-CONFIG_I: Configured from console by console

#查看创建的VLAN
Switch#show vlan

VLAN Name                             Status    Ports
---- -------------------------------- --------- -------------------------------
1    default                          active    Fa0/1, Fa0/2, Fa0/3, Fa0/4
                                                Fa0/5, Fa0/6, Fa0/7, Fa0/8
                                                Fa0/9, Fa0/10, Fa0/11, Fa0/12
                                                Fa0/13, Fa0/14, Fa0/15, Fa0/16
                                                Fa0/17, Fa0/18, Fa0/19, Fa0/20
                                                Fa0/21, Fa0/22, Fa0/23, Fa0/24
                                                Gig0/1, Gig0/2
10   scb                              active    
20   cwb                              active    
1002 fddi-default                     active    
```



## 添加端口成员到指定的VLAN

```shell
Switch#configure terminal 
#配置1-2口
Switch(config)#interface range fastEthernet 0/1-2
#添加到VLAN10
Switch(config-if-range)#switchport access vlan 10
Switch(config-if-range)#exit

#配置1-2口
Switch(config)#interface range fastEthernet 0/3-4
#添加到VLAN20
Switch(config-if-range)#switchport access vlan 20
Switch(config-if-range)#exit
Switch(config)#exit
Switch#
%SYS-5-CONFIG_I: Configured from console by console
show 
% Incomplete command.
#再次查看VLAN
Switch#show vlan
#vlan编号		VLAN名称										VLAN状态	 VLAN端口成员
VLAN 				Name                       Status    Ports
---- -------------------------------- --------- -------------------------------
1    default                          active    Fa0/5, Fa0/6, Fa0/7, Fa0/8
                                                Fa0/9, Fa0/10, Fa0/11, Fa0/12
                                                Fa0/13, Fa0/14, Fa0/15, Fa0/16
                                                Fa0/17, Fa0/18, Fa0/19, Fa0/20
                                                Fa0/21, Fa0/22, Fa0/23, Fa0/24
                                                Gig0/1, Gig0/2
10   scb                              active    Fa0/1, Fa0/2
20   cwb                              active    Fa0/3, Fa0/4

```

## VLAN间和VLAN内的计算机测试

相同VLAN间的计算机是可以ping通的，但是不同VLAN间的计算机是不能ping通的，即：即使各台计算机处于同一个IP网段，如果同一个VLAN中，可以ping通，如果不在同一个VLAN中，则不能ping通



## 什么是VLAN

1. VLAN虚拟局域网，就是将一个物理LAN划分为逻辑LAN

2. VLAN可以隔离广播域：我们在ping一个未知主机的时候，ARP会广播，但是这个广播只会在主机所在的VLAN中进行广播，VLAN之间无法通过二层设备互相访问，VLAN之间的通信需要依靠三层设备，比如：路由器，三层交换机等等



# 跨交换机VLAN配置

![](/Users/chenyansong/Desktop/)

## 配置交换机VLAN

![image-20190827213432885](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190827213432885.png)

![image-20190827213847163](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190827213847163.png)

```shell
#在switch1上配置
config t
hostname switch1
vlan 10
exit
vlan 20
exit
#将对应网口纳入到对应的vlan
interface fastEthernet 0/10
switchport access vlan 10
exit
interface fastEthernet 0/20
switchport access vlan 20
#查看VLAN
show vlan


#在switch2上配置
config t
hostname switch2
vlan 10
exit
vlan 20
exit
#将对应网口纳入到对应的vlan
interface fastEthernet 0/10
switchport access vlan 10
exit
interface fastEthernet 0/20
switchport access vlan 20
#查看VLAN
show vlan

```

我们没有做trunk，看看跨交换机的情况下，VLAN中的计算机能否通

![image-20190827214853274](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190827214853274.png)

我们可以看到即使2台交换机是在同一个VLAN中，但是因为他们跨交换机了，所以还是不能通信

## 设置trunk

```shell
#switch1
config
interface fastEthernet 0/1
switchport mode trunk
exit

#switch2
config
interface fastEthernet 0/1
switchport mode trunk
exit


#switch3
config t
interface range fastEthernet 0/1-2
#帧标记
switchport trunk encapsulation dot1q
switchport mode trunk

```

设定完trunk之后，我们再次ping，我们发现是可以ping通的

![image-20190827215546237](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190827215546237.png)





![image-20190827220124701](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190827220124701.png)



交换机之间同一VLAN通信，交换机需要相互告知帧是属于哪一个VLAN的，因为交换机在trunk链路上传输的帧，不再是以太网帧，而是在以太网帧中插入了VLAN的标记，这个VLAN帧标记的国际标准即IEEE802.1q，VLAN标记的位数为14byte，一次VLAN的数量0-4095，0，1（默认出厂），4095不能使用