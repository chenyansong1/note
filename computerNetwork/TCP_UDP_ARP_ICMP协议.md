[TOC]

# TCP协议

一种面向连接的，可靠的，基于字节流的传输层通信协议，该协议主要用于在源主机和目的主机之间建立一个虚拟连接，实现高可靠的数据交换

![image-20190901095626765](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190901095626765.png)

```shell
顺序号：告诉对方，这个是我发送给你的第300个字节的数据
确认号：反馈给对方，你发送给我的前400字节的数据都已经收到了
窗口大小：表示当前，我能收数据的窗口为1000，那么对方看到这个之后，再次发送数据的时候，就不会超过1000，这就是发送方的发送能力不能超过接收方的接收能力
SYN：请求建立连接
FIN：请求断开连接
```

 

一、TCP报文格式

​        TCP/IP协议的详细信息参看《TCP/IP协议详解》三卷本。下面是TCP报文格式图：

![img](/Users/chenyansong/Documents/note/images/computeNetwork/22312037_1365321234nnNc.png)
图1 TCP报文格式

​        上图中有几个字段需要重点介绍下：
​        （1）序号：Seq序号，占32位，用来标识从TCP源端向目的端发送的字节流，发起方发送数据时对此进行标记。
​        （2）确认序号：Ack序号，占32位，只有ACK标志位为1时，确认序号字段才有效，Ack=Seq+1。
​        （3）标志位：共6个，即URG、ACK、PSH、RST、SYN、FIN等，具体含义如下：
​                （A）URG：紧急指针（urgent pointer）有效。
​                （B）ACK：确认序号有效。
​                （C）PSH：接收方应该尽快将这个报文交给应用层。
​                （D）RST：重置连接。
​                （E）SYN：发起一个新连接。
​                （F）FIN：释放一个连接。

​        需要注意的是：
​                （A）不要将确认序号Ack与标志位中的ACK搞混了。
​                （B）确认方Ack=发起方Req+1，两端配对。 

二、三次握手
        所谓三次握手（Three-Way Handshake）即建立TCP连接，就是指建立一个TCP连接时，需要客户端和服务端总共发送3个包以确认连接的建立。在socket编程中，这一过程由客户端执行connect来触发，整个流程如下图所示：

![img](/Users/chenyansong/Documents/note/images/computeNetwork/22312037_1365405910EROI.png)
图2 TCP三次握手

​        （1）第一次握手：Client将标志位SYN置为1，随机产生一个值seq=J，并将该数据包发送给Server，Client进入SYN_SENT状态，等待Server确认。
​        （2）第二次握手：Server收到数据包后由标志位SYN=1知道Client请求建立连接，Server将标志位SYN和ACK都置为1，ack=J+1，随机产生一个值seq=K，并将该数据包发送给Client以确认连接请求，Server进入SYN_RCVD状态。
​        （3）第三次握手：Client收到确认后，检查ack是否为J+1，ACK是否为1，如果正确则将标志位ACK置为1，ack=K+1，并将该数据包发送给Server，Server检查ack是否为K+1，ACK是否为1，如果正确则连接建立成功，Client和Server进入ESTABLISHED状态，完成三次握手，随后Client与Server之间可以开始传输数据了。
​        
​        SYN攻击：
​                在三次握手过程中，Server发送SYN-ACK之后，收到Client的ACK之前的TCP连接称为半连接（half-open connect），此时Server处于SYN_RCVD状态，当收到ACK后，Server转入ESTABLISHED状态。SYN攻击就是Client在短时间内伪造大量不存在的IP地址，并向Server不断地发送SYN包，Server回复确认包，并等待Client的确认，由于源地址是不存在的，因此，Server需要不断重发直至超时，这些伪造的SYN包将产时间占用未连接队列，导致正常的SYN请求因为队列满而被丢弃，从而引起网络堵塞甚至系统瘫痪。SYN攻击时一种典型的DDOS攻击，检测SYN攻击的方式非常简单，即当Server上有大量半连接状态且源IP地址是随机的，则可以断定遭到SYN攻击了，使用如下命令可以让之现行：
​                \#netstat -nap | grep SYN_RECV

