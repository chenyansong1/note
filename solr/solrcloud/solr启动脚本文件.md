
以下整理自：http://lucene.apache.org/solr/guide/6_6/solr-control-script-reference.html
**注意：version=6.6，有些脚本在5.x中不行**

/bin/solr的脚本文件有以下作用：

* start solr
* stop solr
* create and delete collections(cores)
* perform operations on zk
* check the status of Solr
* configured shards


## 启动

```
bin/solr start [options]
bin/solr start -help

bin/solr restart [options]
bin/solr restart -help

```

|参数|说明|示例|
|----|----|----|
|-a "string"|添加JVM参数，以-X开始，如果是以-D开始的，那么可以省略-a|bin/solr start -a "-Xdebug -Xrunjdwp:transport=dt_socket, server=y,suspend=n,address=1044"|
|-cloud|Start Solr in SolrCloud mode,会启动一个内嵌的zk,可以使用-z去指定外部存在的zk|bin/solr start -c <br/>or<br/> bin/solr start -c -z zk:2181|
|-d dir|指定solr服务目录，默认是在：$SOLR_HOME/server|bin/solr start -d newServerDir|
|-e|Start Solr with an example configuration The available options are:(cloud，techproducts，dih，schemaless)|bin/solr start -e schemaless|
|-f|前台运行，不能和-e一起使用|bin/solr start -f|
|-h hostname|Start Solr with the defined hostname. If this is not specified, 'localhost' will be assumed.|bin/solr start -h search.mysolr.com|
|-m memory|Start Solr with the defined value as the min (-Xms) and max (-Xmx) heap size for the JVM.|bin/solr start -m 1g|
|-p port|Start Solr on the defined port. If this is not specified, '8983' will be used.|bin/solr start -p 8655|
|-s dir|Sets the solr.solr.home system property;The default value is server/solr,This parameter is ignored when running examples (-e)|bin/solr start -s newHome|
|-v|Be more verbose. This changes the logging level of log4j from INFO to DEBUG, having the same effect as if you edited log4j.properties accordingly.|bin/solr start -f -v|
|-q|Be more quiet. This changes the logging level of log4j from INFO to WARN, having the same effect as if you edited log4j.properties accordingly. This can be useful in a production setting where you want to limit logging to warnings and errors.|bin/solr start -f -q|
|-z zkhost|**Start Solr with the defined ZooKeeper connection string. This option is only used with the -c option, to start Solr in SolrCloud mode. If this option is not provided, Solr will start the embedded ZooKeeper instance and use that instance for SolrCloud operations.**|bin/solr start -c -z server1:2181,server2:2181|


## 设置Java系统属性

The bin/solr script will pass any additional parameters that begin with -D to the JVM, which allows you to set arbitrary Java system properties.

For example, to set the auto soft-commit frequency to 3 seconds, you can do:
```
bin/solr start -Dsolr.autoSoftCommit.maxTime=3000
```


## SolrCloud Mode

下面两种方式是等价的
```
bin/solr start -c
bin/solr start -cloud
```

如果你指定了-z参数，如下：
```
#如果指定了根节点，那么在启动solr之前，需要在zookeeper中手动的创建根节点，如下
bin/solr zk mkroot /solr -z 192.168.1.4:2181
bin/solr start -c -z 192.168.1.4:2181/solr
```

会使用外部的zookeeper，如果没有使用-z去指定参数，那么会使用内嵌的zookeeper            
> If you do not specify the -z option when starting Solr in cloud mode, then Solr will launch an embedded ZooKeeper server listening on the Solr port + 1000, i.e., if Solr is** running on port 8983, then the embedded ZooKeeper will be listening on port 9983**.



> 还有一点是关于solrcloud在zookeeper中创建节点的问题：如果连接zookeeper的时候指定的是host:port/solrcloud (像这样在port之后进行了指定字符串名称solrcloud),那么我们需要在zookeeper中进行手动的创建这个节点（这是一个坑，之前一直没有指定成功，就是这个问题),
> * If your ZooKeeper connection string uses a chroot, such as localhost:2181/solr, **then you need to create the /solr znode before launching SolrCloud** using the bin/solr script.
> * To do this use the **mkroot command** outlined below, for example: bin/solr zk mkroot /solr -z 192.168.1.4:2181


## 停止命令

```
bin/solr stop [options]
bin/solr stop -help

#下面是一些可选的参数
-p <port>	 Stop Solr running on the given port. 如果你运行了多个实例，那么可以指定一个端口说明你要停止哪一个实例，或者你使用-all来停止所有的实例. bin/solr stop -p 8983
-all		
-k <key>		Stop key used to protect from stopping Solr inadvertently; default is "solrrocks". eg: bin/solr stop -k solrrocks
```

## 查看系统信息

* 版本信息
```
bin/solr -version
```

* 状态信息

