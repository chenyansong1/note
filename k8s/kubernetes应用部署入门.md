[TOC]

# 创建pod

```shell
Create and run a particular image, possibly replicated.

#这里可以创建两种类型的controller(deployment， job)
 Creates a deployment or job to manage the created container(s).

#基于上面的两种控制去创建pod
Usage:
  kubectl run NAME --image=image [--env="key=value"] [--port=port] [--replicas=replicas] [--dry-run=bool]
[--overrides=inline-json] [--command] -- [COMMAND] [args...] [options]

# NAME 控制器的名称


Use "kubectl options" for a list of global command-line options (applies to all commands).


Examples:
  # Start a single instance of nginx.
  kubectl run nginx --image=nginx
  
  # Start a single instance of hazelcast and let the container expose port 5701 .
  kubectl run hazelcast --image=hazelcast --port=5701
  
  # Start a single instance of hazelcast and set environment variables "DNS_DOMAIN=cluster" and "POD_NAMESPACE=default" in the container.
  kubectl run hazelcast --image=hazelcast --env="DNS_DOMAIN=cluster" --env="POD_NAMESPACE=default"
  
  # Start a single instance of hazelcast and set labels "app=hazelcast" and "env=prod" in the container.
  kubectl run hazelcast --image=hazelcast --labels="app=hazelcast,env=prod"
  
  # Start a replicated instance of nginx.
  kubectl run nginx --image=nginx --replicas=5
  
  # Dry run（干跑模式，并没有真实启动他们）. Print the corresponding API objects without creating them.
  kubectl run nginx --image=nginx --dry-run
  
  # Start a single instance of nginx, but overload the spec of the deployment with a partial set of values parsed from JSON.
  kubectl run nginx --image=nginx --overrides='{ "apiVersion": "v1", "spec": { ... } }'
  
  # Start a pod of busybox and keep it in the foreground, don't restart it if it exits.(不会重启)
  kubectl run -i -t busybox --image=busybox --restart=Never
  
  # Start the nginx container using the default command, but use custom arguments (arg1 .. argN) for that command.
  kubectl run nginx --image=nginx -- <arg1> <arg2> ... <argN>
  
  # Start the nginx container using a different command and custom arguments.
  kubectl run nginx --image=nginx --command -- <cmd> <arg1> ... <argN>
  
  # Start the perl container to compute π to 2000 places and print it out.
  kubectl run pi --image=perl --restart=OnFailure -- perl -Mbignum=bpi -wle 'print bpi(2000)'
  
  # Start the cron job to compute π to 2000 places and print it out every 5 minutes.
  kubectl run pi --schedule="0/5 * * * ?" --image=perl --restart=OnFailure -- perl -Mbignum=bpi -wle 'print bpi(2000)'


[root@es2 ~]# kubectl run nginx --image=nginx --port 80 --replicas=1 --dry-run=true
kubectl run --generator=deployment/apps.v1 is DEPRECATED and will be removed in a future version. Use kubectl run --generator=run-pod/v1 or kubectl create instead.
deployment.apps/nginx created (dry run)
[root@es2 ~]# 

#去掉干跑模式，再次创建
[root@es2 ~]# kubectl run nginx --image=nginx --port 80 --replicas=1 
kubectl run --generator=deployment/apps.v1 is DEPRECATED and will be removed in a future version. Use kubectl run --generator=run-pod/v1 or kubectl create instead.
deployment.apps/nginx created

#查看deployment
[root@es2 ~]# kubectl get deployment
NAME    READY   UP-TO-DATE   AVAILABLE   AGE
nginx   0/1     1            1           17s
[root@es2 ~]# 

#查看pod
[root@es2 ~]# kubectl get pods
NAME                     READY   STATUS    RESTARTS   AGE
nginx-7c45b84548-pvm4q   1/1     Running   0          2m35s
#查看pod所属的node
[root@es2 ~]# kubectl get pods -o wide
NAME                     READY   STATUS    RESTARTS   AGE     IP           NODE      NOMINATED NODE   READINESS GATES
nginx-7c45b84548-pvm4q   1/1     Running   0          3m24s   10.244.1.2   spark02   <none>           <none>
[root@es2 ~]# 

```

