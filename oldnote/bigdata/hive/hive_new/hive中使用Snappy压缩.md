---
title: hive中数据的存储格式和Snappy压缩
categories: hive  
tags: [hive]
---

```

hive>set mapreduce.map.output.compress=true;
hive>set mapreduce.map.output.compress.codec=org.apache.hadoop.io.compress.SnappyCodec ;

//同时需要在hadoop按照的时候支持Snappy

```


在hive中一般使用的存储格式为:orc,parquet
数据压缩一般使用的是snappy

```
create table if not exists test_textfile
(

...
)

stored as textfile



create table if not exists test_orc_snappy
(

...
)

stored as orc tblproperties("orc.compress"="SNAPPY")

#储存使用的是orc
#压缩使用的是SNAPPY


向压缩的表中插入数据:
insert into tble test_orc_snappy select * from test_textfile



```

