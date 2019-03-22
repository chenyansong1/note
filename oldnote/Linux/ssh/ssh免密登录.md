---
title: ssh免密的登录
categories: Linux   
toc: true  
tags: [Linux基础命令]
---

[TOC]

Linux：openSSH

	服务器端：sshd，配置文件/etc/ssh/ssd_config
	
	客户端：ssh,配置文件 /etc/ssh/ss_config
	
	客户端提供的工具：
	
		ssh-keygen：key generation,秘钥生成器
	
		ssh-copy-id：将公钥传输到远程服务器
	
		scp：远程copy



ssh-keygen -t rsa 生成公钥，私钥，如下

	~/.ssh/id_rsa
	
	~/.ssh/id_rsa.pub

公钥复制到远程主机某用户的家目录下的.ssh/authorized_keys（追加）文件或.ssh/authorized_keys2（追加）文件

![image-20181006112615938](/Users/chenyansong/Documents/note/images/linux/ssh/ssh-keygen.png)



一次性执行

```
ssh-keygen -t rsa -f .ssh/id-rsa-xxx -P ''

#-t 指定算法
#-f 指定文件路径
#-P 指定密码

ssh-keygen -t rsa -f .ssh/id-rsa-xxx -N ''
```





复制公钥到指定的主机的指定用户名下

```
ssh-copy-id -i ~/.ssh/id_rsa.pub root@ip

#-i [identity_file]	指定公钥路径

```



免密登录

```
#如果我们传过去的是/root/.ssh/id_dsa_for_install这个文件，那么ssh的时候，需要指定这个文件
ssh -i /root/.ssh/id_dsa_for_install 'root@10.130.10.60'

#默认情况下是~/.ssh/id_rsa.pub ，所以默认不用指定

```

