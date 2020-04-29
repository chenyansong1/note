---
title: Spark2.0新特性之Dataset
categories: spark  
tags: [spark]
---


# DataFrame的基本操作实例

```
#employee.json
{"name": "Leo", "age": 25, "depId": 1, "gender": "male", "salary": 20000}
{"name": "Marry", "age": 30, "depId": 2, "gender": "female", "salary": 25000}

#department.json
{"id": 1, "name": "Technical Department"}
{"id": 2, "name": "Financial Department"}
{"id": 3, "name": "HR Department"}

/*
需求:
1.只统计年龄在20岁以上的员工
2.根据部门和员工性别进行分组,统计出每个部门分性别的平均薪资和年龄
*/


    // 构造SparkSession,基于builder
    val spark = SparkSession
      .builder()
      .appName("DepartmentAvgSalaryAndAgeStat")
      .master("local")
      .config("spark.sql.warehouse.dir", "C:\\Users\\Administrator\\Desktop\\spark-warehouse")
      .getOrCreate()

    //导入spark的隐式转换
    import spark.implicits._
    //导入spark sql的functions
    import org.apache.spark.sql.functions._

    //首先将两份数据文件加载进行,形成两个DataFrame
    val employee = spark.read.json("C:\\Users\\Administrator\\Desktop\\employee.json")
    val department = spark.read.json("C:\\Users\\Administrator\\Desktop\\department.json")

    //进行计算操作
    employee
      //首先对employee进行过滤,只统计20岁以上的员工
      .filter($"age" > 20)
      //需要跟department数据进行join,注意:untyped join,两个表的字段的连接条件,需要使用三个等号
      .join(department, $"depId" === $"id")
      //根据部门名称和员工性别进行分组
      .groupBy(department("name"), employee("gender"))
      //执行聚合函数
      .agg(avg(employee("salary")), avg(employee("age")))
     //最后将结构显示出来
      .show

/*
+--------------------+------+-----------+--------+
|                name|gender|avg(salary)|avg(age)|
+--------------------+------+-----------+--------+
|       HR Department|female|    21000.0|    21.0|
|Technical Department|  male|    17500.0|    30.0|
|Financial Department|female|    26500.0|    30.0|
|       HR Department|  male|    18000.0|    42.0|
+--------------------+------+-----------+--------+
 */
/*
总结:
1.DataFrame==dataset[Row]
2.DataFrame的类型是Row,所以untyped类型,弱类型
3.dataset的类型通常是我们自定义的case class ,所以是type类型,强类型
4.dataset开发,与rdd开发有很多的共同点
  dataset API也分成transformation和action,transformation是lazy
 */

```


# dataset的action操作(collect,count,foreach,reduce)

```

 // 构造SparkSession,基于builder
 val spark = SparkSession
   .builder()
   .appName("DepartmentAvgSalaryAndAgeStat")
   .master("local")
   .config("spark.sql.warehouse.dir", "C:\\Users\\Administrator\\Desktop\\spark-warehouse")
   .getOrCreate()

 //导入spark的隐式转换
 import spark.implicits._

 //首先将两份数据文件加载进行,形成两个DataFrame
 val employee = spark.read.json("C:\\Users\\Administrator\\Desktop\\employee.json")

 //collect:将存储在集群上的分布式数据集(比如dataset)中的所有数据获取到driver端
 employee.collect.foreach(print)
 //[25,1,male,Leo,20000][30,2,female,Marry,25000][35,1,male,Jack,15000]

 //count:对dataset中的记录进行统计个数
 println(employee.count)

 //first:获取数据集中的第一条数据
 println(employee.first)

 //foreach:遍历数据集中的每一条数据,对数据进行操作,这个跟collect不同,
 //collect是将数据获取到driver端进行操作,而foreach是将计算操作推到集群上去分布式的执行
 //foreach(println(_))这种操作,最终的结果是打印在集群中的各个节点上的
 employee.foreach(println(_))

 //reduce:对数据集中的所有的数据进行规约的操作
 employee.map(employee=>1).reduce(_ + _)

 //take:从数据集中获取指定条数
 employee.take(3).foreach(println(_))

```



