---
title: 利用Percona Xtrabackup快速备份MySQL(转)
categories: mysql   
toc: true  
tags: [mysql]
---


# 1.了解备份方式

* 热备份：读写不受影响（mysqldump–>innodb）
* 温备份：仅可以执行读操作（mysqldump–>myisam）
* 冷备份：离线备份，读写都不可用
* 逻辑备份：将数据导出文本文件中（mysqldump）
* 物理备份：将数据文件拷贝（xtrabackup、mysqlhotcopy）
* 完整备份：备份所有数据
* 增量备份：仅备份上次完整备份或增量备份以来变化的数据
* 差异备份：仅备份上次完整备份以来变化的数据



# 2.创建备份用户
```
mysql> grant reload,lock tables,replication client on *.* to 'bak'@'localhost' identified by 'bak2015';
mysql> flush privileges;
```


# 3.安装
```
# rpm -ivh http://www.percona.com/downloads/percona-release/redhat/0.1-3/percona-release-0.1-3.noarch.rpm
# yum install percona-xtrabackup

#xtrabackup2.2不支持MySQL5.1的Innodb引擎，如需要可安装2.0版本
```


# 4.常用参数
```
–user=     #指定数据库备份用户
–password=  #指定数据库备份用户密码
–port=     #指定数据库端口
–host=     #指定备份主机
–socket=    #指定socket文件路径
–databases=  #备份指定数据库,多个空格隔开，如–databases=”dbname1 dbname2″，不加备份所有库
–defaults-file=       #指定my.cnf配置文件
–apply-log         #日志回滚
–incremental=          #增量备份，后跟增量备份路径
–incremental-basedir=     #增量备份，指上次增量备份路径
–redo-only         #合并全备和增量备份数据文件
–copy-back         #将备份数据复制到数据库，数据库目录要为空
–no-timestamp          #生成备份文件不以时间戳为目录名
–stream=             #指定流的格式做备份，–stream=tar，将备份文件归档
–remote-host=user@ip DST_DIR #备份到远程主机


```

# 5.完整备份与恢复

## 5.1.完整备份
```
# innobackupex --user=bak --password='bak2015' /mysql_backup 
```

## 5.2.备份恢复
```
# innobackupex --defaults-file=/etc/mysql/my.cnf --copy-back /home/loongtao/mysql_backup/2015-02-08_11-56-48/

```

## 5.3.备份文件说明
```
# ls 2015-02-08_11-56-48

/*
backup-my.cnf：记录innobackup使用到mysql参数
xtrabackup_binary：备份中用到的可执行文件
xtrabackup_checkpoints：记录备份的类型、开始和结束的日志序列号
xtrabackup_logfile：备份中会开启一个log copy线程，用来监控innodb日志文件（ib_logfile），如果修改就会复制到这个文件
*/

```

# 6.完整备份+增量备份与恢复
## 6.1.完整备份
```
innobackupex --user=bak --password='bak2015' /mysql_backup
#备份后位置是：/mysql_backup/2015-02-08_11-56-48
```

## 6.2.增量备份1

```
# innobackupex --user=bak --password='bak2015' --incremental /data1/mysql_backup --incremental-basedir=/mysql_backup/2015-02-08_11-56-48  
#指定上次完整备份目录

```

## 6.3.增量备份2
```
innobackupex --user=bak --password='bak2015' --incremental /data1/mysql_backup --incremental-basedir=/mysql_backup/2015-02-08_12-16-06  
#指定上次增量备份目录
```

## 6.4.查看xtrabackup_checkpoints文件
一目了然，可以看到根据日志序号来增量备份

![](http://ols7leonh.bkt.clouddn.com//assert/img/mysql/xtrabackup/1.jpg)
 

##  6.5.备份恢复
> 备份恢复思路

将增量备份1、增量备份2…合并到完整备份，加到一起出来一个新的完整备份，将新的完整备份以拷贝的形式到数据库空目录（rm /var/lib/mysql/* -rf）


> 预备完整备份

xtrabackup把备份过程中可能有尚未提交的事务或已经提交但未同步数据文件的事务，写到xtrabackup_logfile文件，所以要先通过这个日志文件回滚，把未完成的事务同步到备份文件，保证数据文件处于一致性。
```
# innobackup --apply-log --redo-only 2015-02-08_11-56-48
```

> 合并第一个增量备份

```
# innobackupex --apply-log --redo-only /mysql_backup/2015-02-08_11-56-48/ --incremental-dir=mysql_backup/2015-02-08_12-16-06

```


> 合并第二个增量备份

```
# innobackupex --apply-log --redo-only /mysql_backup/2015-02-08_11-56-48/ --incremental-dir=mysql_backup/2015-02-08_16-06-53
```

> 恢复完整备份

这时2015-02-08_11-56-48完整备份已经包含所有增量备份，可以通过查看checkpoints来核实
```
# innobackupex --defaults-file=/etc/mysql/my.cnf --copy-back /mysql_backup/2015-02-08_11-56-48/

```
>  修改恢复数据文件权限

```
# chown -R mysql.mysql /var/lib/mysql
```


> 启动MySQL,查看数据库恢复情况

```
# /etc/init.d/mysqld start
```


# 7.备份文件归档压缩
## 7.1.归档并发送到备份服务器
```
 innobackupex --databases=test --user=bak --password='bak2015' --stream=tar /mysql_backup 2>/mysql_backup/bak.log |ssh root@192.168.18.251 "cat - > /mysql_backup/`date +%F`.tar"

#解压：tar -ixvf `date +%F`.tar

```

## 7.2.归档备份
```
# innobackupex --databases=test --user=bak --password='bak2015' --stream=tar /mysql_backup > /mysql_backup/`date +%F`.tar

#解压：tar -ixvf `date +%F`.tar
```

## 7.3.压缩归档备份
```
# innobackupex --databases=test --user=bak --password='bak2015' --stream=tar /mysql_backup |gzip >/mysql_backup/`date +%F`.tar.gz

#解压：tar -izxvf `date +%F`.tar.gz

```

[整理自](http://www.saunix.cn/1559.html)
