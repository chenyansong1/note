[TOC]

参见：https://www.cnblogs.com/justmine/p/8991379.html



# Ingress原理

现在有这样一个需求，我们需要在一个web中加上HTTPS的证书验证，但是我们的请求是下图的样子：

![1564127582810](E:\git-workspace\note\images\docker\1564127582810.png)

我们需要在Pod中卸载HTTPS的证书验证，但是Pod是有生命周期的并且没有状态，所以这种方式是：既贵且慢，我们想要在Pod的前面，service的后面放一个Pod来统一卸载证书，如下图：

![1564127803999](E:\git-workspace\note\images\docker\1564127803999.png)

但是这种方式增加中间层的访问，于是有了下面的方式，将**证书卸载Pod**共享Node的网络名称空间，这样外面可以直接访问这个Pod，而不用通过service进行转发，如下图

![1564128006390](E:\git-workspace\note\images\docker\1564128006390.png)

这样每个Node上只需要有一个这样的Pod即可，但是怎么保证这种Pod的单节点问题，使用DaemonSet（在有限的节点上运行此类Pod，并将这几个节点设置为污点，这样只能这些Pod在上面运行），我们将这样的Pod命名为**Ingress Controller**，这种controller的目前的实现方式有如下的几种：Envoy， Traefik， nginx，他们都能实现调度

调度的配置文件实时更新是通过ingress资源实现的，服务（service）其实是帮忙分组的功能，并没有其他的用处，Ingress资源通过service得到后端Pod的信息，然后将这些信息动态注入到Ingress Controller中

![](E:\git-workspace\note\images\docker\1564120148053.png)

1. 客户端首先对`kubia.example.com`执行DNS查询，DNS服务器(或本地操作系统)返回了Ingress控制器的IP
2. 客户端然后向Ingress控制器发送HTTP请求
3. 并在Host头中指定`kubia.example.com`
4. 控制器从该头部确定客户端访问的是哪个服务，通过与该服务关联的Endpoint对象查看Pod IP，并将客户端的请求转发给其中一个Pod

> 如上图，Ingress控制器不会将请求转发给该服务，只用他来选择一个Pod，大多数控制器都是这样来工作的


# 创建Ingress资源

```yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
	name: kubia
spec:
	rules:
	-	host: kubia.example.com #Ingress将域名映射到你的服务
		http:
			paths:
			-	path: /
				backend:
					serviceName: kubia-nodeport	#将所有的请求都发送到kubia-nodeport:80
					servicePort: 80
```



# 通过Ingress暴露多个服务

一个Ingress可以将多个**主机和路径**映射到多个服务

## 将不同的服务映射到相同主机的不同路径

```yaml
- 	host: kubia.example.com
	http: 
	- 	path: /kubia
		backend:
			serviceName: kubia #kubia.example.com/kubia的请求将会转发至kubia服务
			servicePort: 80
	-	path: /foo
		backend:
			serviceName: bar #kubia.example.com/bar的请求将会转发至bar服务
			servicePort: 80
```



## 将不同的服务映射到不同的主机上

```yaml
spec:
	rules:
	-	host: foo.example.com
		http:
			paths:
			-	path: /
				backend:
					serviceName: foo #对foo.example.com的请求将会转发至foo服务
					servicePort: 80
	-	host: bar.example.com
		http:
			paths:
			-	path: /
				backend:
					serviceName: bar #bar.example.com的请求将会转发至bar服务
					servicePort: 80
```

# 配置Ingress处理TLS

当客户端创建到Ingress控制器的TLS连接时，控制器将终止TLS连接，**客户端和控制器之间的通信是加密的，而控制器和后端Pod之间的通信则不是，运行在Pod上的应用程序不需要支持TLS**，例如，如果Pod运行web服务器，则他只能接收HTTP通信，并让Ingress控制器负责处理与TLS相关的所有内容，要控制器能够这样做，**需要将控制器和私钥附加到Ingress**，这两个必需资源在称为Secret的Kubernetes资源中，然后在Ingress manifest中引用他

iptables, ipvs是四层调度器，工作在TCP/IP协议栈，只能对IP做调度，这里是没法解除SSL回话的，这就需要再Pod中解除SSL，但是SSL回话既贵且慢，所以应该尽可能让前端调度器（service）卸载

![1564122131277](E:\git-workspace\note\images\docker\1564122131277.png)

```yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
	name: kubia
spec:
	tls:
	-	hosts:
		-	kubia.example.com #将接收来自kubia.example.com主机的TLS连接
		secretName: tls-secret #从tls-secret中获得之前创立的私钥和证书
	rules:
	-	host: kubia.example.com 
		http:
			paths:
			-	path: /
				backend:
					serviceName: kubia-nodeport
					servicePort: 80
```

配置证书之后，我们可以访问，如下的结果

![1564122489486](E:\git-workspace\note\images\docker\1564122489486.png)





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

4. 创建ingress

   ```shell
   #
   kubectl explain ingress.spec
   
   ```

   ![1563935896678](E:\git-workspace\note\images\docker\1563935896678.png)





