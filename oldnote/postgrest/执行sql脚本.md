多postgresql库批量执行SQL脚本文件
原创 2016年08月15日 17:19:40 578
1、编辑pgpass文件
pgpass文件内容格式为：     地址：端口：数据库名称：用户名：密码
cd ${HOME}
vi .pgpass
编辑如 ：192.168.1.90:5432:ABCD:odoo:odoo
保存后  chmod 600 .pgpass
2、编辑shell脚本
mkdir shell
cd shell 
 mkdir back #存放备份文件
 vi execute_sql.shell



```
#!/bin/bash
DATE=$(date +%Y%m%d)
DIR="sql_back_${DATE}"
HOST="127.0.0.1"
PORT="5432"
USER="postgres"
PASSWORD="12345)(*&^%RFVwsx"
dataname="BDSSA1"

#清空.pgpass文件
echo "" >${HOME}/.pgpass

#遍历往.pgpass文件写数据，因为psql没提供-p 输密码的参数，所以只能通过.pgpass
echo $HOST:$PORT:$dataname:$USER:$PASSWORD >>${HOME}/.pgpass
chmod 0600 ${HOME}/.pgpass

#执行SQL脚本
psql -h $HOST -d $dataname -U $USER -f *.sql -o out.log

```


参见：http://blog.csdn.net/fm0517/article/details/53130244

http://blog.csdn.net/odoo_autoyong/article/details/52212979



