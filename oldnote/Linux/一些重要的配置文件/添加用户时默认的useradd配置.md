---
title: Linux一些重要的配置文件之添加用户时默认的useradd配置
categories: Linux   
toc: true  
tags: [Linux重要配置文件]
---


涉及到的文件有如下:
* /etc/default/useradd
* /etc/login.defs
* /etc/skel

> /etc/default/useradd

```

[root@lamp01 application]# cat /etc/default/useradd
# useradd defaults file
GROUP=100        #依赖于/etc/login.defs 的USERGROUPS_ENAB参数,如果为no,则此处控制
HOME=/home    #把用户的家目录建在/home中
INACTIVE=-1    #是否启用账号过期停权,-1表示不启用
EXPIRE=        #账号终止日期,不设置表示不启用
SHELL=/bin/bash    #新用户默认所用的shell类型
SKEL=/etc/skel        #配置新用户家目录的默认文件存放路径,/etc/skell就是配置在这里,当我们用useradd添加用户时,用户目录下的文件,都是从这里配置的目录中复制过去的
CREATE_MAIL_SPOOL=yes    #创建mail文件
```

>  /etc/login.defs

```
[root@lamp01 application]# cat /etc/login.defs |grep -vE "^#"
 
MAIL_DIR        /var/spool/mail

#对密码的定义
PASS_MAX_DAYS   99999    #一个秘密最长可以使用的天数
PASS_MIN_DAYS   0    #更换密码的最小天数
PASS_MIN_LEN    5    #密码的最小长度
PASS_WARN_AGE   7    #密码失效前多少天开始警告

#对UID,GID的定义 
UID_MIN                   500    #最小UID为500, 也就是说添加用户时,UID是从500开始的
UID_MAX                 60000    #最大的UID为60000
 
GID_MIN                   500    
GID_MAX                 60000
 
#创建用户的时候，是否创建家目录
CREATE_HOME     yes
 
#对umask的定义
UMASK           077    
#所以产生用户的家目录的权限就是:777-077 = 700
[root@lamp01 home]# pwd
/home
[root@lamp01 home]# ll
总用量 48
drwx------. 3 chenyansong  chenyansong  4096 2月  11 18:18 chenyansong
drwx------  2 chenyansong2 chenyansong2 4096 7月  20 2016 chenyansong2
drwx------  2 lisi         lisi         4096 7月  18 2016 lisi
#注意是:用户的家目录的权限,家目录里面的文件或者是目录是644



#删除用户，是否同时删除对应的组
USERGROUPS_ENAB yes
 
#加密方式
ENCRYPT_METHOD SHA512 
```

> /etc/skel

&emsp;/etc/skel目录是用来存放新用户配置文件的目录，当我们添加新用户的时候，这个目录下的所有文件会自动被复制到新添加的用户的家目录下；默认情况下，/etc/skel目录下的所有文件都是隐藏文件（以.点开头的文件）通过修改、添加、删除/etc/skel目录下的文件，我们可以为新创建的用户提供统一的、标准的、初始化用户环境。
 
&emsp;例如：如果我们在/etc/skel/下创建了一个readme.txt文件，那么我们再创建用户的时候，在用户的家目录下就会存在一个
叫做readme.txt的文件。


```
[root@lamp01 skel]# pwd
/etc/skel
[root@lamp01 skel]# ll -a
总用量 20
drwxr-xr-x.  2 root root 4096 7月   3 2016 .
drwxr-xr-x. 87 root root 4096 2月  13 00:06 ..
-rw-r--r--.  1 root root   18 5月  11 2012 .bash_logout
-rw-r--r--.  1 root root  176 5月  11 2012 .bash_profile
-rw-r--r--.  1 root root  124 5月  11 2012 .bashrc

/*
为什么useradd的时候就自动在用户家目录下创建skel目录中的文件？
参见：/etc/default/useradd 下的配置项SKEL
*/

```

> 案例

```
#请问如下的登录环境故障的原理及解决办法?
-bash-4.1$
-bash-4.1$

#显示不正常，解决的办法：将/etc/skel/.bash*的文件拷贝到用户的家目录下
```



