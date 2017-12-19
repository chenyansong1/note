---
title: explain优化慢查询语句
categories: mysql   
tags: [mysql]
---




# 1.执行计划 — EXPLAIN命令

&emsp;执行计划是语句优化的主要切入点，通过执行计划的判读了解语句的执行过程。在执行计划生成方面，MySQL与Oracle明显不同，它不会缓存执行计划，每次都执行“硬解析”。查看执行计划的方法，就是使用EXPLAIN命令。

## 1.1.基本语法
```
EXPLAIN QUERY
#当在一个Select语句前使用关键字EXPLAIN时，MySQL会解释了即将如何运行该Select语句，它显示了表如何连接、连接的顺序等信息


EXPLAIN EXTENDED QUERY
#当使用EXTENDED关键字时，EXPLAIN产生附加信息，可以用SHOW WARNINGS浏览。该信息显示优化器限定SELECT语句中的表和列名，重写并且执行优化规则后SELECT语句是什么样子，并且还可能包括优化过程的其它注解。在MySQL5.0及更新的版本里都可以使用，在MySQL5.1里它有额外增加了一个过滤列(filtered)。


EXPLAIN PARTITIONS QUERY
#显示的是查询要访问的数据分片——如果有分片的话。它只能在MySQL5.1及更新的版本里使用


EXPLAIN FORMAT=JSON (5.6新特性)
#另一个格式显示执行计划。可以看到诸如表间关联方式等信息。

```


## 1.2.EXPLAIN输出字段

> id

&emsp;MySQL选定的执行计划中查询的序列号。如果语句里没有子查询等情况，那么整个输出里就只有一个SELECT，这样一来每一行在这个列上都会显示一个1。如果语句中使用了子查询、集合操作、临时表等情况，会给ID列带来很大的复杂性。如上例中，WHERE部分使用了子查询，其id=2的行表示一个关联子查询。
```
mysql> explain select * from (select * from student) as a;
+----+-------------+------------+--------+---------------+------+---------+------+------+-------+
| id | select_type | table      | type   | possible_keys | key  | key_len | ref  | rows | Extra |
+----+-------------+------------+--------+---------------+------+---------+------+------+-------+
|  1 | PRIMARY     | <derived2> | system | NULL          | NULL | NULL    | NULL |    1 |       |
|  2 | DERIVED     | student    | ALL    | NULL          | NULL | NULL    | NULL |    1 |       |
+----+-------------+------------+--------+---------------+------+---------+------+------+-------+

```

> select_type

&emsp;语句所使用的查询类型。是简单SELECT还是复杂SELECT(如果是后者，显示它属于哪一种复杂类型)。常用有以下几种标记类型。

* DEPENDENT SUBQUERY
 子查询内层的第一个SELECT，依赖于外部查询的结果集
* DEPENDENT UNION
 子查询中的UNION，且为UNION中从第二个SELECT开始的后面所有SELECT，同样依赖于外部查询的结果集
* PRIMARY
 子查询中的最外层查询，注意并不是主键查询
* SIMPLE
 除子查询或UNION之外的其他查询
* SUBQUERY
 子查询内层查询的第一个SELECT，结果不依赖于外部查询结果集
* UNCACHEABLE SUBQUERY
 结果集无法缓存的子查询
* UNION
 UNION语句中的第二个SELECT开始后面的所有SELECT，第一个SELECT为PRIMARY
* UNION RESULT
 UNION中的合并结果。从UNION临时表获取结果的SELECT
* DERIVED
 衍生表查询(FROM子句中的子查询)。MySQL会递归执行这些子查询，把结果放在临时表里。在内部，服务器就把当做一个"衍生表"那样来引用，因为临时表就是源自子查询


> table

&emsp;这一步所访问的数据库中表的名称或者SQL语句指定的一个别名表。这个值可能是表名、表的别名或者一个为查询产生的临时表的标识符，如派生表、子查询或集合。


> type

表的访问方式。以下列出了各种不同类型的表连接，依次是从最好的到最差的
* system
   系统表，表只有一行记录。这是const表连接类型的一个特例
* const
    读常量，最多只有一行匹配的记录。由于只有一行记录，优化程序里该行记录的字段值可以被当作是一个恒定值。const用于在和PRIMARY KEY或UNIQUE索引中有固定值比较的情形
*  eq_ref
    最多只会有一条匹配结果，一般是通过主键或唯一键索引来访问。从该表中会有一行记录被读取出来以和从前一个表中读取出来的记录做联合。与const类型不同的是，这是最好的连接类型。它用在索引所有部分都用于做连接并且这个索引是一个PRIMARY KEY或UNIQUE类型。eq_ref可以用于在进行"="做比较时检索字段。比较的值可以是固定值或者是表达式，表达示中可以使用表里的字段，它们在读表之前已经准备好了。
