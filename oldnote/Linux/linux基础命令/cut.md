---
title: Linux基础命令之cut
categories: Linux   
toc: true  
tags: [Linux基础命令]
---

# 1.语法
``` shell

       -c, --characters=LIST
              select only these characters
 
       -d, --delimiter=DELIM
              use DELIM instead of TAB for field delimiter
 
       -f, --fields=LIST
              select  only  these fields;  also print any line that contains no delimiter
              character, unless the -s option is specified

```

# 2.举例
```
[root@linux-study cys_test]# echo "my name is chenyansong" >> name.txt
 
#d 分隔符，f字段
[root@linux-study cys_test]# cut -d " " -f2,4 name.txt
name chenyansong
 
#c 字符,13- 表示第13个字符到最后
[root@linux-study cys_test]# cut -c 1-11,13- name.txt
my name is
 

#截取指定字符串的列

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
 