# 基础操作(持久化,临时视图,执行计划,ds/df互转换,写数据)

```
  // 构造SparkSession,基于builder
  val spark = SparkSession
    .builder()
    .appName("DepartmentAvgSalaryAndAgeStat")
    .master("local")
    .config("spark.sql.warehouse.dir", "C:\\Users\\Administrator\\Desktop\\spark-warehouse")
    .getOrCreate()

  //导入spark的隐式转换
  import spark.implicits._

  //首先将两份数据文件加载进行,形成两个DataFrame
  val employee = spark.read.json("C:\\Users\\Administrator\\Desktop\\employee.json")

  //如果要对一个dataset重复计算两次的话,那么建议先对这个dataset进行持久化在进行操作,避免重复计算
  employee.cache
  println(employee.count)
  println(employee.count)
  /*
  17/04/19 15:25:43 INFO DAGScheduler: Job 1 finished: count at TopNBasic.scala:35, took 0.733971 s
  17/04/19 15:25:44 INFO CodeGenerator: Code generated in 18.156697 ms
  7

  // ....

  17/04/19 15:25:44 INFO DAGScheduler: ResultStage 4 (count at TopNBasic.scala:36) finished in 0.015 s
  17/04/19 15:25:44 INFO DAGScheduler: Job 2 finished: count at TopNBasic.scala:36, took 0.118242 s
  7
   */

  //创建临时视图,主要为了可以直接对数据执行sql语句
  employee.createTempView("employee")
  spark.sql("select * from employee where age>30").show
  /*
  +---+-----+------+----+------+
  |age|depId|gender|name|salary|
  +---+-----+------+----+------+
  | 35|    1|  male|Jack| 15000|
  | 42|    3|  male| Tom| 18000|
  +---+-----+------+----+------+
   */

  //获取spark sql的执行计划
  spark.sql("select * from employee where age>30").explain
  /*
  == Physical Plan ==
  *Filter (isnotnull(age#0L) && (age#0L > 30))
  +- InMemoryTableScan [age#0L, depId#1L, gender#2, name#3, salary#4L], [isnotnull(age#0L), (age#0L > 30)]
        +- InMemoryRelation [age#0L, depId#1L, gender#2, name#3, salary#4L], true, 10000, StorageLevel(disk, memory, deserialized, 1 replicas)
              +- *FileScan json [age#0L,depId#1L,gender#2,name#3,salary#4L] Batched: false, Format: JSON, Location: InMemoryFileIndex[file:/C:/Users/Administrator/Desktop/employee.json], PartitionFilters: [], PushedFilters: [], ReadSchema: struct<age:bigint,depId:bigint,gender:string,name:string,salary:bigint>
  //我们知道带*号的都是自动化生成的(whole-stage-code-generation)
   */

  //查看scheme
  employee.printSchema
  /*
  root
   |-- age: long (nullable = true)
   |-- depId: long (nullable = true)
   |-- gender: string (nullable = true)
   |-- name: string (nullable = true)
   |-- salary: long (nullable = true)
   */

  //写数据
  val employeeWithAgeDF = spark.sql("select * from employee where age>30")
  employeeWithAgeDF.write.json("C:\\Users\\Administrator\\Desktop\\employeeWithAge.txt")

  /*在C:\Users\Administrator\Desktop\employeeWithAge.txt目录下
  ._SUCCESS.crc
  .part-00000-d683f074-b191-49e4-9725-c92002f25c9f.json.crc
  _SUCCESS
  part-00000-d683f074-b191-49e4-9725-c92002f25c9f.json
   */

  //DataFrame转换成dataset
  //case class Employee(name: String, age:Long, depId:Long, gender:String, salary:Long)
  val employeeDS = employee.as[Employee]
  employeeDS.show()
  employeeDS.printSchema()
  /*
  +---+-----+------+------+------+
  |age|depId|gender|  name|salary|
  +---+-----+------+------+------+
  | 25|    1|  male|   Leo| 20000|
  | 30|    2|female| Marry| 25000|
  | 35|    1|  male|  Jack| 15000|
  | 42|    3|  male|   Tom| 18000|
  | 21|    3|female|Kattie| 21000|
  | 30|    2|female|   Jen| 28000|
  | 19|    2|female|   Jen|  8000|
  +---+-----+------+------+------+

  root
   |-- age: long (nullable = true)
   |-- depId: long (nullable = true)
   |-- gender: string (nullable = true)
   |-- name: string (nullable = true)
   |-- salary: long (nullable = true)
   */

  //dataset转成DataFrame
  employeeDS.toDF


```


