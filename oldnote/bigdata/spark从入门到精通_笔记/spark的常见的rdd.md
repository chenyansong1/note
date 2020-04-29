---
title: spark的常见的rdd
categories: spark   
toc: true  
tag: [spark]
---


# 创建RDD

* 使用程序中的集合创建rdd
主要用于进行测试,可以在实际部署到集群运行之前,自己使用集合构造册数数据,来测试后面的spark应用的流程
* 使用本地文件创建rdd
* 使用HDFS文件创建rdd
主要可以针对HDFS上存储的大数据,进行离线批处理操作



使用程序中的集合创建rdd

```
object MakeRDD {
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "C:\\Users\\Administrator\\Desktop\\hadoop\\")

    val sc = sparkContext("Transformation Operations")
    test(sc)
    sc.stop()//停止SparkContext,销毁相关的Driver对象,释放资源
  }

  def test(sc:SparkContext)={
    val numberRdd = sc.parallelize(Seq(1,2,3,8,22))
    val reducedRdd = numberRdd.reduce(_+_)
    println(reducedRdd)

  }

  //构建SparkContext
  def sparkContext(name:String)={
    val conf = new SparkConf().setAppName(name).setMaster("local")
    val sc = new SparkContext(conf)

    sc
  }

}




```

使用本地文件创建rdd和HDFS文件创建rdd
1.如果是针对本地文件的话,如果是在Windows上本地测试,Windows上有一份文件即可;如果是在spark集群上针对linux本地文件,那么需要将文件拷贝到所有worker节点上
2.spark的textFile方法支持针对目录,压缩文件以及通配符进行rdd的创建
3.spark默认会为HDFS的每一个block创建一个partition,但是也可以通过textFile()的第二个参数手动设置分区数量,只能比block数量多,不能比block数量少


```

object MakeRDD {
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "C:\\Users\\Administrator\\Desktop\\hadoop\\")

    val sc = sparkContext("Transformation Operations")
    test(sc)
    sc.stop()//停止SparkContext,销毁相关的Driver对象,释放资源
  }

  def test(sc:SparkContext)={
    val rdd = sc.textFile("C:\\Users\\Administrator\\Desktop\\xx.txt")
    val rddreuslt = rdd.flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_)
    rddreuslt.foreach(println)
  }

  //在实际的生成中,我们是封装函数来进行逻辑的组织
  def sparkContext(name:String)={
    val conf = new SparkConf().setAppName(name).setMaster("local")
    val sc = new SparkContext(conf)

    sc
  }
}


//如果是使用HDFS文件创建rdd,只要把textFile的文件路径修改为HDFS文件路径即可
val rdd = sc.textFile("hdfs://spark1:9000/xx.txt")

//如果是在集群中运行的时候,那么需要将.setMaster("local")去掉

```


# Transformation和action

spark支持两种rdd操作:Transformation和action,Transformation操作会针对已有的rdd创建一个新的rdd,而action则主要是对rdd进行最后的操作,比如遍历,reduce,保存到文件等,并可以返回结果给Driver程序

例如map就是一种Transformation操作,他用于将已有的rdd的每个元素传入一个自定义的函数,并获取一个新的元素,然后将所有的新元素组成一个新的rdd;而reduce就是一种action操作,它用于对rdd中的所有元素进行聚合操作,并获取一个最终的结果,然后返回给Driver程序

Transformation的特点就是lazy特性,lazy特性的指的是:如果一个spark应用中只定义了Transformation操作,那么即使你执行该应用,这些操作也不会执行,也就是说,Transformation是不会触发spark程序的执行的,他只是记录了对rdd的所作的操作,但是不会自发的执行,只有当Transformation之后,接着执行一个action操作,那么所有的Transformation才会被执行,spark通过这种lazy特性,来进行底层的spark应用执行的优化,避免产生过多的中间结果


action操作执行,会触发一个spark job的运行,从而触发这个action之前所有的Transformation的执行,这是action的特性

