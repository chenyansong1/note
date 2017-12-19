# 1.配置说明

![](/images/canal/config_file.jpg)


canal配置方式有两种：

* ManagerCanalInstanceGenerator： 基于manager管理的配置方式，目前alibaba内部配置使用这种方式。大家可以实现CanalConfigClient，连接各自的管理系统，即可完成接入。
* SpringCanalInstanceGenerator：基于本地spring xml的配置方式，目前开源版本已经自带该功能所有代码，建议使用



# 2.Spring配置

spring配置的原理是将整个配置抽象为两部分：

* xxxx-instance.xml (canal组件的配置定义，可以在多个instance配置中共享)
* xxxx.properties (每个instance通道都有各自一份定义，因为每个mysql的ip，帐号，密码等信息不会相同)
通过spring的PropertyPlaceholderConfigurer通过机制将其融合，生成一份instance实例对象，每个instance对应的组件都是相互独立的，互不影响


# 3.properties配置文件

properties配置分为两部分：

* canal.properties (系统根配置文件)
* instance.properties (instance级别的配置文件，每个instance一份)


## 3.1.canal.properties介绍


canal配置主要分为两部分定义： 

1.instance列表定义 (列出当前server上有多少个instance，每个instance的加载方式是spring/manager等)


|参数名字								|参数说明							|默认值|
| ------------------------------------- |-----------------------------------|--------------------|
|canal.destinations						|当前server上部署的instance列表		|无|
|canal.conf.dir							|conf/目录所在的路径				|	../conf|
|canal.auto.scan						|开启instance自动扫描,如果配置为true，canal.conf.dir目录下的instance配置变化会自动触发：a. instance目录新增： 触发instance配置载入，lazy为true时则自动启动;b. instance目录删除：卸载对应instance配置，如已启动则进行关闭;c. instance.properties文件变化：reload instance配置，如已启动自动进行重启操作 | true|
|canal.auto.scan.interval				|instance自动扫描的间隔时间，单位秒	|5|
|canal.instance.global.mode				|全局配置加载方式					|spring|
|canal.instance.global.lazy				|全局lazy模式						|false|
|canal.instance.global.manager.address	|全局的manager配置方式的链接信息	|无|
|canal.instance.global.spring.xml		|全局的spring配置方式的组件文件		|classpath:spring/file-instance.xml (spring目录相对于canal.conf.dir) |
|canal.instance.example.mode;canal.instance.example.lazy;canal.instance.example.spring.xml;….. |	instance级别的配置定义，如有配置，会自动覆盖全局配置定义模式,命名规则:canal.instance.{name}.xxx	|无|



2.common参数定义，比如可以将instance.properties的公用参数，抽取放置到这里，这样每个instance启动的时候就可以共享. 【instance.properties配置定义优先级高于canal.properties】


|参数名字									|参数说明																			|默认值 |   
|-------------------------------------------|---------------------------------------------------------------------------------  |------|
|canal.id									|每个canal server实例的唯一标识，暂无实际意义										|1      |
|canal.ip									|canal server绑定的本地IP信息，如果不配置，默认选择一个本机IP进行启动服务			|无     |
|canal.port									|canal server提供socket服务的端口													|11111  |
|canal.zkServers							|	canal server链接zookeeper集群的链接信息;例子：127.0.0.1:2181,127.0.0.1:2182		|无     |
|canal.zookeeper.flush.period				|canal持久化数据到zookeeper上的更新频率，单位毫秒									|1000   |
|canal.file.data.dir						|	canal持久化数据到file上的目录													|../conf (默认和instance.properties为同一目录，方便运维和备份)|
|canal.file.flush.period					|	canal持久化数据到file上的更新频率，单位毫秒										|1000|
|canal.instance.memory.batch.mode			|canal内存store中数据缓存模式:1. ITEMSIZE : 根据buffer.size进行限制，只限制记录的数量;2. MEMSIZE : 根据buffer.size * buffer.memunit的大小，限制缓存记录的大小   |MEMSIZE|
|canal.instance.memory.buffer.size			|canal内存store中可缓存buffer记录数，需要为2的指数								|16384|
|canal.instance.memory.buffer.memunit		|内存记录的单位大小，默认1KB，和buffer.size组合决定最终的内存使用大小			|1024|
|canal.instance.transactionn.size			|最大事务完整解析的长度支持超过该长度后，一个事务可能会被拆分成多次提交到canal store中，无法保证事务的完整可见性	|	1024|
|canal.instance.fallbackIntervalInSeconds	|canal发生mysql切换时，在新的mysql库上查找binlog时需要往前查找的时间，单位秒,说明：mysql主备库可能存在解析延迟或者时钟不统一，需要回退一段时间，保证数据不丢|	60|
|canal.instance.detecting.enable			|	是否开启心跳检查	|false																	|			
|canal.instance.detecting.sql				|心跳检查sql			|insert into retl.xdual values(1,now()) on duplicate key update x=now() |
|canal.instance.detecting.interval.time		|心跳检查频率，单位秒	|3                                                                      |
|canal.instance.detecting.retry.threshold	|心跳检查失败重试次数	|3                                                                      |
|canal.instance.detecting.heartbeatHaEnable	|心跳检查失败后，是否开启自动mysql自动切换,说明：比如心跳检查失败超过阀值后，如果该配置为true，canal就会自动链到mysql备库获取binlog数据	|false|
|canal.instance.network.receiveBufferSize	|网络链接参数，SocketOptions.SO_RCVBUF				|16384 |
|canal.instance.network.sendBufferSize		|网络链接参数，SocketOptions.SO_SNDBUF				|16384 |
|canal.instance.network.soTimeout			|网络链接参数，SocketOptions.SO_TIMEOUT				|30    |
|canal.instance.filter.query.dcl			|是否忽略DCL的query语句，比如grant/create user等	|false |
|canal.instance.filter.query.dml			|是否忽略DML的query语句，比如insert/update/delete table.(mysql5.6的ROW模式可以包含statement模式的query记录)	|	false |
|canal.instance.filter.query.ddl			|是否忽略DDL的query语句，比如create table/alater table/drop table/rename table/create index/drop index. (目前支持的ddl类型主要为table级别的操作，create databases/trigger/procedure暂时划分为dcl类型)	|false|
|canal.instance.get.ddl.isolation			|ddl语句是否隔离发送，开启隔离可保证每次只返回发送一条ddl数据，不和其他dml语句混合返回.(otter ddl同步使用) |	false|


