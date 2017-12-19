---
title: hbase的shell操作
categories: hbase   
toc: true  
tag: [hbase]
---



# 官网shell
https://learnhbase.wordpress.com/2013/03/02/hbase-shell-commands/

<!--more-->


# 1.进入客户端
```
#打开CLI
#$HBASE_HOME/bin/hbase shell

#退出
#$HBASE_HOME/bin/hbase shell
……
>quit


如果有kerberos认证，需要事先使用相应的keytab进行一下认证（使用kinit命令），认证成功之后再使用hbase shell进入可以使用whoami命令可查看当前用户
hbase(main)> whoami

```


# 2.帮助命令help
```
help "COMMAND"

#如查看describe的帮助文档
hbase(main):008:0> help 'describe'
Describe the named table. For example:
  hbase> describe 't1'
  hbase> describe 'ns1:t1'
 
Alternatively, you can use the abbreviated 'desc' for the same thing.
  hbase> desc 't1'
  hbase> desc 'ns1:t1'

```


# 3.创建表create
```
#create '表名', '列族名1','列族名2','列族名N'
hbase(main):002:0> create 'user','info1','info2'

#查看
hbase(main):003:0> list
TABLE                                                                                                        
user                                                                                                         
1 row(s) in 0.0120 seconds
 
=> ["user"]


```
# 4.查看所有表list
```
hbase(main):003:0> list
TABLE                                                                                                        
user                                                                                                         
1 row(s) in 0.0120 seconds
 
=> ["user"]

```

# 5.描述表describe
```
hbase(main):004:0> describe 'user'
Table user is ENABLED                                                                                        
COLUMN FAMILIES DESCRIPTION                                                                                  
{NAME => 'info1', DATA_BLOCK_ENCODING => 'NONE', BLOOMFILTER => 'ROW', REPLICATION_SCOPE => '0', VERSIONS => '
1', COMPRESSION => 'NONE', MIN_VERSIONS => '0', TTL => 'FOREVER', KEEP_DELETED_CELLS => 'FALSE', BLOCKSIZE =>
'65536', IN_MEMORY => 'false', BLOCKCACHE => 'true'}                                                         
{NAME => 'info2', DATA_BLOCK_ENCODING => 'NONE', BLOOMFILTER => 'ROW', REPLICATION_SCOPE => '0', VERSIONS => '
1', COMPRESSION => 'NONE', MIN_VERSIONS => '0', TTL => 'FOREVER', KEEP_DELETED_CELLS => 'FALSE', BLOCKSIZE =>
'65536', IN_MEMORY => 'false', BLOCKCACHE => 'true'}                                                         


```
# 6.判断表存在exists
```
#exists '表名'
hbase(main):011:0> exists 'user'
Table user does exist 

```

# 7.判断是否禁用表is_enabled/enable
&emsp;在删除表的时候要先禁用表
```
#is_enabled  '表名'   
hbase(main):012:0> is_enabled 'user'
true                                                                                                         
0 row(s) in 0.0410 seconds

#is_disabled  '表名'
hbase(main):013:0> is_disabled 'user'
false                                                                                                        
0 row(s) in 0.0160 seconds


/*
If you want to delete a table or change its settings, as well as in some other situations, you need to disable the table first, using the disable command. 

*/
hbase(main):008:0> disable 'test'
0 row(s) in 1.1820 seconds
 
hbase(main):009:0> enable 'test'
0 row(s) in 0.1770 seconds

#如在drop之前，需要disable 表
hbase(main):011:0> drop 'test'
0 row(s) in 0.1370 seconds


```

# 8.添加记录put
```
#put  ‘表名’, ‘rowKey’, ‘列族 : 列‘  ,  '值'
 
hbase(main):003:0> put 'user','1234','info1:name','zhangsan'
0 row(s) in 0.3500 seconds
 
#查看rowkey对应的值
hbase(main):004:0> get 'user','1234'
COLUMN                       CELL                                                                            
 info1:name                  timestamp=1480639410845, value=zhangsan                                         
1 row(s) in 0.0610 seconds

```