# type操作(coalesce,reparation)
```
 // 构造SparkSession,基于builder
 val spark = SparkSession
   .builder()
   .appName("DepartmentAvgSalaryAndAgeStat")
   .master("local")
   .config("spark.sql.warehouse.dir", "C:\\Users\\Administrator\\Desktop\\spark-warehouse")
   .getOrCreate()

 //导入spark的隐式转换
 import spark.implicits._

 //首先将两份数据文件加载进行,形成两个DataFrame
 val employee = spark.read.json("C:\\Users\\Administrator\\Desktop\\employee.json")
 val employeeDS = employee.as[Employee]

 println(employeeDS.rdd.partitions.size)

 //coalesce和reparation操作,都是用来重新定义分区的
 //区别在于:coalesce只能用于减少分区数据量,而且可以选择不发生shuffle
 //reparation可以增加分区,也可以减少分区,必须会发生shuffle,相当于进行了一次重分区操作
 val employeeDSRepartitioned = employeeDS.repartition(7)
 println(employeeDSRepartitioned.rdd.partitions.size)

 val employeeDSCoalesced = employeeDS.coalesce(3)
 println(employeeDSRepartitioned.rdd.partitions.size)

 employeeDSCoalesced.show()

```


# typed操作(distinct和dropDuplicates)
```

  // 构造SparkSession,基于builder
  val spark = SparkSession
    .builder()
    .appName("DepartmentAvgSalaryAndAgeStat")
    .master("local")
    .config("spark.sql.warehouse.dir", "C:\\Users\\Administrator\\Desktop\\spark-warehouse")
    .getOrCreate()

  //导入spark的隐式转换
  import spark.implicits._

//case class Employee(name: String, age:Long, depId:Long, gender:String, salary:Long)
  //首先将两份数据文件加载进行,形成两个DataFrame
  val employee = spark.read.json("C:\\Users\\Administrator\\Desktop\\employee.json")
  val employeeDS = employee.as[Employee]

  //distinct和dropDuplicates都是用来去重的
  //区别在于:
  //  distinct:是根据每一条数据进行完整内容的对比和去重
  //  dropDuplicates可以根据指定的字段进行去重
  employeeDS.distinct.show
  employeeDS.dropDuplicates(Seq("name")).show
/*
+---+-----+------+------+------+
|age|depId|gender|  name|salary|
+---+-----+------+------+------+
| 30|    2|female| Marry| 25000|
| 21|    3|female|Kattie| 21000|
| 42|    3|  male|   Tom| 18000|
| 35|    1|  male|  Jack| 15000|
| 30|    2|female|   Jen| 28000|
| 19|    2|female|   Jen|  8000|
| 25|    1|  male|   Leo| 20000|
+---+-----+------+------+------+


+---+-----+------+------+------+
|age|depId|gender|  name|salary|
+---+-----+------+------+------+
| 35|    1|  male|  Jack| 15000|
| 42|    3|  male|   Tom| 18000|
| 30|    2|female|   Jen| 28000|
| 30|    2|female| Marry| 25000|
| 21|    3|female|Kattie| 21000|
| 25|    1|  male|   Leo| 20000|
+---+-----+------+------+------+
*/


```


