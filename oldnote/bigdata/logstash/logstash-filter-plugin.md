[TOC]



# cvs

解析csv数据，可以指定列名

```js
filter {
    csv {
        columns => ["field1", "field2"]
        convert => {//Possible conversions are integer, float, date, date_time, boolean
            "field1" => "integer"
            "field2" => "boolean"
        }
        
        quote_char => 
        separator => ","
        
        //跳过空列
        skip_empty_colums => false
        
        //跳过空行
        skip_empty_rows => false
    }
}
```



# date

解析event的字段作为date，然后使用这个date作为这个event个的timestamp

```js
filter {
    match => [] //An array with field name first, and format patterns following, [ field, formats... ]
    //match => [ "logdate", "MMM dd yyyy HH:mm:ss", "MMM  d yyyy HH:mm:ss", "ISO8601" ]
    //对于嵌套结构，使用：[foo][bar]
    
    timezone => string //
    
}

```



# dissect

不同于split，他使用的是一系列的分隔符，但是他不使用正则表达式，所以他更快，如果你的event是多行的，那么Grok 更加的适用

使用示例

```shell
#一般示例
%{a} - %{b} - %{c}


# +  /num
%{+some_field}  #自然顺序添加字段
%{+some_field/2}  #指定顺序添加字段  %{+a/2} %{+a/1} %{+a/4} %{+a/3}   


# ?  跳过字段
%{}     # is an empty skip field.
%{?foo} # is a named skip field.



# &  The found value is added to the Event using the found value of another field as the key.

%{&some_field}

google: 77.98
%{?a}: %{&a}
google => 77.98


# -> add the -> suffix to the field on the left of the padding.
00000043 ViewReceive     machine-321
%{id} %{function->} %{server}

#输出如下
{
  "id": "00000043",
  "function": "ViewReceive",
  "server": "machine-123"
}

```

一个完整的例子

```
filter {
  if [type] == "syslog" or "syslog" in [tags] {
    dissect {
      mapping => {
        "message" => "%{ts} %{+ts} %{+ts} %{src} %{} %{prog}[%{pid}]: %{msg}"
      }
    }
  }
}
```



Dissect用到的选项

```shell
# convert_datatype  hash

filter {
  dissect {
    convert_datatype => {
      "cpu" => "float"
      "code" => "int"
    }
  }
}


# mapping hash
## \n 会被当做两个字符
## 如果其中存在双引号，那么使用单引号定义值
filter {
  dissect {
    mapping => {
      # using an actual line break
      "message" => '"%{field1}" "%{field2}"
 "%{description}"'   #使用单引号
      "description" => "%{field3} %{field4} %{field5}"
    }
  }
}

```

# drop

这个插件最好是和条件表达式结合

```shell
filter {
	if [loglevel] == "debug" {
		drop { }
	}
}
    
#percentage  default 100
#删除比例  
#Example, to only drop around 40% of the events that have the field loglevel with value "debug".
filter {
  if [loglevel] == "debug" {
    drop {
      percentage => 40  
    }
  }
}
```

# elasticsearch

从ES中查询出来数据

https://www.elastic.co/guide/en/logstash/7.2/plugins-filters-elasticsearch.html

```shell
#从数据库读取数据，将性别（1,2）替换成es对应数据（男女）
#logstash 从jdbc抽取数据，然后在过滤的过程中需要从es加载一些数据到内存中，作为比对数据！  从jdbc抽取得每条记录都要与内存中的数据比对，符合条件的就输出到文本

#用es数据替换掉字段值

```

# environment

如果使用，需要安装这个插件，这个插件存储子域在 @metadata中，然后这些子域可以在后面的其他部分使用

```shell
{ 
	environment { 
		add_metadata_from_env ⇒ { 
			"field_name" ⇒ "ENV_VAR_NAME" 
		}
	} 
}

#访问环境变量
["@metadata"]["field_name"]



#example
add_metadata_from_env => {
  "field1" => "ENV1"
  "field2" => "ENV2"
  # "field_n" => "ENV_n"
}
```

# extractnumbers

从字符串中提取数字

# fingerprint

创建一个唯一的hash，作为一个字段

https://www.elastic.co/guide/en/logstash/current/plugins-filters-fingerprint.html

# geoip

添加IP信息字段

