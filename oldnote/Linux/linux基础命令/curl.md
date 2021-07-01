---
title: Linux基础命令之curl
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



```shell 
#需求:测试能否正常访问一个网站(即:求返回的状态码 200表示正常)
[root@lnmp02 sbin]# curl -I -s  --w "%{http_code}\n" www.etiantian.org -o /dev/null
200
[root@lnmp02 sbin]#

#说明
-I/--head        (HTTP/FTP/FILE) Fetch the HTTP-header only 只是显示请求头信息 (-I 是i的大写)
 
-s/--silent         Silent or quiet mode. Don’t show progress meter or error messages.  Makes Curl mute.    (不显示下载进度)
 
-o/--output <file>   Write output to <file> instead of stdout.    (

-w/--write-out <format>
All variables are specified as %{variable_name} and to output a normal % you just write them as %%. You can output a newline by using \n, a carriage return with \r and a  tab  space  with \t.  #输出响应状态的某一个字段（通过指定特定的格式）
 


# -w可以指定的字段：
http_code      The numerical response code
http_connect   The numerical code
。。。。。。。。(可以使用man curl查看)


#curl:使用密码登录
curl -XGET -u esuser:test@bd http://172.16.110.241:9200



#get带参数
curl http://test.echo.com/master?mod=1&act=2
注意：在linux下，上面的例子PHP $_GET只能获取到参数mod；因为url中有&，其他参数获取不到，在linux中，&符号会使进程系统后台运行。
有两种解决办法：
①使用转义：
curl http://test.echo.com/master?mod=1\&act=2
②用双引号把url引起来：
curl "http://test.echo.com/master?mod=1&act=2"


curl中post传递参数(使用-d传递post参数)：
①一维数组：
curl -d "name=echo&mod=1&act=1" "http://test.echo.com/test.php"
②多维数组（二维数组为例）：
curl -d "user[name]=echo&mod=1&act=1" "http://test.echo.com/test.php"
以上输出结果为：
Array(
[user] => Array(
[name] => echo
)
[mod] => 1
[act] => 1
)

```

