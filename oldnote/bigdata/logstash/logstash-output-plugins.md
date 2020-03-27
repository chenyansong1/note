[TOC]



# elasticsearch

## 重试策略

提交到ES是通过http请求进行的，会返回一个Error，200表示ok，如下的错误码将会被处理：

* 400 or 404 error 这些event会被丢到dead letter queue中（如果dead letter queue没有开启，那么会有错误日志提醒，同时这部分event会被drop掉）
* 409 error（conflict）也是打印错误日志，然后drop 掉这部分event，如果想要retry这部分409 error，那么可以设置`retry_on_conflict`的值



单个请求数据超过20M，那么将分多次请求，如果一个document的数据超过20M，那么只能一次请求了

## 配置选项

```js
output {
    
    elasticsearch {
        action => "index" //delete, create, update, 其他 "%{[foo]}"
        failure_type_logging_whitelist => [] //如果有些错误不想被记录，这里写上错误号，比如409error
        
        hosts => ["127.0.0.1:9200","127.0.0.2:9200"]// ["http://127.0.0.1"] ["https://127.0.0.1:9200"] ["https://127.0.0.1:9200/mypath"]
        
        ilm_enabled => auto //true, false, auto 自动创建索引， ES 7.0.0之后
        
        index => "logstash-%{+YYYY.MM.dd}" //这样的索引不包含大写字符
        
        template => path //
        template_name => "logstash" //
    }
}
```



# email 



# file

写文件到磁盘，可以使用event的field作为文件名或者是文件路径的一部分，默认写一行日志以json格式，可以自定义格式的（使用line)

```ruby
output {
 file {
   path => ...
   codec => line { format => "custom format: %{message}"}
 }
}
```



```js

output {
	file {
		create_if_deleted => true //创建是否重建
    	dir_mode => 0750 //看umask
        file_mode => -1 //同上
        flush_interval => 2 //0表示每条日志都会写磁盘
        path => /var/log/logstash/%{host}/%{application} //这里可以使用event field；  path => "./test-%{+YYYY-MM-dd}.txt"
        codec => // codec plugins 插件
	}
}
```







# http







# kafka



# mongodb



# pipe



# redis



# syslog



# tcp



# udp



