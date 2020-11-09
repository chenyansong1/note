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

        proxy_pass http://user/;
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