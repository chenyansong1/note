---
title: es-设置模板
categories: elasticsearch   
toc: true  
tag: [elasticsearch]
---



索引别名的应用场景：

　　比如，公司使用es收集应用的运行日志，每个星期创建一个索引库，这样时间长了就会创建很多的索引库，操作和管理的时候很不方便。

　　由于新增索引数据只会操作当前这个星期的索引库，所以就创建了两个别名。

　　　　curr_week：这个别名指向这个星期的索引库，新增数据操作这个索引库。

　　　　last_3_month：这个别名指向最近三个月的所有索引库，因为我们的需求是查询最近三个月的日志信息。

　　后期只需要修改这两个别名和索引库之间的指向关系即可。应用层代码不需要任何改动。

　　还要把三个月以前的索引库close掉，留存最近一年的日志数据，一年以前的数据删除掉。

　  说明：可以类似，指定多个索引库查询。定义一个索引别名，如zhouls_all，将索引zhouls1映射到这个别名上，把索引zhouls2，把索引zhoulsn，也映射到这个别名上。

　　那么，在通过别名来查询时，直接同查询别名zhouls_all，就可以把对应所有的索引zhouls,1,2,...n都一次性查询完了。

 　　但是，如果你是具体要插入和操作数据，则，就不方便使用别名了。而是具体到某个索引zhoulsn了。


# 创建

```
PUT /_template/my_logs 
{
  "template": "logstash-*", 
  "order":    1, 
  "settings": {
    "number_of_shards": 1 
  },
  "mappings": {
    "_default_": { 
      "_all": {
        "enabled": false
      }
    }
  },
  "aliases": {
    "last_3_months": {} 
  }
}

# Example 说明

PUT /_template/my_logs 		#创建一个名为 my_logs 的模板。
{
  "template": "logstash-*", 	#将这个模板应用于所有以 logstash- 为起始的索引。
  "order":    1, 			#这个模板将会覆盖默认的 logstash 模板，因为默认模板的 order 更低。
  "settings": {
    "number_of_shards": 1 	#限制主分片数量为 1 。
  },
  "mappings": {
    "_default_": { 
      "_all": {
        "enabled": false	#为所有类型禁用 _all 域。
      }
    }
  },
  "aliases": {
    "last_3_months": {} 	#添加这个索引至 last_3_months 别名中。
  }
}


# 下面是实际的例子
PUT /_template/my_logs 
{
  "template": "logstash-*", 
 
  "mappings": {
    "_default_": { 
      "_all": {
        "enabled": false
      }
    },
    "properties":{
    	"productID":{
    		 "type" : "string",
             "index" : "not_analyzed" 
    	}
    }
  }
}


```

# 查看






# 删除

```
DELETE /_template/template_1

```



test_temp























https://www.elastic.co/guide/cn/elasticsearch/guide/current/index-templates.html

https://elasticsearch.cn/article/335

http://www.cnblogs.com/zlslch/p/6478168.html