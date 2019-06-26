[TOC]

# Docker容器

## 容器的基本操作

* 启动容器

  ```shell
  docker run Image [command] [arg...]
  
  #run表示启动一个容器
  #image：指定启动的镜像
  #command:
  
  docker run ubuntu echo 'hello world'
  ```

  ![1561456974068](https://github.com/chenyansong1/note/blob/master/images/docker/docker_command1.png?raw=true)

  > 我们看到容器启动执行完命令就退出了

* 启动交互式容器

  ```shell
  #
  docker run -i -t iamge /bin/bash
  
  #-i --interactive=true|false 默认是false ； 交互式
  #-t  --tty=true|false 默认是false，表示打开一个伪tty终端
  
  ```

  ![1561457432124](https://github.com/chenyansong1/note/blob/master/images/docker/docker_command2.png?raw=true)



* 查看容器

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

* 自定义容器名字

  ```shell
  docker run --name=container01 -i -t  ubuntu:15.10 /bin/bash
  
  #--name指定容器的名字
  ```

  ![1561516513407](https://github.com/chenyansong1/note/blob/master/images/docker/docker_command5.png?raw=true)








## 守护式容器









## 在容器中部署静态网站



