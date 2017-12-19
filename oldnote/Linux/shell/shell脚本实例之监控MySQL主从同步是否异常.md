---
title: shell脚本实例之监控MySQL主从同步是否异常
categories: shell   
toc: true  
tags: [shell,mysql]
---



# 1.问题描述
&emsp;（生产实战案例）：监控MySQL主从同步是否异常，如果异常，则发送短信或者邮件给管理员。提示：如果没主从同步环境,可以用下面文本放到文件里读取来模拟：
* 阶段1：开发一个守护进程脚本每30秒实现检测一次。
* 阶段2：如果同步出现如下错误号（1158,1159,1008,1007,1062），则跳过错误。
* 阶段3：请使用数组技术实现上述脚本（获取主从判断及错误号部分）





# 2.分析

1. 监控是否同步
2. 忽略特定的错误号



# 3.脚本实现

```
#!/bin/sh
error_code=(1158 1159 1008 1007 1062);
COM="mysql -uroot -poldboy456 -S /data/3307/mysql.sock"
main (){
        while true ;do
                status=($($COM -e "show slave status\G;"|egrep "_Running|Seconds_Behind_Master|Last_SQL_Errno"|awk '{print $NF}'));
                #judge slave is ok
                if [ "${status[0]}" == "Yes" -a "${status[1]}" == "Yes" -a "${status[2]}" == "0" ];then
                        echo "mysql slave is ok";
                else
                        #inogre errors
                        for ((i=0;i<${#error_code[*]};i++));do
                                if [ ${error_code[i]} -eq ${status[3]} ];then
                                        $COM -e "stop slave;set global sql_slave_skip_counter=1;start slave;";
                                        echo "mysql slave is not ok.";
                                fi
                        done
                        echo "mysql slave is not ok"|mail -s "mysql_slave_status:" 1327401579@qq.com;
                fi
                sleep 30;
        done
}

```



