---
title: SparkSQL数据源之JDBC数据源
categories: spark  
tags: [spark]
---



spark sql支持使用JDBC从关系型数据库(比如Mysql)中读取数据,读取的数据,依然由DataFrame表示,可以很方便的使用spark core提供的各种算子进行处理

实际上使用spark sql处理JDBC中的数据是非常有用的,比如说,你的mysql业务数据库中,有大量的数据,比如1000万,然后,你现在需要编写一个程序,对线上的脏数据进行某种复杂业务逻辑的处理,甚至复杂到可能涉及到要用spark sql反复查询hive中的数据,来进行关联处理

此时,用spark sql来通过JDBC数据源,加载mysql中的数据,然后通过各种算子进行处理,是最好的选择,因为spark是分布式的计算框架,对于1000万数据,肯定是分布式处理的,而如果你自己手工编写一个java程序,那么你只能分批次处理了,首先处理2万条,再处理2万条,可能运行完你的java程序,已经是好久之后的事情了


```
sqlContext.read.format("jdbc").options(
Map(
"url"->"jdbc:mysql://spark1:3306/testdb",
"dbtable"->"students"
)).load()
```

案例:查询分数大于80分的学生信息

```
create database testdb;
use testdb;
create table student_infos(name varchar(20), age int);
create table student_scores(name varchar(20), score int);

insert into student_infos values("leo",18),("marry",17),("jack",19);

insert into student_scores values("leo",88),("marry",77),("jack",99);


create table good_student_infos(name varchar(20),  age  int, score int);

```


完整的代码
```
 val data2Mysql = (iterator: Iterator[(String, Int, Int)])=>{
   val conn:Connection = null
   val ps:PreparedStatement = null
   var sql = "insert into good_student_infos (name,age,scores) values (?,?,?)"
   try{
     //Class.forName("com.mysql.jdbc.Driver")
     val conn = DriverManager.getConnection("jdbc:mysql://spark1:3306/testdb","","")
     iterator.foreach{
       case (name,age,scores) =>
         ps.setString(1,name)
         ps.setInt(2,age)
         ps.setInt(3,age)
         ps.executeUpdate()
       case _ =>
     }
   }catch{
     case e:Exception => println("mysql exception")
   }finally {
     if (ps != null)
       ps.close()
     if (conn != null)
       conn.close()
   }
 }

 def RDD2DataFrameByReflection(): Unit ={
   val sparkConf = new SparkConf().setAppName("dataFrame").setMaster("local")
   val sc = new SparkContext(sparkConf)

   val sqlContext = new SQLContext(sc)

   // 通过jdbc构建DataFrame
   val studentInfosDF = sqlContext.read.format("jdbc")
     .options(Map(
       "url"->"jdbc:mysql://spark1:3306/testdb",
       "dbtable"->"student_infos"
     )).load()

   val studentScoresDF = sqlContext.read.format("jdbc")
     .options(Map(
       "url"->"jdbc:mysql://spark1:3306/testdb",
       "dbtable"->"student_scores"
     )).load()

   // 将两个DataFrame转换为rdd,执行join操作
   val studentInfosRdd = studentInfosDF.rdd.map(row=>(row(0).toString,row(1).toString.toInt))
   val studentScoresRdd = studentScoresDF.rdd.map(row=>(row(0).toString,row(1).toString.toInt))
   val studentInfoScoresRdd = studentInfosRdd.join(studentScoresRdd).map(t=>(t._1, t._2._1, t._2._2))

   // 过滤学生成绩大于80的学生信息
   val goodStudentRdd = studentInfoScoresRdd.filter(_._3 > 80)

   goodStudentRdd.foreachPartition(data2Mysql(_))

 }

```




