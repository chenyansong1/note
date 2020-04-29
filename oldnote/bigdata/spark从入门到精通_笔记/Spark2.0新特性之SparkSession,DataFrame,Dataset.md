---
title: Spark2.0新特性之SparkSession,DataFrame,Dataset
categories: spark  
tags: [spark]
---


# Spark SQL介绍


Spark SQL是Spark的一个模块，主要用于处理结构化的数据。与基础的Spark RDD API不同的是，Spark SQL的接口会向提供更多的信息，包括数据结构以及要执行的计算操作等。在Spark SQL内部，会使用这些信息执行一些额外的优化。使用Spark SQL有两种方式，包括SQL语句以及Dataset API。但是在计算的时候，无论你是用哪种接口去进行计算，它们使用的底层执行引擎是完全一模一样的。这种底层执行机制的统一，就意味着我们可以在不同的方式之间任意来回切换，只要我们可以灵活地运用不同的方式来最自然地表达我们要执行的计算操作就可以了。

Spark SQL的一个主要的功能就是执行SQL查询语句。**Spark 2.0开始，最大的一个改变，就是支持了SQL 2003标准语法，还有就是支持子查询**。Spark SQL也可以用来从Hive中查询数据。当我们使用某种编程语言(scala,java)开发的Spark作业来执行SQL时，返回的结果是Dataframe/Dataset类型的。当然，我们也可以通过Spark SQL的shell命令行工具，或者是JDBC/ODBC接口来访问。

# Dataframe/Dataset介绍
Dataset是一个分布式的数据集。Dataset是Spark 1.6开始新引入的一个接口，它结合了RDD API的很多优点（包括强类型，支持lambda表达式等），以及Spark SQL的优点（优化后的执行引擎）。Dataset可以通过JVM对象来构造，然后通过transformation类算子（map，flatMap，filter等）来进行操作。Scala和Java的API中支持Dataset，但是Python不支持Dataset API。不过因为Python语言本身的天然动态特性，Dataset API的不少feature本身就已经具备了（比如可以通过row.columnName来直接获取某一行的某个字段）。R语言的情况跟Python也很类似。

Dataframe就是按列组织的Dataset。在逻辑概念上，可以大概认为Dataframe等同于关系型数据库中的表，或者是Python/R语言中的data frame，但是在底层做了大量的优化。Dataframe可以通过很多方式来构造：比如结构化的数据文件，Hive表，数据库，已有的RDD。Scala，Java，Python，R等语言都支持Dataframe。在Scala API中，**Dataframe就是Dataset[Row]的类型别名**。在Java中，需要使用Dataset<Row>来代表一个Dataframe。


# SparkSession
从Spark 2.0开始，一个最大的改变就是，Spark SQL的统一入口就是SparkSession，SQLContext和HiveContext未来会被淘汰。可以通过SparkSession.builder()来创建一个SparkSession，如下代码所示。SparkSession内置就支持Hive，包括使用HiveQL语句查询Hive中的数据，使用Hive的UDF函数，以及从Hive表中读取数据等。


```
 // 构造SparkSession,基于builder
 val spark = SparkSession
   .builder()
   .appName("SparkSQLDemo")
   .master("local")
   // spark.sql.warehouse.dir必须设置,这是spark sql的元数据仓库目录
   .config("spark.sql.warehouse.dir","C:\\Users\\Administrator\\Desktop\\spark-warehouse")
   .getOrCreate()

 // 导入隐式转换
 import spark.implicits._

```


# Dataframe：untyped操作
有了SparkSession之后，就可以通过已有的RDD，Hive表，或者其他数据源来创建Dataframe，比如说通过json文件来创建。Dataframe提供了一种domain-specific language来进行结构化数据的操作，这种操作也被称之为untyped操作，与之相反的是基于强类型的typed操作。
```
 // 读取json文件,构造一个untyped弱类型的DataFrame
 val df = spark.read.json("C:\\Users\\Administrator\\Desktop\\people.json")
 /*
 {"name":"Michael"}
 {"name":"Andy", "age":30}
 {"name":"Justin", "age":19}
  */
 // 打印数据
 df.show
 // 打印元数据
 df.printSchema
 // select操作,典型的弱类型,untyped操作
 df.select("name").show
 //scala的表达式的语法,要用$作为前缀
 df.select($"name", $"age"+1).show
 //filter操作+表达式
 df.filter($"age">21).show
 //分组之后求count
 df.groupBy("age").count.show

```

