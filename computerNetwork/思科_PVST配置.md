[TOC]

per vlan spanning tree：每一个VLAN生成一棵树

# 配置要求

![1566951327485](https://github.com/chenyansong1/note/blob/master/images/computeNetwork/1566951327485.png?raw=true)

![1566951344682](https://github.com/chenyansong1/note/blob/master/images/computeNetwork/1566951344682.png?raw=true)

![1566951408191](https://github.com/chenyansong1/note/blob/master/images/computeNetwork/1566951408191.png?raw=true)

# 配置

```shell
#配置交换机C
config
hostnmae SwitchC
vlan 10
vlan 20
exit
#设定这两个口的trunk封装
interface range gigabitEthernet 0/1-2
switchport trunk encapsulation dot1q
switchport mode trunk
#设定生成树的协议
spanning-tree mode pvst
#设置优先级
spanning-tree vlan 10 priority 32768
spanning-tree vlan 20 priority 32768



#配置交换机A
hostname SwitchA
vlan 10
vlan 20
interface range gigabitEthernet 0/1-2
switchport trunk encapsulation dot1q
#指定模式为trunk
switchport mode trunk
#设定生成树的协议
spanning-tree mode pvst
#设置优先级
spanning-tree vlan 10 priority 4096
spanning-tree vlan 20 priority 8192


#配置交换机B
hostname SwitchA
vlan 10
vlan 20
#指定trunk的封装，采用dot1q
interface range gigabitEthernet 0/1-2
switchport trunk encapsulation dot1q
#指定模式为trunk
switchport mode trunk
#设定生成树的协议
spanning-tree mode pvst
#设置优先级
spanning-tree vlan 10 priority 8192
spanning-tree vlan 20 priority 4096
```

# 验证

在交换机C上查看

```shell
#对于VLAN10的情况
show spanning-tree vlan 10
#我们发现对应vlan10,gi0/1口是转发，gi0/2口是block
```

![1566952268349](https://github.com/chenyansong1/note/blob/master/images/computeNetwork/1566952268349.png?raw=true)

```shell
#对于VLAN10的情况
show spanning-tree vlan 20
#我们发现对应vlan10,gi0/1口是block，gi0/2口是转发
```

![1566952846360](https://github.com/chenyansong1/note/blob/master/images/computeNetwork/1566952846360.png?raw=true)

在交换机A上查看根桥

```shell
#对于VLAN10的情况
show spanning-tree vlan 10
```

![1566953062743](https://github.com/chenyansong1/note/blob/master/images/computeNetwork/1566953062743.png?raw=true)

![1566953224052](https://github.com/chenyansong1/note/blob/master/images/computeNetwork/1566953224052.png?raw=true)

```shell
#对于VLAN10的情况
show spanning-tree vlan 20
#自己并不是VLAN20的根桥
```

![1566953274256](https://github.com/chenyansong1/note/blob/master/images/computeNetwork/1566953274256.png?raw=true)

# 生成树协议

生成树，快速生成树，pvst(思科私有)，mstp（思科没有）

选择根桥的原则：桥ID最小的为根桥

桥ID由桥优先级+桥MAC地址共同组成

