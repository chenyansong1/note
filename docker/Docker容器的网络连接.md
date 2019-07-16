[TOC]

# Docker容器的网络基础

![1562144459116](E:\git-workspace\note\images\docker\network2.png)





```shell
#安装网桥管理工具
yum install -y bridge-utils

#查看网桥
brctl show

[root@spark01 ~]# brctl show
docker0         8000.02420dd54da6       no              veth549ad8a
[root@spark01 ~]# 
```

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



# docker网络类型



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



docker有3中网络，默认是桥接网络（nat桥）

```shell
[root@spark01 ~]# docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
49c87fe39fb9        bridge              bridge              local
8813cd1ab2ff        host                host                local
678c49b204c1        none                null                local
[root@spark01 ~]# 
#none表示没有网络，表示信息孤岛(在批处理任务中，他们只是进行了计算，然后得到了结果，加工得到的数据放到外部存储卷上即可)
```

## 桥接（bridge）

docker会在宿主机上虚拟出来一个软交换机（网卡为docker0），每个新建的容器会有两个虚拟的网卡，一个在容器上，另一个在软交换机上（veth6d3205b）

![1562319581021](E:\git-workspace\note\images\docker\im11.png)

现在我们需要显示另外一半（在另外一半的容器中，需要进入容器中查看）

![1562319714347](E:\git-workspace\note\images\docker\im12.png)

docker默认是桥接模式，所以没启动一个容器，在主机上都有一个虚拟的网卡（另外一半在容器内）

![1563193278766](E:\git-workspace\note\images\docker\network7.png)

这些虚拟的网卡被插入到了docker0上，下图我们知道在docker0上关联了1个接口

![1563193636787](E:\git-workspace\note\images\docker\network8.png)

我们进入容器中，查看容器的网卡

![1563193705682](E:\git-workspace\note\images\docker\network9.png)

![1562144783425](E:\git-workspace\note\images\docker\network3.png)

![1563194287598](E:\git-workspace\note\images\docker\network11.png)

docker 会为每一个容器在nat表中生成规则，如下

![1563193988268](E:\git-workspace\note\images\docker\network10.png)



## 仅主机桥（host)

让容器使用宿主机的网络名称空间

当我们外部的网络需要访问时，需要添加dnet的方式以便其他客户端能够访问，**每一个容器拥有自己独立的User,Mount, Pid, 但是他们共用UTS， Net， IPC**

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





## docker的网络模型总结

![1562321355693](E:\git-workspace\note\images\docker\network6.png)

## 查看网络详情，查看某容器的网络

查看每种网络详情

```shell
[root@spark01 ~]# docker network inspect  bridge
[
    {
        "Name": "bridge",
        "Id": "49c87fe39fb92fd5397ee8807a9fc035f23acb7017d3a9f183c91ed4c92350fb",
        "Created": "2019-07-15T20:07:07.881620154+08:00",
        "Scope": "local",
        "Driver": "bridge",
        "EnableIPv6": false,
        "IPAM": {
            "Driver": "default",
            "Options": null,
            "Config": [
                {
                    "Subnet": "172.17.0.0/16",
                    "Gateway": "172.17.0.1"
                }
            ]
        },
        "Internal": false,
        "Attachable": false,
        "Ingress": false,
        "ConfigFrom": {
            "Network": ""
        },
        "ConfigOnly": false,
        "Containers": {
            "3774b05770463aea562cdcd653c542bce4b753d7442a02f2aa28558431671e74": {
                "Name": "b3",
                "EndpointID": "069355db7c3a5d2aa39aaf5d2445845fdb6a35a932e17f3b8a9ad49d796d54af",
                "MacAddress": "02:42:ac:11:00:02",
                "IPv4Address": "172.17.0.2/16",#使用网段
                "IPv6Address": ""
            }
        },
        "Options": {
            "com.docker.network.bridge.default_bridge": "true",
            "com.docker.network.bridge.enable_icc": "true",
            "com.docker.network.bridge.enable_ip_masquerade": "true",
            "com.docker.network.bridge.host_binding_ipv4": "0.0.0.0",
            "com.docker.network.bridge.name": "docker0", #docker0使用的是bridge网络
            "com.docker.network.driver.mtu": "1500"
        },
        "Labels": {}
    }
]
[root@spark01 ~]# 
```

查看某个容器使用的网络

```shell
docker container inspect web1

            "Networks": {
                "bridge": {#该容器使用的是bridge网络
                    "IPAMConfig": null,
                    "Links": null,
                    "Aliases": null,
                    "NetworkID": "4c34b9fe4450836cb8e2f1fdc656e2b3aa1e2861e0d2569baff0f987f94ead8c",
                    "EndpointID": "eaa5201e85aa2dccff07858ad6a7e4a23c0221252728fa72fc8810676f5ceaab",
                    "Gateway": "172.17.0.1",#网关
                    "IPAddress": "172.17.0.2",#ip地址
                    "IPPrefixLen": 16,
                    "IPv6Gateway": "",
                    "GlobalIPv6Address": "",
                    "GlobalIPv6PrefixLen": 0,
                    "MacAddress": "02:42:ac:11:00:02",
                    "DriverOpts": null
                }
            }
```



