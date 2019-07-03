[TOC]

# Docker目录结构说明

显示 Docker 系统信息，包括镜像和容器数

```shell
#查看docker的工作目录
docker info

$ docker info
Containers: 12	#容器的数量
Images: 41		#镜像数
Storage Driver: aufs	
 Root Dir: /var/lib/docker/aufs	#容器的根目录
 Backing Filesystem: extfs	#使用的文件系统
 Dirs: 66
 Dirperm1 Supported: false
Execution Driver: native-0.2
Logging Driver: json-file
Kernel Version: 3.13.0-32-generic
Operating System: Ubuntu 14.04.1 LTS
CPUs: 1
Total Memory: 1.954 GiB
Name: iZ23mtq8bs1Z
ID: M5N4:K6WN:PUNC:73ZN:AONJ:AUHL:KSYH:2JPI:CH3K:O4MK:6OCX:5OYW


[root@spark01 ~]# docker info
Containers: 14		#容器数量
 Running: 1			#正在运行的容器
 Paused: 0
 Stopped: 13		#stop的容器
Images: 3			#镜像数量
Server Version: 18.09.6
Storage Driver: devicemapper
 Pool Name: docker-253:0-201561120-pool
 Pool Blocksize: 65.54kB
 Base Device Size: 10.74GB
 Backing Filesystem: xfs
 Udev Sync Supported: true
 Data file: /dev/loop0
 Metadata file: /dev/loop1
 Data loop file: /var/lib/docker/devicemapper/devicemapper/data
 Metadata loop file: /var/lib/docker/devicemapper/devicemapper/metadata
 Data Space Used: 396.2MB
 Data Space Total: 107.4GB
 Data Space Available: 48.62GB
 Metadata Space Used: 1.585MB
 Metadata Space Total: 2.147GB
 Metadata Space Available: 2.146GB
 Thin Pool Minimum Free Space: 10.74GB
 Deferred Removal Enabled: true
 Deferred Deletion Enabled: true
 Deferred Deleted Device Count: 0
 Library Version: 1.02.149-RHEL7 (2018-07-20)
Logging Driver: json-file
Cgroup Driver: cgroupfs
Plugins:
 Volume: local
 Network: bridge host macvlan null overlay
 Log: awslogs fluentd gcplogs gelf journald json-file local logentries splunk syslog
Swarm: inactive
Runtimes: runc
Default Runtime: runc
Init Binary: docker-init
containerd version: bb71b10fd8f58240ca47fbb579b9d1028eea7c84
runc version: 2b18fe1d885ee5083ef9f0838fee39b62d653e30
init version: fec3683
Security Options:
 seccomp
  Profile: default
Kernel Version: 3.10.0-327.el7.x86_64
Operating System: CentOS Linux 7 (Core)
OSType: linux
Architecture: x86_64
CPUs: 4
Total Memory: 7.543GiB
Name: spark01						#宿主机的主机名
ID: 7HNX:S7RM:ZXXM:SJFZ:IOOL:E5Z7:66SJ:WDPO:2F2C:GIFB:GIYS:LE4U
Docker Root Dir: /var/lib/docker	#docker的安装目录
Debug Mode (client): false
Debug Mode (server): false
Registry: https://index.docker.io/v1/
Labels:
Experimental: false
Insecure Registries:
 127.0.0.0/8
Live Restore Enabled: false
Product License: Community Engine

```



```shell
#docker的工作目录
[root@spark01 ~]# ll /var/lib/docker/
total 4
drwx------.  2 root root   23 Jun 24 16:38 builder
drwx------.  4 root root   87 Jun 24 16:38 buildkit
drwx------. 16 root root 4096 Jun 26 19:43 containers #容器
drwx------.  5 root root   50 Jun 24 16:39 devicemapper
drwx------.  3 root root   25 Jun 24 16:38 image	#镜像文件
drwxr-x---.  3 root root   18 Jun 24 16:38 network
drwx------.  4 root root   30 Jun 24 16:38 plugins
drwx------.  2 root root    6 Jun 24 16:38 runtimes
drwx------.  2 root root    6 Jun 24 16:38 swarm
drwx------.  2 root root    6 Jun 25 18:00 tmp
drwx------.  2 root root    6 Jun 24 16:38 trust
drwx------.  2 root root   24 Jun 24 16:38 volumes
```

