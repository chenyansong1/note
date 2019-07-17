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

* ADD

  ```shell
  ###################
  ADD指令类似于COPY指令，ADD支持使用TAR文件和URI路径
  #语法
  ADD <SRC>... <DEST> 
  #OR
  ADD ["<SRC>",.."<DEST>"]
  
  #操作准则
  如果<src>为URI且<dest>不以/结尾，则<src>指定的文件将被下载并直接被创建为<dest>；如果<dest>以/结尾，则文件名URI指定的文件将被直接下载并保存为<dest>/<filename>
  
  如果<src>是一个本地系统上的压缩格式的tar文件，他将被展开为一个目录，其行为类似于"tar x"命令，然后，通过URL获取到的tar文件将不会自动展开
  
  如果<src>有多个，或其 间接或直接使用了通配符，则<dest>必须是一个以/结尾的目录路径；如果<dest>不以/结尾，则其被视作一个普通文爱你，<src>的内容将被直接写入到<dest>中
  ###################
  ```

  ```shell
  #vim Dockerfile
  ADD http://nginx.org/download/nginx-1.17.1.tar.gz /usr/local/src/
  
  #构建镜像
  docker build -t tinyhttpd:v0.1-3 ./
  ```

  ![1563335633532](E:\git-workspace\note\images\docker\1563335633532.png)

  ```shell
  ADD nginx-1.17.1.tar.gz /usr/local/src/
  docker build -t tinyhttpd:v0.1-4 ./
  ```

  ![1563335774729](E:\git-workspace\note\images\docker\1563335774729.png)

* WORKDIR

  ```shell
  用于为Dockerfile中所有的RUN, CMD， ENTRYPOINT, COPY, ADD指定设定工作目录
  
  #语法
  WORKDIR <dirpath>
  	在Dockerfile文件中，WORKDIR指令可出现多次，其路径也可以为相对路径，不过，其是相对此前一个WORKDIR指令指定的路径
  	另外，WORKDIR也可调用ENV指定定义的变量
  	
  #example
  WORKDIR /var/log
  WORKDIER $STATEPATH
  
  WORKDIR /usr/local/src
  #ADD nginx-1.17.1.tar.gz /usr/local/src/
  ADD nginx-1.17.1.tar.gz ./
  ```

* VOLUME

  ```shell
  用于在image中创建一个挂载点目录（自动挂载卷），以挂载Docker host上的卷或其他容器上的卷
  
  #语法
  VOLUME <mountpoint>
  #or
  VOLUME ["<mountpoint>"]
  如果挂载点目录路径下此前文件存在，docker run命令会在卷挂载完成后将此前的所有文件复制到新挂载的卷中
  
  #example
  VOLUME /data/mysql/
  
  ```

  ![1563338401742](E:\git-workspace\note\images\docker\1563338401742.png)

* EXPOSE

  ```shell
  用于为容器打开指定要监听的端口，实现与外部通信
  #语法
  EXPOSE <PORT>[/<protocol>][<PORT>[/<protocol>]...]
  #<protocol>用于指定传输协议，可为tcp或udp二者之一，默认为TCP协议
  
  #EXPOSE指令可一次指定多个端口，例如：
  EXPOSE 11211/udp 11211/tcp
  #这里只是说要暴露的端口，但是是否真正要暴露，则需要-P来指定（表示暴露所有要暴露的端口）
  ```

  ![1563343831089](E:\git-workspace\note\images\docker\1563343831089.png)

  ![1563343849209](E:\git-workspace\note\images\docker\1563343849209.png)

  我们kill掉容器之后，指定 -P去暴露端口

  ![1563343900888](E:\git-workspace\note\images\docker\1563343900888.png)

  ![1563343916601](E:\git-workspace\note\images\docker\1563343916601.png)



