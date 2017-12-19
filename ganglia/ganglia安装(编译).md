需要配置监控节点到各个被监控节点的ssh免密码登录

需要关闭SELinux，（坑）

rddganglia的架构如下图所示

![](/images/ganglia/jiagou.png)


在监控节点上只需要安装gmetad,ganglia-web（用于web展示），而在被监控的节点上需要安装gmond

# 1.依赖检验,安装

新建一个 ganglia.rpm 文件,写入以下依赖组件

```
$ vim ganglia.rpm
apr-devel
apr-util
check-devel
cairo-devel
pango-devel
libxml2-devel
glib2-devel
dbus-devel
freetype-devel
fontconfig-devel
gcc-c++
expat-devel
python-devel
rrdtool
rrdtool-devel
libXrender-devel
zlib
libart_lgpl
libpng
dejavu-lgc-sans-mono-fonts
dejavu-sans-mono-fonts
perl-ExtUtils-CBuilder
perl-ExtUtils-MakeMaker

```

查看这些组件是否有安装

```
$ rpm -q `cat ganglia.rpm`
package apr-devel is not installed
apr-util-1.3.9-3.el6_0.1.x86_64
check-devel-0.9.8-1.1.el6.x86_64
cairo-devel-1.8.8-3.1.el6.x86_64
pango-devel-1.28.1-10.el6.x86_64
libxml2-devel-2.7.6-14.el6_5.2.x86_64
glib2-devel-2.28.8-4.el6.x86_64
dbus-devel-1.2.24-7.el6_3.x86_64
freetype-devel-2.3.11-14.el6_3.1.x86_64
fontconfig-devel-2.8.0-5.el6.x86_64
gcc-c++-4.4.7-11.el6.x86_64
package expat-devel is not installed
python-devel-2.6.6-52.el6.x86_64
libXrender-devel-0.9.8-2.1.el6.x86_64
zlib-1.2.3-29.el6.x86_64
libart_lgpl-2.3.20-5.1.el6.x86_64
libpng-1.2.49-1.el6_2.x86_64
package dejavu-lgc-sans-mono-fonts is not installed
package dejavu-sans-mono-fonts is not installed
perl-ExtUtils-CBuilder-0.27-136.el6.x86_64
perl-ExtUtils-MakeMaker-6.55-136.el6.x86_64

```
使用 yum install 安装机器上没有的组件

**还要安装 confuse**

下载地址:https://github.com/martinh/libconfuse#download

```
tar -zxf confuse-2.7.tar.gz
cd confuse-2.7
./configure CFLAGS=-fPIC --disable-nls
make && make install
```



# 2.安装ganglia

```
$ tar -xvf /home/hadoop/ganglia-3.6.0.tar.gz -C /opt/soft/

```

**安装gmetad**

```
./configure --prefix=/usr/local/ganglia --with-gmetad --with-libpcre=no --enable-gexec --enable-status --sysconfdir=/etc/ganglia
make && make install
cp gmetad/gmetad.init /etc/init.d/gmetad
cp /usr/local/ganglia/sbin/gmetad /usr/sbin/
chkconfig --add gmetad

```
**安装gmond**

```
cp gmond/gmond.init /etc/init.d/gmond
cp /usr/local/ganglia/sbin/gmond /usr/sbin/
gmond --default_config>/etc/ganglia/gmond.conf
chkconfig --add gmond
```

可能会出现的错误
```
[root@hdp-node-01 ganglia-3.7.2]# gmond --default_config>/etc/ganglia/gmond.conf
gmond: error while loading shared libraries: libconfuse.so.1: cannot open shared object file: No such file or directory
[root@hdp-node-01 ganglia-3.7.2]# ln -s /usr/local/lib/libconfuse.so.1 /lib64/
[root@hdp-node-01 ganglia-3.7.2]# gmond --default_config>/etc/ganglia/gmond.conf

```

**安装php和httpd**

```
yum install php httpd -y
```

修改httpd的配置文件/etc/httpd/conf/httpd.conf，只把监听端口改为8080

```
Listen 8080
```

**安装ganglia-web**

