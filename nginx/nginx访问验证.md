[TOC]



# nginx开启访问控制

* 添加密码验证
* 添加白名单

```
user  root;
worker_processes  1;




events {
    worker_connections  1024;
}


http {
    include       mime.types;
    default_type  application/octet-stream;



    sendfile        on;

    keepalive_timeout  65;
    
    client_max_body_size 1025m;
    server {  
        listen  80;  
        server_name 127.0.0.1 ;  
        rewrite ^(.*) https://$host$1 permanent;      
    }

    upstream www_server_pools {
        server 10.130.10.18:9200;
        server 10.130.10.19:9200;
        server 10.130.10.60:9200;
    }

   server {
        listen       19200;
        server_name  10.130.10.111;

        location /_plugin/head/ {
                rewrite ^/(.*)$ /$1  break;
                #添加密码验证
                auth_basic            "Password please";
                auth_basic_user_file  ngxESAuth;
                proxy_pass http://10.130.10.18:9200/$1;
                #添加白名单
                allow   172.16.110.173;
                deny    all;
        }

        location / {
                rewrite ^/(.*)$ /$1  break;
                auth_basic            "Password please";
                auth_basic_user_file  ngxESAuth;
                proxy_connect_timeout   3;
                proxy_send_timeout      30;
                proxy_read_timeout      30;
                proxy_set_header Host $host;
                proxy_set_header X-Real-IP $remote_addr;
                proxy_set_header REMOTE-HOST $remote_addr;
                proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
                proxy_pass http://www_server_pools/$1;

                allow   172.16.110.173;
                deny    all;

       }

    }
}
```





# 客户端访问方式







转自：

https://stackoverflow.com/questions/25559387/is-it-possible-to-use-http-basic-auth-to-connect-to-nginx-proxied-elasticsearch

https://www.techcoil.com/blog/how-to-send-an-http-request-to-a-http-basic-authentication-endpoint-in-java-without-using-any-external-libraries/



需要java8的Base64

```
String authHeader = "Basic " + new String(Base64.encodeBase64(String.format("%s:%s", username, password).getBytes()));

Index index = new Index.Builder(json)
          .index(indexName)
          .type(type)
          .id(id)
          .setHeader("Authorization", authHeader)
          .build();

JestResult result = client.execute(index);
```





```
BufferedReader httpResponseReader = null;
try {
    // Connect to the web server endpoint
    URL serverUrl = new URL("http://httpbin.org/basic-auth/user/passwd");
    HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();
 
    // Set HTTP method as GET
    urlConnection.setRequestMethod("GET");
 
    // Include the HTTP Basic Authentication payload
    urlConnection.addRequestProperty("Authorization", basicAuthPayload);
 
    // Read response from web server, which will trigger HTTP Basic Authentication request to be sent.
    httpResponseReader =
            new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
    String lineRead;
    while((lineRead = httpResponseReader.readLine()) != null) {
        System.out.println(lineRead);
    }
 
} catch (IOException ioe) {
    ioe.printStackTrace();
} finally {
 
    if (httpResponseReader != null) {
        try {
            httpResponseReader.close();
        } catch (IOException ioe) {
            // Close quietly
        }
    }
}
```