* ENV

  用于为镜像定义所需的环境变量，并可被Dockerfile文件中位于其后的其他指令（如ENV，ADD， COPY等）所调用

  ```shell
  #调用格式
  $variable_name 
  #or
  ${variable_name}
  
  #语法
  ENV <KEY> <VALUE>
  #OR
  ENV <KEY>=<VALUE>...
  第一种格式中，<key>之后的所有内容均会被视作<value>的组成部分，因此，一次只能设置一个变量
  
  第二种格式可用一次设置多个变量，么个变量为一个"<key>=<value>"的键值对，如果是<value>中包含空格，可以以反斜线(\)进行转义，也可通过对<value>加引号进行标识，另外，反斜线也可用于续行
  
  定义多个变量时，建议使用第二种方式，一遍在同一层中完成所有功能
  ```

  ```shell
  ENV DOC_ROOT /data/web/html/
  COPY index.html $DOC_ROOT
  
  #没有的话，会有一个默认值
  COPY index.html ${DOC_ROOT:-/data/web/html/}
  
  ###########
  ENV DOC_ROOT=/data/web/html/ \
  	WEB_SERVER_PACKAGE="nginx-1.15.2"
  ADD ${WEB_SERVER_PACKAGE}.tar.gz ./src/
  ```

  可以在容器启动之后，打印变量

  ![1563345228233](E:\git-workspace\note\images\docker\1563345228233.png)

  在`docker run `的时候是可以向变量传值的

  ```shell
  docker run -e, --env list 
  docker run --name tinyweb1 --rm -P -e WEB_SERVER_PACKAGE="nginx-1.15.1" tinyhttpd:v0.1-7 printenv
  ```

  ![1563345633113](E:\git-workspace\note\images\docker\1563345633113.png)

  但是我们看在build过程中产生的版本

  ![1563345805535](E:\git-workspace\note\images\docker\1563345805535.png)

  **因为这个是在build已经生成了，而在run的时候指定的环境变量只是改变了，环境变量本身**

  ![1563345854693](E:\git-workspace\note\images\docker\1563345854693.png)



RUN和CMD的区别

![1563346056712](E:\git-workspace\note\images\docker\1563346056712.png)

所有的命令是基于基础镜像所提供的环境运行的命令，如果在基础镜像中没有这样的命令，那么就不能执行这些命令，**如果多个命令是有关联关系，建议将多条命令放在一起，如下**

* RUN

  ```shell
  用于指定docker build过程中运行的程序
  #语法
  RUN <command> 
  #or
  RUN ["<executable>", "param1", "param2"]
  
  第一种格式中，<command>通常是一个shell命令，且以"/bin/sh -c" 来运行他，这意味着此进程在容器中的PID不为1，不能接受Unix信号，因此，当使用docker stop <container>命令停止容器时，此进程接收不到sigterm信号
  
  第二种格式语法中的参数是一个json格式的数组，其中<executable>为要运行的命令，后面的<paramN>为传递给命令的选项参数，然而，此种格式指定的命令不会以"/bin/sh -c" 来发起，因此常见的shell操作如变量替换以及通配符（？，*等）替换将不会进行，不过如果要运行的命令依赖于此shell特性的话，可以将其替换为类似下面的格式
  RUN ["/bin/bash", "-c", "<executable>", "<param1>"]
  executable是直接启动为pid=1的进程的，所以当docker stop的时候，是可以将其停止的
  
  #example
  ADD http://nginx.org/download/nginx-1.17.1.tar.gz /usr/local/src/
  RUN cd /usr/local/src && \
          tar -xf nginx-1.17.1.tar.gz && \
          mv nginx-1.17.1  webserver
          
  # &&表示前一个成功，才会执行后一个
  # \表示续行
  ```

  ![1563347022284](E:\git-workspace\note\images\docker\1563347022284.png)

  ![1563347088365](E:\git-workspace\note\images\docker\1563347088365.png)

我们的基础镜像一般是一个centos的系统，如下，是我们在一个基础镜像之上进行的构建镜像

```shell
FROM centos
RUN yum -y install epel-release && yum install nginx
COPY 配置文件

#一般都是通过编译安装的，不使用yum安装
```



* CMD

  一般我们在shell中启动的进程，在shell退出之后，也会退出，因为这些进程的父进程是shell，所以我们需要再shell退出时，将启动的进程放到一个init的进程下面

  cmd是可以给多个，但是只有最后一个生效

  ```shell
  类似于RUN指令，CMD指令也可用于运行任何命令或应用程序，不过二者运行时间点不同
  
  RUN指令运行于映像文件构建过程中，而CMD指令运行于基于Dockerfile构建出的新映像文件启动一个容器时
  
  CMD指令的首要目的在于为启动的容器指定默认要运行的 程序，且其运行结束后，容器也将终止，不过，CMD指定的命令其可以被docker run的命令行选项所覆盖
  
  #语法
  CMD <COMMAND>
  #OR
  CMD ["<executable>", "<param1>", "<param1>"]
  #or
  CMD ["<param1>", "<param2>"]
  
  #前两种语法格式的意义通RUN，第三种则用于为ENTRYPOINT指令提供默认参数
  ```

  ```shell
  # cat Dockerfile 
  FROM busybox
  LABEL maintainer="chenyansong <chenyansong@163.com>" app="httpd"
  ENV WEB_DOC_ROOT="/data/web/html"
  
  RUN mkdir $WEB_DOC_ROOT -p  && \
      echo "<h1>busybox httpd server.</h1>" > ${WEB_DOC_ROOT}/index.html
  
  #启动httpd服务
  CMD /bin/httpd -f -h ${WEB_DOC_ROOT}
  ```

  ![1563354475992](E:\git-workspace\note\images\docker\1563354475992.png)

  容器启动之后，默认的执行的命令

  ![1563354498771](E:\git-workspace\note\images\docker\1563354498771.png)

  启动容器

  ```shell
  #我们发现程序会卡在启动的界面，因为默认启动的httpd,并不是shell
  docker run --name tinyweb2 -it --rm -P tinyhttpd:v0.2-1
  ```

  ![1563354689402](E:\git-workspace\note\images\docker\1563354689402.png)

  我们使用exec进入容器

  ```shell
  docker exec -it tinyweb2 /bin/sh
  ```

  ![1563354862462](E:\git-workspace\note\images\docker\1563354862462.png)



