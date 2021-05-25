[toc]

转自：https://blog.csdn.net/qq_30507163/article/details/103384848

根据一些条件的集合获取过滤后的数据。

要得到满足一系列等式条件的数据列表，则我们可以使用 query.filter_by 过滤器。

query.filter 接收关键字参数，并把接收到的参数作为我们想要在数据库里查询的字段名值对。比如，要找到用户名为admin 的用户列表，则可以这样：

```shell
users = User.query.filter_by(username='admin').all()
```

可以用多个条件和链式查询：

```shell
>>> users = User.query.order_by(User.username.desc()).filter_by(username='admin').limit(2).all()
```


query.filter_by 只有在你知道要查询的值时才工作。使用query.filter则可以避免这一不方便之处，你可以把一个比较大小的Python表达式传给它：

```shell
user = User.query.filter(User.id > 1).all()

```

query.filter可以接收任何Python的比较表达式。

对于Python的常规类型，比如：整数（integers）、字符串（string）和日期（dates），可以使用 == 操作符来表示相等的比较。对于类型为整数、浮点数（float）、日期（date）的列，还可以用 >、<、<= 和 >= 操作符来表示不等的比较。

一些复杂的SQL查询也可以转为用SQLalchemy的函数来表示。例如，可以像下面这样实现SQL中 IN、OR 和 NOT 的比较操作(SQLAlchemy中， 与None 的比较都会转化为null 的比较)：

```shell
from flask_sqlalchemy import not_, or_
user = User.query.filter(User.usernaem.in_(['admin']), User.password==None).first()
```



# 找出拥有密码的用户
```shell
user = User.query.filter(not_(User.password == None)).first()
```



# 这些方法都可以被组合起来
```shell
user = User.query.filter(or_(not_(User.password==None),User.id>1)).first()
```



