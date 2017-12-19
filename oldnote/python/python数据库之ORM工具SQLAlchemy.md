---
title:  python数据库之ORM工具SQLAlchemy
categories: python   
toc: true  
tags: [python]
---



# 1.ORM简介
* 一种对象关系映射工具
* 面向对象编程开发方法发生而产生的
* 面向对象编程中数据保存在对象中并在对象中交互，运行于内存（不易持久化）
* 关系是指关系数据库，即数据保存在数据库中（可持久化）
* 对象可以轻松的从数据库中载入持久化的数据
* 对象还可以将需要持久化的对象数据保存至数据库

# 2. ORM特性
* 提高开发效率，不用直接编写查询数据库载入数据的代码
* 开发人员不用接触SQL语句，可以完成数据库有关操作
* 主要缺点是：不易数据库查询优化，并可能带来性能上的损失

# 3.SQLAlchemy
* 开源并使用MIT许可证
* 可兼容切换多种数据库（如：SQLite、MySQL、PostgreSQL、MsSQL、Oracle等）
* 安装：在管理员方式的命令提示符中：pip install SQLAlchemy
 
# 4.使用步骤
1. 构造声明基类
    Base = sqlalchemy.ext.declarative.declarative_base()

2. 声明对象关系映射类（继承Base）
3. 创建与数据库的连接
    engine = sqlalchemy.create_engine(数据库连接字符串)
4. 创建表（如果表存在，可以省略）
    Base.metadata.create_all(engine)
5. 使用会话访问数据库
    1. 创建会话
        Session = sqlalchemy.orm.sessionmaker(bind=engine)
         (或者先创建Session，后配置：Session.configure(bind=engine))
        session = Session()
    2. 会话方法 
```
#添加
session.add()
session.add_all()

#查询
session.query()
         filter_by()/filter()                #过滤
         order_by()                    #排序
         first()                #取第一条
         all()                    #取所有
         可切片（[1:3]）            #取切片
        
#删除
session.delete()

#提交事务
session.commit()
会话提交时，映射类各种改变都会提交

```
    

# 5.定义实例
1. 主键的定义
    primary_key = True
2. 主要列类型
```
Column(用它来构造列对象)
Integer
Float
String
Text
Sequence
Date
DateTime
Boolean
Binary

```
 
# 6.基本操作（使用步骤的代码实现）
```
#!/usr/bin/python
 
from sqlalchemy import create_engine,String,Integer,Column
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
 
#'数据库类型+数据库驱动名称://用户名:口令@机器地址:端口号/数据库名'
db_name = 'mysql+mysqldb://root:oldboy123@192.168.0.11/testdb'     
# 创建对象的基类:           
Base = declarative_base()                                    
 
class User(Base):                                #继承基类：相当于定义映射关系（实体对象和数据库字段的映射关系）
        # 表的名字:
        __tablename__ = 'user'           
                     
        # 表的结构:
        id = Column(Integer,primary_key=True)
        name = Column(String(50))


# 初始化数据库连接:
engine = create_engine(db_name)

#有这条语句，如果表不存在，那么就创建表（如上面的user表）
Base.metadata.create_all(engine)
 
#创建session
Session = sessionmaker(bind=engine)
session = Session()
 
 
#add
'''
u = User()
u.id=1
u.name = 'Lily'
session.add(u)
 
u2 = User(id=2,name='Bob')
session.add(u2)
 
session.commit()
'''
 
#查询并过滤
u3 = session.query(User).filter_by(id=2).first()
#u3 = session.query(User).filter(User.id=2).first()
 
print("u3:id={0},name={1}".format(str(u3.id),u3.name))

#查询并排序
#session.query(User).order_by('id').all()

#查询并删除 
#session.query(User).filter_by(id=3).delete()
 
#delete
session.delete(u3)
session.commit()
# 关闭Session:
session.close()

```
 
# 7.一对多映射关系
user中的addresses是包含若干个Address对象的list

