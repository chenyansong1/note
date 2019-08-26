[TOC]



# 交换机配置文件的上传和下载

一般需要将交换机的配置文件备份，这里将交换机的配置文件保存到tftp服务器上

```shell
#配置交换机的IP地址
config t
#交换机在出厂的时候，默认情况下，所有的端口都属于VLAN1
Switch(config)#interface vlan 1
#or
Switch(config)#int vlan 1
#给VLAN1配置IP地址，就是给交换机配置了管理IP
Switch(config-if)#ip address 192.168.1.1 255.255.255.0
Switch(config-if)#no shutdown


Switch#
```

![image-20190826220848615](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190826220848615.png)

![image-20190826220908969](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190826220908969.png)



现在假设交换机上的配置文件丢失了，需要通过备份的配置文件恢复

1. 还是给交换机配置一个IP地址，保证交换机和TFTP服务器之间能够ping通

   ![image-20190826221042851](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190826221042851.png)

2. 从TFTP服务器恢复配置

   ![image-20190826221240027](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190826221240027.png)

3. 同理，我们也可以备份startup-config，同时也可以copy TFTP到running-config



# IOS的备份和升级

```shell
#查看交换机的操作系统
Switch#show flash
```

![image-20190826221528203](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190826221528203.png)

备份交换机的操作系统

```shell
Switch#copy flash tftp
```

![image-20190826221737230](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190826221737230.png)

![image-20190826221834360](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190826221834360.png)

恢复操作系统

```shell
copy tftp flash
```

升级操作系统

1. copy新的操作系统到flash中

   ![image-20190826222132598](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190826222132598.png)

2. 再次查看flash中存在的操作系统

   ![image-20190826222301818](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190826222301818.png)

3. 指定使用EA8启动交换机

   ![image-20190826222407215](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190826222407215.png)

4. 删除EA4操作系统

   ![image-20190826222515325](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190826222515325.png)



