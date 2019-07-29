[TOC]

# user(group,serviceaccount), role, rolebinding的关系

RBAC：Role based access controller 基于角色的访问控制

许可：permission

角色：operations objects (对哪些对象，执行什么操作)

url：

```
/apis/<group>/<version>/namespaces/<NAME_SPACE>/<KIND>/[/OBJECT_ID]/
```

rolebinding：角色绑定

```shell
role：
	operations
	objects
rolebinding
	user account OR service account
	role
clusterRole
clusterRoleBinding
##################################
subject:
	user
	group
	servcieaccount
operator:
	list,get,watch...
object:
	resouce group
	resource
	non-resource url
```

![1564385873059](E:\git-workspace\note\images\docker\1564385873059.png)

![1564380652162](E:\git-workspace\note\images\docker\1564380652162.png)

# 创建role

```shell
kubectl create role --help

#kubectl create role Name --verb=verb --resource=resource.group/subresource

#kubectl create role pods-reader --verb=get,list --resource=pods -o yaml --dry-run >./role.demo.yaml
vim role.demo.yaml
```

![1564381530650](E:\git-workspace\note\images\docker\1564381530650.png)

```shell
kubectl get role
```

![1564381730436](E:\git-workspace\note\images\docker\1564381730436.png)

# rolebinding

```shell
#kubectl create rolebinding --help

Usage:
  kubectl create rolebinding NAME --clusterrole=NAME|--role=NAME [--user=username] [--group=groupname] [--serviceaccount=namespace:serviceaccountname] [--dry-run] [options]
  
#kubectl create rolebinding maedu-read-pods --role=pods-reader --user=magedu  -o yaml --dry-run >./rolebinding-demo.yaml
```

![1564382136168](E:\git-workspace\note\images\docker\1564382136168.png)

切换用户，进行测试

![1564382459649](E:\git-workspace\note\images\docker\1564382459649.png)

# clusterRole

```shell
#kubectl create clusterrole --help
Usage:
  kubectl create clusterrole NAME --verb=verb --resource=resource.group [--resource-name=resourcename] [--dry-run]
[options]

#kubectl create clusterrole cluster-reader --verb=get,list,watch --resource=pods -o yaml --dry-run >./cluster-demo.yaml
```

# clusterrolebinding

```shell
#kubectl create clusterrolebinding --help
Usage:
  kubectl create clusterrolebinding NAME --clusterrole=NAME [--user=username] [--group=groupname]
[--serviceaccount=namespace:serviceaccountname] [--dry-run] [options]

kubectl create clusterrolebinding magedu-read-all-pods --clusterrole=cluster-reader --user=magedu --dry-run -o yaml
```

![1564383256468](E:\git-workspace\note\images\docker\1564383256468.png)

# 使用rolebinding绑定clusterrole

只针对rolebinding所在的名称空间下生效，这样做的好处是：clusterrole只用创建一次，clusterrole可给所有的名称空间使用（不用在每个名称空间下都创建role）

```shell
kubectl create rolebinding magedu-read-pods --clusterrole=cluster-reader --user=magedu --dry-run -o yaml
```

![1564383870475](E:\git-workspace\note\images\docker\1564383870475.png)

不能访问其他名称空间下的资源

![](E:\git-workspace\note\images\docker\1564383937617.png)

