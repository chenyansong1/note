---
title: hive中常见的查询
categories: hive  
tags: [hive]
---




# 常见的查询语句

```

SELECT [ALL | DISTINCT] select_expr, select_expr, ...
  FROM table_reference
  [WHERE where_condition]
  [GROUP BY col_list]
  [ORDER BY col_list]
  [CLUSTER BY col_list
    | [DISTRIBUTE BY col_list] [SORT BY col_list]
  ]
 [LIMIT number]






hive (default)> select * from emp limit 5;

hive (default)> select * from emp t where t.sal between 800 and 1000;

hive (default)> select * from emp t where t.comm is null;

hive (default)> select count(*) cnt from emp;

hive (default)> select max(sal) max_sal from emp;

hive (default)> select sum(sal) sum_sal from emp;

hive (default)> select avg(sal) from emp;

```




# group by / having

```
#每个部门的平均工资
select t.deptno, avg(t.sal) avg_sal from emp t group by t.deptno


#每个部门中每个岗位的最高薪水
select t.deptno,t.job,max(t.sal) max_sal from emp t group by t.deptno, t.job;



where是针对单条记录进行筛选的
having是针对分组结果进行组内筛选的


#每个部门的平均薪水大于2000的部门
select t.deptno, avg(t.sal) avg_sal from emp t group by t.deptno having avg_sal > 2000 ;


```


# join操作

```
#等值join
select e.empno, e.ename, d.deptno, d.name from emp e 
join dept d on e.deptno=d.deptno ;


#左连接:left join

select e.empno, e.ename, d.deptno, d.name from emp e 
left join dept d on e.deptno=d.deptno ;


#右连接:right join

select e.empno, e.ename, d.deptno, d.name from emp e 
right join dept d on e.deptno=d.deptno ;


#全连接full join
select e.empno, e.ename, d.deptno, d.name from emp e 
full join dept d on e.deptno=d.deptno ;

```


# order by

是对全局数据的排序,仅仅只有一个reduce,如果查询的结果集比较大的时候,会出现内存溢出
```
select * from emp order by empno desc ;

```

# sort by

对每一个reduce内部数据进行排序,对全局的结果集来说不是排序的

```
set mapreduce.job.reduces=3;

select * from emp sort by empno asc ;

insert overwrite local directory '/tmp/datas/sortby-res'
select * from emp sort by empno asc ;


[root@hdp-node-01 sortby-res]# ll /tmp/datas/sortby-res/
total 12
-rw-r--r-- 1 root root 573 Apr 24 15:18 000000_0
-rw-r--r-- 1 root root 468 Apr 24 15:18 000001_0
-rw-r--r-- 1 root root 281 Apr 24 15:18 000002_0
[root@hdp-node-01 sortby-res]# cat 000000_0 
7369SMITHCLERK79021980-12-17800.0\N20
7566JONESMANAGER78391981-4-22975.0\N20
7654MARTINSALESMAN76981981-9-281250.01400.030
7654MARTINSALESMAN76981981-9-281250.01400.030

```


# distribute by

类似于MapReduce中的分区partition的功能,对数据进行分区,通常结果sort by进行使用

```
set mapreduce.job.reduces=3;

#按部门尽心分区,那么相同的部门将分到一个reduce中,然后sort 排序,就是在一个部门中进行排序了
select * from emp distribute by deptno sort by empno asc ;


insert overwrite local directory '/tmp/datas/distributeby-res'
select * from emp distribute by deptno sort by empno asc ;


#注意distribute by要在sort by之前

#查看结果
[root@hdp-node-01 datas]# ll /tmp/datas/distributeby-res/
total 12
-rw-r--r-- 1 root root 586 Apr 24 15:26 000000_0
-rw-r--r-- 1 root root 278 Apr 24 15:26 000001_0
-rw-r--r-- 1 root root 458 Apr 24 15:26 000002_0

#可以看到在000000_0中的都是部门号为30的,而且empno是升序排列的
[root@hdp-node-01 datas]# cat /tmp/datas/distributeby-res/000000_0 
7499ALLENSALESMAN76981981-2-201600.0300.030
7499ALLENSALESMAN76981981-2-201600.0300.030
7521WARDSALESMAN76981981-2-221250.0500.030
7521WARDSALESMAN76981981-2-221250.0500.030
7654MARTINSALESMAN76981981-9-281250.01400.030
7654MARTINSALESMAN76981981-9-281250.01400.030
7698BLAKEMANAGER78391981-5-12850.0\N30
7698BLAKEMANAGER78391981-5-12850.0\N30
7844TURNERSALESMAN76981981-9-81500.00.030
7844TURNERSALESMAN76981981-9-81500.00.030
7900JAMESCLERK76981981-12-3950.0\N30
7900JAMESCLERK76981981-12-3950.0\N30
```

# cluster by 

当distribute by 和sort by字段相同时,可以使用cluster by代替

```
select * from emp cluster by deptno ;

```

