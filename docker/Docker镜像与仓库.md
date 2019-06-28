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

* 推送镜像

```shell

```





# 构建镜像





# Dockerfile指令







# Dockerfile构建过程







