[TOC]

![image-20190811084720415](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190811084720415.png)

常见的协议

```shell
http 80
ftp 21 20
https 443
smtp	tcp 25	#发邮件
pop3	tcp 110  #收邮件
rdp	tcp 3389 #连接远程计算机
dns udp	53
IP访问Windows共享文件夹 tcp 445
Telnet	tcp 23
```

 测试端口是否通

```shell
telnet ip port
```

pathping查看实现路径跟踪

```shell
pathping www.baidu.com
```

![image-20190811134019184](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190811134019184.png)



ARP协议

将计算机的IP地址解析成MAC地址

```shell
#查看缓存的Mac地址
arp -a

#修改错误的Mac地址
arp -s 192.168.88.120 00-0c-29-47-ed
#然后我们再ping120,是ping不通的，此时需要清除ARP缓存
```

抓包工具

当我们发现网络拥塞的时候，可能是有人在发广播包(交换机会将广播发给所有的机器)，导致网络拥塞，此时我们需要抓包，找到发包者

![image-20190811142902612](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190811142902612.png)

从上图，我们可以知道是104这台机不停的在发广播，当然104这个地址也是可以自己修改的，如果我们随机的选择一个IP作为源IP发广播，我们还是不能定位真正的发送者，遇到这种情况需要拔交换机上的网线来确定是那台服务器在搞事情，如果现在交换机上有200台服务器，我们使用**二分法**，先拔100台服务器的网线，然后再50台...





