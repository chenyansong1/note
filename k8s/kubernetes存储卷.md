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



emptyDir

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

