生成一个Chrome浏览器信任的自签证书

[TOC]

# 1.证书生成脚本



```
openssl req \
    -newkey rsa:2048 \
    -x509 \
    -nodes \
    -keyout www.taishiganzhi.com.key \
    -new \
    -out www.taishiganzhi.com.crt \
    -subj /CN=www.taishiganzhi.com.dev \
    -reqexts SAN \
    -extensions SAN \
    -config <(cat /etc/pki/tls/openssl.cnf  \
        <(printf '[SAN]\nsubjectAltName=DNS:www.taishiganzhi.com')) \
    -sha256 \
    -days 3650

```



> 以上脚本会生成两个文件：
>
> www.taishiganzhi.com.crt  
>
> www.taishiganzhi.com.key



# 2.Windows本地导入证书

1. win+r
2. 输入：certmgr.msc
3. 进入下面的配置页面

![1548301003305](https://github.com/chenyansong1/note/blob/master/images/linux/ssh/chrome-ssl1.png?raw=true)



![1548300933957](https://github.com/chenyansong1/note/blob/master/images/linux/ssh/chrome-ssl.png?raw=true)





# 3.域名访问

因为上面是设置的域名访问

所以要在本地进行hosts映射

打开文件：C:\Windows\System32\drivers\etc\hosts



添加：

>10.130.10.22	www.taishiganzhi.com



# 4.浏览器访问



![1548301155706](https://github.com/chenyansong1/note/blob/master/images/linux/ssh/chrome-ssl3.png?raw=true)





# 5.修改Nginx中的配置

```
vim /usr/local/nginx/conf/nginx.conf

    server {
        listen 443 ssl;
        #ssl_certificate  /usr/local/nginx/conf/server.crt;
        #ssl_certificate_key  /usr/local/nginx/conf/server_nopwd.key;

        ssl_certificate  /usr/local/nginx/conf/www.taishiganzhi.com.crt;
        ssl_certificate_key  /usr/local/nginx/conf/www.taishiganzhi.com.key;
	#......
```


