---
title: hive安装
categories: hive   
toc: true  
tag: [hive]
---

# 1.解压
```
cd /home/hadoop/app/
#解压
tar -zxvf apache-hive-1.2.1-bin.tar.gz 
#创建软链接
ln -s apache-hive-1.2.1-bin hive

```


# 2.安装mysql数据库
 参见《mysql的yum安装.md》
注意：删除匿名用户，允许用户远程连接

```
#如果出现没有权限的问题，在mysql授权(在安装mysql的机器上执行)
mysql -uroot -p
 #(执行下面的语句  *.*:所有库下的所有表   %：任何IP地址或主机都可以连接)
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'root' WITH GRANT OPTION;
FLUSH PRIVILEGES;
```


# 3.配置hive
 hive-env.sh
```
#改名
mv ./conf/hive-env.sh.template ./conf/hive-env.sh
#指定：配置其中的$hadoop_home
 HADOOP_HOME=/home/hadoop/app/hadoop-2.6.4


```


 hive-site.xml
源文件太多，我们自己新建一个,主要是配置连接mysql的连接信息(连接的url,连接驱动,用户名,密码)
```
 vi  hive-site.xml
#添加如下内容：

<configuration>
<property>
<name>javax.jdo.option.ConnectionURL</name>
<value>jdbc:mysql://localhost:3306/hive?createDatabaseIfNotExist=true</value>
<description>JDBC connect string for a JDBC metastore</description>
</property>
 
<property>
<name>javax.jdo.option.ConnectionDriverName</name>
<value>com.mysql.jdbc.Driver</value>
<description>Driver class name for a JDBC metastore</description>
</property>
 
<property>
<name>javax.jdo.option.ConnectionUserName</name>
<value>root</value>
<description>username to use against metastore database</description>
</property>
 
<property>
<name>javax.jdo.option.ConnectionPassword</name>
<value>root</value>
<description>password to use against metastore database</description>
</property>
</configuration>
 
<property>
  <name>hive.metastore.warehouse.dir</name>
  <value>/user/hive/warehouse</value>
  <description>这里配置的是hive在HDFS中存储数据的目录:hdfs://host_name:9000/user/hive/warehouse 就是这个目录 </description>
</property>

<property>
  <name>hive.metastore.uris</name>
  <value>thrift://hadoop-master:9083</value>
  <description>如果配置了这里,就可以通过远程连接到hive的元数据,注意要在远端(hadoop-master中)的hive中启动:hive --service metastore &</description>
</property>

```


# 4.mysql的连接驱动
 安装hive和mysq完成后，将mysql的连接驱动jar包拷贝到$HIVE_HOME/lib目录下

# 5.Jline包版本不一致的问题
 需要拷贝hive的lib目录中jline.2.12.jar的jar包替换掉hadoop中的  /home/hadoop/app/hadoop-2.6.4/share/hadoop/yarn/lib/jline-0.9.94.jar
```
[root@hdp-node-01 hive]# ./bin/hive
 
Logging initialized using configuration in jar:file:/home/hadoop/app/apache-hive-1.2.1-bin/lib/hive-common-1.2.1.jar!/hive-log4j.properties
[ERROR] Terminal initialization failed; falling back to unsupported
'java.lang.IncompatibleClassChangeError: Found class jline.Terminal, but interface was expected'        #hadoop中的jar和hive中的jar包版本不一致冲突了
        at jline.TerminalFactory.create(TerminalFactory.java:101)
        at jline.TerminalFactory.get(TerminalFactory.java:158)
        at jline.console.ConsoleReader.<init>(ConsoleReader.java:229)
        at jline.console.ConsoleReader.<init>(ConsoleReader.java:221)
        at jline.console.ConsoleReader.<init>(ConsoleReader.java:209)
        at org.apache.hadoop.hive.cli.CliDriver.setupConsoleReader(CliDriver.java:787)
        at org.apache.hadoop.hive.cli.CliDriver.executeDriver(CliDriver.java:721)
        at org.apache.hadoop.hive.cli.CliDriver.run(CliDriver.java:681)
        at org.apache.hadoop.hive.cli.CliDriver.main(CliDriver.java:621)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:606)
        at org.apache.hadoop.util.RunJar.run(RunJar.java:221)
        at org.apache.hadoop.util.RunJar.main(RunJar.java:136)

```


