


# 合并shell

```

#合并

//major compaction
major_compact '表名或region名'

//minor compaction
compact '表名或region名'
```

查看hbase的目录可以看到，在列族info1下面有多个hfile文件，我们可以手动的使用com


## minor合并

```

[root@hdp-node-02 zookeeper]# /bigdata_installed/hadoop/bin/hdfs dfs -ls /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1
-rw-r--r--   3 root supergroup      43689 2017-06-23 11:59 /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1/2d00054a49924bb799e7fde723682da4
-rw-r--r--   3 root supergroup       5291 2017-06-23 12:07 /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1/9890c8c293624b61a8b97198106562d6
-rw-r--r--   3 root supergroup       5291 2017-06-23 12:07 /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1/9d1bbc78d17147ebb8014301efb326f1
-rw-r--r--   3 root supergroup       5291 2017-06-23 12:06 /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1/afd925701d16426da18e74ae023bb876

#执行minor合并
compact 'user'
#或者
compact 'e0e3ab04968fda09964758a0ededb14e'
#执行minor合并之后
[root@hdp-node-02 zookeeper]# /bigdata_installed/hadoop/bin/hdfs dfs -ls /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1
-rw-r--r--   3 root supergroup      43699 2017-06-23 12:07 /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1/7c28d655124d4f3b8e5c90201b3dfdf4

```

# minor合并规则

* hbase.hstore.compaction.min :默认值为 3，表示至少需要三个满足条件的store file时，minor compaction才会启动
* minor compaction的运行机制要复杂一些，它由一下几个参数共同决定：
* hbase.hstore.compaction.min :默认值为 3，表示至少需要三个满足条件的store file时，minor compaction才会启动
* hbase.hstore.compaction.max 默认值为10，表示一次minor compaction中最多选取10个store file
* hbase.hstore.compaction.min.size 表示文件大小小于该值的store file 一定会加入到minor compaction的store file中
* hbase.hstore.compaction.max.size 表示文件大小大于该值的store file 一定会被minor compaction排除
* hbase.hstore.compaction.ratio 将store file 按照文件年龄排序（older to younger），minor compaction总是从older store file开始选择，如果该文件的size 小于它后面hbase.hstore.compaction.max 个store file size 之和乘以 该ratio，则该store file 也将加入到minor compaction 中。

```
[root@hdp-node-02 zookeeper]# /bigdata_installed/hadoop/bin/hdfs dfs -ls /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1
17/06/23 13:54:54 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
Found 14 items
-rw-r--r--   3 root supergroup       5303 2017-06-23 13:54 /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1/077d8ff76e8d471e9c14d0b301d3d51e
-rw-r--r--   3 root supergroup       5291 2017-06-23 13:54 /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1/365de4ab8dba43b3b72d1241b673bd2e
-rw-r--r--   3 root supergroup       5291 2017-06-23 13:54 /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1/38cc0c2425f04478998a91c6309b972e
-rw-r--r--   3 root supergroup       5291 2017-06-23 13:54 /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1/73ec17fb7b42483c91b6dc3d05d8c548
-rw-r--r--   3 root supergroup       5291 2017-06-23 13:54 /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1/88f30ce702d6461b857d033257574f12
-rw-r--r--   3 root supergroup       5303 2017-06-23 13:54 /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1/8960bb86b3b84c38b09bbbd2bc74e16e
-rw-r--r--   3 root supergroup       5411 2017-06-23 13:53 /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1/8a3690da1ec94f48bb26219cdcc6dc1f
-rw-r--r--   3 root supergroup       5291 2017-06-23 13:54 /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1/a116e033cd2142a4b8be4ae119fa9b5a
-rw-r--r--   3 root supergroup      48905 2017-06-23 13:50 /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1/a12a973d59b4493a806e478a5146972f
-rw-r--r--   3 root supergroup       5291 2017-06-23 13:54 /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1/ae2865e7916b4192ad7593898626b257
-rw-r--r--   3 root supergroup       5291 2017-06-23 13:54 /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1/bb71641556674349a0e8bef9dce28010
-rw-r--r--   3 root supergroup       5303 2017-06-23 13:54 /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1/c0db05c7db484c83b7eb264ba3681425
-rw-r--r--   3 root supergroup       5291 2017-06-23 13:54 /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1/d706103cac914c13aab37d8ffef0a524
-rw-r--r--   3 root supergroup       5291 2017-06-23 13:54 /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1/ea385acbdbfe4fdf8889ccafcaa4a2b7
[root@hdp-node-02 zookeeper]# 
```


## major合并

```
[root@hdp-node-02 zookeeper]# /bigdata_installed/hadoop/bin/hdfs dfs -ls /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1
-rw-r--r--   3 root supergroup       8575 2017-06-23 11:54 /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1/4c7d7a57f621416b9973ee60be4fe735
-rw-r--r--   3 root supergroup      45745 2017-06-23 11:54 /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1/6a4af798a81e42c2b2c9d9a2a474f008

#major合并
major_compact 'user'

#再次查看
[root@hdp-node-02 zookeeper]# /bigdata_installed/hadoop/bin/hdfs dfs -ls /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1
-rw-r--r--   3 root supergroup      43689 2017-06-23 11:59 /hbase/data/default/user/e0e3ab04968fda09964758a0ededb14e/info1/2d00054a49924bb799e7fde723682da4

```













参考：
http://hbasefly.com/2016/07/13/hbase-compaction-1/


