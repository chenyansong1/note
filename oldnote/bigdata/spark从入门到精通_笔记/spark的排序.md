---
title: spark的排序
categories: spark   
toc: true  
tag: [spark]
---




这里使用的是sortByKey对Tuple按照key进行排序


```
val lineRdd = sc.textFile("C:\\Users\\Administrator\\Desktop\\xx.txt")
val reducedRdd = lineRdd.flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_)

//将排序的key转换到Tuple的key列
val countWords = reducedRdd.map(count=>(count._2,count._1))

val sortedRdd = countWords.sortByKey(false)

//重新组织
val result = sortedRdd.map(sort=>(sort._2,sort._1))

result.foreach(println)

/*执行结果:
(spark,19)
(hadoop,13)
(88,6)
(100,6)
(56,6)
(22,4)
(33,4)
(99,4)
(94,2)
(94spark,1)
 */

```



二次排序
也是使用sortByKey,只不过此时key为我们自定义的Bean作为key来进行排序,而Bean中有比较的方法
```


class SecondarySortKey(val first:Int, val second: Int ) extends Ordered[SecondarySortKey] with Serializable{
  def compare(other: SecondarySortKey): Int = {
    if(this.first - other.first !=0){
      this.first - other.first
    }else{
      this.second - other.second
    }
  }
}

object SecondarySortKey{
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "C:\\Users\\Administrator\\Desktop\\hadoop\\")
    val sc = sparkContext("Transformation Operations")
    val line = sc.textFile("C:\\Users\\Administrator\\Desktop\\xx.txt")
    /*xx.txt
    1 11
    2 22
    3 33
    2 11
    1 22
     */
    val pairWithSortKey = line.map(line=>{
      val arr = line.split(" ")
      val first = arr(0).toInt
      val second = arr(1).toInt
      (new SecondarySortKey(first,second), line)  //指定Tuple的key为SecondarySortKey
    }).sortByKey(false).map(pair=>pair._2)//sortByKey排序的时候会以SecondarySortKey为key排序

    pairWithSortKey.collect.foreach(println)
    /*
    打印结果:
    3 33
    2 22
    2 11
    1 22
    1 11
     */
  }

  //在实际的生成中,我们是封装函数来进行逻辑的组织
  def sparkContext(name:String)={
    val conf = new SparkConf().setAppName(name).setMaster("local")

    //创建SparkContext,这是第一个RDD创建的唯一入口,是通往集群的唯一通道
    val sc = new SparkContext(conf)
    sc
  }
}


```

其实对基本类型spark有对基本类型的比较方法的实现,所以不用我们实现比较方法也能实现排序


