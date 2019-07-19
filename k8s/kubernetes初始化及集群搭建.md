[TOC]

# 容器编排工具

* docker-compose：单机编排工具；docker-swarm：面向多主机;docker machine：预处理工具,上述就是docker的三剑客

![1563433838923](https://github.com/chenyansong1/note/blob/master/images/docker/1563433838923.png?raw=true)

* mesos编排：IDC编排，容器编排的框架（marathon）

* kubernetes

CI: 持续集成

CD：持续交付，Delivery

CD：持续部署：Deployment

DevOps



Borg是Google的内部系统，kubernetes是站在此的基础上，2015.7发布1.0



# k8s的特性

* 自动装箱
* 自我修复（其实是容器直接kill，然后重新启动一个）
* 水平扩展（只要物理平台的资源足够）
* 服务发现和负载均衡
* 自动发布和回滚
* 秘钥和配置管理
* 存储编排
* 批处理运行

# 架构概述

![1563438332254](https://github.com/chenyansong1/note/blob/master/images/docker/1563438332254.png?raw=true)

**在k8s上最小运行的单元为Pod**，可以理解为容器的外壳，给容器做了一层封装，一个Pod中可以有多个容器，多个容器之间共享网络名称空间，文件系统，对外一个Pod像一个虚拟机，通常一个Pod中只放一个容器（当然一个容器中是可以存在辅助容器的，如一个辅助的日志收集容器，sidecar）

pod

1. 自主式pod
2. 控制器管理的pod
   1. ReplicationController ： 副本控制器，如果副本数量少了，自动添加；可以滚动更新（创建一个新的pod，移除一个old pod）
   2. ReplicaSet
   3. Deployment：无状态应用管理
   4. statefulSet：有状态管理
   5. DaemonSet：
   6. Job：临时的pod
   7.  CronJob：定时的job
   8. HPA：自动监控资源，水平自动伸缩器

标签选择器(label selector)：用来选择特定的pod，便于pod管理

# 基础概念

一个pod的hostname和IP在每次启动的时候，都会变，但是他们的label是不会变的，所以service是每次都通过标签选择器去选择对应label的pod即可

service是一个dnat规则，下面是一个nginx-tomcat-MySQL的访问模型

![1563443233537](https://github.com/chenyansong1/note/blob/master/images/docker/1563443233537.png?raw=true)

同一个pod内的多个容器间通信：lo

各Pod间通信：叠加网络

Pod与service之间的通信：iptables

kube-proxy:与service通信，在每个节点创建规则，每个pod的变动，也是由他反应到规则上

etcd:各个master有一个共享的存储，是一个键值存储系统，实现leader的选举，像zookeeper，etcd需要做成HA，一般需要3节点

CNI：容器网络接口

* flannel：网络配置（叠加网络）
* calico：网络配置，网络策略（IPIP）
* canel：用第一种方式提供网络，用第二种方式提供网络策略

网络策略：定义名称空间之间的互相访问

![1563445098519](https://github.com/chenyansong1/note/blob/master/images/docker/1563445098519.png?raw=true)



# kubeadm部署k8s

https://github.com/kubernetes/kubeadm/blob/master/docs/design/design_v1.10.md

1. master, nodes : 安装kuberlet, kubeadm, docker
2. master：kubeadm init
3. nodes：kubeadm join



1. 本地hosts做dns解析

   ![image-20190718215825027](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190718215825027.png?raw=true)

2. 各个节点时间同步

3. 关闭iptables 和 firewalld，最后开机disable

4. 下载k8s的包

   ![image-20190718220316794](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190718220316794.png?raw=true)

   ![image-20190718220444775](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190718220444775.png?raw=true)

   ​	![image-20190718220610006](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190718220610006.png?raw=true)

5. 安装k8s和Docker的yum配置（阿里云上有对应的仓库）

   ![image-20190718221648871](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190718221648871.png?raw=true)

   ![image-20190718221724120](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190718221724120.png?raw=true)

   ![image-20190718221842590](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190718221842590.png?raw=true)

   同时生成k8s的仓库

   ```shell
   #cd /etc/yum.repo.d/
   #vim kubernetes.repo
   [kubernetes]
   name=Kubernates Repo
   baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64/
   gpgcheck=1
   gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
   enabled=1
   
   #可能报错
   wget https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
   wget https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg
   rpm --import rpm-package-key.gpg
   rpm --import yum-key.gpg
   ```

   ```shell
   #查看所有的repo
   yum repolist
   ```

   ![image-20190718222706513](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190718222706513.png?raw=true)

   **将上面的节点复制到其他的节点上**

6. 安装docker-ce, kubelet, kebuadm, kubectl

   ```shell
   yum install -y docker-ce kubelet kubeadm kubectl
   ```

   ![image-20190718223020962](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190718223020962.png?raw=true)

7. vim

   ```shell
   #vim /usr/lib/systemd/system/docker.service
   ```

   ![image-20190718223927397](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190718223927397.png?raw=true)

   ![image-20190718224309103](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190718224309103.png?raw=true)

   ```shell
   #重新加载配置
   systemctl daemon-reload
   #设置开机自启
   systemctl enable docker
   systemctl start docker
   ```

8. 确保网络

   ![image-20190718224504879](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190718224504879.png?raw=true)

9. 查看kubelet生成的文件

   ![image-20190718224557362](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190718224557362.png?raw=true)

   我们查看一下配置文件

   ![image-20190718224726683](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190718224726683.png?raw=true)

10. 启动kubelet

    ```shell
    systemctl start kubelet
    ```

    ![image-20190718224940539](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190718224940539.png?raw=true)

    此时，我们并不能直接启动kubelet服务，因为有好些文件没有配置，这里我们只需要设置一下开机自启就行了

    ```shell
    systemctl enable kubelet
    ```

11. kubeadm init

    ![image-20190718230157182](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190718230157182.png?raw=true)

    启动会有swap报错，我们需要忽略，解决的方法

    ```shell
    vim /etc/sysconfig/kubelet
    KUBELET_EXTRA_ARGS="--fail-swap-on=false"
    ```

    我们再一次进行初始化

    ![image-20190718230532224](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190718230532224.png?raw=true)

    等待一两分钟之后，我们查看本地的docker镜像

    ![image-20190718230924303](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190718230924303.png?raw=true)