# typed操作(except,filter,interset)
```

 // 构造SparkSession,基于builder
 val spark = SparkSession
   .builder()
   .appName("DepartmentAvgSalaryAndAgeStat")
   .master("local")
   .config("spark.sql.warehouse.dir", "C:\\Users\\Administrator\\Desktop\\spark-warehouse")
   .getOrCreate()

 //导入spark的隐式转换
 import spark.implicits._

//case class Employee(name: String, age:Long, depId:Long, gender:String, salary:Long)
 //首先将两份数据文件加载进行,形成两个DataFrame
 val employee = spark.read.json("C:\\Users\\Administrator\\Desktop\\employee.json")
 val employee2 = spark.read.json("C:\\Users\\Administrator\\Desktop\\employee2.json")
 val employeeDS = employee.as[Employee]
 val employeeDS2 = employee2.as[Employee]

 //except:获取当前dataset中有,但是另外一个dataset中没有的元素
 //filter:根据我们自己的逻辑,如果返回true,那么就保留该元素,否则就过滤掉
 //intersect:求两个数据集的交集
 employeeDS.except(employeeDS2).show

 employeeDS.filter(employee => employee.age > 20)

 employeeDS.intersect(employeeDS2)


```


# typed(map,flatMap,mapPartitions)
```
case class Employee(name: String, age:Long, depId:Long, gender:String, salary:Long)
case class Department(id:Long, name:String)



 // 构造SparkSession,基于builder
 val spark = SparkSession
   .builder()
   .appName("DepartmentAvgSalaryAndAgeStat")
   .master("local")
   .config("spark.sql.warehouse.dir", "C:\\Users\\Administrator\\Desktop\\spark-warehouse")
   .getOrCreate()

 //导入spark的隐式转换
 import spark.implicits._

 //首先将两份数据文件加载进行,形成两个DataFrame
 val employee = spark.read.json("C:\\Users\\Administrator\\Desktop\\employee.json")
 val department = spark.read.json("C:\\Users\\Administrator\\Desktop\\department.json")

 val employeeDS = employee.as[Employee]
 val departmentDS = department.as[Department]
 //map:将数据集中的每条数据都做一个映射,返回一条新数据
 //flatMap:数据集中的每条数据都可以返回多条数据,然后进行压平
 //mapPartitions:一次性对一个partition中的数据进行处理

 employeeDS.map(employee => (employee.name, employee.age, employee.salary+1000))

 departmentDS.flatMap{
   department=>
     Seq(Department(department.id+1, department.name+"_1"), Department(department.id+2, department.name+"_2"))
 }

 employeeDS.mapPartitions{
   employeeIter=>
     val result = mutable.ArrayBuffer[(String,Long)]()
     while(employeeIter.hasNext){
       var emp = employeeIter.next
       result += ((emp.name, emp.salary+1000))
     }
     //需要将集合转成迭代器返回
     result.toIterator
 }

```

# typed(joinwith,sort)
```
case class Employee(name: String, age:Long, depId:Long, gender:String, salary:Long)
case class Department(id:Long, name:String)


    // 构造SparkSession,基于builder
    val spark = SparkSession
      .builder()
      .appName("DepartmentAvgSalaryAndAgeStat")
      .master("local")
      .config("spark.sql.warehouse.dir", "C:\\Users\\Administrator\\Desktop\\spark-warehouse")
      .getOrCreate()

    //导入spark的隐式转换
    import spark.implicits._

    //首先将两份数据文件加载进行,形成两个DataFrame
    val employee = spark.read.json("C:\\Users\\Administrator\\Desktop\\employee.json")
    val department = spark.read.json("C:\\Users\\Administrator\\Desktop\\department.json")

    val employeeDS = employee.as[Employee]
    val departmentDS = department.as[Department]

    employeeDS.joinWith(departmentDS, $"depId"===$"id").show

	employeeDS.sort($"salary".desc).show

```