instance.properties介绍：

a. 在canal.properties定义了canal.destinations后，需要在canal.conf.dir对应的目录下建立同名的文件

比如：
```
canal.destinations = example1,example2
```

这时需要创建example1和example2两个目录，每个目录里各自有一份instance.properties. 
ps:canal自带了一份instance.properties demo，可直接复制conf/example目录进行配置修改

```
cp -R example example1/
cp -R example example2/
```

b. 如果canal.properties未定义instance列表，但开启了canal.auto.scan时


* server第一次启动时，会自动扫描conf目录下，将文件名做为instance name，启动对应的instance
* server运行过程中，会根据canal.auto.scan.interval定义的频率，进行扫描 
1.发现目录有新增，启动新的instance
2.发现目录有删除，关闭老的instance
3.发现对应目录的instance.properties有变化，重启instance 

一个标准的conf目录结果：

```
jianghang@jianghang-laptop:~/work/canal/deployer/target/canal$ ls -l conf/
总用量 8
-rwxrwxrwx 1 jianghang jianghang 1677 2013-03-19 15:03 canal.properties  ##系统配置
drwxr-xr-x 2 jianghang jianghang   88 2013-03-19 15:03 example  ## instance配置
-rwxrwxrwx 1 jianghang jianghang 1840 2013-03-19 15:03 logback.xml ## 日志文件
drwxr-xr-x 2 jianghang jianghang  168 2013-03-19 17:04 spring  ## spring instance模板

```

|数名字												|参数说明														|默认值			 |
| --------------------------------------------------|---------------------------------------------------------------| -------------- |
|canal.instance.mysql.slaveId						|mysql集群配置中的serverId概念，需要保证和当前mysql集群中id唯一	|1234            |
|canal.instance.master.address						|mysql主库链接地址												|127.0.0.1:3306  |
|canal.instance.master.journal.name					|mysql主库链接时起始的binlog文件								|	无           |
|canal.instance.master.position						|mysql主库链接时起始的binlog偏移量								|无              |
|canal.instance.master.timestamp					|	mysql主库链接时起始的binlog的时间戳							|	无           |
|canal.instance.dbUsername							|mysql数据库帐号												|	canal        |
|canal.instance.dbPassword							|mysql数据库密码												|	canal        |
|canal.instance.defaultDatabaseName					|mysql链接时默认schema											|无              |
|canal.instance.connectionCharset 					|mysql 数据解析编码												|UTF-8	         |
|canal.instance.filter.regex						|mysql 	数据解析关注的表，Perl正则表达式.多个正则之间以逗号(,)分隔，转义符需要双斜杠(\\) ;常见例子：1. 所有表：.* or .*\\..* ;2. canal schema下所有表： canal\\..* ;3. canal下的以canal打头的表：canal\\.canal.* ; 4. canal schema下的一张表：canal.test1 ;5. 多个规则组合使用：canal\\..*,mysql.test1,mysql.test2 (逗号分隔) ; 注意：此过滤条件只针对row模式的数据有效(ps. mixed/statement因为不解析sql，所以无法准确提取tableName进行过滤) |	.*\\..* |



下面是几点说明：

1.MySQL链接时的起始位置

* canal.instance.master.journal.name + canal.instance.master.position : 精确指定一个binlog位点，进行启动
* canal.instance.master.timestamp : 指定一个时间戳，canal会自动遍历mysql binlog，找到对应时间戳的binlog位点后，进行启动
* 不指定任何信息：默认从当前数据库的位点，进行启动。(show master status)

2.mysql解析关注表定义

* 标准的Perl正则，注意转义时需要双斜杠：\\

3.mysql链接的编码

* 目前canal版本仅支持一个数据库只有一种编码，如果一个库存在多个编码，需要通过filter.regex配置，将其拆分为多个canal instance，为每个instance指定不同的编码


参考：

http://blog.csdn.net/hyx1990/article/details/52524115
