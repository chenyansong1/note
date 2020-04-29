---
title: sqoop的数据导入导出实例
categories: sqoop   
toc: true  
tag: [sqoop]
---




# 1.Sqoop的数据导入
&emsp;“导入工具”导入单个表从RDBMS到HDFS。表中的每一行被视为HDFS的记录。所有记录都存储为文本文件的文本数据（或者Avro、sequence文件等二进制数据）

## 1.1.语法
```
$ sqoop import (generic-args) (import-args) 
```

<!--more-->

## 1.2.实例
### 1.2.1.表数据
在mysql中有一个库userdb中三个表：emp, emp_add和emp_contact
表emp:

id	| name	   |deg	           | salary|dept
:---:|:--------:|:-------------:|:------:|:----:
1201|	gopal 	| manager	   | 50,000|TP
1202|	manisha| Proof reader  | 50,000|TP
1203|	khalil	| php dev  		|30,000|AC
1204| prasanth	|php dev		|30,000|AC
1205|	kranthi| admin			|20,000|TP

表emp_add:地址


id	| hno	|  street	|city
:---:|:---:|:-----------:|:----:
1201|288A	|vgiri	    |  jublee
1202|108I	|aoc	    | sec-bad
1203|144Z	|pgutta	hyd |
1204|78B	|old city	|sec-bad
1205|720X	|hitec	    | sec-bad
 
表emp_conn:联系表

id	| phno	|  email
:---:|:---:|:-----------:
1201|2356742|gopal@tp.com
1202|1661663|manisha@tp.com
1203|8887776|khalil@ac.com
1204|9988774|prasanth@ac.com
1205|1231231|kranthi@tp.com


### 1.2.2.导入表表数据到HDFS

下面的命令用于从MySQL数据库服务器中的emp表导入HDFS。
```

$bin/sqoop import   \
--connect jdbc:mysql://hdp-node-01:3306/test   \
--username root  \
--password root   \
--table emp   \
--m 1 


#查看
$ $HADOOP_HOME/bin/hadoop fs -cat /user/hadoop/emp/part-m-00000

1201, gopal,    manager, 50000, TP
1202, manisha,  preader, 50000, TP
1203, kalil,    php dev, 30000, AC
1204, prasanth, php dev, 30000, AC
1205, kranthi,  admin,   20000, TP


```


### 1.2.3.导入关系表到HIVE
其实就是：首先导入到hdfs，然后从hdfs中load到hive中
```
bin/sqoop import --connect jdbc:mysql://hdp-node-01:3306/test --username root --password root --table emp --hive-import --m 1
```


### 1.2.4.导入到HDFS指定目录
&emsp;在导入表数据到HDFS使用Sqoop导入工具，我们可以指定目标目录。以下是指定目标目录选项的Sqoop导入命令的语法。

```
--target-dir <new or exist directory in HDFS>
```

 下面的命令是用来导入emp_add表数据到'/queryresult'目录。
```
bin/sqoop import \
--connect jdbc:mysql://hdp-node-01:3306/test \
--username root \
--password root \
--target-dir /queryresult \
--table emp --m 1

#查看
 $HADOOP_HOME/bin/hadoop fs -cat /queryresult/part-m-*

1201, 288A, vgiri,   jublee
1202, 108I, aoc,     sec-bad
1203, 144Z, pgutta,  hyd
1204, 78B,  oldcity, sec-bad
1205, 720C, hitech,  sec-bad

```

### 1.2.5.导入表数据子集
&emsp;我们可以导入表的使用Sqoop导入工具，"where"子句的一个子集。它执行在各自的数据库服务器相应的SQL查询，并将结果存储在HDFS的目标目录。
where子句的语法如下。
```
--where <condition>
```

 下面的命令用来导入emp_add表数据的子集。子集查询检索员工ID和地址，居住城市为：Secunderabad
```
bin/sqoop import \
--connect jdbc:mysql://hdp-node-01:3306/test \
--username root \
--password root \
--where "city ='sec-bad'" \
--target-dir /wherequery \
--table emp_add --m 1
```

 按需导入：query
```
bin/sqoop import \
--connect jdbc:mysql://hdp-node-01:3306/test \
--username root \
--password root \
--target-dir /wherequery2 \    
--query 'select id,name,deg from emp WHERE  id>1207 and $CONDITIONS' \                                #query和$CONDITIONS是固定的格式 
--split-by id \
--fields-terminated-by '\t' \
--m 1

```