# 查看和删除镜像

* 查看镜像

```shell
docker images [options] [repository]
#-a , --all=false 列出所有的镜像
#-f , --filter=[] 过滤条件
#--no-trunc=false 不截断镜像的Id
#-q,--quiet=false 只显示镜像的唯一Id

[root@spark01 ~]# docker images -a
REPOSITORY(仓库名)          TAG（镜像的标签名）                 IMAGE ID（镜像的Id，可能会截断）            CREATED             SIZE（镜像的大小）
nginx               latest              719cd2e3ed04        2 weeks ago         109MB
hello-world         latest              fce289e99eb9        5 months ago        1.84kB
ubuntu              15.10               9b9cb95443b5        2 years ago         137MB
[root@spark01 ~]# docker images
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
nginx               latest              719cd2e3ed04        2 weeks ago         109MB
hello-world         latest              fce289e99eb9        5 months ago        1.84kB
ubuntu              15.10               9b9cb95443b5        2 years ago         137MB
[root@spark01 ~]# docker images -q
719cd2e3ed04
fce289e99eb9
9b9cb95443b5
[root@spark01 ~]# docker images --no-trunc
REPOSITORY          TAG                 IMAGE ID                                                                  CREATED             SIZE
nginx               latest              sha256:719cd2e3ed04781b11ed372ec8d712fac66d5b60a6fb6190bf76b7d18cb50105   2 weeks ago         109MB
hello-world         latest              sha256:fce289e99eb9bca977dae136fbe2a82b6b7d4c372474c9235adc1741675f587e   5 months ago        1.84kB
ubuntu              15.10               sha256:9b9cb95443b5f846cd3c8cfa3f64e63b6ba68de2618a08875a119c81a8f96698   2 years ago         137MB
[root@spark01 ~]# 

```

* 仓库

  每个镜像下有很多种不同的版本

* tag标签

  镜像名+tag=完整的镜像名（有一个唯一的镜像Id），如果没有指定，默认是latest，没有标签名的镜像，就是中间层

  同一个仓库的相同标签可能对应的是同一个镜像Id，也就是说同样的镜像文件会**根据我们的需求不同**，打上不同的标签

* 查看某一个镜像

  ```shell
  docker images centos
  
  #查看镜像(容器)的详细信息
  docker inspect [options] container|image [container|image]
  #-f , --format
  ```

  

* 删除镜像

  ```shell
  docker rmi [options] image [image...]
  #-f, --force=false 强制删除镜像
  #--no-prune=false Don't delete untaged parents
  
  #删除多个镜像
  docker rmi ubuntu:12.04 ubuntu:precise
  
  #删除所有的镜像:-q拿到所有的镜像id 
  docker rmi $(docker images -q)
  ```

  ![1561620627226](E:\git-workspace\note\images\docker\docker_command10.png)

# 获取和推送镜像

* 查找镜像

  ```shell
  #方式1：通过官网Docker Hub 
  https://registry.hub.docker.com
  #这个要注册Docker Hub的网站
  #国内可以使用阿里云：https://yq.aliyun.com/articles/29941
  #但是也是要注册的
  
  
  #方式2：docker search [options] term
  --automated=false  #only show automated builds
  --no-trunc=flase   #Don't truncate output
  -s,--stars=0       #only displays with at least x stars
  #最多返回25个结果
  
  docker search nginx
  docker search -s 99 tomcat
  ```

  