```shell
filter {
	geoip {
		cache_size => 1000 #默认缓存的IP条数，这个是要缓存在内存中的,所以需要占用内存，即：并不是越大越好，超过这个数之后，就不会向内存中添加IP记录了
		#相同的geoip_type共享相同的这部分IP内存 The last declared cache size will win.所以只需要一份cache
		
		
		
		database => path
		#The default database is GeoLite2-City.
		#GeoLite2-City, GeoLite2-Country, GeoLite2-ASN are the free databases from Maxmind that are supported
		
		
		
		#This plugin now includes both the GeoLite2-City and GeoLite2-ASN databases. If database and default_database_type are unset, the GeoLite2-City database will be selected. To use the included GeoLite2-ASN database, set default_database_type to ASN.
		default_data_type => string
		#The default value is City
		#The only acceptable values are City and ASN

		
		fields => array 
		#An array of geoip fields to be included in the event.
		#Possible fields depend on the database type. By default, all geoip fields are included in the event.
		
		#For the built-in GeoLite2 City database, the following are available: city_name, continent_code, country_code2, country_code3, country_name, dma_code, ip, latitude, longitude, postal_code, region_name and timezone.
		
		source => string #必须字段
		#The field containing the IP address or hostname to map via geoip. If this field is an array, only the first value will be used.
		
		
		#The field containing the IP address or hostname，通过这个IP地址或者hostname，找到对应的地理位置信息
		source => string
		
		#Tags the event on failure to look up geo information. This can be used in later analysis.
		tag_on_failure => array  #Default value is ["_geoip_lookup_failure"]
		
		
		target => string #Default value is "geoip"
		
	}
}

```

举例说明

```tsx
input {
    stdin {
    }
}

filter {
    geoip {
            source => "message"
    }
}

output {
    stdout {
        codec => rubydebug
    }
}

#输入183.60.92.253，得到信息如下，geoip 下的就是地区信息
{
       "message" => "183.60.92.253",
       "geoip" => {  #增加的数据
       		"continent_code" => "AS",
          "longitude" => 113.25,
          "city_name" => "Guangzhou",
          "timezone" => "Asia/Shanghai",
          "ip" => "183.60.92.253",
          "country_name" => "China",
         	"country_code3" => "CN",
          "region_name" => "Guangdong",
          "location" => {
          		"lon" => 113.25,
          		"lat" => 23.1167
        	},
         "country_code2" => "CN",
         "region_code" => "44",
          "latitude" => 23.1167
    	},
      "@version" => "1",
    	"@timestamp" => 2018-11-26T02:00:07.753Z,
      "host" => "localhost.localdomain"
}


##### 保留IP地址信息的指定字段 #####
input {
    stdin {
    }
}

filter {
    geoip {
        source => "message"
        # 指定需要的字段
        fields => ["country_name", "continent_code", "region_name", "city_name", "latitude", "longitude"]
    }
}

output {
    stdout {
        codec => rubydebug
    }
}


#输入 183.60.92.253 返回的结果
{
    "message" => "183.60.92.253",
    "@version" => "1",
    "@timestamp" => 2018-11-26T02:26:37.333Z,
    "host" => "localhost.localdomain",
    "geoip" => {
     		"longitude" => 113.25,
        "continent_code" => "AS",
        "latitude" => 23.1167,
        "city_name" => "Guangzhou",
        "country_name" => "China",
        "region_name" => "Guangdong"
    }
}

#还可以通过 remove_field 删除字段
filter {
    geoip {
        source => "message"
        # 删除经纬度信息
        remove_field => ["[geoip][latitude]", "[geoip][longitude]"
    }
}
  
  
############## 重命名 geoip 字段 ###################  
filter {
    geoip {
        source => "message"
        fields => ["country_name", "continent_code", "region_name", "city_name", "latitude", "longitude"]
        target => "location"
    }
}


# 输入 183.60.92.253 返回的结果
{
      "location" => {
        "continent_code" => "AS",
              "latitude" => 23.1167,
          "country_name" => "China",
           "region_name" => "Guangdong",
             "city_name" => "Guangzhou",
             "longitude" => 113.25
    },
    "@timestamp" => 2018-11-26T02:51:35.604Z,
      "@version" => "1",
          "host" => "localhost.localdomain",
       "message" => "183.60.92.253"
}
```

source 可以是任意处理后的字段，需要注意的是 IP 必须是公网 IP，否者 logstash 返回空的信息，像这样

```dart
127.0.0.1
{
       "message" => "127.0.0.1",
    "@timestamp" => 2018-11-26T02:30:53.190Z,
          "host" => "localhost.localdomain",
         "geoip" => {},
      "@version" => "1",
          "tags" => [
        [0] "_geoip_lookup_failure"
    ]
}
```

