转自：https://1530236181.iteye.com/blog/2427350

[TOC]

# 1. 问题描述及解决办法

在使用yum的时候，会有如下的报错

```
在使用yum的时候会报错：http://mirrors.aliyun.com/centos/%24releasever/addons/x86_64/repodata/repomd.xml: [Errno 14] HTTP Error 404: Not Found 
Trying other mirror. 
Error: Cannot retrieve repository metadata (repomd.xml) for repository: addons. Please verify its path and try again 
```



**错误的原因是”$releasever“这个变量没有被解析出来**，查看CentOS-Base.repo，才发现问题，源文件中的地址中的$releasever在终端中被替换成%24releasever。 
   搜了网上的一些博客，有人详细介绍了这个参数的代表意义 
   http://julyclyde.org/?p=275 
   然而而该博客却没有说明解决方法。 
   可以看出是$releasever产生的问题。因此进入/etc/yum.conf,发现是在distroverpkg=redhat-release对应的包中寻找$releasever变量，就用 rpm -q redhat-release查看是否安装了此包，结果package redhat-release is not installed。 

那要安装此包，可是yum又不能用，死循环呀！！想到把/etc/yum.repos.d/*.repo内的$releasever全部用centos5.8中的5替代，然后yum update，接着安装 yum install redhat-release ，结果为安装的是 centos-release（可以用rpm -q redhat-release查看依旧没结果，改为rpm -q centos-release，可以看见centos-release-5-10.el5.centos）。 

最后yum makecache,成功。进入/etc/yum.repos.d/*.repo内，发现又变为了$releasever，但是现在yum可以正常使用啦  



转自：https://blog.csdn.net/zgege/article/details/82315110



# 2.centos6.5环境下安装yum工具

前不久因为安装数据库时动了yum安装文档中的参数，导致yum安装软件时总是出现no package等问题，决定重装yum工具。

第一步：下载原有yum安装包

```
[root@linux-node3 ~]# rpm -qa yum
yum-3.4.3-150.el7.centos.noarch
[root@linux-node3 ~]# rpm -qa | grep yum | xargs rpm -e --nodeps          #这一步一定要执行，而且要将所有的组件卸载掉，如果卸载不干净，后面安装会有问题
[root@linux-node3 ~]# rpm -qa yum
```


下载包：

```
wget http://mirrors.163.com/centos/6/os/x86_64/Packages/python-iniparse-0.3.1-2.1.el6.noarch.rpm


wget http://mirrors.163.com/centos/6/os/x86_64/Packages/yum-metadata-parser-1.1.2-16.el6.x86_64.rpm

wget http://mirrors.163.com/centos/6/os/x86_64/Packages/yum-3.2.29-81.el6.centos.noarch.rpm

wget http://mirrors.163.com/centos/6/os/x86_64/Packages/yum-plugin-fastestmirror-1.1.30-41.el6.noarch.rpm
```




注意：有可能安装包时会出现不存在该包的情况，如下：

```
--2018-09-01 22:53:25--  http://mirrors.163.com/centos/6/os/x86_64/Packages/yum-plugin-fastestmirror-1.1.30-40.el6.noarch.rpm
Resolving mirrors.163.com... 59.111.0.251
Connecting to mirrors.163.com|59.111.0.251|:80... connected.
HTTP request sent, awaiting response... 404 Not Found
2018-09-01 22:53:25 ERROR 404: Not Found.
```




原因是因为该包发生更新了。解决办法是在http://mirrors.163.com/centos/6/os/x86_64/Packages中去查找该包的升级版本。把刚才的网址中对应版本的数字更改即可。

解压:

```
rpm -ivh python-iniparse-0.3.1-2.1.el6.noarch.rpm

rpm -ivh yum-metadata-parser-1.1.2-16.el6.x86_64.rpm

rpm -ivh yum-3.2.29-81.el6.centos.noarch.rpm yum-plugin-fastestmirror-1.1.30-41.el6.noarch.rpm --force --nodeps (这是两个包同时执行)
```




执行

```
yum list
```

可能会报错，解决方法：yum clean all 

然后在输入yum list就会有一大堆东西出来。说明安装完毕。



# 3. 更换yum源

https://opsx.alibaba.com/mirror

```
centos
CentOS
1、备份
mv /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.backup
2、下载新的CentOS-Base.repo 到/etc/yum.repos.d/
CentOS 5

wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-5.repo
或者

curl -o /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-5.repo
CentOS 6

wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-6.repo
或者

curl -o /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-6.repo
CentOS 7

wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo
或者

curl -o /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo
3、之后运行yum makecache生成缓存
```

