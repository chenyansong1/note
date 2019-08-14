[TOC]

# 安装GNS3

参考链接：

https://www.mainblog.cn/145.html

https://blog.csdn.net/zhangpeterx/article/details/86407065



# 路由器常用命令行配置

```shell
#进入配置
config t
#配置
ip address 192.168.0.254 255.255.255.0
#生效
no shutdown

#保存配置
#running-config 是内存中存在的配置
#startup-config 是磁盘中存在的配置
Router1#copy running-config startup-config

#如果我们修改了配置，但是我们想要放弃这些修改
Router#copy startup-config running-config
```



# 让虚拟机连接到路由器

1. 指定虚拟机的网络

2. 指定虚拟机的网络对应的物理机上的网卡的IP设置

3. 拖拽出来一个PC，设置PC绑定上面的物理机的虚拟网卡

   ![image-20190814214729650](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190814214729650.png)

   注意：选择绑定的网卡和路由器的网卡相连

   ![image-20190814215051449](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190814215051449.png)

4. 配置路由器对应网卡的IP

   ![image-20190814215256386](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190814215256386.png)

5. 在虚拟机上测试ping路由器的网卡IP

   ![image-20190814215334134](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190814215334134.png)



# 设置路由器允许Telnet

这样就不用直接通过console线去配置，远程就可以实现配置

```shell
#查看同时可以有多少个终端可以连接到路由器
line vty 0 ?

#password 设置密码为：aaa

#表示需要登录
login

```



![image-20190814220733993](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190814220733993.png)

在虚拟机上Telnet

![image-20190814220952257](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190814220952257.png)

然后要求输入账号密码

![image-20190814221223572](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190814221223572.png)

查看路由器上有哪些人登录，我们可以看到，有一个是console连接的，有一个是用vty(virtual telnet )

![image-20190814221419063](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190814221419063.png)

禁止Telnet

1. 需要登录
2. 但是有没有给密码，这两者矛盾就实现了禁止Telnet

![image-20190814221721987](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190814221721987.png)





