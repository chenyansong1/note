[TOC]



* 依赖的基础环境
  * 64bits CPU
  * Linux kernel 3.10+
  * Linux Kernel cgroups and namespaces
* CentOS 7
  * Extras repository
* Docker Daemon
  * systemctl start docker.service
* Docker Client
  * docker [options] command [args...]



清华的镜像：https://mirrors.tuna.tsinghua.edu.cn/

![1562242831265](..\images\docker\instruc3.png)





1. 仓库配置文件：

   wget https://mirrors.tuna.tsinghua.edu.cn/docker-ce/linux/centos/docker-ce.repo 

   wget  http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo

2. yum install  docker-ce  (这个是为了区别ce和ee)

   如果用的是centos的extra中的：yum install docker 

# CentOS Docker安装

Docker支持以下的CentOS版本：

- CentOS 7 (64-bit)
- CentOS 6.5 (64-bit) 或更高的版本

------

## 前提条件

目前，CentOS 仅发行版本中的内核支持 Docker。

Docker 运行在 CentOS 7 上，要求系统为64位、系统内核版本为 3.10 以上。

Docker 运行在 CentOS-6.5 或更高的版本的 CentOS 上，要求系统为64位、系统内核版本为 2.6.32-431 或者更高版本。

------

## 使用 yum 安装（CentOS 7下）

Docker 要求 CentOS 系统的内核版本高于 3.10 ，查看本页面的前提条件来验证你的CentOS 版本是否支持 Docker 。

通过 `uname -r` ，命令查看你当前的内核版本

```shell
[root@runoob ~]# uname -r 
```



![img](E:\git-workspace\note\images\docker\docker08.png)

## 安装 Docker

从 2017 年 3 月开始 docker 在原来的基础上分为两个分支版本: Docker CE 和 Docker EE。

Docker CE 即社区免费版，Docker EE 即企业版，强调安全，但需付费使用。

本文介绍 Docker CE 的安装使用。

移除旧的版本：

```shell
$ sudo yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-selinux \
                  docker-engine-selinux \
                  docker-engine
```

安装一些必要的系统工具：

```shell
sudo yum install -y yum-utils device-mapper-persistent-data lvm2
```

添加软件源信息：

```shell
sudo yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo

#直接使用阿里云repo，就不用改repo的东西，如果使用的是清华大学的就要改repo了
```

更新 yum 缓存：

```shell
sudo yum makecache fast
```

安装 Docker-ce：

```shell
#yum install  docker-ce  (这个是为了区别ce和ee)
#如果用的是centos的extra中的：yum install docker 
sudo yum -y install docker-ce
```

启动 Docker 后台服务

```shell
sudo systemctl start docker
```

测试运行 hello-world

```
[root@runoob ~]# docker run hello-world
```



![img](E:\git-workspace\note\images\docker\docker12.png)

由于本地没有hello-world这个镜像，所以会下载一个hello-world的镜像，并在容器内运行。

------

## 添加非root用户

```shell
#创建一个docker的用户组
sudo groupadd docker
#将当前的用户添加到用户组中，这样我们就可以用${USER}这个用户去执行docker命令了
sudo gpasswd -a ${USER} docker
#重新启动docker服务
sudo service docker restart
```



![1561456256653](E:\git-workspace\note\images\docker\docker_add_user2.png)



## 镜像加速

鉴于国内网络问题，后续拉取 Docker 镜像十分缓慢，我们可以需要配置加速器来解决，我使用的是网易的镜像地址：**http://hub-mirror.c.163.com**。

新版的 Docker 使用 /etc/docker/daemon.json（Linux） 或者 %programdata%\docker\config\daemon.json（Windows） 来配置 Daemon。

请在该配置文件中加入（没有该文件的话，请先建一个）：

```shell
{
  "registry-mirrors": ["http://hub-mirror.c.163.com"]
}
```

------

## 删除 Docker CE

执行以下命令来删除 Docker CE：

```shell
$ sudo yum remove docker-ce
$ sudo rm -rf /var/lib/docker
```



# 配置文件

```shell
docker-ce:
#配置文件：/etc/docker/daemon.json
#为了我们融docker的仓库中下载镜像（image）比较的快，我们要使用镜像加速器

```

# 镜像加速

1. 阿里云的加速器，但是需要注册
2. 使用中国科技大学

