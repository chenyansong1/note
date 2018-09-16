[TOC]



# ifconfig显示的信息说明

```
[root@localhost ~]# ifconfig
eth0      Link encap:Ethernet  HWaddr 00:50:56:BF:26:20  
          inet addr:192.168.120.204  Bcast:192.168.120.255  Mask:255.255.255.0
          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1
          RX packets:8700857 errors:0 dropped:0 overruns:0 frame:0
          TX packets:31533 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:1000 
          RX bytes:596390239 (568.7 MiB)  TX bytes:2886956 (2.7 MiB)

lo        Link encap:Local Loopback  
          inet addr:127.0.0.1  Mask:255.0.0.0
          UP LOOPBACK RUNNING  MTU:16436  Metric:1
          RX packets:68 errors:0 dropped:0 overruns:0 frame:0
          TX packets:68 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:0 
          RX bytes:2856 (2.7 KiB)  TX bytes:2856 (2.7 KiB)


#详细说明
eth0      Link encap:Ethernet (底层是Ethernet协议) HWaddr 00:50:56:BF:26:20 (Mac地址)
		  # ip , 广播，子网
          inet addr:192.168.120.204  Bcast:192.168.120.255  Mask:255.255.255.0
          #up 表示启动，支持BROADCAST(广播)，多播(MULTICAST)，mtu:最大传输单元
          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1
          #RX:表示发送
          RX packets:8700857 errors:0 dropped:0 overruns:0 frame:0
          #TX:表示接收
          TX packets:31533 errors:0 dropped:0 overruns:0 carrier:0
          #冲突个数，传输队列
          collisions:0 txqueuelen:1000 
          #接收和发送的数据大小
          RX bytes:596390239 (568.7 MiB)  TX bytes:2886956 (2.7 MiB)
```



# 参数说明

```
-a 显示所有接口的配置信息
[ethX] 显示某一个接口的信息

#配置某个网卡的地址(重启网络服务或者主机将失效)
ifconfig ethX IP/MASK 
ifconfig eth0 172.168.1.1/24


#启动|禁用
ifconfig [ethX] down|up


```

