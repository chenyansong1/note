[TOC]

# api的访问方式

客户端->API server

​	用户：username, uid

​	group

​	extra:

​	API:

​	Request path:

​		/apis/apps/v1/namespace/default/deployments/myapp-deploy

HTTP request verb:

​	get, post, put, delete

API requests verb:

​	get, list, create, update, patch, watch, proxy, redirect, delete, deletecollection

Resource:

Subresource:

Namespace

API group:



k8s有两类账号，一类是用户访问的账号，另外一类是Pod访问service需要的账号

# Pod的认证方式

在Pod访问service的时候，也是需要认证的，那么认证的信息放在哪里了，我们看到每个名称空间下，都有一个默认的secret，默认Pod就是使用挂载这个secret进行认证的

![image-20190728165545423](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190728165545423.png?raw=true)

![image-20190728165631536](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190728165631536.png?raw=true)



# serviceaccount资源

serviceaccount只是用来创建账号的，但是授权需要另外做

![image-20190728165949741](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190728165949741.png?raw=true)

```shell
#生成一个框架，然后通过文件创建
kubectl create serviceaccount mysa -o yaml --dry-run
```

![image-20190728171732885](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190728171732885.png?raw=true)

系统会自动为这个账户生成secret信息

![image-20190728171842035](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190728171842035.png?raw=true)

在创建Pod时指定使用的serviceaccount



![image-20190728172125840](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190728172125840.png?raw=true)



# 获取私有镜像认证的方式

1. 将创建的secret定义在serviceaccount上，然后将sa定义在Pod上（像上面的那种方式）
2. 通过Pod中的一个imagePullSeccret中配置一个secret进行认证，但是这样用户可以通过Pod中的定义的secret得到这个secret，但是如果通过将secret定义在sa中，然后在Pod中定义sa，这样只能通过Pod拿到的是sa的信息，而不能直接拿到secret

![image-20190728173048338](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190728173048338.png?raw=true)





# kubeconfig

view

![image-20190728174656832](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190728174656832.png?raw=true)

* clusters：可以配置多个集群信息
* users：可以配置多个账户信息

* context：用来说明，使用哪个账号访问哪一个集群



# 自定义证书

```shell
#创建私钥
(umask 077; openssl genresa -out mageud.key 2048)

#生成签署请求
openssl req -new -key magedu.key -out magedu.csr -subj "/CN=magedu"

#使用CA去签署
#这个目录下有ca.crt,ca.key
cd /etc/kubernet/pki/
#签署
openssl x509 -req -in magedu.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out magedu.crt -days 365

```

![image-20190728180739964](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190728180739964.png?raw=true)

创建认证用户

![image-20190728181231959](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190728181231959.png?raw=true)

查看创建的认证用户

![image-20190728181422656](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190728181422656.png?raw=true)

设置上下文，让创建的用户可以访问集群

![image-20190728181554193](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190728181554193.png?raw=true)

![image-20190728181612002](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190728181612002.png?raw=true)

切换当前账号

![image-20190728181700676](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190728181700676.png?raw=true)

但是这个用户账号是没有管理员的权限的，所以执行管理命令，会有下面的权限问题

![image-20190728181753596](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190728181753596.png?raw=true)



配置集群

![image-20190728182307378](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190728182307378.png?raw=true)

![image-20190728182335375](https://github.com/chenyansong1/note/blob/master/images/docker/image-20190728182335375.png?raw=true)