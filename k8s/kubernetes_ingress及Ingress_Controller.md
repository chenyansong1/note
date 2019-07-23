[TOC]



# Ingress原理



* ClusterIP只能在集群内部访问边界不可达

* NodePort（在ClusterIP的基础上增强）：client->NodeIP:NodePort->ClusterIP:ServicePort->PodIP:containerPort 此时的NodeIP是有多个的，所以前面需要加上一个负载均衡器，k8s如果部署在公有云上，并且公有云支持lbaas
* LoadBalancer
* ExternalName
  * FQDN
    * CNAME->FQDN
* 无头服务（No ClusterIP , Headless Service）
  * ServiceName -> PodIP(不进过serviceIP)

4层调度，7层调度

使用一个独立的Pod（HTTPS明文卸载器），反向代理后端的Pod

![1563873209072](E:\git-workspace\note\images\docker\1563873209072.png)

打上污点，让别的Pod都不能运行在此，然后在指定的几个节点上运行独立的Pod，并将这些Pod指定为DaemonSet的模式

上面的Pod在k8s中有一个专门的称呼：Ingress Controller



常见的调度器

HAProxy(不用)

Traefik

Nginx

Envoy（微服务）



Ingress资源

![1563874552941](E:\git-workspace\note\images\docker\1563874552941.png)



# Ingress调度清单

```shell
kubectl explain ingress

kubectl explain ingress.spec.rules
#host ： 主机调度
#http: 路径调度

#关联后端的Pod
kubectl explain ingress.spec.backend
#serviceName
#servicePort
```

在k8s的[github](https://github.com/kubernetes)上搜索[ingress-nginx](https://github.com/kubernetes/ingress-nginx)

![1563875899219](E:\git-workspace\note\images\docker\1563875899219.png)

创建名称空间

```shell
#创建
kubectl create namespace dev
#查看
kubectl get ns
#删除
kubectl delete ns/dev

#或者通过yaml文件创建
apiVersion:v1
kind: Namespace
metadata:
	name: ingress-nginx
```



1. 下载文件

   ![1563876179324](E:\git-workspace\note\images\docker\1563876179324.png)

2. 创建名称空间

   ```shell
   kubectl apply -f namespace.yaml
   ```

3. 创建所有的yaml清单

   ```shell
   kubectl apply -f ./
   ```

4. 









