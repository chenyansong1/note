[nginx]



# nginx报错问题

## nginx 的nginx segmentation fault (core dumped)

> 有一次是nginx编译时使用的openssl和实际使用的openssl版本不一致

解决方式

```shell
#查看nginx编译使用的openssl的版本号
[root@bdsoc nginx]# ./sbin/nginx -V
nginx version: nginx/1.14.1
built by gcc 4.4.7 20120313 (Red Hat 4.4.7-18) (GCC) 
built with OpenSSL 1.0.1e-fips 11 Feb 2013
TLS SNI support enabled
configure arguments: --prefix=/usr/local/nginx --with-http_ssl_module --add-module=/opt/ModSecurity-nginx_refactoring/nginx/modsecurity/



#查看openssl版本号
openssl version

#在线升级openssl
yum -y update openssl

#或者重新编译nginx，指定openssl的版本号
     
```





