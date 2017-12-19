---
title: HBase的API示例2
categories: hbase   
toc: true  
tag: [hbase]
---



# 1.配置

HBaseConfiguration
包：org.apache.hadoop.hbase.HBaseConfiguration
作用：通过此类可以对HBase进行配置
用法实例：
```
Configuration config = HBaseConfiguration.create();
#说明： HBaseConfiguration.create() 默认会从classpath 中查找 hbase-site.xml 中的配置信息，初始化Configuration。
```
 
使用方法:
```
static Configuration config = null;
static {
     config = HBaseConfiguration.create();
     config.set("hbase.zookeeper.quorum", "slave1,slave2,slave3");
     config.set("hbase.zookeeper.property.clientPort", "2181");
}
```


---


<!--more-->


# 2.表管理类
HBaseAdmin
包：org.apache.hadoop.hbase.client.HBaseAdmin
作用：提供接口关系HBase 数据库中的表信息
 
用法：
```
HBaseAdmin admin = new HBaseAdmin(config);
```
  
# 3.表描述类
HTableDescriptor
包：org.apache.hadoop.hbase.HTableDescriptor
作用：HTableDescriptor 类包含了表的名字以及表的列族信息,表的schema（设计）
用法：
```
HTableDescriptor htd =new HTableDescriptor(tablename);
htd.addFamily(new HColumnDescriptor(“myFamily”));
```
  
# 4.列族的描述类
HColumnDescriptor
包：org.apache.hadoop.hbase.HColumnDescriptor
作用：HColumnDescriptor 维护列族的信息
 
用法：
```
htd.addFamily(new HColumnDescriptor(“myFamily”));
```
  
  
# 5.创建表的操作
```
CreateTable（一般我们用shell创建表）
static Configuration config = null;
static {
     config = HBaseConfiguration.create();
     config.set("hbase.zookeeper.quorum", "slave1,slave2,slave3");
     config.set("hbase.zookeeper.property.clientPort", "2181");
}
 
HBaseAdmin admin = new HBaseAdmin(config);
HTableDescriptor desc = new HTableDescriptor(tableName);
HColumnDescriptor family1 = new HColumnDescriptor(“f1”);
HColumnDescriptor family2 = new HColumnDescriptor(“f2”);
desc.addFamily(family1);
desc.addFamily(family2);
admin.createTable(desc);
```
  
  
# 6.删除表
```
HBaseAdmin admin = new HBaseAdmin(config);
admin.disableTable(tableName);
admin.deleteTable(tableName);

```
  
  
# 7.创建一个表的类
HTable
包：org.apache.hadoop.hbase.client.HTable
作用：HTable 和 HBase 的表通信
用法：
```
// 普通获取表
 HTable table = new HTable(config,Bytes.toBytes(tablename);
// 通过连接池获取表
Connection connection = ConnectionFactory.createConnection(config);
HTableInterface table = connection.getTable(TableName.valueOf("user"));

```

# 8.单条插入数据
Put
包：org.apache.hadoop.hbase.client.Put
作用：插入数据
用法：
Put put = new Put(row);
p.add(family,qualifier,value);
说明：向表 tablename 添加 “family,qualifier,value”指定的值。
 
示例代码：

```
Connection connection = ConnectionFactory.createConnection(config);
HTableInterface table = connection.getTable(TableName.valueOf("user"));
Put put = new Put(Bytes.toBytes(rowKey));
put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier),Bytes.toBytes(value));
table.put(put);
```

# 9.批量插入
```
//批量插入
List<Put> list = new ArrayList<Put>();
Put put = new Put(Bytes.toBytes(rowKey));//获取put，用于插入
put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier),Bytes.toBytes(value));//封装信息
list.add(put);
table.put(list);//添加记录

```
  
# 10.删除数据
Delete
包：org.apache.hadoop.hbase.client.Delete
作用：删除给定rowkey的数据
用法：
```
Delete del= new Delete(Bytes.toBytes(rowKey));
table.delete(del);
代码实例
Connection connection = ConnectionFactory.createConnection(config);
HTableInterface table = connection.getTable(TableName.valueOf("user"));
Delete del= new Delete(Bytes.toBytes(rowKey));
table.delete(del);
```
  
# 11.单条查询
Get
包：org.apache.hadoop.hbase.client.Get
作用：获取单个行的数据
用法：
HTable table = new HTable(config,Bytes.toBytes(tablename));
Get get = new Get(Bytes.toBytes(row));
Result result = table.get(get);
说明：获取 tablename 表中 row 行的对应数据
 