* ref
    JOIN语句中驱动表索引引用的查询。该表中所有符合检索值的记录都会被取出来和从上一个表中取出来的记录作联合。ref用于连接程序使用键的最左前缀或者是该键不是PRIMARY KEY或UNIQUE索引(换句话说，就是连接程序无法根据键值只取得一条记录)的情况。当根据键值只查询到少数几条匹配的记录时，这就是一个不错的连接类型。ref还可以用于检索字段使用"="操作符来比较的时候。


* ref_or_null
   与ref的唯一区别就是在使用索引引用的查询之外再增加一个空值的查询。这种连接类型类似ref，不同的是MySQL会在检索的时候额外的搜索包含NULL值的记录。这种连接类型的优化是从MySQL 4.1.1开始的，它经常用于子查询。
* index_merge
   查询中同时使用两个(或更多)索引，然后对索引结果进行合并(merge)，再读取表数据。这种连接类型意味着使用了Index Merge优化方法。
* unique_subquery
   子查询中的返回结果字段组合是主键或唯一约束。
* index_subquery
   子查询中的返回结果字段组合是一个索引(或索引组合)，但不是一个主键或唯一索引。这种连接类型类似unique_subquery。它用子查询来代替IN，不过它用于在子查询中没有唯一索引的情况下。
* range
   索引范围扫描。只有在给定范围的记录才会被取出来，利用索引来取得一条记录。
* index
   全索引扫描。连接类型跟ALL一样，不同的是它只扫描索引树。它通常会比ALL快点，因为索引文件通常比数据文件小。MySQL在查询的字段知识单独的索引的一部分的情况下使用这种连接类型。
* fulltext
   全文索引扫描。
* all
   全表扫描。


> possible_keys

该字段是指MySQL在搜索表记录时可能使用哪个索引。如果没有任何索引可以使用，就会显示为null。

> key

查询优化器从possible_keys中所选择使用的索引。key字段显示了MySQL实际上要用的索引。当没有任何索引被用到的时候，这个字段的值就是NULL

> key_len

被选中使用索引的索引键长度。key_len字段显示了MySQL使用索引的长度。当key字段的值为NULL时，索引的长度就是NULL。


> ref

列出是通过常量，还是某个表的某个字段来过滤的。ref字段显示了哪些字段或者常量被用来和key配合从表中查询记录出来。

> rows
 
该字段显示了查询优化器通过系统收集的统计信息估算出来的结果集记录条数

> Extra
 
该字段显示了查询中MySQL的附加信息

> filtered
 
这个列式在MySQL5.1里新加进去的，当使用EXPLAIN EXTENDED时才会出现。它显示的是针对表里符合某个条件(WHERE子句或联接条件)的记录数的百分比所作的一个悲观估算



# 2.sql优化

> Where子句中使用独立的列

查询中列如果不是独立的，则不会使用索引

![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/sql/1.jpg)


> 关联查询优化

* 确保ON或者USING子句的列上有索引。一般只需要在关联顺序中的第二个表的相应列上创建索引。
 
* 关联字段类型保持一致。 


> LIKE匹配优化

如果 LIKE 的参数是非通配字符开始的固定字符串，MySQL在做LIKE比较时也可能用到索引
![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/sql/2.png)
 
Extra信息中显示使用了索引。 
like后面使用通配符开始的字符串则不会使用索引

![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/sql/3.png)
 
rows列显示599行，也就是customer表的总行数，因此没利用到索引。



> 避免SQL中出现不必要的类型转换

![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/sql/4.jpg)
![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/sql/5.jpg)
 

 


> Select指定列来代替select *

* 在某些情况下 select *  要比select 指定列 需要浪费更多的资源
* 如果某些列中含有text等类型，select 指定列可以减少网络传输缓冲区的使用
* 如果SQL中含有order by ,并且排序不能利用上已用的索引那么，额外的字段会占用更多的sort_buffer_size .
* Select指定列可以方便使用覆盖索引。

比如下面这个例子，使用到了覆盖索引。

![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/sql/6.jpg)


> 子查询优化

* MySQL5.6前，子查询大多时候会先遍历outer table，对于其返回的每一条记录都执行一次subquery，而且子查询没有任何索引，导致子查询相较于关联查询要慢很多（解决方案：表连接代替子查询）；
 
* MySQL5.6 后，对子查询进行了大幅度的优化，将子查询结果存入临时表，使得子查询只执行一次，而且优化器还会给子查询产生的派生表添加索引，使得子查询性能得到了强劲的优化。

曾经的“绝对真理”：子查询比关联查询慢很多。——不再成立。
 
通过子查询优化可以减少多个查询多次对数据进行访问。
 
但也有时候，子查询可能比关联查询还要快。

