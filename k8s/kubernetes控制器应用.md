[TOC]



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

2：00