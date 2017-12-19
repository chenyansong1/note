---
title: mysql实战之数据库操作
categories: mysql   
tags: [mysql]
---



# 1、查看数据库
```
SHOW DATABASES;
 
# 默认数据库：
  mysql - 用户权限相关数据
  test - 用于用户测试数据
  information_schema - MySQL本身架构相关数据

```


# 2、创建数据库
```
# utf-8 编码
CREATE DATABASE 数据库名称 DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
 
# gbk 编码
CREATE DATABASE 数据库名称 DEFAULT CHARACTER SET gbk COLLATE gbk_chinese_ci;

```


# 3、使用数据库
```
USE db_name;
 
# 可以不使用分号
```



