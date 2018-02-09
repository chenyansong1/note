---
title: 获取索引的文档数-shell脚本
categories: elasticsearch   
toc: true  
tag: [elasticsearch]
---


```
#!/bin/bash


dateStr=`date +%Y%m%d`


while true;do
        qqqsyslog=`curl  -XGET 'http://10.193.13.15:9200/_cat/indices?v'|grep qqqsyslog_$dateStr`
        qqqgenlog=`curl  -XGET 'http://10.193.13.15:9200/_cat/indices?v'|grep qqqgenlog_$dateStr`
        qqqevent=`curl  -XGET 'http://10.193.13.15:9200/_cat/indices?v'|grep qqqevent_$dateStr`

        echo "#########################################"
        echo $qqqsyslog | cut -f6 -d" "
        echo $qqqgenlog | cut -f6 -d" "
        echo $qqqevent | cut -f6 -d" "
        echo "#########################################"
        sleep 60
done

```


