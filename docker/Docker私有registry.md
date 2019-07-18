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



#  私有仓库harbor

harbor是一个类似于docker hub的私有镜像仓库，他是基于docker-compose的，但是他有完整的管理界面

官网：https://github.com/goharbor/harbor/blob/master/docs/installation_guide.md

harbor需要依赖下面这些

### Software

| Software       | Version                       | Description                                                  |
| -------------- | ----------------------------- | ------------------------------------------------------------ |
| Docker engine  | version 17.06.0-ce+ or higher | For installation instructions, please refer to: [docker engine doc](https://docs.docker.com/engine/installation/) |
| Docker Compose | version 1.18.0 or higher      | For installation instructions, please refer to: [docker compose doc](https://docs.docker.com/compose/install/) |
| Openssl        | latest is preferred           | Generate certificate and keys for Harbor                     |

### Network ports

| Port | Protocol | Description                                                  |
| ---- | -------- | ------------------------------------------------------------ |
| 443  | HTTPS    | Harbor portal and core API will accept requests on this port for https protocol, this port can change in config file |
| 4443 | HTTPS    | Connections to the Docker Content Trust service for Harbor, only needed when Notary is enabled, This port can change in config file |
| 80   | HTTP     | Harbor portal and core API will accept requests on this port for http protocol |

```shell
tar zxvf harbor-offline-installer-v1.5.3.tgz

vim harbor.cfg
hostname=node02.chenyansong.com
harbor_admin_password=Harbor12345
db_host=mysql
db_password=root123

#运行（可能会很慢,因为需要加载镜像）
./install.sh

#需要安装docker-compose，他是在epel源中（yum install epel-release -y）
yum install -y docker-compose
```

![1563420380850](E:\git-workspace\note\images\docker\1563420380850.png)

浏览器访问

![1563420583833](E:\git-workspace\note\images\docker\1563420583833.png)

![1563420648238](E:\git-workspace\note\images\docker\1563420648238.png)

docker-compose也是一个启动了多个容器

![1563420713827](E:\git-workspace\note\images\docker\1563420713827.png)

推送镜像

```shell
#打标签
docker tag myweb:v0.3-1 node02.chenyansong.com/devel/myweb:0.3-1
docker tag myweb:v0.3-2 node02.chenyansong.com/devel/myweb:0.3-2

#首先登陆
docker login node02.chenyansong.com
username:admin
pwd:Harbor12345

#然后推送
docker push node02.chenyansong.com/devel/myweb
```

![1563421634563](E:\git-workspace\note\images\docker\1563421634563.png)

停止harbor服务

```shell
#harbor使用的是docker-compose去启动的
docker-compose --help

#进入docker-compose.yml文件所在的目录下
#docker-compose pause
[root@spark02 harbor]# docker-compose pause
Pausing harbor-log         ... done
Pausing harbor-adminserver ... done
Pausing registry           ... done
Pausing redis              ... done
Pausing harbor-db          ... done
Pausing harbor-ui          ... done
Pausing harbor-jobservice  ... done
Pausing nginx              ... done
[root@spark02 harbor]# 

#重新启动
docker-compose unpause

#停止
docker-compose stop

#启动
docker-compose start

```



