[toc]

转：https://blog.csdn.net/zhangskd/article/details/16986931

本文主要内容：SYN Cookie的原理，以及它的内核实现。

内核版本：3.6

Author：zhangskd @ csdn blog

 

## SYN Flood

 

下面这段介绍引用自[1].

SYN Flood是一种非常危险而常见的Dos攻击方式。到目前为止，能够有效防范SYN Flood攻击的手段并不多，

SYN Cookie就是其中最著名的一种。

 

SYN Flood攻击是一种典型的拒绝服务(Denial of Service)攻击。所谓的拒绝服务攻击就是通过进行攻击，使受害主机或

网络不能提供良好的服务，从而间接达到攻击的目的。

SYN Flood攻击利用的是IPv4中TCP协议的三次握手(Three-Way Handshake)过程进行的攻击。

TCP服务器收到TCP SYN request包时，在发送TCP SYN + ACK包回客户机前，TCP服务器要先分配好一个数据区专门

服务于这个即将形成的TCP连接。一般把收到SYN包而还未收到ACK包时的连接状态称为半打开连接(Half-open Connection)。

在最常见的SYN Flood攻击中，攻击者在短时间内发送大量的TCP SYN包给受害者。受害者(服务器)为每个TCP SYN包分配

一个特定的数据区，只要这些SYN包具有不同的源地址(攻击者很容易伪造)。这将给TCP服务器造成很大的系统负担，最终

导致系统不能正常工作。

 

## SYN Cookie

 

SYN Cookie原理由D.J. Bernstain和Eric Schenk提出。

SYN Cookie是对TCP服务器端的三次握手做一些修改，专门用来防范SYN Flood攻击的一种手段。它的原理是，在TCP服务器接收到TCP SYN包并返回TCP SYN + ACK包时，不分配一个专门的数据区，而是根据这个SYN包计算出一个cookie值。这个cookie作为将要返回的SYN ACK包的初始序列号。当客户端返回一个ACK包时，根据包头信息计算cookie，与返回的确认序列号(初始序列号 + 1)进行对比，如果相同，则是一个正常连接，然后，分配资源，建立连接。

 

实现的关键在于cookie的计算，cookie的计算应该包含本次连接的状态信息，使攻击者不能伪造。

cookie的计算：

服务器收到一个SYN包，计算一个消息摘要mac。

mac = MAC(A, k);

MAC是密码学中的一个消息认证码函数，也就是满足某种安全性质的带密钥的hash函数，它能够提供cookie计算中需要的安全性。

在Linux实现中，MAC函数为SHA1。

A = SOURCE_IP || SOURCE_PORT || DST_IP || DST_PORT || t || MSSIND

k为服务器独有的密钥，实际上是一组随机数。

t为系统启动时间，每60秒加1。

MSSIND为MSS对应的索引。

 