[TOC]

# 部署

[github官网](https://github.com/kubernetes/dashboard)

```shell
$ kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v1.10.1/src/deploy/recommended/kubernetes-dashboard.yaml
```

将service改成NodePort类型，便于集群外面浏览器能够访问到

```shell
kubectl -patch svc kubernetes-dashboard -p '{"spec":{"type":"NodePort"}}' -n kube-system
```



认证时的账号必须为ServiceAccount：被dashboard Pod拿来由kubernetes进行认证

# 使用token认证

1. 创建ServiceAccount，根据其管理目标，使用rolebinding或clusterrolebinding绑定合理role或clusterrole
2. 获取此ServiceAccount的secret(自动创建的)，查看secret的详细信息，其中就有token

![image-20190729203844517](/Users/chenyansong/Documents/note/images/docker/image-20190729203844517.png)

![image-20190729203954304](/Users/chenyansong/Documents/note/images/docker/image-20190729203954304.png)

```shell
#创建serviceaccount
kubectl create serviceaccount dashboard-admin -n kube-system 


#clusterrolebinding
#将serviceaccount和dashboard-admin角色进行绑定
kubectl create clusterrolebinding dashboard-cluster-admin --cluserrole=cluster-admin --serviceaccount=kube-system:dashboard-admin

#会自动生成一个secret，如下图
kubectl get secret -n kube-system
```

![image-20190729210249154](/Users/chenyansong/Documents/note/images/docker/image-20190729210249154.png)

这个secret中会有一个token信息

![image-20190729210440174](/Users/chenyansong/Documents/note/images/docker/image-20190729210440174.png)



部分权限

```shell
#创建serviceaccount
kubectl create serviceaccount def-ns-admin -n default 

#rolebinding
#将serviceaccount和dashboard-admin角色进行绑定
kubectl create rolebinding def-ns-admin --cluserrole=cluster-admin --serviceaccount=default:def-ns-admin

#会自动生成一个secret，如下图
kubectl get secret -n kube-system
```

![image-20190729212214606](/Users/chenyansong/Documents/note/images/docker/image-20190729212214606.png)

这个token只能管理default的名称空间



# 使用配置文件进行认证

把ServiceAccount的token封装为kubeconfig文件


```shell
cd /etc/kubernetes/pki/
#为dashboard创建一个专用的证书
(umask 077;openssl genrsa -out dashboard.key 2048)

#证书签署请求
openssl req -new -key dashboard.key -out dashboard.csr -sub "/O=magedu/CN=dashboard"

#使用ca签署
openssl -req -in dashboard.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out dashboard.crt -days 365

#secret
kubectl create secret generic dashboard-cert -n kube-system --from-file=dashboard.crt=./dashboard.crt --from-file=dashboard.key=./dashboard.key

cd /etc/kubernetes/pki/
#创建集群：--kubeconfig表示保存在哪里
kubectl config set-cluster kubernetes --certificate-authority=./ca.crt --server="https://172.20.0.70:6443" -embed-certs=true --kubeconfig=/root/def-ns-admin.conf
```

![image-20190729212906882](/Users/chenyansong/Documents/note/images/docker/image-20190729212906882.png)



```shell
kubectl config set-credentials df-ns-admin --token="粘贴上面生成的token的base64解码" --kubeconfig=/root/def-ns-admin.conf

#base64界面
echo "xxxxx"|base64 -d
```

![image-20190729215458942](/Users/chenyansong/Documents/note/images/docker/image-20190729215458942.png)

![image-20190729213831638](/Users/chenyansong/Documents/note/images/docker/image-20190729213831638.png)



```shell
kubectl config set-context def-ns-admin@kubernetes --cluster=kubernetes --user=def-ns-admin --kubeconfig=/root/def-ns-admin.conf
```

![image-20190729214003955](/Users/chenyansong/Documents/note/images/docker/image-20190729214003955.png)



```shell
kubectl config use-context def-ns-admin@kubernetes --kubeconfig=/root/def-ns-admin.conf
```

![image-20190729214145255](/Users/chenyansong/Documents/note/images/docker/image-20190729214145255.png)

然后这个`=/root/def-ns-admin.conf`就可以作为认证的config文件

![image-20190729214240315](/Users/chenyansong/Documents/note/images/docker/image-20190729214240315.png)





