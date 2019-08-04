[TOC]

# 容器的资源需求与资源限制设定

CPU: 1颗逻辑CPU，1=1000毫核 millicores，例如：500m=0.5CPU

Memory: E , P, T, G, M, K  —  Ei, Pi,….

![image-20190804090249372](/Users/chenyansong/Documents/note/images/docker/image-20190804090249372.png)

```yaml
apiVersion: v1
kind: Pod
metadata:
	name: pod-demo
	labels:
		app: myapp
		tier: frontend
spec:
	containers:
	-	name: myapp
		image: ikubernetes/stress-ng  #压测镜像
		command: ["/usr/bin/stress-ng", "-m 1", "-c 1", "--metrics-brief"]
		resources:
			requests:
				cpu: "200m"
				memory: "128Mi"
			limits:
				cpu: "500m"
				memory: "512Mi"
```

我们创建镜像之后，监测Pod使用了多少资源(现在的CPU资源是4核心，但是pod的配置只是使用0.5个核心，就是1/8=13%)

![image-20190804093652007](/Users/chenyansong/Documents/note/images/docker/image-20190804093652007.png)



QoS: quailty of Service (服务质量监测)

* Guranteed: 同时设置了CPU和内存的request和limit，cpu.limits=cpu.requests, memory.limits = memory.requests 这类Pod的优先级最高
* Burstable:至少有一个容器设置了CPU或内存资源的request属性，具有中等优先级
* BestEffort:没有任何一个容器设置了request或limit属性，最低优先级别

当集群资源不够用时，以BestEffort运行的Pod会被优先终止，确保其他优先级别的Pod能够运行

已经占用量(current)与需求量(request)的比率，大的优先被干掉

 

# HeapSter

我们想要查看node或者是Pod所占资源的情况，此时就要用到一个addon 插件-heapster，他是一个部署在集群级别的，用来收集CPU，memory资源的采集工具

在每个节点上有一个kubelet，每个kubelet中有一个工具

cAdvisor用于采集本节点的资源信息，而HeapSter就是用来统一收集cAdvisor上的每个节点的信息，HeapStrer采集的数据将存入InfluxDB，而Grafana以InfluxDB作为数据源做展示

![image-20190804101305334](/Users/chenyansong/Documents/note/images/docker/image-20190804101305334.png)

1. kubernetes系统指标
2. 容器指标(cpu,memory资源)
3. 应用指标(业务指标，有多少人访问等)



monitor-influxdb



# 安装

[heapster官网](https://github.com/kubernetes-retired/heapster)

## influxdb

```yaml
#wget https://raw.githubusercontent.com/kubernetes-retired/heapster/master/deploy/kube-config/influxdb/influxdb.yaml


apiVersion: apps/v1  #修改下
kind: Deployment
metadata:
  name: monitoring-influxdb
  namespace: kube-system
spec:
	selector:  #添加
		matchLabels:
			task: monitoring
			k8s-app: influxdb

```

![image-20190804112626871](/Users/chenyansong/Documents/note/images/docker/image-20190804112626871.png)



## heapster

创建rbac

```shell
kubectl apply -f https://raw.githubusercontent.com/kubernetes-retired/heapster/master/deploy/kube-config/rbac/heapster-rbac.yaml
```

创建heapster

```shell
kubectl apply -f https://github.com/kubernetes-retired/heapster/raw/master/deploy/kube-config/influxdb/heapster.yaml
```



## grafana

对外开放

```shell
wget https://github.com/kubernetes-retired/heapster/raw/master/deploy/kube-config/influxdb/grafana.yaml
```

![image-20190804114830512](/Users/chenyansong/Documents/note/images/docker/image-20190804114830512.png)

需要对外开放

![image-20190804114913801](/Users/chenyansong/Documents/note/images/docker/image-20190804114913801.png)



创建

```shell
kubectl apply -f grafana.yaml
```

查看服务

![image-20190804115023859](/Users/chenyansong/Documents/note/images/docker/image-20190804115023859.png)

我们查看开放出来的界面

![image-20190804115132178](/Users/chenyansong/Documents/note/images/docker/image-20190804115132178.png)



> heapster已经从1.12之后将废弃

