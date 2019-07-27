[TOC]

基础架构容器：pause

emptyDir：pod删除，他也会被删除，他可以关联宿主机的目录或者内存

hostPath：关联在宿主机上的目录

网络存储：

​	SAN：iSCSI

​	NAS:nfs,cifs

​	分布式存储：glusterfs, rbd, cephfs

​	云存储：EBS， Azure Disk， 阿里云等（k8s托管在云上）

pvc: persistentVolumeClaim：存储卷创建申请

pv,pv的动态供给



# 常见的存储类型

- 三种存储特点

| 存储类型    | 特点                                                         | 使用场景                                                     |
| :---------- | :----------------------------------------------------------- | :----------------------------------------------------------- |
| emptyDir    | emptyDir类型的volume在Pod分配到Node上时被创建，kubernetes会在Node上自动分配一个目录，因此无需指定宿主机Node上对应的目录文件。这个目录的初始内容为空，当pod从Node上移除时，emptyDir中的数据会被永久删除。 | 临时空间，当pod从Node上移除时，emptyDir的数据h会被永久删除。 |
| hostPath    | hostPath类型是映射node文件系统中的文件或者目录到pod里。 配置相同的pod，可能在不同的node上表现不同，因为不同节点上映射的文件内容不同。 | pv/pvc/StorageClass的方法实现。 这种使用的场景是： hostpath是单节点的本地存储方案，不提供任何基于node节点亲和性的pod调度管理支持。 |
| localVolume | local volume 允许用户通过标准pvc接口以简单且可移植的方式访问node节点的本地存储。 PV的定义中需要包含描述节点亲和性的信息，k8s系统则使用该信息将容器调度到正确的pod节点。 | pv/pvc/StorageClass的方法实现 适用于小规模、多节点的k8s开发或测试环境，尤其是在不具备一套安全、可靠且性能有保障的存储集群时。 |



## emptyDir

```yaml
#创建Pod时指定存储卷
apiVersion: v1
kind: Pod
metadata:
	name: pode_name
	namespace: default
	labels:
		app: myapp
spec:
	containers:
	-	name: myapp
		image: ikubernetes/myapp:v1
		volumeMounts:  #在容器中挂载
		-	name: html #卷名
			mountPath: /data/web/html/ #挂载到node的path
	volumes:
	-	name: html #定义卷名
		emptyDir: {} #生命周期同Pod一致
```



gitRepo是基于emptyDir进行创建的，将git中的代码pull回来到指定的目录下

## hostPath

hostPath:宿主机路径存储卷，将宿主机的文件系统的某一个目录建立关系，Pod删除时，宿主机的目录是不会被删除的，只要同一个Pod还是调度到该节点，对应的数据依然是存在的

```shell
kubectl explain pods.spec.volumes.hostPath.type
```

The supported values for field `type` are:

| Value               | Behavior                                                     |
| :------------------ | :----------------------------------------------------------- |
|                     | Empty string (default) is for backward compatibility, which means that no checks will be performed before mounting the hostPath volume. |
| `DirectoryOrCreate` | If nothing exists at the given path, an empty directory will be created there as needed with permission set to 0755, having the same group and ownership with Kubelet. |
| `Directory`         | A directory must exist at the given path                     |
| `FileOrCreate`      | If nothing exists at the given path, an empty file will be created there as needed with permission set to 0644, having the same group and ownership with Kubelet. |
| `File`              | A file must exist at the given path                          |
| `Socket`            | A UNIX socket must exist at the given path                   |
| `CharDevice`        | A character device must exist at the given path              |
| `BlockDevice`       | A block device must exist at the given path                  |

```yaml
appVersion: v1
kind: Pod
metadata:
	name: pod-vol-hostpath
	namespace: default
spec:
	containers:
	-	name: myapp
		image: ikubernetes/myapp:v1
		volumeMounts:
         -	name: html
         	mountPaht: /usr/share/nginx/html/
	volumes:
	-	name: html
		hostPath:
			path: /data/pod/volume1
			type: DirectoryOrCreate
```



