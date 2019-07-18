[TOC]

# 镜像仓库分类

![1563413172887](E:\git-workspace\note\images\docker\1563413172887.png)



# 安装私有镜像仓库



托管镜像，存储卷方式存储镜像

我们安装registry

```shell
yum install -y docker-registry
```

其实registry内部包含的是一个distribution的程序

![1563414026370](E:\git-workspace\note\images\docker\1563414026370.png)

我们可以看到安装生成了下面的文件

![1563414090501](E:\git-workspace\note\images\docker\1563414090501.png)

```shell
#配置文件
/etc/docker-distribution/registry/config.yml
#主程序
/usr/bin/registry
#启动服务
/usr/lib/systemd/system/docker-distribution.service
#数据目录
/var/lib/registry
```

修改配置文件

```shell
[root@spark02 img3]# cat /etc/docker-distribution/registry/config.yml
version: 0.1
log:
  fields:
    service: registry
storage:
    cache:#内存缓存
        layerinfo: inmemory
    filesystem:#数据目录
        rootdirectory: /var/lib/registry
http:#监听：0.0.0.0：5000
    addr: :5000

```

启动服务

```shell
systemctl start  docker-distribution

#查看监听的端口
```

![1563414580597](E:\git-workspace\note\images\docker\1563414580597.png)

# 向私有仓库中push镜像

```shell
#打标签
docker tag myweb:v0.3-11 node02.chenyansong.com:5000/myweb:v0.3-11

#docker image ls

#push
docker push node02.chenyansong.com:5000/myweb:v0.3-11
```

这里会有一个报错，服务器端使用的是HTTP，但是客户端使用的是HTTPS

![1563416357009](E:\git-workspace\note\images\docker\1563416357009.png)

我们可以改变docker的服务为非安全的

```shell
[root@spark02 img3]# vim /etc/docker/daemon.json 
{
 "registry-mirrors":["https://registry.docker-cn.com"],
 "insecure-registries":["node02.chenyansong.com:5000"]
}

#然后重启docker服务
systemctl restart docker

#重新push
docker push node02.chenyansong.com:5000/myweb:v0.3-11

#进入本地镜像仓库查看
/var/lib/registry
```



# 在其他地方pull镜像

```shell
#在其他地方也要接受不安全的设置
[root@spark03 img3]# vim /etc/docker/daemon.json 
{
 "registry-mirrors":["https://registry.docker-cn.com"],
 "insecure-registries":["node02.chenyansong.com:5000"]
}

#然后重启docker服务
systemctl restart docker

#下载镜像
docker pull node02.chenyansong.com:5000/myweb:v0.3-11
```













