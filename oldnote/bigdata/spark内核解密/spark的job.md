---
title: spark的job
categories: spark   
toc: true  
tag: [spark]
---


# job
在一个Executor中一次性最多能够运行多少并发的Task取决于当前Executor能够使用的Cores数量

如果有88个文件,而每个文件的大小小于128M,那么将会有88个Partition,所以会启动88个task,而由于数据本地性,假如有的机器上有54个文件,那么在该机器上会启动54个task

如果在进行cache时,cache的数据放置在哪台机器上,那么后续的操作会在那台机器上进行,这就是数据本地性

![](/assert/img/bigdata/spark内核解密/job的执行过程.png)




# rdd的依赖关系


![](/assert/img/bigdata/spark内核解密/stage.png)

窄依赖:每个父RDD的Partition最多被子RDD的一个Partition所使用;例如map,filter
宽依赖:多个子RDD的Partition会依赖同一个父RDD的Partition;例如groupByKey,reduceByKey

特别说明:对join操作有两种情况,如果说join操作的使用每个Partition仅仅和已知的Partition进行join,这次是join操作就是窄依赖

每个stage里面的Task的数量是由该stage中最后一个RDD的Partition的数量决定的
从后往前推理,遇到宽依赖就断开,遇到窄依赖就把当前的RDD加入到该stage中

最后一个stage里面的任务类型是ResultTask,前面其他所有的Stage里面的任务的类型为ShuffleMapTask


hadoop中的MapReduce中的Mapper和Reducer在spark中等量的算子是:map和reduceByKey














