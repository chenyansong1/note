#!/bin/bash



echo "####################### syslog , genlog, event index of ES, close index start #######################"

#es config
es_host=es
#index name
day_week_date=`date  "+%Y%m%d" -d "-7day"`

index_syslog=syslog_$day_week_date
index_event=event_$day_week_date

#拿到所有的索引
index_array=`curl es:9200/_cat/indices|grep -v close|grep open|uniq |sort -n|awk -F " " '{print $3}'`
index_array=($index_array)

for index_name in "${index_array[@]}";do

    index_dt=`echo $index_name|cut -d "_" -f2`
	#close index if dt le day_week_date
    if [ $index_dt -le $day_week_date ];then
        echo "$index_name"
        #index close
        curl -XPOST http://$es_host:9200/$index_name/_close
        sleep 1
    fi
    
done



echo "####################### syslog , genlog, event index of ES, close index end #######################"
