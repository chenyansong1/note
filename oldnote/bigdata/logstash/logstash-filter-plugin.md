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
		cache_size => 1000 #默认缓存的IP条数，这个是要缓存在内存中的
	}
}
```









grok

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







