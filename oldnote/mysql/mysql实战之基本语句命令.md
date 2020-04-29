---
title: mysql实战之基本语句命令
categories: mysql   
tags: [mysql]
---




```

#1、单实例mysql启动

[root@localhost ~]# /etc/init.d/mysqld start
Starting MySQL                                             [确定]
#mysqld_safe –user=mysql &



#2、查看MySQL端口
[root@localhost ~]# ss -lntup|grep 3306
tcp  LISTEN 0 50  *:3306 *:* users:(("mysqld",19651,10))



#3、查看MySQL进程
[root@localhost ~]# ps -ef|grep mysql|grep -v grep
root     19543     1  0 Oct10 ?        00:00:00 /bin/sh /usr/local/mysql/bin/mysqld_safe --datadir=/usr/local/mysql/data --pid-file=/usr/local/mysql/data/localhost.localdomain.pid
mysql    19651 19543  0 Oct10 ?        00:05:04 /usr/local/mysql/libexec/mysqld --basedir=/usr/local/mysql --datadir=/usr/local/mysql/data --user=mysql --log-error=/usr/local/mysql/data/localhost.localdomain.err --pid-file=/usr/local/mysql/data/localhost.localdomain.pid --socket=/tmp/mysql.sock --port=3306



#4、MySQL启动原理
/etc/init.d/mysqld 是一个shell启动脚本，启动后最终会调用mysqld_safe脚本，最后调用mysqld服务启动mysql(是一个二进制文件)。
        "$manager" \
        --mysqld-safe-compatible \
        --user="$user" \
        --pid-file="$pid_file" >/dev/null 2>&1 &



#5、关闭数据库
[root@localhost ~]# /etc/init.d/mysqld stop
Shutting down MySQL....                                    [确定]



#6、查看mysql数据库里操作命令历史
cat /root/.mysql_history


#7、强制linux不记录敏感历史命令
HISTCONTROL=ignorespace



# 8、MySQL设置密码
/usr/local/mysql/bin/mysqladmin -u root password 'oldsuo'


#9、MySQL修改密码，与多实例指定sock修改密码
mysqladmin -uroot -passwd password 'oldsuo'
mysqladmin -uroot -passwd password 'oldsuo' -S /data/3306/mysql.sock


#登陆mysql数据库
mysql -uroot –p
 
#查看有哪些库
show databases;
#删除test库
drop database test;
 
#使用test库
use test;
#查看有哪些表
show tables;
#查看suoning表的所有内容
select * from suoning;
 
#查看当前版本
select version();
#查看当前用户
select user();
#查看用户和主机列，从mysql.user里查看
select user,host from mysql.user;
#删除前为空，后为localhost的库
drop user ""@localhost；
 
#刷新权限
flush privileges;
 
#跳出数据库执行命令
system ls;


```

