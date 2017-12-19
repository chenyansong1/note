---
title: HiveServer2, Beeline, JDBC的使用
categories: hive  
tags: [hive]
---



# 启动hiveserver2服务

hiveServer2是将hive作为一个服务启动,这样可以有更多的客户端连接过来,他启动的进程服务叫RunJar

<!--more-->

```
bin/hiveserver2

```

# beeline连接hiveserver2


```

bin/beeline
beeline>!connec jdbc:hive2://hdp-node-01:10000

用户名输入当前用户(root),密码是空(回车即可)

#这样就相当于进入了cli中

[root@hdp-node-01 hive]# bin/hiveserver2 &
[root@hdp-node-01 hive]# bin/beeline 
Beeline version 1.2.1 by Apache Hive
#去连接服务
beeline> !connec jdbc:hive2://hdp-node-01:10000
Connecting to jdbc:hive2://hdp-node-01:10000
#输入用户名和密码
Enter username for jdbc:hive2://hdp-node-01:10000: root
Enter password for jdbc:hive2://hdp-node-01:10000: 
Connected to: Apache Hive (version 1.2.1)
Driver: Hive JDBC (version 1.2.1)
Transaction isolation: TRANSACTION_REPEATABLE_READ
0: jdbc:hive2://hdp-node-01:10000> 
#执行查询语句
0: jdbc:hive2://hdp-node-01:10000> select * from default.emp;
OK
+------------+------------+------------+----------+---------------+----------+-----------+-------------+--+
| emp.empno  | emp.ename  |  emp.job   | emp.mgr  | emp.hiredate  | emp.sal  | emp.comm  | emp.deptno  |
+------------+------------+------------+----------+---------------+----------+-----------+-------------+--+
| 7369       | SMITH      | CLERK      | 7902     | 1980-12-17    | 800.0    | NULL      | 20          |
| 7499       | ALLEN      | SALESMAN   | 7698     | 1981-2-20     | 1600.0   | 300.0     | 30          |
| 7521       | WARD       | SALESMAN   | 7698     | 1981-2-22     | 1250.0   | 500.0     | 30          |
| 7566       | JONES      | MANAGER    | 7839     | 1981-4-2      | 2975.0   | NULL      | 20          |
| 7654       | MARTIN     | SALESMAN   | 7698     | 1981-9-28     | 1250.0   | 1400.0    | 30          |

其实beeline显示的数据相比较于hive 的cli更加的规整,我们更容易观察,但是他不会显示执行的日志信息,日志信息在hiveserver中

```


# JDBC连接hiveserver2服务

```
#下面是连接的步骤:

Class.forName("org.apache.hive.jdbc.HiveDriver");
 
Connection cnct = DriverManager.getConnection("jdbc:hive2://<host>:<port>", "<user>", "<password>");
//Connection cnct = DriverManager.getConnection("jdbc:hive2://<host>:<port>", "<user>", "");
 
Statement stmt = cnct.createStatement();
ResultSet rset = stmt.executeQuery("SELECT foo FROM bar");


```

下面是官网的例子
```
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;
 
public class HiveJdbcClient {
  private static String driverName = "org.apache.hive.jdbc.HiveDriver";
 
  /**
   * @param args
   * @throws SQLException
   */
  public static void main(String[] args) throws SQLException {
      try {
	//加载驱动
      Class.forName(driverName);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }
    //获取JDBC连接
    Connection con = DriverManager.getConnection("jdbc:hive2://localhost:10000/default", "hive", "");

    Statement stmt = con.createStatement();
    String tableName = "testHiveDriverTable";
    stmt.execute("drop table if exists " + tableName);
    stmt.execute("create table " + tableName + " (key int, value string)");
    // show tables
    String sql = "show tables '" + tableName + "'";
    System.out.println("Running: " + sql);
    ResultSet res = stmt.executeQuery(sql);
    if (res.next()) {
      System.out.println(res.getString(1));
    }
       // describe table
    sql = "describe " + tableName;
    System.out.println("Running: " + sql);
    res = stmt.executeQuery(sql);
    while (res.next()) {
      System.out.println(res.getString(1) + "\t" + res.getString(2));
    }
 
    // load data into table
    // NOTE: filepath has to be local to the hive server
    // NOTE: /tmp/a.txt is a ctrl-A separated file with two fields per line
    String filepath = "/tmp/a.txt";
    sql = "load data local inpath '" + filepath + "' into table " + tableName;
    System.out.println("Running: " + sql);
    stmt.execute(sql);
 
    // select * query
    sql = "select * from " + tableName;
    System.out.println("Running: " + sql);
    res = stmt.executeQuery(sql);
    while (res.next()) {
      System.out.println(String.valueOf(res.getInt(1)) + "\t" + res.getString(2));
    }
 
    // regular hive query
    sql = "select count(1) from " + tableName;
    System.out.println("Running: " + sql);
    res = stmt.executeQuery(sql);
    while (res.next()) {
      System.out.println(res.getString(1));
    }
  }
}

#注意上面的代码对于jdbc的连接关闭没有做,我们在实际生产中要有的
```

hiveserver2和jdbc的使用场景:
将hive中的分析结果存储在hive表(result),前端通过dao代码,进行数据的查询(因为结果的数据集很少,所以很快)

hiveserver2的并发有点问题



