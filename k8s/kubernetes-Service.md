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

![1563955572514](https://github.com/chenyansong1/note/blob/master/images/docker/1563955572514.png?raw=true)



## iptables

1. Client Pod 直接请求ServiceIP，然后ServiceIP将请求转发到对应的Pod

![1563955926922](https://github.com/chenyansong1/note/blob/master/images/docker/1563955926922.png?raw=true)



## ipvs

增加或者删除一个Pod，都会被apiserver的etcd检测到，然后kube-proxy通过watch到apiserver的变化，kube-proxy通知ipvs

![1563862678106](https://github.com/chenyansong1/note/blob/master/images/docker/1563862678106.png?raw=true)

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

![1563866549311](https://github.com/chenyansong1/note/blob/master/images/docker/1563866549311.png?raw=true)

* endpoint 与 service 与 pod的关系，我们可以简单的理解为：service到pod

![1563866652862](https://github.com/chenyansong1/note/blob/master/images/docker/1563866652862.png?raw=true)



资源记录的格式：

```shell
SVC_NAME.NS_NAME.DOMAIN.LTD.
#默认的格式
svc.cluster.local.
#eg
redis.default.svc.cluster.local.
```

## NodePort类型的service

NodePort（在ClusterIP的基础上增强）：client->NodeIP:NodePort->ClusterIP:ServicePort->PodIP:containerPort 此时的NodeIP是有多个的，所以前面需要加上一个负载均衡器，k8s如果部署在公有云上，并且公有云支持lbaas

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
		nodePort: 30080 #节点端口,确保不会和其他进程冲突,type=NodePort是需要指定这个参数
```

创建

```shell
kubectl apply -f myapp-svc.yaml
```

下图，可以看到，service上的端口映射为node上的30080

![1563867144303](https://github.com/chenyansong1/note/blob/master/images/docker/1563867144303.png?raw=true)

这样我们就可以在集群机器外访问：访问的顺序是：node:port->service:port->Pod:port

![1563867210671](https://github.com/chenyansong1/note/blob/master/images/docker/1563867210671.png?raw=true)



## ExternalName

集群内的pod想要请求集群外的服务(如互联网上，或者是本地局域网中)

通过service请求外部服务

![image-20190725205559725](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190725205559725.png?raw=true)

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

  ![1563868114482](https://github.com/chenyansong1/note/blob/master/images/docker/1563868114482.png?raw=true)



## 无头service(headless service)

无头服务（No ClusterIP , Headless Service）：ServiceName -> PodIP(不进过serviceIP)

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

我们解析一下，这个无头的service域名，



![image-20190725215826376](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190725215826376.png?raw=true)

我们查看Pod的IP

![image-20190725215955896](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190725215955896.png?raw=true)



我们查看dns服务的地址

![image-20190725215850302](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190725215850302.png?raw=true)



但是对于有头的服务，解析出来的一定是clusterIP自己的地址

![image-20190725220050742](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190725220050742.png?raw=true)






# NodePort，LoadBalancer还是Ingress？我该如何选择 

转自：https://www.cnblogs.com/justmine/p/8628465.html

当我们使用k8s集群部署好应用的Service时，默认的Service类型是ClusterIP，这种类型只有 Cluster 内的节点和 Pod 可以访问。如何将应用的Service暴露给Cluster外部访问呢，Kubernetes 提供了多种类型的 Service，如下：

## ClusterIP

ClusterIP服务是Kuberntets的默认服务。它在集群内部生成一个服务，供集群内的其他应用访问。外部无法访问。

ClusterIP服务的 YAML 文件如下：

```yaml
apiVersion: v1
kind: Service
metadata:  
  name: my-internal-service
selector:    
  app: my-app
spec:
  type: ClusterIP
  ports:  
  - name: http
    port: 80
    targetPort: 80
    protocol: TCP
```

如果不能从互联网访问ClusterIP服务，那我们还介绍它干啥？其实，我们可以使用Kubernetes proxy来访问它！

![img](https://github.com/chenyansong1/note/blob/master/images/docker/1082769-20180323092525525-1026425.png?raw=true)

 

开启Kubernetes Proxy：

```
$ kubectl proxy --port=8080
```

现在可以通过Kubernetes API使用下面这个地址来访问这个服务：

```
http://localhost:8080/api/v1/proxy/namespaces/<NAMESPACE>/services/<SERVICE-NAME>:<PORT-NAME>/
```

为了访问上面定义的服务，可以使用下面这个地址：

```
http://localhost:8080/api/v1/proxy/namespaces/default/services/my-internal-service:http/
```

使用场景

在某些场景下，你会使用Kubernetes proxy来访问服务：

1. 调试服务，或者是因为某些原因需要从电脑直接连接服务；
2. 允许内部流量，显示内部仪表盘等。

这个访问需要你作为一个已验证的用户去运行kubectl，所以不要通过这种方式将服务发布到互联网，或者是在生产环境下使用。

## NodePort

NodePort服务是让外部流量直接访问服务的最原始方式。NodePort，顾名思义，在所有的节点（虚拟机）上开放指定的端口，所有发送到这个端口的流量都会直接转发到服务。

NodePort服务的YAML文件如下：

```yaml
apiVersion: v1
kind: Service
metadata:  
 name: my-nodeport-service
selector:   
 app: my-app
spec:
 type: NodePort
 ports:  
 - name: http
  port: 80
  targetPort: 80
  nodePort: 30036
  protocol: TCP
```

从本质上来看，NodePort服务有两个地方不同于一般的“ClusterIP”服务。首先，它的类型是“NodePort”。还有一个叫做“nodePort"的端口，能在节点上指定开放哪个端口。如果没有指定端口，它会选择一个随机端口。大多数时候应该让Kubernetes选择这个端口，就像谷歌领导人Thockin说的，关于能使用哪些端口，有很多注意事项。

使用场景

这种方式有一些不足：

1. 一个端口只能供一个服务使用；
2. 只能使用30000–32767的端口；
3. 如果节点 / 虚拟机的IP地址发生变化，需要进行处理。

因此，我不推荐在生产环境使用这种方式来直接发布服务。如果不要求运行的服务实时可用，或者在意成本，这种方式适合你。例如用于演示的应用或是临时运行就正好用这种方法。

## LoadBalancer

LoadBalancer服务是发布服务到互联网的标准方式。在GKE中，它会启动一个Network Load Balancer，分配一个单独的IP地址，将所有流量转发到服务中。

![img](https://github.com/chenyansong1/note/blob/master/images/docker/1082769-20180323092559041-1827526722.png?raw=true)

 

使用场景

如果你想直接发布服务，这是默认方式。指定端口的所有流量都会转发到服务中，没有过滤，也没有路由。这意味着你几乎可以发送任意类型的流量到服务中，比如HTTP、TCP、UDP、Websockets、gRPC等等。

这里最大的不足是，使用LoadBalancer发布的每个服务都会有一个自己的IP地址，你需要支付每个服务的LoadBalancer 费用，这是一笔不小的开支。

## Ingress 

Ingress实际上不是一种服务。相反，它在多个服务前面充当“智能路由”的角色，或者是集群的入口。

使用Ingress可以做很多事情，不同类型的Ingress控制器有不同的功能。

默认的GKE ingress控制器会启动一个 HTTP(S) Load Balancer，可以通过基于路径或者是基于子域名的方式路由到后端服务。例如，可以通过foo.yourdomain.com 发送任何东西到foo服务，或者是发送yourdomain.com/bar/路径下的任何东西到bar服务。

![img](https://github.com/chenyansong1/note/blob/master/images/docker/1082769-20180323092723446-545194130.png?raw=true)

对于使用第 7 层HTTP Load Balancer 的GKE上的Ingress对象，其YAML文件如下：

```yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: my-ingress
spec:
  backend:
    serviceName: other
    servicePort: 8080
  rules:
  - host: foo.mydomain.com
    http:
      paths:
      - backend:
          serviceName: foo
          servicePort: 8080
  - host: mydomain.com
    http:
      paths:
      - path: /bar/*
        backend:
          serviceName: bar
          servicePort: 8080
```

使用场景

Ingress可能是发布服务最强大的方式，同时也是最复杂的。Ingress控制器的类型很多，如 Google Cloud Load Balancer，Nginx，Contour，Istio等等。还有一些Ingress控制器插件，比如证书管理器，可以自动为服务提供SSL认证。

如果想在同一个IP地址下发布多个服务，并且这些服务使用相同的第 7 层协议（通常是 HTTP），Ingress是最有用的。如果使用原生的GCP集成，只需要支付一个负载均衡器的费用。因为Ingress是“智能”的，你可以得到很多开箱即用的特性（比如SSL、认证、路由等）



service： 22：00:22