# 9.获取数据get
```
#查看记录rowkey下的所有数据
#get  '表名' , 'rowKey'
hbase(main):007:0> get 'user','1234'
COLUMN                       CELL                                                                            
 info1:address               timestamp=1480639492886, value=beijing                                          
 info1:age                   timestamp=1480639472692, value=22                                               
 info1:name                  timestamp=1480639410845, value=zhangsan
 

#获取某个列族
#get '表名','rowkey','列族'
hbase(main):011:0> get 'user','1234','info1'
COLUMN                       CELL                                                                            
 info1:address               timestamp=1480639492886, value=beijing                                          
 info1:age                   timestamp=1480639472692, value=22                                               
 info1:name                  timestamp=1480639410845, value=zhangsan


#获取某个列族的某个列
#get '表名','rowkey','列族：列’
hbase(main):013:0> get 'user','1234','info1:name'
COLUMN                       CELL                                                                            
 info1:name                  timestamp=1480639410845, value=zhangsan 

或者
hbase(main):012:0> get 'user', '1234', {COLUMN=>'info1:name'}

'备注:COLUMN 和 COLUMNS 是不同的,scan 操作中的 COLUMNS 指定的是表的列族, get操作中的 COLUMN 指定的是特定的列,COLUMNS 的值实质上为“列族:列修饰符”。COLUMN 和 COLUMNS 必须为大写'


```

# 10.查看表中的记录总数count
```
#查看所有记录
hbase(main):015:0> scan 'user'
ROW                          COLUMN+CELL                                                                     
 1234                        column=info1:address, timestamp=1480639492886, value=beijing                    
 1234                        column=info1:age, timestamp=1480639472692, value=22                             
 1234                        column=info1:name, timestamp=1480639410845, value=zhangsan                      
 abcd                        column=info2:name, timestamp=1480639913995, value=anglebady                     


#count  '表名'
hbase(main):014:0> count 'user'
2 row(s) in 0.1410 seconds        #因为所有记录中的行健只有2个，所以只有2条记录
```

# 11.删除delete
```
#删除列记录
#delete  ‘表名’ ,‘行键’ , ‘列族：列'
'删除前'
hbase(main):015:0> scan 'user'
ROW                          COLUMN+CELL                                                                     
 1234                        column=info1:address, timestamp=1480639492886, value=beijing                    
 1234                        column=info1:age, timestamp=1480639472692, value=22                             
 1234                        column=info1:name, timestamp=1480639410845, value=zhangsan                      
 abcd                        column=info2:name, timestamp=1480639913995, value=anglebady                     
2 row(s) in 0.0920 seconds
 
'删除'
hbase(main):016:0> delete
delete                delete_all_snapshot   delete_snapshot       deleteall
hbase(main):016:0> delete 'user','1234','info1:address'

 
'删除后'
hbase(main):017:0> scan 'user'
ROW                          COLUMN+CELL                                                                     
 1234                        column=info1:age, timestamp=1480639472692, value=22                             
 1234                        column=info1:name, timestamp=1480639410845, value=zhangsan                      
 abcd                        column=info2:name, timestamp=1480639913995, value=anglebady                     


#删除整行
#deleteall '表名','rowkey'

'删除前'
hbase(main):020:0> scan 'user'
ROW                          COLUMN+CELL                                                                     
 1234                        column=info1:age, timestamp=1480639472692, value=22                             
 1234                        column=info1:name, timestamp=1480639410845, value=zhangsan                      
 abcd                        column=info2:name, timestamp=1480639913995, value=anglebady                     
2 row(s) in 0.0310 seconds
 
'删除整行'
hbase(main):021:0> deleteall 'user','abcd'
0 row(s) in 0.0280 seconds
 
'删除后'
hbase(main):022:0> scan 'user'
ROW                          COLUMN+CELL                                                                     
 1234                        column=info1:age, timestamp=1480639472692, value=22                             
 1234                        column=info1:name, timestamp=1480639410845, value=zhangsan 



#删除行中的某个列值

# 语法：delete <table>, <rowkey>,  <family:column> , <timestamp>,必须指定列名
# 例如：删除表t1，rowkey001中的f1:col1的数据
hbase(main)> delete 't1','rowkey001','f1:col1'
注：将删除改行f1:col1列所有版本的数据

```

# 12.清空表truncate 
```
#truncate '表名'

hbase(main):023:0> help 'truncate'
  Disables, drops and recreates the specified table.

```


