---
title: SparkStreaming之DStream的output操作
categories: spark  
tags: [spark]
---



# DStream的output操作


Output|Meaning
:-----|:-------
print|打印每个batch中的前10个元素,主要用于测试,或者是不需要执行什么output操作时,用于简单的触发一下job
saveAsTextFile(prefix,[suffix])|将每个batch的数据保存到文件中,每个batch的文件的命名格式为:prefix-TIME_IN_MS[.suffix]
saveAsObjectFile|同上,但是将每个batch的数据以序列化对象的方式,保存到SequenceFile中
saveAsHadoopFile|同上,将数据保存到hadoop文件中
foreachRDD|最常用的output操作,遍历DStream中的每个产生的RDD,进行处理,可以将每个RDD中的数据写入外部村粗,比如:文件,数据库,缓存等,通常在其中,是针对RDD执行action操作的,比如foreach


DStream中的所有的计算,都是由output操作触发的,比如print(),如果没有任何output操作,那么,压根儿就不会执行定义的计算逻辑

此外,即使你使用了foreachRDD output操作,也必须在里面对RDD执行action操作,才能触发对每一个batch的计算逻辑,否则,光有foreachRDD output操作,在里面没有对RDD执行action操作,也不会触发任何的逻辑



# foreachRDD详解

通常在foreachRDD中,都会创建一个Connection,比如 JDBC Connection,然后通过Connection将数据写入外部的存储

误区一:在RDD的foreach操作外部,创建Connection
这种方式是错误的,因为他会导致Connection对象呗序列化后传输到每个Task中,而这种Connection对象,实际上一般是不支持序列化的,也就无法被传输

代码如:
```
dstream.foreachRDD{
	rdd=>
		val connection = createNewConnection()
		rdd.foreach{
			record=>
				connection.send(record)
		}
}
```


误区二:在RDD的foreach操作内部,创建connection
这种方式是可以的,但是效率低下,因为他会导致对于RDD中的每一条数据,都会创建一个connection对象,而通常来说,connection的创建是很消耗性能的

代码如:
```
dstream.foreachRDD{
	rdd=>
		rdd.foreach{
			record=>
				val connection = createNewConnection()
				connection.send(record)
				connection.close				
		}
}

```

合理方式一:
使用RDD的foreachPartition操作,并且在该操作内部,创建Connection对象,这样就相当于为RDD的每个Partition创建一个Connection对象,节省了资源

```
dstream.foreachRDD{
	rdd=>
		rdd.foreachPartition{
			partitionRecords=>
				val connection = createNewConnection()
				partitionRecords.foreach(record=>connection.send(record))
				connection.close				
		}
}
```

合理方式二:较"合理方式一"更好
自己手动封装一个静态连接池,使用RDD的foreachPartition操作,并且在该操作内部,从静态连接池中,通过静态方法,获取到一个连接,使用之后再还回去,这样额话,甚至在多个RDD的partition之间,也可以复用连接了,而且可以让连接池采取懒创建的策略,并且空闲一段时间后,将其释放掉

```
dstream.foreachRDD{
	rdd=>
		rdd.foreachPartition{
			partitionRecords=>
				val connection = ConnectionPool.getConnection()
				partitionRecords.foreach(record=>connection.send(record))
				ConnectionPool.returnConnection(connection)			
		}
}

```


案例:统计全局的WordCount计数,并写入到mysql中

建表
```
create table wordcount(
	id integer auto_increment primary key,
	updated_time timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
	word varchar(255),
	count integer
)

```



代码
```
  val conf = new SparkConf()
    .setAppName("Streaming")
    .setMaster("local[2]")

  // 每收集多长时间的数据就划分为一个batch进行处理,这里设置为1秒:Seconds(1)
  val ssc = new StreamingContext(conf,Seconds(5))

  val line = ssc.socketTextStream("spark1", 9999)
  val pairDStream = line.flatMap(_.split(" ")).map((_,1))

  //updateFunc: (Seq[V], Option[S]) => Option[S]
  val func = (values:Seq[Int], state:Option[Int])=>{
    val sum = values.sum + state.getOrElse(0)
    Option(sum)
  }

  // 全局统计
  val wordCount = pairDStream.updateStateByKey(func)

  // 每次得到当前所有单词的统计次数之后,将其写入mysql,以便后续的J2EE应用程序进行web展示
  wordCount.foreachRDD{
    wordCountRdd=>
      wordCountRdd.foreachPartition{
        partitionRecord=>
          // 获取连接
          val conn = ConnectionPool.getConnection()
          // 遍历partition中的数据,使用一个连接,插入数据到mysql
          while(partitionRecord.hasNext){
            val (word, count ) = partitionRecord.next()
            val sql = "insert into wordcount(word,count) values(?, ?)"
            val ps = conn.prepareStatement(sql)
            ps.setString(1,word)
            ps.setInt(2,count)
            ps.executeUpdate()
          }
          // 还回去连接
          ConnectionPool.returnConnection(conn)
      }
  }

  ssc.start()
  ssc.awaitTermination()
  ssc.stop()


```

连接池代码
```
package org.dt.spark

import java.sql.{Connection, DriverManager}

import scala.collection.mutable

object ConnectionPool {

  val connectionQueue = new mutable.LinkedList[Connection]()

  // 加载驱动
  Class.forName("com.mysql.jdbc.Driver")

  // 获取连接,多线程并发访问控制
  def getConnection(): Connection ={
    if(connectionQueue==null){

      for(i<-1 to 10){
        val conn = DriverManager.getConnection("jdbc:mysql://spark1:3306/testdb", "root", "root")
        connectionQueue.push(conn)
      }
    }

    connectionQueue.poll()
  }

  def returnConnection(conn: Connection): Unit ={
    connectionQueue.push(conn)
  }

}


```