```
geoip {
   ``source => "http_x_forwarded_for" # 取自nginx中的客户端ip
   ``target => "geoip"
   ``database => "/data/logstash/GeoLite2-City.mmdb"
   ``add_field => [ "[geoip][coordinates]", "%{[geoip][longitude]}" ]
   ``add_field => [ "[geoip][coordinates]", "%{[geoip][latitude]}" ]
  ``}
```



# grok

Grok or Dissect Or both?

Dissect：一些常规的分隔符，推荐使用，没有使用正则表达式，比较grok更快

Grok：如果是line to line ,使用正则表达式

Dissect and Grok：可以混合使用

这里有一些通用的匹配：https://github.com/logstash-plugins/logstash-patterns-core/blob/master/patterns/grok-patterns



* 基本语法

  ```shell
  %{SYNTAX:SEMANTIC}
  #syntax是pattern的名字
  #semantic：是字段名称
  
  
  # 55.3.244.1 GET /index.html 15824 0.043
   %{IP:client} %{WORD:method} %{URIPATHPARAM:request} %{NUMBER:bytes} %{NUMBER:duration}
  
  #产生如下的字段
  client: 55.3.244.1
  method: GET
  request: /index.html
  bytes: 15824
  duration: 0.043
  
  
  
  # 类型转换
  %{NUMBER:num:int}
  #which converts the num semantic from a string to an integer. 
  ```

  

* Custom Patterns

  Sometimes logstash doesn’t have a pattern you need. so 你必须自己写正则表达式

  ```shell
  #方式一
  (?<field_name>the pattern here)
  (?<queue_id>[0-9A-F]{10,11})
  
  
  
  
  #方式二
  #you can create a custom patterns file.
  #创建一个目录patterns，创建一个文件 extra(这个文件随意命名)
  #In that file, write the pattern you need as the pattern name, a space, then the regexp for that pattern.
  # contents of ./patterns/postfix:
  POSTFIX_QUEUEID [0-9A-F]{10,11}
  
  
  filter {
    grok {
      patterns_dir => ["./patterns"] #指定自定义的pattern的所在目录
      match => { "message" => "%{SYSLOGBASE} %{POSTFIX_QUEUEID:queue_id}: %{GREEDYDATA:syslog_message}" }
      #The timestamp, logsource, program, and pid fields come from the SYSLOGBASE pattern which itself is defined by other patterns.
    }
  }
  
  
  #方式三
  #Another option is to define patterns inline in the filter using pattern_definitions.
  
  ```



* 配置选项

  ```shell
  break_on_match
  filter {
  	grok {
  		#Break on first match. The first successful match by grok will result in the filter being finished. If you want grok to try all patterns (maybe you are parsing different things), then set this to false
  		break_on_match => true
  		
  		#If true, keep empty captures as event fields.
  		keep_empty_captures => false
  		
  		# 去匹配哪个字段
  		match => {
  			"message" => "Duration: %{NUMBER:duration}"
  		}
  		
  		#If you need to match multiple patterns against a single field, the value can be an array of patterns:
  		match => {
            "message" => [
              "Duration: %{NUMBER:duration}",
              "Speed: %{NUMBER:speed}"
            ]
          }
          
          #This allows you to overwrite a value in a field that already exists.
          #overwrite => array
          match => { "message" => "%{SYSLOGBASE} %{DATA:message}" }
          overwrite => [ "message" ]
          
          
          #
          pattern_definitions => hash
         	
          
          # ls有很多自定义的Pattern，除非必须，我们没必要自己定义pattern
          patterns_dir => array
          patterns_dir => ["/opt/logstash/patterns", "/opt/logstash/extra_patterns"]
  
  	    #一个Pattern file如下
  	    #NAME PATTERN
  	    NUMBER \d+
  
          
          #匹配的文件
          #Glob pattern, used to select the pattern files in the directories specified by patterns_dir
          patterns_files_glob => "*"
          
  	}
  }
  ```

  

# http

The HTTP filter provides integration with external web services/REST APIs.



# i18n

The i18n filter allows you to remove special characters from a field

# java_uuid

# jdbc_static

This filter enriches events with data pre-loaded from a remote database.

This filter is best suited for enriching events with reference data that is static or does not change very often, such as environments, users, and products.

