---
title: Linux基础命令之wget
categories: Linux   
toc: true  
tags: [Linux基础命令]
---



# 语法
```
wget [option]... [URL]...

       -q
       --quiet
           Turn off Wget’s output.

       -O file
       --output-document=file

       --spider
           When invoked with this option, Wget will behave as a Web spider, which means that it will not download the
           pages, just check that they are there.  For example, you can use Wget to check your bookmarks:
 
                   wget --spider --force-html -i bookmarks.html
 
           This feature needs much more work for Wget to get close to the functionality of real web spiders.

       -T seconds
       --timeout=seconds

 
       --limit-rate=amount
           Limit the download speed to amount bytes per second.  Amount may be expressed in bytes, kilobytes with the
           k suffix, or megabytes with the m suffix.  For example, --limit-rate=20k will limit the retrieval rate to
           20KB/s.  This is useful when, for whatever reason, you don’t want Wget to consume the entire available
           bandwidth.

       --tries=number
           Set number of retries to number.
 

```

# 实例

```
# -O 下载到指定路径并改名
[root@lamp01 chenyansong]wget -O ./test_wget www.baidu.com
--2017-02-13 17:25:24--  http://www.baidu.com/
正在解析主机 www.baidu.com... 119.75.217.109, 119.75.218.70
正在连接 www.baidu.com|119.75.217.109|:80... 已连接。
已发出 HTTP 请求，正在等待回应... 200 OK
长度：2381 (2.3K) [text/html]
正在保存至: “./test_wget”
 
100%[==========================================================================================>] 2,381       --.-K/s   in 0.004s 
 
2017-02-13 17:25:34 (640 KB/s) - 已保存 “./test_wget” [2381/2381])


# 不显示下载的详情
wget -q -O ./test_wget2 www.baidu.com



#只是检查文件是否存在不进行下载
[root@lamp01 chenyansong]wget --spider http://www.cnblogs.com/peida/archive/2013/03/18/2965d369.html
开启 Spider 模式。检查是否存在远程文件。
--2017-02-13 17:47:15--  http://www.cnblogs.com/peida/archive/2013/03/18/2965d369.html
正在解析主机 www.cnblogs.com... 42.121.252.58
正在连接 www.cnblogs.com|42.121.252.58|:80... 已连接。
已发出 HTTP 请求，正在等待回应... 404 Not Found
远程文件不存在 -- 链接断开！！！
 
[root@lamp01 chenyansong]echo $?     #使用echo $?去检查上一条命令的执行结果,也就是说文件是否存在
8
[root@lamp01 chenyansong]wget --spider http://www.cnblogs.com/peida/archive/2013/03/18/2965369.html
开启 Spider 模式。检查是否存在远程文件。
--2017-02-13 17:47:39--  http://www.cnblogs.com/peida/archive/2013/03/18/2965369.html
正在解析主机 www.cnblogs.com... 42.121.252.58
正在连接 www.cnblogs.com|42.121.252.58|:80... 已连接。
已发出 HTTP 请求，正在等待回应... 200 OK
长度：78156 (76K) [text/html]
存在远程文件且该文件可能含有更深层的链接，
但不能进行递归操作 -- 无法取回。
 
[root@lamp01 chenyansong]


#使用wget –tries增加重试次数
wget --tries=40 URL
```

[详细参见](http://www.cnblogs.com/peida/archive/2013/03/18/2965369.html)