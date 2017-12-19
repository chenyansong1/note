---
title: mysql数据库安装之后的优化操作
categories: mysql   
tags: [mysql]
---



# 1.删除不必要的用户和库

```

#查看用户和主机列，从mysql.user里查看
select user,host from mysql.user;
 
#删除用户名为空的用户，并检查
delete from mysql.user where user='';
select user,host from mysql.user;
 
#删除主机名为localhost.localdomain的库，并检查
delete from mysql.user where host='localhost.localdomain';
select user,host from mysql.user;
 
#删除主机名为::1的库，并检查。::1库的作用为IPV6
delete from mysql.user where host='::1';
 
#删除test库
drop database test;


```

# 2.添加额外管理员

```
# 添加额外管理员，system作为管理员，oldsuo为密码
mysql> delete from mysql.user;
Query OK, 2 rows affected (0.00 sec)
mysql> grant all privileges on *.* to system@'localhost' identified by 'oldsuo' with grant option;
Query OK, 0 rows affected (0.00 sec)
 
# 刷新MySQL的系统权限相关表，使配置生效
mysql> flush privileges;
Query OK, 0 rows affected (0.00 sec)
mysql> select user,host from mysql.user;
+--------+-----------+
| user   | host      |
+--------+-----------+
| system | localhost |
+--------+-----------+
1 row in set (0.00 sec)
mysql>


```

# 3.设置登录密码并开机自启
```
#设置密码，并登陆
/usr/local/mysql/bin/mysqladmin -u root password 'oldsuo'
mysql -usystem -p
 
#开机启动mysqld，并检查
chkconfig mysqld on
chkconfig --list mysqld


```





