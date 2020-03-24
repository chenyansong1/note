[TOC]



# 常用网址

ES现有产品特性开源介绍：

https://www.elastic.co/cn/subscriptions



ES-stack下载：

https://www.elastic.co/cn/downloads/logstash



教程：

https://www.elastic.co/guide/en/logstash/7.2/first-event.html



# logstash 常用命令

```shell
#测试配置文件是否OK
bin/logstash -f first-pipeline.conf --config.test_and_exit

#启动logstash
bin/logstash -f first-pipeline.conf --config.reload.automatic

#The --config.reload.automatic option（修改配置文件自动重启选项） enables automatic config reloading so that you don’t have to stop and restart Logstash every time you modify the configuration file.

```

# 常用的插件



## filter

### grok



### geoip

```shell
input {
    beats {
        port => "5044"
    }
}
 filter {
    grok {
    	# combined apachelog 解析Apache日志
        match => { "message" => "%{COMBINEDAPACHELOG}"}
    }
    geoip {
    	# 地理位置插件
        source => "clientip"
    }
}
output {
    stdout { codec => rubydebug }
}
```



## output

```properties
output {
    elasticsearch {
    	#这里可以配置主机名
        hosts => [ "localhost:9200" ]
    }
}
```

完整的配置如下：

```properties
input {
    beats {
        port => "5044"
    }
}
 filter {
    grok {
        match => { "message" => "%{COMBINEDAPACHELOG}"}
    }
    geoip {
        source => "clientip"
    }
}
output {
    elasticsearch {
        hosts => [ "localhost:9200" ]
    }
}
```

### 写数据到不同的output中

```properties
#写数据到文件中
output {
    elasticsearch {
        hosts => ["IP Address 1:port1", "IP Address 2:port2", "IP Address 3"]
    }
    file {
        path => "/path/to/target/file"
    }
}

```

写数据到多个ES节点

```properties
output {
    elasticsearch {
    	#默认端口是9200，可以省略
        hosts => ["IP Address 1:port1", "IP Address 2:port2", "IP Address 3"]
    }
}
```

# logstash工作原理

inputs → filters → outputs. 

* Inputs generate events,
* filters modify them
* outputs ship them elsewhere.
>Inputs and outputs support codecs that enable you to encode or decode the data 



常用的input如下

- **file**: reads from a file on the filesystem, much like the UNIX command `tail -0F`
- **syslog**: listens on the well-known port 514 for syslog messages and parses according to the RFC3164 format
- **redis**: reads from a redis server, using both redis channels and redis lists. Redis is often used as a "broker" in a centralized Logstash installation, which queues Logstash events from remote Logstash "shippers".
- **beats**: processes events sent by [Beats](https://www.elastic.co/downloads/beats).



常用的filter如下：

- **grok**: parse and structure arbitrary text. Grok is currently the best way in Logstash to parse unstructured log data into something structured and queryable. With 120 patterns built-in to Logstash, it’s more than likely you’ll find one that meets your needs!

  解析非结构化的数据，有120种模板可以使用

- **mutate**: perform general transformations on event fields. **You can rename, remove, replace, and modify fields in your events**.

- **drop**: drop an event completely, for example, *debug* events.

- **clone**: make a copy of an event, possibly adding or removing fields.

- **geoip**: add information about geographical location of IP addresses (also displays amazing charts in Kibana!)

常用的output如下：

- **elasticsearch**: send event data to Elasticsearch. If you’re planning to save your data in an efficient, convenient, and easily queryable format…Elasticsearch is the way to go. Period. Yes, we’re biased :)
- **file**: write event data to a file on disk.
- **graphite**: send event data to graphite, a popular open source tool for storing and graphing metrics. http://graphite.readthedocs.io/en/latest/
- **statsd**: send event data to statsd, a service that "listens for statistics, like counters and timers, sent over UDP and sends aggregates to one or more pluggable backend services". If you’re already using statsd, this could be useful for you!

常用的编码，解码方式：

- **json**: encode or decode data in the JSON format.
- **multiline**: merge multiple-line text events such as java exception and stacktrace messages into a single event.（多行文本作为一个事件）

# logstash目录结构

目录结构说明（Directory Layout of `.zip` and `.tar.gz` Archives）

