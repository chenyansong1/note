#!/bin/bash


if [ $# -lt 2 ];then
	exit -1
fi

ip_str=$1
opera_flg=$2
#echo $ip_str:$opera_flg

#get INPUT ip list
ip_list=`iptables -n --line-number -L INPUT |awk 'NR>2 {print $5}'|grep -v  "0.0.0.0"`
#echo $ip_list

column_to_row=`echo $ip_list|tr " " ","|sed -e 's/,$/\n/'`

#echo $column_to_row

if [ "$opera_flg" = "delete" ];then
	#echo "delete"
	is_contain=`iptables -L -n --line-number|grep $ip_str|wc -l`
	if [ $is_contain -gt 0 ];then
		del_num=`iptables -L -n --line-number|grep $ip_str|head -1|cut -d " " -f1`
                /sbin/iptables -D INPUT $del_num 
                echo 1
        else
                echo 1
        fi

        exit 1
fi

if [ "$opera_flg" = "add" ];then
	is_contain=`echo $ip_list|grep $ip_str|wc -l`
	#echo $is_contain
	if [ $is_contain -gt 0 ];then
		echo 1
	else
		/sbin/iptables -I INPUT  -s $ip_str -j ACCEPT
		echo 1
	fi
	exit 1
fi

echo "-1"
exit 2 