# 运行SQL查询

SparkSession的sql()函数允许我们执行SQL语句，得到的结果是一个Dataframe。
```

 //基于DataFrame创建临时视图
 df.createTempView("people")
 // 使用SparkSession的sql()函数就可以进行sql操作
 val sqlDF = spark.sql("select * from people")
 sqlDF.show
 /*
 +----+-------+
 | age|   name|
 +----+-------+
 |null|Michael|
 |  30|   Andy|
 |  19| Justin|
 +----+-------+
  */


```

# Dataset：typed操作
Dataset与RDD比较类似，但是非常重要的一点不同是，RDD的序列化机制是基于Java序列化机制或者是Kryo的，而Dataset的序列化机制基于一种特殊的Encoder，来将对象进行高效序列化，以进行高性能处理或者是通过网络进行传输。Dataset除了Encoder，也同时支持Java序列化机制，但是encoder的特点在于动态的代码生成，同时提供一种特殊的数据格式，来让spark不将对象进行反序列化，即可直接基于二进制数据执行一些常见的操作，比如filter、sort、hash等。
```
case class Person(name: String, age: Long)



 //直接基于jvm object来构造dataset
 val caseClassDS = Seq(Person("leo", 25)).toDS()
 caseClassDS.show
 /*
 +----+---+
 |name|age|
 +----+---+
 | leo| 25|
 +----+---+
  */

 //基于原始数据类型构造dataset
 val primitiveDS = Seq(1, 2, 3).toDS
 primitiveDS.map(_ + 1).show
 /*
 +-----+
 |value|
 +-----+
 |    2|
 |    3|
 |    4|
 +-----+
  */

 //基于已有的结构化文件,来构造dataset
 // spark.read.json首先获取到的是一个DataFrame,然后使用as[People]之后,将DataFrame转换成dataset
 val peopleDS = spark.read.json("C:\\Users\\Administrator\\Desktop\\people.json").as[Person]
 peopleDS.show
 /*
 +----+-------+
 | age|   name|
 +----+-------+
 |null|Michael|
 |  30|   Andy|
 |  19| Justin|
 +----+-------+
  */

```

# Hive操作

在Spark 2.0中，是支持读写hive中存储的数据的。但是，因为hive有较多的依赖，所以默认情况下，这些依赖没有包含在spark的发布包中。如果hive依赖可以在classpath路径中，那么spark会自动加载这些依赖。这些hive依赖必须在所有的worker node上都放一份，因为worker node上运行的作业都需要使用hive依赖的序列化与反序列化包来访问hive中的数据。

只要将hive-site.xml、hdfs-site.xml和core-site.xml都放入spark/conf目录下即可。

如果要操作Hive，那么构建SparkSession的时候，就必须启用Hive支持，包括连接到hive的元数据库，支持使用hive序列化与反序列化包，以及支持hive udf函数。如果我们没有安装hive，也是可以启用hive支持的。如果我们没有放置hive-site.xml到spark/conf目录下，SparkSession就会自动在当前目录创建元数据库，同时创建一个spark.sql.warehouse.dir参数设置的目录，该参数的值默认是当前目录下的spark-warehouse目录。在spark 2.0中，hive.metastore.warehouse.dir属性已经过时了，现在使用 spark.sql.warehouse.dir属性来指定hive元数据库的位置。


