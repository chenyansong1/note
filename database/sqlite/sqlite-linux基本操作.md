[toc]

转自：https://www.cnblogs.com/electronic/p/11000443.html

```shell
#创建或者连接到某个数据库student.db
sqlite3 student.db



#数据库命令是以 【.】  开头的；数据库语句是以【；】结尾的

1.schema 表名    显示表结构　如：【 .schema student 】
2.【 .tables 】  显示表
3.【 .quit 】或 【 .exit 】 退出数据库控制界面
　　
　　
# 创建一个数据表：student 
【 create table student (id int primary key,name char,age int,sex char); 】　　


#向表中插入数据  insert into 表名 values (值1，值2，值3，值4)； 如：
【 insert into student values (0,'zhang0',20,'m'); 】 没有返回错误信息则插入成功


#查找语句 select *from 表名；

查找表中所有内容显示 【 select *from student; 】
查找某个字段（列信息）【 select id from student; 】
按照某个条件查找 【 select * from student where age>25 and sex='m' 】 { 条件 and  or 表示 与 ，或 }





```

