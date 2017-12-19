---
title: mysql实战之表内容操作
categories: mysql   
tags: [mysql]
---



# 1、增
```
# 语法：insert into 表 (列名,列名...) values (值,值,值...)

# 插入单条数据
        insert into 表 (列名,列名...) values (值,值,值...)
# 插入多条数据
       insert into 表 (列名,列名...) values (值,值,值...),(值,值,值...)
# 插入另一条语句的查询结果
        insert into 表 (列名,列名...) select 列名,列名... from 表
```


# 2、删
```
# 语法：delete from 表
delete from 表;
delete from 表 where id＝1;

```


# 3、改
```
# 语法：update 表 set name ＝ 'nick' where id>1
update 表 set name ＝ 'nick' where id>1
```



# 4、查
```
# 语法：select * from 表
select * from 表
select * from 表 where id > 1
select nid,name,gender as gg from 表 where id > 1
# as 做别名

```



# 5、条件
```
# 语法：select * from 表 where id > 1

# id在5到16之间
   select * from 表 where id between 5 and 16
# 多个条件
    select * from 表 where id>1 and name != 'nick' and num = 12
# id在元组中
    select * from 表 where id in(11, 22, 33)
# id不在元组中
    select * from 表 where id not in (11, 22, 33)
# id在查询结果中
    select * from 表 where id in (select nid from 表);

```

# 6、通配符
```
# 语法：select * from 表 where name like '_n%'
# ni开头的所有(多个字符串)
   select * from 表 where name like "ni%";
#s开头的所有(一个字符)
    select * from where name like "s_";

```

# 7、限制

```
# 语法：select * from 表 limit 9,5;
# 前5行
    select * from 表 limit 5;
# 从第9行开始的5行
    select * from 表 limit 9,5;
# 从第9行开始的5行
    select * from 表 limit 5 offset 9;
```

# 8、排序

```
# 语法：select * from 表 order by 列1 desc,列2 asc
# 根据"列"从小到大排列
   select * from 表 order by 列 asc;
# 根据"列"从大到小排列
   select * from 表 order by 列 desc;
# 根据"列1"从大到小排列,如果相同则按"列2"从小到大排序
   select * from 表 order by 列1 desc , 列2 asc;


```

# 9、分组

```
# 语法：select num from 表 group by num
# 根据num分组
    select num from 表 group by num;
# 根据num和nid分组
    select num, nid  from 表 group by num,nid;
# 内置函数
    select num, nid  from 表 where nid>10 group by num,nid order nid desc;
# 取分组后id大于10的组
    select num  from 表 group by num having max(id) > 10

/*
 注：group by 必须在where之后，order by之前
count(*) , count(1)    #表示个数
sum(score)        #表示和
max(score)    #最大数
min(score)    #最小数
having    #分组之后用having
*/
```

# 10、连表

```
# 语法：inner join . on、left join . on、right join . on
# 内连接
    select A.num,    A.name, B.name from A, B where A.id = B.id
    select A.num,    A.name, B.name from A inner join  B on A.id = B.id
# 左连接(A 表所有显示,如果B中无对应关系,则值为null)
    select A.num,    A.name, B.name from A left join  B on A.id = B.id
# 右连接(B 表所有显示,如果B中无对应关系,则值为null
    select A.num,    A.name, B.name from A right join  B on A.id = B.id

```


# 11、组合

```
# 语法：union、union all
select nickname from A
union
select name from B

#组合,不处理重合
select nickname from A
union all
select name from B

```


# 12.表中插入数据

```
# 1>    插入单个数据，student为表的名称
   insert into student(id, name) values (1, 'nick')

# ２>    批量插入数据，student为表的名称。
   insert into student(id, name) values (1, 'nick'), (3, 'kangkan'), (4,'kadd')

```


# 13.表中删除数据

```
# 1>    删除所有数据，student为表的名称
   delete from student;
# 2>    删除表中的某行或某些
   delete from student where id=4
# 3>    直接清空某张表
    truncate table student;

```


# 14.查看建表语句

```
show create table 表名 \G
```

# 15.查看表结构
```
desc 表名
```

# 16.查看是否建索引
```
mysql> explain select * from student where name="student" \G;
*************************** 1. row ***************************
           id: 1
  select_type: SIMPLE
        table:student
         type: ref        #有索引
possible_keys:index_name
          key: index_name      #表示有
      key_len: 20
          ref: const
         rows: 1        #检索了几行
        Extra: Using where
1 row in set (0.07 sec)

```



[整理自](http://dbaplus.cn/news-11-760-1.html)


