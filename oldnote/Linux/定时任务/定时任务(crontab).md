---
title: 定时任务(crontab)
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



# 1.定时任务crond介绍

&emsp;crond是linux系统中用来定期执行命令或指定程序任务的一种服务或软件
&emsp;特殊需求：（秒级别）crond服务就无法搞定了，一般工作中写脚本守护进程执行。

```
crond    #守护进程    一直运行着
crontab    #设置命令    -l list     -e edit
```

## 1.1.crond是什么

&emsp;linux系统的定时任务crond,相当于我们平时生活中的闹钟的功能。可以满足周期性执行任务的需求。


## 1.2 为什么要使用crond定时任务


## 1.3 不同系统的定时任务和种类


### 1.3.1 windows 7 系统的定时任务
&emsp;开始→所有程序→附件→系统工具→选择任务计划程序

### 1.3.2 linux系统的定时任务
&emsp;linux系统中定时任务调度的工作可以分为以下两个情况：
&emsp;情况一:linux系统自身定期执行的任务工作：系统周期性执行的任务工作，如轮询系统日志，备份系统数据，清理系统缓存等。

> centos5.X例：

```
[root@CentOS5 log]# ll messages*
-rw------- 1 root root 372258 Mar 14 20:48 messages
-rw------- 1 root root 349535 Nov 11 18:13 messages.1

#提示：centos 6.4日志轮询结尾是按时间了。
```

> centos6.X例：

```
[root@CentOS6 log]# ll messages*
-rw------- 1 root root  1591 3月  25 21:57 messages
-rw------- 1 root root 78304 3月   3 20:40 messages-20140303
-rw------- 1 root root 78050 3月   8 19:42 messages-20140311
-rw------- 1 root root   745 3月  18 00:46 messages-20140318
-rw------- 1 root root 77232 3月  22 21:20 messages-20140325

```

&emsp;情况二:用户执行的任务工作：某个用户或系统管理员定期要做的任务工作，例如每隔5分钟和互联网上时间服务
器进行时间同步，每天晚上0点备份站点数据及数据库数据，一般这些工作需要由每个用户自行设置才行。
```
[root@CentOS6 log]# crontab -l
#time sync by lee at 2014-1-14
*/5 * * * * /usr/sbin/ntpdate  time.windows.com >/dev/null  2>&1
```


# 2.定时任务crond使用说明

```
[root@CentOS6 log]# crontab --help
crontab：无效选项 -- -
crontab: usage error: unrecognized option
usage:  crontab [-u user] file
        crontab [-u user] [ -e | -l | -r ]
                (default operation is replace, per 1003.2)
 -e      (edit user's crontab)    #重点
 -l      (list user's crontab)        #重点
        -r      (delete user's crontab)
        -i      (prompt before deleting user's crontab)
        -s      (selinux context)

/*
crontab -e     ===  vi /var/spool/cron/root
crontab -l      ===   cat /var/spool/cron/root

如果是root用户，编辑或者是查看的是：/var/spool/cron/root
但是如果是用户：zhangsan，则编辑或查看的是：/var/spool/cron/zhangsan
*/

```

## 2.1 指令说明
通过crontab我们可以在固定的间隔时间执行指定的系统指令或script脚本。时间间隔的单位是分钟，小时，日，月，周及以上的任意组合（注意：日和周不要组合）

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/定时任务/1.png)


## 2.2 使用者权限及定时任务文件

|文件|说明|
|-|-|
|/etc/cron.deny|该文件中所列用户不允许使用crontab命令。|
|/etc/cron.allow|该文件中所列用户允许使用crontab命令，优先于/etc/cron.deny|
|/var/spool/cron|所有用户crontab配置文件默认都存放在此目录，文件名以用户名命名。|


## 2.3 指定用户创建定时任务（-u）
&emsp;当一个用户(chenyansong)创建一个定时任务的时候，即：crontab -e ,此时会有:/var/spool/cron/chenyansong文件创建
&emsp;那么在当前用户下，怎样以其他用户的名义创建定时任务呢？
> 方式一：

```
[root@lamp01 chenyansong]# crontab -u chenyansong -e

[root@lamp01 chenyansong]# crontab -u chenyansong -l
######

```
> 方式2:

```
su - chenyansong    #切换用户,然后再创建
crontab -e
crontab -l
        
```

> 另外一个问题：

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/定时任务/2.png)

 
那么chenyansong用户又是怎么进入/var/spool/cron/ ，然后去查看 /var/spool/cron/chenyansong 的呢？
 
原因：用户（chenyansong）使用：crontab

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/定时任务/3.png)
 

