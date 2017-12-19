---
title: RANK 和 DENSE_RANK(组内排名函数)
categories: hive   
toc: true  
tag: [hive]
---


* RANK() 生成数据项在分组中的排名，排名相等会在名次中留下空位
* DENSE_RANK() 生成数据项在分组中的排名，排名相等会在名次中不会留下空位



```
SELECT 
	cookieid,
	createtime,
	pv,
	RANK() OVER(PARTITION BY cookieid ORDER BY pv desc) AS rn1,
	DENSE_RANK() OVER(PARTITION BY cookieid ORDER BY pv desc) AS rn2,
	ROW_NUMBER() OVER(PARTITION BY cookieid ORDER BY pv DESC) AS rn3 
FROM lxw1234 
WHERE cookieid = 'cookie1';
 
cookieid day           pv       rn1     rn2     rn3 
-------------------------------------------------- 
cookie1 2015-04-12      7       1       1       1
cookie1 2015-04-11      5       2       2       2
cookie1 2015-04-15      4       3       3       3
cookie1 2015-04-16      4       3       3       4
cookie1 2015-04-13      3       5       4       5
cookie1 2015-04-14      2       6       5       6
cookie1 2015-04-10      1       7       6       7
 
rn1: 15号和16号并列第3, 13号排第5
rn2: 15号和16号并列第3, 13号排第4
rn3: 如果相等，则按记录值排序，生成唯一的次序，如果所有记录值都相等，或许会随机排吧。

```


