---
title:  python-模块-连接pg
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
class PostgreSQLPipeline(object):
    def process_item(self,item, spider)
            #sql_desc="INSERT INTO postgresql_1(fullname, username, organization, mail, joined,followers,starred,following,popular_repos,popular_repos_download,popular_repos_star,popular_repos_info,home_page)values(item['fullname'], item['username'], item['organization'], item['mail'],item['joined'],item['followers'],item['starred'],item['following'],item['popular_repos'],item['popular_repos_download'],item['popular_repos_star'],item['popular_repos_info'], item['home_page'])"
        conn = psycopg2.connect(database="mypg", user="postgres", password="student", host="127.0.0.1", port="5432")
        try:
            cur=conn.cursor()
                #self.conn.query(sql_desc)
                #cur.execute("INSERT INTO ewrrw values(dict(item));")
            cur.execute("""INSERT INTO postgresql_1
                (fullname, username, organization, mail, joined, followers, starred, following, popular_repos, popular_repos_download, popular_repos_star, popular_repos_info, home_page)
                VALUES(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);""",
                (item['fullname'],
                item['username'],
                item['organization'],
                item['mail'],
                item['joined'],
                item['followers'],
                item['starred'],
                item['following'],
                item['popular_repos'],
                item['popular_repos_download'],
                item['popular_repos_star'],
                item['popular_repos_info'],
                item['home_page']),)

            conn.commit()
            log.msg("Data added to PostgreSQL database!",
                level=log.DEBUG,spider=spider)

        except Exception,e:
            print 'insert record into table failed'
            print e

        finally:
            if cur:
                cur.close()
        conn.close()
        return item

```

查询

```
#!/usr/bin/python

import psycopg2

conn = psycopg2.connect(database="testdb", user="postgres", password="pass123", host="127.0.0.1", port="5432")
print "Opened database successfully"

cur = conn.cursor()

cur.execute("SELECT id, name, address, salary  from COMPANY")
rows = cur.fetchall()
#row = cur.fetchone()
for row in rows:
   print "ID = ", row[0]
   print "NAME = ", row[1]
   print "ADDRESS = ", row[2]
   print "SALARY = ", row[3], "\n"

print "Operation done successfully";
conn.close()

```


官网：

http://initd.org/psycopg/docs/install.html#prerequisites
