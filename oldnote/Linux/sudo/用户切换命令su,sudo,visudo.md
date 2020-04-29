---
title: 用户切换命令su,sudo,visudo
categories: Linux   
toc: true  
tags: [linux]

---

[TOC]




# 1.用户切换命令 su

## 1.1.参数
|参数|详解|
|:-|-|
|-;-l;  --login|使一个shell成为登录的shell，如执行su - oldboy ,表示该用户想要改变身份为oldboy,并使用oldboy用户的环境变量，如：/home/oldboy/.bash_profile等|
|-c|pass a single command to the shell with -c :切换到一个shell下，执行一个命令，然后退出所切换的用户目录|


```

[root@lamp01 chenyansong]su - chenyansong -c "touch fileByRoot.txt"
[root@lamp01 chenyansong]ll
总用量 20

-rw-rw-r-- 1 chenyansong chenyansong    0 2月  13 21:22 fileByRoot.txt
# 可以看到创建的文件是以chenyansong为所属用户,所属组,因为是以chenyansong为用户创建的


```


## 1.2.优点

如果知道了root的密码，可以从普通用户转到root

## 1.3.缺点

普通用户切换到root之后，拥有root的权限，带来了很大的安全管理问题，例如：如果普通用户将root的密码改了


![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/sudo/sudo1.png)



# 2.sudo


## 2.1.语法
```
sudo cmd 

```

## 2.2.sudo的执行原理

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/sudo/sudo2.png)


## 2.3执行原理实操过程

### 2.3.1.visudo添加rm权限
viduso进入文件
```
## Allow root to run any commands anywhere
root    ALL=(ALL)       ALL
chenyansong    ALL=(ALL)       /bin/rm

#ALL一定是大写的,All还不行

```

### 2.3.2.使用sudo删除
```
[root@lamp01 ~]# su - zhangsan

#直接删除,权限不够
[zhangsan@lamp01 ~]$ rm -f /etc/oldboy.md5
rm: 无法删除"/etc/oldboy.md5": 权限不够

#使用sudo去删除
[zhangsan@lamp01 ~]$ sudo rm -f /etc/oldboy.md5
[sudo] password for zhangsan:
[zhangsan@lamp01 ~]$

#使用sudo去cp,但是在sudo中没有配cp执行权限
[zhangsan@lamp01 ~]$ sudo cp /etc/hosts /home/zhangsan/
Sorry, user zhangsan is not allowed to execute '/bin/cp /etc/hosts /home/zhangsan/' as root on lamp01.
[zhangsan@lamp01 ~]$

```

### 2.3.3检查时间戳文件
```
[root@lamp01 ~]# ll /var/db/sudo/zhangsan/
总用量 4
-rw------- 1 root zhangsan 28 2月  14 08:07 0

```

### 2.3.4.sudo 相关的参数

#### 2.3.4.1.列出当前用户所有可以使用的sudo命令


``` 
#sudo -l  列出用户在主机上可用和被禁止的命令
[root@lamp01 ~]# sudo -l
User root may run the following commands on this host:
    (ALL) ALL
 
[zhangsan@lamp01 ~]$ sudo -l
User zhangsan may run the following commands on this host:
    (ALL) /bin/rm
 

```


#### 2.3.4.2.删除密码时间戳

```
# sudo -k
#通-K,删除时间戳,下一个sudo就要求提供密码,前有NOPASSWD:参数,时间戳默认5分钟也会 失效
[zhangsan@lamp01 ~]$ sudo -K
[zhangsan@lamp01 ~]$ sudo -l
[sudo] password for zhangsan:        #提供密码
User zhangsan may run the following commands on this host:
    (ALL) /bin/rm
 
#删除时间戳,然后看时间戳文件是否存在
[zhangsan@lamp01 ~]$ sudo -K
[zhangsan@lamp01 ~]$ logout
[root@lamp01 ~]# ll /var/db/sudo/zhangsan/
总用量 0
```

#### 2.3.4.3.visudo -c 语法检查
```
[root@lamp01 ~]# visudo -c
/etc/sudoers: parsed OK
```

# 3.visudo



等同于：vim /etc/sudoers 能够检查编辑的文件是否有误

## 3.1.通过echo的方式修改vim /etc/sudoers
```
[root@lamp01 ~]# echo "chenyansong ALL=(ALL)   ALL">>/etc/sudoers
[root@lamp01 ~]# tail -1 /etc/sudoers
chenyansong ALL=(ALL)   ALL

```