* 拉取镜像

  ```shell
  docker pull [options] Name [:tag]
  -a, --all-tags=false 下载所有tag的到的镜像到本地
  
  docker pull [选项] [Docker Registry 地址[:端口号]/]仓库名[:标签]
  - Docker 镜像仓库地址：地址的格式一般是 <域名/IP>[:端口号]。默认地址是 Docker Hub。
  - 仓库名：如之前所说，这里的仓库名是两段式名称，即 <用户名>/<软件名>。对于 Docker Hub，如果不给出用户名，则默认为 library，也就是官方镜像。
  
  $ docker pull ubuntu:18.04
  上面的命令中没有给出 Docker 镜像仓库地址，因此将会从 Docker Hub 获取镜像。而镜像名称是 ubuntu:18.04，因此将会获取官方镜像 library/ubuntu 仓库中标签为 18.04 的镜像。
  
  #查看本地的镜像
  docker images -a
  
  [root@spark01 ~]# docker images -a
  REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
  nginx               latest              719cd2e3ed04        2 weeks ago         109MB
  ubuntu              15.10               9b9cb95443b5        2 years ago         137MB
  [root@spark01 ~]# 
  
  #拉取远程的镜像
  docker pull ubuntu:14.04
  ##这里既指定了name=ubuntu , 也指定了tag=14.04
  
  
  ```

* 解决拉取镜像缓慢的问题

  ```shell
  --registry-mirror 选项
  1. 修改： /etc/default/docker
  2. 添加：DOCKER_OPTS="--registry-mirror=http://mirror-address"
  
  
  对于使用 systemd 的系统，请在 /etc/docker/daemon.json 中写入如下内容（如果文件不存在请新建该文件）
  
  {
    "registry-mirrors": [
      "https://dockerhub.azk8s.cn",
      "https://reg-mirror.qiniu.com"
    ]
  }
  注意，一定要保证该文件符合 json 规范，否则 Docker 将不能启动。
  
  之后重新启动服务。
  
  $ sudo systemctl daemon-reload
  $ sudo systemctl restart docker
  注意：如果您之前查看旧教程，修改了 docker.service 文件内容，请去掉您添加的内容（--registry-mirror=https://dockerhub.azk8s.cn）。
  ```

  

* 推送镜像

```shell
docker push Name:[tag]

docker push username/nginx
#此时需要输入用户名
#输入密码
#输入验证邮箱
#这里需要注意的是：docker并不会将整个镜像都传到docker hub上，只会上传修改的部分，这个和git很像

#成功之后，就可以在docker hub的网站上看到
```

# 构建镜像

```shell
#两种方式

#通过容器构建镜像
docker commit 

#通过Dockerfile文件构建镜像
docker build


docker commit [options] container [repository[:tag]]
-a,--author=""  #指定镜像的作者（作者的名字+联系方式）
-m, --message="" #commit message 镜像构建的信息
-p, --pause=true #不去暂停正在使用的容器

docker commit -a 'username+email' -m 'nginx' commit_test usnername/commit_test1

#查看构建的镜像
docker images -a
```

# Dockerfile指令

* 创建Dockerfile文件
* 使用docker build命令

```shell
#创建第一个Dockerfile
#First Dockerfile
FROM ubuntu:14:04	#镜像的基础
MAINTAINER username "username@gmail.com" #镜像的维护人
#需要在镜像中执行的命令
RUN apt-get update
RUN apt-get install -y nginx
EXPOSE 80	#暴露的端口


docker build [options] path|url|-
--force-rm=false
--no-cache=false
--pull=false
-q,--quiet=false
--rm=true
-t,--tag=""

docker build -t="username/df_test"
```



* 指令格式

  ```shell
  instruction arg
  
  #example
  #First Dockerfile
  FROM ubuntu:14:04	#镜像的基础
  MAINTAINER username "username@gmail.com" #镜像的维护人
  #需要在镜像中执行的命令
  RUN apt-get update
  RUN apt-get install -y nginx
  EXPOSE 80	#暴露的端口
  ```



* FROM指令

  ```shell
  FROM <image>
  FROM <image>:<tag>
  
  #image必须是已经存在的镜像，必须是基础镜像，必须是第一条非注释指令
  
  
  ```

  

