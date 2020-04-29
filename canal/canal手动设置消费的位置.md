
有时，我们线上的canal是启动了很长一段时间了，但是我们新启动一个实例去消费的时候，就会从最老的binlog去消费，那么此时就会有很多的binlog等待去消费，而此时我们希望从最近的binlog去消费，于是就有了手动设置消费的binlog位置的需求，具体的操作如下：

# 1.停止客户端的消费者

如果你的客户端是一个工程，那么停掉工程即可，如果你的客户端是一个程序，那么需要停掉程序


# 2.删除zk上实例的消费位置记录

如果是新建的实例，没有消费记录，不用删除

```
[zk: localhost:2181(CONNECTED) 13] ls  /otter/canal/destinations/example/1001        
[cursor, filter]

#删除消费位置
[zk: localhost:2181(CONNECTED) 14] rmr  /otter/canal/destinations/example/1001/cursor
[zk: localhost:2181(CONNECTED) 15] ls  /otter/canal/destinations/example/1001        
[filter]

```

# 3.查看需要消费的binlog

一般在canal中有一个消费者在消费着，这时我们新创建的实例需要从老实例的消费位置开始消费，那么就需要知道老实例的现在的消费位置，下面是的老实例是4solr

```
[zk: localhost:2181(CONNECTED) 1] get  /otter/canal/destinations/4solr/1001/cursor
{"@type":"com.alibaba.otter.canal.protocol.position.LogPosition","identity":{"slaveId":-1,"sourceAddress":{"address":"127.0.0.1","port":3306}},"postion":{"included":false,"journalName":"mysql-bin.000008","position":3122,"timestamp":1502683336000}}

```

# 4.修改配置文件

修改配置文件中消费的起始位置，那么会在客户端重新消费的时候，在zk下重新生成一个cursor

```
#vim instance.properties 

#################################################
## mysql serverId
canal.instance.mysql.slaveId = 1234

# position info 重新设置起始的消费位置
canal.instance.master.address = 127.0.0.1:3306
canal.instance.master.journal.name = mysql-bin.000008 
canal.instance.master.position = 3122
canal.instance.master.timestamp = 1502683336000

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
~                                                      
```


# 5.重新启动客户端，查看zk下的消费记录

```
[zk: localhost:2181(CONNECTED) 20] ls  /otter/canal/destinations/example/1001
[cursor, running, filter]

#查看消费的位置
[zk: localhost:2181(CONNECTED) 22] get /otter/canal/destinations/example/1001/cursor
{"@type":"com.alibaba.otter.canal.protocol.position.LogPosition","identity":{"slaveId":-1,"sourceAddress":{"address":"127.0.0.1","port":3306}},"postion":{"included":false,"journalName":"mysql-bin.000009","position":1186,"timestamp":1502786711000}}

```


