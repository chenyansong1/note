[TOC]





CNI：容器网络接口

# 使用到的网络

node network

pod network

cluster network(service network or virtual IP 仅仅出现在service的规则中)

kube-proxy将service的



# Service 的工作模式

userspace:1.1-

iptables:1.10-

ipvs:1.11+

userspace

![1563862678106](E:\git-workspace\note\images\docker\1563862678106.png)

增加或者删除一个Pod，都会被apiserver的etcd检测到，然后proxy通过watch到apiserver的变化，proxy通知ipvs

# 使用清单创建service

```shell
#查看创建步骤
kubectl explain svc

kubectl explain svc.spec
#ports：与哪些后端端口产生关联关系
#selector:需要关联到哪些Pod资源上
#clusterIP:动态分配，当然是可以指定固定的
#type: service类型：ExternalName, ClusterIP, NodePort, LoadBalancer
```

* 通过清单创建ClusterIP

```yaml
#vim redis-svc.yaml
apiVersion: v1
kind: Service
metadata:
	name: redis
	namespace: default
spec:
	selector: 
		app: redis
		role: logstor
	clusterIP: 10.97.97.97
	type: ClusterIP
	ports:
	-	port: 6379 
		targetPort: 6379
```

```shell
#创建服务
kubectl apply -f redis-svc.yaml

#查看服务
kubectl get svc
```

![1563866549311](E:\git-workspace\note\images\docker\1563866549311.png)

endpoint 与 service 与 pod的关系

![1563866652862](E:\git-workspace\note\images\docker\1563866652862.png)

资源记录的格式：SVC_NAME.NS_NAME.DOMAIN.LTD

```shell
#默认的格式
svc.cluster.local
redis.default.svc.cluster.local
```

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

  