[TOC]

# 链路聚合

链路聚合可以将低速的链路聚合为一个高速的链路，比较经济适用，实现的步骤：

1. 先把端口捆绑成channel-group，通道组
2. 通道组之间要相互按照规定的协议协商之后，成功之后，才会形成port-channel，端口通道可以按照VLAN的要求设定为trunk

![image-20190828201448749](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190828201448749.png)



* pagp：端口汇集协议，思科专有
* lacp：链路汇集控制协议，IEEE标准

# 配置通道组

交换机A的1口，2口做成了Channel-group1，通道组1

交换机B的23口，24口做成了Channel-group1，通道组1

两台交换机之间的channel-group相互协商，按照lacp协议协商，如果协商成功之后，就会产生port-channel，端口通道

```shell
#配置交换机A
config t
hostname switchA
interface range fastEthernet 0/1-2
#指定协议
channel-protocol lacp 
channel-group 1 mode active
write


#配置交换机B
config t
hostname switchB
interface range fastEthernet 0/23-24
#指定协议
channel-protocol lacp 
channel-group 1 mode active
write


#查看聚合的情况
show enerchanel summary
```

![image-20190828202709814](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190828202709814.png)

![image-20190828202751373](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190828202751373.png)



# 配置VLAN

```shell
#在SwitchA上做VLAN
vlan 10
vlan 20
exit
interface fastEthernet 0/3
switchport access vlan 10
exit
interface fastEthernet 0/4
switchport access vlan 20
exit

show vlan
```

![image-20190828203048474](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190828203048474.png)

```shell
#在SwitchB上做VLAN
vlan 10
vlan 20
exit
interface fastEthernet 0/1
switchport access vlan 10
exit
interface fastEthernet 0/2
switchport access vlan 20
exit

show vlan
```

![image-20190828203339091](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190828203339091.png)



# 配置trunk

```shell
#SwitchA
#直接针对端口通道进行配置，而不是针对具体的端口
interface port-channel 1
switchport trunk encapsulation dot1q
switchport mode trunk
write

#SwitchB
#直接针对端口通道进行配置，而不是针对具体的端口
interface port-channel 1
switchport trunk encapsulation dot1q
switchport mode trunk
write

```



