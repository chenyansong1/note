---
title: spark性能优化之三十六之数据倾斜解决方案之聚合源数据以及过滤导致倾斜的key
categories: spark  
tags: [spark]
---


# 第一个方案：聚合源数据

咱们现在，做一些聚合的操作，groupByKey、reduceByKey；groupByKey，说白了，就是拿到每个key对应的values；reduceByKey，说白了，就是对每个key对应的values执行一定的计算。

<!--more-->

现在这些操作，比如groupByKey和reduceByKey，包括之前说的join。都是在spark作业中执行的。

spark作业的数据来源，通常是哪里呢？90%的情况下，数据来源都是hive表（hdfs，大数据分布式存储系统）。hdfs上存储的大数据。hive表，hive表中的数据，通常是怎么出来的呢？有了spark以后，hive比较适合做什么事情？hive就是适合做离线的，晚上凌晨跑的，ETL（extract transform load，数据的采集、清洗、导入），hive sql，去做这些事情，从而去形成一个完整的hive中的数据仓库；说白了，数据仓库，就是一堆表。

spark作业的源表，hive表，其实通常情况下来说，也是通过某些hive etl生成的。hive etl可能是晚上凌晨在那儿跑。今天跑昨天的数九。

数据倾斜，某个key对应的80万数据，某些key对应几百条，某些key对应几十条；现在，咱们直接在生成hive表的hive etl中，对数据进行聚合。比如按key来分组，将key对应的所有的values，全部用一种特殊的格式，拼接到一个字符串里面去，比如“key=sessionid, value: action_seq=1|user_id=1|search_keyword=火锅|category_id=001;action_seq=2|user_id=1|search_keyword=涮肉|category_id=001”。

对key进行group，在spark中，拿到key=sessionid，values<Iterable>；hive etl中，直接对key进行了聚合。那么也就意味着，每个key就只对应一条数据。在spark中，就不需要再去执行groupByKey+map这种操作了。直接对每个key对应的values字符串，map操作，进行你需要的操作即可。key,values串。

spark中，可能对这个操作，就不需要执行shffule操作了，也就根本不可能导致数据倾斜。

或者是，对每个key在hive etl中进行聚合，对所有values聚合一下，不一定是拼接起来，可能是直接进行计算。reduceByKey，计算函数，应用在hive etl中，每个key的values。



# 聚合源数据方案，第二种做法

你可能没有办法对每个key，就聚合出来一条数据；

那么也可以做一个妥协；对每个key对应的数据，10万条；有好几个粒度，比如10万条里面包含了几个城市、几天、几个地区的数据，现在放粗粒度；直接就按照城市粒度，做一下聚合，几个城市，几天、几个地区粒度的数据，都给聚合起来。比如说

city_id date area_id

select ... from ... group by city_id

尽量去聚合，减少每个key对应的数量，也许聚合到比较粗的粒度之后，原先有10万数据量的key，现在只有1万数据量。减轻数据倾斜的现象和问题。



# 第二个方案：过滤导致倾斜的key

如果你能够接受某些数据，在spark作业中直接就摒弃掉，不使用。比如说，总共有100万个key。只有2个key，是数据量达到10万的。其他所有的key，对应的数量都是几十。

这个时候，你自己可以去取舍，如果业务和需求可以理解和接受的话，在你从hive表查询源数据的时候，直接在sql中用where条件，过滤掉某几个key。

那么这几个原先有大量数据，会导致数据倾斜的key，被过滤掉之后，那么在你的spark作业中，自然就不会发生数据倾斜了。
