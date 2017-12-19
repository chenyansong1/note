---
title:  python数据库之python DB API 2.0简绍
categories: python   
toc: true  
tags: [python]
---




# 1.目的
* 统一数据库操作的界面
* 切换后台数据库时，几乎不用更改数据库操作的有关代码
* 官网：https://www.python.org/dev/peps/pep-0249/
 

# 2.连接对象

* connect()函数成功连接后返回的对象
* 方法
    1. cursor()返回操作数据库的游标
    2. commit()事务提交
    3. rollback()事务回滚
    4. close()关闭连接

# 3.游标对象
 
* 连接对象的cursor()方法返回游标对象
* 方法
    1. callproc(pname [,params])  调用存储过程
    2. execute(sqlstr [,params]) 执行sql查询语句
    3. executemany(sqlstr [,seq-params]) 以序列中的参数执行多次sql查询
    4. fetchone() 获取查询结果中的一个数据行
    5. fetmany(size) 获取查询结果中指定数量的数据行
    6. fetchall()获取查询结果的所有行
    7. nextset() 获取所有查询结果集当前结果集的下一个结果集
    8. 可迭代获取结果
* 属性
    * arraysize
    * setinputsize
    * setoutputsize


# 4.操作异常
* StandardError
    * Waring
    * Error 
        * InterfaceError
        * DataBaseError

 
# 5.参数形式
* sql查询语句中的使用参数时占位符的形式由模块级变量paramstyle的值指定
* qmark:(?)
* numeric:(:1)
* named:(:name)
* format:(%s)
    cursor.executemany('INSERT INTO t_user values(%s,%s)',(1,'zhangsan'))
* pyformat:(%(name)s)
    cursor.execute("insert into student values(%(id)s,%(name)s,%(grade)s)" , {'id':6,'name':'haha','grade':100})     使用%(name)s作为占位符，以字典的方式提供参数
* pyformat:(%(name)d)


