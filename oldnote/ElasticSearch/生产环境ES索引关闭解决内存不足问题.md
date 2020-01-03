[TOC]

# segment memory占用

## 删除段

```shell
[root@es01 ~]# crontab -l
*/1 * * * * /usr/sbin/ntpdate 10.130.10.221>/dev/null 2>&1
00 01 * * * /usr/bin/curl  -XDELETE es01:9200/netflow_$(date +"\%Y\%m\%d" -d "-1day") >/dev/null  2>&1
00 01 * * * /usr/bin/curl  -XDELETE es01:9200/dns_$(date +"\%Y\%m\%d" -d "-1day") >/dev/null  2>&1
00 01 * * * /usr/bin/curl  -XDELETE es01:9200/http_$(date +"\%Y\%m\%d" -d "-1day") >/dev/null  2>&1
```



## 打开或者关闭索引

比如说，我们想关闭 syslog_20191230索引：

```cmd
curl -X POST http://127.0.0.1:9200/syslog_20191230/_close

#curl -X POST http://127.0.0.1:9200/syslog_20191230/_close?pretty
```

通过如下请求，我们可以打开一个被关闭的索引：

```cmd
POST http://127.0.0.1:9200/syslog_20191230/_open
#curl -X POST http://127.0.0.1:9200/syslog_20191230/_open?pretty
```

```cmd
#关闭15天以前的索引
#!/bin/bash

#merge netflow dns http 只是合并前一天的数据，因为只保留2天的数据
#merge syslog 合并所以时间段的数据

#es config
es_host=10.119.248.90
#index name
day15_date=`date  "+%Y%m%d" -d "-15day"`

index_syslog=syslog_$yesterday_date
index_genlog=genlog_$yesterday_date
index_event=event_$yesterday_date

index_array=(
$index_syslog
$index_genlog
$index_event
)

for index_name in "${index_array[@]}";do
    #is exist
    index_cnt=`curl -XHEAD -i http://$es_host:9200/$index_name/$index_name|grep 200|wc -l`
    echo "$index_name index_cnt = $index_cnt ####################################"
    [ $index_cnt -le 0 ]&& continue

    #index close
   	curl -XPOST http://$es_host:9200/$index_name/_close
    
    #10min
    sleep 600
done


#定时任务
[root@es01 ~]# crontab -l
*/1 * * * * /usr/sbin/ntpdate 10.130.10.221>/dev/null 2>&1
00 01 * * * /usr/bin/curl  -XDELETE es01:9200/netflow_$(date +"\%Y\%m\%d" -d "-1day") >/dev/null  2>&1
00 01 * * * /usr/bin/curl  -XDELETE es01:9200/dns_$(date +"\%Y\%m\%d" -d "-1day") >/dev/null  2>&1
00 01 * * * /usr/bin/curl  -XDELETE es01:9200/http_$(date +"\%Y\%m\%d" -d "-1day") >/dev/null  2>&1
00 02 * * * /bin/bash /root/merge_segments_es.sh

```

手动执行一次关闭指定的索引

```shell
#!/bin/bash

#merge netflow dns http 只是合并前一天的数据，因为只保留2天的数据
#merge syslog 合并所以时间段的数据

#es config
es_host=10.119.248.90
#index name
day15_date=`date  "+%Y%m%d" -d "-15day"`

index_syslog=syslog_$yesterday_date
index_genlog=genlog_$yesterday_date
index_event=event_$yesterday_date

#拿到所有的索引，可以手动去掉最近15天的数据，然后填入下面的数组中
#curl es:9200/_cat/indices|grep -v close|grep open|uniq |sort -n|awk -F " " '{print $3}'

index_array=(
$index_syslog
$index_genlog
$index_event
)

for index_name in "${index_array[@]}";do
    #is exist
    index_cnt=`curl -XHEAD -i http://$es_host:9200/$index_name/$index_name|grep 200|wc -l`
    echo "$index_name index_cnt = $index_cnt ####################################"
    [ $index_cnt -le 0 ]&& continue

    #index close
   	curl -XPOST http://$es_host:9200/$index_name/_close
    
    #10min
    sleep 600
