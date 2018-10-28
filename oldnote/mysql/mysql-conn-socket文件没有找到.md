

# ERROR 2002 (HY000): Can't connect to local MySQL server through socket '***' (2)

[TOC]

有时候，当我们使用“mysql”、“mysqladmin”、“mysqldump”等命令管理数据库时，服务器抛出类似如下错误：

```
`ERROR 2002 (HY000): Can't connect to local MySQL server through socket '/var/lib/mysql/mysql.sock' (2)`
```

这个错误是由于什么原因导致的呢？

## 一、错误现场还原：

下面我们通过三种方式来连接，然后观察提示的错误信息：

- 1、直接使用“*mysql*”命令，不带主机名参数；
- 2、使用带了主机名“*localhost*”参数的“mysql -h localhost”命令；
- 3、使用带了主机名“*127.0.0.1*”参数的“mysql -h 127.0.0.1”命令。

```
`[root@aiezu.com ~]``# mysql``ERROR 2002 (HY000): Can``'t connect to local MySQL server through socket '``/var/lib/mysql/mysql``.sock' (2)``[root@aiezu.com ~]``# mysql -h localhost``ERROR 2002 (HY000): Can``'t connect to local MySQL server through socket '``/var/lib/mysql/mysql``.sock' (2)``[root@aiezu.com ~]``# mysql -h 127.0.0.1``ERROR 1045 (28000): Access denied ``for` `user ``'root'``@``'localhost'` `(using password: NO)`
```

　　通过上面实验可以看出，前面两种方式都能产生标题中的错误，而第三种方式连接是不会产生标题中的错误的（第三种方式这里产生的是由于密码问题拒绝访问的错误信息）(爱E族)。

##   二、错误产生原因解析：

 　　这是由于我们连接数据库使用的主机名参数为 localhost，或者未使用主机名参数、服务器默认使用“localhost做为主机名（爱Ｅ族）。 使用主机名参数为 localhost 连接mysql服务端时，mysql客户端会认为是连接本机，所以会尝试以socket文件方式进行连接(socket文件连接方式，比“ip：端口”方式效率更高)，这时根据配置文件“/etc/my.cnf”的路径，未找到相应的socket文件，就会引发此错误。

   

## 三、修复故障前准备：

### 1、看mysql服务是否在运行：

　　由于“socket”文件是由mysql服务运行时创建的，如果提示“ERROR 2002 (HY000): Can't connect to local MySQL server through socket '***' (2)”，找不到“socket”文件，我们首先要确认的是mysql服务是否正在运行。

```
`# 1、 端口是否打开``[root@aiezu.com ~]``# lsof -i:3306``COMMAND   PID  USER   FD   TYPE DEVICE SIZE``/OFF` `NODE NAME``mysqld  12207 mysql   14u  IPv4  52350      0t0  TCP *:mysql (LISTEN)` `# 2、mysqld服务是否正在运行``[root@aiezu.com ~]``# service mysqld status``mysqld (pid  4717) is running...` `# 3、如果mariadb，同样方法查服务是否正在运行：``[root@aiezu.com ~]``# service mariadb status``Redirecting to ``/bin/systemctl` `status  mariadb.service``● mariadb.service - MariaDB database server``   ``Loaded: loaded (``/usr/lib/systemd/system/mariadb``.service; disabled; vendor preset: disabled)``   ``Active: active (running) since 四 2016-11-03 13:47:37 CST; 23min ago`
```

  

### 2、确定“socket”文件正确位置：

　　确定mysql服务正常运行后，产生此错误的原因只剩下“socket”文件路径不正确了，我们可以使用“find”命令或者“lsof”命令来确定socket文件的正确路径：

```
`[root@aiezu.com ~]``# lsof  -c mysqld|grep sock$``mysqld 4717 mysql 12u unix 0xffff88010a655b80 0t0 77474827 ``/storage/db/mysql/mysql``.sock` `[root@aiezu.com ~]``# find / -name '*.sock'``/storage/db/mysql/mysql``.sock`
```

 

##  四、故障解决方法：

### 解决方案一：

　　修改“/etc/my.cnf”配置文件，在配置文件中添加“[client]”选项和“[mysql]”选项，并使用这两个选项下的“socket”参数值，与“[mysqld]”选项下的“socket”参数值，指向的socket文件路径完全一致。如下： 

```
`[mysqld]``datadir=/storage/db/mysql``socket=/storage/db/mysql/mysql.sock``...省略n行（爱E族）...` `[client]``default-character-set=utf8``socket=/storage/db/mysql/mysql.sock` `[mysql]``default-character-set=utf8``socket=/storage/db/mysql/mysql.sock`
```

修改完后，重启mysqld服务，即可解决此问题。

　

### 解决方案二：

　　使用“ln -s /storage/db/mysql/mysql.sock /var/lib/mysql/mysql.sock”命令，将正确的socket文件位置，软链接到提示错误的socket文件路径位置，即可解决此问题：

```
[root@aiezu.com ~]# ls /var/lib/mysql/mysql.sock
ls: cannot access /var/lib/mysql/mysql.sock: No such file or directory
[root@aiezu.com ~]# ln -s /storage/db/mysql/mysql.sock /var/lib/mysql/mysql.sock
[root@aiezu.com ~]# ls /var/lib/mysql/mysql.sock
/var/lib/mysql/mysql.sock
```











转自：http://aiezu.com/article/mysql_cant_connect_through_socket.html



