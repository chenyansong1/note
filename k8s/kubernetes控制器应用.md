[TOC]

# pod-yaml定义



Pod资源

```shell
spec.containers <[]object>
- name <string>
	image <string>
	
	imagePullPolicy <string> #这里可以指定镜像仓库的地址
		Always(总是从仓库中下载:如果我们想要image，刚好在本地存在，但是这个镜像是伪装的，并不是我们自己的), Never(如果本地有就用，没有也不会从仓库下载), IfNotPresent(如果本地存在直接使用，否则就从仓库下载)
		#如果镜像的标签是latest，那么使用的是Always
		#否则使用的是IfNotPresent
		
	ports <[]object> #这里只是显示的说明暴露了哪些端口
  	containerPort <integer>
  	name <string>
  	protocol <string>
  	
  command <[]string> #如果没有提供，那么将会运行image中的entrypoint
  
  args <[]string> #代替镜像的CMD传递给entrypoint的参数，通过$(var_name) 作为变量的引用
		
```

![image-20190720215028438](/Users/chenyansong/Documents/note/images/docker/image-20190720215028438.png)

![image-20190720215250060](/Users/chenyansong/Documents/note/images/docker/image-20190720215250060.png)

一个资源可以拥有多个标签，而一个标签可以被添加到多个对象上

```shell
key=value
	key:只能是数字，字母，_, -,., 并且只能字母或数字开头及结尾
	value:可以为空，只能是数字，字母，_, -,., 并且只能字母或数字开头及结尾
	
```

```shell
#查看pod的标签
kubectl get pods --show-labels
```

![image-20190720222258006](/Users/chenyansong/Documents/note/images/docker/image-20190720222258006.png)

```shell
#过滤标签:查看有app标签
kubectl get pods -l app

#显示具体的标签是什么
kubectl get pods -l app --show-labels
#显示同时又app,run标签的pod
kubectl get pods -L app,run

#显示每个pod的对应标签的标签的值
kubectl get pods -L app,run
```

![image-20190720222932994](/Users/chenyansong/Documents/note/images/docker/image-20190720222932994.png)



修改资源的标签

![image-20190720223051545](/Users/chenyansong/Documents/note/images/docker/image-20190720223051545.png)

```shell
kubectl label pods pod-demo release=canary
```

![image-20190720223146251](/Users/chenyansong/Documents/note/images/docker/image-20190720223146251.png)

修改已经存在的标签

```shell
kubectl label pods pod-demo release=stable --overwrite
```

![image-20190720223359085](/Users/chenyansong/Documents/note/images/docker/image-20190720223359085.png)



标签选择器：

1. 等值关系的：= or != 

2. 集合关系的: 

   key in (val1, val2….) 

   key notin (val1, val2...)

   key ： 存在这个key就行

   !key : 不存在此键

3. 许多资源支持内嵌字段定义其使用的标签选择器

   matchLabels：直接给定键值对

   matchExpressions:基于给定的表达式来定义使用的标签选择器 {key:"keyName", operator:"=", values:[val1, val2...]}

   ​	operator:

   ​		In， NotIn：values必须为非空列表

   ​		Exists, NotExists：values的值必须为空列表

```shell
#等值关系
kubectl get pods -l release=stable --show-labels
kubectl get pods -l release=stable,app=myapp --show-labels

kubectl get pods -l release !=stable

#集合关系
kubectl get pods -l "release in (canary,beta,alpha)"

kubectl get pods -l "release notin (canary,beta,alpha)"

```



节点也是可以打标签的

```shell
#查看节点的标签
kubectl get nodes --show-labels

#给node打标签
kubectl label nodes node01.test.com disktype=ssd
```

![image-20190721081945136](/Users/chenyansong/Documents/note/images/docker/image-20190721081945136.png)

```shell
nodeSelector <map[String] string>	节点标签选择器

nodeName <String> 运行在指定节点上
```

![image-20190721082319642](/Users/chenyansong/Documents/note/images/docker/image-20190721082319642.png)

因为node01上有这个标签，所以我们重新创建pod的时候，这个pod是运行在node01上的

![image-20190721082632829](/Users/chenyansong/Documents/note/images/docker/image-20190721082632829.png)



```shell
annotations:注解
#与label不同的地方在于，他不能英语挑选资源对象，仅用于为对象提供“元数据”

#查找pod的annotations
kubectl describe pods pod-demo
```

![image-20190721083118944](/Users/chenyansong/Documents/note/images/docker/image-20190721083118944.png)

为资源添加annotation

![image-20190721083339583](/Users/chenyansong/Documents/note/images/docker/image-20190721083339583.png)

之后再次通过yaml文件创建Pod，然后describe查看

![image-20190721083441202](/Users/chenyansong/Documents/note/images/docker/image-20190721083441202.png)



Pod的生命周期

状态：

​	Pending 调度尚未完成

​	Running 运行状态

​	Failed	失败

​	Succeeded

​	Unkown 为止

![image-20190721085119829](/Users/chenyansong/Documents/note/images/docker/image-20190721085119829.png)

容器的重启策略

```shell
restartPolicy
# Always  ： default
#OnFailure： when failure ,will restart
#Never
```



探针类型三种：

```shell
ExecAction
TCPSockcetAction
HTTPGetAction

#查看container的探针
#kubectl explain pods.spec.containers

```

![image-20190721091732018](/Users/chenyansong/Documents/note/images/docker/image-20190721091732018.png)

livenessProbe：存活性探测

