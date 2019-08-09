linux网卡兼容性问题

最近，有一批联想的服务器，但是配置好网口之后，服务器连接到交换机上是不能ping通的，但是笔记本直连服务器是可以ping通的，这个是由于**联想的服务器的千兆口不能兼容百兆口的交换机所致**

查看对应的IP配置在

```shell
#查看所有的网口
ifconfig

#查看IP配置在哪个网口
ethtool -p enp2s0  #此时enp2s0对应的网口灯会闪
```



查看对应的网卡的速率，是百兆，还是千兆，还是万兆

```shell
#方式1
lspci -vvv | grep Ethernet

#方式2
[root@soc22 ~]# ethtool eno1 #这个是：只有网口连通，才能查看
Settings for eno1:
        Supported ports: [ TP ]
        Supported link modes:   10baseT/Half 10baseT/Full 
                                100baseT/Half 100baseT/Full 
                                1000baseT/Full 
        Supported pause frame use: Symmetric
        Supports auto-negotiation: Yes
        Advertised link modes:  10baseT/Half 10baseT/Full 
                                100baseT/Half 100baseT/Full 
                                1000baseT/Full 
        Advertised pause frame use: Symmetric
        Advertised auto-negotiation: Yes
        Speed: 100Mb/s  #网口速率
 		....
```











