[TOC]



https://www.cnblogs.com/justmine/p/8628465.html

# 使用到的网络

* node network

* pod network

* cluster network(service network or virtual IP 仅仅出现在service的规则中)

* kube-proxy将service的变化，转换为当前节点上调度规则（iptables, ipvs）


# Service 的工作模式

userspace:1.1-

iptables:1.10-

ipvs:1.11+

## userspace模式

1. Client Pod 发送了一个请求，请求到达内核空间（ServiceIP）
2. ServiceIP通过规则，转发到kube-proxy
3. kube-proxy通过调度将请求转发到对应的Pod（此时还是会走内核空间的ServiceIP规则）

![1563955572514](E:\git-workspace\note\images\docker\1563955572514.png)



## iptables

1. Client Pod 直接请求ServiceIP，然后ServiceIP将请求转发到对应的Pod

![1563955926922](E:\git-workspace\note\images\docker\1563955926922.png)



## ipvs

增加或者删除一个Pod，都会被apiserver的etcd检测到，然后kube-proxy通过watch到apiserver的变化，kube-proxy通知ipvs

![1563862678106](E:\git-workspace\note\images\docker\1563862678106.png)

# 使用清单创建service

## service字段说明

```shell
#查看创建步骤
kubectl explain svc

kubectl explain svc.spec
#ports：与哪些后端端口产生关联关系
#selector:需要关联到哪些Pod资源上
#clusterIP:动态分配，当然是可以指定固定的
#type: service类型：
#	ExternalName(将集群外部的服务引入到集群内部，在集群内部使用)
#	ClusterIP（默认，仅用于集群内通信）, 
#	NodePort(用于集群外部通信) 
#	LoadBalancer（lbaas）
```

##  ClusterIP方式创建service

通过清单创建ClusterIP，这种方式创建的service只能在集群内部访问

```yaml
#vim redis-svc.yaml
apiVersion: v1
kind: Service
metadata:
	name: redis
	namespace: default
spec:
	selector: #选择具有下面label的Pod将会被service匹配到
		app: redis
		role: logstor
	clusterIP: 10.97.97.97 #将service的IP写死
	type: ClusterIP
	ports:
	-	port: 6379 #service暴露的端口
		targetPort: 6379 #容器所在的Pod的端口
```

```shell
#创建服务
kubectl apply -f redis-svc.yaml

#查看服务
kubectl get svc
```

![1563866549311](E:\git-workspace\note\images\docker\1563866549311.png)

* endpoint 与 service 与 pod的关系，我们可以简单的理解为：service到pod

![1563866652862](E:\git-workspace\note\images\docker\1563866652862.png)



资源记录的格式：SVC_NAME.NS_NAME.DOMAIN.LTD

```shell
#默认的格式
svc.cluster.local
redis.default.svc.cluster.local
```

## NodePort类型的service

* 生成一个类型为NodePort的service

```yaml
#vim myapp-svc.yaml
apiVersion: v1
kind: Service
metadata:
	name: myapp
	namespace: default
spec:
	selector: 
		app: myapp
		role: canary
	clusterIP: 10.99.99.99
	type: NodePort
	ports:
	-	port: 80  #service端口
		targetPort: 80 #Pod端口
		nodePort: 30080 #节点端口,确保不会和其他进程冲突
```

创建

```shell
kubectl apply -f myapp-svc.yaml
```

![1563867144303](E:\git-workspace\note\images\docker\1563867144303.png)

这样我们就可以在集群机器外访问

![1563867210671](E:\git-workspace\note\images\docker\1563867210671.png)



* ExternalName

  通过service请求外部服务

  ```yaml
  #CNAME记录
  kubectl explain svc.spec.externalName
  #ClientIP None
  ```

* 将同一个客户端的请求调度到同一个Pod

  ```shell
  #直接在原来的基础上打补丁
  kubectl patch svc myapp -p '{"spec":{"sessionAffinity":"ClientIP"}}'
  ```

  ![1563868114482](E:\git-workspace\note\images\docker\1563868114482.png)

* 无头service

  此时的service是没有cluster_ip的，直接解析service的name到Pod的IP

  ```shell
  kubectl explain svc.spec.clusterIP
  ```

  ```yaml
  #vim myapp-svc-headless.yaml
  apiVersion: v1
  kind: Service
  metadata:
  	name: myapp
  	namespace: default
  spec:
  	selector: 
  		app: myapp
  		role: canary
  	clusterIP: None
  	ports:
  	-	port: 80
  		targetPort: 80
  ```

  