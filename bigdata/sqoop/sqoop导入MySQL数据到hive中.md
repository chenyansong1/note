sqoop导入MySQL数据到hive中操作步骤，以下步骤是以全量导入为背景，不考虑增量的情况

执行步骤如下：
* 1.安装sqoop
* 2.切换用户到aphadoop
* 3.导入环境变量
```
export PATH=$PATH:/home/chenyansong/soft_install/sqoop-1.4.6/bin
```
* 4.执行脚本insert_mysql_data_to_hive.sh，需要修改脚本中对应的参数：
	* 4.1.指定机器的IP
	* 4.2.数据库名
	* 4.3.表名
	* 4.4.用户名
	* 4.5.密码
	* 4.6.hive的库名
	* 4.7.hive的表名



**下面是具体的实施步骤**
----


# 1.安装sqoop

[sqoop介绍及安装](https://chenyansong.site/2017/04/16/bigdata/sqoop/sqoop%E4%BB%8B%E7%BB%8D%E5%8F%8A%E5%AE%89%E8%A3%85/)

**注意**
* 下载sqoop可以到官网，或者[sqoop-1.4.6.bin__hadoop-2.0.4-alpha.tar.gz](https://github.com/chenyansong1/company/blob/master/sqoop%E5%AF%BC%E5%85%A5MySQL/sqoop-1.4.6.bin__hadoop-2.0.4-alpha.tar.gz)


* 运行sqoop命令需要aphadoop的权限

```
sudo su - aphadoop
```

* MySQL中需要导出的表要有主键


# 2.sqoop配置参数说明

|参数|含义|
|---|----|
|import|表示导入数据|
|--connect jdbc:mysql://192.168.1.204/test|连接MySQL的test数据库|
|--driver com.mysql.jdbc.Driver|指定驱动|
|--username xxx|指定MySQL库的用户名|
|--password ppp|指定MySQL库的密码|
|--table test_for_sqoop|指定导出MySQL的哪张表|
|--fields-terminated-by "\t"|指定hive的字段分隔符|
|--lines-terminated-by "\n"|指定hive的行分隔符|
|--hive-import|表示导入到hive中|
|--create-hive-table|可以通过 --create-hive-table 创建表，如果表已经存在则会执行失败|
|--hive-table test.test_sqoop_by_cys|hive的表名|
|--null-string '\\N'|&emsp;|
|--null-non-string	'\\N'| &emsp;|
|--delete-target-dir|&emsp; |

Sqoop 默认地导入空值（NULL）为 "null" 字符串，而 hive 使用 \N 去标识空值（NULL），故你在 import 或者 export 时候，需要做相应的处理。在 import 时，使用如下命令：
```
$ sqoop import  ... --null-string '\\N' --null-non-string '\\N'
```

执行下面的命令会将 mysql 中的数据导入到 hdfs 中（默认在hdfs的home目录下以表名创建一个目录），然后创建一个hive 表，最后再将 hdfs 上的文件移动到 hive 表的目录下面,如果使用了--delete-target-dir，那么会将hdfs中的对应的表名删除。


# 3.注意事项


* 导入到hive中时分隔符的问题，可以指定下面的参数

```
--fields-terminated-by "\t" --lines-terminated-by "\n"
```


* 空字段的问题



```
mysql> desc test_for_sqoop;
+-------+--------------+------+-----+---------+-------+
| Field | Type         | Null | Key | Default | Extra |
+-------+--------------+------+-----+---------+-------+
| id    | int(11)      | NO   | PRI | NULL    |       |
| name  | varchar(255) | YES  |     | NULL    |       |
| age   | int(11)      | YES  |     | NULL    |       |
+-------+--------------+------+-----+---------+-------+

#mysql中表数据如下
mysql> select *from test_for_sqoop;
+----+----------+------+
| id | name     | age  |
+----+----------+------+
|  1 | zhangsan |   44 |
|  2 | lisi     |   44 |
|  3 | wangwu   | NULL |
|  4 | NULL     | NULL |
+----+----------+------+
4 rows in set (0.00 sec)

#如果在hive中没有指定空字段的处理（即没有配置--null-string '\\N'，--null-non-string '\\N'），会出现下面的结果
hive> select *from test.test_for_sqoop;
1       zhangsan        44
2       lisi    44
3       wangwu  NULL
4       null    NULL

#会发现id=4的name字段在hive中为字符"null",下面的查询结果可以说明
hive> select *from test.test_for_sqoop where name is not null;
1       zhangsan        44
2       lisi    44
3       wangwu  NULL
4       null    NULL

hive> select *from test.test_for_sqoop where age is not NULL;
1       zhangsan        44
2       lisi    44

hive> select *from test.test_for_sqoop where name is not NULL;
1       zhangsan        44
2       lisi    44
3       wangwu  NULL
4       null    NULL

#这里足够说明是字符串"null"
hive> select *from test.test_for_sqoop where name='null';
4       null    NULL

hive> select *from test.test_for_sqoop where age is null;
3       wangwu  NULL
4       null    NULL

```

如果没有指定null字段的处理，那么会将null的字符串在hive中转成'null',mysql中的int类型转成NULL

如果指定了空字段的处理，需要在配置文件中添加如下的参数
```
--null-string
'\\N'
--null-non-string
'\\N'
```

hive中插叙的结果如下
```
hive> select *from test.test_for_sqoop;
1       zhangsan        44
2       lisi    44
3       wangwu  NULL
4       NULL    NULL


hive> select *from test.test_for_sqoop where age='NULL';


hive> select *from test.test_for_sqoop where age is null;
3       wangwu  NULL
4       NULL    NULL


hive> select *from test.test_for_sqoop where name is null;
4       NULL    NULL


hive> select *from test.test_for_sqoop where name is not null;
1       zhangsan        44
2       lisi    44
3       wangwu  NULL

hive> select *from test.test_for_sqoop where age is not null;
1       zhangsan        44
2       lisi    44

```

* 增量导入的问题




写成一个脚本，需要传入的参数有：
1.配置文件（其中包含连接MySQL的地址，用户名，密码，表名，查询的sql【如果不指定，那么导入全部的字段】，在hive中的表名，hive的字段分隔【有默认值】，行分隔【有默认值】，如果要增量导入，那么需要指定增量的标识）


* 将查询结果导入




2.压缩的问题

3.是否分区的问题




注意事项：

当增量导入的时候，下面两个选项不能同时使用：
--append and --delete-target-dir can not be used together.

hive不支持增量导入

解决的办法是：通过向hdfs中执行增量导入（即hive表对应额hdfs目录），这样查询hive表的时候也是可以查到增量的数据的

```
#增量导入需要注意的是指定分隔符
import
--connect
jdbc:mysql://192.168.1.204/test
--username
mretlog
--password
mretlog@aipai
--table
test_for_sqoop
--target-dir
/user/aipai/warehouse/test.db/test_for_sqoop
--fields-terminated-by
"\001"
--lines-terminated-by
"\n"
--null-string
'\\N'
--null-non-string
'\\N'
--incremental
append
--check-column
id
--last-value
4
```



* 只是创建表结构

```
chenyansong@hadoop01204:~/soft_install/conf$ cat hive_import_options_only_create_table.txt 

create-hive-table 
--connect
jdbc:mysql://192.168.1.204/test
--username
mretlog
--password
mretlog@aipai
--table
test_for_sqoop
--hive-table 
test.test_sqoop_only_struct

```
* 时间字段的问题


Sqoop 1.4.6支持的增量导入方式只有append附加新数据记录和lastmodified新增修改数据两种，其中append不支持导入至Hive，而lastmodifed支持导入至Hive。
在自动化导入中，务必使用sqoop job的方式，在crontab中写入，并导出日志信息。

```
sqoop import --connect jdbc:mysql://centos:3306/sqooptest --table bigdata --username root --password 123456 --check-column last_mod_ts --incremental lastmodified --last-value "2016-10-03 22:39:43" --merge-key class_id -m 1

```

那边的数据是
1.全量导入

2.增量导入
2.1.增量中如果存在更新