## 2.4 指令选项说明表
|参数|含义|指定示例|
|-|-|-|
|-l(字母)|查看crontab文件内容|crontab -l|
|-e|编辑crontab文件内容|crontab -e|
|-i|删除crontab文件内容,删除前会提示确认|crontab -ri|
|-r|删除crontab文件内容|crontab -r|
|-u user|指定使用的用户执行任务|crontab -u lee -l|
|提示:crontab{-l,-e}实际上就是操作/var/spool/cron当前用户这样的文件|


注意:
|crontab -e|/var/spool/cron/root|前者会检查语法，而后者不会。|
|-|-|-|
|visudo|/etc/sudoers|前者会检查语法，而后者不会。|



## 2.5.指令的使用格式
&emsp;默认情况下，当用户建立定时任务规则后，该规则记录对应的配置文件会存在于/var/spool/cron中，其crontab配
置文件对应的文件名与登录的用户名一致。如：root用户的定时任务配置文件为/var/spool/cron/root。
&emsp;crontab用户的定时任务一般分为6段（空格分隔，系统的定时任务则/etc/crontab分为7段），其中前五段位时间设定段，第六段为所要执行的命令或脚本任务段。

### 2.5.1 crontab基本格式
```
* * * * * cmd
/*
提示：
1.cmd为要执行的命令或脚本，例如/bin/sh  /server/scripts/lee.sh.
2.每个段之间必须要有空格。
*/
```

### 2.5.2 crontab语法格式中时间段的含义表

|段|含义|取值范围|
|-|-|-|
|第一段|代表分钟|00-59|
|第二段|代表小时|00-23|
|第三段|代表日期|01-31|
|第四段|代表月份|01-12|
|第五段|代表星期|0-7(0和7都代表星期日)|


### 2.5.3 crontab语法格式中特殊符号的含义表
|特殊符号|含义|
|-|-|
|*|*号表示任意时间都，就是“每”的意思，举例：如00 01 * * * cmd表示每月每周每日的凌晨1点执行cmd任务|
|-|减号，表示分隔符，表示一个时间范围段，如17-19点，每小时的00分执行任务。00 17-19 * * * cmd。就是17,18,19点整点分别执行的意思|
|，|逗号，表示分隔时间段的意思。30 17,18,19 * * * cmd 表示每天17,18,19点的半点执行cmd。也可以和“-”结合使用，如： 30 3-5,17-19 * * * cmd|
|/n   | n代表数字，即”每隔n单位时间”,例如：每10分钟执行一次任务可以写 */10 * * * * cmd,其中 */10，*的范围是0-59，也可以写成0-59/10|


# 3.书写crond定时任务7个基本要领

## 3.1 为定时任务规则加必要的注释
&emsp;加了注释，就知道定时任务运行的是什么作业，以防以后作业混乱。这是个好习惯和规范。
```
[root@angelT ~]# crontab -l
#time sync by lee at 2014-1-14    #这就是添加的注释
*/5 * * * * /usr/sbin/ntpdate  time.windows.com >/dev/null  2>&1

```



## 3.2 定时任务命令或程序最好写到脚本里执行
```
[root@angelT ~]# crontab -l
#backup www to /backup
00 00 * * * /bin/sh /server/scripts/www_bak.sh >/dev/null  2>&1    #写到www_bak.sh脚本中

```

## 3.3定时任务执行的脚本要规范路径，如：/server/scripts

```
[root@angelT ~]# crontab -l
#backup www to /backup
00 00 * * * /bin/sh /server/scripts/www_bak.sh >/dev/null  2>&1   
```


## 3.4执行shell脚本任务时前加/bin/sh
&emsp;执行定时任务时，如果是执行脚本，尽量在脚本前面带上/bin/sh命名（这样就不用考虑权限的问题），否则有可能因为忘了为脚本设定执行权限，从而无法完成任务。
```
[root@angelT ~]# crontab -l
#backup www to /backup
00 00 * * * /bin/sh /server/scripts/www_bak.sh >/dev/null  2>&1

```


## 3.5 定时任务结尾加 >/dev/null 2>&1
```
[root@angelT ~]# crontab -l
#backup www to /backup
00 00 * * * /bin/sh /server/scripts/www_bak.sh >/dev/null  2>&1

```

### 3.5.1 有关/dev/null的说明

```
# /dev/null为特殊的字符设备文件，表示黑洞设备或空设备。
[root@angelT ~]# ll /dev/null 
crw-rw-rw- 1 root root 1, 3 3月  26 01:10 /dev/null

```

