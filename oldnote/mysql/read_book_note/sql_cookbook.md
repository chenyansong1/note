[TOC]

# 第1章 检索记录

## 1.1.从表中检索所有的行和列

```
SELECT * FROM emp;

/*
不建议写*，因为别人在读你的代码的时候，并不能已下子知道你的表的字段，所以建议分别指定每一列
*/
```



## 1.6.在WHERE子句中引用取别名的列

如果想通过别名的方式进行过滤，如下

```
SELECT sal as salary, comm as commission
FROM emp
WHERE salary < 5000;

#会报错
[Err] ERROR:  column "salary" does not exist
```



上面，我们对“sal" 列取了别名”salary“，然后在WHERE中使用这个别名去过滤，但是很遗憾报错了



> 报错的原因：
>
> SQL的解析过程是这样的，首先解析的是FROM子句，然后是WHERE子句，最后才是SELECT子句，所以**在WHER子句中使用SELECT子句定义的别名**，肯定出错



**解决的方式**

```
SELECT * 
FROM(
    SELECT sal as salary, comm as commission
    FROM emp
) X
WHERE salary < 5000;

/*因为FROM子句中有了别名的定义，所以在执行WHERE子句的时候，就可以使用别名了*/
```



## 1.7.连接列值

如果我们想要将多列的值，连接为一列展示

* Oracle， PostgreSQL

```
SELECT 
(ename ||'works as a ' ||job) as msg
FROM emp
WHERE deptno=10
```



* MySQL

```
#在MySQL中数据库支持CONCAT函数
SELECT 
CONCAT(ename, ' works as a ', job) as msg
FROM emp
WHERE deptno=10;
```



