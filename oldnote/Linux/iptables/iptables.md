[TOC]





![img](/Users/chenyansong/Documents/note/images/linux/iptables/seven-layer.png)



通信过程中，每层协议都要加上一个数据首部（header），称为封装（Encapsulation），如下图所示



![img](/Users/chenyansong/Documents/note/images/linux/iptables/header-layer.png)



# IP报文头



![image-20181017200226974](/Users/chenyansong/Documents/note/images/linux/iptables/ip-layer.png)

![img](/Users/chenyansong/Documents/note/images/linux/iptables/ip-layer2.png)



Fragment ID :段标识，如果一个数据很大的时候，会分段发送，所以段标识相同的报文是同一个报文

MF:more Fragment:是否分段

DF: Don't Frament 不允许分片

Frament Offset:如果一个报文分成了3段发送，此时3个段就会有一个offset，用来标识3段的排序，如果偏移为0，则为第一个段，如果偏移为100，则假设为第2个段，依次类推

TTL : 生存时间，每经过一个路由，减一

Protocol：协议（字段指出此数据报所携带上层数据使用的TCP协议还是UDP协议，以便对等层接收到数据报交给上层相应的协议（TCP或者UDP协议）进行处理；)

常用网际协议编号：

![img](/Users/chenyansong/Documents/note/images/linux/iptables/ip-procol.png)



Header CheckSum: 首部校验和，占用16位二进制数，用于协议头数据有效性的校验，可以保证IP报头区在传输时的正确性和完整性。头部检验和字段是根据IP协议头计算出的检验和，它不对头部后面的数据进行计算。

# TCP报文头

![image-20181017200410479](/Users/chenyansong/Documents/note/images/linux/iptables/tcp-layer.png)



![img](/Users/chenyansong/Documents/note/images/linux/iptables/tcp-layer2.png)



* 源端口和目的端口：各占2个字节，分别写入源端口和目的端口；
* 序号：占4个字节，TCP连接中传送的字节流中的每个字节都按顺序编号。例如，一段报文的序号字段值是 301 ，而携带的数据共有100字段，显然下一个报文段（如果还有的话）的数据序号应该从401开始；
* 确认号：占4个字节，是期望收到对方下一个报文的第一个数据字节的序号。例如，B收到了A发送过来的报文，其序列号字段是501，而数据长度是200字节，这表明B正确的收到了A发送的到序号700为止的数据。因此，B期望收到A的下一个数据序号是701，于是B在发送给A的确认报文段中把确认号置为701；
* 数据偏移，占4位，它指出TCP报文的数据距离TCP报文段的起始处有多远；
* 保留，占6位，保留今后使用，但目前应都位0；
* 紧急URG，当URG=1，表明紧急指针字段有效。告诉系统此报文段中有紧急数据；

* 确认ACK，仅当ACK=1时，确认号字段才有效。TCP规定，在连接建立后所有报文的传输都必须把ACK置1；
* 推送PSH，当两个应用进程进行交互式通信时，有时在一端的应用进程希望在键入一个命令后立即就能收到对方的响应，这时候就将PSH=1；
* 复位RST，当RST=1，表明TCP连接中出现严重差错，必须释放连接，然后再重新建立连接；
* 同步SYN，在连接建立时用来同步序号。当SYN=1，ACK=0，表明是连接请求报文，若同意连接，则响应报文中应该使SYN=1，ACK=1；
* 终止FIN，用来释放连接。当FIN=1，表明此报文的发送方的数据已经发送完毕，并且要求释放；
* 窗口，占2字节，指的是通知接收方，发送本报文你需要有多大的空间来接受；
* 检验和，占2字节，校验首部和数据这两部分；
* 紧急指针，占2字节，指出本报文段中的紧急数据的字节数；
* 选项，长度可变，定义一些其他的可选的参数。





参考：https://www.cnblogs.com/lifan3a/articles/6649970.html

https://blog.csdn.net/qzcsu/article/details/72861891



## tcp三次握手



![image-20181018194913463](/Users/chenyansong/Documents/note/images/linux/iptables/tcp-thread.png)