综上:Transformation会产生rdd,而action会产生结果而不是rdd

下图是程序提交的流程上来反映spark的Transformation的lazy特性


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/spark从入门到精通_笔记/Transformation_action.png)


案例:统计文件中每行出现的次数
```
 val rdd = sc.textFile("C:\\Users\\Administrator\\Desktop\\xx.txt")
 var reducedByKeyRdd = rdd.map((_,1)).reduceByKey(_+_)
 reducedByKeyRdd.foreach(println)

```


常用的Transformation介绍

操作|介绍
:---|:---
map|将rdd中的每个元素传入自定义函数,获取一个新的元素,然后用新的元素组成的新的rdd
filter|对RDD中的每一个元素进行判断,如果返回true就保留
flatMap|与map类似,大那是对每个元素都可以返回一个或多个新元素,然后对所有的元素flat
groupByKey|根据key进行分组,每个key对应一个Iterator<value>
reduceByKey|对每个key对应的value进行reduce操作
sortByKey|对每个key对应的value进行排序操作
join|对两个包含<key,value>对的rdd进行join操作,每个keyjoin上的pair,都会传入自定义函数进行处理
cogroup|同join,但是是每个key对应的Iterable<value>都会传入自定义的函数进行处理


常用的action操作

操作|介绍
:---|:---
reduce|将rdd中的所有元素进行聚合操作,第一个和第二个元素聚合产生的值,和第三个元素聚合,产生的值和第四个元素聚合,一次类推
collect|将rdd中的所有元素获取到本地客户端
count|获取rdd元素总数
take(n)|后rdd前n个元素
saveAsTextFile|将rdd元素保存到文件中,对每个元素调用toString方法
countByKey|对每个key对应的值进行count计数
foreach|遍历rdd中的每一个元素



# Transformation实例
```
#map:将集合中每个元素乘以2
val numberRdd = sc.parallelize(1 to 7)
val resultRdd = numberRdd.map(_*2)

resultRdd.foreach(println)
/*打印结果:
2
4
6
8
10
12
14
 */


#filter:过滤出集合中的偶数
val numberRdd = sc.parallelize(1 to 7)
val resultRdd = numberRdd.filter(_%2==0)

resultRdd.foreach(println)
/*打印结果:
2
4
6
 */


#flatMap:将行拆分为单词
val linesRdd = sc.parallelize(Seq("zhangsna 88","lisi 99"))
val resultRdd = linesRdd.flatMap(_.split(" "))

resultRdd.foreach(println)
/*打印结果:
zhangsna
88
lisi
99
 */


#groupByKey:将每个班级的成绩进行分组

val linesRdd = sc.parallelize(Seq(("cls1",80),("cls2",88),("cls1",82),("cls2",98)))
val resultRdd = linesRdd.groupByKey()//返回: RDD[(K, Iterable[V])]
resultRdd.foreach{
  score=>{
    print(score._1+" :")

	//println(score._2.toList)
    score._2.foreach(sco=>print(sco.toString + " "))
    println
  }

}

/*打印结果:
cls1 :80 82
cls2 :88 98
 */


#reduceByKey:统计每个班级的总分
val linesRdd = sc.parallelize(Seq(("cls1",80),("cls2",88),("cls1",82),("cls2",98)))
val resultRdd = linesRdd.reduceByKey(_+_)
resultRdd.foreach(println)

/*打印结果:
(cls1,162)
(cls2,186)
 */

#sortByKey:将学生分数进行排序

val linesRdd = sc.parallelize(Seq(("zhangsna",80),("lisi",88),("wangwu",82),("zhaoliu",98)))

//要将key放在tuple2的第一个位置,这就是第一个map的作用,而最后一个map的作用就是调整打印的顺序
val resultRdd = linesRdd.map(t=>(t._2,t._1)).sortByKey(false).map(t=>(t._2,t._1))
resultRdd.foreach(println)

/*打印结果:
(zhaoliu,98)
(lisi,88)
(wangwu,82)
(zhangsna,80)
 */


#join:打印每个学生的成绩

val scoreRdd = sc.parallelize(Seq((1,80),(2,88),(3,82)))
val studRdd = sc.parallelize(Seq((1,"zhangsna"),(2,"lsii"),(3,"wangwu")))

val joinedRdd = scoreRdd.join(studRdd)
/*
def join[W](other: RDD[(K, W)]): RDD[(K, (V, W))]
返回的是:(K, (V, W))
 */
joinedRdd.foreach{
  t=>{//因为返回的是(K, (V, W)),所以用_1,_2去取
    val snu = t._1
    val (score,name) = t._2
    println(name + ":" + score + ":" + snu)
  }
}

/*打印结果:
  zhangsna:80:1
  wangwu:82:3
  lsii:88:2
 */





#cogroup

    val scoreRdd = sc.parallelize(Seq((1,80),(2,88),(1,80),(2,88),(3,82)))
    val studRdd = sc.parallelize(Seq((1,"zhangsna"),(2,"lsii"),(1,"zhangsna2"),(3,"wangwu")))

    val joinedRdd = scoreRdd.cogroup(studRdd)
    /*
    def cogroup[W](other: RDD[(K, W)]): RDD[(K, (Iterable[V], Iterable[W]))]
    返回的是:(K, (Iterable[V], Iterable[W]))
     */
    joinedRdd.foreach{
      t=>{//因为返回的是(K, (V, W)),所以用_1,_2去取
        val snu = t._1
        val (score,name) = t._2
        println(snu + ": " + score.toList.toString + "  " + name.toList.toString)
      }
    }

    /*打印结果:
      1: List(80, 80)  List(zhangsna, zhangsna2)
      3: List(82)  List(wangwu)
      2: List(88, 88)  List(lsii)
     */


```


