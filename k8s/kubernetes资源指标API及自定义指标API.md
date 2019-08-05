[TOC]



新一代监控：

系统资源指标：metrics-server

自定义指标：prometheus， k8s-prometheus-adapter

1. 核心指标流水线

   由kubectl, metrics-server,api-server提供的API组成：CPU(累计使用率)，内存实时使用率，Pod的资源占用率，容器的磁盘占用率

2. 监控流水线

   用于从系统收集各种指标数据并提供终端用户，存储系统以及HPA，他们包含核心指标及许多非核心指标，非核心指标本身不能被k8s所解析



# 部署metrics-server

[官网](https://github.com/kubernetes-incubator/metrics-server/tree/master/deploy/1.8%2B)

但是我们使用kubectl的addon进行部署 [地址](https://github.com/kubernetes/kubernetes/tree/master/cluster/addons/metrics-server)

![1564995013971](E:\git-workspace\note\images\docker\1564995013971.png)

```shell
#将下面的文件下载下来，然后apply
kubectl apply -f ./

#查看创建的服务
kubectl get svc -n kube-system

#查看Pod
kubectl get pod -n kube-system
```

查看API

![1564995258678](E:\git-workspace\note\images\docker\1564995258678.png)



metrics-server部署过程中，可能出现的问题

![1564996027260](E:\git-workspace\note\images\docker\1564996027260.png)

修改授权：**resource-reader.yaml**

```yaml
rules:
- apiGroups:
  - ""
  resources:
  - pods
  - nodes
  - nodes/stats  #新增的项
  - namespaces
```

查看metrics-server的api

```shell
#将metrics-server的代理开出来
kubectl proxy --port=8080

#curl http://localhost:8080/apis/metrics.k8s.io/v1beta1
```

![1564995493259](E:\git-workspace\note\images\docker\1564995493259.png)

查看nodes数据

![1564995532550](E:\git-workspace\note\images\docker\1564995532550.png)

通过top查看nodes

![1564995712105](E:\git-workspace\note\images\docker\1564995712105.png)

通过top查看pods

![1564995833848](E:\git-workspace\note\images\docker\1564995833848.png)

![1564995856386](E:\git-workspace\note\images\docker\1564995856386.png)



# prometheus

自定义监控指标

![1564997704531](E:\git-workspace\note\images\docker\1564997704531.png)

![1564999239076](E:\git-workspace\note\images\docker\1564999239076.png)



## 部署

![1564997409407](E:\git-workspace\note\images\docker\1564997409407.png)

下面是马哥整理的一个[部署方式](https://github.com/iKubernetes/k8s-prom)

![1564998237243](E:\git-workspace\note\images\docker\1564998237243.png)

1. 先创建名称空间

   ```shell
   kubectl apply -f https://raw.githubusercontent.com/iKubernetes/k8s-prom/master/namespace.yaml
   ```

2. 部署exporter

   ![1564998447660](E:\git-workspace\note\images\docker\1564998447660.png)

   查看创建的Pod

   ![1564998536162](E:\git-workspace\note\images\docker\1564998536162.png)

3. 部署prometheus

   ![1564998607792](E:\git-workspace\note\images\docker\1564998607792.png)

4. 访问prometheus的web界面

   ![1564998897663](E:\git-workspace\note\images\docker\1564998897663.png)

5. 进入kube-state-metrics目录去部署state

   ![1564999059555](E:\git-workspace\note\images\docker\1564999059555.png)

6. 部署k8s-prometheus-adapter

   1. 创建证书的key

   2. 创建证书签署请求

   3. 通过内置的CA去签署

      ![1564999620619](E:\git-workspace\note\images\docker\1564999620619.png)
      
   4. 创建secret
   
      ![1564999774510](E:\git-workspace\note\images\docker\1564999774510.png)
   
   5. 部署yaml
   
      ![1564999857362](E:\git-workspace\note\images\docker\1564999857362.png)
   
   6. 查看是否存在对应的api
   
      ![1565006542292](E:\git-workspace\note\images\docker\1565006542292.png)
   
   ​      
   
   
   
## 整合grafana

   我们不用自己定义模板，去grafana的官网去下载，然后导入

​	[官网](https://grafana.com/grafana/dashboards)

# 自动伸缩hpa

## 手动设置伸缩

设置资源上限

![1565007719851](E:\git-workspace\note\images\docker\1565007719851.png)

设置自动伸缩

![1565007816569](E:\git-workspace\note\images\docker\1565007816569.png)

![1565007867331](E:\git-workspace\note\images\docker\1565007867331.png)

ab测试

```shell
ab -c 1000 -n 500000 http://172.20.0.67:31190/index.html
```



查看Pod是否有变化

![1565008122840](E:\git-workspace\note\images\docker\1565008122840.png)



## 文件设置伸缩

```yaml
apiVersoin: autoscaling/v2beta1
kind: HorizontalPodAutoscaler
metadata:
	name: myapp-hpa-v2
spec:
	scaleTargetDef:
		apiVersion: apps/v1
		kind: Deployment
		name: myapp
	minReplicas: 1
	maxReplicas: 10
	metrics:
	-	type: Resource		#通过CPU评估
		resource:
			name: cpu
			targetAverageUtilization: 55
	-	type: Resource  #通过内存去评估
		resource:
			name: memory
			targetAverageValue: 50Mi
			
```

![1565008519020](E:\git-workspace\note\images\docker\1565008519020.png)

## 自定义指标

考虑并发连接数，增加或者减少Pod的数量

![1565010110637](E:\git-workspace\note\images\docker\1565010110637.png)