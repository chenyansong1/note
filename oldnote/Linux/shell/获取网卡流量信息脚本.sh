#!bin/bash

data_file=/root/if_netflow_info.txt

cnt=`ifconfig|grep  BROADCAST|wc -l`

for((i=0;i<$cnt;i++));do
        start=$((i*5+1))
        end=$((start+4))
        if_name=`ifconfig|grep -A 3 BROADCAST|sed -n "$start,${end}p"|grep BROADCAST|awk -F ":" '{print $1}'`
        rx_pack=`ifconfig|grep -A 3 BROADCAST|sed -n "$start,${end}p"|grep RX|awk '{print $5}'`

        time_stamp=`date "+%s"` 
        if [[ $if_name == vEth* ]];then
                echo $if_name":"$time_stamp"~"$rx_pack >> $data_file
        fi        
        
done