其他参见：https://www.elastic.co/guide/en/logstash/7.2/dir-layout.html#zip-targz-layout

| Type                   | Description                                                  | Default Location                                             | Setting         |
| ---------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ | --------------- |
| **home**               | Home directory of the Logstash installation.                 | `{extract.path}- Directory created by unpacking the archive` |                 |
| **bin**                | Binary scripts, including `logstash` to start Logstash and `logstash-plugin` to install plugins | `{extract.path}/bin`                                         |                 |
| **settings（config）** | Configuration files, including `logstash.yml` and `jvm.options` | `{extract.path}/config`                                      | `path.settings` |
| **logs**               | Log files                                                    | `{extract.path}/logs`                                        | `path.logs`     |
| **plugins**            | Local, non Ruby-Gem plugin files. Each plugin is contained in a subdirectory. Recommended for development only. | `{extract.path}/plugins`                                     | `path.plugins`  |
| **data**               | Data files used by logstash and its plugins for any persistence needs. | `{extract.path}/data`                                        | `path.data`     |

# lostash.yml文件说明

```yaml
#可以使用层级式
pipeline:
  batch:
    size: 125
    delay: 50

#也可以使用扁平式
pipeline.batch.size: 125
pipeline.batch.delay: 50


#也支持bash样式的环境变量
pipeline:
  batch:
    size: ${BATCH_SIZE}
    delay: ${BATCH_DELAY:50}
node:
  name: "node_${LS_NODE_NAME}"
path:
   queue: "/tmp/${QUEUE_DIR:queue}"
#Note that the ${VAR_NAME:default_value} notation is supported, setting a default batch delay of 50 and a default path.queue of /tmp/queue in the above example


#模块设定
modules:
  - name: MODULE_NAME1
    var.PLUGIN_TYPE1.PLUGIN_NAME1.KEY1: VALUE
    var.PLUGIN_TYPE1.PLUGIN_NAME1.KEY2: VALUE
    var.PLUGIN_TYPE2.PLUGIN_NAME2.KEY1: VALUE
    var.PLUGIN_TYPE3.PLUGIN_NAME3.KEY1: VALUE
  - name: MODULE_NAME2
    var.PLUGIN_TYPE1.PLUGIN_NAME1.KEY1: VALUE
    var.PLUGIN_TYPE1.PLUGIN_NAME1.KEY2: VALUE

```

其他参数

