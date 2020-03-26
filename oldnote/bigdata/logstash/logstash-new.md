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

# 重新加载配置，多久检查一次，默认3秒
--config.reload.interval <interval>

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



## 关闭logstash

```shell
sudo systemctl stop logstash

#or
kill -TERM {logstash_pid}

#if you are console 
Alternatively, enter Ctrl-C in the console.
```




logstash关闭的时候做了什么

- Stop all input, filter and output plugins
- Process all in-flight events
- Terminate the Logstash process



The following conditions affect the shutdown process:

- An input plugin receiving data at a slow pace.
- A slow filter, like a Ruby filter executing `sleep(10000)` or an Elasticsearch filter that is executing a very heavy query.
- A disconnected output plugin that is waiting to reconnect to flush in-flight events.

如果需要强制关闭logstash (会丢数据)

 use the `--pipeline.unsafe_shutdown` flag when you start Logstash

# 日志logging

你能够为某个子系统，module，或者plugin单独配置日志

```shell
#1.首先设置日志级别是DEBUG

#2.只为一个单独的组件设置日志级别，减少其他日志的带来的干扰，修改了log4j2需要重启logstash ,例如下面我们要定位elasticsearch的output的问题，修改log4j2.properties
logger.elasticsearchoutput.name = logstash.outputs.elasticsearch
logger.elasticsearchoutput.level = debug
```



#  logstash配置

写一个我们自己的配置文件，使用-f去指定执行改配置文件

## 配置文件的结构

```properties
# This is a comment. You should use comments to describe
# parts of your configuration.
input {
  ...
}

filter {
  ...
}

output {
  ...
}

#可以给每个插件配置多个值，具体的配置参见每个plugin
input {
  file {
    path => "/var/log/messages"
    type => "syslog"
  }

  file {
    path => "/var/log/apache/access.log"
    type => "apache"
  }
}
```

## 值类型

* Array

过时了，不推荐使用

```properties
  users => [ {id => 1, name => bob}, {id => 2, name => jane} ]
```



* Lists

```properties
  path => [ "/var/log/messages", "/var/log/*.log" ]
  uris => [ "http://elastic.co", "http://example.net" ]
```

* Boolean

```properties
  ssl_enable => true  #没有引号
```



* Bytes

```properties
#Both SI (k M G T P E Z Y) and Binary (Ki Mi Gi Ti Pi Ei Zi Yi) units are supported. Binary units are in base-1024 and SI units are in base-1000
#不区分大小写

  my_bytes => "1113"   # 1113 bytes
  my_bytes => "10MiB"  # 10485760 bytes
  my_bytes => "100kib" # 102400 bytes
  my_bytes => "180 mb" # 180000000 bytes
```

* Codes

参见插件：https://www.elastic.co/guide/en/logstash/7.2/codec-plugins.html

```
codec => "json"
```

* Hash

```properties
#格式 "field1" => "value1"

match => {
  "field1" => "value1"
  "field2" => "value2"
  ...
}
# or as a single line. No commas between entries:
match => { "field1" => "value1" "field2" => "value2" }
#Note that multiple key value entries are separated by spaces rather than commas. 
#多个key-value ,使用空格而不是逗号分隔
```

* Number

Numbers must be valid numeric values (floating point or integer).

Example:

```js
  port => 33
```

* Password

A password is a string with a single value that is not logged or printed.

Example:

```js
  my_password => "password"
```

* URI

A URI can be anything from a full URL like *http://elastic.co/* to a simple identifier like *foobar*. If the URI contains a password such as *http://user:pass@example.net* the password portion of the URI will not be logged or printed.

Example:

```js
  my_uri => "http://foo:bar@example.net"
```

* Path

A path is a string that represents a valid operating system path.

Example:

```js
  my_path => "/tmp/logstash"
```

* String

可以使用单引号，或者双引号



* 转义字符

you will need to set `config.support_escapes: true` in your `logstash.yml`

| Text | Result                     |
| ---- | -------------------------- |
| \r   | carriage return (ASCII 13) |
| \n   | new line (ASCII 10)        |
| \t   | tab (ASCII 9)              |
| \\   | backslash (ASCII 92)       |
| \"   | double quote (ASCII 34)    |
| \'   | single quote (ASCII 39)    |

Example:

```js
  name => "Hello world"
  name => 'It\'s a beautiful day'
```



# 在配置文件中访问事件数据和字段

logstash是使用inputs->filters->outputs来处理事件

input:生成事件

filters:修改事件

outputs:发送event到 elsewhere

因为input时，event还没有生成，所以所有的field只能适用于filter和output

## 字段引用