## NFS存储类型

挂载到nfs文件系统，需要安装NFS文件系统，但是性能太差，奈何

```yaml
appVersion: v1
kind: Pod
metadata:
	name: pod-vol-nfs
	namespace: default
spec:
	containers:
	-	name: myapp
		image: ikubernetes/myapp:v1
		volumeMounts:
         -	name: html
         	mountPaht: /usr/share/nginx/html/
	volumes:
	-	name: html
		nfs:
			path: /data/volumes
			server: stor01.server.com
```



## localVolume

![1564195924708](E:\git-workspace\note\images\docker\1564195924708.png)

```shell
kubectl explain pods.spec.volumes.persistentVolumeClaim
```

pvc也是一种标准的k8s资源

```shell
#kubectl explain pvc
#kubectl explain pvc.spec
accessModes: #支不支持多人同时访问
dataSource：
resources #最少存储空间
selector #使用标签选择器
storageClassName #存储类名称	    
volumeMode   #后端存储卷的模式
volumeName  #后端卷名称
```



pv 和pvc是一一对应的关系，如果一个pv是占用，表示已经被binding了，在node内部pvc相当于一个存储卷，这个存储卷是可以被多个Pod所访问的（这个可以通过定义pvc的accessModes来指定Pod的访问模式），处理的流程是这样的：

1. 首先是将store存储做成pv
2. 然后用户通过pvc去申请得到某一个pv
3. 最后就可以使用这个pv作为存储卷

```yaml
apiVersion: 
```

# Kubernetes 原生和存储

Kubernetes 原生如何处理存储？Kubernetes 自身提供了一些管理存储的解决方案：临时选项、持久化存储卷、持久化存储卷声明、存储类和有状态副本集。这可能很混乱。

- 持久化存储卷（PV）是由管理员配置的存储单元。它们独立于任何一个容器组，使它们摆脱容器组的短暂生命周期。
- 持久化存储卷声明（PVC）是对存储的请求，即 PVs。使用 PVC，可以将存储绑定到特定节点，使其可供该节点使用。

有两种处理存储的方法：静态和动态。

- 采用静态配置，管理员在实际请求之前，为他们认为可能需要的容器组提供 PVs，并且通过明确指定的 PVCs，将这些 PV 手动绑定到指定的容器组。

  实际上，静态定义的 PV 与 Kubernetes 的可移植结构不兼容，因为正在使用的存储可能依赖于环境，例如 AWS EBS 或 GCE Persistent Disk。手动绑定需要更改 YAML 文件以指向特定供应商的存储解决方案。

- 动态配置

  在开发人员如何考虑资源方面，静态配置也违背了 Kubernetes 的思维方式：CPU 和内存未事先分配并绑定到容器组或容器。它们是动态授予的。

  动态配置使用存储类完成。集群管理员无需事先手动创建 PV。他们改为创建多个存储配置文件，就像模板一样。当开发人员创建 PVC 时，根据请求的要求，在请求时创建其中一个模板，并将其附加到容器组。

![图片](E:\git-workspace\note\images\docker\5c47ff8a4359b.png)



# 定义pv，pvc

pv也是标准的k8s资源

## pv的访问模式


```shell
# kubectl explain pv.spec.accessModes
```

![1564199415714](E:\git-workspace\note\images\docker\1564199415714.png)

但是不同的存储卷支持不同的类型，如下：

![1564199526683](E:\git-workspace\note\images\docker\1564199526683.png)



## pv的存储大小

```shell
#kubectl explain pv.spec.capacity
#以1000为单位
| k |M | G | T | P | E 
#以1024为单位
Ki  | Mi | Gi | Ti | Pi | Ei 

```

## 回收策略

```shell
Retain 保持不动，由管理员手动处理
recycle： 空间回收，及删除存储卷目录下的所有文件，目前仅NFS 和 hostPath 支持此操作
delete： 删除存储卷，仅部分云端存储系统支持，如AWS  GCE  Azure disk 和 cinder 
```



## 定义pv的yaml文件

