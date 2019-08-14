[TOC]

# wireshark 开始抓包

wireshark是捕获机器上的某一块网卡的网络包，当你的机器上有多块网卡的时候，你需要选择一个网卡。 
点击Caputre->Interfaces.. 出现下面对话框，选择正确的网卡。然后点击”Start”按钮, 开始抓包 

![1565752291423](E:\git-workspace\note\images\computeNetwork\1565752291423.png)

### Wireshark 窗口介绍

![这里写图片描述](E:\git-workspace\note\images\docker\20171208163430300.png)

WireShark 主要分为这几个界面 
1. Display Filter(显示过滤器)， 用于过滤 
2. Packet List Pane(封包列表)， 显示捕获到的封包， 有源地址和目标地址，端口号。 颜色不同，代表 
3. Packet Details Pane(封包详细信息), 显示封包中的字段 
4. Dissector Pane(16进制数据) 
5. Miscellanous(地址栏，杂项)

### Wireshark 显示过滤

![这里写图片描述](E:\git-workspace\note\images\docker\20171208163750867.png)

使用过滤是非常重要的， 初学者使用wireshark时，将会得到大量的冗余信息，在几千甚至几万条记录中，以至于很难找到自己需要的部分。搞得晕头转向。 
过滤器会帮助我们在大量的数据中迅速找到我们需要的信息。 
过滤器有两种， 

1. 一种是显示过滤器，就是主界面上那个，用来在捕获的记录中找到所需要的记录 
2. 一种是捕获过滤器，用来过滤捕获的封包，以免捕获太多的记录。 在Capture -> Capture Filters 中设置

### 保存过滤

在Filter栏上，填好Filter的表达式后，点击Save按钮， 取个名字。比如”Filter hls”, 

![这里写图片描述](E:\git-workspace\note\images\docker\20171208164629274.png)


Filter栏上就多了个”Filter hls” 的按钮。 ![这里写图片描述](E:\git-workspace\note\images\docker\20171208164716529.png)

## 过滤表达式的规则

表达式规则 

1. 协议过滤 
   比如TCP，只显示TCP协议。 
2. IP 过滤 
   比如 ip.src ==192.168.1.102 显示源地址为192.168.1.102， 
   ip.dst==192.168.1.102, 目标地址为192.168.1.102 
3. 端口过滤 
   tcp.port ==80, 端口为80的 
   tcp.srcport == 80, 只显示TCP协议的愿端口为80的。 
4. Http模式过滤 
   http.request.method==”GET”, 只显示HTTP GET方法的。 
5. 逻辑运算符为 AND/ OR

## 封包列表(Packet List Pane)

封包列表的面板中显示，编号，时间戳，源地址，目标地址，协议，长度，以及封包信息。 你可以看到不同的协议用了不同的颜色显示。 
你也可以修改这些显示颜色的规则， View ->Coloring Rules. 
![这里写图片描述](E:\git-workspace\note\images\docker\20171208165046952.png)

## 封包详细信息 (Packet Details Pane)

这个面板是我们最重要的，用来查看协议中的每一个字段。 
各行信息分别为 
Frame: 物理层的数据帧概况 
Ethernet II: 数据链路层以太网帧头部信息 
Internet Protocol Version 4: 互联网层IP包头部信息 
Transmission Control Protocol: 传输层T的数据段头部信息，此处是TCP 
Hypertext Transfer Protocol: 应用层的信息，此处是HTTP协议 
![这里写图片描述](E:\git-workspace\note\images\docker\20171208165400201.png)

## wireshark与对应的OSI七层模型

![这里写图片描述](E:\git-workspace\note\images\docker\20171208165634035.png)

# TCP包的具体内容

从下图可以看到wireshark捕获到的TCP包中的每个字段。 
![这里写图片描述](E:\git-workspace\note\images\docker\20171208165702355.png)

# 实例分析TCP三次握手过程

看到这， 基本上对wireshak有了初步了解， 现在我们看一个TCP三次握手的实例 
三次握手过程为 
![这里写图片描述](E:\git-workspace\note\images\docker\20171208165740999.png)

在wireshark中输入http过滤， 然后选中GET /zijian.hls.video.qq.com的那条记录，右键然后点击”Follow TCP Stream”, 
这样做的目的是为了得到与浏览器打开网站相关的数据包，将得到如下图 
![这里写图片描述](E:\git-workspace\note\images\docker\20171208172103278.png)

图中可以看到wireshark截获到了三次握手的三个数据包。第四个包才是HTTP的， 这说明HTTP的确是使用TCP建立连接的。

## 第一次握手数据包

客户端发送一个TCP，标志位为SYN，序列号为0， 代表客户端请求建立连接。 如下图 
![这里写图片描述](E:\git-workspace\note\images\docker\20171208172851923.png)

## 第二次握手的数据包

服务器发回确认包, 标志位为 SYN,ACK. 将确认序号(Acknowledgement Number)设置为客户的I S N加1以.即0+1=1, 如下图 
![这里写图片描述](E:\git-workspace\note\images\docker\20171208172934945.png)

## 第三次握手的数据包