顶级字段直接使用[fieldname] 或者 fieldname；嵌套字段使用`[top-level field][nested field]`.

```js
{
  "agent": "Mozilla/5.0 (compatible; MSIE 9.0)",
  "ip": "192.168.24.44",
  "request": "/index.html"
  "response": {
    "status": 200,
    "bytes": 52353
  },
  "ua": {
    "os": "Windows 7"
  }
}

#To reference the os field, you specify [ua][os]. To reference a top-level field such as request, you can simply specify the field name.
```



## 格式化输出字段

```js
output {
  statsd {
    increment => "apache.%{[response][status]}"
  }
}
```

## 时间字段

```js
output {
  file {
    path => "/var/log/%{type}.%{+yyyy.MM.dd.HH}"
  }
}

#具体的格式，参见：http://joda-time.sourceforge.net/apidocs/org/joda/time/format/DateTimeFormat.html
```



## 条件Conditions

如果你想过滤filter或者output一个event在一个确定的条件下

```js
if EXPRESSION {
  ...
} else if EXPRESSION {
  ...
} else {
  ...
}
```



You can use the following comparison operators:

- equality: `==`, `!=`, `<`, `>`, `<=`, `>=`
- regexp: `=~`, `!~` (checks a pattern on the right against a string value on the left)
- inclusion: `in`, `not in`

The supported boolean operators are:

- `and`, `or`, `nand`, `xor`

The supported unary operators are:

- `!`



```js
filter {
    #这里会有字段的引用[action]
  if [action] == "login" {
    mutate { remove_field => "secret" }
  }
}

# and
output {
  # Send production errors to pagerduty
  if [loglevel] == "ERROR" and [deployment] == "production" {
    pagerduty {
    ...
    }
  }
}
 
    
#in
filter {
  if [foo] in [foobar] {
    mutate { add_tag => "field in field" }
  }
  if [foo] in "foo" {
    mutate { add_tag => "field in string" }
  }
  if "hello" in [greeting] {
    mutate { add_tag => "string in field" }
  }
  if [foo] in ["hello", "world", "foo"] {
    mutate { add_tag => "field in list" }
  }
  if [missing] in [alsomissing] {
    mutate { add_tag => "shouldnotexist" }
  }
  if !("foo" in ["hello", "world"]) {
    mutate { add_tag => "shouldexist" }
  }
}
    

#not in
output {
  if "_grokparsefailure" not in [tags] {
    elasticsearch { ... }
  }
}
    
#判断某个字段是否存在
#The expression if [foo] returns false when
[foo] doesn’t exist in the event,
[foo] exists in the event, but is false, or
[foo] exists in the event, but is null
```



## @metadata字段

logstash1.5之后，就有了这个特殊的字段@metadata，这个字段的内容不是你output的一部分（官网是这么说，但是我测试的时候还是看到了@version ， @timestamp）

```ruby
input { stdin { } }

#添加了三条元数据字段
filter {
  mutate { add_field => { "show" => "This data will be in the output" } }
  mutate { add_field => { "[@metadata][test]" => "Hello" } }
  mutate { add_field => { "[@metadata][no_show]" => "This data will not be in the output" } }
}

output {
  if [@metadata][test] == "Hello" {
    stdout { codec => rubydebug }
  }
}
```

输出是这样的

```ruby
$ bin/logstash -f ../test.conf
Pipeline main started
asdf
{
    "@timestamp" => 2016-06-30T02:42:51.496Z,
      "@version" => "1",
          "host" => "example.com",
          "show" => "This data will be in the output",
       "message" => "asdf"
}
```

如果想要输出@metadat的内容，如下配置

```ruby
 stdout { codec => rubydebug { metadata => true } }
```

那么输出如下

```ruby
$ bin/logstash -f ../test.conf
Pipeline main started
asdf
{
    "@timestamp" => 2016-06-30T02:46:48.565Z,
     "@metadata" => {
           "test" => "Hello",
        "no_show" => "This data will not be in the output"
    },
      "@version" => "1",
          "host" => "example.com",
          "show" => "This data will be in the output",
       "message" => "asdf"
}
```

关于元数据最常用的是提供模板

```ruby
input { stdin { } }

filter {
  grok { match => [ "message", "%{HTTPDATE:[@metadata][timestamp]}" ] }
  date { match => [ "[@metadata][timestamp]", "dd/MMM/yyyy:HH:mm:ss Z" ] }
}

output {
  stdout { codec => rubydebug }
}
```

输出如下

