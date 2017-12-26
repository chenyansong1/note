http://blog.csdn.net/qq_15581405/article/details/54614930
https://loveltyoic.github.io/blog/2016/01/08/spark-es/


http://blog.csdn.net/u012373815/article/details/53266301
http://blog.csdn.net/stark_summer/article/details/49743687

https://www.elastic.co/guide/en/elasticsearch/hadoop/master/spark.html#spark-sql

http://www.ruanyifeng.com/blog/2013/12/getting_started_with_postgresql.html


1.将所有的字段补充完整
2.特殊字段的处理，如：date，int
3.清理程序的完整
4.调试

0.测试不同的数据类型是否可以插入
1.测试新的execute接口的执行速度
2.让每个线程跑一遍大数据量的看看


1.现在没有数据，需要1千万条数据
2.测试通过多线程，看下插入的性能
3.


1.shell脚本中执行copy
2.造一批100w条的数据
3.使用脚本进行测试，看入库的时间

4.如果时间测试OK的话，那么将ES数据批量导出CVS，使用脚本插入

#导入文件
172.16.10.112:/tmp/tbl_test1.csv
#导入语句
BDSSA1=# copy t_siem_general_log(dublecount,recordid,reportapp,reportip,sourceip,sourceport,destip,destport,eventaction,actionresult,reportnetype,eventdefid,eventname,eventlevel,orgid,infoid,affectedsystem,attackmethod,appid,victimtype,attacker,victim,host,filemd5,filedir,referer,requestmethod,firstrecvtime) from '/tmp/tbl_test1.csv' delimiter ',';

sense:
http://172.16.14.21:5601/app/sense

需要安装es的Python插件，这里直接安装的话，那么使用的是最新的版的，看看能不能指定版本：
pip install elasticsearch




pg 在shell中执行
http://bbs.csdn.net/topics/370171611

不输入密码：
http://blog.csdn.net/fm0517/article/details/53130244
http://www.postgres.cn/docs/9.3/app-psql.html
http://blog.csdn.net/xocoder/article/details/24368107


psql --c 执行SQL语句

highgo@sourcedb ~]$ psql --c 'select version();'
              version               
------------------------------------
 HighGo Database 3.1.4 Linux 64-bit
(1 row)

试试这个：shell脚本中，最好使用su -l postgres psql -U xxx -W password -c "";


# ES的分页

```
GET /_search?size=5
GET /_search?size=5&from=5
GET /_search?size=5&from=10

```

https://www.elastic.co/guide/cn/elasticsearch/guide/cn/pagination.html
https://www.elastic.co/guide/cn/elasticsearch/guide/cn/_ranges.html



