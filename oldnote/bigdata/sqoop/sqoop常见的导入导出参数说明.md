---
title: sqoop常见的导入导出参数说明
categories: sqoop   
toc: true  
tag: [sqoop]
---



# 导入到HDFS

```
sqoop import 
--connectjdbc:mysql://192.168.81.176/hivemeta2db 
--username root 
--password passwd 
--table sds


/*
可以通过--m设置并行数据，即map的数据，决定文件的个数。

默认目录是/user/${user.name}/${tablename}，可以通过--target-dir设置hdfs上的目标目录。
*/


#如果想要将整个数据库中的表全部导入到hdfs上，可以使用import-all-tables命令
sqoop import-all-tables 
--connect jdbc:mysql://192.168.81.176/hivemeta2db 
--username root 
--password passwd


#如果想要指定所需的列，使用如下：
sqoop import 
--connect jdbc:mysql://192.168.81.176/hivemeta2db 
--username root -password passwd 
--table sds 
--columns "SD_ID,CD_ID,LOCATION"        #指定需要导出的列




#导入文本时可以指定分隔符：

sqoop import 
--connect jdbc:mysql://192.168.81.176/hivemeta2db 
--username root -password passwd 
--table sds 
--fields-terminated-by '\t'
 --lines-terminated-by '\n' 
--optionally-enclosed-by '\"'



#可以指定过滤条件：
sqoop import 
--connect jdbc:mysql://192.168.81.176/hivemeta2db 
--username root 
--password passwd 
--table sds 
--where "sd_id > 100"


```


# 导入到hive


参数				|	说明
:-------------------|:--------------
–hive-home <dir>	|Hive的安装目录，可以通过该参数覆盖掉默认的hive目录
–hive-overwrite		|	覆盖掉在hive表中已经存在的数据
–create-hive-table	|默认是false,如果目标表已经存在了，那么创建任务会失败
–hive-table			|后面接要创建的hive表
–table				|指定关系数据库表名


```
sqoop create-hive-table 
--connect jdbc:mysql://192.168.81.176/sqoop 
--username root 
--password passwd 
--table sds 
--hive-table sds_bak

#默认sds_bak是在default数据库的

```



# 导出

export选项：

```
sqoop export
--connect jdbc:mysql://192.168.81.176/sqoop
--username root
--password passwd
--table sds
--export-dir /user/guojian/sds
 
/*
上例中sqoop数据中的sds表需要先把表结构创建出来，否则export操作会直接失败
*/
 
```

&emsp;将hdfs上的数据导入到关系数据库中

参数				|	说明
:-------------------|:--------------
--direct	 				|	工具导入mysqlmysqlimport直接使用 
--export-dir <dir>			|需要export的hdfs数据路径
-m,--num-mappers <n>		|	并行export的map个数n
--table <table-name>		|	导出到的目标表
--call <stored-proc-name>	|调用存储过程
--update-key <col-name>		|	指定需要更新的列名，可以将数据库中已经存在的数据进行更新
--update-mode <mode>		|allowinsert (默认）和updateonly更新模式，包括 










