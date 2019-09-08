[TOC]



# NAT的优缺点

地址转化技术

当内网的IP方外外网的地址的时候，通过将内网IP地址变成路由器的外网IP地址，从而实现IP地址转换，回来的时候，还需要查找对应的内网IP，再转换回来

优点

1. 内网能够主动访问外网，外网不能主动访问内网，内网相对安全
2. 通过地址转换，节省公网IP地址

缺点

需要路由器来回修改地址，慢



# NAT和PAT

NAT：是一个内网IP对应路由器上的一个公网地址，不节省公网IP

PAT：端口地址转换，将源端口在路由器出去之前替换，节省公网IP



# 地址转化的类型

* 静态NAT：一一对应，管理员已经配置好了

  ![image-20190823221557147](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190823221557147.png)

* 动态NAT

  ![image-20190823221648011](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190823221648011.png)



* PAT：修改了源地址和源端口，路由器就需要维护一张下面的表

  ![image-20190823221822340](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190823221822340.png)



# 配置PAT

![image-20190824131240640](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190824131240640.png)

配置了CPE这个路由器的默认路由为131.107.0.254，这样内网就可以出去了，但是如果使用一台内网的计算机（10.0.0.4）去ping 外网的IP(如：202.99.160.3)是不通的，因为内网到外网是可以出去的（因为配置了默认路由），但是数据包回来的时候，是回不来的，因为回来的目的地址是10.0.0.4，但是路由器ISP不知道到10网段怎么走，所以数据包是回不去的

我们在路由器ISP上执行debug也是可以看到的

```shell
en
#开始IP数据包转发的显示
debug ip packet
```

![image-20190824132129045](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190824132129045.png)



就是因为路由器CPE没有地址转换，导致内网的IP地址出来的没转换成路由器CPE的公网地址，所以数据包回不来



配置地址转换

```shell
CPE#
CPE#config t
#定义内网有哪些网段
CPE(config)#access-list 10 permit 10.0.0.0 0.0.0.255

#定义公网地址池:需要制定公网地址的第一个和最后一个地址
CPE(config)#ip nat pool publicAdd 131.107.0.1 131.107.0.1 netmask 255.255.255.0

#将内网和公网绑定
#inside source list 10 ： 是上面定义的访问控制列表
#pool publicAdd ：是指定地址池
#overload：表示PAT
ip nat inside source list 10 pool publicAdd overload

#配置内端口和外端口
interface serial 0/0
ip nat outside
exit
interface fastEthernet 0/1
ip nat inside 
```

这样以后，内网就可以ping通外面的公网地址了



在CPE路由器上debug

```shell
debug ip nat
```

![image-20190824133744308](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190824133744308.png)



# 端口映射

外面的人想要访问内网的服务，但是外面的人只能访问到公司的路由器的外网IP，端口映射允许你使用公网地址访问内网服务器

路由器配置端口映射

```shell
en
config t
#131.107.0.1 80 -> 10.0.0.6 80
ip nat inside source static tcp 10.0.0.6 80 131.107.0.1 80

#配置网卡所属的内外网
interface serial 0/0
ip nat outside

interface fastEthernet 0/1
ip nat inside 

#测试
curl http://131.107.0.1:80
```