```
tar xf ganglia-web-3.6.2.tar.gz  -C /opt/soft/
cd /opt/soft/
chmod -R 777 ganglia-web-3.6.2/
mv ganglia-web-3.6.2/ /var/www/html/ganglia
cd /var/www/html/ganglia
useradd www-data 
make install 
chmod 777 /var/lib/ganglia-web/dwoo/cache/ 
chmod 777 /var/lib/ganglia-web/dwoo/compiled/
```

至此ganglia-web安装完成，修改conf_default.php修改文件，指定ganglia-web的目录及rrds的数据目录，修改如下两行：

```
# Where gmetad stores the rrd archives.
conf['gmetad_root'] = "/var/www/html/ganglia"; ## 改为web程序的安装目录
conf['rrds'] = "/var/lib/ganglia/rrds";        ## 指定rrd数据存放的路径

```

创建rrd数据存放目录并授权

```
mkdir /var/lib/ganglia/rrds -p
chown nobody:nobody /var/lib/ganglia/rrds/ -R

```

在rrds目录下会有这样的目录结构
```
drwxr-xr-x. 6 ganglia ganglia   4096 Jun 29 10:20 hadoop		#表示集群的名称
drwxr-xr-x. 2 ganglia ganglia 176128 Jul  7 14:42 __SummaryInfo__ #这里所有数据的汇总

#进入到hadoop集群中
[root@hdp-node-01 hadoop]# pwd
/var/lib/ganglia/rrds/hadoop
[root@hdp-node-01 hadoop]# ll
total 452
drwxr-xr-x. 2 ganglia ganglia 110592 Jul  7 14:56 hdp-node-01	#每个机器节点对应的数据
drwxr-xr-x  2 ganglia ganglia  81920 Jul  7 14:38 hdp-node-02
drwxr-xr-x  2 ganglia ganglia  77824 Jul  7 14:55 hdp-node-03
drwxr-xr-x. 2 ganglia ganglia 176128 Jul  7 14:42 __SummaryInfo__	#这里是hadoop集群的数据的汇总
[root@hdp-node-01 hadoop]# 
```

到这里，收集节点上的ganglia的所有安装工作就完成了，接下来就是要在其他所有节点上安装ganglia的gmond客户端。

**其他节点安装上gmond**

也是要先安装依赖,然后在安装gmond,所有节点安装都是一样的,所以这里写个脚本

```
$ vim install_ganglia.sh

#!/bin/sh

#安装依赖  这是是我已经知道我缺少哪些依赖,所以只安装这些,具体按照你的环境来列出需要安装哪些
yum install -y apr-devel expat-devel rrdtool rrdtool-devel zlib zlib-devel

mkdir /opt/soft;cd /opt/soft
tar -xvf /home/hadoop/confuse-2.7.tar.gz
cd confuse-2.7
./configure CFLAGS=-fPIC --disable-nls
make && make install
cd /opt/soft
#安装 ganglia gmond
tar -xvf /home/hadoop/ganglia-3.6.0.tar.gz
cd ganglia-3.6.0/
./configure --prefix=/usr/local/ganglia --with-libpcre=no --enable-gexec --enable-status --sysconfdir=/etc/ganglia
make && make install
cp gmond/gmond.init /etc/init.d/gmond
cp /usr/local/ganglia/sbin/gmond /usr/sbin/
gmond --default_config>/etc/ganglia/gmond.conf
chkconfig --add gmond

```

可能会出现的错误
```
[root@hdp-node-01 ganglia-3.7.2]# gmond --default_config>/etc/ganglia/gmond.conf
gmond: error while loading shared libraries: libconfuse.so.1: cannot open shared object file: No such file or directory
[root@hdp-node-01 ganglia-3.7.2]# ln -s /usr/local/lib/libconfuse.so.1 /lib64/
[root@hdp-node-01 ganglia-3.7.2]# gmond --default_config>/etc/ganglia/gmond.conf

```


**将这个脚本复制到所有节点执行**


# 3.配置ganglia

分为服务端和客户端的配置，服务端的配置文件为gmetad.conf,客户端的配置文件为gmond.conf

