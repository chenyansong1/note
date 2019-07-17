[TOC]

配置文件的解决方案

通过环境变量的配置，**通过加载系统之上的环境变量**，获取配置，而不通过配置文件，这样我们就不用处理配置文件



# 语法格式

* 格式

  ```shell
  #Comment 注释
  INSTRUCTION arguments 指令
  
  ###指令是不区分大小写的，但是约定是使用大写###
  #顺序执行的指令
  #第一个非注释行必须是FROM指令，用来指明你的基础镜像
  ```

  

dockeringore

底层的镜像启动为容器，然后Dockerfile使用配置，去重新生成了一个镜像，只不过这个过程，Dockerfile帮我们做了

# 环境变量

环境变量使用的方式

```shell
$variable_name
${variable_name}

#还支持变量替换的格式
${variable:-word}  #如果variable值为设置或空，则用word
${variable:+word}  #variable有值则显示word
```



# Dockerfile指令

* FROM

  FROM指令是最重要的一个且必须为Dockerfile文件开篇的第一个非注释行，用于为映像文件构建过程指定基准镜像，后续的指令运行于此基准镜像所提供的运行环境

  在实践中，基准镜像可以是任何可用的镜像文件，默认情况下，docker build 会在docker主机上查找指定的镜像文件，在其不存在时，则会从Docker Hub Registry上拉取所需的镜像文件（如果找不到指定的文件，docker build会返回一个错误的信息）

* Syntax

  * FROM

  ```shell
  FROM <repository>[:<tag>]
  #or
  FROM <repository>@<digest>
  	<repository>：指定作为base image的名称
  	<tag>:base image的标签，可选项，省略时默认为latest
  	<digest>:每个镜像的hash码（使用镜像名称，如果别人将镜像名称所在的镜像替换为挖矿时，我们还是使用的是改镜像名称，那么就会有问题，但是使用md5值，就可以避免这个问题）	
  ```

  

  * MAINTAINER  LABEL

  ```shell
  MAINTAINER(depreacted:废弃)
  用于让Dockerfile制作者提供本人的详细信息
  Dockerfile并不限制MAINTAINER指令可出现的位置，但推荐将其放置于FROM指令之后
  
  MAINTAINER <author detail>
  #<author detail>可以是任何文本信息，但约定俗成使用作者名称及邮件地址
  MAINTAINER "chenyansong <chenyansong@qq.com>"
  
  
  #替代者;label添加元数据到镜像中
  LABEL <key>=<value> <key>=<value>...
  LABEL maintainer="chenyansong <chenyansong@163.com>"
  
  ```

  * COPY

    从当前工作目录中复制文件到目标镜像文件系统中

    ```shell
    用于从Docker主机复制文件到创建的新映像文件
    COPY <SRC>...<DEST>
    #OR
    COPY ["<SRC>",... "<DEST"]
    
    <SRC>:	要复制的源文件或目录，支持使用通配符
    <dest>: 目标路径，即正在创建的image的文件系统路径，建议为<dest>使用绝对路径，否则COPY指定则以WORKDIR为其起始路径
    注意：在路径中有空白符时，通常使用第二种格式
    
    #文件复制准则
    <src>必须是build上下文中的路径，不能是其父目录中的文件
    
    如果<src》是目录，则其内部文件或子目录会被递归复制，但<src>目录自身不会被复制
    
    如果指定了多个<src>，或在<src>中使用了通配符，则<dest>必须是一个目录，且必须以/结尾
    
    如果<dest>事先不存在，他将会被自动创建，这包括其父目录路径
    
    #example
    COPY index.html /data/web/html/
    
    #index.html一定要在Dockerfile所在的目录，或者是子目录中
    ```

  * docker build 制作镜像

    ```shell
    See 'docker build --help'.
    
    Usage:  docker build [OPTIONS] PATH | URL | -
    #PATH:Dockerfile所在的父目录
    ```

    ![1563333953895](E:\git-workspace\note\images\docker\1563333953895.png)

    ![1563333998882](E:\git-workspace\note\images\docker\1563333998882.png)

    ![1563334125493](E:\git-workspace\note\images\docker\1563334125493.png)

    拷贝目录

    ![1563334666625](E:\git-workspace\note\images\docker\1563334666625.png)

    ![1563334756558](E:\git-workspace\note\images\docker\1563334756558.png)