# 网络名称空间

```shell
#ip 命令所属的程序包
rpm -q iproute
```

![image-20190715224607977](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190715224607977.png?raw=true)



网络名称空间的相关操作

![image-20190715224806629](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190715224806629.png?raw=true)

添加网络名称空间

![image-20190715224843486](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190715224843486.png?raw=true)

查看网络名称空间的网卡

![image-20190715224923651](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190715224923651.png?raw=true)



创建成对的网卡

![image-20190715225143013](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190715225143013.png?raw=true)



将一个网络设备移动到一个名称空间

![image-20190715225325398](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190715225325398.png?raw=true)

去对应的名称空间中看

![image-20190715225426476](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190715225426476.png?raw=true)



手动设定网络

```shell
#表示不创建网络
--network none
```

![1563247684035](E:\git-workspace\note\images\docker\1563247684035.png)

设置主机名

```shell
[root@spark02 ~]# docker container attach b1
/ # hostname
4316091fd2a8
/ # 
```

默认的主机名容器的id ，我们可以在容器外注入主机名

```shell
[root@spark02 ~]# docker run --help|grep hostname
  -h, --hostname string                Container host name
[root@spark02 ~]# 

[root@spark02 ~]# docker run --name b1 -it -h www.chenyansong.site --rm busybox
/ # hostname
www.chenyansong.site

#自动生成hosts文件
/ # cat /etc/hosts
127.0.0.1       localhost
::1     localhost ip6-localhost ip6-loopback
fe00::0 ip6-localnet
ff00::0 ip6-mcastprefix
ff02::1 ip6-allnodes
ff02::2 ip6-allrouters
172.17.0.2      www.chenyansong.site www
/ # 

#dns文件
/ # cat /etc/resolv.conf 
# Generated by NetworkManager
nameserver 114.114.114.114
```

设置自定义的dns

```shell
##--dns
[root@spark02 ~]# docker run --name b1 -it --dns 8.8.8.8 -h www.chenyansong.site --rm busybox
/ # cat /etc/resolv.conf 
nameserver 8.8.8.8
/ # 
```

自动想hosts文件中添加主机和IP映射

```shell
[root@spark02 ~]# docker run --help|grep host
      --add-host list                  Add a custom host-to-IP mapping
                                       (host:ip)

[root@spark02 ~]#  docker run --name b1 -it --dns 8.8.8.8 -h www.chenyansong.site --add-host www.chenyansong.site:11.11.11.11 --rm busybox
/ # cat /etc/hosts
127.0.0.1       localhost
::1     localhost ip6-localhost ip6-loopback
fe00::0 ip6-localnet
ff00::0 ip6-mcastprefix
ff02::1 ip6-allnodes
ff02::2 ip6-allrouters
11.11.11.11     www.chenyansong.site
172.17.0.2      www.chenyansong.site www
/ # 
```



服务暴露到对外通信的网络中

```shell
-p 选项的使用格式
-p <containerPort>
	将制定的容器端口映射至主机所有地址的一个动态端口
-p <hostPort>:<containerPort>
	将容器端口<containerPort>映射至指定的主机端口<hostPort>
-p <ip>::<containerPort>
	将指定的容器端口<containerPort>映射至主机指定的<ip>的动态端口
-p <ip>:<hostPort>:<containerPort>
	将指定的容器的端口<containerPort>映射至主机指定<ip>端口<hostPort>
"动态端口"指随机端口，具体的映射接口可使用docker port命令查看
```



```shell
docker run --name -it -p 80 --rm nginx
```

![1563258646321](E:\git-workspace\note\images\docker\1563258646321.png)

![1563259204003](E:\git-workspace\note\images\docker\1563259204003.png)

指定容器开放的端口

```shell
[root@spark02 ~]# docker port nginx1
80/tcp -> 0.0.0.0:32768
[root@spark02 ~]# 

```

指定容器地址

```shell
#指定宿主机的IP
docker run --name nginx_web --rm -p 172.16.110.174::80 nginx

#宿主机上查看端口绑定的地址
[root@spark02 ~]# docker port nginx_web
80/tcp -> 172.16.110.174:32768
[root@spark02 ~]#
```

指定宿主机的端口

```shell
docker run --name nginx_web --rm -p 80:80 nginx

[root@spark02 ~]# docker port nginx_web
80/tcp -> 0.0.0.0:80
[root@spark02 ~]# 
```

既指定端口又指定地址

```shell
docker run --name nginx_web --rm -p 172.16.110.174:80:80 nginx

[root@spark02 ~]# docker port nginx_web
80/tcp -> 172.16.110.174:80
[root@spark02 ~]# 
```

暴露所有的端口：-P(大写)



共享式容器

```shell
#1.首先启动一个容器
docker run --name b1 -it --rm busybox


#2.启动另外一个容器，指定network为第一个容器的网络名称空间
docker run --name b2 --network container:b1 -it --rm busybox


```

我们可以发现，**两个container是共用一个网络名称空间**

![1563262859014](E:\git-workspace\note\images\docker\1563262859014.png)

