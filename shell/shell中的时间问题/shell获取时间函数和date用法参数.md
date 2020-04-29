# 1.shell获取时间：

```
date参数说明

-d datestr : 显示 datestr 中所设定的时间 (非系统时间) 
--help : 显示辅助讯息 
-s datestr : 将系统时间设为 datestr 中所设定的时间 
-u : 显示目前的格林威治时间 
--version : 显示版本编号 

#获取今天时期
`date +%Y%m%d` 或 `date +%F` 或 $(date +%y%m%d) 

#昨天 
dname1="$(date -d -1day +%Y%m%d)"
#前天 
dname1="$(date -d -2day +%Y%m%d)"
#大前天 
dname2="$(date -d -3day +%Y%m%d)"
#明天 
dname1="$(date -d 1day +%Y%m%d)"
#后天 
dname1="$(date -d 2day +%Y%m%d)"
#大后天
dname1="$(date -d 3day +%Y%m%d)"


#时间域 
% H 小时（00..23） 
% I 小时（01..12） 
% k 小时（0..23） 
% l 小时（1..12） 
% M 分（00..59） 
% p 显示出AM或PM 
% r 时间（hh：mm：ss AM或PM），12小时 
% s 从1970年1月1日00：00：00到目前经历的秒数 
% S 秒（00..59） 
% T 时间（24小时制）（hh:mm:ss） 
% X 显示时间的格式（％H:％M:％S） 
% Z 时区 日期域 
% a 星期几的简称（ Sun..Sat） 
% A 星期几的全称（ Sunday..Saturday） 
% b 月的简称（Jan..Dec） 
% B 月的全称（January..December） 
% c 日期和时间（ Mon Nov 8 14：12：46 CST 1999） 
% d 一个月的第几天（01..31） 
% D 日期（mm／dd／yy） 
% h 和%b选项相同 
% j 一年的第几天（001..366） 
% m 月（01..12） 
% w 一个星期的第几天（0代表星期天） 
% W 一年的第几个星期（00..53，星期一为第一天） 
% x 显示日期的格式（mm/dd/yy） 
% y 年的最后两个数字（ 1999则是99） 
% Y 年（例如：1970，1996等） 
注意：只有超级用户才有权限使用date命令设置时间，一般用户只能使用date命令显示时间
```


# 2.shell 时间函数整理

## 获取开始和结束日期内的时间

```
function get_days {  
    datebeg=$1
    dateend=$2

    beg_s=`date -d "$datebeg" +%s`
    end_s=`date -d "$dateend" +%s`
    while [ "$beg_s" -le "$end_s" ]
        do
            #  date -d '1970-01-01 UTC 1397059200 seconds' +"%Y-%m-%d"
            day=`date -d '1970-01-01 UTC '$beg_s' seconds' +"%Y%m%d"`
            echo $day
            beg_s=$((beg_s+86400))
        done
}

#调用
get_days '20170619' '20170622'

#打印结果
chenyansong@hadoop01209:~$ ./test_date.sh 
20170619
20170620
20170621
20170622

```

## 返回当月月末日期YYYYMMDD
```
get_cur_date()
{
Y=`expr substr $1 1 4`
M=`expr substr $1 5 2`
r1=`expr $Y % 4`
r2=`expr $Y % 100`
r3=`expr $Y % 400`

case $M in 01|03|05|07|08|10|12) days=31;; 04|06|09|11) days=30;;
esac
if [ $M -eq 02 ]
then if [ $r1 -eq 0 -a $r2 -ne 0 -o $r3 -eq 0 ]
then days=29
else days=28
fi
fi
echo $Y$M$days
}

#调用
get_cur_date $1

#执行结果
chenyansong@hadoop01209:~$ ./test_date.sh 20160222
20160229
chenyansong@hadoop01209:~$ ./test_date.sh 20180222
20180228
```

## 返回当月月份YYYYMM
```
get_cur_month()
{
Y=`expr substr $1 1 4`
M=`expr substr $1 5 2`
echo $Y$M
}
```


## 返回上月月份YYYYMM
```
get_last_month()
{
Y=`expr substr $1 1 4`
M=`expr substr $1 5 2`
M=`expr $M "-" 1`
if [ $M -eq 0 ];then
    let Y=Y-1
    M=12
else M=`printf "%02d" $M`
fi
echo $Y$M
}
```


## 判断是否闰年 
```
#input:year 
#output: "true" "fase" 
check_leap()
{
Y=`expr substr $1 1 4`
r1=`expr $Y % 4`
r2=`expr $Y % 100`
r3=`expr $Y % 400`
if [ $r1 -eq 0 -a $r2 -ne 0 -o $r3 -eq 0 ];then
    FRUN="true"
else
    FRUN="false"
fi

echo $FRUN
}

#调用
check_leap $1
```



 