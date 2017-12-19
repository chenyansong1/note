---
title:  python数据库之sqlite3模块
categories: python   
toc: true  
tags: [python]
---



# 1.使用步骤
Python就内置了SQLite3，所以，在Python中使用SQLite，不需要安装任何东西，直接使用。
1. 导入sqlite3模块
2. 连接数据库
     * sqlite3.connect(“数据库文件路径及其文件”)
     * sqlite3.connect(“:memory:”)   —— 内存数据库
     * 以上将返回数据库连接对象
3. 用游标对象方法操作数据库
```
cursor.execute()
cursor.executemany()
cursor.executescript()
cursor.fetchone()
cursor.fetchall()
cursor.fetchmany()

```

 
 
 
4. 提交事务
```
con.commit()
con.rollback()

```
5. 关闭连接 
```
con.close()
```

# 2.实现简单的CRUD
```
create table_name(col_name_type,......)               #创建表
insert into table_name(cola,colb...) vlaues(...)       #插入数据
select * from table_name                             #查询数据
update table set .....  where ........                  #更新数据
delete  from  table_name  where .......               #删除数据

```
```
from sqlite3 import connect
 
db_name = 'test.db'
 
con = connect(db_name)
cur = con.cursor()
 
# cur.execute('create table star(id integer,name text,age integer,address text)')                        #创建表
 
# rows = [(1,"王俊凯",16,"重庆"),(2,"王源",15,"重庆"),(3,"易烊千玺",15,"怀化")]
# for item in rows:
#     cur.execute("insert into star (id,name,age,address) values (?,?,?,?)",item)                #插入数据
 
# cur.execute('select * from star')                                         #查询数据
# for row in cur:
#     print(row)
 
# cur.execute('update star set age=? where id=?',(16,3))                      #更新数据
# cur.execute('select * from star')
# for row in cur:
#     print(row)
cur.execute('delete from star where id=?',(3,))                                  #删除数据
cur.execute('select * from star')
for row in cur:
    print(row)
 
con.commit()
con.close()

```
# 3.行对象（Row）简介
## 3.1.支持的操作
* 以列名访问
* 以索引号访问
* 迭代访问
* len()操作

## 3.2.游标建立前
conn.row_factory = sqlite3.Row

## 3.3.程序实现
```
 from sqlite3 import connect,Row
 
db_name = 'test.db'
 
con = connect(db_name)
con.row_factory = Row                                    #设置
cur = con.cursor()
 
cur.execute('select * from star')
row = cur.fetchone()
 
print(type(row))
 
print('以列名访问：',row['name'])                        #以列名访问
 
print('以索引号访问：',row[1])                    #以索引号访问
 
print('以迭代的访问：')
for item in row:
    print(item)
 
print("len():",len(row))
 
con.close()

```

# 4.批量数据库操作
>cur.executemany(sql_string,seq)
 
```
from sqlite3 import connect,Row
 
db_name = 'test.db'
 
con = connect(db_name)
con.row_factory = Row
cur = con.cursor()
 
rows = [(14,'Lily',12,'BeiJing'),(6,'John',13,"ChongQing")]
cur.executemany('insert into star (id,name,age,address) values (?,?,?,?)',rows)        #rows 一个列表的参数
cur.execute('select * from star')
 
for row in cur:
    for r in row:
        print(r)
 
con.commit()
 
 
con.close()

```


 



# 5.批量执行脚本

>cur.executescript(sql_string)

```
from sqlite3 import connect
db_name = 'testb.db'
 
con = connect(db_name)
cur = con.cursor()
 
sql_str = """
create table test(id integer,name text);
insert into test (id,name) values (1,'Lily');
insert into test (id,name) values (2,'Green');
"""
cur.executescript(sql_str)                            #批量执行
 
cur.execute('select * from test')
for item in cur:
    print(item)
 
con.commit()
  
con.close()
```




# 6.自定义函数——创建基本函数
>con.create_function(name,params_num,func_name)

