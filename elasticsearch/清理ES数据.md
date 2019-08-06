[TOC]

# 定时任务清理

```shell
# cron clean netflow
00 01 * * * /usr/bin/curl  -XDELETE vmcorestor1:9200/netflow_$(date +"\%Y\%m\%d" -d "-1day") >/dev/null  2>&1 
```



# 脚本清理

```shell
#!/bin/sh
if [ $# == 2 ]; then
    datebeg=$1
    dateend=$2
else
    echo "请输入开始时间和结束日期，格式为2017-04-04"
    exit 1
fi

beg_s=`date -d "$datebeg" +%s`
end_s=`date -d "$dateend" +%s`

echo "time range : $beg_s 至 $end_s"

while [ "$beg_s" -le "$end_s" ];do
 day=`date -d @$beg_s +"%Y-%m-%d"`;

 #delete index expect netflow
 cnt=`curl -i XHEAD http://vmcorestor1:9200/*_$day|grep 200|wc -l` 
 if [ $cnt -eq 1 ];then
   echo "index exist...."
   curl -XDELETE 'vmcorestor1:9200/*_'$day
 fi

 #delete index netflow
 cnt=`curl -i XHEAD http://vmcorestor1:9200/netflow__$day|grep 200|wc -l`
 if [ $cnt -eq 1 ];then
   echo "index netflow exist...."
   curl -XDELETE 'vmcorestor1:9200/netflow_'$day
 fi

    echo "current date: $day"
    beg_s=$((beg_s+86400));
done


```

