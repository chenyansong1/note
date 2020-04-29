
# 1.描述
lateral view用于和split, explode等UDTF一起使用，**它能够将一行数据拆成多行数据，在此基础上可以对拆分后的数据进行聚合**。lateral view首先为原始表的每行调用UDTF，UTDF会把一行拆分成一或者多行，lateral view再把结果组合，产生一个支持别名表的虚拟表。

 

# 2.例子

假设我们有一张表pageAds，它有两列数据，第一列是pageid string，第二列是adid_list，即用逗号分隔的广告ID集合：


|string pageid|Array<int> adid_list|
|-------------|--------------------|
|"front_page"	|[1, 2, 3]         |
|"contact_page"|[3, 4, 5]          |


 

要统计所有广告ID在所有页面中出现的次数。

首先分拆广告ID：
```
SELECT pageid, adid 
    FROM pageAds LATERAL VIEW explode(adid_list) adTable AS adid;
```
执行结果如下：

|string pageid|int adid|
|-------------|--------|
|"front_page"	|1     |
|"front_page"	|2     |
|"front_page"	|3     |
|"contact_page"|3      |
|"contact_page"|4      |
|"contact_page"|5      |
 

接下来就是一个聚合的统计：
```
SELECT adid, count(1) 
    FROM pageAds LATERAL VIEW explode(adid_list) adTable AS adid
GROUP BY adid;
```

执行结果如下：

|adid	|count(1)|
|-------|--------|
|1		|1       |
|2		|1       |
|3		|2       |
|4		|1       |
|5		|1       |


# 3.多个lateral view语句

一个FROM语句后可以跟多个lateral view语句，后面的lateral view语句能够引用它前面的所有表和列名。 以下面的表为例：

|Array<int> col1	|Array<string> col2|
|-------------------|------------------|
|[1, 2]				|[a", "b", "c"]    |
|[3, 4]				|[d", "e", "f"]    |

 
```
SELECT myCol1, col2 FROM baseTable
    LATERAL VIEW explode(col1) myTable1 AS myCol1;
```

执行结果为：

|int mycol1|Array<string> col2|
|----------|------------------|
|1			|[a", "b", "c"]   |
|2			|[a", "b", "c"]   |
|3			|[d", "e", "f"]   |
|4			|[d", "e", "f"]   |
 

加上一个lateral view：
```
SELECT myCol1, myCol2 FROM baseTable
    LATERAL VIEW explode(col1) myTable1 AS myCol1
    LATERAL VIEW explode(col2) myTable2 AS myCol2;
```

它的执行结果为：

|int myCol1	|string myCol2|
|-----------|-------------|
|1			|"a"          |
|1			|"b"          |
|1			|"c"          |
|2			|"a"          |
|2			|"b"          |
|2			|"c"          |
|3			|"d"          |
|3			|"e"          |
|3			|"f"          |
|4			|"d"          |
|4			|"e"          |
|4			|"f"          |


注意上面语句中，两个lateral view按照出现的次序被执行。


# 如果是map结构

字段			类型
clickitems	map<String,int>

map.put("zhangsan",1)
map.put("zhangsan2",2)
map.put("zhangsan3",3)

需要将上述map转成多行，如下：

查询结果如下：
```
hive> select clickitems from tb_mbclick_day where dt=20170802 limit 2;
OK
{"70000001":1}
{"record_stop_click":1}

hive> select dim_name,dim_value from tb_mbclick_day  LATERAL VIEW explode(clickitems) myTable1 AS dim_name,dim_value where dt=20170802 limit 2;
OK
70000001        1
record_stop_click       1

```







参见:

http://www.cnblogs.com/ggjucheng/archive/2013/01/03/2842938.html