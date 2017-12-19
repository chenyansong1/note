
这里只是提供导入的思路

# 通过跑MapREduce的方式

* 跑一个MapRudece作业，在Map阶段读取Hbase表的数据，并将HBase的KV数据格式转化为Hive表的数据格式，并在Map阶段直接输出到HDFS

* 在Hive中建表，并将HDFS中的数据load进Hive中。

参见：http://blog.bcmeng.com/post/hbase-hive.html


# 通过跑Spark作业的方式

此种实现思路是将hbase表的数据转成RDD模型，在将RDD转成DataFrame模型，注册Spark临时表，在就数据一次从临时表中，导入到hive表中