1. TCP服务器进程先创建传输控制块TCB，时刻准备接受客户进程的连接请求，此时服务器就进入了LISTEN（监听）状态；
2. TCP客户进程也是先创建传输控制块TCB，然后向服务器发出连接请求报文，这是报文首部中的同部位SYN=1，同时选择一个初始序列号 seq=x ，此时，TCP客户端进程进入了 SYN-SENT（同步已发送状态）状态。TCP规定，SYN报文段（SYN=1的报文段）不能携带数据，但需要消耗掉一个序号。
3. TCP服务器收到请求报文后，如果同意连接，则发出确认报文。确认报文中应该 ACK=1，SYN=1，确认号是ack=x+1，同时也要为自己初始化一个序列号 seq=y，此时，TCP服务器进程进入了SYN-RCVD（同步收到）状态。这个报文也不能携带数据，但是同样要消耗一个序号。
4. TCP客户进程收到确认后，还要向服务器给出确认。确认报文的ACK=1，ack=y+1，自己的序列号seq=x+1，此时，TCP连接建立，客户端进入ESTABLISHED（已建立连接）状态。TCP规定，ACK报文段可以携带数据，但是如果不携带数据则不消耗序号。
5. 当服务器收到客户端的确认后也进入ESTABLISHED状态，此后双方就可以开始通信了。 





## tcp四次断开



![image-20181018195743160](/Users/chenyansong/Documents/note/images/linux/iptables/tcp-four.png)

>  为什么客户端最后还要等待2MSL？

MSL（Maximum Segment Lifetime），TCP允许不同的实现可以设置不同的MSL值。

第一，保证客户端发送的最后一个ACK报文能够到达服务器，因为这个ACK报文可能丢失，站在服务器的角度看来，我已经发送了FIN+ACK报文请求断开了，客户端还没有给我回应，应该是我发送的请求断开报文它没有收到，于是服务器又会重新发送一次，而客户端就能在这个2MSL时间段内收到这个重传的报文，接着给出回应报文，并且会重启2MSL计时器。

第二，防止类似与“三次握手”中提到了的“已经失效的连接请求报文段”出现在本连接中。客户端发送完最后一个确认报文后，在这个2MSL时间中，就可以使本连接持续的时间内所产生的所有报文段都从网络中消失。这样新的连接中不会出现旧连接的请求报文。

为什么建立连接是三次握手，关闭连接确是四次挥手呢？

建立连接的时候， 服务器在LISTEN状态下，收到建立连接请求的SYN报文后，把ACK和SYN放在一个报文里发送给客户端。 
而关闭连接时，服务器收到对方的FIN报文时，仅仅表示对方不再发送数据了但是还能接收数据，而自己也未必全部数据都发送给对方了，所以己方可以立即关闭，也可以发送一些数据给对方后，再发送FIN报文给对方来表示同意现在关闭连接，因此，己方ACK和FIN一般都会分开发送，从而导致多了一次。



## tcp的有限状态机

| TCP includes 11 states, they are: |
| --------------------------------- |
| LISTEN                            |
| SYN_SENT                          |
| SYN_RECV                          |
| ESTABLISHED                       |
| FIN_WAIT1                         |
| CLOSE_WAIT                        |
| FIN_WAIT2                         |
| LAST_ACK                          |
| TIME_WAIT                         |
| CLOSED                            |
| CLOSING                           |



### 状态视图

* 服务器端

![img](/Users/chenyansong/Documents/note/images/linux/iptables/tcp-status.png)

* 客户端





![img](/Users/chenyansong/Documents/note/images/linux/iptables/tcp-status2.png)

* 总的状态图



![img](/Users/chenyansong/Documents/note/images/linux/iptables/tcpfsm.png)



# 防火墙



