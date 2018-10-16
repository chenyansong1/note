[TOC]



# httpd的由来



httpd早期是由NCSA开发的，但是后来这个项目解散，成员被散落在各个公司，但是这些开发人员非常喜欢这个软件，于是自发的通过互联网协作，继续为这个软件新增许多功能，包括打补丁，新增功能，后来人们就戏称这个httpd为充满了补丁的服务，A Patchy Server ,简写为apache



ASF:Apache Software Foundation:Apache软件基金会

​	web:httpd 很多时候，我们以Apache来称呼httpd，那是因为早期httpd就是以apache server，因此早期我们来说Apache的时候，就是说的是Apache的web项目httpd，但是到今天为止Apache已经有了很多的开源软件，httpd只是其中的一个而已，所以Apache的很多顶级项目都可以这样来访问:httpd.apache.org/,tomcat.apache.org， flume.apache.org，hadoop.apache.org 等等



# httpd安装配置



* 事先创建进程
* 按需维持适当的进程
* 模块化设计，核心比较小，各种功能都可以模块的添加，支持运行配置，支持单独编译模块
* 支持多种方式的虚拟主机配置
* 支持https协议（mod_ssl)
* 支持用户认证
* 支持基于IP或者主机名的ACL（访问控制）
* 支持每目录的访问控制（ACL）
* 支持URL重写（重要）





## 虚拟主机

1台物理服务器，web程序也只有一个，却可以服务多个不同的站点



* 基于IP的虚拟主机
* 基于端口的虚拟主机
* 基于域名的虚拟主机（常用）



## 目录说明（配置文件和目录）



```
/usr/sbin/httpd (MPM:prefork) 后台会启动多个进程，除了一个进程的用户时root之外，其他的进程的处理用户都是apache用户，apache组
	httpd: root，root	#(master process)主导进程，用来创建进程，销毁进程
	httpd: apache, apache	#(worker process)

/etc/rc.d/init.d/httpd	#配置文件

Port:(80/tcp), ssl:(443/tcp) #开启的端口

/etc/httpd #工作的根目录，进程运行的根目录，相当于程序安装的目录

/etc/httpd/conf 	#配置文件目录
	主配置文件：httpd.conf	#主配置文件
	/etc/httpd/conf.d/*.conf	也是上面主配置文件的组成部分

/etc/httpd/modules: #模块目录

/etc/httpd/logs	--> /var/log/httpd	#日志目录
	日志文件有两类：访问日志:access_log ，错误日志：err_log
	
/var/www/
	html 静态页面所在的目录
	cgi-bin	动态内容的目录，cgi是一种能够让web服务器和应用程序通信的一种机制，让应用程序启动启动额外的程序处理动态内容
	cgi:Common Gateway Interafce 通用网关接口，让web服务器和应用程序服务器打交道的一种协议
	
	Client-->httpd(index.cgi)-->Spawn Process(index.cgi)-->httpd-->Client
	首先由客户端发起httpd请求
	httpd的服务在内部调用一个脚本，如index.cgi
	cgi启动一个和脚本对应的应用程序服务，该应用程序执行该脚本(index.cgi)
	应用程序将脚本的执行结果返回给httpd,最终结果由httpd返回给客户端
	
	
	对于动态的脚本而言，每来一个请求，都会开启一个新的进程，这样如果请求的动态内容的客户端很多的话，那么后台就会有很多的进程，解决的方式是在后台启动一个进程管理所有的需要新开的处理动态内容的进程，由这个进程负责维护所有的动态进程，这就叫做fastcgi(适用于PHP)
```

![image-20181014141836894](/Users/chenyansong/Documents/note/images/http/fastcgi.png)



应用程序服务器进程和web服务器进程通过某个socket进程通信，这样应用程序服务进程和web服务器进程可以放在不同的服务器上，这样就可以实现分离，这样就可以实现这样的场景，一个服务器实现静态内容的访问，另一个服务器实现的是动态内容的应用程序服务；当一个用户请求的是动态内容的时候，可以通过本地的网络来发送给另外一台应用程序主机，这也就是前后端分离的结果



![image-20181014144139576](/Users/chenyansong/Documents/note/images/http/fastcgi2.png)



## 安装

```
yum -y install httpd

rpm -ql httpd

httpd -t #检查配置文件的语法
```

![image-20181014175755949](/Users/chenyansong/Documents/note/images/http/httpd.png)



