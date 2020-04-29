# 安装

未

# solr的web界面说明

![](/images/solr/solrcloud/solr-web-01.png)
![](/images/solr/solrcloud/solr-web-02.png)
![](/images/solr/solrcloud/solr-web-03.png)
![](/images/solr/solrcloud/solr-web-04.png)
![](/images/solr/solrcloud/solr-web-05.png)




# 中文分词

未

# solrcloud原理介绍

http://josh-persistence.iteye.com/blog/2234411


# 启动


```
bin/solr start [options]
bin/solr start -help
bin/solr restart [options]
bin/solr restart -help


bin/solr start -cloud 
//bin/solr start -c

//如果使用zk作为管理器
bin/solr start -c -z 192.168.1.4:2181


```

参见:http://lucene.apache.org/solr/guide/6_6/solr-control-script-reference.html#SolrControlScriptReference-RunningwithExampleConfigurations



zk启动

```
依次在每台机器上执行命令启动节点： 
/bigdata_installed/solr-5.5.0/bin/solr -c -z hdp-node-01:2181,hdp-node-02:2181,hdp-node-03:2181/solrcloud  #如果solrcloud不存在，那么在zk上面手动创建它 

//创建一个名叫big_search的collection，它的shard是3，副本数是2（包括自身也是一个副本）  
bin/solr create_collection -c  big_search -d server/solr/configsets/data_driven_schema_configs/conf/ -shards 3 -replicationFactor 2  

#另外一种创建方式：
http://192.168.1.199:28000/solr/admin/collections?action=CREATE&name=blog&numShards=2&collection.configName=blog&replicationFactor=3&maxShardsPerNode=1


solrcloud中是根据id来判断一条记录是否唯一的，

/bigdata_installed/solr-5.5.0/
2. 通过bin/solr脚本创建一个collection时
bin/solr create -c big_search -d /bigdata_installed/solr-5.5.0/solrconfig-by-self
#-c 指定collection名称, -d 指定configset的目录路径
#此configset被上传到ZooKeeper的/configs/mycollection目录下

bin/solr delete -c big_search

```

添加一个新的节点

```
mkdir <solr.home for new solr node>
cp <existing solr.xml path> <new solr.home>
bin/solr start -cloud -s solr.home/solr -p <port num> -z <zk hosts string>

#Example
mkdir -p example/cloud/node3/solr
cp server/solr/solr.xml example/cloud/node3/solr
bin/solr start -cloud -s example/cloud/node3/solr -p 8987 -z localhost:9983
```



参考：

http://lucene.apache.org/solr/guide/6_6/running-solr.html#RunningSolr-SolrScriptOptions		