# 13.查看所有记录scan
```
#查看所有记录
#scan "表名"  
hbase(main):008:0> scan 'user'
ROW                          COLUMN+CELL                                                                     
 1234                        column=info1:address, timestamp=1480639492886, value=beijing                    
 1234                        column=info1:age, timestamp=1480639472692, value=22                             
 1234                        column=info1:name, timestamp=1480639410845, value=zhangsan                      



#查看某个表某个列中所有数据
#scan "表名" , {COLUMNS=>'列族名:列名'}        COLUMNS 必须为大写
 
hbase(main):028:0> scan 'user',{COLUMNS=>'info1:name'}
ROW                          COLUMN+CELL                                                                     
 1234                        column=info1:name, timestamp=1480639410845, value=zhangsan                      
 abcd                        column=info1:name, timestamp=1480640586515, value=angebady 
 
#scan时取一列
scan 'user_test',{LIMIT =>10}


#其他
  hbase> scan 'hbase:meta'                                #全表扫描
  hbase> scan 'hbase:meta', {COLUMNS => 'info:regioninfo'}        #全表的regioninfo列
  hbase> scan 'ns1:t1', {COLUMNS => ['c1', 'c2'], LIMIT => 10, STARTROW => 'xyz'}    
  hbase> scan 't1', {COLUMNS => ['c1', 'c2'], LIMIT => 10, STARTROW => 'xyz'}
	#查询时间的范围
  hbase> scan 't1', {COLUMNS => 'c1', TIMERANGE => [1303668804, 1303668904]}
  hbase> scan 't1', {REVERSED => true}
  hbase> scan 't1', {FILTER => "(PrefixFilter ('row2')   AND   (QualifierFilter (>=, 'binary:xyz'))) AND (TimestampsFilter ( 123, 456))"}
  hbase> scan 't1', {FILTER =>org.apache.hadoop.hbase.filter.ColumnPaginationFilter.new(1, 0)}
  hbase> scan 't1', {CONSISTENCY => 'TIMELINE'}

#查询表中的数据行数

# 语法：count <table>, {INTERVAL => intervalNum, CACHE => cacheNum}
# INTERVAL设置多少行显示一次及对应的rowkey，默认1000；CACHE每次去取的缓存区大小，默认是10，调整该参数可提高查询速度
# 例如，查询表t1中的行数，每100条显示一次，缓存区为500
hbase(main)> count 't1', {INTERVAL => 100, CACHE => 500}



#查看表“scores”中的所有数据。
hbase(main):012:0> scan 'scores'

#scan 命令可以指定 startrow,stoprow 来 scan 多个 row。
scan 'user_test',{COLUMNS =>'info:username',LIMIT =>10, STARTROW => 'test', STOPROW=>'test2'}


#查看表“scores”中列族“course”的所有数据。
hbase(main):012:0> scan  'scores', {COLUMN => 'grad'}
hbase(main):012:0> scan  'scores', {COLUMN=>'course:math'}
hbase(main):012:0> scan  'scores', {COLUMNS => 'course'}
hbase(main):012:0> scan  'scores', {COLUMNS => 'course'}


```

# 14.更新记录
```
就是重写一遍put，进行覆盖，hbase没有修改，都是追加
```

# 15.注意
* get是查询一条记录，所以要指定rowkey
* scan是全表扫描，查询多条记录
可以查询某些行的列

# 16.shell中过滤器的简单使用

在hbase shell中查询数据，可以在hbase shell中直接使用过滤器：
```

> scan 'testByCrq', FILTER=>"RowFilter(=,'substring:111')"
#查询的是表名为testByCrq，过滤方式是通过rowkey过滤，匹配出rowkey含111的数据。

> scan 'testByCrq', FILTER=>"RowFilter(=,'binary:0111486816556')"
#查询的是表名为testByCrq，过滤方式是通过rowkey过滤，匹配出rowkey等于0111486816556的数据。

> scan 'testByCrq', FILTER=>"RowFilter(<=,'binary:0111486816556')"
#查询的是表名为testByCrq，过滤方式是通过rowkey过滤，匹配出rowkey小于等于0111486816556的数据。

> scan 'testByCrq', FILTER=>"ValueFilter(=,'substring:111')"
#查询的是表名为testByCrq，过滤方式是通过value过滤，匹配出value含111的数据。

> scan 'testByCrq', FILTER=>"FamilyFilter(=,'substring:f')"
#查询的是表名为testByCrq，过滤方式是通过列簇过滤，匹配出列簇含f的数据。 
注：substring不能使用小于等于等符号。

> scan 'testByCrq', FILTER=>"PrefixFilter('00000')"
#查询的是表名为testByCrq，过滤方式是通过前缀过滤过滤的是行键，匹配出前缀为00000的数据

```



2017年10月14日11:38:03 补充：

http://www.cnblogs.com/liyanbin/p/5275632.html

http://blog.csdn.net/qq_27078095/article/details/56482010

http://blog.csdn.net/u014034934/article/details/74330848

 





