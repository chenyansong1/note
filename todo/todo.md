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





/usr/local/pgsql
[root@master data]# /etc/init.d/postgresql status
pg_ctl: server is running (PID: 3850)
/usr/local/pgsql/bin/postgres "-D" "/usr/local/pgsql/data"
[root@master data]# /etc/init.d/postgresql
Usage: /etc/init.d/postgresql {start|stop|restart|reload|status}
[root@master data]# /etc/init.d/postgresql restart
Restarting PostgreSQL: ok
[root@master data]# 





