---
title: SparkSQL数据源之hive数据源
categories: spark  
tags: [spark]
---


spark sql支持对hive中存储的数据进行读写,操作hive中的数据时,必须创建HiveContext,而不是SQLContext,HiveContext继承自SQLContext,但是增加了在hive元数据库中查找表,以及用HiveQL语法编写SQL的功能,除了sql()方法,HiveContext还提供了hql()方法,从而用hive语法来编译sql


使用HiveContext,可以执行hive的大部分功能,包括创建表,往表里到入数据以及用sql语句查询表中的数据,查询出来的数据是一个Row数组

将hive-site.xml拷贝到spark/conf目录下,将mysql connector拷贝到spark/lib目录下

```
val sqlContext = new HiveContext(sc)
sqlContext.sql("create table if not exists students (name String, age Int)")

sqlContext.sql("load data local inpath '/usr/local/spark-study/resouces/students.txt' into table students")

sqlContext.sql("select name, age from students where age<=18").collect

```


spark sql还允许将数据保存到hive表中,调用DataFrame的saveAsTable命令,即可将DataFrame中的数据保存到hive表中,与registerTempTable不同,saveAsTable是会将DataFrame中的数据物化到hive表中的,而且还会在hive元数据库中创建表的元数据

默认情况下,saveAsTable会创建一张hive Managed Table,也就是说,数据的位置都是由元数据库中国你的信息控制的,当managed Table被删除时,表中的数据也会一并被物理删除

registerTempTable只是注册一个临时的表,只要按spark Application重启或者停止了,那么表就没了,而saveAsTable创建的是物化的表,无论spark Application重启或停止,表都会一直存在

调用hiveContext.table()方法还可以直接针对hive中的表,创建一个DataFrame

案例:查询分数大于80分的学生的信息
```
 val sparkConf = new SparkConf().setAppName("dataFrame").setMaster("local")
 val sc = new SparkContext(sparkConf)

 val hiveContext = new HiveContext(sc)

 // 判断是否存在student_info表,如果存在就删除
 hiveContext.sql("drop if exists table student_info")
 // 创建表
 hiveContext.sql("create if not exists table studnet_info (name STRNG, age INT")

 // 将学生基本信息数据导入student_info表
 hiveContext.sql("load data local inpath '/usr/local/spark-study/resouces/student_info.txt' into table student_info ")

 // 同样的步骤:创建表student_score,并加载数据
 hiveContext.sql("drop if exists table student_score")
 hiveContext.sql("create if not exists table student_score (name STRNG, score INT")
 hiveContext.sql("load data local inpath '/usr/local/spark-study/resouces/student_score.txt' into table student_score ")

 // 执行sql查询,关联2张表,查询成绩大于80分的学生成绩
 val goodStudentDF = hiveContext.sql("select info.name,info.age,score.score" +
                         "from student_info info" +
                         "join student_score score on info.name=score.name" +
                         "where score.score>=80")

 // 接着将DataFrame中的数据保存到good_student_info表中
 hiveContext.sql("drop if exists table good_student_info")
 goodStudentDF.saveAsTable("good_student_info")

 // 然后针对good_student_info表直接创建DataFrame
 val goodStudentRows = hiveContext.table("good_student_info").collect
 for(goodStudentRow <- goodStudentRows){
   println(goodStudentRow)
 }

```

测试
```
进入到hive的shell中

>hive
>show tables;
>select * from good_student_info;

```

