

[TOC]



参见：http://blog.51cto.com/ant595/1080922



# syslog



syslog服务，为各个程序负责记录日志

​	syslogd:系统日志，非内核产生的信息

​	klogd：内核日志



kernel : 物理终端（/dev/console) —>

​	/var/log/dmesg

​	dmesg



## 系统日志



/sbin/init

​	/var/log/messages:系统标准错误日志信息，非内核产生的引导信息；各个子系统产生的信息（该日志文件会滚动，避免文件过大）

​	/var/log/maillog:邮件系统产生的日志信息

​	/var/log/secure:安全相关



### /var/log/messages



logrotate:日志切割



在 /etc/cron.daily/logrotate 下有一个天执行的任务，如下：

![image-20181005222800450](/Users/chenyansong/Documents/note/images/linux/log/logrotage.png)

配置文件如下

![image-20181005223112362](/Users/chenyansong/Documents/note/images/linux/log/logrotage2.png)



会包含 /etc/logrotate.d下的配置文件，**这个目录下的配置文件会包含每个子系统的日志配置信息**

![image-20181005223241528](/Users/chenyansong/Documents/note/images/linux/log/logrotage3.png)



我们随便看一个CPU的日志配置

![image-20181005223632906](/Users/chenyansong/Documents/note/images/linux/log/logrotage4.png)



### /var/log/secure

登录信息

![image-20181005224105550](/Users/chenyansong/Documents/note/images/linux/log/secure.png)





## 日志格式，级别，日志的位置



![image-20181005224940297](/Users/chenyansong/Documents/note/images/linux/log/syslog-format0.png)



下面是可选的子系统，即：我们可以为下面的子系统配置日志

![image-20181005224608736](/Users/chenyansong/Documents/note/images/linux/log/syslog-format.png)



下面是可选的日志级别

![image-20181005225322032](/Users/chenyansong/Documents/note/images/linux/log/syslog-level.png)





执行的动作（action），日志记录的位置

![image-20181005225446957](/Users/chenyansong/Documents/note/images/linux/log/syslog_action.png)



实例



![image-20181005225809448](/Users/chenyansong/Documents/note/images/linux/log/syslog-example.png)



## 配置文件



下面是配置文件

/etc/syslog.conf

```
[root@localhost ~]# cat /etc/syslog.conf
 
# Log all kernel messages to the console.
# Logging much else clutters up the screen.
#kern.* /dev/console
# Log anything (except mail) of level info or higher.
 
# Don't log private authentication messages!
*.info;mail.none;authpriv.none;cron.none /var/log/messages
 
# The authpriv file has restricted access.
authpriv.* /var/log/secure
 
# Log all the mail messages in one place. -表示异步写入，先写入的是内存，然后同步到磁盘
mail.* -/var/log/maillog
 
# Log cron stuff
cron.* /var/log/cron
 
# Everybody gets emergency messages
*.emerg *
 
# Save news errors of level crit and higher in a special file.
uucp,news.crit /var/log/spooler
 
# Save boot messages also to boot.log
local7.* /var/log/boot.log
```



如果我们修改了配置文件，一般会重启服务，但是此处如果重启服务，那么别人就不能写日志了，所以这里使用的是重新读取配置文件

```
#推荐
service syslog reload

#重启服务
service syslog restart

```





## syslog服务程序

/etc/rc.d/init.d/syslog 程序会**加载一个配置文件****，**如下：

![image-20181005231807594](/Users/chenyansong/Documents/note/images/linux/log/syslog-config.png)



我们可以看一下syslog程序的配置文件（有了-r就可以作为日志服务器，接收remote的syslog）

![image-20181005232050852](/Users/chenyansong/Documents/note/images/linux/log/syslog-config2.png)



**重启日志服务器的syslog服务**





# syslog-ng

