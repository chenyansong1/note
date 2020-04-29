---
title: azkaban实例
categories: azkaban   
toc: true  
tag: [azkaban]
---





 Azkaba内置的任务类型支持command、java,下面是一些azkaban的应用实例

<!--more-->

# 1.Command类型单一job示例
 1.创建job描述文件
vi command.job
```
#command.job
type=command                                                   
command=echo 'hello'
```
 2.将job资源文件打包成zip文件
```
zip command.job
```

 3.通过azkaban的web管理平台创建project并上传job压缩包
首先：<font color=red>Create Project</font>


![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/azkaban/example/command_1.png)
 

 4.上传zip包(<font color=red>Upload</font>)

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/azkaban/example/command_2.png)
 
 5.启动执行该job

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/azkaban/example/command_3.png)
 



# 2.Command类型多job工作流flow

 1.创建有依赖关系的多个job描述：第一个job：foo.job
```
# foo.job
type=command
command=echo foo

```
第二个job：bar.job依赖foo.job

```
# bar.job
type=command
#依赖上一个foo.job
dependencies=foo
command=echo bar

```


 2.将所有job资源文件打到一个zip包中

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/azkaban/example/mutil_command.png)


 3.在azkaban的web管理界面创建工程并上传zip包


 4.启动工作流flow






# 3.HDFS操作任务
 1.创建job描述文件
```
# fs.job
type=command
command=/home/hadoop/apps/hadoop-2.6.1/bin/hadoop fs -mkdir /azaz

```
 2.将job资源文件打包成zip文件

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/azkaban/example/hdfs_command.png)

 

 3、通过azkaban的web管理平台创建project并上传job压缩包
4、启动执行该job



# 4.MAPREDUCE任务
Mr任务依然可以使用command的job类型来执行

1.创建job描述文件，及mr程序jar包（示例中直接使用hadoop自带的example jar）
```
# mrwc.job
type=command
command=/home/hadoop/apps/hadoop-2.6.1/bin/hadoop  jar hadoop-mapreduce-examples-2.6.1.jar wordcount /wordcount/input /wordcount/azout
```
2.将所有job资源文件打到一个zip包中


 ![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/azkaban/example/mapreduce_command.png)

3、在azkaban的web管理界面创建工程并上传zip包
4、启动job




# 5.HIVE脚本任务
创建job描述文件和hive脚本
1.Hive脚本： test.sql
```
use default;
drop table aztest;
create table aztest(id int,name string) row format delimited fields terminated by ',';
load data inpath '/aztest/hiveinput' into table aztest;
create table azres as select * from aztest;
insert overwrite directory '/aztest/hiveoutput' select count(1) from aztest; 
```
2.Job描述文件：hivef.job
```
# hivef.job
type=command
#-f 执行的是文件，-e执行的命令
command=/home/hadoop/apps/hive/bin/hive -f 'test.sql'
```

3、将所有job资源文件打到一个zip包中

4、在azkaban的web管理界面创建工程并上传zip包

5、启动job

**注意**
* 在启动job的时候，首先执行以下：**立即执行，查看job是否报错**，然后执行executor调用，指定调度的时间周期