```ruby
$ bin/logstash -f ../test.conf
Pipeline main started
02/Mar/2014:15:36:43 +0100
{
    "@timestamp" => 2014-03-02T14:36:43.000Z,
      "@version" => "1",
          "host" => "example.com",
       "message" => "02/Mar/2014:15:36:43 +0100"
}
```



## 配置实例

```js
input {
  #指定读取文件
  file {
    path => "/tmp/access_log"
    #path => "/tmp/*_log"  #一类文件
    start_position => "beginning"
  }
}

filter {
  if [path] =~ "access" {
    mutate { replace => { "type" => "apache_access" } }
    grok {
      match => { "message" => "%{COMBINEDAPACHELOG}" }
    }
  }
  date {
    match => [ "timestamp" , "dd/MMM/yyyy:HH:mm:ss Z" ]
  }
}

output {
  elasticsearch {
    hosts => ["localhost:9200"]
  }
  stdout { codec => rubydebug }
}
```

添加条件判断

```ruby
input {
  file {
    path => "/tmp/*_log"
  }
}

filter {
    #这里有字段引用，条件判断
  if [path] =~ "access" {
    mutate { replace => { type => "apache_access" } }
    grok {
      match => { "message" => "%{COMBINEDAPACHELOG}" }
    }
    date {
      match => [ "timestamp" , "dd/MMM/yyyy:HH:mm:ss Z" ]
    }
  } else if [path] =~ "error" {
    mutate { replace => { type => "apache_error" } }
  } else {
    mutate { replace => { type => "random_logs" } }
  }
}

output {
  elasticsearch { hosts => ["localhost:9200"] }
  stdout { codec => rubydebug }
}
```

正则匹配

```js
output {
  if [type] == "apache" {
    if [status] =~ /^5\d\d/ {
      nagios { ...  }
    } else if [status] =~ /^4\d\d/ {
      elasticsearch { ... }
    }
    statsd { increment => "apache.%{status}" }
  }
}
```

定义处理syslog日志

```ruby
input {
  tcp {
    port => 5000
    type => syslog
  }
  udp {
    port => 5000
    type => syslog
  }
}

filter {
  if [type] == "syslog" {
    grok {
      match => { "message" => "%{SYSLOGTIMESTAMP:syslog_timestamp} %{SYSLOGHOST:syslog_hostname} %{DATA:syslog_program}(?:\[%{POSINT:syslog_pid}\])?: %{GREEDYDATA:syslog_message}" }
      add_field => [ "received_at", "%{@timestamp}" ]
      add_field => [ "received_from", "%{host}" ]
    }
    date {
      match => [ "syslog_timestamp", "MMM  d HH:mm:ss", "MMM dd HH:mm:ss" ]
    }
  }
}

output {
  elasticsearch { hosts => ["localhost:9200"] }
  stdout { codec => rubydebug }
}
```

# 配置文件中使用环境变量

* 格式`${var}`
* 环境变量是大小写敏感的
* 对于没有定义的环境变量，logstash会报错
* 可以给出一个默认值，这样没有定义，也不会报错，格式：`${var: default value}`
* 环境变量的类型可以是：string,number,boolean, array, hash
* 环境变量是不可以被更改的，一旦更改，需要restart logstash 才能生效

设置tcp的端口

```shell
export TCP_PORT=12345

input {
  tcp {
    port => "${TCP_PORT}"
  }
}

input {
  tcp {
    port => 12345
  }
}

input {
  tcp {
    port => "${TCP_PORT:54321}"
  }
}
```

设置文件路径

```ruby
export HOME="/path"

filter {
  mutate {
    add_field => {
      "my_path" => "${HOME}/file.log"
    }
  }
}
```

# 多管道

类似于多线程

```yaml
#vim pipelines.yml
#下面定义了多个pipeline
- pipeline.id: my-pipeline_1
  path.config: "/etc/path/to/p1.config"
  pipeline.workers: 3
- pipeline.id: my-other-pipeline
  path.config: "/etc/different/path/p2.cfg"
  queue.type: persisted
```

在pipelines.yml文件中没有明确指定的，那么将会使用logstash.yml中的值

如果你启动logstash的时候没有指定任何参数，那么他会读取pipelines.yml文件，然后来实例化pipelines，但是如果你使用了-e or -f 那么logstash会忽略pipelines.yml文件，日志会有关于这个的警告

注意事项：

1. 对于input,filter ， output不同的event流，使用多管道是特别有用的，同时可以使用tags and conditionals 进行操作
2. 可以给每个不同的管道配置不同的性能参数

# pipeline-to-pipeline 通信

## 简介

1. 采用 client-server的方式

2. pipeline input是server，启动一个监听虚拟地址

3. pipeline output是client，发送event到input