# 6.启动hive
```
bin/hive

#hive启动之后，可以像MySQL一样操作，写SQL查询语句了

```

# 7.测试
```
#在./conf/hive-site.xml 中配置了连接MySQL时的
<name>javax.jdo.option.ConnectionURL</name>
<value>jdbc:mysql://localhost:3306/hive?createDatabaseIfNotExist=true</value>    #在hive启动的时候，会在MySQL中生成hive数据库


#在MySQL中存放的是元数据，其中有下面几张表需要注意：
DBS：存放的是创建的数据库

TBLS：是表信息

COLUMNS_V2：是所有的字段信息 

```

![](https://github.com/chenyansong1/note/blob/master/img/bigdata/hive/mysql/1.png?raw=true)



所创建的数据库

![](https://github.com/chenyansong1/note/blob/master/img/bigdata/hive/mysql/2.png?raw=true)


 所有表，如下，我们创建了student表

![](https://github.com/chenyansong1/note/blob/master/img/bigdata/hive/mysql/3.png?raw=true)


所有的字段信息，如下是student表中存在的字段

![](https://github.com/chenyansong1/note/blob/master/img/bigdata/hive/mysql/4.png?raw=true)




# 8.使用方式
## 8.1.Hive交互shell
```
bin/hive
```


## 8.2.Hive thrift服务

![](https://github.com/chenyansong1/note/blob/master/img/bigdata/hive/mysql/5.png?raw=true)


```
#启动方式，（假如是在hadoop01上）：
#启动为前台：
bin/hiveserver2

#启动为后台：
nohup bin/hiveserver2 1>/var/log/hiveserver.log 2>/var/log/hiveserver.err &


#连接
#方式（1）
hive/bin/beeline          #回车，进入beeline的命令界面，输入命令连接hiveserver2
beeline> !connect jdbc:hive2//mini1:10000    #（hadoop01是hiveserver2所启动的那台主机名，端口默认是10000）

#方式（2）：启动就连接：
bin/beeline -u jdbc:hive2://mini1:10000 -n hadoop


'接下来就可以做正常sql查询了'

```

## 8.3.Hive命令
```
hive    -e    <quoted-query-string>            #Sql from command line

#Example
hive    -e    "select    *    from     mytable    limit    3"
OK
name1    10
name2    20
name3    30
Time    taken:4.99t secondes


hive    -f    <filename>            #Sql from files；执行指定文件中的一个或多个查询语句，按照惯例，一般将这些文件保存为具有.q或者.hql后缀名文件
hive    -f    /path/to/file/withqueries.hql



hive    -S,--slient            #Slient mode in interactive    shell  ，将会去掉“OK”和后面的查询时间
#Example
hive   -S    -e    "select    *    from     mytable    limit    3"    >/tmp/myquery            #将执行的结果输出到指定的文件中
cat    /tmp/myquery                              #这样文件中内容就只有查询到的结果
name1    10
name2    20
name3    30



```


# 补充

在使用apache-hive-2.1.1-bin版本进行安装的时候，需要他不会自动的创建hive的元数据，需要我们手动的创建，步骤如下：
1.将元数据的校验关闭
vim hive-site.xml 添加

```
<property>
   <name>hive.metastore.schema.verification</name>
   <value>false</value>
   <description></description>
</property>
```

2.初始化元数据


```
$hive_home/bin/schematool -dbType mysql -initSchema
```
参见：https://cwiki.apache.org/confluence/display/Hive/Hive+Schema+Tool










