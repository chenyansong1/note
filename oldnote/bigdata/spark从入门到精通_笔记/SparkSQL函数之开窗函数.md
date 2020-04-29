---
title: SparkSQL函数之开窗函数
categories: spark  
tags: [spark]
---


分组取topN的案例

商品在某个分类下的topN


代码如下
```
 val sparkConf = new SparkConf().setAppName("dataFrame").setMaster("local")
 val sc = new SparkContext(sparkConf)
 val sqlContext = new SQLContext(sc)

 val hiveContext = new HiveContext(sc)
 hiveContext.sql("drop table if not exists sales")
 hiveContext.sql("create table if not exists sales (" +
   "product STRING" +
   "category SRING" +
   "revenue BIGINT" +
   ")")
 hiveContext.sql("load data local inpath '/usr/local/spark-study/resources/sales.txt' into table sales")

 // 使用row_number()开窗函数:给每个分组内的数据,按照其排序的顺序,打上一个分组内的行号,行号从1开始
 val top3SalesDF = hiveContext.sql("select product,category,revenue " +
   "from (" +
   "   select product,category,revenue," +
   "       row_number() over (partition by category order by revenue desc) rank" +
   "   from sales" +
   ") tmp_sales" +
   "where rank <= 3")

 /* row_number函数使用说明:
 1.row_number函数后面跟的是over关键字
 2.括号中是partition by 表示根据哪个字段进行分组
 3.order by表示在组内按照指定的字段进行排序
 4.row_number就能给每个组内的每行一个组内行号
 5.在子查询的外部,取组内排名前3
  */

 hiveContext.sql("drop table if not exists top3_sales")
 top3SalesDF.saveAsTable("top3_sales")


```



