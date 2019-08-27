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

![image-20190826220848615](https://github.com/chenyansong1/note/blob/master/images/computeNetwork/image-20190826220848615.png?raw=true)

![image-20190826220908969](https://github.com/chenyansong1/note/blob/master/images/computeNetwork/image-20190826220908969.png?raw=true)



现在假设交换机上的配置文件丢失了，需要通过备份的配置文件恢复

1. 还是给交换机配置一个IP地址，保证交换机和TFTP服务器之间能够ping通

   ![image-20190826221042851](https://github.com/chenyansong1/note/blob/master/images/computeNetwork/image-20190826221042851.png?raw=true)

2. 从TFTP服务器恢复配置

   ![image-20190826221240027](https://github.com/chenyansong1/note/blob/master/images/computeNetwork/image-20190826221240027.png?raw=true)

3. 同理，我们也可以备份startup-config，同时也可以copy TFTP到running-config



# IOS的备份和升级

```shell
#查看交换机的操作系统
Switch#show flash
```

![image-20190826221528203](https://github.com/chenyansong1/note/blob/master/images/computeNetwork/image-20190826221528203.png?raw=true)

备份交换机的操作系统

```shell
Switch#copy flash tftp
```

![image-20190826221737230](https://github.com/chenyansong1/note/blob/master/images/computeNetwork/image-20190826221737230.png?raw=true)

![image-20190826221834360](https://github.com/chenyansong1/note/blob/master/images/computeNetwork/image-20190826221834360.png?raw=true)

恢复操作系统

```shell
copy tftp flash
```

升级操作系统

1. copy新的操作系统到flash中

   ![image-20190826222132598](https://github.com/chenyansong1/note/blob/master/images/computeNetwork/image-20190826222132598.png?raw=true)

2. 再次查看flash中存在的操作系统

   ![image-20190826222301818](https://github.com/chenyansong1/note/blob/master/images/computeNetwork/image-20190826222301818.png?raw=true)

3. 指定使用EA8启动交换机

   ![image-20190826222407215](https://github.com/chenyansong1/note/blob/master/images/computeNetwork/image-20190826222407215.png?raw=true)

4. 删除EA4操作系统

   ![image-20190826222515325](https://github.com/chenyansong1/note/blob/master/images/computeNetwork/image-20190826222515325.png?raw=true)



