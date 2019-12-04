[TOC]



# route添加/删除路由

## 添加/删除路由

```shell
#添加路由
route add -net 192.168.0.0/24 gw 192.168.0.1
route add -host 192.168.0.1 dev 192.168.0.1

#删除路由
route del -net 192.168.0.0/24 gw 192.168.0.1

#add 增加路由
#del 删除路由
#-net 设置到某个网段的路由
#-host 设置到某台主机的路由
#gw 出口网关 IP地址
#dev 出口网关 物理设备名
```



## 添加默认路由

```shell
#默认路由一条就够了
route add default gw 192.168.0.1
```

## 查看路由

```shell
#查看路由表
route -n
```

# ip route添加/删除路由

## 添加删除路由

```shell
#添加路由
ip route add 192.168.0.0/24 via 192.168.0.1
ip route add 192.168.0.1 dev 192.168.0.1


#删除路由
ip route del 192.168.0.0/24 via 192.168.0.1

#add 增加路由
#del 删除路由
#via 网关出口 IP地址
#dev 网关出口 物理设备名

```

## 增加默认路由

```shell
ip route add default via 192.168.0.1 dev eth0

#via 192.168.0.1 是我的默认路由器
```



## 查看路由信息

```shell
#添加默认路由
ip route
```



# 保存路由设置



保存路由设置，使其在网络重启后任然有效 

在/etc/sysconfig/network-script/目录下创建名为route- eth0的文件 

vi /etc/sysconfig/network-script/route-eth0 

在此文件添加如下格式的内容 

192.168.1.0/24 via 192.168.0.1 

重启网络验证

/etc/rc.d/init.d/network中有这么几行：

```shell
Add non interface-specific static-routes.

if [ -f /etc/sysconfig/static-routes ]; then
grep "^any" /etc/sysconfig/static-routes | while read ignore args ; do
/sbin/route add -$args
done
fi
```



也就是说，将静态路由加到/etc/sysconfig/static-routes 文件中就行了。

如加入：

```shell
route add -net 11.1.1.0 netmask 255.255.255.0 gw 11.1.1.1
```





则static-routes的格式为

```shell
any net 11.1.1.0 netmask 255.255.255.0 gw 11.1.1. 
```

# 在linux下设置永久路由的方法

1.在/etc/rc.local里添加

```shell
route add -net 192.168.3.0/24 dev eth0
route add -net 192.168.2.0/24 gw 192.168.2.254 
```

2.在/etc/sysconfig/network里添加到末尾

```shell
GATEWAY=gw-ip 或者 GATEWAY=gw-dev 
```



3./etc/sysconfig/static-routes : (没有static-routes的话就手动建立一个这样的文件)

```shell
any net 192.168.3.0/24 gw 192.168.3.254
any net 10.250.228.128 netmask 255.255.255.192 gw 10.250.228.129
```



4.开启 IP 转发：

```shell
echo "1" >/proc/sys/net/ipv4/ip_forward (临时)
vi /etc/sysctl.conf --> net.ipv4.ip_forward=1 (永久开启)

```



如果在rc.local中添加路由会造成NFS无法自动挂载问题，所以使用static-routes的方法是最好的。无论重启系统和service network restart 都会

按照linux启动的顺序，rc.local里面的内容是在linux所有服务都启动完毕，最后才被执行的，也就是说，这里面的内容是在netfs之后才被执行的，那也就是说在netfs启动的时候，服务器上的静态路由是没有被添加的，所以netfs挂载不能成功。

static-routes文件又是什么呢，这个是network脚本执行时调用的一个文件，这个文件的放置在/etc/sysconfig目录下，在network脚本中的位置是：

```shell
# Add non interface-specific static-routes.

if [ -f /etc/sysconfig/static-routes ]; then

     grep "^any" /etc/sysconfig/static-routes | while read ignore args ; do
    
         /sbin/route add -$args
    
      done

   fi

```
从这段脚本可以看到，这个就是添加静态路由的方法，static-routes的写法是

any net 192.168.0.0/16 gw 网关ip

这样的话，在启动network脚本的时候路由就自动添加上了，又因为network是在netfs前面启动的，自然在挂载nfs的时候就正常了。

这样看来，如果需要添加静态路由，使用static-routes文件要比使用rc.local好，而且当改变了网络配置，需要重启network脚本的时候，相应的静态路由是可以自动添加上的，但这时如果使用rc.local的话，在重启network服务的时候，原本添加好的静态路由就消失了。