```yaml
# config/pipelines.yml
- pipeline.id: upstream
  config.string: input { stdin {} } output { pipeline { send_to => [myVirtualAddress] } }
- pipeline.id: downstream
  config.string: input { pipeline { address => myVirtualAddress } }
```

   

1. 只能相同进程的output能发送数据到虚拟地址
2. output能够发送数据到一个虚拟地址list
3. logstash会复制每个event，所以使用这种方式会有java heap增大的问题



## 几种不同的模式

### distributor pattern

一种数据的输入，输入的数据是复杂的，需要分拣到不同的管道中，进行不同的逻辑处理

![](E:\git-workspace\note\images\bigdata\logstash\1585105245641.png)

```yaml
# config/pipelines.yml
- pipeline.id: beats-server
  config.string: |
    input { beats { port => 5044 } }
    output {
        if [type] == apache {
          pipeline { send_to => weblogs }
        } else if [type] == system {
          pipeline { send_to => syslog }
        } else {
          pipeline { send_to => fallback }
        }
    }
- pipeline.id: weblog-processing
  config.string: |
    input { pipeline { address => weblogs } }
    filter {
       # Weblog filter statements here...
    }
    output {
      elasticsearch { hosts => [es_cluster_a_host] }
    }
- pipeline.id: syslog-processing
  config.string: |
    input { pipeline { address => syslog } }
    filter {
       # Syslog filter statements here...
    }
    output {
      elasticsearch { hosts => [es_cluster_b_host] }
    }
- pipeline.id: fallback-processing
    config.string: |
    input { pipeline { address => fallback } }
    output { elasticsearch { hosts => [es_cluster_b_host] } }
```



### The output isolator pattern模式

lostash将会阻塞，如果多个output中的一个output挂了的话，使用这种模式将会避免这个问题，例如下面如果http挂了，那么还是会发送数据到ES

```yaml
# config/pipelines.yml
- pipeline.id: intake
  queue.type: persisted
  config.string: |
    input { beats { port => 5044 } }
    output { pipeline { send_to => [es, http] } }
- pipeline.id: buffered-es
  queue.type: persisted
  config.string: |
    input { pipeline { address => es } }
    output { elasticsearch { } }
- pipeline.id: buffered-http
  queue.type: persisted
  config.string: |
    input { pipeline { address => http } }
    output { http { } }
```



### The forked path pattern

好像和上一种相同



### the collector pattern

```yaml
# config/pipelines.yml
- pipeline.id: beats
  config.string: |
    input { beats { port => 5044 } }
    output { pipeline { send_to => [commonOut] } }
- pipeline.id: kafka
  config.string: |
    input { kafka { ... } }
    output { pipeline { send_to => [commonOut] } }
- pipeline.id: partner
  # This common pipeline enforces the same logic whether data comes from Kafka or Beats
  config.string: |
    input { pipeline { address => commonOut } }
    filter {
      # Always remove sensitive data from all input sources
      mutate { remove_field => 'sensitive-data' }
    }
    output { elasticsearch { } }
```



# 多行事件（multiline event)

如果需要将多行当做一条event，参见：https://www.elastic.co/guide/en/logstash/7.2/multiline.html





# 全局模式匹配

```shell
*conf

*apache*

** Match directories recursively.
? Match any one character.

[set] Match any one character in a set. For example, [a-z]. Also supports set negation ([^a-z]).

{p,q}
Match either literal p or literal q. regular expressions (foo|bar).

\ Escape the next metacharacter.


#example
"/path/to/*.conf"
Matches config files ending in .conf in the specified path.
"/var/log/**/*.log
Matches log files ending in .log in subdirectories under the specified path.
"/path/to/logs/{app1,app2,app3}/data.log"
Matches app log files in the app1, app2, and app3 subdirectories under the specified path.

```

# ls-to-ls 通信

需要通过证书进行认证

https://www.elastic.co/guide/en/logstash/7.2/ls-to-ls.html



# ls-node monitor

https://www.elastic.co/guide/en/logstash/7.2/configuring-logstash.html

# 两种队列方式

* Persistent Queeus ：存储数据在磁盘上的一个队列
* Dead Letter Queues ：存储不能处理的数据在磁盘上的一个队列上

