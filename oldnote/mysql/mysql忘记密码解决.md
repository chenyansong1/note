---
title: mysql忘记密码解决
categories: mysql   
tags: [mysql]
---



mysql忘记密码怎么办?

```
# 1> 普通方式
service mysqld stop
mysqld_safe --skip-grant-tables &
输入 mysql -uroot -p 回车进入
>use mysql;
> update user set password=PASSWORD("newpass")where user="root";
更改密码为 newpassord
> flush privileges; 更新权限
> quit 退出
service mysqld restart
mysql -uroot -p新密码进入
 


# 2> 普通方式的简写
service mysqld stop
mysqld_safe --skip-grant-tables --user=mysql &
mysql
update mysql.user set password=PASSWORD("newpass")where user="root" and host='localhost';
flush privileges;
mysqladmin -uroot -pnewpass shutdown
/etc/init.d/mysqld start
mysql -uroot -pnewpass        #登陆
 


# 3>多实例方式
killall mysqld
mysqld_safe –defaults-file=/data/3306/my.cnf –skip-grant-table &
mysql –u root –p –S /data/3306/mysql.sock        #指定sock登陆
update mysql.user set password=PASSWORD("newpass")where user="root";
flush privileges;
mysqladmin -uroot -pnewpass shutdown
/etc/init.d/mysqld start
mysql -uroot -pnewpass        #登陆



```