你能缓存remote data在本地本地，内存中，可以周期性的加载remote data到本地，以下三部分需要定义：

* local_db_objects

  定义colums, type, index ，其中列名和type 和external database的数据列相对应，定义这些就是为了构建本地数据结构

  ```shell
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
  ```

  

* loaders

  查询remote data到本地的查询语句

  ```shell
      loaders => [ 
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
      
      #Make sure the column names and datatypes in the loader SQL statement match the columns defined under local_db_objects. 
      #Each loader has an independent remote database connection.
  ```

  

* lookups

  理想状态下只返回一行

  ```json
      local_lookups => [ 
        {
          id => "local-servers"
          query => "select descr as description from servers WHERE ip = :ip"
          parameters => {ip => "[from_ip]"}
          target => "server"
        },
        {
          id => "local-users"
          query => "select firstname, lastname from users WHERE userid = :id"
          parameters => {id => "[loggedin_userid]"}
          target => "user" 
        }
      ]
  
  #如果返回多列，那么会被store为一个车json对象
  ```

* 整理数据到event的root级别下

  Takes data from the JSON object and stores it in top-level event fields for easier analysis in Kibana.

  ```json
  # using add_field here to add & rename values to the event root
      add_field => { server_name => "%{[server][0][description]}" }
      add_field => { user_firstname => "%{[user][0][firstname]}" } 
      add_field => { user_lastname => "%{[user][0][lastname]}" } 
      remove_field => ["server", "user"]
      staging_directory => "/tmp/logstash/jdbc_static/import_data"
      loader_schedule => "* */2 * * *" # run loaders every 2 hours
      jdbc_user => "logstash"
      jdbc_password => "example"
      jdbc_driver_class => "org.postgresql.Driver"
      jdbc_driver_library => "/tmp/logstash/vendor/postgresql-42.1.4.jar"
      jdbc_connection_string => "jdbc:postgresql://remotedb:5432/ls_test_2"
  ```



下面是一个完整的例子

```json
input {
  generator {
    lines => [
      '{"from_ip": "10.2.3.20", "app": "foobar", "amount": 32.95}',
      '{"from_ip": "10.2.3.30", "app": "barfoo", "amount": 82.95}',
      '{"from_ip": "10.2.3.40", "app": "bazfoo", "amount": 22.95}'
    ]
    count => 200
  }
}

filter {
  json {
    source => "message"
  }

  jdbc_static {
    loaders => [
      {
        id => "servers"
        query => "select ip, descr from ref.local_ips order by ip"
        local_table => "servers"
      }
    ]
    local_db_objects => [
      {
        name => "servers"
        index_columns => ["ip"]
        columns => [
          ["ip", "varchar(15)"],
          ["descr", "varchar(255)"]
        ]
      }
    ]
    local_lookups => [
      {
        query => "select descr as description from servers WHERE ip = :ip"
        parameters => {ip => "[from_ip]"}
        target => "server"
      }
    ]
    staging_directory => "/tmp/logstash/jdbc_static/import_data"
    loader_schedule => "*/30 * * * *"
    jdbc_user => "logstash"
    jdbc_password => "logstash??"
    jdbc_driver_class => "org.postgresql.Driver"
    jdbc_driver_library => "/Users/guy/tmp/logstash-6.0.0/vendor/postgresql-42.1.4.jar"
    jdbc_connection_string => "jdbc:postgresql://localhost:5432/ls_test_2"
  }
}

output {
  stdout {
    codec => rubydebug {metadata => true}
  }
}
```



# json



# kv

# mutate

The mutate filter allows you to perform general mutations on fields. You can rename, remove, replace, and modify fields in your events.

Mutations in a config file are executed in this order:

- coerce
- rename
- update
- replace
- convert
- gsub
- uppercase
- capitalize
- lowercase
- strip
- remove
- split
- join
- merge
- copy



You can control the order by using separate mutate blocks.

```ruby
filter {
    mutate {
        split => ["hostname", "."]
        add_field => { "shortHostname" => "%{hostname[0]}" }
    }

    mutate {
        rename => ["shortHostname", "hostname" ]
    }
}
```

## mutate filter configuration options

