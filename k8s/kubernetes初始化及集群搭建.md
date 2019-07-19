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
   
   #添加了代理，但是ik8s.io这个好像不能用
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

   如果是0的话，需要如下的修改

   ```shell
   [root@controller ~]# cat /etc/sysctl.conf
   net.bridge.bridge-nf-call-iptables = 1
   net.bridge.bridge-nf-call-ip6tables = 1
   #使生效
   sysctl –p
   ```

   

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

    ```shell
    
    ```
#查看参数使用帮助
    kubeadm init --help
    
    #初始化
    kubeadm init  --pod-network-cidr=10.244.0.0/16 --service-cidr=10.96.0.0/12 --ignore-preflight-errors=Swap
    ```

![image-20190718230157182](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190718230157182.png?raw=true)
    
启动会有swap报错，我们需要忽略，解决的方法
    
```shell
    vim /etc/sysconfig/kubelet
KUBELET_EXTRA_ARGS="--fail-swap-on=false"
```

    还是会报错，无法下载镜像。因为无法访问谷歌镜像仓库。可以通过其他途径下载镜像到本地，再执行初始化
    
    ```shell
    #我们可以指定镜像仓库，这样就解决了 无法访问谷歌镜像仓库 的问题
    #通过这个没有报错
    kubeadm init --image-repository registry.aliyuncs.com/google_containers  --token-ttl=0 --pod-network-cidr=10.244.0.0/16 --service-cidr=10.96.0.0/12 --ignore-preflight-errors=Swap
    
    #pod-network-cidr=10.244.0.0/16 这个是flannel中的默认的网络配置
    ```
    
    等待一两分钟之后，我们查看本地的docker镜像
    
    ![image-20190718230924303](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190718230924303.png?raw=true)
    
    ![1563506194811](E:\git-workspace\note\images\docker\1563506194811.png)



```shell
#创建目录及复制文件
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config

#组件状态检查
[root@es2 ~]# kubectl get cs
NAME                 STATUS    MESSAGE             ERROR
controller-manager   Healthy   ok                  
scheduler            Healthy   ok                  
etcd-0               Healthy   {"health":"true"}   
[root@es2 ~]#

#get节点信息
[root@es2 ~]#  kubectl get nodes
NAME   STATUS     ROLES    AGE   VERSION
es2    NotReady   master   17m   v1.15.0
[root@es2 ~]# 
```

部署flannel

```shell
#For Kubernetes v1.7+ 
kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
```

![1563519701146](https://github.com/chenyansong1/note/blob/master/images/docker/1563519701146.png?raw=true)

```shell
#此时nodes是ready
[root@es2 ~]# kubectl get nodes
NAME   STATUS   ROLES    AGE   VERSION
es2    Ready    master   50m   v1.15.0
[root@es2 ~]# 

#查看pod
[root@es2 ~]# kubectl get pods -n kube-system
NAME                          READY   STATUS              RESTARTS   AGE
coredns-bccdc95cf-5sdg5       0/1     ContainerCreating   0          52m
coredns-bccdc95cf-lbpdb       0/1     ContainerCreating   0          52m
etcd-es2                      1/1     Running             0          51m
kube-apiserver-es2            1/1     Running             0          51m
kube-controller-manager-es2   1/1     Running             0          51m
kube-flannel-ds-amd64-llknx   0/1     CrashLoopBackOff    5          4m35s
kube-proxy-fp9zv              1/1     Running             0          52m
kube-scheduler-es2            1/1     Running             0          51m
[root@es2 ~]# 

#查看名称空间
[root@es2 ~]# kubectl get ns
NAME              STATUS   AGE
default           Active   53m
kube-node-lease   Active   53m
kube-public       Active   53m
kube-system       Active   53m

