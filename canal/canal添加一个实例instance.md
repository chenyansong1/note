
直接才conf目录下添加一个实例，都不用重启canal的，牛，然后进入zookeeper中destination下，就可以看到添加的实例，下面是具体的操作步骤：

* 新建是一个实例

```
[root@hdp-node-02 conf]# cd /base_installed/canal/conf/
[root@hdp-node-02 conf]# ll
total 24
-rwxrwxrwx 1 root root 2536 Aug  2 15:10 canal.properties
drwxrwxrwx 2 root root 4096 Aug 15 14:40 example
-rwxrwxrwx 1 root root 3055 Jun 30  2015 logback.xml
drwxrwxrwx 2 root root 4096 Aug 15 14:02 spring


[root@hdp-node-02 conf]# mkdir 4solr
[root@hdp-node-02 conf]# cp example/instance.properties 4solr/

```


* 编辑配置文件

vim 4solr/instance.properties 

```
## mysql serverId
canal.instance.mysql.slaveId = 1234

# 这里配置消费的起始位置，可以参见：手动设置实例的消费位置
canal.instance.master.address = 127.0.0.1:3306
canal.instance.master.journal.name =
canal.instance.master.position =
canal.instance.master.timestamp =

#canal.instance.standby.address = 
#canal.instance.standby.journal.name =
#canal.instance.standby.position = 
#canal.instance.standby.timestamp = 

# username/password
canal.instance.dbUsername = canal
canal.instance.dbPassword = canal
canal.instance.defaultDatabaseName =
canal.instance.connectionCharset = UTF-8

# table regex
canal.instance.filter.regex = .*\\..*
# table black regex
canal.instance.filter.black.regex =

#################################################

```
* 客户端消费实例

参见：[canal的简单API使用](https://github.com/belongtocys/notebook/blob/master/canal/canal%E7%9A%84%E7%AE%80%E5%8D%95API%E4%BD%BF%E7%94%A8.md)

* 在zk中查看实例

```
#查看所有的实例
[zk: localhost:2181(CONNECTED) 2] ls /otter/canal/destinations      
[4solr, example]

#实例下有一个消费者1001
[zk: localhost:2181(CONNECTED) 3] ls /otter/canal/destinations/4solr
[cluster, 1001, running]

#消费者下记录是：游标（消费的位置），过滤器
[zk: localhost:2181(CONNECTED) 4] ls /otter/canal/destinations/4solr/1001
[cursor, filter]

#查看消费的位置
[zk: localhost:2181(CONNECTED) 6] get  /otter/canal/destinations/4solr/1001/cursor
{"@type":"com.alibaba.otter.canal.protocol.position.LogPosition","identity":{"slaveId":-1,"sourceAddress":{"address":"127.0.0.1","port":3306}},"postion":{"included":false,"journalName":"mysql-bin.000008","position":3122,"timestamp":1502683336000}}

```


如果要手动改变实例的消费位置，参见:[手动设置实例的消费位置](https://github.com/belongtocys/notebook/blob/master/canal/canal%E6%89%8B%E5%8A%A8%E8%AE%BE%E7%BD%AE%E6%B6%88%E8%B4%B9%E7%9A%84%E4%BD%8D%E7%BD%AE.md)






