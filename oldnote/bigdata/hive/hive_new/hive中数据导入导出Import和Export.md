---
title: hive中数据导入导出Import和Export
categories: hive  
tags: [hive]
---



# Export将hive表中的数据导出到外部

```
export table tablename [partition (part_coloumn='value' [,...]]
to 'export_target_path'

#export_target_path指的是hdfs上的路径

#其实会将hive表指向的目录中的文件copy到指定的hdfs目录下,同时会将hive表的元数据也copy到hdfs的目录下
hive (default)> export table emp to '/tmp/export/emp_exp';
Copying data from file:/tmp/root/9950c21d-9397-4e81-a3fa-032ffe368fa7/hive_2017-04-24_14-47-28_645_5478278646286661256-1/-local-10000/_metadata
Copying file: file:/tmp/root/9950c21d-9397-4e81-a3fa-032ffe368fa7/hive_2017-04-24_14-47-28_645_5478278646286661256-1/-local-10000/_metadata
Copying data from hdfs://hdp-node-01:9000/user/hive/warehouse/emp
Copying file: hdfs://hdp-node-01:9000/user/hive/warehouse/emp/employee.txt
Copying file: hdfs://hdp-node-01:9000/user/hive/warehouse/emp/employee2.txt
OK
Time taken: 1.856 seconds

#hive表中的数据
hive (default)> dfs -ls /user/hive/warehouse/emp/ ;
Found 2 items
-rwxr-xr-x   3 root supergroup        652 2017-04-23 22:50 /user/hive/warehouse/emp/employee.txt
-rwxr-xr-x   3 root supergroup        652 2017-04-24 00:59 /user/hive/warehouse/emp/employee2.txt

#导出之后的目录
hive (default)> dfs -ls /tmp/export/emp_exp/ ;
Found 2 items
-rwx-wx-wx   3 root supergroup       1601 2017-04-24 14:47 /tmp/export/emp_exp/_metadata
drwx-wx-wx   - root supergroup          0 2017-04-24 14:47 /tmp/export/emp_exp/data

hive (default)> dfs -ls /tmp/export/emp_exp/data/;
Found 2 items
-rwx-wx-wx   3 root supergroup        652 2017-04-24 14:47 /tmp/export/emp_exp/data/employee.txt
-rwx-wx-wx   3 root supergroup        652 2017-04-24 14:47 /tmp/export/emp_exp/data/employee2.txt

```


# Import导入将外部数据导入到hive表中

```
import [[external] table new_or_original_tablename [partition (part_column="value"[,...])]]
from 'souce_path'
[location 'import_target_path']



hive (test_database)> create table if not exists test_database.emp like default.emp;

import table test_database.emp from '/tmp/export/emp_exp' ;

hive (test_database)> dfs -ls /user/hive/warehouse/test_database.db/emp/;
Found 2 items
-rwxr-xr-x   3 root supergroup        652 2017-04-24 15:01 /user/hive/warehouse/test_database.db/emp/employee.txt
-rwxr-xr-x   3 root supergroup        652 2017-04-24 15:01 /user/hive/warehouse/test_database.db/emp/employee2.txt

````




