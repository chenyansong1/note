---
title: linux 下 Socket原理及实现
categories: socket   
tags: [socket]
---



1、主要协议及Socket所在位置 



![img](E:\note\images\socket\socket-procol.png)



* TCP/IP（Transmission Control Protocol/Internet Protocol）即传输控制协议/网间协议，是一个工业标准的协议集，它是为广域网（WANs）设计的。
* UDP（User Data Protocol，用户数据报协议）是与TCP相对应的协议。它是属于TCP/IP协议族中的一种。
* ARP（Address Resolution Protocol，地址解析协议），是根据IP地址获取物理地址的一个TCP/IP协议。
* RARP（Reverse Address Resolution Protocol，反向地址转换协议） RARP允许局域网的物理机器从网关服务器的 ARP 表或者缓存上请求其 IP 地址。
* ICMP（Internet Control Message Protocol，internet控制报文协议）它是TCP/IP协议族的一个子协议，用于在IP主机、路由器之间传递控制消息。
* IGMP（Internet Group Management Protocol，Internet 组管理协议）是因特网协议家族中的一个组播协议。该协议运行在主机和组播路由器之间。



２、Socket是什么？

　　**Socket是应用层与TCP/IP协议族通信的中间软件抽象层**，它是一组接口。在设计模式中，Socket其实就是一个门面模式，它把复杂的TCP/IP协议族隐藏在Socket接口后面，对用户来说，一组简单的接口就是全部，让Socket去组织数据，以符合指定的协议。门面模式，用自己的话说，就是系统对外界提供单一的接口，外部不需要了解内部的实现。



3、Socket编程基本流程： 



![img](E:\note\images\socket\socket-procol2.png) 

* 服务器socket创建过程端说明
  * socket() :  用来指定创建的socket是的类型（套接字通信的类型）：SOCK_STREAM (tcp), SOCK_DGRAM (udp)
  * bind() : 当用socket()函数创建套接字以后，套接字在名称空间(网络地址族)中存在，但没有任何地址给它赋值 ，bind()函数并不是总是需要调用的，只有用户进程想与一个具体的地址或端口相关联的时候才需要调用这个函数。
  * listen() : listen函数在一般在调用bind之后-调用accept之前调用，函数使用主动连接套接口变为被连接套接口，使得一个进程可以接受其它进程的请求，从而成为一个服务器进程。 例如：客户端socket在创建之后，就可以直接调用connect函数 ，与他进行通信。
  * accept() : 在一个套接口接受一个连接



* 客户端socket创建过程说明
  * socket() : 用来指定创建的socket是的类型（套接字通信的类型）：SOCK_STREAM (tcp), SOCK_DGRAM (udp)
  * connect ()

