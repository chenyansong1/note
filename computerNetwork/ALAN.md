[TOC]



lan：局域网

vlan：是虚拟的局域网

将广播隔绝在一个VLAN中

![image-20190821220822740](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190821220822740.png)

![image-20190821221222119](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190821221222119.png)



三层交换机具有路由功能



![image-20190823132129396](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190823132129396.png)



1. 创建VLAN2
2. 将接口添加到VLAN2
3. 将G 1/1配置成干道链路



1. 创建VLAN2
2. 将G 0/1配置成干道链路
3. 将G 0/2配置成干道链路



将interface VLAN1添加IP地址

将interface VLAN2添加IP地址



```shell
en
show alan
#交换机默认所有的接口都在default 的VLAN中
```

![image-20190823164817465](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190823164817465.png)

创建VLAN

```shell
configure t
vlan 2
exit
show vlan
```

![image-20190823165038088](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190823165038088.png)

将一些口放入VLAN2中，将交换机的24口中1-12放入vlan1,13-24口放入vlan2

```shell
config t
#针对一堆接口生效
interface range fastEthernet 0/13 - 24
switchport mode access
switchport access vlan 2
```

![image-20190823165533076](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190823165533076.png)

再次`show vlan`

![image-20190823165734137](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190823165734137.png)



配置干道链路

```shell
config t
#先查看一下交换机的端口模式
interface mode ?
```

![image-20190823170240755](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190823170240755.png)

 ```shell
switchport gigabitEthernet 1/1
#设置成为干道模式
switchport mode trunk
 ```

![image-20190823170524625](/Users/chenyansong/Documents/note/images/computeNetwork/image-20190823170524625.png)

配置三层交换机

```shell
config t
interface gigabitEthernet 0/1
#查看支持的标记
switchport trunk encapsulation ?

switchport trunk encapsulation dot1q
#启用干道
switchport mode trunk 


#配置另外一个接口
interface gigabitEthernet 0/2
switchport trunk encapsulation dot1q
#启用干道
switchport mode trunk 

```



配置不同VLAN之间能够通信

```shell
interface vlan1
ip address 192.168.0.1 255.255.255.0

interfacce vlan2
ip address 192.168.1.1 255.255.255.0

#启用路由
ip routing
```







