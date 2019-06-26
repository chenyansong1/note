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

  ![1561517770962](E:\git-workspace\note\images\docker\docker_command6.png)






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

​	![1561518846963](E:\git-workspace\note\images\docker\docker_command7.png)



### 启动守护式容器

```shell
docker run -d image [command] [args...]

#-d指定容器后台运行
```

![1561519477215](E:\git-workspace\note\images\docker\docker_command8.png)

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



## 在容器中部署静态网站



