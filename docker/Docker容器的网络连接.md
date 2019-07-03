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

  



允许端口映射访问



限制IP访问容器





# Docker容器与外部网络的链接





基于iptables的防火墙机制







Docker容器的网络连接





