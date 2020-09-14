#!/bin/bash


#get INPUT ip list
ip_list=`iptables -n --line-number -L INPUT |awk 'NR>2 {print $5}'|grep -v  "0.0.0.0"`
#echo $ip_list

column_to_row=`echo $ip_list|tr " " ","|sed -e 's/,$/\n/'`
if [ $? -eq 0 ];then
	echo $column_to_row
fi








