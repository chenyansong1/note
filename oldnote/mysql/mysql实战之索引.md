---
title: mysql实战之索引
categories: mysql   
tags: [mysql]
---



# 1、索引概述
* 索引是表的索引目录，在查找内容之前先查目录中查找索引位置，从而快速定位查询数据；
* 可以理解成新华字典中的索引；
* 索引会保存在额外的文件中。


# 2、索引种类

* 普通索引：仅加速查询
* 唯一索引：加速查询 + 列值唯一（可以有null）
* 主键索引：加速查询 + 列值唯一 +　表中只有一个（不可以有null）
* 组合索引：多列值组成一个索引，专门用于组合搜索，其效率大于索引合并
* 全文索引：对文本的内容进行分词，进行搜索
* 索引合并：使用多个单列索引组合查询搜索
* 覆盖索引：select的数据列只用从索引中就能够取得，不必读取数据行，换句话说查询列要被所建的索引覆盖


## a、普通索引
```
# 创建表 + 索引
create table in1{
    nid int not null auto_increament primary key,
    name varchar(32) not null,
    email varchar(64) not null,
    extra text,
    index ix_name (name)
}


# 创建索引
create index index_name on table_name(column_name)

# 删除索引
drop index_name on table_name;
 
# 查看索引
show index from table_name;

#注意：对于创建索引时如果是BLOB 和 TEXT 类型，必须指定length。
create index ix_extra on in1(extra(32));

```


## b、唯一索引
```
# 创建表 + 唯一索引
create table in1{
    nid int not null auto_increament primary key,
    name varchar(32) not null,
    email varchar(64) not null,
    extra text,
    unique ix_name (name)
}

# 创建唯一索引
create unique index 索引名 on 表名(列名)
 
# 删除唯一索引
drop unique index 索引名 on 表名



```


## c、主键索引
```
# 创建表 + 创建主键
create table in1{
    nid int not null auto_increament primary key,
    name varchar(32) not null,
    email varchar(64) not null,
    extra text,
    index ix_name (name)
}

#or

create table in1{
    nid int not null auto_increament ,
    name varchar(32) not null,
    email varchar(64) not null,
    extra text,
    primary key(nid),
    index ix_name (name)
}

# 创建主键
alter table 表名 add primary key(列名);
 
# 删除主键
alter table 表名 drop primary key;
alter table 表名  modify  列名 int, drop primary key;

```


## d、组合索引
```
/*
组合索引是多个列组合成一个索引来查询
应用场景：频繁的同时使用多列来进行查询，如：where name = 'nick' and age = 18。
*/

# 创建表
create table mess{
    nid int not null auto_increment primary key,
    name varchar(32) not null,
    age int not null
}

# 创建组合索引
create index ix_name_age on mess(name,age);

/*
如上创建组合索引之后，查询一定要注意：
 
name and email  -- >使用索引，name一定要放前面
name         -- >使用索引
email         -- >不使用索引
 
注意：同时搜索多个条件时，组合索引的性能效率好过于多个单一索引合并。
*/
```

# 3、相关命令
```
# 查看索引
    show index from  表名
 
# 查看执行时间
    set profiling = 1;  # 开启profiling
    SQL...              # 执行SQL语句
    show profiles;      # 查看结果

```

# 4、如何正确使用索引
```
# like '%xx'，避免%_写在开头
    select * from tb1 where name like '%n';
 
# 使用函数
    select * from tb1 where reverse(name) = 'nick';
 
# or
    select * from tb1 where nid = 1 or email = '630571017@qq.com';
    注：当or条件中有未建立索引的列才失效，否则会走索引
 
# 类型不一致
    如果列是字符串类型，传入条件是必须用引号引起来。
    select * from tb1 where name = 999;
 
# !=，不等于
    select * from tb1 where name != 'nick'
    注：如果是主键，则还是会走索引
        select * from tb1 where nid != 123
 
# >，大于
    select * from tb1 where name > 'nick'
    注：如果是主键或索引是整数类型，则还是会走索引
        select * from tb1 where nid > 123
        select * from tb1 where num > 123
 
# order by
    select email from tb1 order by name desc;
    当根据索引排序时候，选择的映射如果不是索引，则不走索引
    注：如果对主键排序，则还是走索引：
        select * from tb1 order by nid desc;
 
# 组合索引最左前缀
    如果组合索引为：(name,email)，查询使用：
    name and email       -- 使用索引
    name                 -- 使用索引
    email                -- 不使用索引

```

# 5、注意事项
```
# 避免使用select *
# count(1)或count(列) 代替 count(*)
# 创建表时尽量时 char 代替 varchar
# 表的字段顺序固定长度的字段优先
# 组合索引代替多个单列索引（经常使用多个条件查询时）
# 尽量使用短索引
# 使用连接（JOIN）来代替子查询(Sub-Queries)
# 连表时注意条件类型需一致
# 索引散列值（重复少）不适合建索引，例：性别不适合

# 索引会加快查询速度，但是也会影响更新的速度，因为更新要维护索引数据。
# 索引列并不是越多越好，要在频繁查询的where后的条件列上创建索引
# 小表或重复值很多的列可以不建索引，要在大表以及重复值少的条件列上创建索引
# 多个列联合索引有前缀生效特性
# 当字段内容前n个字符已经接近唯一时，可以对字段的前n个字符创建索引

```


# 6、执行计划
```
mysql> select *from student;
+------+-----------+
| id   | name      |
+------+-----------+
|    1 | zhangsan  |
|    2 | zhangsan2 |
|    3 | zhangsan3 |
|    4 | zhangsan4 |
+------+-----------+
4 rows in set (0.00 sec)
 
mysql> explain select *from student;
+----+-------------+---------+------+---------------+------+---------+------+------+-------+
| id | select_type | table   | type | possible_keys | key  | key_len | ref  | rows | Extra |
+----+-------------+---------+------+---------------+------+---------+------+------+-------+
|  1 | SIMPLE      | student | ALL  | NULL          | NULL | NULL    | NULL |    4 |       |
+----+-------------+---------+------+---------------+------+---------+------+------+-------+
1 row in set (0.00 sec)
 

#有子查询
mysql> explain select * from (select id,name from student where id<4) as B;            
+----+-------------+------------+------+---------------+------+---------+------+------+-------------+
| id | select_type | table      | type | possible_keys | key  | key_len | ref  | rows | Extra       |
+----+-------------+------------+------+---------------+------+---------+------+------+-------------+
|  1 | PRIMARY     | <derived2> | ALL  | NULL          | NULL | NULL    | NULL |    3 |             |
|  2 | DERIVED     | student    | ALL  | NULL          | NULL | NULL    | NULL |    4 | Using where |
+----+-------------+------------+------+---------------+------+---------+------+------+-------------+
2 rows in set (0.08 sec)


```


![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/index/1.jpg)
![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/index/2.jpg)
![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/index/3.jpg)
![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/index/4.jpg)





[整理自](http://dbaplus.cn/news-11-760-1.html)
 



