[toc]

转自:http://blog.chinaunix.net/uid-28387257-id-3624060.html



本篇文章分析sack包格式，以及丢包有多个不连续数据段的sack
sack从TCP NewReno以后，得到诸多版本的协议支持。较早的TCP reno、TCP tahoe、TCP NewReno貌似是不支持SACK选项的。

### 一、SACK包格式

  SACK包主要有三个部分：ACK、SLE、SRE。
  ACK表示已经确认的包序号。
  SLE是收到乱序包的第一个字节，左边界；SRE是乱序包的最后一个字节加1，右边界。
  举例来说，如果收到的ACK序号是1460，SLE是2921，SRE是4382
  则表示，从1461--2920的数据包丢掉了。

  ![img](http://blog.chinaunix.net/attachment/201304/26/28387257_136696469888vw.png)
[
](http://blog.chinaunix.net/blog/downLoad/fileid/8366.html)  上图是用wireshark捕捉丢包的情况。
  可以看到，从83621到84681这一段的数据包丢掉了。
  而后面乱序的数据包不断抵达，因此SRE在不断向右移动。



### 二、多个乱序段的情况

  实际情况中较少见，因为这代表着隔三差五就有丢包，丢包率应该很大。
  就我们使用的有线链路而言，丢包率不足0.1%，无线链路大约在5%左右。【注 1】
  在RFC2018中详述了SACK包格式。
  有多个数据块乱序的情况下，SLE和SRE也可以有多组，但因为头部字节限制，最多为4组。



### sack选项

  选项类型: 5  选项长度: 可变，但整个TCP选项长度不超过40字节，实际最多不超过4组边界值。             +--------+--------+
             | Kind=5 | Length |
    +--------+--------+--------+--------+
    |   Left Edge of 1st Block    |
    +--------+--------+--------+--------+
    |   Right Edge of 1st Block   |
    +--------+--------+--------+--------+
    |                  |
    /      . . .         /
    |                  |
    +--------+--------+--------+--------+
    |   Left Edge of nth Block    |
    +--------+--------+--------+--------+
    |   Right Edge of nth Block   |
    +--------+--------+--------+--------+
注：
【1】无线链路的丢包率不确定，也有20%的情况

参考文献
【1】http://www.net130.com/CMS/Pub/softlevel/softlevel_networkengineer/2006_09_12_86547.htm

【2】http://www.ietf.org/rfc/rfc2018.txt