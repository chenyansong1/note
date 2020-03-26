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

文件的状态有如下的几种："closed" or "ignored" "watched" "active",  and "unwatched"，处理文件的步骤如下：

- Checks whether "closed" or "ignored" files have changed in size since last time and if so puts them in the "watched" state.
- Selects enough "watched" files to fill the available space in the window, these files are made "active".
- The active files are opened and read, each file is read from the last known position to the end of current content (EOF) by default.

一下情况是可以控制的：

1. 哪些文件可以被先读取
2. 排序
3. 文件是被完全读取（读完A文件再读B文件，依次...） or 部分读取（先读取A文件的部分，然后读取B文件的部分，依次...），这里涉及的参数有：`file_chunk_count` and `file_chunk_size`
4. 部分读取和sort可以让event尽早的进入kibana中



## 两种操作文件的模式

tail mode and read mode



## 记录文件的当前读取位置

记录在sincedb文件中，格式如下：

1. The inode number (or equivalent).
2. The major device number of the file system (or equivalent).
3. The minor device number of the file system (or equivalent).
4. The current byte offset within the file.
5. The last active timestamp (a floating point number)
6. The last known path that this record was matched to (for old sincedb records converted to the new format, this is blank.

可选项：https://www.elastic.co/guide/en/logstash/7.2/plugins-inputs-file.html#_tail_mode

# generator

生成随机日志事件，用于测试plugin的性能



# http

监听在指定的host：port来接收请求

# http_poller

使用http请求，去获取http数据



# java_stdin

ls的core插件

# jdbc

定时查询一次database的数据到input中，其中每一条记录就是一条event，而其中的列就是event的field



https://www.elastic.co/guide/en/logstash/7.2/plugins-inputs-jdbc.html

# kafka

https://www.elastic.co/guide/en/logstash/7.2/plugins-inputs-kafka.html

# pipe



# redis

# stdin

# syslog



# tcp

https://www.elastic.co/guide/en/logstash/7.2/plugins-inputs-syslog.html

```ruby
input {
  tcp {
    port => 12345
    codec => json
  }
}
```

# udp

参数说明

| Setting                                                      | Input type                                                   | Required | default | 注释                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ | -------- | ------- | ------------------------------------------------------------ |
| [`buffer_size`](https://www.elastic.co/guide/en/logstash/7.2/plugins-inputs-udp.html#plugins-inputs-udp-buffer_size) | [number](https://www.elastic.co/guide/en/logstash/7.2/configuration-file-structure.html#number) | No       | 65536   | 读取的网络的最大包大小                                       |
| [`host`](https://www.elastic.co/guide/en/logstash/7.2/plugins-inputs-udp.html#plugins-inputs-udp-host) | [string](https://www.elastic.co/guide/en/logstash/7.2/configuration-file-structure.html#string) | No       | 0.0.0.0 |                                                              |
| [`port`](https://www.elastic.co/guide/en/logstash/7.2/plugins-inputs-udp.html#plugins-inputs-udp-port) | [number](https://www.elastic.co/guide/en/logstash/7.2/configuration-file-structure.html#number) | Yes      |         | 1024 以下需要root权限                                        |
| [`queue_size`](https://www.elastic.co/guide/en/logstash/7.2/plugins-inputs-udp.html#plugins-inputs-udp-queue_size) | [number](https://www.elastic.co/guide/en/logstash/7.2/configuration-file-structure.html#number) | No       | 2000    | This is the number of unprocessed UDP packets you can hold in memory before packets will start dropping.超过这个数将开始丢包 |
| [`receive_buffer_bytes`](https://www.elastic.co/guide/en/logstash/7.2/plugins-inputs-udp.html#plugins-inputs-udp-receive_buffer_bytes) | [number](https://www.elastic.co/guide/en/logstash/7.2/configuration-file-structure.html#number) | No       |         | The socket receive buffer size in bytes                      |
| [`workers`](https://www.elastic.co/guide/en/logstash/7.2/plugins-inputs-udp.html#plugins-inputs-udp-workers) | [number](https://www.elastic.co/guide/en/logstash/7.2/configuration-file-structure.html#number) | No       | 2       | Number of threads processing packets                         |
| [`source_ip_fieldname`](https://www.elastic.co/guide/en/logstash/7.2/plugins-inputs-udp.html#plugins-inputs-udp-source_ip_fieldname) | [string](https://www.elastic.co/guide/en/logstash/7.2/configuration-file-structure.html#string) | No       | host    | source IP address 的名字                                     |





# unix

作为一个socket去连接，可以是server or client









