[TOC]



# 1.配置文件的大体结构



配置文件

* main配置段：全局配置段
  * event配置段：定义event模型工作特性
* http{}  配置段：定义HTTP协议相关的协议



配置指令：要以分号结尾，语法格式：

​	directive value1, value2,...

支持使用变量

​	内置变量

​		模块自带

​	自定义变量

​		set var_name value



主配置端的指令

​	用于调试，定位问题

​	正常运行必备的配置

​	优化性能的配置

​	事件相关配置



# 主配置段指令

## 1.正常运行的必备配置

```
 1.user USERNAME [GROUPNAME];		
 #用户指定worker进程的用户和组（这个配置也可以在文档Core functionality.user中找到对应的配置说明）

2.pid  /path/to/pid-file;	
#指定NGINX的守护进程的pid文件

3.worker_rlimit_nofile  numer;	
#worker rlimit number of file 指定所有worker进程所能够打开的最大文件句柄数

4.worker_rlimit_core size;   
#所有的worker进程所能够使用的最大核心文件大小（一般不动）

```



## 2.性能优化相关的配置

```
1. worker_processes #; 
#指定worker进程的个数，通常为物理core的数量-1或者-2(留一个core为系统所用)；

2. worker_cpu_affinity cpumask ...;
#可以将对应的worker绑定到对应的CPU的某一个核上，这样就可以提高CPU缓存的命中率，但是该CPU上还是有其他进程可以上去，所以还是有上下文切换；
cpumask(CPU掩码)：假设我们有4颗CPU，使用八位二进制表示如下：
0000 0001
0000 0010
0000 0100
0000 1000
worker_cpu_affinity 00000001 00000010  #表示只使用前2颗CPU

#For example：
worker_processes    4;
worker_cpu_affinity 0001 0010 0100 1000;
#binds each worker process to a separate CPU, while

worker_processes    2;
worker_cpu_affinity 0101 1010;
#binds the first worker process to CPU0/CPU2, and the second worker process to CPU1/CPU3. The second example is suitable for hyper-threading.

3.timer_resolution	
#时间解析度，减少  gettimeofday() 函数的系统调用
#By default, gettimeofday() is called each time a kernel event is received. With reduced resolution, gettimeofday() is only called once per specified interval.

#Example:
timer_resolution 100ms;


4.worker_priority number;
#指明调度worker进程的优先级的，通过nice值指定，nice值的范围为-20 到 20，对应的优先级为100-140，默认情况下nice=0，对应的优先级为120，优先级越小，越容易被调度
#Defines the scheduling priority for worker processes like it is done by the nice command: a negative number means higher priority. Allowed range normally varies from -20 to 20.

#Example:
worker_priority -10;
```



## 3.事件相关的配置



```
1.accept_mutes on|off;
#当一个新的请求到master的时候，master需要决定这个请求是交给哪一个worker去处理
#on表示让多个worker轮询接收新的请求，这就带来了负载均衡的效应
#off表示多个worker抢占新的连接

2.accept_mutex_delay time;
#当accept_mutes on；当一个新的连接连接来的时候，正好分配到了这个worker上，但是此时这个worker是正在处理，这个就是定义将新连接分配给这个worker时等待的时间

3.lock_file	#上面的参数相当于配置的是互斥锁，这里的参数就是互斥锁对应的锁文件路径(accept_mutex用到的锁文件路径)

4.use [epoll|rtsig|select|poll]
#定义使用的事件模型,建议让NGINX自动选择

5.worker_connecctions #;
#一个worker进程能够接受的最大并发连接数

```



## 4.用户用于调试和定位问题

```
For debug logging to work, nginx needs to be built with --with-debug, see “A debugging log”.


1.daemon {on|off};
#是否以守护进程方式运行NGINX，off表示运行在前台，这样各种日志是打印在前台的

2.master_process {on|off};
#是否以master/worker模型来运行NGINX，调试时可以设置为off

3.error_log file [level];
#错误日志

```



# 配置文件生效的方式

```

./sbin/nginx -s {reload|stop|quit|reopen}

```





# nginx作为web服务器的配置

NGINX除了有自己的核心模块之外，有像HTTP模块，mail模块等等，下面讲的是HTTP模块(`ngx_http_core_module`)



## 配置框架



![image-20181125103435813](/Users/chenyansong/Documents/note/images/nginx/http-module.png)



ngx_http_core_module模块可以作为静态的web服务器工作


