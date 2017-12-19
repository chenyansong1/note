# 1.准备

* 建立实例maven工程

* 添加pom依赖

```
<dependency>  
    <groupId>com.alibaba.otter</groupId>  
    <artifactId>canal.client</artifactId>  
    <version>1.0.12</version>  
</dependency>  

```


# 2.简单API实例

```
package com.aipai.canalclient.process;


import java.util.List;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;

public class  CanalDemo {
    public static void main(String args[]) {
        // 创建链接
		// zk：这是HA时的配置
		// example：是一个实例
		// canal,canal是用户名和密码
        CanalConnector connector = CanalConnectors.newClusterConnector("hdp-node-01:2181,hdp-node-02:2181,hdp-node-03:2181", "example", "canal", "canal");
        int batchSize = 1000;
        int emptyCount = 0;
        try {
            connector.connect();
            connector.subscribe(".*\\..*");//订阅某一个库下的某一张表
            connector.rollback();
            int totalEmptyCount = 120;
            while (emptyCount < totalEmptyCount) {
                Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    emptyCount++;
                    System.out.println("empty count : " + emptyCount);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    emptyCount = 0;
                    // System.out.printf("message[batchId=%s,size=%s] \n", batchId, size);
                    printEntry(message.getEntries());
                }
                connector.ack(batchId); // 提交确认
                // connector.rollback(batchId); // 处理失败, 回滚数据
            }
            System.out.println("empty too many times, exit");
        } finally {
            connector.disconnect();
        }
    }
    private static void printEntry(List<Entry> entrys) {
        for (Entry entry : entrys) {
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN ||
                    entry.getEntryType() == EntryType.TRANSACTIONEND) {
                continue;
            }
            RowChange rowChage = null;
            try {
                rowChage = RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error, data:" + entry.toString(),e);
            }
            EventType eventType = rowChage.getEventType();
            System.out.println(String.format("================> binlog[%s:%s] , name[%s,%s] , eventType : %s",
            entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
                    eventType));
            for (RowData rowData : rowChage.getRowDatasList()) {
                if (eventType == EventType.DELETE) {
                    printColumn(rowData.getBeforeColumnsList());
                } else if (eventType == EventType.INSERT) {
                    printColumn(rowData.getAfterColumnsList());
                } else {
                    System.out.println("-------> before");
                    printColumn(rowData.getBeforeColumnsList());
                    System.out.println("-------> after");
                    printColumn(rowData.getAfterColumnsList());
                }
            }
        }
    }
    private static void printColumn(List<Column> columns) {
        for (Column column : columns) {
            System.out.println("is primary key =" + column.getIsKey() + "   " + column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }
}

```


# 3.canal的数据格式


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

# 4.测试

* 登录数据库，触发数据库变更

```
create table test (  
uid int (4) primary key not null auto_increment,  
name varchar(10) not null);  
  
insert into test (name) values('10');  

```


* client 抓取mysql信息

```
================> binlog[mysql-bin.000016:3281] , name[canal_test,test] , eventType : INSERT  
uid : 7    update=false  
name : 10    update=false  
empty count : 1  
empty count : 2  
```


# 5.测试过程中产生的问题

* 启动失败，log日志中地址正在使用
1、11111端口正在被占用 可以用 ls -i:11111 查看监听进程谁占用端口 或者 用 ps -ef | grep 11111 查看哪个进程占用端口号  然后 kill -9 进程号  杀掉占用进程
2、可以编辑 canal/conf/canal.properties 中的端口号 ，改为不占用的端口

* canal无法抓取mysql触发数据库改变的信息
1、检查mysql是否打开binlog写入功能  检查binlog 是否为行模式。
```
show variables like "binlog_format"  
```

* 检查my.cnf 和 instance.properties 等配置文件填写信息是否正确。
* 检查client 代码 调试实例代码
* 版本兼容问题，canal 1.8 换成 canal 1.7 继续测试
* 查看所有日志文件 分析日志 
* 查看mysql的模式和查看binlog位置

```
mysql> show master status;
+------------------+----------+--------------+------------------+
| File             | Position | Binlog_Do_DB | Binlog_Ignore_DB |
+------------------+----------+--------------+------------------+
| mysql-bin.000009 |      208 |              |                  |
+------------------+----------+--------------+------------------+

mysql> show  global variables like '%binlog_format%';
+---------------+-------+
| Variable_name | Value |
+---------------+-------+
| binlog_format | ROW   |
+---------------+-------+

```

* 找不到消费的位置报错

如有一个实例名为：borrow
```
destination:borrow[com.alibaba.otter.canal.parse.exception.CanalParseException: can’t find start position for borrow

```
需要删掉borrow目录下的meta.dat


参考：

http://blog.csdn.net/hackerwin7/article/details/37923607  
http://zhm8.cn/2017/02/23/canal%E5%88%86%E6%9E%90binlog/