#查看名称空间下的所有的pod
[root@es2 ~]# kubectl get pods -n kube-system
NAME                          READY   STATUS    RESTARTS   AGE
coredns-bccdc95cf-65hlf       1/1     Running   0          23m
coredns-bccdc95cf-wlf2r       0/1     Running   0          23m
etcd-es2                      1/1     Running   1          22m
kube-apiserver-es2            1/1     Running   1          22m
kube-controller-manager-es2   1/1     Running   0          23m
kube-flannel-ds-amd64-xhzd5   1/1     Running   0          33s
kube-proxy-zlppx              1/1     Running   0          23m
kube-scheduler-es2            1/1     Running   1          22m
```



加入node节点

```shell
#安装docker ， kubelet
#拷贝上面修改的配置
scp /etc/sysconfig/kubelet spark02:/etc/sysconfig/kubelet 
scp /usr/lib/systemd/system/kubelet.service.d/10-kubeadm.conf spark02:/usr/lib/systemd/system/kubelet.service.d/10-kubeadm.conf

###################################

[root@spark02 ~]# cat /usr/lib/systemd/system/kubelet.service.d/10-kubeadm.conf
# Note: This dropin only works with kubeadm and kubelet v1.11+
[Service]

Environment="KUBELET_SYSTEM_PODS_ARGS=--pod-manifest-path=/etc/kubernetes/manifests --allow-privileged=true --fail-swap-on=false"
Environment="KUBELET_KUBECONFIG_ARGS=--bootstrap-kubeconfig=/etc/kubernetes/bootstrap-kubelet.conf --kubeconfig=/etc/kubernetes/kubelet.conf"
Environment="KUBELET_CONFIG_ARGS=--config=/var/lib/kubelet/config.yaml"
Environment="KUBELET_CGROUP_ARGS=--cgroup-driver=cgroupfs"


# This is a file that "kubeadm init" and "kubeadm join" generates at runtime, populating the KUBELET_KUBEADM_ARGS variable dynamically
EnvironmentFile=-/var/lib/kubelet/kubeadm-flags.env
# This is a file that the user can use for overrides of the kubelet args as a last resort. Preferably, the user should use
# the .NodeRegistration.KubeletExtraArgs object in the configuration files instead. KUBELET_EXTRA_ARGS should be sourced from this file.
EnvironmentFile=-/etc/sysconfig/kubelet
ExecStart=
ExecStart=/usr/bin/kubelet $KUBELET_KUBECONFIG_ARGS $KUBELET_CONFIG_ARGS $KUBELET_KUBEADM_ARGS $KUBELET_EXTRA_ARGS
[root@spark02 ~]# 
###################################



#然后启动
systemctl start docker

#设置开机自启
systemctl enable docker kubelet

#加入集群
kubeadm join 172.16.110.242:6443 --token bzfk1j.3nbzxav979qi3fcq \
    --discovery-token-ca-cert-hash sha256:b22e4bffd32b17bb6966a22fa8bc70497d1cd3155be7dca70437a23b9a58876c --ignore-preflight-errors=Swap
    
    
```

如果我们忘记了Master节点的加入token，可以使用如下命令来查看：

```
kubeadm token list

# 输出：
# TOKEN                     TTL       EXPIRES                USAGES                   DESCRIPTION   EXTRA GROUPS
# abcdef.0123456789abcdef   22h       2018-11-10T14:24:51Z   authentication,signing   <none>        system:bootstrappers:kubeadm:default-node-token
```

默认情况下，token的有效期是24小时，如果我们的token已经过期的话，可以使用以下命令重新生成：

```
kubeadm token create

# 输出：
# 9w6mbu.3k2z7pprl3eaozk9
```

如果我们也没有`--discovery-token-ca-cert-hash`的值，可以使用以下命令生成：

```
openssl x509 -pubkey -in /etc/kubernetes/pki/ca.crt | openssl rsa -pubin -outform der 2>/dev/null | openssl dgst -sha256 -hex | sed 's/^.* //'

# 输出：
# 9fcb02a0f4ab216866f87986106437b7305474850f0de81b9ac9c36a468f7c67
```

现在，我们登录到工作节点服务器，准备加入到集群。

我们可以知道集群启动的时候，是启动的static pod

