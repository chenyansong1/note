[TOC]

转自：https://www.cnblogs.com/kelamoyujuzhen/p/9126022.html



假设这样一种场景：

　　某运营商的Linux服务器上装配了2家互联网公司的Web服务，每个Web服务分配了一个公网IP地址。但是运营商的Linux服务器只有一块网卡。这就需要在一块网卡上绑定多个IP地址。对用户来说，就好像每个Web服务再单独一台服务器上一样。

实验环境：Redhat 6.4 企业版64位

在目录/etc/sysconfig/network-scripts下可以看到物理网卡的配置文件ifcfg-eth#，如果要在某个物理网卡上绑定多个虚拟IP address，就需要在这个物理网卡上虚拟出网卡ifcfg-eth#:$（注：#、$代表数字0，1，2....，其中$取值范围0-255，#视服务器上插了多少个以太网卡而定）。

使用ifconfig命令在ifcfg-eth0上面添加多个虚拟网卡

添加之前

```shell
[root@51cto network-scripts]# ifconfig
eth0      Link encap:Ethernet  HWaddr 00:0C:29:82:AA:8B  
          inet addr:192.168.80.222  Bcast:192.168.80.255  Mask:255.255.255.0
          inet6 addr: fe80::20c:29ff:fe82:aa8b/64 Scope:Link
          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1
          RX packets:1084 errors:0 dropped:0 overruns:0 frame:0
          TX packets:868 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:1000 
          RX bytes:98302 (95.9 KiB)  TX bytes:89630 (87.5 KiB)

lo        Link encap:Local Loopback  
          inet addr:127.0.0.1  Mask:255.0.0.0
          inet6 addr: ::1/128 Scope:Host
          UP LOOPBACK RUNNING  MTU:16436  Metric:1
          RX packets:0 errors:0 dropped:0 overruns:0 frame:0
          TX packets:0 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:0 
          RX bytes:0 (0.0 b)  TX bytes:0 (0.0 b)
```

添加虚拟网卡eth0:0（不一定非得从0开始，只要是0-255随便写）

````shell
[root@51cto network-scripts]# ifconfig eth0:0 192.168.80.223
[root@51cto network-scripts]# ifconfig 
eth0      Link encap:Ethernet  HWaddr 00:0C:29:82:AA:8B  
          inet addr:192.168.80.222  Bcast:192.168.80.255  Mask:255.255.255.0
          inet6 addr: fe80::20c:29ff:fe82:aa8b/64 Scope:Link
          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1
          RX packets:1208 errors:0 dropped:0 overruns:0 frame:0
          TX packets:940 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:1000 
          RX bytes:108516 (105.9 KiB)  TX bytes:98514 (96.2 KiB)

eth0:0    Link encap:Ethernet  HWaddr 00:0C:29:82:AA:8B  
          inet addr:192.168.80.223  Bcast:192.168.80.255  Mask:255.255.255.0
          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1

lo        Link encap:Local Loopback  
          inet addr:127.0.0.1  Mask:255.0.0.0
          inet6 addr: ::1/128 Scope:Host
          UP LOOPBACK RUNNING  MTU:16436  Metric:1
          RX packets:0 errors:0 dropped:0 overruns:0 frame:0
          TX packets:0 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:0 
          RX bytes:0 (0.0 b)  TX bytes:0 (0.0 b)
````

由于这是使用命令临时加上去的，当然在/etc/sysconfig/network-scripts下面的网卡配置文件不会与任何变化。

可见虚拟网卡与真是网卡，IP不一样，但是MAC一样。

如果要禁用虚拟网，操作方式和禁用物理网卡是一样的，ifconfig eth#:$ down即可。禁用之后Win10就ping不通了，在Linux上使用ifconfig看不到eth#:$这个虚拟网卡。如果我们想再次起用这个虚拟网卡可以吗？ifconfig eth#:$ up是否可以呢？

不好意思，用ifconfig临时加的虚拟网卡一旦down就没了，再也up不起来。

```shell
[root@51cto network-scripts]# ifconfig eth0:1 up
SIOCSIFFLAGS: Cannot assign requested address
```

如果重启网络服务，重启系统，临时加的虚拟网卡也会消失。

 

永久生效的办法只有一个，修改配置文件。这个配置文件系统没有自带，需要手动创建，在/etc/sysconfig/network-scripts下面创建

ifcfg-eth#:$

```shell
[root@51cto network-scripts]# cp ifcfg-eth0 ifcfg-eth0:0
[root@51cto network-scripts]# vi ifcfg-eth0:0
[root@51cto network-scripts]# cat ifcfg-eth0:0
DEVICE=eth0:0
TYPE=Ethernet
ONBOOT=yes
NM_CONTROLLED=yes
BOOTPROTO=none
IPADDR=192.168.80.223
PREFIX=24
DEFROUTE=yes
IPV4_FAILURE_FATAL=yes
IPV6INIT=no
NAME="System eth0:0"
HWADDR=00:0C:29:82:AA:8B
[root@51cto network-scripts]# service network restart
```