```
from sqlite3 import connect,Row
import binascii
 
db_name = 'test.db'
 
def encrypt(mydata):
    crc = str(binascii.crc32(mydata.encode()))
    while len(crc) < 10:
        crc = '0'+ crc
    return mydata + crc
 
def check(mydata):
    if len(mydata) < 11:
        return None
    crc_res = str(binascii.crc32(mydata[:-10].encode()))
    while len(crc_res) < 10:
        crc_res = '0'+ crc_res
    if crc_res == mydata[-10:]:
        return mydata[:-10]
 
 
con = connect(db_name)
con.create_function('checkk',1,check)                            #checkk 是注册函数的名字，注册了之后可以在sql语句中使用的，check是对应的执行的函数（实际就是执行的这个函数）
 
cur = con.cursor()
 
sql_scrpit = """
drop table if exists testa;
create table if not exists testa(id integer,name text);
insert into testa (id,name) values (3,"%s");
insert into testa (id,name) values (4,"%s");
"""
names = ['Lily','Green']
names = tuple(encrypt(i) for i in names)
sql_scrpit = sql_scrpit % names
print(sql_scrpit)
cur.executescript(sql_scrpit)
 
cur.execute('select id,checkk(name) from testa')                #调用注册的函数
for item in cur:
    print(item)
 
cur.execute('update testa set name=? where id=?',('dfddkkjd1122334455',4))
cur.execute('select id,checkk(name) from testa')
for item in cur:
    print(item)
 
con.commit()
con.close()

```



# 7.自定义函数——创建聚合函数
* 实现一些方法的一个自定义类
   con.create_aggregate(name,params_num,class_name)
* 协议方法
    step()
    finalize()

```
#求绝对值和
from sqlite3 import connect,Row
import binascii
 
db_name = 'test.db'
 
class AbsSum:
    def __init__(self):
        self.s = 0
    def step(self,v):
        self.s += abs(v)
    def finalize(self):
        return self.s
 
con = connect(db_name)
con.create_aggregate('abssum',1,AbsSum)
 
cur = con.cursor()
 
sql_scrpit = """
drop table if exists testa;
create table if not exists testa(id integer,name text,score integer);
insert into testa (id,name,score) values (3,"Lily",8);
insert into testa (id,name,score) values (4,"Jhon",-7);
"""
cur.executescript(sql_scrpit)
 
cur.execute('select abssum(score) from testa')
for item in cur:
    print(item)
 
con.commit()
con.close()
```
 

# 8.数据类型转换
python	sqlite
None	NULL
str	text
int	integer
bytes	blob




# 9.保存文件至数据库
* execute("insert into t values(?) ", (f.read()))

```
from sqlite3 import connect,Row,Binary
import binascii
 
db_name = 'test.db'
 
con = connect(db_name)
cur = con.cursor()
 
sql_scrpit = """
drop table if exists testa;
create table if not exists testa(id integer,data blob);
"""
cur.executescript(sql_scrpit)
 
f = open('test.jpg','rb')
 
cur.execute('insert into testa (id,data) values (3,?)',(f.read(),))
 
cur.execute('select * from testa where id=3')
record = cur.fetchone()
f = open('tt.jpg',"wb+")
f.write(record[1])
f.close()

con.commit()
con.close()

```