# 8.多对多映射关系
 
 
 
 


# 练习
关于：联系人和分组
```
from sqlalchemy import create_engine,String,Integer,Column,ForeignKey,Table,Text,Sequence
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker,relationship
 
db_name = 'mysql+mysqlconnector://root:123456@localhost:3306/test'
 
Base = declarative_base()
 
con_gro = Table('asso',Base.metadata,
                Column('con_id',Integer,ForeignKey('contact.id')),
                Column('gro_id',Integer,ForeignKey('mygroup.id')))
 
class Contact(Base):
    __tablename__ = 'contact'
    id =  Column(Integer,Sequence('contact_id'),primary_key=True)
    name = Column(String(20))
    home_tel = Column(String(20))
    office_tel = Column(String(20))
    mobile_phone = Column(String(20))
    memo = Column(Text)
    groups = relationship("Group",secondary=con_gro,backref="contacts")
 
class Group(Base):
    __tablename__ = 'mygroup'
    id =  Column(Integer,Sequence('mygroup_id'),primary_key=True)
    name = Column(String(20))
    memo = Column(Text)
 
if __name__ == '__main__':
 
    cts = [
        {'name':'Lily','home_tel':'0551-98789233','memo':"安徽"},
        {'name':'Bob','office_tel':'021-94679233','memo':"上海"},
        {'name':'Mike','mobile_phone':'18298781089'},
        {'name':'John','home_tel':'010-57989043','memo':"北京"},
        {'name':'Green','mobile_phone':'13908707652',},
        {'name':'Tom','mobile_phone':'13109008759'},
    ]
 
    gps = [
        {"name":'朋友'},
        {'name':'家人'},
        {'name':"学生"},
    ]
 
 
    engine = create_engine(db_name)
    Session = sessionmaker(bind=engine)
    session = Session()
 
    print('初始化数据库……')
    Base.metadata.drop_all(engine)
    Base.metadata.create_all(engine)
 
    print('插入数据……')
    for ct in cts:
        session.add(Contact(**ct))
    for gp in gps:
        session.add(Group(**gp))
 
    session.commit()
 
    print('从数据库中查询到的数据……')
    for c in session.query(Contact).all():
        print(c.id,c.name,c.home_tel,
            c.office_tel,c.mobile_phone,c.memo)
    for g in session.query(Group).all():
        print(g.id,g.name,g.memo)
 
    print('查询联系人，并添加到组2……')
    ct = session.query(Contact).filter_by(name="Tom").first()
    print(ct.id,ct.name)
    gp = session.query(Group).filter_by(id=2).first()
    gp.contacts.append(ct)
 
    ct = session.query(Contact).filter_by(id=1).first()
    print(ct.id,ct.name)
    gpo = session.query(Group).filter_by(id=1).first()
    gpo.contacts.append(ct)
 
    ct = session.query(Contact).filter_by(id=3).first()
    print(ct.id,ct.name)
    gp.contacts.append(ct)
 
    ct = session.query(Contact).filter_by(id=5).first()
    print(ct.id,ct.name)
    gp.contacts.append(ct)
 
    session.commit()
 
    print("查询指定组的成员：")
    gp = session.query(Group).filter_by(name="家人").first()
    print(gp.id,gp.name)
    for c in gp.contacts:
        print(c.id,c.name)
 
    print("删除组成员：")
    gp.contacts.remove(c)
    session.commit()
    for c in gp.contacts:
        print(c.id,c.name)
 
    print("删除联系人：")
    c = session.query(Contact).filter_by(id=5).first()
    session.delete(c)
 
    print("删除组：")
    g = session.query(Group).filter_by(id=2).first()
    session.delete(g)
 
    session.commit()
 
    for c in session.query(Contact).all():
        print(c.id,c.name,c.home_tel,
            c.office_tel,c.mobile_phone,c.memo)
    for g in session.query(Group).all():
        print(g.id,g.name,g.memo)
 
 
 
```