- [Persistent Queues](https://www.elastic.co/guide/en/logstash/7.2/persistent-queues.html) protect against data loss by storing events in an internal queue on disk.
- [Dead Letter Queues](https://www.elastic.co/guide/en/logstash/7.2/dead-letter-queues.html) provide on-disk storage for events that Logstash is unable to process. You can easily reprocess events in the dead letter queue by using the `dead_letter_queue` input plugin.

默认没有开启这些特性的



## Persistent Queues

ls默认是使用的memory存储数据，这种方式如果ls临时挂了，那么数据就会丢失，同时持久化队列可以用来替代如Redis，MQ，kafka等这些publish-subscriber模型

优点：

1. 不需要消耗机器buffer，可以实现像Redis or kafka这样的消息队列
2. 提供至少一次的发送数据，保证数据在logstash短暂故障之后不会丢失数据



缺点：

1. 存储在disk上的数据是没有副本的，disk坏了，那么数据也一样会丢失



persistent Queue work

input → queue → filter + output

当写队列成功之后，input send 一条消息到 data source，当一条event被filter和output处理完成之后，被标记为ack，没有被标记为ack的事件下一次还是会被filter和output继续处理



配置

```shell
queue.type: persisted. By default, persistent queues are disabled (default: queue.type: memory).

path.queue: The directory path where the data files will be stored. By default, the files are stored in path.data/queue.

queue.page_capacity: The maximum size of a queue page in bytes. The queue data consists of append-only files called "pages". The default size is 64mb. 
Changing this value is unlikely to have performance benefits.

queue.drain: Specify true if you want Logstash to wait until the persistent queue is drained before shutting down. The amount of time it takes to drain the queue depends on the number of events that have accumulated in the queue. 
Therefore, you should avoid using this setting unless the queue, even when full, is relatively small and can be drained quickly.建议不要设置

queue.max_events: The maximum number of events that are allowed in the queue. The default is 0 (unlimited).默认是0，表示没有限制

queue.max_bytes: The total capacity of the queue in number of bytes. The default is 1024mb (1gb). Make sure the capacity of your disk drive is greater than the value you specify here.

If you are using persistent queues to protect against data loss, but don’t require much buffering, you can set queue.max_bytes to a smaller value, such as 10mb, to produce smaller queues and improve queue performance.
如果只是为了保护数据不丢失，那么不需要太大的buffer，设置set queue.max_bytes to a smaller value, such as 10mb, to produce smaller queues and improve queue performance.


#example
queue.type: persisted
queue.max_bytes: 4gb
```

如果Queue满了，那么logstash的input将不再接受数据，After the filter and output stages finish processing existing events in the queue and ACKs them, Logstash automatically starts accepting new events.



* 持久化控制
  1. persistent queue有一系列的page去存储数据（每一个page是一个文件），其中有两类page，head page（只有一个） 和 tail page（有很多个），input是向head page写数据的，在head page写满数据之后会将这个head page变成tail page（queue.page_capacity 达到），一个新的head page创建
  2. head page只是添加数据，而tail page是不能改变的
  3. the queue records details about itself (pages, acknowledgements, etc) in a separate file called a checkpoint file.当记录一个checkpoint的时候，logstash将会做如下的几件事：
     1. Call fsync on the head page.
     2. Atomically write to disk the current state of the queue.
  4. 任何没有进入checkpoint的数据，即使进入了queue，也是会丢失
  5. 为了保证checkpoint记录的实时性，可以频繁的checkpoint，这里就有一个参数`queue.checkpoint.writes`，如果设置为1，那么force a checkpoint after each event is written. 默认这个值的1024，**需要说明的是这样会有性能消耗，因为频繁的写磁盘**



* page 数据回收

  each page is one file，Pages are deleted (garbage collected) after all events in that page have been ACKed

## Dead Letter Queues

* 原理

  Dead Letter：当一条event不能成功处理（要么是mapping问题，要么是其他问题），此时ls的处理方式，要么是挂起，要么是drop the event，如果配置了这个特性，那么write unsuccessful events to a dead letter queue instead of dropping them，这个特性目前只是支持ES，并且只是在特定的ls中有

  

  每条没有写成功的event的格式是：原始的event，和meta数据（用来描述event没有被成功处理的原因，以及某个plugin的信息和入库的timestamp）

![](E:\git-workspace\note\images\bigdata\logstash\dead_letter_queue.png)



* 配置

  ```yaml
  #默认的关闭的
  dead_letter_queue.enable: true
  
  #配置文件的存储路径
  path.dead_letter_queue: "path/to/data/dead_letter_queue"
  #By default, the dead letter queue files are stored in path.data/dead_letter_queue. 
  #Each pipeline has a separate queue. For example, the dead letter queue for the main pipeline is stored in LOGSTASH_HOME/data/dead_letter_queue/main by default. The queue files are numbered sequentially: 1.log, 2.log, and so on.
  #不能将使用相同的路径在两个不同的logstash实例中
  
  #文件回滚策略
  ##当文件达到file size的阈值的时候，一个新的文件会自动创建
  ##默认的dead letter的最大size是1024mb，可以改变
  dead_letter_queue.max_bytes
  #如果超过这个设定，那么整个文件将会被删除
  ```

* 处理在dead letter queue中的event

  通过dead letter input plugin来处理dead event，可以根据不同的需求来处理这些dead event，例如如果是mapping引起的error，那么读dead event，然后删除mapping的error的field，然后重新入到ES中

  ```yaml
  input {
    dead_letter_queue {
      path => "/path/to/data/dead_letter_queue" #队列的存储路径
      commit_offsets => true 	#设置offset，下次从这里消费，这样不会重复执行
      pipeline_id => "main"  #处理dead letter queue数据的pipeline 的ID，默认的main
    }
  }
  
  output {
    stdout {
      codec => rubydebug { metadata => true }
    }
  }
  ```

  ![1585184295816](E:\git-workspace\note\images\bigdata\logstash\1585184295816.png)

  处理dead queue的数据是不需要停下系统单独处理的，如果这次还是没有成功处理，那么这条event就会被忽略了，不会重复提交到dead letter queue中再次被处理

* 处理指定时间后的数据

  当dead queue里面的数据比较多的时候，我们只想从指定的地方开始读取数据，此时是可以指定时间戳的

  ```yaml
  input {
    dead_letter_queue {
      path => "/path/to/data/dead_letter_queue"
      start_timestamp => "2017-06-06T23:40:37"
      pipeline_id => "main"
    }
  }
  ```



* Example

  ```json
  {"geoip":{"location":"home"}}
  ```

  ```json
  {
     "@metadata" => {
      "dead_letter_queue" => {
         "entry_time" => #<Java::OrgLogstash::Timestamp:0x5b5dacd5>,
          "plugin_id" => "fb80f1925088497215b8d037e622dec5819b503e-4",
        "plugin_type" => "elasticsearch",
             "reason" => "Could not index event to Elasticsearch. status: 400, action: [\"index\", {:_id=>nil, :_index=>\"logstash-2017.06.22\", :_type=>\"doc\", :_routing=>nil}, 2017-06-22T01:29:29.804Z My-MacBook-Pro-2.local {\"geoip\":{\"location\":\"home\"}}], response: {\"index\"=>{\"_index\"=>\"logstash-2017.06.22\", \"_type\"=>\"doc\", \"_id\"=>\"AVzNayPze1iR9yDdI2MD\", \"status\"=>400, \"error\"=>{\"type\"=>\"mapper_parsing_exception\", \"reason\"=>\"failed to parse\", \"caused_by\"=>{\"type\"=>\"illegal_argument_exception\", \"reason\"=>\"illegal latitude value [266.30859375] for geoip.location\"}}}}"
      }
    },
    "@timestamp" => 2017-06-22T01:29:29.804Z,
      "@version" => "1",
         "geoip" => {
      "location" => "home"
    },
          "host" => "My-MacBook-Pro-2.local",
       "message" => "{\"geoip\":{\"location\":\"home\"}}"
  }
  ```

  ```json
  input {
    dead_letter_queue {
      path => "/path/to/data/dead_letter_queue/" 
    }
  }
  filter {
    mutate {
      remove_field => "[geoip][location]" 
    }
  }
  output {
    elasticsearch{
      hosts => [ "localhost:9200" ] 
    }
  }
  ```





# 转换数据的plugin

## 核心操作

* date filter

  Parses dates from fields to use as Logstash timestamps for events.

  ```json
  filter {
    date {
      #设置logdate为ls的时间戳
      match => [ "logdate", "MMM dd yyyy HH:mm:ss" ]
    }
  }
  ```

* drop filter

  和条件配合使用，用来删除event

  ```json
  filter {
    if [loglevel] == "debug" {
      drop { }
    }
  }
  ```

* 生成一致性hash

  The following config fingerprints the `IP`, `@timestamp`, and `message` fields and adds the hash to a metadata field called `generated_id`:

  ```json
  filter {
    fingerprint {
      source => ["IP", "@timestamp", "message"]
      method => "SHA1"
      key => "0123"
      target => "[@metadata][generated_id]"
    }
  }
  ```

* mutate filter

  You can rename, remove, replace, and modify fields in your events.

  The following config renames the `HOSTORIP` field to `client_ip`:

  ```json
  filter {
    mutate {
      rename => { "HOSTORIP" => "client_ip" }
    }
  }
  ```

  删除字符两端的空格

  ```json
  filter {
    mutate {
      strip => ["field1", "field2"]
    }
  }
  ```

## 序列化数据

这部分涉及到数据的反序列化

https://www.elastic.co/guide/en/logstash/7.2/data-deserialization.html



## 提取字段

提取字段，and 解析非结构化的字段

* dissect filter 解析

  解析非结构化的数据，通过分隔符解析，并不使用正则表达式，如果要使用正则表达式，那么可以使用grok filter

  ```json
  Apr 26 12:20:02 localhost systemd[1]: Starting system activity accounting tool...
  ```

  ```json
  filter {
    dissect {
      mapping => { "message" => "%{ts} %{+ts} %{+ts} %{src} %{prog}[%{pid}]: %{msg}" }
    }
  }
  ```

  解析结果如下

  ```json
  {
    "msg"        => "Starting system activity accounting tool...",
    "@timestamp" => 2017-04-26T19:33:39.257Z,
    "src"        => "localhost",
    "@version"   => "1",
    "host"       => "localhost.localdomain",
    "pid"        => "1",
    "message"    => "Apr 26 12:20:02 localhost systemd[1]: Starting system activity accounting tool...",
    "type"       => "stdin",
    "prog"       => "systemd",
    "ts"         => "Apr 26 12:20:02"
  }
  ```

* kv filter

  键值对解析

  ```json
  ip=1.2.3.4 error=REFUSED
  ```

  ```json
  filter {
    kv { }
  }
  ```

  After the filter is applied, the event in the example will have these fields:

  - `ip: 1.2.3.4`
  - `error: REFUSED`



* grok filter

  可以使用正则表达式解析日志

  ```json
  55.3.244.1 GET /index.html 15824 0.043
  ```

  ```json
  filter {
    grok {
      match => { "message" => "%{IP:client} %{WORD:method} %{URIPATHPARAM:request} %{NUMBER:bytes} %{NUMBER:duration}" }
    }
  }
  ```

  After the filter is applied, the event in the example will have these fields:

  - `client: 55.3.244.1`
  - `method: GET`
  - `request: /index.html`
  - `bytes: 15824`
  - `duration: 0.043`



## 添加额外数据到event中

* geoip filter

  ```json
  filter {
    geoip {
      source => "clientip"
    }
  }
  ```

* jdbc_static filter

  提前加载database的数据，来enrich event

  ```json
  filter {
    jdbc_static {
      loaders => [ 
      #指定查询缓存在本地的数据
        {
          id => "remote-servers"
          query => "select ip, descr from ref.local_ips order by ip"
          local_table => "servers"
        },
        {
          id => "remote-users"
          query => "select firstname, lastname, userid from ref.local_users order by userid"
          local_table => "users"
        }
      ]
  	#Defines the columns, types, and indexes used to build the local database structure. The column names and types should match the external database
      local_db_objects => [ 
        {
          name => "servers"
          index_columns => ["ip"]
          columns => [
            ["ip", "varchar(15)"],
            ["descr", "varchar(255)"]
          ]
        },
        {
          name => "users"
          index_columns => ["userid"]
          columns => [
            ["firstname", "varchar(255)"],
            ["lastname", "varchar(255)"],
            ["userid", "int"]
          ]
        }
      ]
      local_lookups => [ 
        {
          id => "local-servers"
          query => "select descr as description from servers WHERE ip = :ip"
          parameters => {ip => "[from_ip]"}
          target => "server"  # Specifies the event field that will store the looked-up data. If the lookup returns multiple columns, the data is stored as a JSON object within the field.
        },
        {
          id => "local-users"
          query => "select firstname, lastname from users WHERE userid = :id"
          parameters => {id => "[loggedin_userid]"}
          target => "user" 
        }
      ]
      # using add_field here to add & rename values to the event root
      add_field => { server_name => "%{[server][0][description]}" }
      add_field => { user_firstname => "%{[user][0][firstname]}" } 
      add_field => { user_lastname => "%{[user][0][lastname]}" }
      remove_field => ["server", "user"]
      jdbc_user => "logstash"
      jdbc_password => "example"
      jdbc_driver_class => "org.postgresql.Driver"
      jdbc_driver_library => "/tmp/logstash/vendor/postgresql-42.1.4.jar"
      jdbc_connection_string => "jdbc:postgresql://remotedb:5432/ls_test_2"
    }
  }
  ```



* jdbc_streaming filter

  The following example executes a SQL query and stores the result set in a field called `country_details`:

  ```json
  filter {
    jdbc_streaming {
      jdbc_driver_library => "/path/to/mysql-connector-java-5.1.34-bin.jar"
      jdbc_driver_class => "com.mysql.jdbc.Driver"
      jdbc_connection_string => "jdbc:mysql://localhost:3306/mydatabase"
      jdbc_user => "me"
      jdbc_password => "secret"
      statement => "select * from WORLD.COUNTRY WHERE Code = :code"
      parameters => { "code" => "country_code"}
      target => "country_details"
    }
  }
  ```

* translate filter

  The [translate filter](https://www.elastic.co/guide/en/logstash/7.2/plugins-filters-translate.html) replaces field contents based on replacement values specified in a hash or file. Currently supports these file types: YAML, JSON, and CSV.

  The following example takes the value of the `response_code` field, translates it to a description based on the values specified in the dictionary, and then removes the `response_code` field from the event:

  ```json
  filter {
    translate {
      field => "response_code"
      destination => "http_response"
      dictionary => {
        "200" => "OK"
        "403" => "Forbidden"
        "404" => "Not Found"
        "408" => "Request Timeout"
      }
      remove_field => "response_code"
    }
  }
  ```

# 性能调节

## troubleshooting

1. CPU

   如果CPU is high， 需要调节worker 的settings

2. IO

   1. 检查磁盘是否饱和：iostat
   2. 检查network是否饱和：iftop

3. JVM heap

   当heap是too low的时候，会造成jvm的垃圾回收，此时CPU会居高不下，需要增加heap的大小，不要让heap大小超过物理内存，**至少要留1G的内存给物理的OS和其他进程**

   可以使用jmap或者VisualVM 去测量jvm的使用情况

   调整minimum (Xms) and maximum (Xmx) heap allocation size to the same value

4. worker settings



## 调整性能问题

```shell
#这个值是增加filter和output的线程数，当发现event在积累，或者CPU是空闲的时候
#增加这个值会增加IO
pipeline.workers

#每次发送到filter和output之前的event数量（单个worker），增加这个会增加内存
pipeline.batch.size

#filter and output最大延时处理event的时间，一般不怎么设定
pipeline.batch.delay

```

# ls性能监控

The metrics collected by Logstash include:

- Logstash node info, like pipeline settings, OS info, and JVM info.
- Plugin info, including a list of installed plugins.
- Node stats, like JVM stats, process stats, event-related stats, and pipeline runtime stats.
- Hot threads.

https://www.elastic.co/guide/en/logstash/7.2/logstash-monitoring-overview.html



# 插件

## 代理

The majority of the plugin manager commands require access to the internet to reach [RubyGems.org](https://rubygems.org/). If your organization is behind a firewall you can set these environments variables to configure Logstash to use your proxy.

```shell
export http_proxy=http://localhost:3128
export https_proxy=http://localhost:3128
```

## Listing plugins

```shell
bin/logstash-plugin list 
bin/logstash-plugin list --verbose 
bin/logstash-plugin list '*namefragment*'  #Will list all installed plugins containing a namefragment

#Will list all installed plugins for a particular group (input, filter, codec, output)
bin/logstash-plugin list --group output

```



## add plugin

能访问网络时

```shell
bin/logstash-plugin install logstash-output-kafka
```

如果不能访问网络

```shell
bin/logstash-plugin install /path/to/logstash-output-kafka-1.0.0.gem
```

自定义插件

The path needs to be in a specific directory hierarchy: `PATH/logstash/TYPE/NAME.rb`, where TYPE is *inputs* *filters*, *outputs* or *codecs* and NAME is the name of the plugin.

```shell
# supposing the code is in /opt/shared/lib/logstash/inputs/my-custom-plugin-code.rb
bin/logstash --path.plugins /opt/shared/lib
```



## update or remove plugin

```shell
bin/logstash-plugin update 
bin/logstash-plugin update logstash-output-kafka
```

```shell
bin/logstash-plugin remove logstash-output-kafka
```



## 自定义插件

https://www.elastic.co/guide/en/logstash/7.2/plugin-generator.html

```sh
bin/logstash-plugin generate --type input --name xkcd --path ~/ws/elastic/plugins
```

- `--type`: Type of plugin - input, filter, output, or codec
- `--name`: Name for the new plugin
- `--path`: Directory path where the new plugin structure will be created. If not specified, it will be created in the current directory.

  

# 安装过程中遇到的问题



别人写好的grok ： https://github.com/logstash-plugins/logstash-patterns-core/tree/master/patterns



arm架构-问题：

1. ES的安装，遇到jdk的问题（需要jdk是arm的包）

2. kebana的安装问题：

   https://www.huaweicloud.com/kunpeng/software/kibana.html

3. logstash-IP中文包汉化问题



![](C:\Users\landun\AppData\Roaming\Typora\typora-user-images\1585032026981.png)