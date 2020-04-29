> 常用的shell操作：



安装：
http://www.cnblogs.com/hyl8218/p/5537489.html
http://www.cnblogs.com/hyl8218/p/5537489.htmls
http://www.open-open.com/lib/view/open1451574710511.html
http://www.centoscn.com/image-text/install/2015/0918/6190.html
http://blog.csdn.net/cheng830306/article/details/50609221


集群启动：
http://qindongliang.iteye.com/blog/2275990
zk节点不存在的问题：
http://www.voidcn.com/blog/lsjlnd/article/p-6041052.html

```
依次在每台机器上执行命令启动节点： 
/bigdata_installed/solr-5.5.0/bin/solr -c -z hdp-node-01:2181,hdp-node-02:2181,hdp-node-03:2181/solrcloud  #如果solrcloud不存在，那么在zk上面手动创建它 

//创建一个名叫big_search的collection，它的shard是3，副本数是2（包括自身也是一个副本）  
bin/solr create_collection -c  big_search -d server/solr/configsets/data_driven_schema_configs/conf/ -shards 3 -replicationFactor 2  

solrcloud中是根据id来判断一条记录是否唯一的，

/bigdata_installed/solr-5.5.0/
2. 通过bin/solr脚本创建一个collection时
bin/solr create -c big_search -d /bigdata_installed/solr-5.5.0/solrconfig-by-self
#-c 指定collection名称, -d 指定configset的目录路径
#此configset被上传到ZooKeeper的/configs/mycollection目录下

bin/solr delete -c big_search


```





soft commit 和hard soft问题



http://blog.csdn.net/oliverkehl/article/details/51801311


----------

http://blog.csdn.net/jediael_lu/article/details/76525755

http://violetgo.com/blogs/2013/11/24/solr_research-doc.html
http://blog.csdn.net/jediael_lu/article/details/76525755

http://www.cnblogs.com/lykm02/p/4056449.html

API：http://blog.csdn.net/wzb56_earl/article/details/7819723
可以在程序中计数，当数量达到一定时就提交
可以在程序中计时，当时间达到一定时就提交

---


soft commit and hard commit 
https://my.oschina.net/u/3240397/blog/831282
https://www.zhihu.com/question/20879818
http://san-yun.iteye.com/blog/2024386
http://www.jianshu.com/p/6ba7af1931ec

solr有commit与softcommit 实时


kibana的使用:可以实时的看到某个appkey发送过来的数据，如果想要看某个事件下的数据，其中有个字段aplan_eid 可以看是哪个事件发送过来的数据，发送了哪些字段

_index:event-5dd45f43ae732a4a8babe17aa966dc3c9a5e

appkey=5dd45f43ae732a4a8babe17aa966dc3c9a5e



#udtf
http://www.cnblogs.com/ggjucheng/archive/2013/02/01/2888819.html
http://www.cnblogs.com/zhangshihai1232/articles/6182650.html
http://www.cnblogs.com/longjshz/p/5488748.html
这篇说明了输入参数和输出参数
http://blog.csdn.net/u013668852/article/details/72569961
在map类型的输入的时候，需要类型转换，参见我自定的udf:UDFMAPDetect4Search

udf:http://blog.csdn.net/ruidongliu/article/details/8791865



hive case :http://blog.csdn.net/longerandlonger/article/details/8755395

shell 判断文件是否为空：http://www.letuknowit.com/topics/20120402/linux-shell-test-command.html/

1.只能查询昨天的数据，因为算新增用户的时候算的昨天的，所以这边只能拿到昨天的新增用户
2.需要一些测试的数据，是新用户，我这边判断新用户是根据cookieid（web),machineid(app),web端可以通过清cookie来模拟新用户，但是app的新用户不知道怎么模拟


solr默认会有一个id字段用来标识一条记录，但是如果你插入的数据中没有id的话，或者id不是小写的话，就会有这个默认的字段
但是如果你指定了id（小写)的话，那么再进行插入，就会覆盖原来id相同的记录



对于大量数据，近实时，别人是怎么做到的，看下

在工程中，有多个jar冲突的问题，还就就是添加了其他依赖的jar

rz -bey