```shell 
对于使用 systemd 的系统，请在 /etc/docker/daemon.json 中写入如下内容（如果文件不存在请新建该文件）

{
  "registry-mirrors": [
    "https://dockerhub.azk8s.cn",
    "https://reg-mirror.qiniu.com"
  ]
}

#注意，一定要保证该文件符合 json 规范，否则 Docker 将不能启动。

#之后重新启动服务。

$ sudo systemctl daemon-reload
$ sudo systemctl restart docker

#注意：如果您之前查看旧教程，修改了 docker.service 文件内容，请去掉您添加的内容（--registry-mirror=https://dockerhub.azk8s.cn）
```



# docker命令简介

```shell
[root@spark01 ~]# docker 

Usage:  docker [OPTIONS] COMMAND

A self-sufficient runtime for containers

Options:
      --config string      Location of client config
                           files (default "/root/.docker")
  -D, --debug              Enable debug mode
  -H, --host list          Daemon socket(s) to connect to
  -l, --log-level string   Set the logging level
                           ("debug"|"info"|"warn"|"error"|"fatal") (default "info")
      --tls                Use TLS; implied by --tlsverify
      --tlscacert string   Trust certs signed only by
                           this CA (default
                           "/root/.docker/ca.pem")
      --tlscert string     Path to TLS certificate
                           file (default
                           "/root/.docker/cert.pem")
      --tlskey string      Path to TLS key file
                           (default
                           "/root/.docker/key.pem")
      --tlsverify          Use TLS and verify the remote
  -v, --version            Print version information
                           and quit

#管理类
Management Commands:
  builder     Manage builds
  config      Manage Docker configs
  container   Manage containers
  engine      Manage the docker engine
  image       Manage images
  network     Manage networks
  node        Manage Swarm nodes
  plugin      Manage plugins
  secret      Manage Docker secrets
  service     Manage services
  stack       Manage Docker stacks
  swarm       Manage Swarm
  system      Manage Docker
  trust       Manage trust on Docker images
  volume      Manage volumes

Commands:
  attach      Attach local standard input, output, and error streams to a running container
  build       Build an image from a Dockerfile
  commit      Create a new image from a container's changes
  cp          Copy files/folders between a container and the local filesystem
  create      Create a new container
  diff        Inspect changes to files or directories on a container's filesystem
  events      Get real time events from the server
  exec        Run a command in a running container
  export      Export a container's filesystem as a tar archive
  history     Show the history of an image
  images      List images
  import      Import the contents from a tarball to create a filesystem image
  info        Display system-wide information
  inspect     Return low-level information on Docker objects
  kill        Kill one or more running containers
  load        Load an image from a tar archive or STDIN
  login       Log in to a Docker registry
  logout      Log out from a Docker registry
  logs        Fetch the logs of a container
  pause       Pause all processes within one or more containers
  port        List port mappings or a specific mapping for the container
  ps          List containers
  pull        Pull an image or a repository from a registry
  push        Push an image or a repository to a registry
  rename      Rename a container
  restart     Restart one or more containers
  rm          Remove one or more containers
  rmi         Remove one or more images
  run         Run a command in a new container
  save        Save one or more images to a tar archive (streamed to STDOUT by default)
  search      Search the Docker Hub for images
  start       Start one or more stopped containers
  stats       Display a live stream of container(s) resource usage statistics
  stop        Stop one or more running containers
  tag         Create a tag TARGET_IMAGE that refers to SOURCE_IMAGE
  top         Display the running processes of a container
  unpause     Unpause all processes within one or more containers
  update      Update configuration of one or more containers
  version     Show the Docker version information
  wait        Block until one or more containers stop, then print their exit codes

```

查看每个命令的子命令

