---
title: logstash几种配置示例
categories: logstash   
toc: true  
tag: [logstash]
---


下面是logstash的几种配置示例,更多的配置看[官网](https://www.elastic.co/guide/en/logstash/current/config-examples.html)

<!--more-->


# logstash采集数据到kafka的配置文件
```
input {
  file {
    path => "/var/nginx_logs/*.log"        #采集路径 
    discover_interval => 5        #5s采集一次
    start_position => "beginning"    #刚开始从头开始采集
  }
}
 
output {
	kafka {
	  topic_id => "accesslog"     #kafka的topic
	  codec => plain {
		format => "%{message}"
		charset => "UTF-8"
	  }
	  bootstrap_servers => "172.16.0.11:9092,172.16.0.12:9092,172.16.0.13:9092" #指定kafka的broker 的地址
	}
}

```


# 采集数据从kafka到elasticsearch
```
input {
  kafka {
    type => "level-one"
    auto_offset_reset => "smallest"//从最小偏移读
    codec => plain {    //文本
      charset => "GB2312"
    }
	group_id => "es"//groupID
	topic_id => "itcast"        //topic
	zk_connect => "172.16.0.11:2181,172.16.0.12:2181,172.16.0.13:2181" //zk
  }
}

filter {
  mutate {
    split => { "message" => "	" }//字段分割符,如果是\t(制表符),那么需要手动输入一个制表符(所以此处你看到的空格其实是制表符)
      add_field => {
        "event_type" => "%{message[3]}"
        "current_map" => "%{message[4]}"
        "current_X" => "%{message[5]}"
        "current_y" => "%{message[6]}"
        "user" => "%{message[7]}"
        "item" => "%{message[8]}"
        "item_id" => "%{message[9]}"
        "current_time" => "%{message[12]}"
     }
     remove_field => [ "message" ]
  } 
}

output {
    elasticsearch {
      index => "level-one-%{+YYYY.MM.dd}"
	  codec => plain {
        charset => "GB2312"
      }
      hosts => ["172.16.0.14:9200", "172.16.0.15:9200", "172.16.0.16:9200"]
    } 
}


```


