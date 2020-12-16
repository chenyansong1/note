[toc]

转自：https://www.cnblogs.com/woshimrf/p/nginx-proxy-rewrite-url.html



使用Nginx做代理的时候，可以简单的直接把请求原封不动的转发给下一个服务。

比如，访问abc.com/appv2/a/b.html, 要求转发到localhost:8088/appv2/a/b.html

简单配置如下：



```shell
upstream one {
  server localhost:8088 weight=5;
}

server {
    listen              80;
    server_name         abc.com;
    access_log  "pipe:rollback /data/log/nginx/access.log interval=1d baknum=7 maxsize=1G"  main;

    location / {
        proxy_set_header Host $host;
        proxy_set_header  X-Real-IP        $remote_addr;
        proxy_set_header  X-Forwarded-For  $proxy_add_x_forwarded_for;
        proxy_set_header X-NginX-Proxy true;

        proxy_pass http://one;
    }

}
```

即，设置`proxy_pass`即可。请求只会替换域名。

但很多时候，我们需要根据url的前缀转发到不同的服务。

比如

abc.com/user/profile.html转发到 **用户服务**localhost:8089/profile.html

abc.com/order/details.html转发到 **订单服务** localhost:8090/details.html

即，url的前缀对下游的服务是不需要的，除非下游服务添加context-path, 但很多时候我们并不喜欢加这个。如果Nginx转发的时候，把这个前缀去掉就好了。

## 一个种方案是proxy_pass后面加根路径`/`.



```shell
server {
    listen              80;
    server_name         abc.com;
    access_log  "pipe:rollback /data/log/nginx/access.log interval=1d baknum=7 maxsize=1G"  main;

    location ^~/user/ {
        proxy_set_header Host $host;
        proxy_set_header  X-Real-IP        $remote_addr;
        proxy_set_header  X-Forwarded-For  $proxy_add_x_forwarded_for;
        proxy_set_header X-NginX-Proxy true;

        proxy_pass http://127.0.0.1:8080/BDSOC/user/;
    }

    location ^~/order/ {
        proxy_set_header Host $host;
        proxy_set_header  X-Real-IP        $remote_addr;
        proxy_set_header  X-Forwarded-For  $proxy_add_x_forwarded_for;
        proxy_set_header X-NginX-Proxy true;

        proxy_pass http://order/;
    }

}
```

`^~/user/`表示匹配前缀是`user`的请求，proxy_pass的结尾有`/`， 则会把`/user/*`后面的路径直接拼接到后面，即移除user.

## 另一种方案是使用`rewrite`



```shell
upstream user {
  server localhost:8089 weight=5;
}
upstream order {
  server localhost:8090 weight=5;
}


server {
    listen              80;
    server_name         abc.com;
    access_log  "pipe:rollback /data/log/nginx/access.log interval=1d baknum=7 maxsize=1G"  main;

    location ^~/user/ {
        proxy_set_header Host $host;
        proxy_set_header  X-Real-IP        $remote_addr;
        proxy_set_header  X-Forwarded-For  $proxy_add_x_forwarded_for;
        proxy_set_header X-NginX-Proxy true;

        rewrite ^/user/(.*)$ /$1 break;
        proxy_pass http://user;
    }

    location ^~/order/ {
        proxy_set_header Host $host;
        proxy_set_header  X-Real-IP        $remote_addr;
        proxy_set_header  X-Forwarded-For  $proxy_add_x_forwarded_for;
        proxy_set_header X-NginX-Proxy true;

        rewrite ^/order/(.*)$ /$1 break;
        proxy_pass http://order;
    }

}
```

注意到proxy_pass结尾没有`/`， `rewrite`重写了url。







# 示例