```
[root@hdp-node-02 bin]# ./solr status

Found 1 Solr nodes: 

Solr process 2280 running on port 8983
{
  "solr_home":"/bigdata_installed/solr-5.5.0/server/solr",
  "version":"5.5.0 2a228b3920a07f930f7afb6a42d0d20e184a943c - mike - 2016-02-16 15:22:52",
  "startTime":"2017-09-01T03:08:46.26Z",
  "uptime":"0 days, 4 hours, 32 minutes, 11 seconds",
  "memory":"26.7 MB (%5.4) of 490.7 MB",
  "cloud":{
    "ZooKeeper":"hdp-node-01:2181,hdp-node-02:2181,hdp-node-03:2181/solrcloud",
    "liveNodes":"3",
    "collections":"3"}}
```

* 健康检查

```
bin/solr healthcheck [options]
bin/solr healthcheck -help

#-c 指定collection的名字，-z指定zookeeper（注意如果有根节点，需要加上根节点）
[root@hdp-node-02 solr-5.5.0]# bin/solr healthcheck -c gettingstarted -z localhost:2181/solrcloud
{
  "collection":"gettingstarted",
  "status":"healthy",
  "numDocs":32,
  "numShards":1,
  "shards":[{
      "shard":"shard1",
      "status":"healthy",
      "replicas":[{
          "name":"core_node1",
          "url":"http://192.168.153.201:8983/solr/gettingstarted_shard1_replica1/",
          "numDocs":32,
          "status":"active",
          "uptime":"0 days, 4 hours, 37 minutes, 10 seconds",
          "memory":"30 MB (%6.1) of 490.7 MB",
          "leader":true}]}]}

```


## Collections and Cores

The bin/solr script can also help you create new collections (in SolrCloud mode) or cores (in standalone mode), or delete collections.

### Create

create 命令能够去检查solr的运行模式，如果是standalone模式，就去create core ,如果是solrcloud模式，就去创建collection

```
bin/solr create [options]
bin/solr create -help
```

下面是create中的一些可选的参数

![](/images/solr/solrcloud/shell/solr_create_params.jpg)


* 配置文件


这里需要注意的是当我们创建collection的时候，会使用配置去创建，那么默认的配置文件如下：
> First, if you don’t provide the -d (配置文件目录) or -n （配置名字） options, then the default configuration 

```
/bigdata_installed/solr-5.5.0/server/solr/configsets/data_driven_schema_configs/conf

[root@hdp-node-02 conf]# pwd
/bigdata_installed/solr-5.5.0/server/solr/configsets/data_driven_schema_configs/conf
[root@hdp-node-02 conf]# ll
total 144
-rw-r--r-- 1 root root  3974 Jan 25  2016 currency.xml
-rw-r--r-- 1 root root  1348 Jan 25  2016 elevate.xml
drwxr-xr-x 2 root root  4096 Aug  6 22:04 lang
-rw-r--r-- 1 root root 54490 Jan 25  2016 managed-schema
-rw-r--r-- 1 root root   308 Jan 25  2016 params.json
-rw-r--r-- 1 root root   873 Jan 25  2016 protwords.txt
-rw-r--r-- 1 root root 60567 Feb 13  2016 solrconfig.xml
-rw-r--r-- 1 root root   781 Jan 25  2016 stopwords.txt
-rw-r--r-- 1 root root  1119 Jan 25  2016 synonyms.txt
```

如果在创建collection的时候，没有指定配置的目录，或者配置的名字，那么会使用上述目录下的配置文件**上传到zookeeper**，然后在zookeeper节点的名字就是/solrcloud/configs/gettingstarted,**zk节点的名字会以collection的名字来命名**， 如下：

下面使用例子来说明：
创建了一个collection
```
bin/solr create -c gettingstarted

```
在zookeeper中观察配置文件的节点：
```
[zk: localhost:2181(CONNECTED) 3] ls /solrcloud/configs
[gettingstarted, big_search2, big_search3]
[zk: localhost:2181(CONNECTED) 4] ls /solrcloud/configs/gettingstarted
[currency.xml, protwords.txt, synonyms.txt, params.json, elevate.xml, solrconfig.xml, stopwords.txt, lang, managed-schema]
[zk: localhost:2181(CONNECTED) 5] 
```

在webui中也可以看到：

![](/images/solr/solrcloud/shell/create_config.jpg)

也就是说，在每个collection中，都会生成默认的配置文件，这些配置文件在zookeeper中可以看到，然后就是这个配置文件属于特定的collection，如果没有为这些配置文件命名，那么默认会以collection的名字来为zookeeper下的该collection配置文件命名

You can override the name given to the configuration directory in ZooKeeper by using the -n option. For instance, the command bin/solr create -c logs -d basic_configs -n basic will upload the server/solr/configsets/basic_configs/conf directory to ZooKeeper as /configs/basic.