# action实例

```
#reduce操作
 val scoreRdd = sc.parallelize(Seq(1,2,3,4,5))
 val result = scoreRdd.reduce(_+_)
 println(result)

 /*打印结果:
 15
  */



#collect
val scoreRdd = sc.parallelize(Seq(1,2,3,4,5),3)
//使用collect操作将分布在远程的数据拉取到本地,对大数据量不要这么做,测试可以,因为可能造成本地内存溢出,还可能因为将远程的数据拉倒本地,走网络的话,性能会很差
val result = scoreRdd.collect
result.foreach(println)



#count
val scoreRdd = sc.parallelize(Seq(1,2,3,4,5),3)
val result = scoreRdd.count
println(result)



#take

 val scoreRdd = sc.parallelize(Seq(1,2,3,4,5),3)
 //从远程获取指定数量的数据,返回:Array[T]
 val result = scoreRdd.take(3)
 result.foreach(println)
 
 /*结果打印:
 1
 2
 3
  */




#saveAsTextFile
val scoreRdd = sc.parallelize(Seq(1,2,3,4,5),3)
//从远程获取指定数量的数据,返回:Array[T]
val result = scoreRdd.saveAsTextFile("C:\\Users\\Administrator\\Desktop\\hadoop\\result")

/*结果:
在C:\\Users\\Administrator\\Desktop\\hadoop\\result目录下,有下面的文件:

._SUCCESS.crc
.part-00000.crc
.part-00001.crc
.part-00002.crc
_SUCCESS
part-00000
part-00001
part-00002

因为在parallelize的指定的分区为3,所以会生成3个part,其中的在
part-00000文件中存在的数据:1
part-00000文件中存在的数据:2\n3
part-00000文件中存在的数据:4\n5\n6

*/




#countByKey
 val scoreRdd = sc.parallelize(Seq(("cls1","zhangsan"),("cls1","zhangsan"),("cls1","zhangsan"),("cls3","zhangsan3"),("cls2","zhangsan2")),3)

 val result = scoreRdd.countByKey() //返回:Map[K, Long]
 for((k,v)<-result){
   println(k+":"+v.toString)
 }
 /*结果打印:
 cls2:1
 cls3:1
 cls1:3
  */










```