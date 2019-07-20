[TOC]

资源：对象

* workload：Pod，controller（replicaSet, Deployment, StatefulSet, DaemonSet, Job, Cronjob)
* 服务发现及服务均衡：service， ingress
* 配置与存储相关的资源：Volume（云存储，本地分布式存储...）， CSI（容器存储接口）
  * ConfigMap
  * Secret:保存敏感数据
  * DownwardAPI
* 集群级别的资源
  * Namespace
  * Node
  * Role
  * ClusterRole
  * RoleBinding
  * ClusterRoleBinding
* 元数据型资源
  * HPA
  * PodTemplate
  * LimitRange

![image-20190720163519217](/Users/chenyansong/Documents/note/images/docker/image-20190720163519217.png)



创建资源的方法

​	apiserver仅接受JSON格式的资源定义

​	yaml格式提供配置清单，apiserver可自动将其转为json格式，而后再提交

大部分资源的配置清单

* apiVersion:创建的资源属于哪个api群组及其版本，查看所有的组名及版本

  ![image-20190720165409883](/Users/chenyansong/Documents/note/images/docker/image-20190720165409883.png)

* kind：资源类别（Pod， controller(...) , service)

* metadata：元数据

  name

  namespace

  labels

  annotations:注解

  每个资源的引用方式：/api/GROUP/VERSION/namespace/NAMESPACE/TYPE/NAME

  ![image-20190720170341075](/Users/chenyansong/Documents/note/images/docker/image-20190720170341075.png)

* spec：期望的状态 disired state

* status：当前状态 current state 本字段由kubernetes维护



上面这样的格式定义，k8s提供了一个格式定义的说明

```shell
#查看pod资源该怎么定义
kubectl explain pods

```

![image-20190720170931547](/Users/chenyansong/Documents/note/images/docker/image-20190720170931547.png)

如果我们想要看二级字段，所以可以依次查看多级的字段定义

```shell
#如查看metadata的定义
kubectl explain pods.metadata
```



我们创建yaml的文件

```shell
chenyansongdeMacBook-Pro:mainfests chenyansong$ cat pod-demo.yaml 
apiVersion: v1
kind: Pod
metadata:
        name: pod-name
        namespace: default
        labels:
                app: myapp
                tier: frontend
spec:
        containers:#这里containers的类型为list,通过-进行表示，这里创建了2个容器
        - name: myapp
          image: nginx
        - name: busybox
          image: busybox
          command:#指定容器的默认命令
          - "/bin/sh"
          - "-c"
          - "sleep 3600"

```

创建pod通过yaml文件

```shell
kubectl create -f pod-demo.yaml 
```

![image-20190720174945532](/Users/chenyansong/Documents/note/images/docker/image-20190720174945532.png)

![image-20190720175025070](/Users/chenyansong/Documents/note/images/docker/image-20190720175025070.png)

我们也可以查看容器的详细信息

```shell
#查看创建的pod的详情
kubectl describe pods pod-demo
```

![image-20190720175341624](/Users/chenyansong/Documents/note/images/docker/image-20190720175341624.png)

我们可以通过events来看里面发生了什么

![image-20190720175633698](/Users/chenyansong/Documents/note/images/docker/image-20190720175633698.png)

从上面可以看到有一个容器启动失败，我们可以看一个pod内部的一个容器的日志

```shell
#查看某个pod的容器的日志
kubectl logs pod-demo myapp

```

![image-20190720180200949](/Users/chenyansong/Documents/note/images/docker/image-20190720180200949.png)



我们进入某个pod的某个容器

```shell
kubectl exec -it pod-demo -c myapp -- /bin/sh
```



删除一个基于文件定义的pod，这种文件创建的pod是不会因为你的删除，controller会帮你启动一遍

```shell
kubectl delete -f pod-demo.yaml
```

