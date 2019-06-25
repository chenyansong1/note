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

  ![1561456974068](E:\git-workspace\note\images\docker\docker_command1.png)

  > 我们看到容器启动执行完命令就退出了

* 启动交互式容器

  ```shell
  #
  docker run -i -t iamge /bin/bash
  
  #-i --interactive=true|false 默认是false ； 交互式
  #-t  --tty=true|false 默认是false，表示打开一个伪tty终端
  
  ```

  ![1561457432124](E:\git-workspace\note\images\docker\docker_command2.png)



* 查看容器

  ```shell
  docker ps [-a] [-l]
  
  #-a 列出所有的容器
  #-l 列出最新创建的容器
  #如果什么参数都不加，表示查看正在运行中的容器
  容器的唯一Id， names 容器的名字
  
  
  #查看容器的配置
  docker inspect [CONTAINER ID | NAMES]
  
  ```

  ![1561458223375](E:\git-workspace\note\images\docker\docker_command4.png)



![1561458364055](E:\git-workspace\note\images\docker\test.png)

## 守护式容器









## 在容器中部署静态网站