# typed(randomSplit,sample)
```
randomSplit:将一个数据集随机切分成几份数据集
sample:按照指定的比例随机抽取出来一些数据


    // 构造SparkSession,基于builder
    val spark = SparkSession
      .builder()
      .appName("DepartmentAvgSalaryAndAgeStat")
      .master("local")
      .config("spark.sql.warehouse.dir", "C:\\Users\\Administrator\\Desktop\\spark-warehouse")
      .getOrCreate()

    //导入spark的隐式转换
    import spark.implicits._

    //首先将两份数据文件加载进行,形成两个DataFrame
    val employee = spark.read.json("C:\\Users\\Administrator\\Desktop\\employee.json")
    val department = spark.read.json("C:\\Users\\Administrator\\Desktop\\department.json")

    val employeeDS = employee.as[Employee]
    val departmentDS = department.as[Department]

    //切成3分,每份的权重为2,10,20
    employeeDS.randomSplit(Array(2,10,20))

    //随机抽取数据总量的0.3的比率
    employeeDS.sample(false, 0.3).show

```


# untyped操作(select,where,groupBy,agg,col,join)
```
untyped操作:实际上就涵盖了普通sql语法的全部了



    // 构造SparkSession,基于builder
    val spark = SparkSession
      .builder()
      .appName("DepartmentAvgSalaryAndAgeStat")
      .master("local")
      .config("spark.sql.warehouse.dir", "C:\\Users\\Administrator\\Desktop\\spark-warehouse")
      .getOrCreate()

    //导入spark的隐式转换
    import spark.implicits._
    import org.apache.spark.sql.functions._

    //首先将两份数据文件加载进行,形成两个DataFrame
    val employee = spark.read.json("C:\\Users\\Administrator\\Desktop\\employee.json")
    val department = spark.read.json("C:\\Users\\Administrator\\Desktop\\department.json")

    employee
      .where("age > 20")
      .join(department, $"depId" === $"id")
      .groupBy(department("name"), employee("gender"))
      //要使用avg函数,需要导入org.apache.spark.sql.functions._
      .agg(avg(employee("salary")))
      .show

    employee
      .select($"name", $"depId", $"salary")
      .where("age>30")
      .show

```


# 聚合函数(avg,sum,max,min,count,countDistinct)
```
 // 构造SparkSession,基于builder
 val spark = SparkSession
   .builder()
   .appName("DepartmentAvgSalaryAndAgeStat")
   .master("local")
   .config("spark.sql.warehouse.dir", "C:\\Users\\Administrator\\Desktop\\spark-warehouse")
   .getOrCreate()

 //导入spark的隐式转换
 import spark.implicits._
 import org.apache.spark.sql.functions._

 //首先将两份数据文件加载进行,形成两个DataFrame
 val employee = spark.read.json("C:\\Users\\Administrator\\Desktop\\employee.json")
 val department = spark.read.json("C:\\Users\\Administrator\\Desktop\\department.json")

 employee
   .join(department, $"depId"===$"id")
   .groupBy(department("name"))
   //要使用avg函数,需要导入org.apache.spark.sql.functions._
   .agg(avg(employee("salary")), sum(employee("salary")), max(employee("salary")), min(employee("salary")), count(employee("name")), countDistinct(employee("name")))
   .show

/*
+--------------------+------------------+-----------+-----------+-----------+-----------+--------------------+
|                name|       avg(salary)|sum(salary)|max(salary)|min(salary)|count(name)|count(DISTINCT name)|
+--------------------+------------------+-----------+-----------+-----------+-----------+--------------------+
|Technical Department|           17500.0|      35000|      20000|      15000|          2|                   2|
|       HR Department|           19500.0|      39000|      21000|      18000|          2|                   2|
|Financial Department|20333.333333333332|      61000|      28000|       8000|          3|                   2|
+--------------------+------------------+-----------+-----------+-----------+-----------+--------------------+
 */

```

