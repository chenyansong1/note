参见：https://www.zentao.net/book/zentaopmshelp/303.html

[TOC]



# 一、下载地址

禅道开源版：     http://dl.cnezsoft.com/zentao/docker/docker_zentao.zip

数据库用户名：  **root**,默认密码：  **123456**。运行时，可以设置  **MYSQL_ROOT_PASSWORD**变量来更改密码。

可挂载目录

**/app/zentaopms**:该目录为禅道目录，里面包含禅道代码及附件上传目录。

**/var/lib/mysql**:该目录为数据库的数据目录。

# 二、安装使用

注意：需要关闭下selinux

## 1.创建数据目录

```shell
useradd docker
cd /home/docker
mkdir -p /home/docker/data/{mysql,chandao}
```



## 2.构建镜像

下载安装包，解压缩。 进入docker_zentao目录，执行命令 docker build -t [镜像名称] [Dockerfile所在目录]

```
docker build -t zentao ./
```

## 3.运行镜像

```
docker run --name [容器名称] -p [主机端口]:80 -v [主机代码目录]:/app/zentaopms -v [主机数据目录]:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=[数据库密码] -d [镜像名]:latest
```

例如

创建 /data/www /data/data 目录。

执行命令：

```shell
docker run --name zentao -p 80:80 -v /data/www:/app/zentaopms -v /data/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=123456 -d zentao:latest



#如果是root，需要特殊的权限，所以汇报如下的错误
ls: cannot open directory .: Permission denied
原因：CentOS 7中默认安全设置Selinux开启的原因

有三种办法解决：

在运行容器时，给容器加特权，即加上--privileged=true参数
关闭selinux，即宿主机中运行#setenforce 0命令
添加selinux规则，改变挂载目录的安全性文本
chcon [-R] [-t type] [-u user] [-r role] 文件或者目录
chcon -Rt svirt_sandbox_file_t /soft

#修改如下
docker run --privileged=true --name zentao -p 8088:80 -v /home/docker/data/:/app/zentaopms -v /home/docker/data/mysql/:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=123456 -d zentao:latest

```

运行成功

![img](https://github.com/chenyansong1/note/blob/master/images/docker/file.png?raw=true)

## 4.安装禅道

![](https://github.com/chenyansong1/note/blob/master/images/docker/file-1563860347709.png?raw=true)

