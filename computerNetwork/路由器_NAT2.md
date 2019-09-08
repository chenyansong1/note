[TOC]

# NAT的概念

* 网络地址转换（network address translation,NAT），主要作用于将私有地址转换为公有地址，由于现行IP地址标准---IPv4的限制，Internetmainline这IP地址空间短缺的问题，从ISP申请给企业的每位员工分配一个合法的IP地址是不显示的

* NAT不仅较好的解决了IP地址不足的问题，而且还能够有效的避免来自网络外部的攻击，隐藏并保护网络内部的计算机

* 私有IP地址

  A类：10.0.0.0-10.255.255.255

  B类：172.16.0.0-172.31.255.255

  C类：192.168.0.0-192.168.255.255

* NAT功能通常被集成路由器，防火墙，单独的NAT设备中



# NAT工作原理

![image-20190908115036710](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908115036710.png)



# NAT配置

## 动态NAT

1. 先确定NAT的内部接口和外部接口
2. 确定需要转换的网络



![image-20190908115454645](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908115454645.png)

## 静态NAT

![image-20190908120153452](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908120153452.png)

如果使用的是地址池

![image-20190908120543187](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190908120543187.png)