# 练习
关于：联系人和分组的增删改查
```
import os
import sqlite3
from sqlite3 import connect
 
class MySqliteDb(object):
    """Sqlite3 Db Class"""
    def __init__(self, dbname="address.db"):
        self.dbname = dbname
        self.con = None
        self.curs = None
 
    def getCursor(self):
        self.con = sqlite3.connect(self.dbname)
        if self.con:
            self.curs = self.con.cursor()
 
    def closeDb(self):
        if self.curs:
            self.curs.close()
        if self.con:
            self.con.commit()
            self.con.close()
 
    def __enter__(self):
        self.getCursor()
        return self.curs
 
    def __exit__(self, exc_type, exc_val, exc_tb):
        if exc_val:
            print("Exception has generate: ",exc_val)
            print("Sqlite3 execute error!")
        self.closeDb()
 
def initDb():
    sql_script = '''
        create table contact
        (id integer primary key autoincrement not null,
        name varchar(20) not null,
        home_tel varchar(20),
        office_tel varchar(20),
        mobile_phone varchar(20),
        memo text);
        create table mygroup(
        id integer primary key autoincrement not null,
        name  varchar(20) not null,
        memo text);
        create table con_gro(
        cid integer not null,
        gid integer not null,
        FOREIGN KEY(cid) REFERENCES contact(id),
        FOREIGN KEY(gid) REFERENCES mygroup(id)
            );
        '''
    if not os.path.exists('address.db'):
        with MySqliteDb() as db:
            db.executescript(sql_script)
 
class Contact:
    def __init__(self,name='',home_tel='',office_tel='',mobile_phone='',memo=''):
        self.id = -1
        self.name = name
        self.home_tel = home_tel
        self.office_tel = office_tel
        self.mobile_phone = mobile_phone
        self.memo = memo
        self.gid = None
        self.keys = ('home_tel','office_tel','mobile_phone','memo')
        self.__change()
 
    def save(self):
        if self.id == -1:
            param_dicts = {k:getattr(self,k) for k in self.keys if getattr(self,k)}
            keys = tuple(k for k in self.keys if k in param_dicts)
            quotes = ','.join(('?' for i in range(len(param_dicts))))
            param_tuple = [self.name,]
            for key in keys:
                param_tuple.append(param_dicts[key])
            param_tuple = tuple(param_tuple)
            sql = "insert into contact (name,%s) values (?,%s)" % (','.join(keys),quotes)
            if self.name:
                try:
                    with MySqliteDb() as db:
                        db.execute(sql,param_tuple)
                        res = db.execute('select id from contact where name=?',(self.name,))
                        res = res.fetchone()
                        self.id = res[0]
                    return True
                except:
                    print("保存失败，请检查服务器！")
            else:
                print("姓名不能为空！")
                return False
        else:
            return self.update()
 
    def update(self):
        sql = "update contact set %s=? where id=?"
        chgs = {k:getattr(self,k) for k in self.keys \
                if getattr(self,k) and getattr(self,k) != self.vals.get(k)}
        if not chgs:
            return
        try:
            with MySqliteDb() as db:
                for k,v in chgs.items():
                    db.execute(sql % k,(v,self.id))
            self.__change()
            return True
        except:
            print("更新失败！")
            return False
 
    def load(self,id):
        try:
            with MySqliteDb() as db:
                res = db.execute('select * from contact where id=?',(id,))
                res = res.fetchone()
                self.name = res[1]
                self.id = id
                for i,v in enumerate(res[2:]):
                    setattr(self,self.keys[i-2],v)
            self.__change()
            return True
        except:
            print('数据载入失败！')
 
 
    def load_from_name(self):
        if self.name:
            try:
                with MySqliteDb() as db:
                    res = db.execute('select * from contact where name=?',(self.name,))
                    res = res.fetchone()
                    self.id = res[0]
                    for i,v in enumerate(res[2:]):
                        setattr(self,self.keys[i-2],v)
                self.__change()
                return True
            except:
                print('数据载入失败！')
 
    def get_by_name(self,name):
        try:
            with MySqliteDb() as db:
                res = db.execute('select * from contact where name=?',(name,))
                res = res.fetchall()
            return res
        except:
            print('数据查询失败！')
 
    def delete(self):
        try:
            with MySqliteDb() as db:
                db.execute('delete from contact where id=?',(self.id,))
                db.execute('delete from con_gro where cid=?',(self.id,))
            return True
        except:
            print('数据查询失败！')
 
    def all(self):
        try:
            with MySqliteDb() as db:
                res = db.execute('select * from contact')
                res = res.fetchall()
            return res
        except:
            print('数据查询失败！')
 
    def add_to_group(self,gid):
        if gid and self.id != -1:
            self.gid = gid
            try:
                with MySqliteDb() as db:
                    db.execute('insert into con_gro (cid,gid) values (?,?)',(self.id,gid))
                return True
            except:
                print('数据查询失败！')
 
    def __change(self):
        self.vals = {k:getattr(self,k) for k in self.keys}
 
class Group:
    def __init__(self,name='',memo=''):
        self.id = -1
        self.name = name
        self.memo =memo
        self.contacts = []
 
    def save(self):
        if self.name and self.id == -1:
            try:
                with MySqliteDb() as db:
                    db.execute('insert into mygroup (name,memo) values (?,?)',(self.name,self.memo))
                    res = db.execute('select id from mygroup where name=?',(self.name,))
                    self.id = res.fetchone()[0]
                    return True
            except:
                print("数据查询失败！")
        if self.name and self.id != -1:
            return self.update()
 
    def update(self):
        try:
            with MySqliteDb() as db:
                db.execute('update mygroup set name=?,memo=? where id=?',(self.name,self.memo,self.id))
                return True
        except:
            print("数据更新失败！")
 
    def load(self,id):
        try:
            with MySqliteDb() as db:
                res = db.execute('select * from mygroup where id=?',(id,))
                res = res.fetchone()
                self.id = id
                self.name = res[1]
                self.memo = res[2]
        except:
            pass
 
    def delete(self):
        if self.id != -1:
            try:
                with MySqliteDb() as db:
                    db.execute('delete from mygroup where id=?',(self.id,))
                    db.execute('delete from con_gro where gid=?',(self.id,))
                    return True
            except:
                print("数据查询失败！")
 
    def load_from_name(self):
        if self.name:
            try:
                with MySqliteDb() as db:
                    res = db.execute('select * from mygroup where name=?',(self.name,))
                    res = res.fetchone()
                    self.id = res[0]
                    self.memo = res[2]
            except:
                pass
 
    def all(self):
        try:
            with MySqliteDb() as db:
                res = db.execute('select * from mygroup')
                return res.fetchall()
        except:
            print("数据查询失败！")
 
    def add_contact(self,cid):
        if cid and self.id != -1:
            try:
                with MySqliteDb() as db:
                    db.execute('insert into con_gro (cid,gid) values (?,?)',(cid,self.id))
                return True
            except:
                print('数据查询失败！')
 
    def del_contact(self,cid):
        try:
            with MySqliteDb() as db:
                db.execute('delete from con_gro where cid=? and gid=?',(cid,self.id))
            return True
        except:
            pass
 
    def all_contacts(self):
        # return objects of members
        if self.id != -1:
            try:
                with MySqliteDb() as db:
                    cts = []
                    cids = db.execute('select cid from con_gro where gid=?',(self.id,))
                    cids = cids.fetchall()
                    for cid in cids:
                        c = Contact()
                        c.load(cid[0])
                        cts.append(c)
                return cts
            except:
                print('数据查询失败！')
 
def info():
    sqls = [
        'select * from contact',
        'select * from mygroup',
        'select * from con_gro',
    ]
    try:
        with MySqliteDb() as db:
            for sql in sqls:
                res = db.execute(sql)
                res = res.fetchall()
                for r in res:
                    print(r)
    except:
        print('数据查询失败！')
 
 
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
 
    print('初始化数据库……')
    initDb()
    print('插入数据……')
    for ct in cts:
        Contact(**ct).save()
 
    for gp in gps:
        Group(**gp).save()
 
    print('从数据库中查询到的数据……')
    info()
 
    print('查询联系人，并添加到组2……')
    ct = Contact(name='Tom')
    ct.load_from_name()
    print(ct.id,ct.name)
    ct.add_to_group(2)
 
    cta = Contact()
    cta.load(1)
    print(cta.id,cta.name)
    cta.add_to_group(1)
 
    ctb = Contact()
    ctb.load(3)
    print(ctb.id,ctb.name)
    ctb.add_to_group(2)
 
    ctb = Contact()
    ctb.load(5)
    print(ctb.id,ctb.name)
    ctb.add_to_group(2)
    info()
 
    print("查询所有联系人：")
    for r in Contact().all():
        print(r)
 
    print("查询所有组：")
    for r in Group().all():
        print(r)
 
    print("查询指定组的成员：")
    gp = Group(name='家人')
    gp.load_from_name()
    print(gp.id,gp.name)
 
    print("通过组添加成员：")
    gp.add_contact(4)
 
    cts = gp.all_contacts()
    for ct in cts:
        print(ct.id,ct.name)
 
    print("删除组成员：")
    gp.del_contact(5)
    info()
 
    print("删除联系人：")
    ct = Contact()
    ct.load(5)
    ct.delete()
    info()
 
    print("删除组：")
    gp = Group()
    gp.load(2)
    gp.delete()
 
    info() 
 
```