![image-20181014180042314](/Users/chenyansong/Documents/note/images/http/httpd2.png)



会启动一个root用户的进程，然后启动多个Apache的用户进程

![image-20181014180217355](/Users/chenyansong/Documents/note/images/http/httpd3.png)



## 配置文件说明



httpd配置文件

```
vim /etc/httpd/httd.conf

directive			vlaue
指令(不区分大小写)	   值(分区大小写)

#修改配置文件之后，检查语法是否正常，类似于nginx -t 
httpd -t 


httpd -l #查看已经存在的模块

httpd -D #查看所有的支持的模块
```



指令的查询

Httpd.apache.org下面进入到Documents页面，找到“指令快速参考”

![image-20181014183412142](/Users/chenyansong/Documents/note/images/http/httpd-config1.png)

选择指令的其实字母，能够快速查询

![image-20181014183514433](/Users/chenyansong/Documents/note/images/http/httpd-config2.png)



```
#httpd的根目录
ServerRoot	"/etc/httpd"

#pid的保存目录，相对于ServerRoot
PidFile	run/httpd.pid

#tcp的timeout
Timeout 120


#是否使用长链接
KeepAlive Off

#
MaxKeepAliveRequests	100
KeepAliveTimeout	15

#prefork模式
<IfModule prefork.c>
StartServers 8	#启动的时候启动多少个空闲进程(一个root的用户进程；7个apache的用户进程)
MinSpareServers  5	#最小空闲进程
MaxSpareServers		20	#最大空闲进程
ServerLimit	256		#MaxClients的最大值
MaxClients	256		#
MaxRequestsPerChild	4000	#每一个进程的最大响应的请求次数
</IfModule>


#worker模式
<IfModule worker.c>
StartServers 2	#默认启动多少个进程
MaxClients	150	#MaxClients的最大值
MinSpareThreads	25	#最小空闲线程(所有进程加起来)
MaxSpareThreads	75	#最大空闲线程(所有进程加起来)
ThreadsPerChild	25	#每个进程可以生成多少个线程
MaxRequestsPerChild	0	#每一个进程的最多响应的请求次数，0表示不做限定
</IfModule>

Listen	80	#监听的端口
Listen	8080

#LoadModule	foo_module(模块名称)  modules/mod_auth_basic.so(模块路径)
LoadModule	auth_basic_module  modules/mod_auth_basic.so

#包含的配置文件的目录
Include conf.d/*.conf

#worker进程需要使用普通用户运行(这里指定是谁)
User	apache	
Group	apache

#服务管理员
ServerAdmin	root@localhost

ServerName www.baidu.com #当前服务器的IP地址对应的主机名作为此处的值

UseCanonicalName  Off

#文档根目录
DocumentRoot  "/var/www/html"

#定义这个路径的访问规则
<Directory "/var/www/html">
	#None 任何选项都不支持，推荐使用
	#Indexes 列出所有的根写的文件，不建议使用
	#FollowSymLinks 跟随符号链接(html下文件通过符号链接指向了另外一个文件)，不建议使用
	#Includes 允许执行服务器端包含(SSI)
	#ExecCGI 允许执行CGI脚本
	#MultiView 内容协商
	#All 支持以上所有的选项
	Options	Indexes FollowSymLinks

	#允许覆盖(Acl)
	#AuthConfig #需要基于一个账号密码的认证
	AllowOverride None	#none说明使用order,allow,deny的方式进行配置
	
	########认证###########
	AllowOverride AuthConfig
	AuthType Basic
	AuthName "Restricted Stie..."
	
	#定义用户访问
	AuthUserFile "/etc/httpd/conf/htpasswd"
	Require	user hadoop #只允许hadoop用户登录，需要建立该用户
	Require valid-user	#需要用户登录验证
	
	#创建用户
	#htpasswd -c -m /etc/httpd/conf/htpasswd hadoop
	#htpasswd  -m /etc/httpd/conf/htpasswd hadoop #只有第一次-c去创建文件，后面都不用创建文件
	#htpasswd -D username #删除用户
	
	#定义组访问
	AuthGroupFile "/etc/httpd/conf/htgroup"
	Require group myusers
	
	vim /etc/httpd/conf/htgroup
	myusers: hadoop tom #这些用户需要事先已经创建了密码文件了htpasswd创建
	###################
	
	#用于定义基于主机的访问功能的，IP，网络地址或主机定义访问控制机制
	Order allow,deny	#先allow 后deny
	Allow from all
	
	#仅允许192.168.0.0/24 这个网段的IP访问
	Order allow,deny	#这里的次数很重要，不能先deny,后allow,
	Allow from 192.168.0.0/24  #Allow from 192.168.1.1 192.168.1.2 空格隔开
	
	#地址的表示方式
	IP	
	network/netmask 192.168.0.0/24 
	hostname : www.a.com
	domainname	a.com
	部分IP： 172.16 == 172.16.0.0/16
	
	
	#仅不允许192.168.0.0/24 这个网段的IP访问
	Order deny,allow	#这里的次数很重要，不能先allow,后deny
	Deny from 192.168.0.0/24
	
	
</Directory>

#不指定访问的路劲的时候，默认的index页面
DirectoryIndex index.html index.html.var

#以.ht开头的文件都禁止访问
<Files ~"^\.ht">
	Order allow,deny
	Deny from all
<Files>

#
TypesConfig /etc/mime.types

DefaultType text/plain	#默认类型为：文本

HostnameLookups Off	#日志中IP地址是否反解为主机名

ErrorLog logs/error_log
LogLevel warn
#日志格式，看官网 指令/LogFormat
LogFormat "%h %l %u %t" combined
LogFormat "%h %l %u %t" common
LogFormat "%h %l %u %t" referer


CustomLog logs/access_log combined

#Alias /luntan/ "/bbs/forum/" #后面两个斜线于要对应
Alias /luntan "/bbs/forum"
#浏览器访问http:172.16.1.1/lantan/a.jpg #访问的是 /bbs/forum/a.jpg
```





