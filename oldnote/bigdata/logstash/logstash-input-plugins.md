[TOC]

# dead_letter_queue

https://www.elastic.co/guide/en/logstash/7.2/plugins-inputs-dead_letter_queue.html

# elasticsearch

作用：重建索引

操作：可以通过cron去定时查询 or 指定一个time去加载数据到logstash

示例：

```ruby
    input {
      # Read all documents from Elasticsearch matching the given query
      elasticsearch {
        hosts => "localhost"
        query => '{ "query": { "match": { "statuscode": 200 } }, "sort": [ "_doc" ] }'
      }
    }
```

就会生成一个如下的查询条件

```json
    curl 'http://localhost:9200/logstash-*/_search?&scroll=1m&size=1000' -d '{
      "query": {
        "match": {
          "statuscode": 200
        }
      },
      "sort": [ "_doc" ]
    }'
```

## 调度语法

参见：https://github.com/jmettraux/rufus-scheduler#parsing-cronlines-and-time-strings

| `* 5 * 1-3 *`               | will execute every minute of 5am every day of January through March. |
| --------------------------- | ------------------------------------------------------------ |
| `0 * * * *`                 | will execute on the 0th minute of every hour every day.      |
| `0 6 * * * America/Chicago` | will execute at 6:00am (UTC/GMT -5) every day.               |



## 参数选项

参见：https://www.elastic.co/guide/en/logstash/7.2/plugins-inputs-elasticsearch.html#_scheduling

# exec

定时（也是使用的cron）或者间隔时间，捕获cmd的命令作为input

选项参数：https://www.elastic.co/guide/en/logstash/7.2/plugins-inputs-exec.html

# file





# generator







# http





# http_poller





# java_generator



# java_stdin



# jdbc



# kafka



# log4j



# meetup



# pipe



# redis

# stdin

# syslog

# tcp

# udp

# unix