我们创建的pod的IP地址属于cni0桥，cni0属于10.244.0.0/16下的一个地址，每个node中会有一个cni0地址（比如在另外一个node上，可能的cni0桥的地址为10.244.0.1/24）

![image-20190720083035915](/Users/chenyansong/Documents/note/images/docker/image-20190720083035915.png)



# 通过service访问pod服务

由上面我们知道启动的pod地址为10.244.1.2  ，但是当我们delete掉该pod的时候，controller会重新启动一个pod，但是此时的pod的地址已经变了，所以我们不能使用IP地址去访问pod中的服务，此时就用到了`kubectl expose`

```shell
kubectl expose --help

Usage:
  kubectl expose (-f FILENAME | TYPE NAME) [--port=port] [--protocol=TCP|UDP|SCTP] [--target-port=number-or-name]
[--name=name] [--external-ip=external-ip-of-service] [--type=type] [options]

#可以暴露下面的对象
 pod (po), service (svc), replicationcontroller (rc), deployment (deploy), replicaset (rs)

#service的类型有下面的几种
--type='': Type for this service: ClusterIP, NodePort, LoadBalancer, or ExternalName. Default is 'ClusterIP'.

Examples:
  # Create a service for a replicated nginx, which serves on port 80 and connects to the containers on port 8000.
  kubectl expose rc nginx --port=80 --target-port=8000
  
  # Create a service for a replication controller identified by type and name specified in "nginx-controller.yaml",
which serves on port 80 and connects to the containers on port 8000.
  kubectl expose -f nginx-controller.yaml --port=80 --target-port=8000
  
  # Create a service for a pod valid-pod, which serves on port 444 with the name "frontend"
  kubectl expose pod valid-pod --port=444 --name=frontend
  
  # Create a second service based on the above service, exposing the container port 8443 as port 443 with the name
"nginx-https"
  kubectl expose service nginx --port=443 --target-port=8443 --name=nginx-https
  
  # Create a service for a replicated streaming application on port 4100 balancing UDP traffic and named 'video-stream'.
  kubectl expose rc streamer --port=4100 --protocol=UDP --name=video-stream
  
  # Create a service for a replicated nginx using replica set, which serves on port 80 and connects to the containers on
port 8000.
  kubectl expose rs nginx --port=80 --target-port=8000
  
  # Create a service for an nginx deployment, which serves on port 80 and connects to the containers on port 8000.
  kubectl expose deployment nginx --port=80 --target-port=8000

```



```shell
[root@es2 ~]# kubectl expose deployment nginx --name=nginx-service --port=80 --target-port=80
service/nginx-service exposed
[root@es2 ~]# 

#控制器类型：deployment
#该类型下的具体的控制器：nginx
#service/nginx-service exposed  表示service下的具体的服务名称为：nginx-service

#查看创建的服务
[root@es2 ~]# kubectl  get services
NAME            TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)   AGE
kubernetes      ClusterIP   10.96.0.1      <none>        443/TCP   17h
nginx-service   ClusterIP   10.96.253.37   <none>        80/TCP    2m32s
[root@es2 ~]# kubectl  get svc
NAME            TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)   AGE
kubernetes      ClusterIP   10.96.0.1      <none>        443/TCP   17h
nginx-service   ClusterIP   10.96.253.37   <none>        80/TCP    2m46s
[root@es2 ~]# 
#ClusterIP 表示service的类型
#10.96.253.37是10.96.0.0/12中的一个地址，是一个动态生成的地址，这种地址是更多的被pod客户端来访问的

#以后我们访问service所代理的pod服务的时候，只需要指定service的名称，如：
curl nginx-service   
#这里会有一个域名解析，此时就要需要用到coredns
```

![image-20190720091358255](/Users/chenyansong/Documents/note/images/docker/image-20190720091358255.png)

从上面我们可以知道coredns的地址为：10.244.0.2

但是我们也不会使用coredns的IP地址，直接作为dns的地址，coredns也是有服务名称的，我们一般使用coredns的服务名

```shell
#查看服务名
kubectl get svc -n kube-system
```

![image-20190720141521678](/Users/chenyansong/Documents/note/images/docker/image-20190720141521678.png)

```shell
dig -t A nginx-service @10.96.0.10
#安装
yum install -y bind-utils

```

![image-20190720143718569](/Users/chenyansong/Documents/note/images/docker/image-20190720143718569.png)