```yaml
#kubectl explain pv
#kubectl explain pv.spec.hostPath

---
apiVersion: v1
kind: PersistentVolume
metadata:
	name: pv001 #pv是集群级别的资源，他不应该属于某一个namespace
	labels:
		name: pv001
spec:
	nfs:
		path: /data/volums/v1
		server: stor01.server.com
	accessModes: ["ReadWriteMany", "ReadWriteOnce"]
	capacity: 
		storage: 2G
---
#可以创建多个pv
apiVersion: v1
kind: PersistentVolume
metadata:
	name: pv002 #pv是集群级别的资源，他不应该属于某一个namespace
	labels:
		name: pv002
spec:
	nfs:
		path: /data/volums/v2
		server: stor01.server.com
	accessModes: ["ReadWriteMany"]
	capacity: 
		storage: 5G
---


#kubectl apply -f xxx.yaml
```

![1564206784540](E:\git-workspace\note\images\docker\1564206784540.png)

## 定义pvc

```yaml
#pvc
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
	name: mypvc
	namespace: default
spec:
	accessModes: ["ReadWriteMany"]
	resources:
		requests:
			storage: 3G
---
#pod
apiVersion: v1
kind: Pod
metadata:
	name: pod-vol-pvc
	namespace: default
spec:
	containers:
	-	name: myapp
		image: ikubernetes/myapp:v1
		volumeMount:
		-	name: html
			mountPath: /usr/share/nginx/html/
	volumes:
	-	name: html
		persistentVolumeClaim:
			claimName: mypvc
			
#kubectl apply -f xx.yaml
```

我们再次查看pv的状态，发现有pv已经被pvc绑定了

![1564207174162](E:\git-workspace\note\images\docker\1564207174162.png)

![1564207213602](E:\git-workspace\note\images\docker\1564207213602.png)



## 存储类

pvc申请的时候，并不一定有剩余的pv可以使用，这就需要动态绑定



# 存储卷配置

## configMap

configmap也是一种资源，为了将配置文件从镜像中解耦，配置信息的注入，扮演了配置中心，不能保存敏感信息，比如数据库的账户

### 命令行创建

```shell
[root@spark03 ~]# kubectl explain cm
KIND:     ConfigMap
VERSION:  v1
//...

#直接命令行创建
kubectl create confmap --help
Examples:
  # Create a new configmap named my-config based on folder bar
  kubectl create configmap my-config --from-file=path/to/bar
  
  # Create a new configmap named my-config with specified keys instead of file basenames on disk
  kubectl create configmap my-config --from-file=key1=/path/to/bar/file1.txt --from-file=key2=/path/to/bar/file2.txt
  
  # Create a new configmap named my-config with key1=config1 and key2=config2
  kubectl create configmap my-config --from-literal=key1=config1 --from-literal=key2=config2
  
  # Create a new configmap named my-config from the key=value pairs in the file
  kubectl create configmap my-config --from-file=path/to/bar
  
  # Create a new configmap named my-config from an env file
  kubectl create configmap my-config --from-env-file=path/to/bar.env

```

![1564210678009](E:\git-workspace\note\images\docker\1564210678009.png)

```shell
#通过文件创建
#### vim www.conf ######
server{
	server_name myapp.test.com;
	listen 80;
	root /data/web/html;
}
##### ####### ##########

kubectl create configmap nginx-www --from-file=www./www.conf
kubectl create configmap nginx-www --from-file=./www.conf #如果不指定key，将以文件名作为key
```

![1564211089121](E:\git-workspace\note\images\docker\1564211089121.png)

![1564211132813](E:\git-workspace\note\images\docker\1564211132813.png)



### pod通过环境变量env引用

创建Pod引用上面的配置