认证在浏览器中显示

![](/Users/chenyansong/Documents/note/images/http/image-20181015223048530.png)



日志格式说明

![image-20181015230433339](/Users/chenyansong/Documents/note/images/http/image-20181015230433339.png)



MPM:multi Path Modules(多处理模块)，定义多用户请求的时候，响应的模型，常用的方式有：

* mpm_winnt, 
* prefork(预先生成进程，一个请求用一个进程响应)
* worker(基于线程来工作：一个请求用一个线程响应；启动多个进程，每个进程生成多个线程，每一个线程响应一个请求)
* event(基于事件驱动，一个进程处理多个用户请求，httpd2.4之后支持，nginx默认使用这种)



```
#这个是httpd服务的配置文件，可以指定启动的是以prefork，worker,event的方式工作
vim /etc/sysconfig/httpd 

#可以改成对应的模型
/usr/sbin/httpd.worker
/usr/sbin/httpd.event
```



# 虚拟主机



* 基于IP（IP是稀缺自然）

  IP1:80

  IP2:80

  

* 基于端口(网页间跳转使用；或者内网使用)

  IP:80

  IP:8080

* 基于域名(一般使用这个)

  IP:80

  ​	主机名不同

  ​		www.a.org

  ​		www.b.net



得先取消中心主机，注释DocumentRoot 即可

```
vim /etc/httpd/httpd.conf
# DocumentRoot "/var/www/html"

vim /etc/httpd/conf.d/virtual.conf
#基于IP的
<VirtualHost 172.16.100.1:80>
	ServerName www.a.com
	DocumentRoot "/www/a.com"
</VirtualHost>

<VirtualHost 172.16.100.2:80>
	ServerName www.b.com
	DocumentRoot "/www/b.com"
</VirtualHost>

##创建目录
mkdir -p /www/{a,b}.com


#基于端口的,此时需要在主配置文件中，添加监听8080的端口
vim /etc/httpd/httpd.conf
Listen 80
Listen 8080

vim /etc/httpd/conf.d/virtual.conf
<VirtualHost 172.16.100.1:80>
	ServerName www.a.com
	DocumentRoot "/www/a.com"
</VirtualHost>

<VirtualHost 172.16.100.1:8080>
	ServerName www.b.com
	DocumentRoot "/www/b.com"
</VirtualHost>


#基于主机名的
vim /etc/httpd/conf.d/virtual.conf

NameVirtualHost 172.16.100.2:80

<VirtualHost 172.16.100.1:80>
	ServerName www.a.com
	DocumentRoot "/www/a.com"
	#基于密码的登录验证
	<Directory "www/b.com">
		Options none
		AllowOverride authconfig
		AuthType basic
		AuthName "Restrict area..."
		AuthUserFile "/etc/httpd/.htpasswd"	#需要通过htpasswd 创建认证文件
		Require valid-users
	</Directory>
</VirtualHost>

<VirtualHost 172.16.100.1:80>
	ServerName www.b.com
	DocumentRoot "/www/b.com"
	#拒绝 172.16.100.177 主机访问
	<Directory "www/b.com">
		Options none
		AllowOverride none
		Order deny,allow
		Deny from 172.16.100.177
	</Directory>
</VirtualHost>

vim /etc/hosts
172.16.100.1	www.a.com
172.16.100.1	www.b.com


#默认虚拟主机(当访问一个不存在的主机的时候，返回的是默认的主机)
<VirtualHost _default_:80>
	DocumentRoot "/www/default"
</VirtualHost>
```

