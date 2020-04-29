---
title: nginx rewrite 重写
categories: nginx   
toc: true  
tags: [nginx]
---


官方：http://nginx.org/en/docs/http/ngx_http_rewrite_module.html
参考：http://seanlook.com/2015/05/17/nginx-location-rewrite/#more

# 1.介绍
&emsp;rewrite功能就是，使用nginx提供的全局变量或自己设置的变量，结合正则表达式和标志位实现url重写以及重定向。rewrite只能放在server{},location{},if{}中，并且只能对域名后边的除去传递的参数外的字符串起作用，例如http://seanlook.com/a/we/index.php?id=1&u=str 只对/a/we/index.php重写。需要PCRE软件的支持，即通过Perl兼容正则表达式语法进行规制匹配，实现URL的重写

# 2.语法
```
#语法：
rewrite regex replacement [flag]  #将匹配（regex）到的部分替换（replacement ）
#默认：none
#应用位置：server, location, if

```

# 3.flag标志位
* last :本条规则匹配完成后,继续向下匹配新的locationURI规则
* break : 本条规则匹配完成即终止,不再匹配后面的任何规则
* redirect : 返回302临时重定向，地址栏会显示跳转后的地址
* permanent : 返回301永久重定向，地址栏会显示跳转后的地址

&emsp;因为301和302不能简单的只返回状态码，还必须有重定向的URL，这就是return指令无法返回301,302的原因了。这里 last 和 break 区别有点难以理解：
* last一般写在server和if中，而break一般使用在location中
* last不终止重写后的url匹配，即新的url会再从server走一遍匹配流程，而break终止重写后的匹配
* break和last都能组织继续执行后面的rewrite指令


# 4.if中的rewrite

## 4.1.语法
```
#语法
if(condition){...}，对给定的条件condition进行判断

```

## 4.2.说明

```
#当表达式只是一个变量时，如果值为空或任何以0开头的字符串都会当做false
#直接比较变量和内容时，使用=或!=
~正则表达式匹配，~*不区分大小写的匹配，!~区分大小写的不匹配
-f和!-f用来判断是否存在文件
-d和!-d用来判断是否存在目录
-e和!-e用来判断是否存在文件或目录
-x和!-x用来判断文件是否可执行
```

## 4.3.实例
```
if ($http_user_agent ~ MSIE) {
    rewrite ^(.*)$ /msie/$1 break;
} //如果UA包含"MSIE"，rewrite请求到/msid/目录下
 
if ($http_cookie ~* "id=([^;]+)(?:;|$)") {
    set $id $1;
} //如果cookie匹配正则，设置变量$id等于正则引用部分
 
if ($request_method = POST) {
    return 405;
} //如果提交方法为POST，则返回状态405（Method not allowed）。return不能返回301,302
 
if ($slow) {
    limit_rate 10k;
} //限速，$slow可以通过 set 指令设置
 
if (!-f $request_filename){
    break;
    proxy_pass  http://127.0.0.1;
} //如果请求的文件名不存在，则反向代理到localhost 。这里的break也是停止rewrite检查
 
if ($args ~ post=140){
    rewrite ^ http://example.com/ permanent;
} //如果query string中包含"post=140"，永久重定向到example.com
 
location ~* \.(gif|jpg|png|swf|flv)$ {
    valid_referers none blocked www.jefflei.com www.leizhenfang.com;
    if ($invalid_referer) {
        return 404;
    } //防盗链
}
```

## 4.4.下面是可以用作if判断的全局变量
```
$args ： #这个变量等于请求行中的参数，同$query_string
$content_length ： 请求头中的Content-length字段。
$content_type ： 请求头中的Content-Type字段。
$document_root ： 当前请求在root指令中指定的值。
$host ： 请求主机头字段，否则为服务器名称。
$http_user_agent ： 客户端agent信息
$http_cookie ： 客户端cookie信息
$limit_rate ： 这个变量可以限制连接速率。
$request_method ： 客户端请求的动作，通常为GET或POST。
$remote_addr ： 客户端的IP地址。
$remote_port ： 客户端的端口。
$remote_user ： 已经经过Auth Basic Module验证的用户名。
$request_filename ： 当前请求的文件路径，由root或alias指令与URI请求生成。
$scheme ： HTTP方法（如http，https）。
$server_protocol ： 请求使用的协议，通常是HTTP/1.0或HTTP/1.1。
$server_addr ： 服务器地址，在完成一次系统调用后可以确定这个值。
$server_name ： 服务器名称。
$server_port ： 请求到达服务器的端口号。
$request_uri ： 包含请求参数的原始URI，不包含主机名，如：”/foo/bar.php?arg=baz”。
$uri ： 不带请求参数的当前URI，$uri不包含主机名，如”/foo/bar.html”。
$document_uri ： 与$uri相同。
 
例：http://localhost:88/test1/test2/test.php
$host：localhost
$server_port：88
$request_uri：http://localhost:88/test1/test2/test.php
$document_uri：/test1/test2/test.php
$document_root：/var/www/html
$request_filename：/var/www/html/test1/test2/test.php

```

# 5.常用正则
```
. ： 匹配除换行符以外的任意字符
? ： 重复0次或1次
+ ： 重复1次或更多次
* ： 重复0次或更多次
\d ：匹配数字
^ ： 匹配字符串的开始
$ ： 匹配字符串的介绍
{n} ： 重复n次
{n,} ： 重复n次或更多次
[c] ： 匹配单个字符c
[a-z] ： 匹配a-z小写字母的任意一个
小括号()之间匹配的内容，可以在后面通过$1来引用，$2表示的是前面第二个()里的内容。正则里面容易让人困惑的是\转义特殊字符。

```

# 6.rewrite实例
```
rewrite ^/images/(.*)_(\d+)x(\d+)\.(png|jpg|gif)$ /resizer/$1.$4?width=$2&height=$3? last;
#对形如/images/bla_500x400.jpg的文件请求，重写到/resizer/bla.jpg?width=500&height=400地址，并会继续尝试匹配location。

```
# 7.应用场景

1. 可以调整用户浏览的URL，看起来更规范，合乎开发及产品人员的需求
2. 为了让搜索引擎收录网站内容及用户体验更好，企业会将动态URL地址伪装成静态地址提供服务
3. 网站换新域名后，让旧的域名的访问跳转到新的域名上
4. 根据特殊变量、目录、客户端的信息进行URL跳转等。

