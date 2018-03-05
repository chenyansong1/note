---
title:  python-模块-http
categories: python   
toc: true  
tags: [python]
---


通过python去连接pg

# 安装pg模块

```
pip install psycopg2
```


# 连接程序实例


```
#!/usr/bin/python
# -*- coding: gbk -*-
import psycopg2
# 数据库连接参数
conn = psycopg2.connect(database="database_name", user="test_user", password="123456", host="192.168.1.225", port="5432")
cur = conn.cursor()
#cur.execute("CREATE TABLE test(id serial PRIMARY KEY, num integer,data varchar);")
# insert one item
#cur.execute("INSERT INTO test(num, data)VALUES(%s, %s)", (1, 'aaa'))


cur.execute('select * from t_siem_alarm_active limit 2')
rows = cur.fetchall()        #all rows in table
print(rows)
for i in rows:
    print(i)
conn.commit()
cur.close()
conn.close()

```



官网：

http://initd.org/psycopg/docs/install.html#prerequisites
