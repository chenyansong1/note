[TOC]



# 路由参数及简单配置

```
route 
	add	添加
		-host	主机路由
		-net	网络路由
			-net 0.0.0.0 默认网络路由

	route add -net|-host DEST gw nexthop
	#添加默认路由
	route add default gw nexthop			


	del	删除
		-host
		-net
	route del -net 10.0.0.0/8 [gw nexthop]
	#删除默认路由
	route del -net 0.0.0.0
	route del default
	

#以上配置的路由信息，重启网络服务或者主机后，都会失效


#添加到达10.0.0.0/8 网络的路由是192.168.10.1
route add -net 10.0.0.0/8 gw 192.168.10.1

#后面不带参数，直接查看路由表
route 
route -n	#以数字方式显示主机和端口信息
```



# 路由永久配置

```
vim /etc/sysconfig/network-scripts/route-eth0

#格式1
#到达 192.168.10.0/24 这个网络，通过 10.10.10.254
192.168.10.0/24 via 10.10.10.254

#格式2
ADDRESS0=
NETMASK0=
GATEWAY0=

#注意：两种配置方式二选一，不能同时存在
```



配置之后需要重启网络

service network restart