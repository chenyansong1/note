---
title: mysql实战之表操作
categories: mysql   
tags: [mysql]
---





# 1、创建表
```
# 基本语法：
create table 表名(
    列名  类型  是否可以为空  默认值  自增  主键，
    列名  类型  是否可以为空
)ENGINE=InnoDB DEFAULT CHARSET=utf8
 
not null                # 不可以为空
default 1              # 默认值为1
auto_increment   # 自增
primary key         # 主键
constraint 外键名 foreign key (从表字段’自己‘) references 主表(主键字段)    # 外键

```

> 是否可空，null表示空，非字符串

```
not null    - 不可空
null          - 可空
```

> 默认值

```
#创建列时可以指定默认值，当插入数据时如果未主动设置，则自动添加默认值
            create table tb1(
                nid int not null defalut 2,
                num int not null
            )
```
> 自增

```
#如果为某列设置自增列，插入数据时无需设置此列，默认将自增（表中只能有一个自增列）
            create table tb1(
                nid int not null auto_increment primary key,
                num int null
            )
            或
            create table tb1(
                nid int not null auto_increment,
                num int null,
                index(nid)
            )

/*
注意：1、对于自增列，必须是索引（含主键）。
          2、对于自增可以设置步长和起始值
*/
show session variables like 'auto_inc%';
set session auto_increment_increment=2;
set session auto_increment_offset=10;


```

> 主键

```
#主键，一种特殊的唯一索引，不允许有空值，如果主键使用单个列，则它的值必须唯一，如果是多列，则其组合必须唯一。
create table tb1(
    nid int not null auto_increment primary key,
    num int null
)

#or
create table tb1(
    nid int not null,
    num int not null,
    primary key(nid,num)
)

```

> 外键

```
#一个特殊的索引，只能是指定内容
create table color(
    nid int not null primary key,
    name char(16) not null
)

create table fruit(
    nid int not null primary key,
    smt char(32) null,
    color_id int not null,
    constraint fk_cc foreign key (color_id) references color(nid)
)

```

# 2、删除表
```
drop table 表名
```


# 3、清空表
```
# 表还存在，表内容清空
delete from 表名
truncate table 表名
```


# 4、修改表
```
# 添加列：
        alter table 表名 add 列名 类型
# 删除列：
        alter table 表名 drop column 列名
# 修改列：
        alter table 表名 modify column 列名 类型;  -- 类型
        alter table 表名 change 原列名 新列名 类型; -- 列名，类型

# 添加主键：
        alter table 表名 add primary key(列名);
# 删除主键：
        alter table 表名 drop primary key;
        alter table 表名  modify  列名 int, drop primary key;

# 添加外键：
        alter table 从表 add constraint 外键名称（形如：FK_从表_主表） foreign key 从表(外键字段) references 主表(主键字段);
# 删除外键：
        alter table 表名 drop foreign key 外键名称

# 修改默认值：
      ALTER TABLE testalter_tbl ALTER i SET DEFAULT 1000;
# 删除默认值：
      ALTER TABLE testalter_tbl ALTER i DROP DEFAULT;

# 更改表名
         rename table 原表名 to 新表名;


```
> 增删改表的字段

```
#增加表字段，altertable法。
#1>    语法： altertable 表名 add 字段 类型 其他；
#2>    插入列，名为sex。
       alter table student add sex char(4)
#3>    插入名为suo列在name后面。
       alter table student add suo int(4) after name;
#4>    插入名为qq列在第一。
       alter table student add qq varchar(15) first;



#更改表名字，rename法。
#1>    语法: rename table 原表名 to 新表名；
#2>    更改oldsuo表为oldning。
       rename table oldsu to oldning


#删除表
#1>    语法：drop table <表名>；
#2>    删除表名为oldsuo表。
       drop table oldsu
```


