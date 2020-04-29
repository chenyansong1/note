# mysql语法之case when语法


## 1.表的创建

```
CREATE TABLE `lee` (
`id` int(10) NOT NULL AUTO_INCREMENT, 
`name` char(20) DEFAULT NULL, 
`birthday` datetime DEFAULT NULL, 
PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8

#数据插入
insert into lee(name,birthday) values ('sam','1990-01-01');
insert into lee(name,birthday) values ('lee','1980-01-01');
insert into lee(name,birthday) values ('john','1985-01-01');
```

## 2.表数据如下
```
mysql> select * from lee;
+----+------+---------------------+
| id | name | birthday            |
+----+------+---------------------+
|  1 | sam  | 1990-01-01 00:00:00 |
|  2 | lee  | 1980-01-01 00:00:00 |
|  3 | john | 1985-01-01 00:00:00 |
+----+------+---------------------+
3 rows in set (0.00 sec)

```

## 2.case when用法

* 第一种用法
```	
SELECT name,
 CASE WHEN birthday < '1981' THEN 'old'
WHEN birthday > '1988' THEN 'yong'
 ELSE 'ok' END YORN
FROM lee;

+------+------+
| name | YORN |
+------+------+
| sam  | yong |
| lee  | old  |
| john | ok   |
+------+------+

```


* 第二种用法：
```
SELECT NAME, 
 CASE name
 WHEN 'sam' THEN 'yong'
 WHEN 'lee' THEN 'handsome'
 ELSE 'good' END as oldname
FROM lee;

+------+----------+
| NAME | oldname  |
+------+----------+
| sam  | yong     |
| lee  | handsome |
| john | good     |
+------+----------+

```

* 第三种：当然了，case when 语句还可以复合
```
select name, birthday,
 case
 when birthday > '1983' then 'yong'
 when name='lee' then 'handsome'
 else 'just so so' end as bir
from lee;


+------+---------------------+----------+
| name | birthday            | bir      |
+------+---------------------+----------+
| sam  | 1990-01-01 00:00:00 | yong     |
| lee  | 1980-01-01 00:00:00 | handsome |
| john | 1985-01-01 00:00:00 | yong     |
+------+---------------------+----------+

```