```shell
#Convert a field’s value to a different type, like turning a string to an integer.
#If the field value is an array, all members will be converted. If the field is a hash no action will be taken.
convert
	#字段Type
	integer #strings are parsed; comma-separators are supported (e.g., the string "1,000" produces an integer with value of one thousand)
	interger_eu	#same as integer, except string values support dot-separators and comma-decimals (e.g., "1.000" produces an integer with value of one thousand)	
	float	#
	float_eu
	string
	boolean
	
	
    filter {
      mutate {
        convert => {
          "fieldname" => "integer"
          "booleanfield" => "boolean"
        }
      }
    }


#############
copy #Copy an existing field to another field. Existing target field will be overriden.
    filter {
      mutate {
         copy => { "source_field" => "dest_field" }
      }
    }
    
    
#############
gsub  #用指定字符串去替换匹配中的字符串

    filter {
      mutate {
        gsub => [
          # replace all forward slashes with underscore
          "fieldname", "/", "_",
          # replace backslashes, question marks, hashes, and minuses
          # with a dot "."
          "fieldname2", "[\\?#-]", "."
        ]
      }
    }

#############
join #Join an array with a separator character. Does nothing on non-array fields.

   filter {
     mutate {
       join => { "fieldname" => "," }
     }
   }


#############
lowercase => array #Convert a string to its lowercase equivalent.
    filter {
      mutate {
        lowercase => [ "fieldname" ]
      }
    }

uppercase

#############
merge
#Merge two fields of arrays or hashes. String fields will be automatically be converted into an array, so:
`array` + `string` will work
`string` + `string` will result in an 2 entry array in `dest_field`
`array` and `hash` will not work

    filter {
      mutate {
         merge => { "dest_field" => "added_field" }
      }
    }



#############
#Set the default value of a field that exists but is null
coerce
    filter {
      mutate {
        # Sets the default value of the 'field1' field to 'default_value'
        coerce => { "field1" => "default_value" }
      }
    }

#############
rename	 #Rename one or more fields.
    filter {
      mutate {
        # Renames the 'HOSTORIP' field to 'client_ip'
        rename => { "HOSTORIP" => "client_ip" }
      }
    }


#############
replace => hash
#Replace the value of a field with a new value. The new value can include %{foo} strings to help you build a new value from other parts of the event.
    filter {
      mutate {
        replace => { "message" => "%{source_host}: My new message" }
      }
    }


#############
split #Split a field to an array using a separator character. Only works on string fields.

    filter {
      mutate {
         split => { "fieldname" => "," }
      }
    }


#############
strip => array
#去掉两端的空格
#Strip whitespace from field. NOTE: this only works on leading and trailing whitespace.
    filter {
      mutate {
         strip => ["field1", "field2"]
      }
    }


#############
update #Update an existing field with a new value. If the field does not exist, then no action will be taken.
    filter {
      mutate {
        update => { "sample" => "My new message" }
      }
    }

```

# prune

The prune filter is for removing fields from events based on whitelists or blacklist of field names or their values (names and values can also be regular expressions).

基于event的字段名或者字段值，移除指定的字段，只支持顶级字段

```ruby
    filter {
      prune {
        whitelist_names => [ "msg" ]
      }
    }
Allows both `"msg"` and `"msg_short"` through.
  


    filter {
      prune {
        whitelist_names => ["^msg$"]
      }
    }
Allows only `"msg"` through.
```

```ruby
#blacklist_names #Exclude fields whose names match specified regexps, by default exclude unresolved %{field} strings.

		filter {
      prune {
        blacklist_names => [ "method", "(referrer|status)", "${some}_field" ]
      }
    }



#Exclude specified fields if their values match one of the supplied regular expressions. In case field values are arrays, each array item is matched against the regular expressions and matching array items will be excluded.

    filter {
      prune {
        blacklist_values => [ "uripath", "/index.php",
                              "method", "(HEAD|OPTIONS)",
                              "status", "^[^2]" ]
      }
    }

```





# range

检查number or string是否在指定的范围内，如果是那么执行指定的action，Supported actions are drop event, add tag, or add field with specified value

```ruby
    filter {
      range {
        ranges => [ "message", 0, 10, "tag:short",
                    "message", 11, 100, "tag:medium",
                    "message", 101, 1000, "tag:long",
                    "message", 1001, 1e1000, "drop",
                    "duration", 0, 100, "field:latency:fast",
                    "duration", 101, 200, "field:latency:normal",
                    "duration", 201, 1000, "field:latency:slow",
                    "duration", 1001, 1e1000, "field:latency:outlier",
                    "requests", 0, 10, "tag:too_few_%{host}_requests" ]
      }
    }
```



# ruby





syslog_pri

threats_classifier

throttle

tld

translate

truncate

urldecode

uuid

xml