| Setting                        | Description                                                  | Default value                                                |
| ------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| `node.name`                    | A descriptive name for the node.                             | Machine’s hostname（默认主机名）                             |
| `path.data`                    | The directory that Logstash and its plugins use for any persistent needs. | `LOGSTASH_HOME/data`                                         |
| `pipeline.id`                  | The ID of the pipeline.                                      | `main`                                                       |
| `pipeline.java_execution`      | Use the Java execution engine.                               | true                                                         |
| `pipeline.workers`             | The number of workers that will, in parallel, execute the filter and output stages of the pipeline. If you find that events are backing up, or that the CPU is not saturated, consider increasing this number to better utilize machine processing power. | Number of the host’s CPU cores（使用的CPU core数，默认是主机上所有的CPU） |
| `pipeline.batch.size`          | The maximum number of events an individual worker thread will collect from inputs before attempting to execute its filters and outputs. Larger batch sizes are generally more efficient, but come at the cost of increased memory overhead. You may need to increase JVM heap space in the `jvm.options` config file. See [Logstash Configuration Files](https://www.elastic.co/guide/en/logstash/7.2/config-setting-files.html) for more info. | `125` 单个worker上的最大events发送到filter和output，这个数据增大会增加jvm堆内存（配置jvm heap 参见：jvm.options 文件） |
| `pipeline.batch.delay`         | When creating pipeline event batches, how long in milliseconds to wait for each event before dispatching an undersized batch to pipeline workers. | `50` （单位毫秒）                                            |
| `pipeline.unsafe_shutdown`     | When set to `true`, forces Logstash to exit during shutdown even if there are still inflight events in memory. By default, Logstash will refuse to quit until all received events have been pushed to the outputs. Enabling this option can lead to data loss during shutdown. | `false` （在关机的时候是否强制退出，设置为true的时候，可能会丢失数据） |
| `pipeline.plugin_classloaders` | (Beta) Load Java plugins in independent classloaders to isolate their dependencies. | `false`                                                      |
| `path.config`                  | The path to the Logstash config for the main pipeline. If you specify a directory or wildcard, config files are read from the directory in alphabetical order. | Platform-specific. See [Logstash Directory Layout](https://www.elastic.co/guide/en/logstash/7.2/dir-layout.html). |
| `config.string`                | A string that contains the pipeline configuration to use for the main pipeline. Use the same syntax as the config file. | None                                                         |
| `config.test_and_exit`         | When set to `true`, checks that the configuration is valid and then exits. Note that grok patterns are not checked for correctness with this setting. Logstash can read multiple config files from a directory. If you combine this setting with `log.level: debug`, Logstash will log the combined config file, annotating each config block with the source file it came from. | `false` （检查配置文件是否正确，如果有多个配置文件，那么检查每个配置文件，打印他们） |
| `config.reload.automatic`      | When set to `true`, periodically checks if the configuration has changed and reloads the configuration whenever it is changed. This can also be triggered manually through the SIGHUP signal. | `false` （如果配置文件修改了，那么自动加载配置文件，不需要手动重启） |
| `config.reload.interval`       | How often in seconds Logstash checks the config files for changes. | `3s` （多久检查一次配置文件是否改变，单位：秒）              |
| `config.debug`                 | When set to `true`, shows the fully compiled configuration as a debug log message. You must also set `log.level: debug`. WARNING: The log message will include any *password* options passed to plugin configs as plaintext, and may result in plaintext passwords appearing in your logs! | `false` （显示debug信息，这些打印的日志中 密码 文字）        |
| `config.support_escapes`       | When set to `true`, quoted strings will process the following escape sequences: `\n` becomes a literal newline (ASCII 10). `\r` becomes a literal carriage return (ASCII 13). `\t` becomes a literal tab (ASCII 9). `\\` becomes a literal backslash `\`. `\"` becomes a literal double quotation mark. `\'` becomes a literal quotation mark. | `false` （支持转义字符），如果为true，那么转移字符将会生效（如：\t 会变成tab) |
| `modules`                      | When configured, `modules` must be in the nested YAML structure described above this table. | None (一个嵌套的yaml结构)                                    |
| `queue.type`                   | The internal queuing model to use for event buffering. Specify `memory` for legacy in-memory based queuing, or `persisted` for disk-based ACKed queueing ([persistent queues](https://www.elastic.co/guide/en/logstash/7.2/persistent-queues.html)). | `memory`  （队列的数据持久化的方式：memory是内存，而persisted是磁盘持久化） |
| `path.queue`                   | The directory path where the data files will be stored when persistent queues are enabled (`queue.type: persisted`). | `path.data/queue` （持久化方式是persisted的话，指定磁盘路径） |
| `queue.page_capacity`          | The size of the page data files used when persistent queues are enabled (`queue.type: persisted`). The queue data consists of append-only data files separated into pages. | 64mb （一个page的数据大小），可能存在多个page                |
| `queue.max_events`             | The maximum number of unread events in the queue when persistent queues are enabled (`queue.type: persisted`). | 0 (unlimited) （0是没有限制），在队列中没有读取的最大event数 |
| `queue.max_bytes`              | The total capacity of the queue in number of bytes. Make sure the capacity of your disk drive is greater than the value you specify here. If both `queue.max_events` and `queue.max_bytes` are specified, Logstash uses whichever criteria is reached first. | 1024mb (1g) ，所有page的大小                                 |
| `queue.checkpoint.acks`        | The maximum number of ACKed events before forcing a checkpoint when persistent queues are enabled (`queue.type: persisted`). Specify `queue.checkpoint.acks: 0` to set this value to unlimited. | 1024                                                         |
| `queue.checkpoint.writes`      | The maximum number of written events before forcing a checkpoint when persistent queues are enabled (`queue.type: persisted`). Specify `queue.checkpoint.writes: 0` to set this value to unlimited. | 1024                                                         |
| `queue.checkpoint.retry`       | When enabled, Logstash will retry once per attempted checkpoint write for any checkpoint writes that fail. Any subsequent errors are not retried. This is a workaround for failed checkpoint writes that have been seen only on filesystems with non-standard behavior such as SANs and is not recommended except in those specific circumstances. | `false`                                                      |
| `queue.drain`                  | When enabled, Logstash waits until the persistent queue is drained before shutting down. | `false`                                                      |
| `dead_letter_queue.enable`     | Flag to instruct Logstash to enable the DLQ feature supported by plugins. | `false`                                                      |
| `dead_letter_queue.max_bytes`  | The maximum size of each dead letter queue. Entries will be dropped if they would increase the size of the dead letter queue beyond this setting. | `1024mb`                                                     |
| `path.dead_letter_queue`       | The directory path where the data files will be stored for the dead-letter queue. | `path.data/dead_letter_queue`                                |
| `http.host`                    | The bind address for the metrics REST endpoint.              | `"127.0.0.1"` （http的metrics的主机和端口 # Bind port for the metrics REST endpoint, this option also accept a range (9600-9700) and logstash will pick up the first available ports. http.port: 9600-9700） |
| `http.port`                    | The bind port for the metrics REST endpoint.                 | `9600` （绑定端口）                                          |
| `log.level`                    | The log level. Valid options are:`fatal``error``warn``info``debug``trace` | `info` （日志级别）                                          |
| `log.format`                   | The log format. Set to `json` to log in JSON format, or `plain` to use `Object#.inspect`. | `plain` （日志格式）                                         |
| `path.logs`                    | The directory where Logstash will write its log to.          | `LOGSTASH_HOME/logs` （日志路径）                            |
| `path.plugins`                 | Where to find custom plugins. You can specify this setting multiple times to include multiple paths. Plugins are expected to be in a specific directory hierarchy: `PATH/logstash/TYPE/NAME.rb` where `TYPE` is `inputs`, `filters`, `outputs`, or `codecs`, and `NAME` is the name of the plugin. | Platform-specific. See [Logstash Directory Layout](https://www.elastic.co/guide/en/logstash/7.2/dir-layout.html).  （自定义插件） |

# logstash的安全认证

秘钥库（用于安全设置）：为了让filebeat和logstash的数据传输更加的安全

参见：https://www.elastic.co/guide/en/logstash/7.2/keystore.html

# 启动logstash

## 命令行启动

在test阶段，命令行是ok的，但是在produce环境中，还是使用logstash.yml



```properties
bin/logstash [options]


#通过指定的配置文件启动
bin/logstash -f mypipeline.conf


#下面说明几个命令行参数的含义
--node.name NAME
Specify the name of this Logstash instance. If no value is given it will default to the current hostname.

-f, --path.config CONFIG_PATH
#指定配置文件或者目录 -f foo -f bar is the same as -f bar
Load the Logstash config from a specific file or directory. 
#可以支持这样指定
bin/logstash --debug -f '/tmp/{one,two,three}'


-e, --config.string CONFIG_STRING
Use the given string as the configuration data. Same syntax as the config file. If no input is specified, then the following is used as the default input: input { stdin { type => stdin } } and if no output is specified, then the following is used as the default output: output { stdout { codec => rubydebug } }. If you wish to use both defaults, please use the empty string for the -e flag. The default is nil.
#默认input是
input { stdin { type => stdin } }
#默认output是
output { stdout { codec => rubydebug } }
#如果都使用默认的
 -e
 
 
 #所有命令行参数：https://www.elastic.co/guide/en/logstash/7.2/running-logstash-command-line.html  
 #同时可以参见上方的表格注释

```



## 运行logstash作为一个service

参见：https://www.elastic.co/guide/en/logstash/7.2/running-logstash.html

这里只是说明其中的systemd

```shell
sudo systemctl start logstash.service
```



# 日志logging

你能够为某个子系统，module，或者plugin单独配置日志






















arm架构-问题：

1. ES的安装，遇到jdk的问题（需要jdk是arm的包）

2. kebana的安装问题：

   https://www.huaweicloud.com/kunpeng/software/kibana.html

3. logstash-IP中文包汉化问题



![1585032026981](C:\Users\landun\AppData\Roaming\Typora\typora-user-images\1585032026981.png)