三、四次挥手
         三次握手耳熟能详，四次挥手估计就![img](http://blog.chinaunix.net/kindeditor/plugins/emoticons/images/20.gif)，所谓四次挥手（Four-Way Wavehand）即终止TCP连接，就是指断开一个TCP连接时，需要客户端和服务端总共发送4个包以确认连接的断开。在socket编程中，这一过程由客户端或服务端任一方执行close来触发，整个流程如下图所示：

![img](/Users/chenyansong/Documents/note/images/computeNetwork/22312037_1365503104wDR0.png)
图3 TCP四次挥手

​        由于TCP连接时全双工的，因此，每个方向都必须要单独进行关闭，这一原则是当一方完成数据发送任务后，发送一个FIN来终止这一方向的连接，收到一个FIN只是意味着这一方向上没有数据流动了，即不会再收到数据了，但是在这个TCP连接上仍然能够发送数据，直到这一方向也发送了FIN。首先进行关闭的一方将执行主动关闭，而另一方则执行被动关闭，上图描述的即是如此。
​        （1）第一次挥手：Client发送一个FIN，用来关闭Client到Server的数据传送，Client进入FIN_WAIT_1状态。
​        （2）第二次挥手：Server收到FIN后，发送一个ACK给Client，确认序号为收到序号+1（与SYN相同，一个FIN占用一个序号），Server进入CLOSE_WAIT状态。
​        （3）第三次挥手：Server发送一个FIN，用来关闭Server到Client的数据传送，Server进入LAST_ACK状态。
​        （4）第四次挥手：Client收到FIN后，Client进入TIME_WAIT状态，接着发送一个ACK给Server，确认序号为收到序号+1，Server进入CLOSED状态，完成四次挥手。
​        上面是一方主动关闭，另一方被动关闭的情况，实际中还会出现同时发起主动关闭的情况，具体流程如下图：

![img](/Users/chenyansong/Documents/note/images/computeNetwork/22312037_13655617062cGr.png)



# UDP协议

无连接，不可靠，由于UDP比较简单，UDP头包含很少的字节，比TCP负载消耗少，而且不需要建立连接，在传送数据较少的情况下，UDP比TCP更加高效

![image-20190901103647355](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190901103647355.png)



# ARP协议

ICMP是要进过IP封装，但是ARP是不需要进过IP封装的

![image-20190825082548831](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190825082548831.png)

ARP协议即：地址解析协议的缩写，所谓地址解析就是主机在发送数据帧前需要将目的IP地址转换成目的Mac地址的过程

![image-20190901105532082](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190901105532082.png)

```shell
网络设备通过ARP报文来发现目的MAC地址。ARP报文中包含以下字段：
1）Hardware Type：硬件地址类型，一般为以太网；
2）Protocol Type：表示三层协议地址类型，一般为IP；
3）Hardware Length和Protocol Length为MAC地址和IP地址的长度，单位是字节； （这个理论上可以不要，因为前面已经确定了硬件类型和协议类型）
4）Operation Code指定了ARP报文的类型，包括ARP request和ARP reply；
5）Source Hardware Address 指的是发送ARP报文的设备MAC地址；
6）Source Protocol Address指的是发送ARP报文的设备IP地址；
7）Destination Hardware Address指的是接收者MAC地址，在ARP request报文中，该字段值为0；
8）Destination Protocol Address指的是接受者的IP地址。

作者：廖马儿
链接：https://www.jianshu.com/p/e3a0f972ca4d
来源：简书
简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。
```



![image-20190901104448718](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190901104448718.png)

![image-20190901104651899](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190901104651899.png)



# ICMP协议

ICMP协议属于网络层协议，主要用于在主机和路由器之间传递控制信息，控制消息是指网络是否畅通，主机是否可达，路由是否可用等网络本身的消息

ICMP报文被封装在IP数据包内部传输，IP首部的协议字段值为1说明封装的是一个ICMP报文

![image-20190901105719929](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190901105719929.png)

tracert

![image-20190901110337901](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190901110337901.png)