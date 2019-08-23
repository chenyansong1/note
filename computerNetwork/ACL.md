[TOC]



# ACL的类型

路由器上实现的安全

1. 标准的ACL：基于源地址进行控制
2. 扩展的ACL：基于源地址，目标地址，协议，端口号 进行控制



![image-20190823174313569](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190823174313569.png)



# 配置路由器的ACL

```shell
config t

#查看帮助
access-list ?
```

![image-20190823175418122](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190823175418122.png)

```shell
access-list 10 permit 192.168.1.0 0.0.0.255
access-list 10 permit 192.168.2.0 0.0.0.255

#删除所有
no access-list 10

#查看
show access-lists
#除了允许的网段，默认是都拒绝的
```

![image-20190823175806714](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190823175806714.png)

```shell
#绑定配置的ACL到路由器的指定接口
config t
interface serial 3/0
ip access-group 10 ?
```

![image-20190823180203559](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190823180203559.png)

```shell
#绑定出去的控制列表
ip access-group 10 out
```



现在配置允许网路中的一个IP不能通过

```shell
access-list 10 deny host 192.168.2.2
show access-lists
```

![image-20190823180835987](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190823180835987.png)

```shell
#重新设置
config t
no access-list 10
access-list 10 deny host 192.168.2.2
access-list 10 permit 192.168.1.0 0.0.0.255
access-list 10 permit 192.168.2.0 0.0.0.255
```

![image-20190823181053175](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190823181053175.png)



上面的配置是默认拒绝，现在配置为默认允许所有的，拒绝一些

```shell
access-list 20 deny 192.168.0.0 0.0.0.255
access-list 20 permit any
```



# 扩展的ACL

![image-20190823182505368](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190823182505368.png)

```shell
config t
#1-99是标准的，100是属于扩展的编号
access-list 100 permit ip 192.168.2.0 0.0.0.255 any

#查看允许的协议
access-list 100 permit ?
#如果是ip的话，没有端口
#如果是tcp/udp的话，需要带上port
```

![image-20190823182739493](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190823182739493.png)

```shell
#指定源地址的网段，目标地址的网段，端口80
access-list 100 permit tcp 192.168.1.0 0.0.0.255 10.0.0.0 0.255.255.255 eq 80

#access ping any target
access-list 100 permit icmp 192.168.0.0 0.0.0.255 any

#show access-list
```

![image-20190823183144732](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190823183144732.png)

绑定访问控制列表

```shell
config t
inerface serial 3/0
ip access-group 100 out
```

从网关返回说目标主机不可达，说明是出不去

![image-20190823183537911](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190823183537911.png)



# 使用ACL保护路由器的安全

![image-20190823184418810](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190823184418810.png)

配置路由器只允许指定的计算机能够远程Telnet访问

```shell
#设置路由器密码
config t
line vty 0 15
password aaa
login
exit

#创建访问控制列表
access-list 10 permit host 192.168.1.3 
#or
access-list 10 permit 192.168.1.3 0.0.0.0

#将ACL绑定到Telnet接口
line vty 0 15
access-class 10 in
```