```shell
#docker container --help

[root@spark01 ~]# docker container --help

Usage:  docker container COMMAND

Manage containers

Commands:
  attach      Attach local standard input, output, and error streams to a running container
  commit      Create a new image from a container's changes
  cp          Copy files/folders between a container and the local filesystem
  create      Create a new container
  diff        Inspect changes to files or directories on a container's filesystem
  exec        Run a command in a running container
  export      Export a container's filesystem as a tar archive
  inspect     Display detailed information on one or more containers
  kill        Kill one or more running containers
  logs        Fetch the logs of a container
  ls          List containers
  pause       Pause all processes within one or more containers
  port        List port mappings or a specific mapping for the container
  prune       Remove all stopped containers
  rename      Rename a container
  restart     Restart one or more containers
  rm          Remove one or more containers
  run         Run a command in a new container
  start       Start one or more stopped containers
  stats       Display a live stream of container(s) resource usage statistics
  stop        Stop one or more running containers
  top         Display the running processes of a container
  unpause     Unpause all processes within one or more containers
  update      Update configuration of one or more containers
  wait        Block until one or more containers stop, then print their exit codes

Run 'docker container COMMAND --help' for more information on a command.
[root@spark01 ~]# 
```



docker的版本

```shell
[root@spark01 ~]# docker version
Client:
 Version:           18.09.6
 API version:       1.39
 Go version:        go1.10.8
 Git commit:        481bc77156
 Built:             Sat May  4 02:34:58 2019
 OS/Arch:           linux/amd64
 Experimental:      false

Server: Docker Engine - Community
 Engine:
  Version:          18.09.6
  API version:      1.39 (minimum version 1.12)
  Go version:       go1.10.8 #编写的语言版本
  Git commit:       481bc77
  Built:            Sat May  4 02:02:43 2019
  OS/Arch:          linux/amd64 #平台版本
  Experimental:     false
[root@spark01 ~]# 
```



docker info

```shell
#查看docker的更加详细的版本
[root@spark01 ~]# docker info
Containers: 2
 Running: 2
 Paused: 0
 Stopped: 0
Images: 7
Server Version: 18.09.6
Storage Driver: devicemapper #centos7.4之后，换成了overlay2
 Pool Name: docker-253:0-201561120-pool
 Pool Blocksize: 65.54kB
 Base Device Size: 10.74GB
 Backing Filesystem: xfs
 Udev Sync Supported: true
 Data file: /dev/loop0
 Metadata file: /dev/loop1
 Data loop file: /var/lib/docker/devicemapper/devicemapper/data
 Metadata loop file: /var/lib/docker/devicemapper/devicemapper/metadata
 Data Space Used: 1.685GB
 Data Space Total: 107.4GB
 Data Space Available: 46.86GB
 Metadata Space Used: 2.662MB
 Metadata Space Total: 2.147GB
 Metadata Space Available: 2.145GB
 Thin Pool Minimum Free Space: 10.74GB
 Deferred Removal Enabled: true
 Deferred Deletion Enabled: true
 Deferred Deleted Device Count: 0
 Library Version: 1.02.149-RHEL7 (2018-07-20)
Logging Driver: json-file
Cgroup Driver: cgroupfs
Plugins:#产检
 Volume: local #卷
 Network: bridge host macvlan null overlay#网络
 Log: awslogs fluentd gcplogs gelf journald json-file local logentries splunk syslog#日志
Swarm: inactive
Runtimes: runc
Default Runtime: runc
Init Binary: docker-init
containerd version: bb71b10fd8f58240ca47fbb579b9d1028eea7c84
runc version: 2b18fe1d885ee5083ef9f0838fee39b62d653e30
init version: fec3683
Security Options:#安全选项
 seccomp
  Profile: default
Kernel Version: 3.10.0-327.el7.x86_64#内核
Operating System: CentOS Linux 7 (Core)
OSType: linux
Architecture: x86_64
CPUs: 4
Total Memory: 7.543GiB
Name: spark01
ID: 7HNX:S7RM:ZXXM:SJFZ:IOOL:E5Z7:66SJ:WDPO:2F2C:GIFB:GIYS:LE4U
Docker Root Dir: /var/lib/docker
Debug Mode (client): false
Debug Mode (server): false
Registry: https://index.docker.io/v1/
Labels:
Experimental: false
Insecure Registries:
 127.0.0.0/8
Registry Mirrors:#镜像仓库
 https://dockerhub.azk8s.cn/
 https://reg-mirror.qiniu.com/
Live Restore Enabled: false
Product License: Community Engine

WARNING: the devicemapper storage-driver is deprecated, and will be removed in a future release.
WARNING: devicemapper: usage of loopback devices is strongly discouraged for production use.
         Use `--storage-opt dm.thinpooldev` to specify a custom block storage device.
[root@spark01 ~]# 
```