```shell

user  root;
worker_processes  1;

#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;

#pid        logs/nginx.pid;


events {
    worker_connections  1024;
}


http {
    include       mime.types;
    default_type  application/octet-stream;

    #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
    #                  '$status $body_bytes_sent "$http_referer" '
    #                  '"$http_user_agent" "$http_x_forwarded_for"';

    #access_log  logs/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    #keepalive_timeout  0;
    keepalive_timeout  65;
	fastcgi_connect_timeout 300;
	fastcgi_send_timeout 300;
	fastcgi_read_timeout 300;
	proxy_connect_timeout 300s;
	proxy_send_timeout 300s;
	proxy_read_timeout 300s;
    #gzip  on;
	client_max_body_size 1025m;
	proxy_temp_path /tmp/temp_dir;
	proxy_cache_path /tmp/cache levels=1:2 keys_zone=cache_one:100m inactive=1d max_size=10g;
    server {
         listen  80;
	 server_name 127.0.0.1 ;
   	 rewrite ^(.*) https://$host$1 permanent;
    }
    server {
        listen 443 ssl;
        ssl_certificate  /usr/local/nginx/conf/server.crt;
        ssl_certificate_key  /usr/local/nginx/conf/server_nopwd.key;
	#ssl_protocols     TLSv1.2 TLSv1.3;
        ssl_protocols     TLSv1 TLSv1.1 TLSv1.2;	 
        #ssl_protocols    SSLv2  TLSv1.2 TLSv1.3 TLSv1.1;
	ssl_prefer_server_ciphers on;
	ssl_dhparam /usr/local/nginx/conf/dhparams.pem;
        #charset koi8-r;
	server_name  localhost;
	ssl_session_cache    shared:SSL:1m;
	ssl_session_timeout  5m;
	ssl_ciphers  HIGH:!aNULL:!MD5;
	server_tokens off;		

	add_header Strict-Transport-Security "max-age=31536000; includeSubdomains";

        location / {
           root   html;
           index  index.html;
#	   ModSecurityEnabled on;  
#           ModSecurityConfig modsecurity.conf;
           proxy_pass   https://127.0.0.1:8443;
	   proxy_set_header   X-Real-IP $remote_addr;
        }
	location ~ /ukey{
	   root /usr/local;
	   index test.html;	
	}

	location ^~/SOCWeb/csoc/ {

		proxy_set_header Host $host;
		proxy_set_header  X-Real-IP        $remote_addr;
		proxy_set_header  X-Forwarded-For  $proxy_add_x_forwarded_for;
		proxy_set_header X-NginX-Proxy true;

       		proxy_pass http://127.0.0.1:8080/BDSOCWeb/;
        }
#	location /SOCWeb {
#           root   html;
#           index  index.html;
#           ModSecurityEnabled on;  
#           ModSecurityConfig modsecurity.conf;
#           proxy_pass   https://127.0.0.1:8443/SOCWeb;
#	   proxy_set_header   X-Real-IP $remote_addr;
#        }
#	location /ZZSY {
#           root   html;
#           index  index.html;
#           ModSecurityEnabled on;  
#           ModSecurityConfig modsecurity.conf;
#           proxy_pass   https://127.0.0.1:8443/ZZSY;
#	   proxy_set_header   X-Real-IP $remote_addr;
#        }
#	location /nereport {
#           root   html;
#           index  index.html;
#	   ModSecurityEnabled on;  
#           ModSecurityConfig modsecurity.conf;
#           proxy_pass   https://127.0.0.1:8443/nereport;
#	   proxy_set_header   X-Real-IP $remote_addr;
#        }
	location /SOCWeb/websocket {
                        proxy_pass https://127.0.0.1:8443;
                        proxy_http_version 1.1;
                        proxy_read_timeout 86400s;
                        proxy_set_header Upgrade $http_upgrade;
                        proxy_set_header Connection "upgrade";
                }
        location /guacamole/ {
           # root   html;
           # index  index.html index.htm;
           proxy_pass   https://127.0.0.1:8443/guacamole/;
           proxy_set_header   X-Real-IP $remote_addr;
           proxy_set_header Upgrade $http_upgrade;
           proxy_set_header Connection "Upgrade";
        }

        #error_page  404              /404.html;

        # redirect server error pages to the static page /50x.html
        #
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }

        # proxy the PHP scripts to Apache listening on 127.0.0.1:80
        #
        location ~ \.(js|css|jpg|png|json|gif|swf|ico|xml)$ {
		proxy_pass  https://127.0.0.1:8443;
                proxy_redirect off;
                proxy_set_header Host $host;
                proxy_cache cache_one;
                proxy_cache_valid 200 302 24h;
                proxy_cache_valid 301 30d;
                proxy_cache_valid any 5m;
                expires 90d;
                add_header wall  "hey!guys!give me a star.";
	}

    }


}

```

