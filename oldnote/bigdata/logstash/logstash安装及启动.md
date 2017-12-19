---
title: logstash安装及启动
categories: logstash   
toc: true  
tag: [logstash]
---

# 架构

![](http://ols7leonh.bkt.clouddn.com//assert/img/bigdata/logstash/1.png)


<!--more-->


# 安装
```
#Download and unzip Losgstash
下载地址:     https://www.elastic.co/downloads/logstash


#config file  Create a file named "logstash-simple.conf" and save it in the same directory as Logstash.
input { stdin { } }
output {
  elasticsearch { hosts => ["localhost:9200"] }
  stdout { codec => rubydebug }
}


#运行
bin/logstash -f logstash-simple.conf



```



配置文件的其他的例子

**https://www.elastic.co/guide/en/logstash/current/config-examples.html**




官方文档:
https://www.elastic.co/guide/en/logstash/current/getting-started-with-logstash.html

中文网站: 
http://kibana.logstash.es/content/


