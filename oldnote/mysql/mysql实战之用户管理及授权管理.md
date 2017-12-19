---
title: mysql实战之用户管理及授权管理
categories: mysql   
tags: [mysql]
---





# 1、用户管理
```
# 创建用户
create user '用户名'@'IP地址' identified by '密码';
# 删除用户
drop user '用户名'@'IP地址';
# 修改用户
rename user '用户名'@'IP地址'; to '新用户名'@'IP地址';;
# 修改密码
set password for '用户名'@'IP地址' = Password('新密码')

#PS：用户权限相关数据保存在mysql数据库的user表中，所以也可以直接对其进行操作（不建议）

# 查看当前用户
select user();
# 查看所有用户
select host,user from mysql.user;
# 人性化显示所有用户
SELECT DISTINCT CONCAT('User: ''',user,'''@''',host,''';') AS query FROM mysql.user;
# 查看用户的所有权限
show grants for 'nick'@'%';

```

# 2、授权管理
```
# 查看权限
    show grants for '用户'@'IP地址'
# 授权
    grant  权限 on 数据库.表 to   '用户'@'IP地址'
# 取消权限
    revoke 权限 on 数据库.表 from '用户'@'IP地址'

```
常用权限：
* all privileges   除grant外的所有权限
* select              仅查权限
* select,insert    查和插入权限
* usage              无访问权限

对于目标数据库以及内部其他：

* 数据库名.*        #数据库中的所有
* 数据库名.表        #指定数据库中的某张表
* 数据库名.存储过程        #指定数据库中的存储过程
* \*.\*        #所有数据库中的所有表


对于用户和IP：
* 用户名@IP地址        #用户只能在该IP下才能访问
* 用户名@192.168.0.1.%        #用户只能在该IP段下才能访问(通配符%表示任意)
* 用户名@%        #用户可以在任意IP下访问(默认IP地址位%)

更多权限

![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/privilege/1.jpg)


添加额外管理员
![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/privilege/2.jpg)
 
简单示例
![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/privilege/3.jpg)

创建用户一般流程
![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/privilege/4.png)

 
 


# 6、授权局域网内主机远程连接数据库
```
#百分号匹配法
    grant all on *.* to 'test'@'192.168.200.%' identified by 'test123';
#子网掩码配置法
    grant all on *.* to 'test'@'192.168.200.0/255.255.255.0' identified by 'test123';
#刷新权限
    flush privileges;
#远程登陆连接
    mysql -utest -ptest123 -h 192.168.200.96
```

[整理自](http://dbaplus.cn/news-11-760-1.html)

