[TOC]

# Docker目录结构说明

```shell
#查看docker的工作目录
docker info

```







```shell


#docker的工作目录
[root@spark01 ~]# ll /var/lib/docker/
total 4
drwx------.  2 root root   23 Jun 24 16:38 builder
drwx------.  4 root root   87 Jun 24 16:38 buildkit
drwx------. 16 root root 4096 Jun 26 19:43 containers
drwx------.  5 root root   50 Jun 24 16:39 devicemapper
drwx------.  3 root root   25 Jun 24 16:38 image
drwxr-x---.  3 root root   18 Jun 24 16:38 network
drwx------.  4 root root   30 Jun 24 16:38 plugins
drwx------.  2 root root    6 Jun 24 16:38 runtimes
drwx------.  2 root root    6 Jun 24 16:38 swarm
drwx------.  2 root root    6 Jun 25 18:00 tmp
drwx------.  2 root root    6 Jun 24 16:38 trust
drwx------.  2 root root   24 Jun 24 16:38 volumes
```



# 查看和删除镜像





# 获取和推送镜像





# 构建镜像





# Dockerfile指令







# Dockerfile构建过程







