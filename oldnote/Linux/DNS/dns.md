[TOC]



DNS : Domain Name service 域名服务

域名：www.baidu.com(主机名，FQDN：full Qualified Domain Name 完全限定域名)；baidu.com(域名)

DNS：名称解析（name resolving)



```
hosts:files dns

file:/etc/hosts
dns:DNS

ping www.baidu.com
首先会找/etc/hosts中是否有对应的IP，如果没有，那么去DNS中寻找对应的IP

hosts文件的格式：
	IPaddress 	FQDN			Aliases
	172.16.0.1	www.baidu.com	xxx
	
随着主机数量的增多，这样文件中的IP和主机的映射会越来越大，不好管理，于是就出现了DNS
```



一般我们寻找一个人的过程如下：

中国.上海.徐汇.西藏路.35.小明，这样我们就定位到了一个人，但是老外的写法是喜欢，从后向前写，小明.35.西藏路.徐汇.上海.中国，类似的，我们的域名也是这样，比如：www.baidu.com. (**注意后面有一个点**)，首先是最后面的一个点（表示根服务器），然后就是com(顶级域)，再然后是baidu，最后才是www。如下图：

![image-20181010220141859](https://github.com/chenyansong1/note/blob/master/images/linux/dns/dns-1.png?raw=true)



* 资源记录的格式

```
#格式
name 	[ttl]		in 		rrt(资源记录类型)		value
www.baidu.com.		in		A					1.1.1.1
1.1.1.1				in		PTR					www.baidu.com.


#资源记录类型
SOA(Start Of Authority 起始授权记录，用来标明主从服务器之间如何同步数据，以及起始授权的对象是谁)：
	zone name TTL	IN		SOA		FQDN  ADMINISTRATOR_MAILBOX	(
                                                                serial number
                                                                refresh	刷新时间
                                                                retry	重试次数
                                                                expire	重试时间
                                                                na ttl	否定回答的ttl值
                                                            	)
	时间单位：M(分钟)，H(小时),D(天)， W(周)，默认是 秒
	邮箱格式：admin@baidu.com	-写成->	admin.baidu.com
    
    baidu.com.	600	IN	SOA		ns1.baidu.com.	admin.baidu.com.	(
    															2018090101
    															1H
    															5M
    															1W
    															1D
    																)
	
	
	
NS(name server [DNS]):	domain name (zone name) -> FQDN
					baidu.com.	600	IN	NS	ns.baidu.com.
					ns.baidu.com.	600	IN	A	1.1.1.1
					
MX(Mail eXchanger 邮件): zone name -> FQDN
				zone name 	TTL		IN		MX	pri(优先级0-99)		value
				baidu.com.	600		IN		MX	10			mail.baidu.com.
				mail.baidu.com.	600	IN		A		1.1.1.3
				
A(address):			FQDN	-> 	IPv4		A记录
AAAA:				FQDN	->	IPv6		A记录
PTR(pointer):		IP  -> 	FQDN		指针记录
CNAME(Canonical NAME 正式名称，通常用来标明别名):	FQDN -> FQDN 
		www2.baidu.com.		IN		CNAME	www.baidu.com



#域(Domain) 区域（Zone）的区别
域：
区域：

.com服务器的DNS解析如下：
magedu.com.		IN	NS	ns.magedu.com.
ns.magedu.com.	IN	A	192.168.0.10

magedu.com.		服务器的解析如下(所在网段为：192.168.0.0/24)
www		192.168.0.1		A	..
mail	192.168.0.2		MX	..

此时在服务器上需要建立两个区域文件：
正向区域文件：
magedu.com.		IN	SOA	....  #第一条一定是SOA
www.magedu.com.	IN	A	192.168.0.1	
简写为：
www				IN	A	192.168.0.1			#www后面可以通过 magedu.com. 补全

反向区域文件：
0.168.192.in-addr.arpa.		IN	SOA	.... #第一条一定是SOA ,并且网络位反写
192.168.0.1		IN	PTR		www.magedu.com.

1.0.168.192.in-addr.arpa.	IN	PTR		www.magedu.com.
简写
1							IN	PTR		www.magedu.com.


mx 只能定义在正向区域文件中
ns正向，反向
A记录只能定义在正向
prt记录只能定义在反向



# 区域传送(当主DNS上有改变的时候，需要及时的通知从DNS服务器)
	完全区域传送 (全量同步)
	增量区域传送(部分同步)
区域类型
	主区域(Master)
	从区域(slave)
	提示区域(hint 定义根在什么地方)
	转发区域(直接配置的区域)

```



## SRV

参见：https://www.lijiaocn.com/技巧/2017/03/06/dns-srv.html

DNS SRV是DNS记录中一种，用来指定服务地址。与常见的A记录、cname不同的是，SRV中除了记录服务器的地址，**还记录了服务的端口**，并且可以设置每个服务地址的优先级和权重。访问服务的时候，本地的DNS resolver从DNS服务器查询到一个地址列表，根据优先级和权重，从中选取一个地址作为本次请求的目标地址

### 用场景

一个能够支持SRV的LDAP client可以通过查询域名，得知LDAP服务的IP地址和服务端口：

```
_ldap._tcp.example.com
```

这个域名的格式是rfc-2782中推荐的格式，_ldap表示LDAP服务，_tcp表示通过TCP协议访问LDAP服务。

### SRV格式

SRV的DNS类型代码为33。

SRV的记录格式为：

```
_Service._Proto.Name TTL Class SRV Priority Weight Port Target

Service: 服务名称，前缀“_”是为防止与DNS Label（普通域名）冲突。
Proto:   服务使用的通信协议，_TCP、_UDP、其它标准协议或者自定义的协议。
Name:    提供服务的域名。
TTL:     缓存有效时间。
CLASS:   类别
Priority: 该记录的优先级，数值越小表示优先级越高，范围0-65535。
Weight:   该记录的权重，数值越高权重越高，范围0-65535。     
Port:     服务端口号，0-65535。
Target:   host地址。
```



# bind

bind

​	/etc/named.conf