客户端再次发送确认包把服务器发来ACK的序号字段+1,放在确定字段中发送给对方.并且在数据段放写ISN的+1, SYN标志位为1,ACK标志位为1. 如下图: 
![这里写图片描述](E:\git-workspace\note\images\docker\20171208173006339.png)

就这样通过了TCP三次握手，建立了连接

# 常用到的过滤器规则

* 捕捉过滤器（CaptureFilters）：用于决定将什么样的信息记录在捕捉结果中。需要在开始捕捉前设置
* 显示过滤器（DisplayFilters）：在捕捉结果中进行详细查找。他们可以在得到捕捉结果后随意修改

## 捕捉过滤器

​	捕捉前**依据协议**的相关信息进行过滤设置

### 协议过滤语法

| 语法   | Protocol | Direction | Host(s) | Value | Logical Operations | Other expression|
| ------ | ------------ | ------------- | ----------- | --------- | ---------------------- | --------------------- |
| 例子： | tcp          | dst           | 10.1.1.1    | 80        | and                    | tcp dst 10.2.2.2 3128 |

示例：

```shell
(host 10.4.1.12 or src net 10.6.0.0/16) and tcp dst portrange 200-10000 and dst net 10.0.0.0/8
```


捕捉IP为10.4.1.12或者源IP位于网络10.6.0.0/16，目的IP的TCP端口号在200至10000之间，并且目的IP位于网络 10.0.0.0/8内的所有封包

* Protocol（协议）

  可能值: ether, fddi, ip, arp, rarp, decnet, lat, sca, moprc, mopdl, tcp and udp.
如果没指明协议类型，则默认为捕捉所有支持的协议。
  注：在wireshark的HELP-Manual Pages-Wireshark Filter中查到其支持的协议
  
  **如果没有特别指明是什么协议，则默认使用所有支持的协议。**
  
* Direction（方向）:

  可能的值: src, dst, src and dst, src or dst

  **如果没有特别指明来源或目的地，则默认使用 “src or dst” 作为关键字**

  例如，”host 10.2.2.2″与”src or dst host 10.2.2.2″是一样的。

* Host(s):

  可能的值： net, host, port, portrange.

  ```shell
net 10.6.0.0/16
  host 10.130.10.22
  
  port 80
  portrange 200-10000
  ```
  
  
  
  如果没有指定此值，则默认使用”host”关键字。
  
  例如，”src 10.1.1.1″与”src host 10.1.1.1″相同。

* Logical Operations（逻辑运算）:

  可能的值：not, and, or.

  **否(“not”)具有最高的优先级**。或(“or”)和与(“and”)具有相同的优先级，运算时从左至右进行。

  例如

  ```shell
  “not tcp port 3128 and tcp port 23″与”(not tcp port 3128) and tcp port 23″相同
  
  “not tcp port 3128 and tcp port 23″与”not (tcp port 3128 and tcp port 23)”不同
  ```




## 显示过滤器

对捕捉到的数据包依据协议或包的内容进行过滤

### 协议过滤语法

| 语法    | Protocol | .    | string1 | .    | string2 | comparsion operator | value  | logical Operations | Other expression |
| ------- | -------- | ---- | ------- | ---- | ------- | ------------------- | ------ | ------------------ | ---------------- |
| example | http     | .    | request |      | method  | ==                  | "POST" | or                 | icmp.type        |


string1和string2是可选的

* 按协议进行过滤

  ```shell
  #显示SNMP或DNS或ICMP封包
  snmp || dns || icmp	
  ```

* 按协议的属性值进行过滤

  ```shell
  ip.addr == 10.1.1.1
  #
  ip.src != 10.1.2.3 or ip.dst != 10.4.5.6
  #显示来自10.230网段的封包。
  ip.src == 10.230.0.0/16
  #显示来源或目的TCP端口号为25的封包
  tcp.port == 25	
  #显示目的TCP端口号为25的封包
  tcp.dstport == 25	
  #显示post请求方式的http封包
  http.request.method== "POST"	
  #显示请求的域名为tracker.1ting.com的http封包
  http.host == "tracker.1ting.com"
  #显示包含TCP SYN标志的封包
  tcp.flags.syn == 0×02	
  ```

### 内容过滤语法

* 深度字符串匹配

  contains ：Does the protocol, field or slice contain a value

示例

```shell
#显示payload中包含"http"字符串的tcp封包。
tcp contains "http"	
#显示请求的uri包含"online"的http封包
http.request.uri contains "online"	
```

* 特定偏移处值的过滤

  ```shell
  #16进制形式，tcp头部一般是20字节，所以这个是对payload的前三个字节进行过滤
  tcp[20:3] == 47:45:54 
  http.host[0:4] == "trac"
  ```

过滤中函数的使用（upper、lower）

```shell
upper(string-field) - converts a string field to uppercase
lower(string-field) - converts a string field to lowercase
#示例
upper(http.request.uri) contains "ONLINE"
```

wireshark过滤支持比较运算符、逻辑运算符，内容过滤时还能使用位运算。
如果过滤器的语法是正确的，表达式的背景呈绿色。**如果呈红色，说明表达式有误**