但是他们的文件系统是隔离的，如下

![1563262984612](E:\git-workspace\note\images\docker\1563262984612.png)



```shell
#b2
echo "hello world" >/tmp/index.html
httpd -h /tmp/
netstat -lantup

#b1上访问
wget -O - -q 127.0.0.1
```

![1563263157449](E:\git-workspace\note\images\docker\1563263157449.png)

> 说明他们共享lo接口

直接使用宿主机的网络名称空间

```shell
docker run --name b2 --network host -it --rm busybox
```

![1563263495993](E:\git-workspace\note\images\docker\1563263495993.png)

![1563263722924](E:\git-workspace\note\images\docker\1563263722924.png)

以往工作在宿主机上的进程，可以放入容器中，因为容器和宿主机共享的是同一套网络名称空间，所以不影响他们的访问



改变docker默认的172.17网络

自定义docker0桥的网络属性信息：

```shell
vim /etc/docker/daemon.json
{
	"bip":"192.168.1.5/24",
	"fixed-cidr":"10.20.0.0/16",
	"fixed-cidr-v6":"2001:bd8::/64",
	"mtu":1500,
	"default-gateway":"10.20.1.1",
	"default-gateway-v6":"2001:bd8:abcd::89",
	"dns":["10.20.1.2","10.20.1.3"]
}

#核心选项为bip, 即bridge ip之意，用于指定docker0桥自身的IP地址，其他选项可通过此地址计算得到
```

```shell
#先停止docker
systemctl stop docker

#修改上面的配置
[root@spark02 ~]# cat /etc/docker/daemon.json
{
 "registry-mirrors":["https://registry.docker-cn.com"],
 "bip":"10.0.0.1/16"
}

#重新启动docker
systemctl start  docker

#查看docker0的ip
[root@spark02 ~]# ifconfig
docker0: flags=4099<UP,BROADCAST,MULTICAST>  mtu 1500
        inet 10.0.0.1  netmask 255.255.0.0  broadcast 10.0.255.255
        inet6 fe80::42:8ff:feff:ab5d  prefixlen 64  scopeid 0x20<link>
        ether 02:42:08:ff:ab:5d  txqueuelen 0  (Ethernet)
        RX packets 52  bytes 5784 (5.6 KiB)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 65  bytes 5447 (5.3 KiB)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0

```

docker默认的连接方式是sock

```shell
[root@spark02 ~]# ps -ef|grep docker
root     31447     1  0 16:26 ?        00:00:00 /usr/bin/dockerd -H fd:// --containerd=/run/containerd/containerd.sock
```

我们可以改变监听的方式，使用tcp套接字去监听

```shell
vi /etc/docker/daemon.json
	"hosts":["tcp://0.0.0.0:2375", "unix://var/run/docker.sock"]
```

```shell
#其他docker客户端连接
docker -H 172.20.0.67:2379 ps

#查看远端的docker服务上的镜像
docker -H 172.20.0.67:2379 image ls
```



创建自定义的网络

```shell
[root@spark02 ~]# docker network --help

Usage:  docker network COMMAND

Manage networks

Commands:
  connect     Connect a container to a network
  create      Create a network
  disconnect  Disconnect a container from a network
  inspect     Display detailed information on one or more networks
  ls          List networks
  prune       Remove all unused networks
  rm          Remove one or more networks

Run 'docker network COMMAND --help' for more information on a command.
[root@spark02 ~]# docker network create --help

Usage:  docker network create [OPTIONS] NETWORK

Create a network

Options:
      --attachable           Enable manual container attachment
      --aux-address map      Auxiliary IPv4 or IPv6 addresses used by Network driver (default map[])
      --config-from string   The network from which copying the configuration
      --config-only          Create a configuration only network
  -d, --driver string        Driver to manage the Network (default "bridge")
      --gateway strings      IPv4 or IPv6 Gateway for the master subnet
      --ingress              Create swarm routing-mesh network
      --internal             Restrict external access to the network
      --ip-range strings     Allocate container ip from a sub-range
      --ipam-driver string   IP Address Management Driver (default "default")
      --ipam-opt map         Set IPAM driver specific options (default map[])
      --ipv6                 Enable IPv6 networking
      --label list           Set metadata on a network
  -o, --opt map              Set driver specific options (default map[])
      --scope string         Control the network's scope
      --subnet strings       Subnet in CIDR format that represents a network segment
[root@spark02 ~]# 


#创建一个自定义的网络
docker network create -d bridge --subnet "172.26.0.0/16" --gateway "172.26.0.1" mybr0
```

![1563267751496](E:\git-workspace\note\images\docker\1563267751496.png)

然后我们创建容器时，可以将容器加入到此network

```shell
 docker run --name b2 -it --network  mybr0 --rm  busybox
```

![1563268679352](E:\git-workspace\note\images\docker\1563268679352.png)

上面开启了两个network，一个是mybr0，一个是docker0，他们属于不同的网段，那么怎么通信呢，只需要打开核心转发即可

打开核心转发，同时需要改下iptables

```shell
[root@spark02 ~]# cat /proc/sys/net/ipv4/ip_forward
1
[root@spark02 ~]# 
```

