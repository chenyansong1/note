[toc]

https://blog.csdn.net/qq_23518283/article/details/101167405



# 1.创建时间和更新时间

记录第一次创建的时间：default
记录每次更新数据的时间：onupdate
数据库设计如下：

```shell
## 创建时间
create_at = db.Column(db.DateTime, default=datetime.datetime.now)
## 更新时间
update_at = db.Column(db.DateTime, default=datetime.datetime.now, onupdate=datetime.datetime.now)
```

数据库设计字段中datetime.datetime.now和datetime.datetime.now()的区别：

```shell
datetime.datetime.now：动态的当前时间，也就是数据库添加、修改的时间
datetime.datetime.now()：固定的时间，程序部署的时间
```



# 2.flask-sqlalchemy默认值机制

在我们设计数据库时，**字段中设置的default其实并没有同步到mysql等数据库中，只是sqlalchemy在插入数据时帮助我们添加到了数据库中**，如果想在mysql的**表结构中看到这个字段有默认值**需要使用server_default

```shell
city = db.Column(db.String(10), server_default='北京')
```


但是当我们要给布尔值类型指定server_default时，需要用text(“0”)或者text(“1”)，text(“0”)代表False，text(“1”)代表True

```shell
from sqlalchemy import text
is_deleted = db.Column(db.Boolean, server_default=text('0'))
```


**因为mysql的datetime类型的数据不支持函数，所以不能通过server_default指定默认值为当前时间，只能通过sqlalchemy为我们提供的default**

