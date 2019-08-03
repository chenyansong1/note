[TOC]



运行一个Pod需要的最低资源，以及这个Pod能够使用的最大资源限制，以及这个Pod需要开放的端口，根据这些条件**预选**出来一些满足条件的节点，然后根据**优选函数**，找出最佳的node节点，最后将这个Pod绑定到选定的node节点上

Predicate(预选)-> Priority(优选)->Select(选定)



Pod亲和性：如需要将一组有关联关系的应用调度到一个机架或者一个节点中

Pod反亲和性：如将httpd和NGINX同时提供80服务，那么需要将他们调度到不同的节点

污点node：如果一些Pod能够容忍node的污点，那么该Pod就可以运行在该node上

容忍度：如果一个Pod已经调度在节点上，但是节点又出现了新的Pod不能容忍的污点，那么两种方式：1.Pod离开该节点；2.该节点在限定期限内驱离该Pod



# 预选策略

* CheckNodeCondition：检查节点的磁盘，网络等是否正常

* GeneralPredicates:
  * HostName:检查Pod对象是否定义了pod.spec.hostname的值
  * PodFitsHostPorts: pods.spec.containers.ports.hostPort
  * MatchNodeSelector：pods.spec.nodeSelector
  * PodFitsResources: 检查节点是否有足够的资源支撑Pod

* NoDiskConflict：Pod的卷请求是否可以在此节点使用，默认没有启用

* PodToleratesNodeTaints:检查Pod上的spec.tolerations可容忍的污点是否完全包含节点上的污点

* PodToleratesNodeNoExecuteTaints:不能执行的污点，动态下不能容忍节点时，节点会驱离Pod，默认是没有启用

* CheckNodeLabelPresence：检查节点上指定标签的存在性，默认是没有启用

* CheckServiceAffinity：相同Service的Pod对象尽可能放在相同的节点上，默认也没有启用
* CheckVolumeBinding:检查节点上的pvc
* NoVolumeZoneConflict:
* CheckNodeMemoryPressure:检查内存节点是否存在压力
* CheckNodePIDPressure：检查节点上的PID是否过多
* CheckNodeDiskPressure:
* MatchInterPodAffinity:



# 优选函数

将启用的所有的优选函数得分总和进行比较

* LeastRequested

  [cpu( (capacity-sum(requested))*10/capacity ) + memory( (capacity-sum(requested))*10/capacity ) ]/2

* BalancedResourceAllocation:cpu和内存资源被占用的比率，相近的胜出
* NodePrefreAvoidPods:根据节点的注解信息
* TaintToleration：将Pod对象的spec.tolerations与节点的taints列表项进行匹配度检查
* SelectorSpreading：尽可能的将Pod分散

* InterPodAffinity：
* NodeAffinity：
* MostRequested:尽可能的集中运行节点，默认没有启用
* NodeLabel：，默认没有启用
* ImageLocality:有Pod需要的镜像体积大小就得分，默认没有启用





# 高级调度方式

## 节点选择器调度

### nodeSelector，nodeName

```yaml
#kubectl explain pods.spec.nodeSelector

apiVersion: v1
kind: Pod
metadata:
	name: pod-demo
	namespace: default
spec:
	containers:
	-	name: myapp
		image: ikubenetes/myapp:v1
	nodeSelector:
		disktype: ssd		#选择有“disktype=ssd”的节点
```

我们给node01打上标签

![image-20190803175226550](/Users/chenyansong/Documents/note/images/docker/image-20190803175226550.png)

我们查看该Pod是否运行在node01上

![image-20190803175402147](/Users/chenyansong/Documents/note/images/docker/image-20190803175402147.png)

如果我们改变Pod的nodeSelector的值`disktype=harddisk`,那么此时的Pod创建时会处于Pending状态，这就意味着nodeSeletor属于强约束

![image-20190803175545426](/Users/chenyansong/Documents/note/images/docker/image-20190803175545426.png)



同时我们可以查看该Pod的创建过程

```shell
kubectl describe pods pod-demo
```

![image-20190803175721374](/Users/chenyansong/Documents/note/images/docker/image-20190803175721374.png)

那么我们将node02打上标签`disktype=harddisk`

```shell
kubectl label nodes node02.magedu.com disktype=harddisk
```

我们再次查看Pod所在的节点

![image-20190803175849971](/Users/chenyansong/Documents/note/images/docker/image-20190803175849971.png)



### Affinity亲和性

```yaml
#kubectl explain pods.spec.affinity

#nodeAffinity
#podAffinity
#podAntAffinity

#kubectl explain pods.spec.affinity.nodeAffinity
#preferredDuringSchedulingIgnoredDuringExecution 尽量满足的条件
#requiredDuringSchedulingIgnoredDuringExecution 必须满足的条件：硬亲和性


#kubectl explain pods.spec.affinity.nodeAffinity.requiredDuringSchedulingIgnoredDuringExecution.nodeSelectorTerms

apiVersion: v1
kind: Pod
metadata:
	name: pod-node-affinity-demo
	namespace: default
spec:
	containers:
	-	name: myapp
		image: ikubenetes/myapp:v1
	affinity:
		nodeAffinity:
			requiredDuringSchedulingIgnoredDuringExecution
				nodeSelectorTerms:
				-	matchExpressions:
					-	key: zone			# zone in (foo, bar)
						operator: In
						values: 
						-	foo
						- bar
```

我们去创建Pod，但是没有任何节点拥有上面定义的标签，所有Pod处于Pending状态

![image-20190803181952420](/Users/chenyansong/Documents/note/images/docker/image-20190803181952420.png)



```yaml
apiVersion: v1
kind: Pod
metadata:
	name: pod-node-affinity-demo
	namespace: default
spec:
	containers:
	-	name: myapp
		image: ikubenetes/myapp:v1
	affinity:
		nodeAffinity:
			preferredDuringSchedulingIgnoredDuringExecution:
			-	preference:
					matchExpressions:
					-	key: zone
						operator: In
						values:
						-	foo
						- bar
				weight: 60
```

我们可以看到Pod是可以被创建的为Running的，也就说明preferredDuringSchedulingIgnoredDuringExecution不是硬亲和

![image-20190803183134265](/Users/chenyansong/Documents/note/images/docker/image-20190803183134265.png)



> 上面的调度方式，需要同时考虑到节点和Pod，让Pod的标签和节点的标签去匹配，这种方式，我们既要给节点打标签，又要给Pod打标签



## Pod亲和调度

单独的Pod亲和性：一组Pod中，最先调度的一个Pod(或一组Pod中的几个)会随机的进入一个节点，然后一组Pod中剩下的Pod会尽可能的调度到和第一个Pod相同的位置(如在同一个机架上，或者在同一个机房，这样就可以让一组Pod的通信更加的高效)