> GROUP BY优化

表的标识列分组比其他列分组的效率高。
```
SELECT actor.first_name, actor.last_name, count(*) FROM film_actor INNER JOIN actor USING (actor_id) GROUP BY actor.first_name, actor.last_name;
```

![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/sql/7.jpg)

 优化后：
```
SELECT actor.first_name, actor.last_name,count(*) FROM film_actor  INNER JOIN actor USING (actor_id) GROUP BY actor.actor_id ;
```

![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/sql/8.jpg)

因为actor.actor_id是主键，分组效率会提升。
 
使用GROUP BY子句时，结果集会自动按照分组的字段进行排序，GROUP BY子句中可以直接使用DESC或者ASC关键字，使得分组的结果集按需要的方向排序。
 
So：如果没有排序需求，可以加ORDER BY NULL,让MySQL不再进行文件排序，从而提高查询效率。

> UNION优化

除非需要消除重复的行，否则一定要使用union all，因为没有ALL关键字，MySQL会给临时表加上DISTINCT选项，使得对整个临时表做代价很高的唯一性检查。

由于union产生的临时表无法使用优化器的优化策略，所以可以直接将WHERE, ORDER BY, LIMIT等子句冗余的写一份到各个子查询中。

![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/sql/9.jpg)

![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/sql/10.png)
 
 如果把ORDER BY, LIMIT等子句冗余写一份到各个子查询中。

 
![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/sql/11.png)

 
则排序的基数会有效的得到降低，从而提高效率。



# 3.抓慢查询
```
#a.show full processlist; 
  或者   
 [root@MySQL 3309]# mysql -uroot -poldboy123 -S /data/3309/mysql.sock -e "show full processlist;"  
用grep 去抓取特定的语句
[root@MySQL 3309]# mysql -uroot -poldboy123 -S /data/3309/mysql.sock -e "show full processlist;"|grep select -i


#b.分析慢查询日志（下面三行在my.cnf中添加）
  long_query_time =1    //查询超过1s
  log-slow-queries = /data/3306/slow.log  //慢查询日志
  log_queries_not_using_indexes  //没有索引的

```
 

# 4.explain语句检查索引执行情况

# 5.对需要建立索引的列建立索引

# 6.使用慢查询工具（每天早晨发邮件，自动化的）


# 查看语句的详细执行时间

```
Examples:
mysql> SELECT @@profiling;    //查看是否开启
+-------------+
| @@profiling |
+-------------+
|           0 |
+-------------+
1 row in set (0.00 sec)
 
mysql> SET profiling = 1;    //设置开启
Query OK, 0 rows affected (0.00 sec)
 
mysql> DROP TABLE IF EXISTS t1;
Query OK, 0 rows affected, 1 warning (0.00 sec)
 
mysql> CREATE TABLE T1 (id INT);
Query OK, 0 rows affected (0.01 sec)
 
mysql> SHOW PROFILES;      //显示所有查询记录的时间
+----------+----------+--------------------------+
| Query_ID | Duration | Query                    |
+----------+----------+--------------------------+
|        0 | 0.000088 | SET PROFILING = 1        |
|        1 | 0.000136 | DROP TABLE IF EXISTS t1  |
|        2 | 0.011947 | CREATE TABLE t1 (id INT) |
+----------+----------+--------------------------+
3 rows in set (0.00 sec)
//查询某一条记录的各项消耗时间
mysql> SHOW PROFILE FOR QUERY 1;
+--------------------+----------+
| Status             | Duration |
+--------------------+----------+
| query end          | 0.000107 |
| freeing items      | 0.000008 |
| logging slow query | 0.000015 |
| cleaning up        | 0.000006 |
+--------------------+----------+
4 rows in set (0.00 sec)
 
mysql> SHOW PROFILE CPU FOR QUERY 2;
+----------------------+----------+----------+------------+
| Status               | Duration | CPU_user | CPU_system |
+----------------------+----------+----------+------------+
| checking permissions | 0.000040 | 0.000038 |   0.000002 |
| creating table       | 0.000056 | 0.000028 |   0.000028 |
| After create         | 0.011363 | 0.000217 |   0.001571 |
| query end            | 0.000375 | 0.000013 |   0.000028 |
| freeing items        | 0.000089 | 0.000010 |   0.000014 |
| logging slow query   | 0.000019 | 0.000009 |   0.000010 |
| cleaning up          | 0.000005 | 0.000003 |   0.000002 |
+----------------------+----------+----------+------------+

```


参考:
[解开发者之痛：中国移动MySQL数据库优化最佳实践](http://dbaplus.cn/news-11-606-1.html)
[如何用一款小工具大大加速MySQL SQL语句优化](http://dbaplus.cn/news-11-687-1.html)