首先配置监控节点上的gmetad.conf,这个文件只有监控节点上有（其他的被监控节点没有）


```
$ vi  /etc/ganglia/gmetad.conf
## 定义数据源的名字及监听地址，gmond会将收集的数据发送到数据源监听机器上的rrd数据目录中
## hadoop cluster 为自己定义
data_source "hadoop cluster" 192.168.0.101:8649

```

**接着配置 gmond.conf(所有的被监控节点上都要有)**

```
$ head -n 80 /etc/ganglia/gmond.conf

/* This configuration is as close to 2.5.x default behavior as possible
   The values closely match ./gmond/metric.h definitions in 2.5.x */
globals {
  daemonize = yes        ## 以守护进程运行
  setuid = yes           
  user = nobody          ## 运行gmond的用户
  debug_level = 0        ## 改为1会在启动时打印debug信息
  max_udp_msg_len = 1472
  mute = no              ## 哑巴，本节点将不会再广播任何自己收集到的数据到网络上
  deaf = no              ## 聋子，本节点将不再接收任何其他节点广播的数据包
  allow_extra_data = yes
  host_dmax = 86400 /*secs. Expires (removes from web interface) hosts in 1 day */
  host_tmax = 20 /*secs */
  cleanup_threshold = 300 /*secs */
  gexec = no
  # By default gmond will use reverse DNS resolution when displaying your hostname
  # Uncommeting following value will override that value.
  # override_hostname = "mywebserver.domain.com"
  # If you are not using multicast this value should be set to something other than 0.
  # Otherwise if you restart aggregator gmond you will get empty graphs. 60 seconds is reasonable
  send_metadata_interval = 0 /*secs */
 
}
 
/*
 * The cluster attributes specified will be used as part of the <CLUSTER>
 * tag that will wrap all hosts collected by this instance.
 */
cluster {
  name = "hadoop cluster"    ## 指定集群的名字
  owner = "nobody"           ## 集群的所有者
  latlong = "unspecified"
  url = "unspecified"
}
 
/* The host section describes attributes of the host, like the location */
host {
  location = "unspecified"
}
 
/* Feel free to specify as many udp_send_channels as you like.  Gmond
   used to only support having a single channel */
udp_send_channel {
  #bind_hostname = yes # Highly recommended, soon to be default.
                       # This option tells gmond to use a source address
                       # that resolves to the machine's hostname.  Without
                       # this, the metrics may appear to come from any
                       # interface and the DNS names associated with
                       # those IPs will be used to create the RRDs.
#  mcast_join = 239.2.11.71    ## 单播模式要注释调这行
  host = 192.168.0.101    ## 单播模式，指定接受数据的主机
  port = 8649             ## 监听端口
  ttl = 1
}
 
/* You can specify as many udp_recv_channels as you like as well. */
udp_recv_channel {
  #mcast_join = 239.2.11.71    ## 单播模式要注释调这行
  port = 8649
  #bind = 239.2.11.71          ## 单播模式要注释调这行
  retry_bind = true
  # Size of the UDP buffer. If you are handling lots of metrics you really
  # should bump it up to e.g. 10MB or even higher.
  # buffer = 10485760
}
 
/* You can specify as many tcp_accept_channels as you like to share
   an xml description of the state of the cluster */
tcp_accept_channel {
  port = 8649
  # If you want to gzip XML output
  gzip_output = no
}
 
/* Channel to receive sFlow datagrams */
#udp_recv_channel {
#  port = 6343
#}
 
/* Optional sFlow settings */
```

说明：
* 监控节点上要有：gmetad.conf和gmond.conf配置文件
* 被监控的节点上要有：gmond.conf文件


# 4 启动 ganglia
所有节点启动 gmond 服务
```
/etc/init.d/gmond start
```

在监控节点上启动gmetad httpd 服务
```
/etc/init.d/gmetad start
/etc/init.d/httpd start
```

# 5 在浏览器中访问hadoop1:8080/ganglia,就会出现下面的页面

![](/images/ganglia/web_cluster5.jpg)


配置完成

转自：http://www.cnblogs.com/pingjie/p/4809489.html#top
