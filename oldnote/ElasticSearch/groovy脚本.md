---
title: groovy脚本
categories: elasticsearch   
toc: true  
tag: [elasticsearch]
---



es，其实是有个内置的脚本支持的，可以基于groovy脚本实现各种各样的复杂操作
基于groovy脚本，如何执行partial update


```
PUT /test_index/test_type/11
{
  "num": 0,
  "tags": []
}

```

（1）内置脚本

```
#post更新
POST /test_index/test_type/11/_update
{
   "script" : "ctx._source.num+=1"  #ctx._source.num是拿到字段
}

#get
{
  "_index": "test_index",
  "_type": "test_type",
  "_id": "11",
  "_version": 2,
  "found": true,
  "_source": {
    "num": 1,   #原来是0，执行脚本之后，变成1
    "tags": []
  }
}

```

（2）外部脚本

需要在elasticsearch-2.4.3/config/scripts 下新建一个脚本，取名为test-add-tags.groovy

```
#test-add-tags.groovy 

ctx._source.tags+=new_tag


#更新
POST /test_index/test_type/11/_update
{
  "script": {
    "lang": "groovy",    #脚本类型
    "file": "test-add-tags",   #脚本文件
    "params": {
      "new_tag": "tag1"     #参数
    }
  }
}

```

（3）用脚本删除文档

```
ctx.op = ctx._source.num == count ? 'delete' : 'none'

POST /test_index/test_type/11/_update
{
  "script": {
    "lang": "groovy",
    "file": "test-delete-document",
    "params": {
      "count": 1
    }
  }
}

```

（4）upsert操作

```
POST /test_index/test_type/11/_update
{
  "doc": {
    "num": 1
  }
}

{
  "error": {
    "root_cause": [
      {
        "type": "document_missing_exception",
        "reason": "[test_type][11]: document missing",
        "index_uuid": "6m0G7yx7R1KECWWGnfH1sw",
        "shard": "4",
        "index": "test_index"
      }
    ],
    "type": "document_missing_exception",
    "reason": "[test_type][11]: document missing",
    "index_uuid": "6m0G7yx7R1KECWWGnfH1sw",
    "shard": "4",
    "index": "test_index"
  },
  "status": 404
}


```


如果指定的document不存在，就执行upsert中的初始化操作；如果指定的document存在，就执行doc或者script指定的partial update操作

```
POST /test_index/test_type/11/_update
{
   "script" : "ctx._source.num+=1",
   "upsert": {      #如果不存在就初始化
       "num": 0,
       "tags": []
   }
}

```


