todo
# unix_timestamp在hive中的不同

```
#转化UNIX时间戳（从1970-01-01 00:00:00 UTC到指定时间的秒数）到当前时区的时间格式
hive> select from_unixtime(1323308943,'yyyyMMdd') from dual;
20111208


hive> select unix_timestamp('20100110');
NULL

hive> select unix_timestamp('20100110 00:00:00');
NULL

hive> select unix_timestamp('2010-01-10');
NULL

#只有下面的这种写法可以拿到时间戳，其他的情况都不能拿到时间戳
hive> select unix_timestamp('2010-01-10 00:00:00');
1263052800





```



http://www.jianshu.com/p/e30395941f9c

https://my.oschina.net/u/2438020/blog/495235

http://dacoolbaby.iteye.com/blog/1826307


# unix时间戳转时间函数



```
#语法: from_unixtime(bigintunixtime[, string format])
#返回值: string
#说明: 转化UNIX时间戳（从1970-01-01 00:00:00 UTC到指定时间的秒数）到当前时区的时间格式
 
举例：
hive>selectfrom_unixtime(1323308943,‘yyyyMMdd’)fromdual;
20111208
```