### 1.2.6.增量导入
&emsp;增量导入是仅导入新添加的表中的行的技术。
它需要添加‘incremental’, ‘check-column’, 和 ‘last-value’选项来执行增量导入。
下面的语法用于Sqoop导入命令增量选项。
```
--incremental <mode>
--check-column <column name>
--last value <last check column value>

```

 假设新添加的数据转换成emp表如下：
1206, satish p, grp des, 20000, GR
下面的命令用于在EMP表执行增量导入。
```
bin/sqoop import \
--connect jdbc:mysql://hdp-node-01:3306/test \
--username root \
--password root \
--table emp --m 1 \
--incremental append \            #增量
--check-column id \            #检查ID的值为1208
--last-value 1208

```



# 2.Sqoop的数据导出
&emsp;将数据从HDFS导出到RDBMS数据库，导出前，目标表必须存在于目标数据库中。

* 默认操作是从将文件中的数据使用INSERT语句插入到表中
* 更新模式下，是生成UPDATE语句更新表数据

## 2.1.语法
```
$ sqoop export (generic-args) (export-args) 
```

## 2.2.示例
 数据是在HDFS 中“EMP/”目录的emp_data文件中。所述emp_data如下：

```
1201, gopal,     manager, 50000, TP
1202, manisha,   preader, 50000, TP
1203, kalil,     php dev, 30000, AC
1204, prasanth,  php dev, 30000, AC
1205, kranthi,   admin,   20000, TP
1206, satish p,  grp des, 20000, GR

```
 1、首先需要手动创建mysql中的目标表
```
$ mysql
mysql> USE db;
mysql> CREATE TABLE employee (
   id INT NOT NULL PRIMARY KEY,
   name VARCHAR(20),
   deg VARCHAR(20),
   salary INT,
   dept VARCHAR(10));
```
 2、然后执行导出命令

```
bin/sqoop export \
--connect jdbc:mysql://hdp-node-01:3306/test \
--username root \
--password root \
--table employee \
--export-dir /user/hadoop/emp/
```

 3、验证表mysql命令行。

```

mysql>select * from employee;
如果给定的数据存储成功，那么可以找到数据在如下的employee表。
+------+--------------+-------------+-------------------+--------+
| Id   | Name         | Designation | Salary            | Dept   |
+------+--------------+-------------+-------------------+--------+
| 1201 | gopal        | manager     | 50000             | TP     |
| 1202 | manisha      | preader     | 50000             | TP     |
| 1203 | kalil        | php dev     | 30000               | AC     |
| 1204 | prasanth     | php dev     | 30000             | AC     |
| 1205 | kranthi      | admin       | 20000             | TP     |
| 1206 | satish p     | grp des     | 20000             | GR     |
+------+--------------+-------------+-------------------+--------+
```




# 3.Sqoop作业(job)

&emsp;Sqoop作业——将事先定义好的数据导入导出任务按照指定流程运行

## 3.1.语法
```
$ sqoop job (generic-args) (job-args)
   [-- [subtool-name] (subtool-args)]
 
$ sqoop-job (generic-args) (job-args)
   [-- [subtool-name] (subtool-args)]

```


## 3.2.创建作业(--create)
 在这里，我们创建一个名为myjob，这可以从RDBMS表的数据导入到HDFS作业。

```
bin/sqoop job --create myimportjob -- import --connect jdbc:mysql://hdp-node-01:3306/test --username root --password root --table emp --m 1
#该命令创建了一个从db库的employee表导入到HDFS文件的作业。
```

## 3.3.验证作业 (--list)

```
‘--list’ 参数是用来验证保存的作业。下面的命令用来验证保存Sqoop作业的列表。
$ sqoop job --list
它显示了保存作业列表。
Available jobs:
   myimportjob
检查作业(--show)
‘--show’ 参数用于检查或验证特定的工作，及其详细信息。以下命令和样本输出用来验证一个名为myjob的作业。
$ sqoop job --show myjob
它显示了工具和它们的选择，这是使用在myjob中作业情况。

Job: myjob
Tool: import Options:
----------------------------
direct.import = true
codegen.input.delimiters.record = 0
hdfs.append.dir = false
db.table = employee
...
incremental.last.value = 1206
...
```
## 3.4.执行作业 (--exec)
&emsp;‘--exec’ 选项用于执行保存的作业。下面的命令用于执行保存的作业称为myjob。
```
$ sqoop job --exec myjob
它会显示下面的输出。
10/08/19 13:08:45 INFO tool.CodeGenTool: Beginning code generation
...
```

