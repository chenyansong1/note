---
title: logstash安装及启动
categories: logstash   
toc: true  
tag: [logstash]
---

# 架构

![](https://github.com/chenyansong1/note/blob/master/img/bigdata/logstash/1.png?raw=true)

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

https://blog.csdn.net/songfeihu0810232/article/details/94406608


官方文档:
https://www.elastic.co/guide/en/logstash/current/getting-started-with-logstash.html

中文网站: 
http://kibana.logstash.es/content/


