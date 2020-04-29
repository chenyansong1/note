---
title: shell脚本实例之分库分表备份脚本
categories: shell   
toc: true  
tags: [shell,mysql]
---



# 1.场景
&emsp;当我们只需要恢复一个库的时候（这个库很小），而整个所有的数据库很大，此时我们就需要分库备份



# 2.shell脚本
## 2.1.多实例

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/shell/mysql_1.png)

 
```
#sed的作用
[root@lamp01 ~]# mysql -e "show databases"
+--------------------+
| Database           |
+--------------------+
| information_schema |
| mysql              |
| test               |
+--------------------+
[root@lamp01 ~]# mysql -e "show databases"|sed "1d"    #sed "1d"就是删除第1行
information_schema
mysql
test


```

## 2.2.单实例

```
#!/bin/sh
 
USER="root";
PASSWORD="123456";
MY_CMD="mysql -u$USER -p$PASSWORD";
MY_DUMP="mysqldump -u$USER -p$PASSWORD";
BAK_PATH="/home/chenyansong/shell/$(date +%F)";
[ ! -d $BAK_PATH ] && mkdir -p $BAK_PATH
 
for dbName in `$MY_CMD -e "show databases;"|sed "1d"|grep -v "_schema"`;do
        $MY_DUMP --events -x -B $dbName|gzip>${BAK_PATH}/${dbName}.sql.gz
        if [ $? -eq 0 ];then
                echo "${dbName}:$(date +'%F %H:%M:%S')">>${BAK_PATH}/${dbName}.log
        fi
done


```
生成的结果文件如下:

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/shell/mysql_2.png)


# 3.分表备份

## 3.1.多实例

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/shell/mysql_3.png)

## 3.2.单实例

```
#!/bin/sh
 
USER="root";
PASSWORD="123456";
MY_CMD="mysql -u$USER -p$PASSWORD";
MY_DUMP="mysqldump -u$USER -p$PASSWORD";
BAK_PATH="/home/chenyansong/shell/$(date +%F)";
[ ! -d $BAK_PATH ] && mkdir -p $BAK_PATH
 
for dbName in `$MY_CMD -e "show databases;"|sed "1d"|grep -v "_schema"`;do
        for tname in `$MY_CMD -e "show tables from $dbName"|sed "1d"`;do
                $MY_DUMP --events -x $dbName $tname|gzip>${BAK_PATH}/${dbName}_${tname}.sql.gz
 
                if [ $? -eq 0 ];then
                        echo "${dbName}_${tname}:$(date +'%F %H:%M:%S')">>${BAK_PATH}/${dbName}.log
                fi
        done
 
done


/*
注意：分表备份中，不能使用-B （如果使用，就会当做是多个库），然后就是指定（库名 表名）$MY_DUMP --events -x $dbName $tname

恢复某一个表
[root@lnmp02 2016-08-21]# mysql -uroot -p123456 test <test_t1.sql 
*/
```










