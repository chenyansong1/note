---
title: row number over 函数(生成分组内记录的序列)
categories: hive   
toc: true  
tag: [hive]
---



ROW_NUMBER() 从1开始，按照顺序，生成分组内记录的序列
比如，按照pv降序排列，生成分组内每天的pv名次,ROW_NUMBER() 的应用场景非常多，再比如，获取分组内排序第一的记录;获取一个session中的第一条refer等。

```

SELECT 
	cookieid,
	createtime,
	pv,
	ROW_NUMBER() OVER(PARTITION BY cookieid ORDER BY pv desc) AS rn 
FROM lxw1234;
 
cookieid day           pv       rn(这是生成的排名列)
------------------------------------------- 
cookie1 2015-04-12      7       1
cookie1 2015-04-11      5       2
cookie1 2015-04-15      4       3
cookie1 2015-04-16      4       4
cookie1 2015-04-13      3       5
cookie1 2015-04-14      2       6
cookie1 2015-04-10      1       7
cookie2 2015-04-15      9       1
cookie2 2015-04-16      7       2
cookie2 2015-04-13      6       3
cookie2 2015-04-12      5       4
cookie2 2015-04-14      3       5
cookie2 2015-04-11      3       6
cookie2 2015-04-10      2       7


```

 