### 3.5.2 有关重定向的说明
```
>或1>   输出重定向：把前面输出的东西输入到后边的文件中，会删除文件原有内容。
>>或1>>追加重定向：把前面输出的东西追加到后边的文件中，不会删除文件原有内容。
<或<0   输入重定向：输入重定向用于改变命令的输入，指定输入内容，后跟文件名。
<<或<<0输入重定向：后跟字符串，用来表示“输入结束”，也可用ctrl+d来结束输入。
2>       错误重定向：把错误信息输入到后边的文件中，会删除文件原有内容。
2>>     错误追加重定向：把错误信息追加到后边的文件中，不会删除文件原有内容。
标准输入（stdin）：代码为0，使用<或<<。
标准输出（stdout）:代码为1，使用>或>>。正常的输出。
标准错误输出（sederr）：代码为2，使用2>或2>>。
特殊：
2>&1就是把标准错误重定向到标准输出（>&）。
>/dev/null 2>&1 等价于 1>/dev/null  2>/dev/null

```

### 3.5.3 >/dev/null 2>&1的作用
如果定时任务规范结尾不加 >/dev/null 2>&1,很容易导致硬盘inode空间被占满，从而系统服务不正常（var/spool/clientmqueue邮件临时队列目录，垃圾文件存放于此，如果是centos 6.4系统，默认不装sendmail服务，所以不会有这个目录。）

## 3.6 在指定用户下执行相关定时任务
&emsp;这里要特别注意不同用户的环境变量问题，如果是调用了系统环境变量/etc/profile，最好在程序脚本中将用到的环境变量重新export下。


## 3.7生产任务程序不要随意打印输出信息

&emsp;在调试好脚本程序后，应尽量把DEBUG及命令输出的内容信息屏蔽掉，如果确实需要输出日志，可定向到日志文件里，避免产生系统垃圾。


# 4. 配置定时任务规范操作过程


![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/定时任务/4.png)

![](http://ols7leonh.bkt.clouddn.com//assert/img/linux/定时任务/5.png)
 

# 5.生产场景如何调试crond定时任务

&emsp;规范的公司开发和运维人员操作流程：个人的开发配置环境-->办公室的测试环境-->idc机房的测试环境-->idc机房的正式环境

## 5.1 增加执行频率调试任务
&emsp;在调试时，把任务执行频率调快一点，看能不能正常执行，如果正常，那就没问题了，再改成需要的任务的执行时间。
&emsp;注意：有些任务时不允许频繁执行的，例如：定时往数据库里插入数据，这样的任务要在测试机上测试好，然后正式线上出问题的机会就少了。
 

## 5.2调整系统时间调试任务

&emsp;用正确的执行任务的时间，设置完成后，可以修改下系统当前时间，改成任务执行时间的前几分钟来测试（或者重启定时任务服务）


## 5.3通过日志输出调试定时任务
&emsp;在脚本中加入日志输出，然后把输出打到指定的日志中，然后观察日志内容的结果，看是否正确执行

## 5.4注意一些任务命令带来的问题

&emsp;注意： * * * * * echo “==”>>/tmp/lee.log >/dev/null 2>&1 这里隐藏的无法正确执行的任务配置，原因是前面多了>>,或者去掉结尾的 >/dev/null 2>&1。


## 5.5 注意环境变量导致的定时任务故障

crontab执行shell时只能识别为数不多的系统环境变量，一般用户定义的普通变量无法是别的，如果在编写的脚本中需要使用这些变量，最好使用export重新声明该变量，脚本才能正常执行，例如：在调试java程序任务的时候，注意环境变量，把环境变量的定义加到脚本里。
参见：http://oldboy.blog.51cto.com/2561410/1541515


## 5.6通过定时任务日志调试定时任务

```
[root@angelT ~]# tail -f /var/log/cron 
Mar 26 15:55:01 angelT CROND[3415]: (ida) CMD (/usr/sbin/ntpdate time.windows.com >/dev/null 2>&1)
Mar 26 15:55:01 angelT CROND[3416]: (root) CMD (/usr/sbin/ntpdate  time.windows.com >/dev/null  2>&1)
Mar 26 16:00:01 angelT CROND[3422]: (root) CMD (/usr/sbin/ntpdate  time.windows.com >/dev/null  2>&1)
Mar 26 16:00:01 angelT CROND[3423]: (root) CMD (/usr/lib64/sa/sa1 1 1)

```

# 注意：

1.crontab 需要加载 环境变量
 
```
有时我们创建了一个crontab，但是这个任务却无法自动执行，而手动执行这个任务却没有问题，这种情况一般是由于在crontab文件中没有配置环境变量引起的。

#!/bin/sh
source /etc/profile		# 加载环境变量

start_tomcat=$tomcat_home/bin/startup.sh
$start_tomcat

```

2.crontab中要写全路径







[整理自:老男孩](http://oldboy.blog.51cto.com/)
