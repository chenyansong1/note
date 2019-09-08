[TOC]

# DHCP简介

DHCP：动态主机配置协议（Dynamic Host Configuration Protocol）的缩写

在TCP/IP网络中配置计算机的IP地址，可以采用两种方式：一种就是手工配置，即由网络管理员分配敬爱IP地址；另一种是由DHCP服务器自动分配IP地址

DHCP基于C/S模式，DHCP客户机启动后自动寻找并与DHCP服务器通信，从服务器哪里获取IP地址，子网掩码，网关，DNS服务器等TCP/IP参数，DHCP服务器可以是按照DHCP服务软件的计算机，也可以是网络中的路由器设备，交换机设备

DHCP协议基于UDP协议，DHCP服务器端口67，客户端端口68，DHCP广播使用的目的IP地址为有限广播255.255.255.255

![image-20190908103049074](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908103049074.png)

# 路由器充当DHCP服务器

![image-20190908103402741](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908103402741.png)



# DHCP中继

由于DHCP服务依赖于广播，因此一般情况下，DHCP客户机和DHCP服务器应该位于同一个IP网络之内，如果DHCP客户机和DHCP服务器处于不同的IP网络，而路由器可以隔离广播，因此处于不同网络的DHCP客户机和服务器将无法通信

![image-20190908104235275](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908104235275.png)

DHCP中继的工作原理，从某个接口收到DHCP广播之后，根据路由器的配置情况，向某个指定的IP地址单播转发，从而实现DHCP服务跨路由工作

![image-20190908104709214](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908104709214.png)

