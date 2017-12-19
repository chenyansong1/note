# 1.创建索引

在创建的时候指定primary shard 和replica shard

```
PUT /secisland/
{
  "settings":{
    "index":{"number_of_shards":3, "number_of_replicas":2} 
  }
}


#简写
PUT /secisland/
{
  "settings":{"number_of_shards":3, "number_of_replicas":2}
}


#返回结果
{
  "acknowledged": true
}
```


# 2.更新索引的副本数量

创建索引的时候可以通过修改参数number_of_shards和number_of_replicas参数来改变分片和副本的数量，在默认情况下，会创建5个主分片，每个主分片1个副本分片,下面是创建3个主分片和2个副本分片的数量

在head中查看

![](/images/es/number_shard_replicas.jpg)


另外一种方式是通过update-index-settingAPI完成对副本数量的修改，例如：
```
put /secisland/_settings/
{
	"number_of_replicas:1
}
```
这样就把副本数量改成了1个副本，主分片我们是不能修改的

# 创建索引并手动指定映射

对于任何es文档而言，一个文档会包括一个或者多个字段，任何字段都要有自己的数据类型，例如：string，integer，date等，es中时通过映射中是通过映射来进行字段和数据类型对应的，在默认情况下es会自动识别字段的数据类型，同时es提供了mappings参数可以显示进行映射

下面是显示的指定字段的映射类型

```
PUT /secisland
{
	"settings":{"number_of_shards"3,"number_of_replicas":2},
	"mappings":{
		"secilog":{
			"properties":{
				"logType":{
					"type":"string",
					"index":"not_analyzed"
				}
			}
		}
	}
}

```
在上面的例子中，我们创建了一个名为secilog的类型（type），类型中有一个字段（logType),字段名称为string，而且这个字段是不进行分析的


# 删除索引

```
DELETE /secisland/

```
上面的实例删除了名为secisland的索引，删除索引需要指定索引名称，别名或者通配符

删除索引可以使用逗号分隔符，或者使用_all或*号删除全部索引

**_all或*删除全部索引时要谨慎**

为了防止误删除，可以设置elasticsearch.yml属性action.destructive_requires_name为true，禁止使用通配符或_all删除索引，必须使用名称或者别名才能删除该索引

# 获取索引

```
GET /secisland/

#返回下面的结果

{
  "secisland": {
    "aliases": {},
    "mappings": {
      "secilog": {
        "properties": {
          "computer": {
            "type": "string"
          },
          "message": {
            "type": "string"
          }
        }
      }
    },
    "settings": {
      "index": {
        "creation_date": "1497160695057",
        "uuid": "kVBKHTtCTlaExNhfHaw6RQ",
        "number_of_replicas": "1",
        "number_of_shards": "5",
        "version": {
          "created": "2040299"
        }
      }
    },
    "warmers": {}
  }
}

```

上面的示例获取名为secisland的索引，获取索引需要指定索引名称，别名或者通配符

获取索引可以使用通配符获取多个索引，或者使用_all或*号获取全部索引

```
GET /_all

GET /secis*

```

返回结果过滤，可以自定义返回结果的属性

```
GET /secisland/_settings,_mappings


#返回结果
{
  "secisland": {
    "settings": {
      "index": {
        "creation_date": "1497160695057",
        "uuid": "kVBKHTtCTlaExNhfHaw6RQ",
        "number_of_replicas": "1",
        "number_of_shards": "5",
        "version": {
          "created": "2040299"
        }
      }
    },
    "mappings": {
      "secilog": {
        "properties": {
          "computer": {
            "type": "string"
          },
          "message": {
            "type": "string"
          }
        }
      }
    }
  }
}

```

上面的示例只是返回secisland索引的settings和mappings属性，可配置的属性包括：_settings,_mappings,_warmers,_aliases



如果索引不存在，系统会返回一个错误内容，例如：

```
GET /xxxxx

{
  "error": {
    "root_cause": [
      {
        "type": "index_not_found_exception",
        "reason": "no such index",
        "index": "xxxxx",
        "resource.type": "index_or_alias",
        "resource.id": "xxxxx"
      }
    ],
    "type": "index_not_found_exception",
    "reason": "no such index",
    "index": "xxxxx",
    "resource.type": "index_or_alias",
    "resource.id": "xxxxx"
  },
  "status": 404
}

```

# 打开或关闭索引

打开或关闭索引接口允许关闭一个打开的索引或者打开一个关闭的索引，关闭的索引只能显示索引元数据信息，不能够进行读写操作

```
POST /secisland/_close
POST /secisland/_open

```

可以同时打开或者关闭多个索引，如果指向不存在的索引会抛出错误，可以使用配置ignore_unavailable=true，不显示异常

全部索引可以使用_all打开或者关闭，或者使用通配符表示全部

设置config/elasticsearch.yml属性action.destructive_requires_name为true，禁止使用通配符或者_all标识索引

因为关闭的索引会继续占用磁盘空间而不能使用，所以关闭的索引接口可能造成磁盘空间的浪费

禁止使用关闭索引功能，可以设置settingcluster.indices.close.enable为false，默认是true

