# mysql行转列，列转行问题

## 1.列转行

有下面这样一张表

```
mysql> select * from tb_paidashi_pay_model_day where dt=20170516 ;
+------+----------+------+---------+------------+------------+----------+
| id   | platform | type | paytype | totalpayer | totalmoney | dt       |
+------+----------+------+---------+------------+------------+----------+
| 2667 | android  |    1 |      20 |         40 |      55503 | 20170516 |
| 2668 | android  |    1 |      70 |        106 |     150006 | 20170516 |
| 2669 | android  |    2 |      20 |         11 |      31502 | 20170516 |
| 2670 | android  |    2 |      70 |         22 |      77000 | 20170516 |
| 2671 | android  |    3 |      20 |          2 |       8001 | 20170516 |
| 2672 | android  |    3 |      70 |          7 |      40002 | 20170516 |
| 2673 | android  |    4 |      20 |          4 |          4 | 20170516 |
| 2674 | android  |    4 |      70 |          2 |      15001 | 20170516 |
| 2675 | ios      |    1 |      70 |         17 |      32500 | 20170516 |
| 2676 | ios      |    2 |      20 |          1 |       2800 | 20170516 |
| 2677 | ios      |    2 |      70 |          7 |      31600 | 20170516 |
| 2678 | pc       |    2 |      70 |          1 |       3900 | 20170516 |
+------+----------+------+---------+------------+------------+----------+
12 rows in set (0.00 sec)

```

现在需要将下面的查询结果**进行列转行**

```

mysql> select type, sum(1) sum from tb_paidashi_pay_model_day where dt=20170516 group by type;
+------+------+
| type | sum  |
+------+------+
|    1 |    3 |
|    2 |    5 |
|    3 |    2 |
|    4 |    2 |
+------+------+
4 rows in set (0.00 sec)

```

使用count统计

```
    SELECT
    count(case type  when 1 then 1 else NULL END) as  'type_1',
    count(case type  when 2 then 1  else NULL END) as  'type_2',
    count(case type  when 3 then 1 else NULL END) as  'type_3',
    count(case type  when 4 then 1 else NULL END) as  'type_4'
    from tb_paidashi_pay_model_day where dt=20170516 ;
+--------+--------+--------+--------+
| type_1 | type_2 | type_3 | type_4 |
+--------+--------+--------+--------+
|      3 |      5 |      2 |      2 |
+--------+--------+--------+--------+
1 row in set (0.00 sec)
```

这里需要注意的问题是count遇到null的时候，会不去统计，如果我们将null变成0，那么结果如下：
```
   SELECT
	  count(case type  when 1 then 1 else 0 END) as  'type_1',
	  count(case type  when 2 then 1  else 0 END) as  'type_2',
	  count(case type  when 3 then 1 else 0 END) as  'type_3',
	  count(case type  when 4 then 1 else 0 END) as  'type_4'
    from tb_paidashi_pay_model_day where dt=20170516 ;
+--------+--------+--------+--------+
| type_1 | type_2 | type_3 | type_4 |
+--------+--------+--------+--------+
|     12 |     12 |     12 |     12 |
+--------+--------+--------+--------+
1 row in set (0.00 sec)
```
出现上面的结果是因为count将非null做了统计，尽管是0，count也是会计数的

但是我们如果使用sum，那么结果如下：

```
  SELECT
	 sum(case type  when 1 then 1 else 0 END) as  'type_1',
	 sum(case type  when 2 then 1  else 0 END) as  'type_2',
	 sum(case type  when 3 then 1 else 0 END) as  'type_3',
	 sum(case type  when 4 then 1 else 0 END) as  'type_4'
  from tb_paidashi_pay_model_day where dt=20170516 ;
+--------+--------+--------+--------+
| type_1 | type_2 | type_3 | type_4 |
+--------+--------+--------+--------+
|      3 |      5 |      2 |      2 |
+--------+--------+--------+--------+
1 row in set (0.00 sec)



 SELECT
 sum(case type  when 1 then 1 else null END) as  'type_1',
 sum(case type  when 2 then 1  else null END) as  'type_2',
 sum(case type  when 3 then 1 else null END) as  'type_3',
 sum(case type  when 4 then 1 else null END) as  'type_4'
 from tb_paidashi_pay_model_day where dt=20170516 ;
+--------+--------+--------+--------+
| type_1 | type_2 | type_3 | type_4 |
+--------+--------+--------+--------+
|      3 |      5 |      2 |      2 |
+--------+--------+--------+--------+
1 row in set (0.00 sec)

```

## 2.行转列

有下面一张表

```

mysql> SELECT  * from TabName ;
+----+------+------------+--------+
| Id | Name | Date       | Scount |
+----+------+------------+--------+
|  1 | 小说 | 2013-09-01 |  10000 |
|  2 | 微信 | 2013-09-01 |  20000 |
|  3 | 小说 | 2013-09-02 |  30000 |
|  4 | 微信 | 2013-09-02 |  35000 |
|  5 | 小说 | 2013-09-03 |  31000 |
|  6 | 微信 | 2013-09-03 |  36000 |
|  7 | 小说 | 2013-09-04 |  35000 |
|  8 | 微信 | 2013-09-04 |  38000 |
+----+------+------------+--------+
8 rows in set (0.00 sec)

```

列转行统计数据，这个和上面的列转行不同，这里是求列转行之后的max
```
mysql> SELECT Date ,
    -> MAX(CASE NAME WHEN '小说' THEN Scount ELSE 0 END ) 小说,
    -> MAX(CASE NAME WHEN '微信' THEN Scount ELSE 0 END ) 微信
    -> FROM TabName
    -> GROUP BY Date;
+------------+-------+-------+
| Date       | 小说  | 微信  |
+------------+-------+-------+
| 2013-09-01 | 10000 | 20000 |
| 2013-09-02 | 30000 | 35000 |
| 2013-09-03 | 31000 | 36000 |
| 2013-09-04 | 35000 | 38000 |
+------------+-------+-------+
4 rows in set (0.00 sec)


```

行转列统计数据,并对列进行合并

```
mysql> select
    ->     Date, group_concat(NAME,'总量:',Scount) as b_str from   TabName
    ->  group by Date;
+------------+-------------------------------+
| Date       | b_str                         |
+------------+-------------------------------+
| 2013-09-01 | 小说总量:10000,微信总量:20000 |
| 2013-09-02 | 小说总量:30000,微信总量:35000 |
| 2013-09-03 | 小说总量:31000,微信总量:36000 |
| 2013-09-04 | 小说总量:35000,微信总量:38000 |
+------------+-------------------------------+
4 rows in set (0.00 sec)

mysql> select Date,NAME, group_concat(NAME,'总量:',Scount) as b_str from   TabNa
me
    ->  group by Date ,NAME;
+------------+------+----------------+
| Date       | NAME | b_str          |
+------------+------+----------------+
| 2013-09-01 | 小说 | 小说总量:10000 |
| 2013-09-01 | 微信 | 微信总量:20000 |
| 2013-09-02 | 小说 | 小说总量:30000 |
| 2013-09-02 | 微信 | 微信总量:35000 |
| 2013-09-03 | 小说 | 小说总量:31000 |
| 2013-09-03 | 微信 | 微信总量:36000 |
| 2013-09-04 | 小说 | 小说总量:35000 |
| 2013-09-04 | 微信 | 微信总量:38000 |
+------------+------+----------------+
8 rows in set (0.00 sec)

mysql>


```
