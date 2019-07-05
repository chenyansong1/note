[TOC]

# Docker容器的网络基础

![1562144364227](E:\git-workspace\note\images\docker\network1.png)

这里的docker0是Linux的虚拟网桥

![1562144459116](E:\git-workspace\note\images\docker\network2.png)

* linux的虚拟网桥的特点

  * 可以设置IP地址
  * 相当于拥有一个影藏的虚拟网卡

* docker0的地址划分

  * IP：172.17.42.1 子网掩码：255.255.0.0
  * MAC: 02:42:ac:11:00:00 到02:42：ac:11:ff:ff
  * 总共提供了65534个地址

* 为每一个容器提供一个Mac地址

  ![1562144783425](E:\git-workspace\note\images\docker\network3.png)



```shell
#安装网桥管理工具
yum install -y bridge-utils

#查看网桥
brctl show

```

![1562145564084](E:\git-workspace\note\images\docker\network4.png)

* 修改docker0地址

  ```shell
  ifconfig docker01 192.168.200.1 netmask 255.255.255.0
  
  ```

* 添加虚拟网桥

  ```shell
  brctl addbr br0
  ifconfig br0 192.168.100.1 netmask 255.255.255.0
  ```

* 更改docker守护进程的启动配置

  ```shell
  /etc/default/docker 中添加DOCKER_OPS的值：-b=br0
  ```

  

# Docker容器的互联

* ## 允许所有的容器的互联

```shell
#默认允许容器间的互联
--icc=true 

#启动两个容器
docker run -it --name cct1 username/cct
docker run -it --name cct2 username/cct
#在一个容器中去ping另一个容器
```

容器的IP地址默认是随机的，所以需要固定容器的IP地址,link选项会自动修改别名之间

```shell
--link
docker run --link=[container_name]:[alias] [image] [command]

docker run -it --name --link=cct1:webtest cct1 username/cct

ping webtest

#查看环境变量
env

#查看host文件
vim /etc/hosts
#添加了webtest的地址映射
172.xx.xx.xx webtest


#同时启动多个容器
docker start container1 container2 container3
```



## 拒绝容器间互联

 阻断容器间的连接是基于安全的考虑，但是又有基于安全的需求，基于特定容器间的连接

  ```shell
  #关闭所有的连接
  --icc=false
  
  #允许特定连接
  --icc=false
  --iptables=true  #控制网络访问的组件
  --link
  
  ```



## 允许特定容器间的连接

* ip_forward

  ```shell
  #1：允许转发
  --ip-forward=true
  
  [root@spark01 ~]# sysctl net.ipv4.conf.all.forwarding
  net.ipv4.conf.all.forwarding = 1
  [root@spark01 ~]# 
  ```

  

* iptables

  iptables是与Linux内核集成的包过滤防火墙系统，几乎所有的linux发行版都会包含IPtables的功能

  ![1562154628494](E:\git-workspace\note\images\docker\network5.png)

  * filter表中包含的链

    * INPUT
    * FORWARD
    * OUTPUT

    ```shell
    iptables -t filter -L -n
    ```

    

* 查看端口映射

  ```shell
  docker port containerName
  80/tcp -> 0.0.0.0:49153
  
  #curl 127.0.0.1:49153
  ```

  

* 允许端口映射访问

* 限制IP访问容器

  ```shell
  iptables  -I DOCKER -s 10.22.111.3 -d 172.17.0.8 -p tcp --dport 80 j DROP
  
  #iptalbes -L -n
  ```





6种名称空间：UTS(主机名和域名)， User, mount, IPC,Pid,network

名称空间超过了物理网卡的数量，可以使用虚拟网卡设备（使用软件的方式模拟），Linux支持二层设备和三层设备模拟

单节点上的容器间通信

![1562316696877](E:\git-workspace\note\images\docker\im9.png)

在容器和宿主机上建立了一个软交换机，每个容器会虚拟出来一个连接（容器的网卡和交换机上的网卡）

两个软交换机的连接

![1562316882424](E:\git-workspace\note\images\docker\im10.png)

连个节点上的容器通信

snet, dnet

overlay network(叠加网络)：基于隧道

bridge：在本机上创建一个软交换机（docker0）

docker会在宿主机上虚拟出来一个软交换机（网卡为docker0），每个新建的容器会有两个虚拟的网卡，一个在容器上，另一个在软交换机上（veth6d3205b）

![1562319581021](E:\git-workspace\note\images\docker\im11.png)

现在我们需要显示另外一半（在另外一半的容器中，需要进入容器中查看）

![1562319714347](E:\git-workspace\note\images\docker\im12.png)



查看nat表

![1562319897325](E:\git-workspace\note\images\docker\im13.png)

> MASQUERADE 地址伪装

​	

仅主机桥

当我们外部的网络需要访问时，需要添加dnet的方式以便其他客户端能够访问

![1562320957100](E:\git-workspace\note\images\docker\im14.png)

对外使用同一个主机名和域名，使用同一个网络

```shell
[root@spark01 ~]# docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
8091dcd66ac0        bin_default         bridge              local
4c34b9fe4450        bridge              bridge              local
#host让容器使用宿主机的网络名称空间
8813cd1ab2ff        host                host                local

#没有任何网络，即不能通信
678c49b204c1        none                null                local
[root@spark01 ~]# 

```

docker的网络模型

![1562321355693](E:\git-workspace\note\images\docker\network6.png)

查看某个容器使用的网络

```shell
docker container inspect web1

            "Networks": {
                "bridge": {
                    "IPAMConfig": null,
                    "Links": null,
                    "Aliases": null,
                    "NetworkID": "4c34b9fe4450836cb8e2f1fdc656e2b3aa1e2861e0d2569baff0f987f94ead8c",
                    "EndpointID": "eaa5201e85aa2dccff07858ad6a7e4a23c0221252728fa72fc8810676f5ceaab",
                    "Gateway": "172.17.0.1",
                    "IPAddress": "172.17.0.2",
                    "IPPrefixLen": 16,
                    "IPv6Gateway": "",
                    "GlobalIPv6Address": "",
                    "GlobalIPv6PrefixLen": 0,
                    "MacAddress": "02:42:ac:11:00:02",
                    "DriverOpts": null
                }
            }
```

