[TOC]

# 容器编排工具

* docker-compose：单机编排工具；docker-swarm：面向多主机;docker machine：预处理工具,上述就是docker的三剑客

![1563433838923](E:\git-workspace\note\images\docker\1563433838923.png)

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

![1563438332254](E:\git-workspace\note\images\docker\1563438332254.png)

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

![1563443233537](E:\git-workspace\note\images\docker\1563443233537.png)

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

![1563445098519](E:\git-workspace\note\images\docker\1563445098519.png)