工作在主机或者是网络的**边缘**，对于进出的数据报文能够根据**事先定义好的规则**，做出相应的处理的组件，称之为网络防火墙或者主机防火墙.

 `

规则：匹配标准

​	IP：SIP， DIP

​	TCP：SPort，DPort  	 SYN=1,FIN-0,RST=0,ACK=0(TCP三次握手中的第一次)；SYN=1,ACK=1,FIN=0,RST=0(TCP三次握手中的第2次);ACK=1,FIN=0,SYN=0,RST=0(establish 以建立连接)

​	UDP：SPort，DPort

​	ICMP: icmp-type



数据报文过滤

Linux2.2

​	ipchain(用户空间命令)/firewall（内核空间命令）

Linux2.4

​	iptables(用户空间命令)/netfilter（内核空间命令）



![image-20181018234248087](/Users/chenyansong/Documents/note/images/linux/iptables/sys-user.png)



在用户空间中有（iptables,或者是ipchain)这样一个工具，我们可以使用这样的工具编写规则，然后将规则刷新到内核空间中，netfilter会根据规则达到防火墙的目的



hook function:5个钩子函数

* prerouting 刚到达本机

* input：进入本机（1）
* output:从本机出去（2）
* forward从本机转发（3）
* postrouting 从本机发送出去



![image-20181020090657434](/Users/chenyansong/Documents/note/images/linux/iptables/hook.png)



把访问频繁的请求放在上面，避免过多的检查（如web服务）

通常在一个数据报文送达到网卡之后，网卡会将数据报文送达到路由表进行路由决策，但是在网卡上也有一个**钩子函数**，用于路由之前的修改数据报文的内容，同样在数据报文通过网卡发送出去之前，我们也是可以修改数据报文的内容，这里也是对应一个钩子函数。

![image-20181020091741755](/Users/chenyansong/Documents/note/images/linux/iptables/hook2.png)



在网卡的出口和入口位置，我们都可以对数据报文的内容进行修改，如：实现地址转换的功能



规则链：每一个规则函数上提供的规则，将组成一个链，所以就有了下面这5个规则链

​	PREROUTING

​	INPUT

​	FORWARD

​	OUTPUT

​	POSTROUTING

功能：

filter（过滤）：这里每个链都对应一个列，每个链内部又有多行，所以组成了 **表**

​	INPUT 能不能到达本机

​	OUTPUT 能不能从本机发出

​	FORWARD 能不能经由本机转发

nat(地址转换)：这里每个链都对应一个列，每个链内部又有多行，所以组成了 **表**

​	PREROUTING

​	OUTPUT

​	POSTROUTING

mangle(修改报文首部):表

​	修改ttl值等

​	PREROUTING

​	INPUT

​	OUTPUT

​	FORWARD

​	POSTROUTING

raw(原始格式)：表

​	不做任何修改

​	PREROUTING

​	OUTPUT



![image-20181020133158472](/Users/chenyansong/Documents/note/images/linux/iptables/chains-type.png)



这就是所谓的 **四表五链**



自定义链

可以使用自定义链，但只在被调用时才能发挥作用，而且如果没有被任何一条规则匹配到，还应该有返回机制,用户可以删除自定义的空链，默认链不能被删除



# 规则



每一条规则都有两个内置的计数器

​	被匹配到的报文个数

​	被匹配的报文体积之和



规则：是由 **匹配标准** 和 **处理动作** 组成

iptable [-t TABLE]  COMMAND  CHAIN [num] 匹配标准  -j 处理办法



## 匹配标准

* 通用匹配

| 匹配标准                | 说明                                                         |
| ----------------------- | ------------------------------------------------------------ |
| -s , —src               | 指定源地址                                                   |
| -d , —dst               | 指定目标地址                                                 |
| -p {tcp \| udp \| icmp} | 指定协议                                                     |
| -i  interface           | 指定数据报文流入的接口（从哪个网卡进入的），在prerouting，input, forward中使用才有意义 |
| -o interface            | 指定数据报文流出的接口（从哪个网卡出去的），在postrouting,output,forward中使用才有意义 |

* 扩展匹配（调用netfilter的扩展模块）

​		隐含扩展：不用特别指定由哪个模块进行扩展 ，因为此时使用了 -p(tcp|udp|icmp)

​		显式扩展：必须指明由哪个模块进行的扩展，在iptable中使用-m选项可完成此功能



## 处理动作

-j target

| TARGET |                                            |
| ------ | ------------------------------------------ |
| ACCEPT | 接收                                       |
| DROP   | 悄悄拒绝                                   |
| REJECT | 明确拒绝，测试目的建议使用，一般不建议使用 |



```
#来自172.16这个网段的地址，都拒绝掉(drop)
iptable  -t filter -A INPUT -s 172.16.0.0/16 -j DROP


#来自172.16这个网段的地址，且访问 172.16.100.7 的主机都拒绝掉(drop)
iptable  -t filter -A INPUT -s 172.16.0.0/16 -d 172.16.100.7 -j DROP

```

​	

## 命令command



管理规则

-A :附加一条规则，在链的尾部追加，

-I chain [num] : 插入一条规则，指定插入到chain链上的第num条的位置 ，省略num则为第一条

-D chain [num] : 



管理链

​	 

​	