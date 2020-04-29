# 安装

需要到[这里](http://lucene.apache.org/solr/)下载

```
#安装到指定的目录xxx
tar zxf solr-x.y.z.tgz -C /xxx 
```

# 启动命令

```
#帮助命令
bin/solr -help

#启动帮助命令
bin/solr start -help

#启动，运行在前台（默认是运行在后台）
bin/solr start -f

#启动指定端口
bin/solr start -p 8984

```

# 停止命令

```
#停止指定端口的服务
bin/solr stop -p 8983

#如果运行在前台（-f），那么使用Ctrl-c就可以停止，如果运行在后台需要使用stop，并且指定端口去指明停止哪个实例，也可以使用-all去停止所有的实例
#停止所有的solr服务
bin/solr stop -all
```

# 状态检查

```
bin/solr status
```

# 访问web界面
```
http://localhost:8983/solr/
```

# Create a Core/collection

```
#下面是创建的帮助文档
bin/solr create -help
Usage: solr create [-c name] [-d confdir] [-n configName] [-shards #] [-replicationFactor #] [-p port]

  Create a core or collection depending on whether Solr is running in standalone (core) or SolrCloud
  mode (collection). In other words, this action detects which mode Solr is running in, and then takes
  the appropriate action (either create_core or create_collection). For detailed usage instructions, do:

    bin/solr create_core -help
       or
    bin/solr create_collection -help

#solr会根据运行的模式（单机或者集群），选择对应的创建选项（create_core or create_collection），

#通过bin/solr脚本创建一个collection时
bin/solr create -c big_search -d /bigdata_installed/solr-5.5.0/solrconfig-by-self
#-c 指定collection名称, -d 指定configset的目录路径


#create core(if solr mode is standalone)
bin/solr create -c <name>

#create collection(if solr mode is SolrCloud mode)
bin/solr create -c <name>

```
创建core的时候，使用的是/bigdata_installed/solr-5.5.0/server/solr/configsets/data_driven_schema_configs 目录写的conf配置文件进行的创建，如果你使用了zk去管理配置文件可以在zk的目录下看到配置如下：

```
#每个core的配置文件
[zk: localhost:2181(CONNECTED) 0] ls /solrcloud/configs
[big_search2, big_search3]

#某个core的配置文件
[zk: localhost:2181(CONNECTED) 1] ls /solrcloud/configs/big_search3  
[solrconfig.xml, schema.xml]

```
# delete collection 

方式一
```
http://192.168.153.202:8983/solr/admin/collections?action=DELETE&name=blog

```

方式二

```
1)documents type 选择 XML 
2)documents 输入下面语句
<delete><query>*:*</query></delete>
<commit/>

```


# Add Documents

下面是solr的使用post工具去添加doc的例子
```
#post help
bin/post -help

#添加之前solr的core或者collection要已经被创建
bin/solr create -c gettingstarted
bin/post -c gettingstarted example/exampledocs/*.xml


#可以添加各种类型的doc(xml,json,cvs)
JSON file: bin/post -c wizbang events.json
XML files: bin/post -c records article*.xml
CSV file: bin/post -c signals LATEST-signals.csv

```


# 查询

接上面的例子，下面是一个简单的查询

the following query searches all document fields for "video"
```
http://localhost:8983/solr/gettingstarted/select?q=video

```

返回文档的格式

![](/note/images/solr/solrcloud/shell/query_1.jpg)


返回的文档只是包含指定的字段
```
http://localhost:8983/solr/gettingstarted/select?q=video&fl=id,name,price
```

在指定查询的字段
```
http://localhost:8983/solr/gettingstarted/select?q=name:black
```

指定查询的范围
The following query finds every document whose price is between $0 and $400.
```
#%20 表示$符号
http://localhost:8983/solr/gettingstarted/select?q=price:0%20TO%20400&fl=id,name,price

```

分面查询

```
#facet=true表示开启分面，facet.field=cat 指定分面字段
http://localhost:8983/solr/gettingstarted/select?q=price:0%20TO%20400&fl=id,name,price&facet=true&facet.field=cat
```
下面可以看到分面的查询结果:

![](/note/images/solr/solrcloud/shell/query_faset.jpg)

查询某个的分面下的结果

```
http://localhost:8983/solr/gettingstarted/select?q=price:0%20TO%20400&fl=id,name,price&facet=true&facet.field=cat&fq=cat:software
```

参见：http://lucene.apache.org/solr/guide/6_6/running-solr.html#RunningSolr-AddDocuments




# 健康检查

```
[root@hdp-node-03 solr-5.5.0]# ./bin/solr healthcheck -c big_search2 -z localhost:2181/solrcloud
```




























2. 创建一个collection并上传关联配置文件至Zookeeper。

./bin/solr create_collection -c students -d server/solr/configsets/sample_techproducts_configs/conf -shards 3 -replicationFactor 3



测试完之后可以删除Collection

http://node1:8983/solr/admin/collections?action=DELETE&name=students