```
http {
	#负载均衡使用
	upstream {
        ...
	}
	
	server {#每个server类似于httpd中的<VirtualHost>
        location URL {#类似于 httpd中的documentRoot，用于定义URL与本地文件系统的映射关系
        	root "/path/to/somedir";
            ...
        }
        location URL {
        	if ...{#条件判断的功能
                
        	}
            ...
        }
	} 
	
	server {
        ...
	}
}

#与HTTP相关的指令，仅能够放置于http, server ,location, upstream,if 上下文，但是有些指令仅能应用于这5中指令中的某些中，而非全部

当我们查看文档的时候，可以看到每一个指令的用法如下
Syntax:	root path;	#root指令
Default:	root html;
Context:	http, server, location, if in location #说明root指令只能用在这些地方

```



## 配置指令

### server

```
1.server {}
#定义一个虚拟主机

#基于端口的虚拟主机
server {
    listen 8080;
    server_name www.baidu.com;
    root "/vhosts/web1";
}
```
### listen
```
2.listen
listen address[:port]
listen port;
#指定监听的地址和端口，如下
listen 127.0.0.1:8000;
listen 127.0.0.1;
listen 8000;
listen *:8000;
listen localhost:8000;

```
### server_name
```
3.server_name

Syntax:	server_name name ...;	#支持多个主机，名称可以使用正则表达式（需要以 ~ 开头）或者通配符
Default:	server_name "";
Context:	server

多个server匹配法则：
1.先做精确匹配检查
2.左侧通配符匹配检查
3.右侧通配符匹配检查
4.正则表达式匹配检查
5.default_server

```

### root

```
4.root
#用于设置资源路径映射，用于指明请求的URL所对应的资源所在的文件系统上的根路径
Syntax:	root path;	#root指令
Default:	root html;
Context:	http, server, location, if in location #说明root指令只能用在这些地方

```

### location

```
5.location
#根据用户请求的URI来匹配location(因为一个server中可以有多个location)，当匹配到时，此请求将会被相应的location配置块所处理，例如做访问控制等功能
Syntax:	
	location [ = | ~ | ~* | ^~ ] uri { ... }
	location @name { ... }
Default:	—
Context:	server, location

######################
= 表示精确匹配
~ 正则表达式模式匹配检查，区分大小写
~* 正则表达式模式匹配检查，不分区大小写
^~ URI前半部分匹配，不支持正则表达式

优先级：= > ^~  > ~ > ~* > 不带任何符号
######################



server {
    listen 80;
    server_name www.baidu.com;
    location / {
        root "/vhosts/web1";
    }
    location /images {
        root "/vhosts/imags";
    }
    location ~* \.php$ {#正则
        fcgipass
    }
    
    
}


location = / {
    [ configuration A ]
}

location / {
    [ configuration B ]
}

location /documents/ {
    [ configuration C ]
}

location ^~ /images/ {
    [ configuration D ]
}

location ~* \.(gif|jpg|jpeg)$ {
    [ configuration E ]
}

#The “/” request will match configuration A, the “/index.html” request will match configuration B, the “/documents/document.html” request will match configuration C, the “/images/1.gif” request will match configuration D, and the “/documents/1.jpg” request will match configuration E.

```

### alias

```
6.alias
#用来实现路径映射，他和root的区别在于，alias可以拿到URI中匹配的变量，然后使用这些变量
Syntax:	alias path;
Default:	—
Context:	location

#拿到URI中的匹配
location ~ ^/users/(.+\.(?:gif|jpe?g|png))$ {
    alias /data/w3/images/$1;#使用拿到的匹配
}

#alias和root的区别：
#root是指明根的位置，对应URL中的/
#alias表示路径映射，是一个完整的路径
When location matches the last part of the directive’s value:

#www.baidu.com/images/a.jpg <--- /www/pictures/a.jpg
location /images/ {#alias就完全是一个映射，注意反斜线需要一一对应
    alias /www/pictures/;
}

it is better to use the root directive instead:
#www.baidu.com/images/a.jpg <--- /vhosts/web1/images/a.jpg
location /images/ {#root还要替换掉根
    root /vhosts/web1;
}

```

### index模块
```
7.指定默认主页面
#有一个index模块，如下
```



![image-20181125150750897](/Users/chenyansong/Documents/note/images/nginx/index_module.png)


### error_page模块

```
8.error_page code [...] [=code] URI | @name
#为常见的错误信息提供错误页面
#根据http响应状态码来指明特用的错误页面

error_page 404 /404_customed.html
[=code] : 以指定的响应吗进行响应，而不是以默认的原来的响应，默认表示以新资源的响应吗为其响应吗

#重写响应吗为200
error_page 404 =200 /404_customed.html


```