done
```





## 段合并

段合并不应该用在一个活跃的索引上，而是应该应用于一个数据不会变化的索引上

```shell
#!/bin/bash

#merge netflow dns http 只是合并前一天的数据，因为只保留2天的数据
#merge syslog 合并所以时间段的数据

#es config
es_host=10.119.248.90
#index name
yesterday_date=`date  "+%Y%m%d" -d "-1day"`
index_netflow=netflow_$yesterday_date
index_http=http_$yesterday_date
index_dns=dns_$yesterday_date
index_syslog=syslog_$yesterday_date
index_genlog=genlog_$yesterday_date
index_event=event_$yesterday_date

index_array=(
$index_syslog
$index_genlog
$index_event
)

for index_name in "${index_array[@]}";do
    #is exist
    index_cnt=`curl -XHEAD -i http://$es_host:9200/$index_name/$index_name|grep 200|wc -l`
    echo "$index_name index_cnt = $index_cnt ####################################"
    [ $index_cnt -le 0 ]&& continue
    
    #if segments <10 ,do nothing
    segments_cnt=`curl http://$es_host:9200/_cat/segments/$index_name|wc -l`
    echo "$index_name segments_cnt = $segments_cnt ####################################"
    [ $segments_cnt -le 10 ]&& continue
    #segments merge
    #echo "http://$es_host:9200/$index_name/_forcemerge?max_num_segments=1"
   	curl -XPOST http://$es_host:9200/$index_name/_forcemerge?max_num_segments=1
    
    #10min
    sleep 600
done


#定时任务
[root@es01 ~]# crontab -l
*/1 * * * * /usr/sbin/ntpdate 10.130.10.221>/dev/null 2>&1
00 01 * * * /usr/bin/curl  -XDELETE es01:9200/netflow_$(date +"\%Y\%m\%d" -d "-1day") >/dev/null  2>&1
00 01 * * * /usr/bin/curl  -XDELETE es01:9200/dns_$(date +"\%Y\%m\%d" -d "-1day") >/dev/null  2>&1
00 01 * * * /usr/bin/curl  -XDELETE es01:9200/http_$(date +"\%Y\%m\%d" -d "-1day") >/dev/null  2>&1
00 02 * * * /bin/bash /root/merge_segments_es.sh
```



参看：https://www.cnblogs.com/seaspring/p/9231774.html

# 清除查询缓存

这个只是治标不治本

```shell
#清除指定的缓存
curl -XPOST es:9200/syslog_20200101/_cache/clear

#清除所有的缓存
curl -XPOST es:9200/_cache/clear
```





索引优化策略：

1. ES中的字段能不能精简
2. 默认所有的索引都建模板，模板中，所有的字段不分词，个别需要分词的，只是单独分词
3. 进行一天一次的段合并
4. genlog不存储
5. syslog,event只存储7天，其他的默认关闭，有用到再打开
6. netflow，http只存储一天数据，提供统计
7. 修改了ES中原来的配置文件参数，待观察是否有效
8. 修改了JVM的参数，保持一半内存在JVM，一个给机器的Lucene使用
9. 减少http和netflow中的数据量：减少不必要的数据存储
   1. 观察到其中有很大一部分的数据是来自：百度的api，这部分数据对我们是没有用的
   2. 根据业务需求看看 这部分数据到底要不要保留



参见：

https://www.cnblogs.com/seaspring/p/9231774.html





https://elasticsearch.cn/question/5279

https://elasticsearch.cn/question/3838

https://elasticsearch.cn/question/4136

https://www.elastic.co/guide/en/elasticsearch/reference/6.2/cluster-allocation-explain.html

https://elasticsearch.cn/question/4714

https://elasticsearch.cn/question/3135

https://elasticsearch.cn/question/9090

https://www.wenyuanblog.com/blogs/elasticsearch-memory-considerations.html