htpasswd创建认证文件

```
#第一次创建需要 -c 去创建文件
htpasswd -c -m /etc/httpd/.htpasswd tom

htpasswd  -m /etc/httpd/.htpasswd jerry


```



指定日志格式和主机别名

![](/Users/chenyansong/Documents/note/images/http/vhost.png)



## 官网帮助



![image-20181016212854608](/Users/chenyansong/Documents/note/images/http/vhost-help.png)



# 基于openssl的httpd



```
yum install mod_ssl

rpm -ql mod_ssl
```

![image-20181016222524597](/Users/chenyansong/Documents/note/images/http/ssl.png)



在/etc/httpd/conf.d/ssl.conf下这个文件



### CA

```
#1.选择一台主机，生成私钥
cd /etc/pki/CA
(umask 077; openssl genrsa -out private/cakey.pem 2048)

#2.根据私钥生成CA的自签证书
vim ../tls/openssl.cnf #编辑这个配置，指定默认的城市，省份，组织，部门等
countryName_default = CN
stateOrProvinceName_default	= Henan
localityName_default = zhengzhou
0.organizationName_default = testOrg
organizationalUnitName_default = Tech

#生成自签(-x509是表示自签)
openssl req -new -x509 -key private/cakey.pem -out cacert.pem -days 3655
#此时会让你指定主机名，Email

vim ../tls/openssl.cnf
dir 	 = /etc/pki/CA
certs 	 = $dir/certs
crl_dir  = $dir/crl
database = $dir/index.txt

#创建需要的文件
mkdir certs crl newcerts
touch index.txt
echo 01> serial

#3.生成某个应用的证书,在应用所在的主机上
cd /etc/httpd/
mkdir ssl
cd ssl

#生成秘钥
(umask 077; openssl genrsa 1024 > httpd.key)

#生成证书签署请求文件
openssl req -new -key httpd.key -out httpd.csr
CN
Henan
zhengzhou
testOrg
Tech
hello.test.com #这个主机一定要和实际访问的主机一致


#4.将生产的证书请求文件 发送 到CA那台主机上
scp httpd.csr host-ca:/tmp


#5.在CA这台主机上签署请求
openssl ca -in /tmp/httpd.csr -out /tmp/httpd.crt -days 3650
y
y

#查看CA已经签署的证书
cat /etc/pki/Ca/index.txt

#6.将CA签署的证书拷贝到应用服务器
scp host-ca:/tmp/httpds.crt ./

#7.配置应用服务器 使用证书
cd /etc/httpd/conf.d/
cp ssl.conf ssl.conf.bak

vim ssl.conf
<VirtualHost 172.16.100.1:443>
	ServerName hello.test.com
	DocumentRoot "/www/test.com"
	ErrorLog logs/ssl_error_log
	TransferLog logs/ssl_access_log
	SSLEngine on #on表示启用ssl功能
	SSLProtocol all -SSLv2  #从所有中去掉 SSLv2 剩下 SSLv1，tsl
	SSLCertificateFile /etc/httpd/ssl/httpd.crt  #证书文件的位置
	SSLCertificateKeyFile /etc/httpd/ssl/httpd.key #指定私钥路径
</VirtualHost>


#8.重启服务
httpd -t #检查配置文件
service httpd restart

#9.浏览器访问
https://hello.test.com
```

![image-20181016225052034](/Users/chenyansong/Documents/note/images/http/ca.png)

![image-20181016225116738](/Users/chenyansong/Documents/note/images/http/ca02.png)



浏览器访问的时候，会提示我们



1. 将CA的自己的证书（/etc/pki/CA/cacert.pem ），拿到我们的Windows上，改名为cacert.crt
2. 双击证书，安装，选择“受信任的根证书颁发机构”
3. 浏览器访问：https://hello.test.com



# 编译安装httpd

