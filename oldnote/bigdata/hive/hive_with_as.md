hive 可以用with as的方法把表加入内存，其他语句可以随时使用（这样在多次使用的时候，不用重复查询多次）。
```
with q1 as (select * from src where key= ‘5’),
q2 as (select * from src s2 where key = ‘4’)
select * from q1 union all select * from q2;

#复杂一点的如：
hql="use adw;
add jar /usr/local/hive/udf-lib/aipai-udf-0.0.1-SNAPSHOT.jar;
create temporary function uapt as 'com.aipai.udf.UDFUADetect4webH5';

with a as(select zhiboid,substr(clitime-clitime%60000,0,10) as sta_time,uapt(ua) as platform,cookieid as mc_uid from tb_webzhibolog_day where dt=${dt}),
b as(select zhiboid,substr(clitime-clitime%60000,0,10) as sta_time,(CASE
  WHEN ostype == 'ios' THEN 'ios'
  WHEN ostype == 'Android' THEN 'android'
  ELSE 'other'
END) as platform,machineid as mc_uid from tb_mbzhibolog_day where dt=${dt}),
c as(select * from a union all select * from b),
d as(select zhiboid,sta_time,(CASE
  WHEN platform == 'pc' THEN 1
  WHEN platform == 'android' THEN 2
  WHEN platform == 'ios' THEN 3
  WHEN platform == 'androidh5' THEN 4
  WHEN platform == 'iosh5' THEN 5
  ELSE -1
END) as platform_num ,mc_uid from c)
select platform_num,zhiboid,count(distinct mc_uid),sta_time from d group by zhiboid,platform_num,sta_time;
"

```