​		如果我们将Dockerfile修改如下

```shell
# cat Dockerfile 
FROM busybox
LABEL maintainer="chenyansong <chenyansong@163.com>" app="httpd"
ENV WEB_DOC_ROOT="/data/web/html"

RUN mkdir $WEB_DOC_ROOT -p  && \
    echo "<h1>busybox httpd server.</h1>" > ${WEB_DOC_ROOT}/index.html

#启动httpd服务
#CMD /bin/httpd -f -h ${WEB_DOC_ROOT}
CMD ["/bin/httpd", "-f", "-h ${WEB_DOC_ROOT}"]
```

![1563355133369](E:\git-workspace\note\images\docker\1563355133369.png)

​	此时是没有`/bin/sh`的

​	我们启动容器，会报错

![1563355235880](E:\git-workspace\note\images\docker\1563355235880.png)

因为在这种情况下，并不会运行为shell的子进程，那么对于`${WEB_DOC_ROOT}`这样的变量是不会被识别的，我们手动设置为shell的子进程

```shell
# cat Dockerfile 
FROM busybox
LABEL maintainer="chenyansong <chenyansong@163.com>" app="httpd"
ENV WEB_DOC_ROOT="/data/web/html"

RUN mkdir $WEB_DOC_ROOT -p  && \
    echo "<h1>busybox httpd server.</h1>" > ${WEB_DOC_ROOT}/index.html

#启动httpd服务
#CMD /bin/httpd -f -h ${WEB_DOC_ROOT}
#CMD ["/bin/httpd", "-f", "-h ${WEB_DOC_ROOT}"]
CMD ["/bin/sh", "-c","/bin/httpd", "-f", "-h ${WEB_DOC_ROOT}"]
CMD ["/bin/sh", "-c","/bin/httpd", "-f", "-h /data/web/html"]
```



CMD只有参数，没有命令的情况

* ENTRYPOINT

  类似CMD指令的功能，用于为容器指定默认运行程序，从而使一个容器像是一个单独的可执行程序

  与CMD不同的是，由ENTRYPOINT启动的程序不会被docker run命令行指定的参数所覆盖，而且，这些命令行参数会被当做参数传递给ENTRYPOINT指定的程序（不过，docker run 命令的--entrypoint选项的参数可覆盖ENTRYPOINT 指令指定的程序）

  ```shell
  #语法
  ENTRYPOINT <command>
  ENTRYPOINT ["<executable>", "param1", "param2"]
  
  #docker run命令传入的命令参数会覆盖CMD指令的内容并且附加到ENTRYPOINT命令最后作为其参数使用
  
  #Dockfile文件中也可以存在多个ENTRYPOINT指令，但仅有最后一个会生效
  
  
  #最后的 ls /data/web/html/ 更改了镜像中默认的运行的程序（通过CMD)指定的
  docker run --name tinyweb2 -it --rm -P tinyhttpd:v0.2-4 ls /data/web/html/
  
  #当我们如果不允许覆盖默认的CMD的命令，我们就要使用ENTRYPOINT
  ```

  我们使用entrypoint去设置默认运行的程序

  ```shell
  #CMD /bin/httpd -f -h ${WEB_DOC_ROOT}
  #CMD ["/bin/httpd", "-f", "-h ${WEB_DOC_ROOT}"]
  ENTRYPOINT /bin/httpd -f -h ${WEB_DOC_ROOT}
  ```

  然后我们启动容器的时候，给他默认的参数，但是我们发现并没有运行我们的参数，说明entrypoint是不会被修改的，而且将传过来的参数(ls /data)当做了entrypoint的参数

  ![1563357512986](E:\git-workspace\note\images\docker\1563357512986.png)

  如果使用的entrypoint，你还是想要覆盖，那么在docker run的时候使用--entrypoint参数

  ![1563357876896](E:\git-workspace\note\images\docker\1563357876896.png)

  

​	







