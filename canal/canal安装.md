安装部署


# 1.开启binlog

开启MySQL的binlog功能，并配置binlog模式为row。

```
# vim /etc/my.cnf 

[mysqld]  
log-bin=mysql-bin #添加这一行就ok  
binlog-format=ROW #选择row模式  
server_id=1 #配置mysql replaction需要定义，不能和canal的slaveId重复 

```


# 2.在mysql配置canal用户

```
CREATE USER canal IDENTIFIED BY 'canal'; 
   
GRANT SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'canal'@'%';  
-- GRANT ALL PRIVILEGES ON *.* TO 'canal'@'%' ; 
 
FLUSH PRIVILEGES;  

```


# 2.部署canal

* 下载canal： https://github.com/alibaba/canal/releases  
* 加压 

```
tar -zxvf canal.deployer-1.0.20.tar.gz -C /base_installed
```

* canal的目录结构 

```
drwxr-xr-x 2 root root 4096 Aug 14 11:28 bin
drwxr-xr-x 6 root root 4096 Aug 14 15:36 conf
drwxr-xr-x 2 root root 4096 Aug  2 14:23 lib
drwxrwxrwx 6 root root 4096 Aug 14 15:33 logs

```

# 3.canal,mysql,canalclient的关系

如下图：

![](/images/canal/install.jpg)


原理相对比较简单：

1.canal模拟mysql slave的交互协议，伪装自己为mysql slave，向mysql master发送dump协议
2.mysql master收到dump请求，开始推送binary log给slave(也就是canal)
3.canal解析binary log对象(原始为byte流)


# 4.配置一个canal实例

在canal配置文件中默认有一个实例叫做：example，我们也可以定义自己的实例，然后将example下的instance.properties 文件拷贝到我们定义的实例下，修改对应的属性即可

```
vim /base_installed/canal/conf/example/instance.properties 
```

编辑下面的配置文件
```
#################################################
## mysql serverId
canal.instance.mysql.slaveId = 1234

# position info 这是数据库的连接信息，journal.name可以配置binlog的名字，开始的位置，时间戳等，如果配置了就从指定的位置进行消费
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
#canal.instance.filter.regex = test.canal_test4 #可以配置指定消费哪个库，哪个表
# table black regex
canal.instance.filter.black.regex =

#################################################

```

# 5.启动和关闭

进入bin目录下
```
#启动  
./startup.sh  
#停止
./stop.sh  

```

验证启动状态，查看log文件

```
#vim canal/log/canal/canal.log  
2014-07-18 10:21:08.525 [main] INFO  com.alibaba.otter.canal.deployer.CanalLauncher - ## start the canal server.  
2014-07-18 10:21:08.609 [main] INFO  com.alibaba.otter.canal.deployer.CanalController - ## start the canal server[10.12.109.201:11111]  
2014-07-18 10:21:09.037 [main] INFO  com.alibaba.otter.canal.deployer.CanalLauncher - ## the canal server is running now ...... 

```

具体instance的日志：

```
#vi logs/example/example.log

2013-02-05 22:50:45.636 [main] INFO  c.a.o.c.i.spring.support.PropertyPlaceholderConfigurer - Loading properties file from class path resource [canal.properties]
2013-02-05 22:50:45.641 [main] INFO  c.a.o.c.i.spring.support.PropertyPlaceholderConfigurer - Loading properties file from class path resource [example/instance.properties]
2013-02-05 22:50:45.803 [main] INFO  c.a.otter.canal.instance.spring.CanalInstanceWithSpring - start CannalInstance for 1-example 
2013-02-05 22:50:45.810 [main] INFO  c.a.otter.canal.instance.spring.CanalInstanceWithSpring - start successful....
```


关闭

```
sh bin/stop.sh
```


# 6.配置HA

canal的HA分为两部分，canal server和canal client分别有对应的ha实现：

* canal server: 为了减少对mysql dump的请求，不同server上的instance要求同一时间只能有一个处于running，其他的处于standby状态.  
* canal client: 为了保证有序性，一份instance同一时间只能由一个canal client进行get/ack/rollback操作，否则客户端接收无法保证有序。整个HA机制的控制主要是依赖了zookeeper的几个特性，watcher和EPHEMERAL节点(和session生命周期绑定).

Canal Server: 

![](/images/canal/ha.jpg)

大致步骤：

