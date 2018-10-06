---
title: Linux基础命令之ssh远程登录优化
categories: Linux   
toc: true  
tags: [Linux基础命令]
---

[TOC]

Linux：openSSH

​	服务器端：sshd，配置文件/etc/ssh/ssd_config

​	客户端：ssh,配置文件 /etc/ssh/ss_config

​	客户端提供的工具：

​		ssh-keygen：key generation,秘钥生成器

​		ssh-copy-id：将公钥传输到远程服务器

​		scp：远程copy



ssh-keygen -t rsa

​	~/.ssh/id_rsa

​	~/.ssh/id_rsa.pub

公钥复制到远程主机某用户的家目录下的.ssh/authorized_keys（追加）文件或.ssh/authorized_keys2（追加）文件

![image-20181006112615938](/Users/chenyansong/Documents/note/images/linux/ssh/ssh-keygen.png)



一次性执行

```
ssh-keygen -t rsa -f .ssh/id-rsa-xxx -P ''
```





复制

ssh-copy-id -i ~/.ssh/id_rsa.pub root@ip



/etc/ssh/sshd_config文件中是配置ssh登陆的

下面是优化的步骤:

# 1.优化的配置
## 1.1.修改默认的端口(22)
```
#Port 22    #改成你自己指定的端口
```

## 1.2修改使用DNS 为no
&emsp;不使用DNS的原因是，我们登陆的时候就是使用的是ip，不需要去解析了，所以DNS解析就用不到了，不使用DNS我们的访问将更快
```
#UseDNS yes
UseDNS no
```


## 1.3.修改登录时监听的IP
&emsp;我们将登陆的IP换成我们指定的内网IP，那样外网的ip地址就不能直接访问了，避免了不必要的攻击，那么外面的人如何进行登录呢，我们可以使用VPN，登录了VPN之后，就相当于用户变成了内网用户，我们可以使用指定的IP去登录了
```
#ListenAddress 0.0.0.0
ListenAddress 192.168.36.129
```

## 1.4.不允许root登录
&emsp;我们不允许root进行登录，如果要使用root的功能，可以使用其他用户使用su去切换登录，但是不允许直接连接登录
```
#PermitRootLogin yes
PermitRootLogin no
```

## 1.5.GSSAPIAuthentication 解决SSH远程连接服务慢的问题
```
#GSSAPIAuthentication no    #如果默认是no,那么默认就行了
```

# 2.修改上面提到的配置文件
&emsp;手动修改文件（一个一个的该，最后将他们放在一起，并且注释，标明时间）
```
Port 52113
PermitRootLogin no
PermitEmptyPasswords no
UseDNS no
GSSAPIAuthentication no 
```

&emsp;通过sed的方式修改文件

&emsp;方式一：
``` shell
 
#增加：
[root@oldboy ssh]# sed -i '13 iPort 52113\nPermitRootLogin no\nPermitEmptyPasswords no\nUseDNS no\nGSSAPIAuthentication no'  sshd_config

#查看：
 
[root@oldboy ssh]# sed -n '13,17p' sshd_config
Port 52113
PermitRootLogin no
PermitEmptyPasswords no
UseDNS no
GSSAPIAuthentication no

```


&emsp;方式二（推荐）：他有个好的习惯就是在修改文件之前，将文件备份（并加上时间）
``` shell 
#下面是一个执行脚本
echo "#--------sshConfig修改ssh默认登录端口,禁止root登录----------------------------#"
\cp /etc/ssh/sshd_config /etc/ssh/sshd_config.$(date +"%F"-$RANDOM)
sed -i 's%#Port 22%Port 52113%' /etc/ssh/sshd_config
sed -i 's%#PermitRootLogin yes%PermitRootLogin no%' /etc/ssh/sshd_config
sed -i 's%#PermitEmptyPasswords no%PermitEmptyPasswords no%' /etc/ssh/sshd_config
sed -i 's%#UseDNS yes%UseDNS no%' /etc/ssh/sshd_config
sed -i 's%GSSAPIAuthentication yes%GSSAPIAuthentication no%' /etc/ssh/sshd_config
egrep "UseDNS|52113|RootLogin|EmptyPass|GSSAPIAuthentication" /etc/ssh/sshd_config

```



# 3.重新加载配置到内存中
&emsp;因为刚刚修改的数据只是在硬盘中，需要重新加载到内存中
```
[root@lamp01 chenyansong]# /etc/init.d/sshd restart
停止 sshd：                                                [确定]
正在启动 sshd：                                            [确定]

#或者

[root@lamp01 chenyansong]# /etc/init.d/sshd reload
重新载入 sshd：                                            [确定]
[root@lamp01 chenyansong]# 
```


