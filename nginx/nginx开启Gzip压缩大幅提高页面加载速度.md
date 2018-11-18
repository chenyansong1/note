





1、Vim打开Nginx配置文件

>  vim /usr/local/nginx/conf/nginx.conf

2、找到如下一段，进行修改

```
gzip on;
gzip_min_length 1k;
gzip_buffers 4 16k;
#gzip_http_version 1.0;
gzip_comp_level 2;
gzip_types text/plain application/x-javascript text/css application/xml text/javascript application/x-httpd-php image/jpeg image/gif image/png;
gzip_vary off;
gzip_disable "MSIE [1-6]\.";

```



3、解释一下

第1行：开启Gzip

第2行：不压缩临界值，大于1K的才压缩，一般不用改

第3行：buffer，就是，嗯，算了不解释了，不用改

第4行：用了反向代理的话，末端通信是HTTP/1.0，有需求的应该也不用看我这科普文了；有这句的话注释了就行了，默认是HTTP/1.1

第5行：压缩级别，1-10，数字越大压缩的越好，时间也越长，看心情随便改吧

第6行：进行压缩的文件类型，缺啥补啥就行了，JavaScript有两种写法，最好都写上吧，总有人抱怨js文件没有压缩，其实多写一种格式就行了

第7行：跟Squid等缓存服务有关，on的话会在Header里增加"Vary: Accept-Encoding"，我不需要这玩意，自己对照情况看着办吧

第8行：IE6对Gzip不怎么友好，不给它Gzip了

 

4、:wq保存退出，重新加载Nginx

> /usr/local/nginx/sbin/nginx -s reload

 

5、用curl测试Gzip是否成功开启

curl -I -H "Accept-Encoding: gzip, deflate" "http://www.slyar.com/blog/"

HTTP/1.1 200 OK
Server: nginx/1.0.15
Date: Sun, 26 Aug 2012 18:13:09 GMT
Content-Type: text/html; charset=UTF-8
Connection: keep-alive
X-Powered-By: PHP/5.2.17p1
X-Pingback: http://www.slyar.com/blog/xmlrpc.php
Content-Encoding: gzip

页面成功压缩

 

curl -I -H "Accept-Encoding: gzip, deflate" "http://www.slyar.com/blog/wp-content/plugins/photonic/include/css/photonic.css"

HTTP/1.1 200 OK
Server: nginx/1.0.15
Date: Sun, 26 Aug 2012 18:21:25 GMT
Content-Type: text/css
Last-Modified: Sun, 26 Aug 2012 15:17:07 GMT
Connection: keep-alive
Expires: Mon, 27 Aug 2012 06:21:25 GMT
Cache-Control: max-age=43200
Content-Encoding: gzip

css文件成功压缩

curl -I -H "Accept-Encoding: gzip, deflate" "http://www.slyar.com/blog/wp-includes/js/jquery/jquery.js"

HTTP/1.1 200 OK
Server: nginx/1.0.15
Date: Sun, 26 Aug 2012 18:21:38 GMT
Content-Type: application/x-javascript
Last-Modified: Thu, 12 Jul 2012 17:42:45 GMT
Connection: keep-alive
Expires: Mon, 27 Aug 2012 06:21:38 GMT
Cache-Control: max-age=43200
Content-Encoding: gzip

js文件成功压缩

curl -I -H "Accept-Encoding: gzip, deflate" "http://www.slyar.com/blog/wp-content/uploads/2012/08/2012-08-23_203542.png"

HTTP/1.1 200 OK
Server: nginx/1.0.15
Date: Sun, 26 Aug 2012 18:22:45 GMT
Content-Type: image/png
Last-Modified: Thu, 23 Aug 2012 13:50:53 GMT
Connection: keep-alive
Expires: Tue, 25 Sep 2012 18:22:45 GMT
Cache-Control: max-age=2592000
Content-Encoding: gzip

图片成功压缩

curl -I -H "Accept-Encoding: gzip, deflate" "http://www.slyar.com/blog/wp-content/plugins/wp-multicollinks/wp-multicollinks.css"

HTTP/1.1 200 OK
Server: nginx/1.0.15
Date: Sun, 26 Aug 2012 18:23:27 GMT
Content-Type: text/css
Content-Length: 180
Last-Modified: Sat, 02 May 2009 08:46:15 GMT
Connection: keep-alive
Expires: Mon, 27 Aug 2012 06:23:27 GMT
Cache-Control: max-age=43200
Accept-Ranges: bytes

最后来个不到1K的文件，由于我的阈值是1K，所以没压缩


参见：https://www.cnblogs.com/mitang/p/4477220.html


