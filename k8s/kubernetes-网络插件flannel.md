[TOC]

# docker的网络模型

* bridge
* joined
* open
* none

# kubernetes网络通信

1. 容器间通信：同一个Pod内的多个容器间的通信，lo
2. Pod通信：Pod IP <—> Pod IP
3. Pod与Service通信：PodIP <—> ClusterIP
4. Service与集群外部客户端的通信



#  CNI

container network Interface

* flannel：只是支持地址分配
* calico：支持地址分配和网络策略（定义Pod和Pod之间能否通信）
* canel
* kube-router



```shell
ls /etc/cni/net.d/
```



# flannel

## 支持的多种后端

1. VxLAN

   1. vxlan

      ![image-20190730223859928](/Users/chenyansong/Documents/note/images/docker/image-20190730223859928.png)

      在一个Pod中ping另一个Pod的地址，我们在node上进行抓包可以看到，确实是进过了封装

      ![image-20190730224220570](/Users/chenyansong/Documents/note/images/docker/image-20190730224220570.png)

   2. Directrouting

      

2. host-gw: Host Gateway：要求必须在同一个网络中

   ![image-20190730215740112](/Users/chenyansong/Documents/note/images/docker/image-20190730215740112.png)

3. 有些使用host-gw(源Pod所在的节点和目标Pod所在的节点在同一个网络中),有些使用VxLAN进行通信(原Pod所在的节点和目标Pod所在的节点不在一个网段，中间隔着路由，自动降级为隧道通信)

4. UDP：性能较前两种低很多



## flannel的配置参数

```shell
#Network
	flannel使用的CIDR格式的网络地址，用于为Pod配置网络功能
	10.244.0.0/16 ->
		master: 10.244.0.0/24
		node01: 10.244.1.0/24
		..
		node255:10.244.255.0/24
		
#SubnetLen:默认为24，把network切分子网供各节点使用时，使用多长的掩码进行切分，默认为24位

#SubnetMin:指定子网的起始子网，默认是：10.244.0
#SubnetMax:10.244.255

#Backend: vxlan, host-gw, udp
	vxlan:默认
```




