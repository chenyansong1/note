---
title:  python数据库之MySQLdb访问mysql数据库
categories: python   
toc: true  
tags: [python]
---


# 1.python连接mysql的第三方驱动
* pymysql
* MySQL-Python
* MySQL官方连接库（MySQL connector Python）

 

# 2.连接数据库的几种方式
```
import MySQLdb as mdb
 
# 连接数据库
conn = mdb.connect('localhost', 'root', 'root')
 
# 也可以使用关键字参数
conn = mdb.connect(host='127.0.0.1', port=3306, user='root', passwd='root', db='test', charset='utf8')
 
# 也可以使用字典进行连接参数的管理
config = {
    'host': '127.0.0.1',
    'port': 3306,
    'user': 'root',
    'passwd': 'root',
    'db': 'test',
    'charset': 'utf8'
}
conn = mdb.connect(**config)

```

# 3.游标（cursor）的方法
参见：python数据库之python DB API 2.0简绍

## 3.1.cursor.rowcount
返回查询结果的行数
```
#!/usr/bin/python
# -*- coding: utf-8 -*-
 
import MySQLdb as mdb
 
con = mdb.connect('localhost', 'testuser', 'test623', 'testdb');
 
with con:
 
    cur = con.cursor()
    cur.execute("SELECT * FROM Writers")
 
    for i in range(cur.rowcount):                #通过遍历行
 
        row = cur.fetchone()
        print row[0], row[1]

```

## 3.2.查询列名
```
In [34]: desc = cursor.description
 
In [35]: desc
Out[35]:
(('id', 3, 1, 11, 11, 0, 1),
('name', 253, 12, 20, 20, 0, 1),
('grade', 3, 3, 11, 11, 0, 1))

In [37]: print(desc[0][0], desc[1][0],desc[2][0])           
('id', 'name', 'grade')



```


# 4.简单的CRUD
```
#coding=utf-8
 
import MySQLdb
 
conn = MySQLdb.connect(host='localhost',user='root',passwd='123456',charset='utf8')
cursor = conn.cursor()
try:
    #创建数据库
    DB_NAME = 'test'
    cursor.execute('DROP DATABASE IF EXISTS %s' %DB_NAME)
    cursor.execute('CREATE DATABASE IF NOT EXISTS %s' %DB_NAME)                        #其实这里的%是python的str的格式化方法
    conn.select_db(DB_NAME)                                                            #选择数据库
 
    #创建表
    TABLE_NAME = 't_user'
    cursor.execute('CREATE TABLE %s(id int primary key,name varchar(30))' %TABLE_NAME)
 
    #插入单条数据
    value = [1,'alexzhou1']
   cursor.execute('INSERT INTO t_user values(%s,%s)',value)
   #cursor.execute("insert into student values(%(id)s,%(name)s,%(grade)s)" , {'id':6,'name':'haha','grade':100})     使用%(name)s作为占位符，以字典的方式提供参数
 
    #批量插入数据
    values = []
    for i in range(2,10):
        values.append((i,'alexzhou%s' %(str(i))))
    cursor.executemany('INSERT INTO t_user values(%s,%s)',values)
 
    #查询记录数量
    count = cursor.execute('SELECT * FROM %s' %TABLE_NAME)
    print 'total records: %d',count
 
    #查询一条记录
    print 'fetch one record:'
    result = cursor.fetchone()
    print result
    print 'id: %s,name: %s' %(result[0],result[1])
 
    #查询多条记录
    print 'fetch five record:'
    results = cursor.fetchmany(5)
    for r in results:
        print r
 
    #查询所有记录
    #重置游标位置，偏移量:大于0向后移动;小于0向前移动，mode默认是relative
    #relative:表示从当前所在的行开始移动,absolute:表示从第一行开始移动
    cursor.scroll(0,mode='absolute')
    results = cursor.fetchall()
    for r in results:
        print r
 
    cursor.scroll(-2)
    results = cursor.fetchall()
    for r in results:
        print r
 
    #更新记录
    cursor.execute('UPDATE %s SET name = "%s" WHERE id = %s' %(TABLE_NAME,'zhoujianghai',1))
    #删除记录
    cursor.execute('DELETE FROM %s WHERE id = %s' %(TABLE_NAME,2))
 
    #必须提交，否则不会插入数据
    conn.commit()
except:
    import traceback
    traceback.print_exc()
    # 发生错误时会滚
    conn.rollback()
finally:
    # 关闭游标连接
    if cursor:
       cursor.close()
    # 关闭数据库连接
    if conn:  
        conn.close()
```
 
 
 


# 5.查询时返回字典结构
MySQLdb默认查询结果都是返回tuple，通过使用不同的游标可以改变输出格式，这里传递一个cursors.DictCursor参数。
```
#方式一
import MySQLdb.cursors
 
conn = MySQLdb.connect(host='localhost', user='root', passwd='root', db='test', cursorclass=MySQLdb.cursors.DictCursor)            #在连接的时候指定 DictCursor
cursor = conn.cursor()
 
cursor.execute('select * from user')
r = cursor.fetchall()
print r
# 当使用位置参数或字典管理参数时，必须导入MySQLdb.cursors模块
 
-------------------------------------------------------

#方式二
import MySQLdb as mdb
conn  = mdb.connect('localhost', 'root', 'root', 'test')
cursor = conn.cursor(cursorclass=mdb.cursors.DictCursor)                            #在返回游标的时候指定
 
cursor.execute('select * from user')
r = cursor.fetchall()
print r


#--------------------          执行结果如下            ------------------------

In [24]: cursor = conn.cursor(cursorclass=MySQLdb.cursors.DictCursor)
 
In [25]: cursor.execute("select *from student")                     
Out[25]: 6L
 
In [26]: for item in cursor:                                        
    print(item)
   ....:    
{'grade': 99L, 'id': 1L, 'name': 'chengyansong'}
{'grade': 99L, 'id': 2L, 'name': 'zhangsan'}
{'grade': 99L, 'id': 2L, 'name': 'zhangsan'}
{'grade': 88L, 'id': 7L, 'name': 'aaaaa'}
{'grade': 0L, 'id': 6L, 'name': 'haha'}
{'grade': 100L, 'id': 6L, 'name': 'haha'}


```
 


# 6.存入和取出图片数据
```
#存入
#!/usr/bin/python
# -*- coding: utf-8 -*-
 
import MySQLdb as mdb
 
 
def read_image():
 
    fin = open("woman.jpg")   
    img = fin.read()
 
    return img
 
 
con = mdb.connect('localhost', 'testuser', 'test623', 'testdb')
 
with con:
 
    cur = con.cursor()
    data = read_image()            #得到图片数据
    cur.execute("INSERT INTO Images VALUES(1, %s)", (data, ))       #存入存入数据库

#取出
#!/usr/bin/python
# -*- coding: utf-8 -*-
 
import MySQLdb as mdb
 
def writeImage(data):
 
    fout = open('woman2.jpg', 'wb')
 
    with fout:
 
        fout.write(data)
 
con = mdb.connect('localhost', 'testuser', 'test623', 'testdb')
 
with con:
 
    cur = con.cursor()
 
    cur.execute("SELECT Data FROM Images WHERE Id=1")
    data = cur.fetchone()[0]
    writeImage(data)   

```


