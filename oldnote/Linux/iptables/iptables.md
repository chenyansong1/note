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

1）序号和确认号：是TCP可靠传输的关键部分。序号是本报文段发送的数据组的第一个字节的序号。在TCP传送的流中，每一个字节一个序号。e.g.一个报文段的序号为300，此报文段数据部分共有100字节，则下一个报文段的序号为400。所以序号确保了TCP传输的有序性。确认号，即ACK，指明下一个期待收到的字节序号，表明该序号之前的所有数据已经正确无误的收到。确认号只有当ACK标志为1时才有效。比如建立连接时，SYN报文的ACK标志位为0。

TCP段首部的定长部分为20个字节，即5个单位的长度。

URG位：紧急标志，和紧急指针配合使用，当其为1时表示，此报文要尽快传送。

ACK位：确认标志，和确认号字段配合使用，当ACK位置1时，确认号字段有效。

PSH位：为推送标志，置1时，发送方将立即发送缓冲区中的数据,送给内核

RST位：复位标志，置1时，表明有严重差错，必须释放连接。

SYN位： 同步标志，置1时，表示请求建立连接。

FIN位：终止标志，置1时，表明数据已经发送完，请求释放连接。

窗口大小：32bit，用于向对方通告当前本机的接受缓冲区的大小。

校验和字段长度：16bit，校验范围包括段首部、数据以及伪首部。





参考：https://www.cnblogs.com/lifan3a/articles/6649970.html