## 3.3.修改vim /etc/sudoers文件的权限位777
```
[root@lamp01 ~]# ll /etc/sudoers   
-r--r----- 1 root root 4131 2月  14 08:24 /etc/sudoers

[root@lamp01 ~]# chmod 777 /etc/sudoers

[root@lamp01 ~]# su - zhangsan

#sudo的授权用户再去执行就会有问题
[zhangsan@lamp01 ~]$ sudo rm -f /etc/test.txt
sudo: /etc/sudoers is mode 0777, should be 0440
sudo: no valid sudoers sources found, quitting

#还是要切换回440
[root@lamp01 ~]# chmod 440 /etc/sudoers
```

## 3.4.viduso文件添加使用权限(语法格式)

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/sudo/sudo3.png)

```

#语法格式
who		which_hosts=(runas)		[NOPASSWD:] command
#who 谁
#which_hosts 指定可以连接进来的主机
#runas	 以谁的身份运行命令
#command  可以执行的命令
```



## 3.4.别名

别名必须全部而且只能使用大写英文字母的组合

```
#首先需要配置一些Alias，这样在下面配置权限时，会方便一些，不用写大段大段的配置。Alias主要分成4种
Host_Alias
Cmnd_Alias
User_Alias
Runas_Alias

#配置Host_Alias：就是主机的列表
Host_Alias      HOST_FLAG = hostname1, hostname2, hostname3

#配置Cmnd_Alias：就是允许执行的命令的列表，命令前加上!表示不能执行此命令.命令一定要使用绝对路径，避免其他目录的同名命令被执行，造成安全隐患 ,因此使用的时候也是使用绝对路径!
Cmnd_Alias      COMMAND_FLAG = command1, command2, command3 ，!command4

#配置User_Alias：就是具有sudo权限的用户的列表
User_Alias USER_FLAG = user1, user2, user3

#配置Runas_Alias：就是用户以什么身份执行（例如root，或者oracle）的列表
Runas_Alias RUNAS_FLAG = operator1, operator2, operator3

#总体的配置权限的格式如下：
USER_FLAG HOST_FLAG=(RUNAS_FLAG) COMMAND_FLAG
#如果不需要密码验证的话，则按照这样的格式来配置
USER_FLAG HOST_FLAG=(RUNAS_FLAG) NOPASSWD: COMMAND_FLAG
 

```

### 3.5.主机别名

* 主机名
* IP
* 网络地址
* 其他主机别名

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/sudo/sudo4.png)

### 3.6.用户别名(%组)

* 用户的用户名
* 组名，使用%引导
* 还可以使用其他已经存在的用户别名



![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/sudo/sudo5.png) 
![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/sudo/sudo6.png)


### 3.7.命令别名

* 命令路径（一般是可执行文件的路径）
* 目录（此目录内的所有的命令）
* 其他已经定义的命令别名

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/sudo/sudo7.png)

### 3.8.角色别名

* 用户名
* %组名
* 其他的runas别名

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/sudo/sudo8.png)



## 3.9.不需要输入密码

```
#说明NOPASSWD只对部分命令不需要输入密码
hadoop ALL=(root) NOPASSWD: /usr/sbin/useradd, PASSWD:/usr/sbin/usermod
```



## 3.10.不允许执行的命令

```
#不允许执行 “/usr/bin/passwd root“这个命令，即：不允许通过sudo修改root的密码
Cmnd_Alias USERADMINCMND = /usr/sbin/useradd, /usr/sbin/usermod,/usr/sbin/userdel,/usr/bin/passwd, !/usr/bin/passwd root




#执行命令后面必须加上字符(下面表示以：字符开头，后面跟任意字符)
/usr/bin/passwd [A-Za-z]*, !/usr/bin/passwd root
```





## 3.10.总结

&emsp;通过上面的别名的配置，我们在创建新用户的时候，让用户属于上面配置的用户别名组, 这样创建的用户就能够有上面对特定组配置的权限

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/sudo/sudo9.png)

注意：
* 授权规则中所有的ALL字符串必须为大写字母
* 一行内容超长可以用“\”斜线换行
* “！”表示非，就是命令取反的意思，即禁止执行的命令
* 放在后面的命令会覆盖前面的命令（如果他们有重叠的话）
```
下面的/sbin/* 和!/sbin/fdisk就是：拥有/sbin/*下除了/sbin/fdisk的权限
/usr/sbin/*,/sbin/*,!/usr/sbin/visudo,!/sbin/fdisk
```
* 命令的路径要全路径
* 如果不需要密码,应该加上NOPASSWD:参数 （即sudo之后不需要密码）
* 用户组前面必须加%号



# 4.sudo su -root


|命令|参数|
|-|-|
|sudo su -|该命令是通过sudo权限进行角色转换(默认是切换到root),执行命令对应账号的密码,非root密码|
|sudo su - oldboy|该命令是通过sudo以root权限,进行su - oldboy,因此输入的是执行命令当时账号的密码|

