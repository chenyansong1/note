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









drop

elasticsearch

environment

extractnumbers

从字符串中提取数字

fingerprint

geoip

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