### 基于IP的访问控制(allow,deny)

```
9.基于IP的访问控制
allow
deny

Syntax:	allow address | CIDR | unix: | all;
Default:	—
Context:	http, server, location, limit_except

Syntax:	deny address | CIDR | unix: | all;
Default:	—
Context:	http, server, location, limit_except


location / {
    deny  192.168.1.1;
    allow 192.168.1.0/24;
    allow 10.1.1.0/16;
    allow 2001:0db8::/32;
    deny  all;
}
```



![image-20181125152829258](/Users/chenyansong/Documents/note/images/nginx/access_control.png)

### 基于用户的访问控制(user,passwd)

```
10.基于用户的访问控制
basic,digest

#基于用户名，密码的访问控制(ngx_http_auth_basic_module)
auth_basic	"";		#认证名称
auth_basic_user_file	#账号密码文件，可以使用htpasswd创建
	账号密码文件建议使用htpasswd来创建

#语法
Syntax:	auth_basic string | off;
Default:	auth_basic off;
Context:	http, server, location, limit_except

Syntax:	auth_basic_user_file file;
Default:	—
Context:	http, server, location, limit_except
Specifies 

#example
location / {
    auth_basic           "Only for VIP";
    auth_basic_user_file conf/htpasswd;
}

# conf/htpasswd格式如下
# comment
name1:password1
name2:password2:comment
name3:password3

#httpd中是自带一个命令的：htpasswd
#第一次使用-c
shell>htpasswd -c -m /etc/nginx/users/.htpasswd tom
new passwd:
```

![image-20181125183416046](/Users/chenyansong/Documents/note/images/nginx/auth_pwd.png)

### 配置SSL

```
11.配置SSL

#1.生成私钥
#2.生成证书签署请求
#3.CA根据2中生产的请求，生成证书

server {
    listen              443 ssl;
    keepalive_timeout   70;
	server_name 	www.test.com;
	
    ssl_protocols       TLSv1 TLSv1.1 TLSv1.2;
    ssl_ciphers         AES128-SHA:AES256-SHA:RC4-SHA:DES-CBC3-SHA:RC4-MD5;
    
    ssl_certificate     /usr/local/nginx/conf/cert.pem; #证书文件
    ssl_certificate_key /usr/local/nginx/conf/cert.key; #私钥文件 
    
    ssl_session_cache   shared:SSL:10m;
    ssl_session_timeout 10m;

    ...
}

#测试
https://www.test.com
```

### 开启stub_status 状态页面

```
12.开启stub_status 状态页面
#The ngx_http_stub_status_module module provides access to basic status information.

#This module is not built by default, it should be enabled with the --with-http_stub_status_module configuration parameter.

#In versions prior to 1.7.5, the directive syntax required an arbitrary argument, for example, “stub_status on”.


#example
location = /basic_status {
    stub_status;
    allow 172.16.0.0/16;	#这些状态数据只能我们自己能够访问
    deny all;
}

#This configuration creates a simple web page with basic status data which may look like as follows:

Active connections: 291 
server accepts handled requests
 16630948 16630948 31070465 
Reading: 6 Writing: 179 Waiting: 106 



# Active connections ：The current number of active client connections including Waiting connections.
accepts：已经接受的连接The total number of accepted client connections.

handled：已经处理过的连接The total number of handled connections. Generally, the parameter value is the same as accepts unless some resource limits have been reached (for example, the worker_connections limit).

requests：请求的数量The total number of client requests.

Reading：正在接受的请求The current number of connections where nginx is reading the request header.

Writing：The current number of connections where nginx is writing the response back to the client.

Waiting：The current number of idle client connections waiting for a request.

```

![image-20181125192013459](/Users/chenyansong/Documents/note/images/nginx/stub_status_module.png)

### rewrite(url重写)

ngx_http_rewrite_module

