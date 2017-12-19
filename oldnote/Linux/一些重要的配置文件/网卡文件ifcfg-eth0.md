---
title: Linux一些重要的配置文件之网卡文件ifcfg-eth0
categories: Linux   
toc: true  
tags: [Linux重要配置文件]
---




# 1.启动网卡
```
ifup
```

# 2. 停止网卡
```
ifdown
```

# 3.修改网卡之后重启
```
/etc/init.d/network restart

#or

ifdown eth0 && ifup eth0    #关闭和启动
```

# 4.ifcfg-eth0各字段的含义
```
[root@lamp01 ~]# cat /etc/sysconfig/network-scripts/ifcfg-eth0
DEVICE=eth0    #物理设备名,eth0表示第1块网卡
BOOTPROTO=none    
#其中proto取值如下:
/*
none:    引导时不使用协议
static:    静态分配地址
bootp:    使用BOOTP协议
dhcp:    使用DHCP协议
#办公室网络一般选择dhcp,外部网络选择静态地址
*/

#HWADDR=00:0c:29:ca:6a:82    #网卡的MAC地址,48位
NM_CONTROLLED=yes    #
ONBOOT=yes
TYPE=Ethernet    #以太网
#UUID="f1827545-55d8-4241-8aae-4775a48310d3"    #UUID用来标识一个物理网卡
DNS2=202.106.0.20    #DNS
DNS1=8.8.8.8
USERCTL=no
IPV6INIT=no
HWADDR=00:0c:29:38:6a:b1
IPADDR=192.168.0.3
NETMASK=255.255.255.0    #子网掩码
GATEWAY=192.168.0.1    #网关地址,路由器的地址

#如果是克隆的虚拟机,需要删除掉MAC地址和UUID,因为克隆的时候,这些事不会变的,但是这些又要是唯一的
```


