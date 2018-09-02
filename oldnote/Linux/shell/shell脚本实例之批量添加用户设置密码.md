---
title: shell脚本实例之批量添加用户设置密码
categories: shell   
toc: true  
tags: [shell]
---



# 1.使用shell批量添加用户设置密码
```
#!/bin/sh
 
for username in `echo oldboy{13..14}`;do
        useradd $username;
        pass=`echo $RANDOM|md5sum|cut -c 10-15`;
        echo "$pass"|passwd --stdin $username
 
        echo -e "${username} \t ${pass}" >>adduser.log
done

/*
说明:
1.$RANDOM是一个内置变量,用来生成一个随机数
2.md5sum是一个加密函数,生成一个加密字符串
3.cut -c 10-15    是取加密字符串的10-15位,作为密码
4.echo -e 表示转义
*/

```



* 方式2，在添加之前判断是否存在该用户，不存在添加

```Shell
#!/bin/bash

for I in {0..10};do
	# 这里是判断用户是否存在
	if id user$I &> /dev/null;then
		echo "user$I exists."
	else
		useradd user$I
		echo user$I | passwd --stdin user$I &> /dev/null
	fi
done
```



* 传参创建指定的用户名

```Shell
#!/bin/bash

for I in `echo $1 | sed "s/,/ /g" `;do
	# 这里是判断用户是否存在
	if id user$I &> /dev/null;then
		echo "user$I exists."
	else
		useradd user$I
		echo user$I | passwd --stdin user$I &> /dev/null
	fi
fone

# ./test.sh user1,user2,user3
```





# 2.使用chpasswd 批量创建密码

```
[root@lnmp02 shell]# useradd chenle
[root@lnmp02 shell]# useradd chenhao

[root@lnmp02 shell]# cat password_.txt       #格式一定要是：（用户名：密码），并且用户已经存在
chenle:chenle
chenhao:chenhao

[root@lnmp02 shell]# chpasswd <password_.txt

#检查
[root@lnmp02 shell]# tail -3 /etc/passwd
oldboy14:x:523:524::/home/oldboy14:/bin/bash
chenle:x:524:525::/home/chenle:/bin/bash
chenhao:x:525:526::/home/chenhao:/bin/bash
[root@lnmp02 shell]#

```

# 密码的规则要求
* 使用4种类别字符
* 足够长,大于7位
* 使用随机字符串
* 定期更换
* 循环周期要足够大 (不要使用最近使用的密码)