```yaml
apiVersion: v1
kind: Pod
metadata:
	name: pod-cm-1
	namespace: default
	labels:
		app: myapp
spec:
	containers:
	-	name: myapp
		image: ikubernetes/myapp:v1
		ports:
		-	name: http
			containerPort: 80
		env:
		-	name: NGINX_SERVER_PORT  #这里定义了一个变量
			valueFrom: 
				configMapKeyRef:
					name: nginx-config #使用哪一个configmap
					key: nginx_port #以这个key的值传递给上面定义的变量
			name: NGINX_SERVER_NAME
			valueFrom:
				configMapKeyRef:
					name: nginx-config
					key: server_name
					
#创建pod，kubectl apply -f xx.yaml
#查看创建的容器中是否存在对应的环境变量
kubectl exec -it container_name -- /bin/sh printenv
```

当我们使用环境变量时只是在Pod启动时有效，比如，我们更改configmap中的端口，但是我们进入容器内部查看，其环境变量的端口并没有改变

### pod通过挂载的方式改变配置

```yaml
apiVersion: v1
kind: Pod
metadata:
	name: pod-cm-2
	namespace: default
	labels:
		app: myapp
spec:
	containers:
	-	name: myapp
		image: ikubernetes/myapp:v1
		ports:
		-	name: http
			containerPort: 80
		volumeMounts:
		-	name: nginxconf
			mountPaht: /etc/nginx/config.d/
			readOnly: true #容器不能修改这些挂载的配置信息
	volumes:
	-	name: nginxconf
		configMap: 
			name: nginx-config #指定上面定义的configmap
			
			
#kubect apply -f xxx.yaml
```

我们进入容器中查看

![1564213579059](E:\git-workspace\note\images\docker\1564213579059.png)

会生成以：key作为文件名，以value作为文件的内容

我们修改一下configmap中的端口，看下Pod中挂载的文件是否修改了，我们发现Pod中的对应文件是修改了

### 例：通过挂载的方式修改nginx配置文件

```yaml
apiVersion: v1
kind: Pod
metadata:
	name: pod-cm-3
	namespace: default
	labels:
		app: myapp
spec:
	containers:
	-	name: myapp
		image: ikubernetes/myapp:v1
		ports:
		-	name: http
			containerPort: 80
		volumeMounts:
		-	name: nginxconf
			mountPaht: /etc/nginx/conf.d/
			readOnly: true #容器不能修改这些挂载的配置信息
	volumes:
	-	name: nginxconf
		configMap: 
			name: nginx-www #指定上面定义的configmap
			#items: 可以选择性的挂载key,同事可以指定生成文件的权限 
			
#kubect apply -f xxx.yaml
```

![](E:\git-workspace\note\images\docker\1564214032441.png)

我们再次修改configmap的端口，改成8080，我们可以进入容器内部看到配置文件中的端口确实是改变了，然后看下nginx是否会自动重新加载，答案是不会（我们netstat -lnt发现进程监听的端口仍然是80，所以我们需要手动重载配置）



## secret

功能和configMap一样，secret也是k8s的一种资源，其中的数据是Base64编码的方式存储数据

```shell
[root@spark03 ~]# kubectl explain secret
KIND:     Secret
VERSION:  v1
//...

#创建secret
[root@spark03 ~]# kubectl create secret --help
Create a secret using specified subcommand.

Available Commands:
  docker-registry Create a secret for use with a Docker registry（#registry的认证信息）
  generic         Create a secret from a local file, directory or literal value （#一般信息，包括如MySQL的用户名密码）
  tls             Create a TLS secret（#证书信息）

Usage:
  kubectl create secret [flags] [options]
  
```

创建

```shell
kubectl create secret generic mysql-root-password --from-literal=password=MyP@123
```

![1564215344849](E:\git-workspace\note\images\docker\1564215344849.png)

我们可以拿到其中的内容进行解码

![1564215452039](E:\git-workspace\note\images\docker\1564215452039.png)

```yaml
apiVersion: v1
kind: Pod
metadata:
	name: pod-secret-1
	namespace: default
	labels:
		app: myapp
spec:
	containers:
	-	name: myapp
		image: ikubernetes/myapp:v1
		ports:
		-	name: http
			containerPort: 80
		env: #通过env注入环境变量
		-	name: MYSQL_ROOT_PASSWORD
			valueFrom:
				secretKeyRef:
					name: mysql-root-password
					kye: password
```

