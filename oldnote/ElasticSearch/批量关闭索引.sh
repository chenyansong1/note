#!/bin/bash


#es config
es_host=10.119.248.90
#index name
day_week_date=`date  "+%Y%m%d" -d "-7day"`

index_syslog=syslog_$day_week_date
index_event=event_$day_week_date

#拿到所有的索引，可以手动去掉最近15天的数据，然后填入下面的数组中
#
index_array=`curl es:9200/_cat/indices|grep -v close|grep open|uniq |sort -n|awk -F " " '{print $3}'`


for index_name in "${index_array[@]}";do
	index_dt=`echo $index_name|cut -d "_" -f2`

    if [ $index_dt -le $day_week_date ]&& echo "$index_name"

    #index close
   	#curl -XPOST http://$es_host:9200/$index_name/_close
    
    #10min
    #sleep 600
done

