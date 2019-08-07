[TOC]



helm之于k8s，就像yum之于linux，helm用于**The package manager for Kubernetes**

# helm核心术语

chart：支持模板机制，值文件，一个helm程序包

chart仓库：保存多个chart，Charts仓库，https/http服务器

Release：chart实例化之后，部署在k8s上的一个实例

# 程序架构

helm：客户端，管理本地的chart仓库，管理chart，与Tiller服务器进行交互，发送Chart，实现安装，查询，卸载等操作

Tiller：服务端（一般运行在目标集群之上），接收heml发来的Charts与Config，合并生成release

![1565082751828](E:\git-workspace\note\images\docker\1565082751828.png)



# 安装helm，tiller

[helm的github](https://github.com/helm/helm/releases)，去下载对应版本的helm

绑定rbac与tiller

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: tiller
  namespace: kube-system
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: tiller
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cluster-admin
subjects:
  - kind: ServiceAccount
    name: tiller
    namespace: kube-system
```

```shell
$ kubectl create -f rbac-config.yaml
serviceaccount "tiller" created
clusterrolebinding "tiller" created
```

查看是否创建了tiller的账户

```shell
kubectl get sa -n kube-system
```

初始化tiller

```shell
$ helm init --service-account tiller --history-max 200
```

![1565085461704](E:\git-workspace\note\images\docker\1565085461704.png)

可能会报如下的错误，需要如下的设置

![1565085549010](E:\git-workspace\note\images\docker\1565085549010.png)

```shell
export HTTPS_PROXY='http://www.ik8s.io:10080'
export NO_PROXY='172.20.0.0/16,127.0.0.0/8'
#再次初始化
helm init --service-account tiller --history-max 200
```

![1565085646026](E:\git-workspace\note\images\docker\1565085646026.png)

初始化之后，查看对应创建的Pod

```shell
kubectl get pods -n kube-system
```

![1565085798791](E:\git-workspace\note\images\docker\1565085798791.png)

查看helm版本

```shell
helm version
```

更新本地仓库

```shell
helm repo update

#本地仓库的路径
$user_home/.helm/repository/cache/archive/
```

解压本地仓库下的tgz文件

![1565093237199](E:\git-workspace\note\images\docker\1565093237199.png)

![1565093248407](E:\git-workspace\note\images\docker\1565093248407.png)

上面的templates是一个模板，其中的变量需要我们去通过**值文件**去填充，我们看一下一个具体的模板文件的样子

![1565093550101](E:\git-workspace\note\images\docker\1565093550101.png)

# 查看，创建chart，release

chart仓库：这里可以搜索到我们需要的package
https://hub.helm.sh/
https://hub.kubeapps.com/



命令行中搜索package

```shell
#列出所有的可以搜索的仓库
helm repo list
```

![1565091575990](E:\git-workspace\note\images\docker\1565091575990.png)

```shell
#列出所有的chart
helm search 

#列出指定chart
helm search jenkins

#查看chart的详细信息
helm inspect stable/jenkins
```

安装

```shell
#install memcached
helm install --name mem1 stable/memcached

#如果不想使用默认的值文件，可以如下
helm install --name mem1 statble/memcached -f xxx.yaml
```

我们可以看到创建memcached的时候，同时创建了Pod，service，statefulSet

![1565092166040](E:\git-workspace\note\images\docker\1565092166040.png)

验证是否正常工作

![1565092305340](E:\git-workspace\note\images\docker\1565092305340.png)

查看已经安装的release

````shell
helm list
````

删除release

```shell
helm delete --help
helm delete mem1
```

helm常用命令

```shell
release管理
	install #增
	delete #删
	upgrade/rollback #升级or回滚
	list #查
	history #查看release部署历史
	status #获取release状态信息
	
chart管理
	create
	delete
	fetch #下载，解压
	get #仅仅下载
	history #查看历史
	inspect #查看一个chart的详细信息
	package #打包
```



# 修改默认的值文件





# 创建自定义chart



