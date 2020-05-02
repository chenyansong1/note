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
首部长度(数据偏移)：该报文中 上层数据的 起始位置 (因为一个TCP报文中存在可选项，即首部并不是固定的)
```

 

## TCP报文格式

​        TCP/IP协议的详细信息参看《TCP/IP协议详解》三卷本。下面是TCP报文格式图：

![img](/Users/chenyansong/Documents/note/images/computeNetwork/22312037_1365321234nnNc.png)

图1 TCP报文格式

![](/Users/chenyansong/Documents/note/images/computeNetwork/image-20200501194513898.png)

报文的类型有如下的几种：

1. 数据报文：有携带上层数据
2. 确认报文：确认号和ACK有效，没有携带上层数据
3. 连接相关报文：**没有携带上层数据**
   1. RST重置连接
   2. SYN，FIN：发起和结束连接
4. 窗口



上图中有几个字段需要重点介绍下：
​        （1）序号：Seq序号，占32位，报文的第一个字符在整个发送数据的偏移
​        （2）确认序号：Ack序号，占32位，只有ACK标志位为1时，确认序号字段才有效，Ack=Seq+1。
​        （3）标志位：共6个，即URG、ACK、PSH、RST、SYN、FIN等，具体含义如下：
​                （A）URG：紧急指针（urgent pointer）有效。告诉系统此报文中有紧急数据，应尽快传送（相当于高优先级的数据）
​                （B）ACK：确认序号有效。
​                （C）PSH：接收方应该尽快将这个报文交给应用层。
​                （D）RST：重置连接。
​                （E）SYN：发起一个新连接。
​                （F）FIN：释放一个连接。

​        需要注意的是：
​                （A）不要将确认序号Ack与标志位中的ACK搞混了。
​                （B）确认方Ack=发起方Req+1，两端配对。 

## 停止等待协议ARQ

* 超时重传

  ![image-20200502162549668](/Users/chenyansong/Documents/note/images/computeNetwork/image-20200502162549668.png)



​		必须：

* 暂时保留已发送的分组的副本
* 分组和确认分组都必须进行编号
* 超时计时器的重传时间应当比数据在分组传输的平均往返时间更长一些



![](/Users/chenyansong/Documents/note/images/computeNetwork/image-20200502163103433.png)

情况三：对于重复的数据包，直接丢掉，但是还是会发一个确认给A

自动重传ARQ(Automatic Repeat reQuest)，ARQ表明重传的请求是自动进行的嗯，接收方不需要请求发送方重传某个出错的分组

ARQ特点：

* 优点是简单；缺点是信道利用率太低

  ![](/Users/chenyansong/Documents/note/images/computeNetwork/image-20200502164240666.png)

  ![image-20200502164423797](/Users/chenyansong/Documents/note/images/computeNetwork/image-20200502164423797.png)



如何提高信道利用率？流水线传输

![image-20200502164606526](/Users/chenyansong/Documents/note/images/computeNetwork/image-20200502164606526.png)



## 连续ARQ协议

* 发送窗口(及接收窗口)

  可连续发送而不需要等待确认的分组数

  ![](/Users/chenyansong/Documents/note/images/computeNetwork/image-20200502165815425.png)



* 累积确认

  ![image-20200502165922791](/Users/chenyansong/Library/Application Support/typora-user-images/image-20200502165922791.png)

  接收方不必对收到的分组逐个发送确认，而是对按序到达的最后一个分组发送确认，这样就表示：到这个分组为止的所有分组都已经正确收到了

  优点：实现容易，即使确认丢失也不必重传

  缺点：不能向发送方反映出接收方已经正确收到的所有分组的信息---go-back-N

* Go-back-N(回退N)

  如果发送方发送了前5个分组，而中间的第三个分组丢失了，这时接收方只能对前两个分组发出确认，发丧方无法知道后面三个分组的下落，而只好把后面的三个分组都再重传一次，这就是go-back-N (回退N)，表示需要再退回来重传已发送的N个分组

  **可见当通信线路质量不好时，连续ARQ协议会带来负面的影响**

* TCP可靠通信的具体实现

  1. 窗口大小的动态确定

     以**字节为单位**的滑动窗口

  2. 确定合理的重传时间

     ![](/Users/chenyansong/Documents/note/images/computeNetwork/image-20200502192738067.png)

     ![](/Users/chenyansong/Documents/note/images/computeNetwork/image-20200502192832905.png)

     ![](/Users/chenyansong/Documents/note/images/computeNetwork/image-20200502193003695.png)

     ![](/Users/chenyansong/Documents/note/images/computeNetwork/image-20200502193541291.png)

     ![](/Users/chenyansong/Documents/note/images/computeNetwork/image-20200502193727567.png)



* 发送缓存和接收缓存的作用

  发送缓存用来暂时存放：

  1. 发送应用程序发送给发送方TCP准备发送的数据
  2. TCP已发送出但尚未收到确认的数据

  接收缓存用来暂时存放：

  1. 按序到达的、但尚未被接收应用程序读取的数据
  2. 未按序到达的数据

  ![](/Users/chenyansong/Documents/note/images/computeNetwork/image-20200502194853578.png)

  ![](/Users/chenyansong/Documents/note/images/computeNetwork/image-20200502194953125.png)

> 注意：
>
> 1. 发送窗口并不等于接收窗口，可能还要考虑拥塞和网络的情况
> 2. 未按序到达的数据，先临时存放在接收窗口中
> 3. 连续发送，累积确认的机制



## 流量控制

让发送方的发送速率不要太快，既要让接收方来得及接收，也不要使网络发生拥塞，利用滑动窗口机制可以很方便的在TCP连接上实现流量控制，接收方通过改变接收窗口 “rwnd" 来控制发送方的发送窗口

![](/Users/chenyansong/Documents/note/images/computeNetwork/image-20200502202019572.png)

![](/Users/chenyansong/Documents/note/images/computeNetwork/image-20200502202051276.png)




## 拥塞控制

拥塞控制是控制全局，而流量控制是控制两端

在某段时间，若对网络中的某资源的需求超过了该资源所能提供的可用部分，网络的性能就要变坏---产生拥塞（congestion）

出现资源拥塞的条件：

​	对资源需求的总和  > 可用资源

若网络中有许多资源同时产生拥塞，网络的性能就要明显变坏，整个网络的吞吐量将随输入负荷的增大而下降



* 拥塞控制和流量控制的关系

  * 拥塞控制要防止过多的数据注入到网络中，是一个全局性的过程，涉及到所有的主机，所有的路由器，以及与降低网络传输性能有关的所有因素（全局）

  * 流量控制往往是指在给定的发送端和接收端之间的点对点通信量的控制，所要做的就是抑制发送端发送数据的速率，以便使接收端来得及接收（两端）

    ![image-20200502204338449](/Users/chenyansong/Documents/note/images/computeNetwork/image-20200502204338449.png)

拥塞控制方法举例

慢开始和拥塞避免

发送方维持一个车叫做拥塞窗口cwnd(congestion window)的状态变量

​	拥塞窗口的大小取决于网络的拥塞程度，并且动态的在变化，发送方让自己的发送窗口等于拥塞窗口，如再考虑接收方的接收能力，则发送窗口还可能小于拥塞窗口。Min{rwnd, cwnd}

​	发送方控制拥塞窗口的原则是：只要网络没有出现拥塞，拥塞窗口就再增大一些以便把更多的分组发送出去，但只要网络出现拥塞，拥塞窗口就要减小一些，以减少注入到网络中的分组数

1. 慢开始算法原理

![](/Users/chenyansong/Documents/note/images/computeNetwork/image-20200502210356621.png)

2. 拥塞避免算法

   ![](/Users/chenyansong/Documents/note/images/computeNetwork/image-20200502210605502.png)

   ![](/Users/chenyansong/Documents/note/images/computeNetwork/image-20200502210632289.png)

   ![image-20200502210722952](/Users/chenyansong/Documents/note/images/computeNetwork/image-20200502210722952.png)

   ![](/Users/chenyansong/Documents/note/images/computeNetwork/image-20200502210807057.png)

   ![image-20200502210917470](/Users/chenyansong/Documents/note/images/computeNetwork/image-20200502210917470.png)



> "拥塞避免"并非指完全能够避免了拥塞，利用以上的措施要完全避免网络拥塞还是不可能的
>
> “拥塞避免” 是说在拥塞避免阶段把拥塞窗口控制为按线性规律增长，使网络比较不容易出现拥塞




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