```
13.rewrite #url重写
Syntax:	rewrite regex replacement [flag];
Default:	—
Context:	server, location, if

An optional flag parameter can be one of:

last:stops processing the current set of ngx_http_rewrite_module directives and starts a search for a new location matching the changed URI;
此rewrite规则重写完成后，不再被后面其他的rewrite规则进行处理，而是由User Agent重新对重写后的URL再一次请求，并从头开始执行类似的过程

http://www.baidu.com/images/a/b/1.jpg --> http://www.baidu.com/imgs/a/b/1.jpg
http://www.baidu.com/imgs/a/b/1.jpg -->再次请求


break:stops processing the current set of ngx_http_rewrite_module directives as with the break directive;
一旦此rewrite规则重写完成之后，由User Agent重新对重写后的URL再一次请求，但是此时不会再规则检查(如果两条规则会造成循环的时候，此时使用break可以打破循环)

redirect:returns a temporary redirect with the 302 code; used if a replacement string does not start with “http://”, “https://”, or “$scheme”;
以302响应码(临时重定向)返回新的URL

	
permanent:returns a permanent redirect with the 301 code.
以301响应码(永久重定向)返回新的URL


#http://www.baidu.com/images/a/b/1.jpg --> /imgs/a/b/1.jpg
rewrite ^/images/(.*\.jpg)$ /imgs/$1 break;

```

### if

`ngx_http_rewrite_module` 模块中

```
Syntax:	if (condition) { ... }
Default:	—
Context:	server, location

#可以使用的条件如下：
A condition may be any of the following:

1. a variable name变量名; false if the value of a variable is an empty string or “0”;


2. comparison of a variable(比较操作符) with a string using the “=” and “!=” operators;

3. 正则表达式模式匹配matching of a variable against a regular expression using the “~” (区分大小写for case-sensitive matching) and “~*” (不区分大小写for case-insensitive matching) operators. Regular expressions can contain captures that are made available for later reuse in the $1..$9 variables. Negative operators “!~”(不匹配检查) and “!~*” are also available. If a regular expression includes the “}” or “;” characters, the whole expressions should be enclosed in single or double quotes.

4. checking of a file existence with the “-f” and “!-f” operators;测试文件是否存在

5. checking of a directory existence with the “-d” and “!-d” operators;测试目录是否存在

6. checking of a file, directory, or symbolic link existence with the “-e” and “!-e” operators;测试是否存在(包括：文件，目录，链接符号)

7. checking for an executable file with the “-x” and “!-x” operators.检查是否有执行权限


#Examples:

if ($http_user_agent ~ MSIE) { #http_user_agent客户端浏览器类型，MSIE是微软浏览器，我们可以根据客户端浏览器的不同，返回不同的站点(web,mobile)
    rewrite ^(.*)$ /msie/$1 break;
}

if ($http_cookie ~* "id=([^;]+)(?:;|$)") {
    set $id $1;
}

if ($request_method = POST) {
    return 405;
}

if ($slow) {
    limit_rate 10k;
}

if ($invalid_referer) {
    return 403;
}

#防盗链
location ~* \.(jpg|gif|jpeg|png)$ {
    valid_referer none blocked www.test.com;
    #一个URL是有一个引用的，我们可以指定来自哪些站点的引用是允许的
    
    if ($invalid_referer) {
    	rewrite ^/ http://www.test.com/403.html;
    }
}

```



### valid_referers校验链接引用

referer是指请求从哪里来的
`ngx_http_referer_module`

```
Syntax:	valid_referers none | blocked | server_names | string ...;
Default:	—
Context:	server, location

#Example 
valid_referers none blocked server_names
               *.example.com example.* www.example.org/galleries/
               ~\.google\.;

if ($invalid_referer) {
    return 403;
}

#如果引用是在valid_referers中指定的，那么$invalid_referer为空，否则为“1”

Parameters can be as follows:

none：the “Referer” field is missing in the request header;

blocked：the “Referer” field is present in the request header, but its value has been deleted by a firewall or proxy server; such values are strings that do not start with “http://” or “https://”;

server_names：the “Referer” request header field contains one of the server names;

arbitrary string：defines a server name and an optional URI prefix. A server name can have an “*” at the beginning or end. During the checking, the server’s port in the “Referer” field is ignored;

regular expression ：the first symbol should be a “~”. It should be noted that an expression will be matched against the text starting after the “http://” or “https://”.
```



### 定制访问日志格式

`ngx_http_log_module`

```
log_format main '$remote_addr - $remote_user....';
access_log logs/access.log main;

```



### 网络连接相关的配置

`ngx_http_core_module`

```
1.keepalive_timeout #;
#长连接的超时时长，默认75s;

2.keepalive_requests #;
#在一个长连接上，能够允许请求的最大资源数

3.keepalive_disable [msie6|safari|none];
#为指定类型的User Agent禁用长连接(msie6为IE)

4.tcp_nodelay on|off;
#是否对长连接使用tcp_nodelay

5.client_header_timeout
#读取HTTP请求报文首部的超时时长

6.client_body_timeout
#读取http请求报文body部分的超时时长

#7.send_timeout #;
#发送响应报文的超时时长

```



### fastcgi的相关配置