# 聚合函数(collect_list, collect_set)
```
collect_list:就是将一个分组内,指定字段的值都收集到一起变成一个数组,不去重
collect_set:同上,唯一的区别是,会去重

常用于行转列
例如:
depId=1,employe=leo
depId=1,employe=jack
depId=1,employe=[leo,jack]



 // 构造SparkSession,基于builder
 val spark = SparkSession
   .builder()
   .appName("DepartmentAvgSalaryAndAgeStat")
   .master("local")
   .config("spark.sql.warehouse.dir", "C:\\Users\\Administrator\\Desktop\\spark-warehouse")
   .getOrCreate()

 //导入spark的隐式转换
 import spark.implicits._
 import org.apache.spark.sql.functions._

 //首先将两份数据文件加载进行,形成两个DataFrame
 val employee = spark.read.json("C:\\Users\\Administrator\\Desktop\\employee.json")
 val department = spark.read.json("C:\\Users\\Administrator\\Desktop\\department.json")

 employee
   .groupBy(employee("depId"))
   .agg(collect_list(employee("name")), collect_set(employee("name")))
   .show

 /*
 +-----+------------------+-----------------+
 |depId|collect_list(name)|collect_set(name)|
 +-----+------------------+-----------------+
 |    1|       [Leo, Jack]|      [Jack, Leo]|
 |    3|     [Tom, Kattie]|    [Tom, Kattie]|
 |    2| [Marry, Jen, Jen]|     [Marry, Jen]|
 +-----+------------------+-----------------+
  */

```


# 其他常用的函数
```
/*
日期函数:current_date, current_timestamp
数学函数:round
随机函数:rand
字符串函数:concat, concat_ws
自定义函数udf和自定义聚合函数udaf

需要的时候去查看官网的API
http://spark.apache.org/docs/latest/api/scala/index.html#org.apache.spark.sql.functions$
*/




 // 构造SparkSession,基于builder
 val spark = SparkSession
   .builder()
   .appName("DepartmentAvgSalaryAndAgeStat")
   .master("local")
   .config("spark.sql.warehouse.dir", "C:\\Users\\Administrator\\Desktop\\spark-warehouse")
   .getOrCreate()

 //导入spark的隐式转换
 import spark.implicits._
 import org.apache.spark.sql.functions._

 //首先将两份数据文件加载进行,形成两个DataFrame
 val employee = spark.read.json("C:\\Users\\Administrator\\Desktop\\employee.json")
 val department = spark.read.json("C:\\Users\\Administrator\\Desktop\\department.json")

 employee
   .select(employee("name"),
     current_date,
     current_timestamp,
     rand,
     round(employee("salary"),2),
     concat(employee("gender"), employee("age")),
     concat_ws("|",employee("gender"), employee("age"))
   ).show

/*
+------+--------------+--------------------+-------------------------+----------------+-------------------+-------------------------+
|  name|current_date()| current_timestamp()|rand(9062034925792708500)|round(salary, 2)|concat(gender, age)|concat_ws(|, gender, age)|
+------+--------------+--------------------+-------------------------+----------------+-------------------+-------------------------+
|   Leo|    2017-04-19|2017-04-19 17:25:...|       0.8536958083571142|           20000|             male25|                  male|25|
| Marry|    2017-04-19|2017-04-19 17:25:...|      0.10866516208665833|           25000|           female30|                female|30|
|  Jack|    2017-04-19|2017-04-19 17:25:...|       0.6128816303412895|           15000|             male35|                  male|35|
|   Tom|    2017-04-19|2017-04-19 17:25:...|       0.9614274109004534|           18000|             male42|                  male|42|
|Kattie|    2017-04-19|2017-04-19 17:25:...|      0.10936129046706444|           21000|           female21|                female|21|
|   Jen|    2017-04-19|2017-04-19 17:25:...|      0.25947595067767937|           28000|           female30|                female|30|
|   Jen|    2017-04-19|2017-04-19 17:25:...|      0.12866036956519833|            8000|           female19|                female|19|
+------+--------------+--------------------+-------------------------+----------------+-------------------+-------------------------+


```


