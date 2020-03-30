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

  















http

i18n

java_uuid

jdbc_static

json

kv

mutate

range

syslog_pri

threats_classifier

throttle

tld

translate

truncate

urldecode

uuid

xml