* MAINTAINER指令

  ```shell
  MAINTAINER <name>
  #指定镜像的作者信息，包含镜像的所有者和联系信息
  
  MAINTAINER username "username@gmail.com" #指定了作者名和作者的email地址
  ```

  

* RUN

  ```shell
  #指定当前镜像中运行的命令
  RUN <comman> (shell模式)
  RUN ["executable", "parm1", "param2"] (exec模式)
  
  #shell模式
  RUN echo hello
  
  #exec模式下
  RUN ["executable", "param1", "param2"]
  RUN ["/bin/bash", "-c", "echo hello"]
  ```

* EXPOSE

  ```shell
  EXPOSE <port> [<port>...]
  #指定运行该镜像的容器使用的端口
  
  EXPOSE 80
  
  #这里指定的端口，只是告诉docker，该容器中会使用这个端口，但是当我们启动特定的容器的时候，还是要指定端口映射
  
  docker run -p 80 -d username/df_test1 nginx -g "daemon off"
  ```

* CMD

  ```shell
  #提供容器运行的默认命令
  CMD ["executable", "param1", "param2"](exec模式)
  CMD command param1 param2 (shell模式)
  CMD ["params1", "params2"](作为entrypoint指令的默认参数) 和entrypoint搭配使用
  
  
  #run执行命令：是在镜像构建过程中执行的，他会覆盖cmd中的命令
  #cmd指定的命令是在容器运行中运行的
  ```

  

* ENTERYPOINT

  ```shell
  #entrypoint指令不会被run命令所覆盖
  ENTRYPONIT ["executable", "param1", "param2"](exec模式)
  
  ENTRYPOINT command param1 param2 (shell模式)
  
  #用entrypoint指定命令，用cmd指定命令运行的参数
  ```

  

* ADD ，COPY

  ```shell
  #将文件或目录复制到指定的文件镜像中
  #支持来源地址 和 目标地址
  ADD <src> ... <dest>
  ADD ["src"..."<dest>"](适用于文件路径中有空格的情况)
  
  COPY <src>...<dest>
  COPY["src"..."<dest>"](适用于文件路径中有空格的情况)
  
  #add包含类似于tar的解压功能
  #如果单纯复制文件，Docker推荐使用copy
  
  #拷贝index.html到指定的路径下
  COPY index.html /usr/share/nginx/html/
  
  ```

  

* VOLUME

  ```shell
  #向镜像容器中添加卷
  VOLUME ["/data"]
  ```

  

* WORKDIR

  ```shell
  #在容器中指定工作路径
  WORKDIR /path/to/workdir
  #需要使用绝对路径，如果使用相对路径，会产生路径传递
  WORKDIR /a
  WORKDIR b
  WORKDIR c
  RUN PWD
  /a/b/c
  ```

  

* ENV

  ```shell
  ENV <KEY><VALUE>
  ENV <KEY>=<VALUE>...
  ```

  

* USER

  ```shell
  #指定镜像会以什么样的用户去运行
  USER daemon
  
  #eg ：使用nginx用户去运行
  USER nginx
  
  USER user
  USER user:group
  USER user:gid
  
  USER uid
  USER uid:gid
  USER uid:group
  
  #默认使用的是root用户
  
  ```

  

* ONBUILD

  ```shell
  #镜像触发器
  #当一个镜像被其他镜像作为基础镜像时执行
  #当此镜像在构建时，会插入指令
  
  ```

  

# Dockerfile构建过程

* 从基础镜像运行一个容器
* 执行一条指令，对容器做出修改
* 对执行修改后的容器，执行类似docker commit的操作，提交一个新的镜像层
* 基于刚提交的镜像层运行一个新的容器
* 再执行Dockerfile中的下一条指令，反复重复上面的操作，直至所有的指令执行完毕

* 使用中间层镜像进行调试

  * 查找错误

* 构建缓存

  将之前的镜像看做缓存

* 不使用缓存

  ```shell
  docker build --no-cache
  ```

* 查看镜像的构建过程

  ```shell
  docker history image
  ```

  