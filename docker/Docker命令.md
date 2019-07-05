[TOC]

![image-20190704220507930](/Users/chenyansong/Documents/note/images/docker/image-20190704220507930.png)



```shell
#原始命令
docker serach
docker pull
docker images

#按照分组实现的命令
docker image pull
docker image ls
Docker image ls --help

#docker image --help

```

当我们`docker serach xx`某个镜像的时候，同时我们也可以使用`https://hub.docker.com/`的官网上去看，比如我们搜索NGINX的镜像，可以看到镜像的详细的使用帮助



删除镜像

```shell
docker rmi
docker image rm

#docker rm container 这个是rm容器的
```



容器

```shell
docker container --help

#创建
docker container create 
docker container start
docker container stop
docker container kill	#停止终止
docker container rm
docker container pause
docker container unpause #取消暂停
docker container top	#查看容器的进程
docker container ls  #列出所有的容器


#启动一个容器
docker run [options] images [command] [args...]
-i #交互式
-t #打开一个终端
--name #为容器指定一个名字
--rm #当容器exit退出的时候，自动remove
-d #运行在后台模式

docker run --name busynametest -it busybox:latest


#查看容器
docker ps -a #查看所有的容器(包括停止的容器)
docker container ls -a


#查看运行中的容器
docker ps
docker container ls

#启动处于停止状态的容器
docker start containerName|Id
docker container start containerName|Id

#删除停止状态的容器
docker container rm containerName|Id


```



查看容器的详情

```shell
docker inspect containerName
```



在运行的容器中运行命令

```shell
docker container exec --help

docker container exec [options] containerName command [args...]

#登录进入容器(shell)
docker exec -it containerName /bin/sh 

#进入容器之后，我们可以执行一些测试的命令
netstat -lant
```



docker的日志

```shell
docker container logs --help

```



网络

```shell
#查看现有的网络
docker network ls

```

![image-20190704230520116](/Users/chenyansong/Documents/note/images/docker/image-20190704230520116.png)





