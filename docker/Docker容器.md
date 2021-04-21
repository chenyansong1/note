[TOC]

# Docker容器

## 容器的基本操作

### 启动容器

  ```shell
  docker run Image [command] [arg...]
  
  #run表示启动一个容器
  #image：指定启动的镜像
  #command:
  
  docker run ubuntu echo 'hello world'
  ```

  ![1561456974068](https://github.com/chenyansong1/note/blob/master/images/docker/docker_command1.png?raw=true)

  > 我们看到容器启动执行完命令就退出了

### 启动交互式容器

  ```shell
  #
  docker run -i -t iamge /bin/bash
  
  #-i --interactive=true|false 默认是false ； 交互式
  #-t  --tty=true|false 默认是false，表示打开一个伪tty终端
  
  ```

  ![1561457432124](https://github.com/chenyansong1/note/blob/master/images/docker/docker_command2.png?raw=true)



### 查看容器

  ```shell
  docker ps [-a] [-l]
  
  #-a 列出所有的容器
  #-l 列出最新创建的容器
  #如果什么参数都不加，表示查看正在运行中的容器
  容器的唯一Id， names 容器的名字
  
  
  #查看容器的配置
  docker inspect [CONTAINER ID | NAMES]
  #返回的是：名称，命令，网络配置等
  
  ```

  ![1561458223375](https://github.com/chenyansong1/note/blob/master/images/docker/docker_command4.png?raw=true)

### 自定义容器名字

  ```shell
  docker run --name=container01 -i -t  ubuntu:15.10 /bin/bash
  
  #--name指定容器的名字
  ```

  ![1561516513407](https://github.com/chenyansong1/note/blob/master/images/docker/docker_command5.png?raw=true)



### 重新启动停止的容器

  ```shell
  docker star [i] 容器名
  #-i 表示以交互的方式启动
  ```

  

### 删除停止的容器

  ```shell
  docker rm [容器名|容器ID]
  #rm只能删除已经停止的容器
  ```

  ![1561517770962](..\images\docker\docker_command6.png)






## 守护式容器

1. 能够长期运行
2. 没有交互式会话
3. 适合运行应用程序和服务

### 后台运行一个容器

```shell
docker run -i -t image /bin/bash

#ctrl+P   crtl+Q 进入后台运行模式

#回到前台
docker attach containerName|containId
```

​	![1561518846963](..\images\docker\docker_command7.png)



### 启动守护式容器

```shell
docker run -d image [command] [args...]

#-d指定容器后台运行
```

![1561519477215](..\images\docker\docker_command8.png)

### 查看日志

```shell
docker logs [-f] [-t] [--tail] 容器名

#-f --follows=true|false 默认false 控制台实时打印
#-t --timestamps=true|false默认为false 在日志前面加上时间
#--tail="all"  这样会显示所有的日志 tail number只显示最新的多少条


#example
#返回所有的日志
docker logs	containername

#-t 会加上时间
docker logs -t containername


#-f 只显示最新的几条
docker logs -f containername

#tail number只显示最新的2条
docker logs -f --tail 2 container02
[root@spark01 ~]# docker logs -f --tail 2 container02
hello world
hello world
```

### 查看容器内的进程

```shell
docker top 容器名

```

![1561519477215](..\images\docker\docker09.png)

### 在运行的容器中启动新进程
```shell
docker exec [-d] [-i] [-t] containerName [command] [arg...]
#-d 后台执行
#-i 交互式
#-t 打开终端


docker start container02

# 在容器中启动另外一个进程bash
docker exec -i -t container02 /bin/bash

#使用Crtl P crtl Q之后，容器进入后台，然后top看进程
docker top container02
[root@spark01 ~]# 


```

### 停止运行中的容器


```shell
docker stop 容器名

docker kill 容器名

#区别：stop会等待容器停止，kill会直接结束容器
```

​	



## 在容器中部署静态网站

* 容器的端口映射

  ```shell
  run [-p|-P]
  # -P --publish-all=true|false，默认为false 为容器暴露的所有端口进行映射
  docker run -P -i -t ubuntu /bin/bash
  
  #-p,--publish=[]
  containerPort  只指定容器的端口，宿主机的端口是随机的
  	docker run -p 90 -i -t ubuntu /bin/bash
  hostPort:containerPort  同时指定宿主机端口和容器端口
  	docker run -p 8080:80 -i -t ubuntu /bin/bash
  ip:containerPort  指定IP和容器端口
  	docker run -p 0.0.0.0:80 -i -t ubuntu /bin/bash
  	
  ip:hostPort:containerPort  指定IP,宿主机端口，容器端口
  	docker run -p 0.0.0.0:80 -i -t ubuntu /bin/bash
  ```

Nginx部署静态网站

* 创建映射80端口的交互式容器
* 安装Nginx
* 安装文本编辑器vim
* 创建静态页面
* 修改Nginx配置文件
* 运行Nginx
* 验证网站访问

```shell
#docker run -p 80 --name web -t -i ubuntu:15.10 /bin/bash

#yum install -y nginx

#yum install -y vim

#mkdir -p /var/www/html
#cd /var/www/html
#echo "hello world">index.html

[root@spark01 ~]# docker ps
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS                   NAMES
3f085ab475e7        ubuntu:15.10        "/bin/bash"         5 minutes ago       Up 5 minutes        0.0.0.0:32768->80/tcp   web
[root@spark01 ~]# 
#可以看到
```

​	

可以`docker inspect web` 去看容器的详情，下图，我们可看到**容器的IP地址和端口映射的情况**

![1561548125207](..\images\docker\docker_command9.png)

访问Nginx

```shell
curl 宿主机IP:产生的随机端口
curl 容器的IP:80(可省略)

```

停止容器之后，重新启动Nginx服务

```shell
#停止容器
docker stop web

#重新启动容器，发现Nginx服务并没有启动
docker start -i web
#进入容器之后，ps -ef|grep nginx

#在宿主机上，exec启动服务
docker exec web nginx

#查看容器启动的进程
docker top web

#ip：port访问Nginx
curl http://host:port
#此时容器的IP地址已经变了，需要重新inspect查看

```

