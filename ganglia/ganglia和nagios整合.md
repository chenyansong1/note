# 1.准备

前提是安装了ganglia和nagios

* ganglia的安装：[ganglia安装(编译)](https://github.com/belongtocys/notebook/blob/master/ganglia/ganglia%E5%AE%89%E8%A3%85(%E7%BC%96%E8%AF%91).md) ; [ganglia安装(yum)](https://github.com/belongtocys/notebook/blob/master/ganglia/ganglia%E5%AE%89%E8%A3%85(yum).md)
* nagios的安装：[Linux下Nagios的安装与配置](https://github.com/belongtocys/notebook/blob/master/oldnote/Linux/nagios/Linux%E4%B8%8BNagios%E7%9A%84%E5%AE%89%E8%A3%85%E4%B8%8E%E9%85%8D%E7%BD%AE.md)


# 2.原理说明

NRPE 总共由两部分组成：
check_nrpe 插件，位于监控主机上
NRPE daemon，运行在远程的Linux主机上(通常就是被监控机)
按照上图，整个的监控过程如下：

![](/images/ganglia/ganglia_nagios_zhenghe.jpg)

当Nagios 需要监控某个远程Linux 主机的服务或者资源情况时：

* Nagios 会运行check_nrpe 这个插件，告诉它要检查什么；
* check_nrpe 插件会连接到远程的NRPE daemon，所用的方式是SSL；
* NRPE daemon 会运行相应的Nagios 插件来执行检查；
* NRPE daemon 将检查的结果返回给check_nrpe 插件，插件将其递交给nagios做处理。


注意：NRPE daemon 需要Nagios 插件安装在远程的Linux主机上，否则，daemon不能做任何的监控。



# 2.节点规划说明

|节点名称|部署说明|
|-------|--------|
|hdp-node-01|gmetad,gmond,nrpe|
|hdp-node-02|gmond|
|hdp-node-02|gmond,nagios|


# 3.安装和配置

## 3.1.nagios监控节点安装的说明


参见：[Linux下Nagios的安装与配置](https://github.com/belongtocys/notebook/blob/master/oldnote/Linux/nagios/Linux%E4%B8%8BNagios%E7%9A%84%E5%AE%89%E8%A3%85%E4%B8%8E%E9%85%8D%E7%BD%AE.md) 中有nagios的监控节点的安装配置


## 3.2.被监控节点安装的说明

被监控节点只需要安装：只要安装nagios-plugs 与 nrpe,因为nagios的 监控节点只是从被监控节点（gmetad节点）获取数据，所以只需要在gmetad所在节点安装即可

```
#!/bin/sh

adduser nagios

cd /opt/soft
tar xvf /home/hadoop/nagios-plugins-2.1.1.tar.gz
cd nagios-plugins-2.1.1
mkdir /usr/local/nagios
./configure --prefix=/usr/local/nagios
make && make install

chown nagios.nagios /usr/local/nagios
chown -R nagios.nagios /usr/local/nagios/libexec

#安装xinetd.看你的机器是否有xinetd,如果没有就安装，有的话就不用了
yum install xinetd -y

cd ../
tar xvf /home/hadoop/nrpe-2.15.tar.gz
cd nrpe-2.15
./configure
make all
make install-daemon
make install-daemon-config
make install-xinetd

```


## 3.3.被监控节点配置说明

1.复制check_ganglia.py脚本到nagios(监控节点)的执行目录中

如果有源码，则check_ganglia.py在ganglia-3.6.0/contrib/check_ganglia.py中
如果没有源码，[这里有源文件](/ganglia/check_ganglia.py)

```
#cp check_ganglia.py  /usr/local/nagios/libexec/
```

2.修改gmetad配置，使其share监控数据

因为默认情况下，ganglia的gmetad服务不会share监控指标给网络上的其他机器，默认只能把数据传输到localhost，所以需要做相应的配置，使其可以share相应数据给其他机器。主要是考虑nagios的主机与ganglia的主机没在同一台机器上。

```
# vi /etc/ganglia/gmetad.conf
trusted_hosts 192.168.7.12  ##添加信任的主机IP
```

3.修改check_ganglia.py脚本的端口号和ip地址

因为check_ganglia.py脚本默认只会从localhost去获得ganglia中gmetad的监控数据，所以此处需要修改脚本，使其可以从集群中某一台主机获取监控指标

```
#vi /usr/local/nagios/libexec/check_ganglia.py
ganglia_host = '192.168.7.17'  ##指定gmetad机器的IP地址  
ganglia_port = 8649             ##指定gmetad机器的端口地址  

```

4.修改check_ganglia.py脚本，监控只对大于等于设定值的情况可以进行报警提示，没有对等于小于的情况做报警提示（**可以根据自己应用场景考虑是否修改脚本**）

脚本之中，只对监控值大于设定值时做了报警提示，比如:温度大于 50度，发出warning报警，大于80度时，发出critical报警;而相对小于设定值做报警的提示，则不能实现。比如：当内存的空闲值小于 100M，发出warning报警，小于80M，发出critical报警,不能实现,此时就需要对check_ganglia做如下修改：

```
# vim  /usr/local/nagios/libexec/check_ganglia.py
#这里是大于的情况，else后面是小于的情况
  if critical > warning:
    if value >= critical:
      print "CHECKGANGLIA CRITICAL: %s is %.2f" % (metric, value)
      sys.exit(2)
    elif value >= warning:
      print "CHECKGANGLIA WARNING: %s is %.2f" % (metric, value)
      sys.exit(1)
    else:
      print "CHECKGANGLIA OK: %s is %.2f" % (metric, value)
      sys.exit(0)
  else:
    if critical >=value:
      print "CHECKGANGLIA CRITICAL: %s is %.2f" % (metric, value)
      sys.exit(2)
    elif warning >=value:
      print "CHECKGANGLIA WARNING: %s is %.2f" % (metric, value)
      sys.exit(1)
    else:
      print "CHECKGANGLIA OK: %s is %.2f" % (metric, value)
      sys.exit(0)
```


5.配置service，host,command

**修改　commands.cfg**

在文件最后加上如下内容
```
# 'check_ganglia' command definition
define command{
        command_name    check_ganglia
        command_line    $USER1$/check_ganglia.py -h $HOSTADDRESS$ -m $ARG1$ -w $ARG2$ -c $ARG3$
        }

#####解释说明#####
# check_ganglia.py 是这个脚本
# -h 是指定需要哪个主机的监控信息（因为gmetad上有收集了hdp-node-01,hdp-node-02,hdp-node-03的信息），所以这里需要去指定拿哪个主机的监控信息
# -m	 是指定监控的指标（如：load_one 就是一分钟内的平均负载）
# -w 是warning时的阈值
# -c 是critical时的阈值
# 其中如果warning是的阈值大于critical时的阈值，那么此时比较就是在做小于的比较（可以看check_ganglia.py脚本的后面部分）

```

**修改templates.cfg**


为3台主机添加如下的模板

```
#add ganglia monitor template
define service {
        use generic-service
        name ganglia-service1
        hostgroup_name hdp-node-01
        service_groups ganglia-metrics1
        register        0
}

define service {
        use generic-service
        name ganglia-service2
        hostgroup_name hdp-node-02
        service_groups ganglia-metrics2
        register        0
}
define service {
        use generic-service
        name ganglia-service3
        hostgroup_name hdp-node-03
        service_groups ganglia-metrics3
        register        0
}
```


**主机host.cfg配置**

这个默认是没有，用localhost.cfg 拷贝来
```
$cp localhost.cfg host_hdp-node-01.cfg 
```

这里面除了配置host信息，还有会主机的一些基本service信息

```
# cat  host_hdp-node-01.cfg     
define host{   
        use                     linux-server 
        host_name               hdp-node-01
        alias                   hdp-node-01
        address                hdp-node-01
        }
 
define hostgroup { 
        hostgroup_name  hdp-node-01
        alias  hdp-node-01
        members hdp-node-01
        }
define service{
        use                             local-service
        host_name                       hdp-node-01
        service_description             PING
        check_command                   check_ping!100,20%!500,60%
        }
 
define service{
        use                             local-service
        host_name                      hdp-node-01
        service_description             根分区
        check_command                   check_local_disk!20%!10%!/
        }
 
define service{
        use                             local-service
        host_name                       hdp-node-01
        service_description             用户数量
        check_command                   check_local_users!20!50
        }
 
define service{
        use                             local-service
        host_name                       hdp-node-01
        service_description             进程数
        check_command                   check_local_procs!550!650!RSZDT
        }
 
define service{ 
        use                             local-service         
        host_name                       hdp-node-01
        service_description             系统负载
        check_command                   check_local_load!5.0,4.0,3.0!10.0,6.0,4.0
} 

```

其他2个节点做类似的修改即可

**service1.cfg 配置**

默认没有service１.cfg，新建一个(这里就会调用在command中定义的check_ganglia命令)，下面的命令去查询了mem_free，和hadoop中的NameNode同步
```
# vim service_hdp-node-01.cfg   
 
define servicegroup {
        servicegroup_name ganglia-metrics1
        alias Ganglia Metrics1
} 

define service{ 
        use                             ganglia-service1
        service_description             内存空闲
        check_command                   check_ganglia!mem_free!200!50
} 
 
define service{
        use                             ganglia-service1
        service_description             NameNode同步
        check_command                   check_ganglia!dfs.namenode.SyncsAvgTime!10!50
}

```
其他2个节点做类似的修改即可

**修改　nagios.cfg**

将以上的修改文件包含进来

```
# ganglia host
cfg_file=/usr/local/nagios/etc/objects/host_hdp-node-01.cfg
cfg_file=/usr/local/nagios/etc/objects/host_hdp-node-02.cfg
cfg_file=/usr/local/nagios/etc/objects/host_hdp-node-03.cfg


# ganglia service
cfg_file=/usr/local/nagios/etc/objects/service_hdp-node-01.cfg
cfg_file=/usr/local/nagios/etc/objects/service_hdp-node-02.cfg
cfg_file=/usr/local/nagios/etc/objects/service_hdp-node-03.cfg

```

**验证配置是否正确**

```
/usr/local/nagios/etc/bin/nagios -v nagios.cfg 
```

**启动监控节点的nagios和被监控节点（gmetad）的nrpe**

没有错误，这时就可以启动**监控节点**上的nagios服务

```
#在监控节点上：启动nagios
/etc/init.d/nagios start
Starting nagios: done.

#在监控节点上：启动httpd
/usr/local/apache2/bin/apachectl start


#在被监控节点上：启动nrpe

service gmetad restart
service gmond restart
service httpd start

#因为将nrpe加入了xinetd，需要重启
service xinetd restart

```


工作正常，现在我们可以nagios的web页面，看是否监控成功。

localhost:8080/nagios

![](/images/ganglia/web_play.jpg)


# 4.邮件报警设置

先检查服务器是否安装sendmail
```
$ rpm -q sendmail
$ yum install sendmail  #如果没有就安装sendmail
$ service sendmail restart  #重启sendmail
```

因为给外部发邮件，需要服务器自己有邮件服务器，这很麻烦并且非常占资源．这里我们配置一下，使用现有的STMP服务器,配置地址　/etc/mail.rc

```
$ vim /etc/mail.rc

set from=cys@qq.com
set smtp=mail.qq.com smtp-auth-user=cys smtp-auth-password=111111 smtp-auth=login
```

配置完毕之后，就可以先命令行测试一下，是否可以发邮件了

```
$ echo "hello world" |mail -s "test" cys@qq.com
```
如果看你的邮件已经收到邮件了，说明sendmail已经没有问题．


下面配置nagios的邮件告警配置

```
$ vim /usr/local/nagios/etc/objects/contacts.cfg
define contact{
        contact_name                    nagiosadmin             ; Short name of user
        use                             generic-contact         ; Inherit default values from generic-contact template (defined above)
        alias                           Nagios Admin            ; Full name of user
        ## 告警时间段
        service_notification_period     24x7
        host_notification_period        24x7
        ## 告警信息格式
        service_notification_options    w,u,c,r,f,s
        host_notification_options       d,u,r,f,s
        ## 告警方式为邮件
        service_notification_commands   notify-service-by-email
        host_notification_commands      notify-host-by-email
        email                           cys@qq.com      ; <<***** CHANGE THIS TO YOUR EMAIL ADDRESS ******
        }


# We only have one contact in this simple configuration file, so there is
# no need to create more than one contact group.

define contactgroup{
        contactgroup_name       admins
        alias                   Nagios Administrators
        members                 nagiosadmin
        }
```

至此配置全部完成



参考：

http://www.cnblogs.com/pingjie/p/4809489.html 

http://blog.csdn.net/shifenglov/article/details/40658007 

http://www.cnblogs.com/mchina/archive/2013/02/20/2883404.html#!comments 




