```shell
kubectl explain pods.spec.containers.livenessProbe
#他下面可以定义这三种探针
ExecAction
TCPSockcetAction
HTTPGetAction

#探测几次
failureThreshold <integer>
#默认是探测3次，3次之后，就返回失败

#在liveness 探测的延迟探测时间，需要等待的时间(等待初始化完成)
initialDelaySeconds

#每次间隔的时长
periodSeconds <integer>
#默认是10秒探测一次

#每次探测如果没有响应，需要等待的时长，默认是1s
timeoutSeconds

```

下面就exec探针进行说明

```shell
kubectl explain pods.spec.containers.livenessProbe.exec

#返回0表示healthy, 返回非0白鸥是unhealthy
```

![image-20190721102840091](/Users/chenyansong/Documents/note/images/docker/image-20190721102840091.png)

tcpSocket

![image-20190721103415928](/Users/chenyansong/Documents/note/images/docker/image-20190721103415928.png)



httpGet

![image-20190721103509385](/Users/chenyansong/Documents/note/images/docker/image-20190721103509385.png)

![image-20190721103825924](/Users/chenyansong/Documents/note/images/docker/image-20190721103825924.png)

我们连接进入上面的Pod的容器

```shell
kubectl -it liveness-httpget-pod -- /bin/sh
#手动删除文件
```

![image-20190721104120338](/Users/chenyansong/Documents/note/images/docker/image-20190721104120338.png)



readinessProbe

![image-20190721122647088](/Users/chenyansong/Documents/note/images/docker/image-20190721122647088.png)

我们删除readinessProbe探测的文件

![image-20190721122832459](/Users/chenyansong/Documents/note/images/docker/image-20190721122832459.png)

我们再次查看创建pod的状态

![image-20190721122921648](/Users/chenyansong/Documents/note/images/docker/image-20190721122921648.png)

我们进入pod内部，看NGINX服务是存在的

![image-20190721123127168](/Users/chenyansong/Documents/note/images/docker/image-20190721123127168.png)

我们重新创建删除的文件

![image-20190721123157622](/Users/chenyansong/Documents/note/images/docker/image-20190721123157622.png)

我们可以看到pod又就绪了

![image-20190721123216746](/Users/chenyansong/Documents/note/images/docker/image-20190721123216746.png)



启动后钩子和终止前钩子

```shell
kubectl explain pod.spec.containers.lifecycle

```

![image-20190721123530982](/Users/chenyansong/Documents/note/images/docker/image-20190721123530982.png)

对于启动后，或者是终止前，我们可以看到也是有三种探针

![image-20190721123621336](/Users/chenyansong/Documents/note/images/docker/image-20190721123621336.png)

我们创建一个postStart的Pod

![image-20190721125857115](/Users/chenyansong/Documents/note/images/docker/image-20190721125857115.png)



进入容器内部，查看是否创建了目录

![image-20190721125529545](/Users/chenyansong/Documents/note/images/docker/image-20190721125529545.png)



# Pod控制器

* ReplicaSet： (ReplicationController的新一代，推荐使用)，不是我们直接使用的控制器

  满足用户数量的副本

  标签选择器：选择由自己管理和控制的副本

  Pod资源模板来完成Pod的新建

  扩缩容操作

* Deployment(无状态)：建构在ReplicaSet之上，通过ReplicaSet来控制Pod

  ReplicaSet的功能

  滚动更新

  回滚机制

  申明式配置：随时改变资源的运行状态

* DaemonSet(无状态)：集群中的每个节点只会运行一个特定的Pod副本
* Job：只是为了完成某项任务，完成之后pod推出
* CronJob:周期性的Job
* StatefulSet：有状态controller



## RelicaSet

简称rs

```shell
#查看文档
kubectl explain rs
```

![image-20190721210233497](/Users/chenyansong/Documents/note/images/docker/image-20190721210233497.png)

我们来定义一个rs,在上面我们使用yaml文件去定义了一个Pod，但是这种Pod是不受controller管理的，所以如果我们要用controller去管理Pod，可以在controller中来定义Pod，定义的格式和Pod定义很相似

![image-20190721211821480](/Users/chenyansong/Documents/note/images/docker/image-20190721211821480.png)

然后我们根据定义去创建rs

```shell
kubectl create -f rs-demo.yaml

#查看控制器创建的Pod
#在pod中定义的名称是没有用的，他是以控制器的名称加一个字符串组成
```

![image-20190721212049630](/Users/chenyansong/Documents/note/images/docker/image-20190721212049630.png)

我们删除一个Pod副本，controller会自动重建

我们的rs控制器定义的副本数量为2，但是如果我们一个不相关的pod的标签，将rs中通过标签选择器选中的的Pod变为3，我们看看结果会如何



![image-20190721212552053](/Users/chenyansong/Documents/note/images/docker/image-20190721212552053.png)

![image-20190721212745013](/Users/chenyansong/Documents/note/images/docker/image-20190721212745013.png)

我们发现rs会删除一个Pod，所以我们在定义标签的时候，需要复杂定义，避免冲突



假如我们想要修改Pod的副本

```shell
kubectl edit rs myapp
#我们将副本改成5
```

![image-20190721213818894](/Users/chenyansong/Documents/note/images/docker/image-20190721213818894.png)

我们再查看副本数量

![image-20190721213859278](/Users/chenyansong/Documents/note/images/docker/image-20190721213859278.png)

然后我们修改Pod的image的版本，从v1改为v2

![image-20190721214231139](/Users/chenyansong/Documents/note/images/docker/image-20190721214231139.png)

我们看到现有的Pod的版本还是v1，因为只有重建的Pod，才会是v2版本的

![image-20190721214408855](/Users/chenyansong/Documents/note/images/docker/image-20190721214408855.png)

08 剩余12：