1.canal server要启动某个canal instance时都先向zookeeper进行一次尝试启动判断 (实现：创建EPHEMERAL节点，谁创建成功就允许谁启动)
2.创建zookeeper节点成功后，对应的canal server就启动对应的canal instance，没有创建成功的canal instance就会处于standby状态
3.一旦zookeeper发现canal server A创建的节点消失后，立即通知其他的canal server再次进行步骤1的操作，重新选出一个canal server启动instance.
4.canal client每次进行connect时，会首先向zookeeper询问当前是谁启动了canal instance，然后和其建立链接，一旦链接不可用，会重新尝试connect. Canal Client的方式和canal server方式类似，也是利用zokeeper的抢占EPHEMERAL节点的方式进行控制.


现在开始配置两个远程节点的canal
需要在配置文件下 如下的配置：

* canal.properties:

```
# vim /base_installed/canal/conf/canal.properties 

# 用逗号隔开 且 不留空格
canal.zkServers=hdp-node-01:2181,hdp-node-02:2181,hdp-node-03:2181

canal.instance.global.spring.xml = classpath:spring/default-instance.xml

```

* instance.properties

```
canal.instance.mysql.slaveId = 1234 ##另外一台机器改成1235，保证slaveId不重复即可  
canal.instance.master.address = 192.168.213.41:3306 
```

* 两个节点都启动canal

```
./canal/bin/startup.sh  
```

* 进入zk客户端

```
# 获取正在运行的canal server  
get /otter/canal/destinations/example/running  
# 获取正在连接的canal client  
get /otter/canal/destinations/example/1001/running  
# 获取当前最后一次消费车成功的binlog  
get /otter/canal/destinations/example/1001/cursor  
```

现在我们来将通过zookeeper获取正在运行的canal server，然后我们将当前运行的canal server 正常关闭掉，我们可以通过zookeeper看到另一台canal server会成为正在运行的canal server，这就是HA模式的自动切换。这些都可以通过zookeeper查询到状态信息。


# 7.canal的数据格式

canal采用protobuff:

```
Entry
    Header
        logfileName [binlog文件名]
        logfileOffset [binlog position]
        executeTime [发生的变更]
        schemaName 
        tableName
        eventType [insert/update/delete类型]
    entryType   [事务头BEGIN/事务尾END/数据ROWDATA]
    storeValue  [byte数据,可展开，对应的类型为RowChange]    
RowChange
    isDdl       [是否是ddl变更操作，比如create table/drop table]
    sql     [具体的ddl sql]
    rowDatas    [具体insert/update/delete的变更数据，可为多条，1个binlog event事件可对应多条变更，比如批处理]
        beforeColumns [Column类型的数组]
        afterColumns [Column类型的数组]      
Column 
    index       
    sqlType     [jdbc type]
    name        [column name]
    isKey       [是否为主键]
    updated     [是否发生过变更]
    isNull      [值是否为null]
    value       [具体的内容，注意为文本]

```

canal-message example: 
比如数据库中的表：
```
mysql> select * from canal_test.person;
+----+------+------+------+
| id | name | age  | sex  |
+----+------+------+------+
|  1 | zzh  |   10 | m    |
|  3 | zzh3 |   12 | f    |
|  4 | zzh4 |    5 | m    |
+----+------+------+------+
3 rows in set (0.00 sec)
更新一条数据（update person set age=15 where id=4）：

****************************************************
* Batch Id: [2] ,count : [3] , memsize : [165] , Time : 2016-09-07 15:54:18
* Start : [mysql-bin.000003:6354:1473234846000(2016-09-07 15:54:06)] 
* End : [mysql-bin.000003:6550:1473234846000(2016-09-07 15:54:06)] 
****************************************************

================> binlog[mysql-bin.000003:6354] , executeTime : 1473234846000 , delay : 12225ms
 BEGIN ----> Thread id: 67
----------------> binlog[mysql-bin.000003:6486] , name[canal_test,person] , eventType : UPDATE , executeTime : 1473234846000 , delay : 12225ms
id : 4    type=int(11)
name : zzh4    type=varchar(100)
age : 15    type=int(11)    update=true
sex : m    type=char(1)
----------------
 END ----> transaction id: 308
================> binlog[mysql-bin.000003:6550] , executeTime : 1473234846000 , delay : 12240ms
```




参考：
http://www.cnblogs.com/tiansha/p/6457950.html		