代码示例：
```
Connection connection = ConnectionFactory.createConnection(config);
HTableInterface table = connection.getTable(TableName.valueOf("user"));
Get get = new Get(rowKey.getBytes());
Result row = table.get(get);
for (KeyValue kv : row.raw()) {
System.out.print(new String(kv.getRow()) + " ");
System.out.print(new String(kv.getFamily()) + ":");
System.out.print(new String(kv.getQualifier()) + " = ");
System.out.print(new String(kv.getValue()));
System.out.print(" timestamp = " + kv.getTimestamp() + "\n");
}
```
  
# 12.批量查询
ResultScanner
包：org.apache.hadoop.hbase.client.ResultScanner
作用：获取值的接口
用法：
ResultScanner scanner = table.getScanner(scan);
For(Result rowResult : scanner){
        Bytes[] str = rowResult.getValue(family,column);
}
说明：循环获取行中列值。
 
代码示例：
```
Connection connection = ConnectionFactory.createConnection(config);
HTableInterface table = connection.getTable(TableName.valueOf("user"));
Scan scan = new Scan();
scan.setStartRow("a1".getBytes());
scan.setStopRow("a20".getBytes());
ResultScanner scanner = table.getScanner(scan);
for (Result row : scanner) {
System.out.println("\nRowkey: " + new String(row.getRow()));
	for (KeyValue kv : row.raw()) {
	     System.out.print(new String(kv.getRow()) + " ");
	     System.out.print(new String(kv.getFamily()) + ":");
	     System.out.print(new String(kv.getQualifier()) + " = ");
	     System.out.print(new String(kv.getValue()));
	     System.out.print(" timestamp = " + kv.getTimestamp() + "\n");
	}
}

```
# 13.hbase过滤器
# 13.1.FilterList

FilterList 代表一个过滤器列表，可以添加多个过滤器进行查询，多个过滤器之间的关系有：
与关系（符合所有）：FilterList.Operator.MUST_PASS_ALL  
或关系（符合任一）：FilterList.Operator.MUST_PASS_ONE
 
使用方法：
```
FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE);   
Scan s1 = new Scan();  
 filterList.addFilter(new SingleColumnValueFilter(Bytes.toBytes(“f1”),  Bytes.toBytes(“c1”),  CompareOp.EQUAL,Bytes.toBytes(“v1”) )  );  
filterList.addFilter(new SingleColumnValueFilter(Bytes.toBytes(“f1”),  Bytes.toBytes(“c2”),  CompareOp.EQUAL,Bytes.toBytes(“v2”) )  );  
 // 添加下面这一行后，则只返回指定的cell，同一行中的其他cell不返回  
 s1.addColumn(Bytes.toBytes(“f1”), Bytes.toBytes(“c1”));  
 s1.setFilter(filterList);  //设置filter
 ResultScanner ResultScannerFilterList = table.getScanner(s1);  //返回结果列表

```


# 13.2.过滤器的种类
过滤器的种类：
* 列植过滤器—SingleColumnValueFilter
过滤列植的相等、不等、范围等
* 列名前缀过滤器—ColumnPrefixFilter
过滤指定前缀的列名
* 多个列名前缀过滤器—MultipleColumnPrefixFilter
过滤多个指定前缀的列名
* rowKey过滤器—RowFilter
通过正则，过滤rowKey值。



# 13.3.列植过滤器—SingleColumnValueFilter

SingleColumnValueFilter 列值判断
相等 (CompareOp.EQUAL ),
不等(CompareOp.NOT_EQUAL),
范围 (e.g., CompareOp.GREATER)…………
下面示例检查列值和字符串'values' 相等...
SingleColumnValueFilter f = new  SingleColumnValueFilter(Bytes.toBytes("cFamily"),
   Bytes.toBytes("column"), 
   CompareFilter.CompareOp.EQUAL, 
   Bytes.toBytes("values")
);
s1.setFilter(f);
注意：如果过滤器过滤的列在数据表中有的行中不存在，那么这个过滤器对此行无法过滤。



# 13.4.列名前缀过滤器—ColumnPrefixFilter

过滤器—ColumnPrefixFilter
ColumnPrefixFilter 用于指定列名前缀值相等
ColumnPrefixFilter f = new ColumnPrefixFilter(Bytes.toBytes("values"));
s1.setFilter(f);



# 13.5.多个列值前缀过滤器—MultipleColumnPrefixFilter

MultipleColumnPrefixFilter 和 ColumnPrefixFilter 行为差不多，但可以指定多个前缀
byte[][] prefixes = new byte[][] {Bytes.toBytes("value1"),Bytes.toBytes("value2")};
Filter f = new MultipleColumnPrefixFilter(prefixes);
s1.setFilter(f);

# 13.6.rowKey过滤器—RowFilter

RowFilter 是rowkey过滤器
通常根据rowkey来指定范围时，使用scan扫描器的StartRow和StopRow方法比较好。
Filter f = new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator("^1234")); //匹配以1234开头的rowkey
s1.setFilter(f);