# Hive操作
```
case class Record(key: Int, value: String)
val warehouseLocation = "file:${system:user.dir}/spark-warehouse"

val spark = SparkSession
  .builder()
  .appName("Spark Hive Example")
  .config("spark.sql.warehouse.dir", warehouseLocation)
  .enableHiveSupport()
  .getOrCreate()

import spark.implicits._



// 创建hive表
spark.sql("create table if not exists src(key INT, value STRING")
// 加载数据
spark.sql("load data local inpath 'C:\\Users\\Administrator\\Desktop\\kv1.txt' into table src")
spark.sql("select * from src").show
spark.sql("select count(*) from src").show
val hiveSqlDF = spark.sql("select key,value from src where key < 10 order by key")
// 将DataFrame转成dataset
val hiveSqlDS = hiveSqlDF.map{
  case Row(key:Int, value:String) => s"KEY: $key, VALUE: $value"
}
hiveSqlDS.show

val recordDF = spark.createDataFrame((1 to 100).map(i => Record(i,s"val_$i")))
recordDF.createOrReplaceTempView("records")
spark.sql("select * from src join records on src.key=records.key").show


```

# Hive 1.2.1安装
spark 2.0，默认是跟hive 1.2.1进行整合的，所以之前我们安装的是hive 0.13.1是不Ok的，实际跑的时候会出现hive 0.13支持的一些操作，spark 2.0会用自己内置的hive 1.2.1 lib去操作和访问我们的hive 0.13（包括metastore service），出现版本不一致的问题

1、将/usr/local/hive删除
2、将apache-hive-1.2.1-bin.tar.gz使用WinSCP上传到spark1的/usr/local目录下。
3、解压缩hive安装包：tar -zxvf apache-hive-1.2.1-bin.tar.gz。
4、重命名hive目录：mv apache-hive-1.2.1-bin hive
5、cp /usr/share/java/mysql-connector-java-5.1.17.jar /usr/local/hive/lib

```
mv hive-default.xml.template hive-site.xml
vi hive-site.xml
<property>
  <name>javax.jdo.option.ConnectionURL</name>
  <value>jdbc:mysql://spark2upgrade01:3306/hive_metadata?createDatabaseIfNotExist=true</value>
</property>
<property>
  <name>javax.jdo.option.ConnectionDriverName</name>
  <value>com.mysql.jdbc.Driver</value>
</property>
<property>
  <name>javax.jdo.option.ConnectionUserName</name>
  <value>hive</value>
</property>
<property>
  <name>javax.jdo.option.ConnectionPassword</name>
  <value>hive</value>
</property>
<property>
  <name>hive.metastore.uris</name>
  <value>thrift://spark2upgrade01:9083</value>
</property>


```


把hive-site.xml中所有${system:java.io.tmpdir}全部替换为/usr/local/hive/iotmp
把hive-site.xml中所有${system:user.name}全部替换为root

jline报错解决
```
rm -rf /usr/local/hadoop/share/hadoop/yarn/lib/jline-0.9.94.jar
cp /usr/local/hive/lib/jline-2.12.jar /usr/local/hadoop/share/hadoop/yarn/lib
```

hive-env.sh
```
mv hive-env.sh.template hive-env.sh
vi /usr/local/hive/bin/hive-config.sh
export JAVA_HOME=/usr/java/latest
export HIVE_HOME=/usr/local/hive
export HADOOP_HOME=/usr/local/hadoop
```



1、将hive-site.xml放置到spark的conf目录下
2、启动hive metastore service
```
hive --service metastore &

```

1、创建一份文件，students.txt，每行是一个学生的信息
2、CREATE TABLE students(name string, age int, score double)
3、LOAD DATA LOCAL INPATH '/usr/local/test_data/students.txt' INTO TABLE students
4、spark-shell --master spark://spark2upgrade01:7077 --driver-memory 500m --executor-memory 500m
5、在spark-shell中，运行针对hive的sql语句
spark.sql(“select * from students”).show();
spark.sql(“select name from students where score>=90;”).show();
spark.sql(“select name from students where age<=15;”).show();
6、观察是否有正确的结果
7、在spark web ui中检查是否有运行的作业记录

