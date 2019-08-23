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

