[TOC]

CDP:Cisco discovery protocol：发现与自己相连的设备的信息，是Cisco公司专有的协议



LLDP：链路层发现协议（Link layer Discovery Protocol），其功能等同于Cisco的CDP协议





![1567039354027](https://github.com/chenyansong1/note/blob/master/images/computeNetwork/1567039354027.png?raw=true)

```shell
#配置C2811B
config t
hostname C2811B
interface fastEthernet 0/0
ip adderss 192.168.1.2 255.255.255.0
no shutdown
write 

#配置C2811A
config t
hostname C2811A
interface fastEthernet 0/0
ip adderss 192.168.1.1 255.255.255.0
no shutdown

interface fastEthernet 0/1
ip adderss 192.168.2.1 255.255.255.0
no shutdown
write 


#配置交换机
config t
hostname C3560-24PS
#给交换机配置IP地址
interface vlan 1
ip address 192.168.2.2 255.255.255.0
no shutdown
```

查看CDP，启动路由器和交换机的cdp协议

```shell
#查看cdp协议，如果没有启动，就启动他
Router#show cdp neighbors 
Capability Codes: R - Router, T - Trans Bridge, B - Source Route Bridge
                  S - Switch, H - Host, I - IGMP, r - Repeater, P - Phone
Device ID    Local Intrfce   Holdtme    Capability   Platform    Port ID
Router#

#启动cdp协议
Router(config)#cdp run

#关闭cdp协议
Router(config)#no cdp run

#在某一个端口上禁用,启用cdp
interface fastEthernet 0/1
cdp enable
no cdp enable
```

启动cdp之后，我们再次查看cdp协议

![1567040074153](https://github.com/chenyansong1/note/blob/master/images/computeNetwork/1567040074153.png?raw=true)

```shell
#在设备C2811B上发现一台neighbors：
##设备Id：C2811A
##和本地的Fast 0/0相连
##兼容的是R设备（路由器）
## 对方的平台是C2800
## 对方的端口是Fas 0/0
```

查看neighbors的更加详细的信息

```shell
show cdp entry C2811A
```

![1567040421568](https://github.com/chenyansong1/note/blob/master/images/computeNetwork/1567040421568.png?raw=true)

查看接口的详细信息

```shell
show cdp interface 
```

![1567040502002](https://github.com/chenyansong1/note/blob/master/images/computeNetwork/1567040502002.png?raw=true)