上传配置文件的时候，指定配置文件的名字，这样在zookeeper中config目录下生成的就是指定的配置文件的名字，而不是默认的collection的名字
```
bin/solr create -c logs -d basic_configs -n basic 
#将上传/solr/configsets/basic_configs/conf 目录到 ZooKeeper 生成的节点是 /solrcloud/configs/basic.
```

共享配置文件,如果在创建collection的时候指定的配置name是已经存在的，那么该collection将会和已有的collection共享配置，不过并不建议这么做

```
bin/solr create -c logs2 -n basic
```


## delete

```
bin/solr delete [options]
bin/solr delete -help
```

* Deletes a core or collection depending on whether Solr is running in standalone (core) or SolrCloud mode (collection). If you're deleting a collection in SolrCloud mode, the default behavior is to also delete the configuration directory from Zookeeper so long as it is not being used by another collection. You can override this behavior by passing -deleteConfig false when running this command.

如果是solrcloud模式，删除collection的同时会删除zookeeper上的配置文件，但是如果你的另外一个collection也是引用的该配置文件，那么就不会删除，想要避免这种问题，那么使用-deleteConfig false 

![](/images/solr/solrcloud/shell/solr_delete1.jpg)


## zk操作

* 上传和下载配置文件

```
bin/solr zk [sub-command] [options]

bin/solr zk -help

Usage: solr zk [-upconfig|-downconfig] [-d confdir] [-n configName] [-z zkHost]

     -upconfig to move a configset from the local machine to Zookeeper.

     -downconfig to move a configset from Zookeeper to the local machine.

     -n configName    Name of the configset in Zookeeper that will be the destinatino of
                       'upconfig' and the source for 'downconfig'.

     -d confdir       The local directory the configuration will be uploaded from for
                      'upconfig' or downloaded to for 'downconfig'. For 'upconfig', this
                      can be one of the example configsets, basic_configs, data_driven_schema_configs or
                      sample_techproducts_configs or an arbitrary directory.

     -z zkHost        Zookeeper connection string.

  NOTE: Solr must have been started least once (or have it running) before using this command.
        This initialized Zookeeper for Solr

```

对于upconfig配置，需要在指定的目录中


上传文件
bin/solr zk -upconfig

```
#上传文件
touch /tmp/test/solrconfig.txt 
bin/solr zk -upconfig -z localhost:2181/solrcloud -n mynewconfig_test -d /tmp/test/

#查看zookeeper
[zk: localhost:2181(CONNECTED) 0] ls /solrcloud/configs
[mynewconfig_test, gettingstarted, big_search2, big_search3]
[zk: localhost:2181(CONNECTED) 0] ls /solrcloud/configs/mynewconfig_test
[solrconfig.xml]
```

需要注意的问题：在指定的上传目录中需要有solrconfig.xml名字的文件

```
ERROR: Specified configuration directory /tmp/test is invalid;
it should contain either conf sub-directory or solrconfig.xml
```

下载文件,将下载的文件放入指定的目录中
bin/solr zk -downconfig
```
[root@hdp-node-02 solr-5.5.0]# bin/solr zk -downconfig -z localhost:2181/solrcloud -n mynewconfig_test -d /tmp/ 
Connecting to ZooKeeper at localhost:2181/solrcloud ...
Downloading configset mynewconfig_test from ZooKeeper at localhost:2181/solrcloud to directory /tmp/conf

[root@hdp-node-02 solr-5.5.0]# ll /tmp/conf/
-rw-r--r-- 1 root root 0 Sep  1 17:39 solrconfig.xml
```

建议将配置文件加入到统一的版本管理中，这样就不用去下载了

* 本地文件和zk节点之间的copy

本地文件和目录，zk节点之间的转换
从本地到zookeeper，从zookeeper到本地，从zookeeper到zookeeper（This command will copy from the local drive to ZooKeeper, from ZooKeeper to the local drive or from ZooKeeper to ZooKeeper）

![](/images/solr/solrcloud/shell/solr_cp.jpg)

```
Recursively copy a directory from local to ZooKeeper.
#递归拷贝目录
bin/solr zk cp -r file:/apache/confgs/whatever/conf zk:/configs/myconf -z 111.222.333.444:2181

Copy a single file from ZooKeeper to local.
#拷贝单个文件
bin/solr zk cp zk:/configs/myconf/managed_schema /configs/myconf/managed_schema -z 111.222.333.444:2181

```

* 从zk中移除节点

![](/images/solr/solrcloud/shell/solr_rm.jpg)

```
bin/solr zk rm -r /configs

bin/solr zk rm /configs/myconfigset/schema.xml
```

* Move One ZooKeeper znode to Another (Rename)

![](/images/solr/solrcloud/shell/solr_mv.jpg)

```
bin/solr zk mv /configs/oldconfigset /configs/newconfigset
```

* 创建zk节点

```
bin/solr zk mkroot /solr -z 123.321.23.43:2181
bin/solr zk mkroot /solr/production
```




















