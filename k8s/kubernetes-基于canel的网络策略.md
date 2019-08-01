[TOC]



calico : ip in ip 提供网络策略

canel:提供网络

[calico](https://docs.projectcalico.org/v3.8/getting-started/kubernetes/installation/flannel)



# 部署calico

calico自带有一个配置清单，也可以使用kubernetes的配置清单（etcd)，建议使用后者，因为只用维护一套配置清单

![1564660986827](E:\git-workspace\note\images\docker\1564660986827.png)



# 定义策略

## 原理

![1564661545080](E:\git-workspace\note\images\docker\1564661545080.png)



## 帮助文档

```shell
[root@spark03 ~]# kubectl explain networkpolicy.spec

FIELDS:
   egress       <[]Object> #出栈(Egress)：限制对方的端口和地址
   ingress      <[]Object> #入栈(Ingress)：限制对方的地址和客户端的端口
   podSelector  <Object> -required- #对哪些Pod应用规则
   policyTypes  <[]string> # "Ingress", "Egress", or "Ingress,Egress"(#默认没有定义，ingress,egress都生效；然后你指定其中一个，那么只有其中一个生效)
   
   
   
[root@spark03 ~]# kubectl explain networkpolicy.spec.egress.to
FIELDS:
   ipBlock      <Object>
   namespaceSelector    <Object>
   podSelector  <Object>
    
```

## 定义规则

定义两个网络名称空间

```shell
kubectl create namespace dev
kubectl create namespace prod
```

定义策略

````yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
	name: deny-all-ingress
spec:
	podSelector: {} #表示选中名称空间中的所有Pod	
	policyTypes:
	-	Ingress #拒绝一切入栈，出栈没有定义，那么可以随意出栈
````

在dev这个名称空间下创建规则

![1564663283155](E:\git-workspace\note\images\docker\1564663283155.png)

在dev这个名称空间下创建Pod

```yaml
apiVersion: v1
kind: Pod
metadata:
	name: pod1
spec:
	containers:
	-	name: myapp
		image: ikubernetes/myapp:v1
```

![1564663548418](E:\git-workspace\note\images\docker\1564663548418.png)

我们访问这个Pod，`curl 10.244.2.2`，发现访问不了，但是如果我们将这个Pod创建在prod这个名称空间中(在prod中是没有定义任何规则的)，我们再次curl是可以访问的

![1564663688145](E:\git-workspace\note\images\docker\1564663688145.png)

放行dev中的所有Pod

```yaml
# vim ingress-def.yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
	name: deny-all-ingress
spec:
	podSelector: {}
    ingress: #ingress所有被允许
    -	{}
	policyTypes:
	-	Ingress
```

![1564664005339](E:\git-workspace\note\images\docker\1564664005339.png)

放行dev中创建的一个Pod，先给放行的Pod打一个标签

```shell
kubectl label pods pod1 app=myapp -n dev
```

放行标签为`app=myapp`的入栈Pod

```yaml
# vim allow-netpol-demo.yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
	name: allow-myapp-ingress
spec:
	podSelector:
		matchLabels:
			app: myapp
    ingress: 
    -	from:
    	-	ipBlock:
    			cidr: 10.244.0.0/16
    			except: #不包含下面的地址
    			-	10.244.1.2/32
    	port:
    	-	protocol: TCP
    		port: 80
	policyTypes:
	-	Ingress
```

![1564664474351](E:\git-workspace\note\images\docker\1564664474351.png)

我们分别访问80和443端口

![1564664516386](E:\git-workspace\note\images\docker\1564664516386.png)



定义Egress

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
	name: deny-all-ingress
spec:
	podSelector: {} #表示选中名称空间中的所有Pod	
	policyTypes:
	-	Egress #拒绝一切出栈，入栈没有定义，那么可以随意出栈
```

允许所有出栈

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
	name: deny-all-ingress
spec:
	podSelector: {} #表示选中名称空间中的所有Pod
	egress:
	-	{} #允许所有的出栈
	policyTypes:
	-	Egress 
```



> 网络策略
>
> ​	名称空间：1.拒绝所有出站，入站；2.放行所有出站目标本名称空间内的所有Pod





