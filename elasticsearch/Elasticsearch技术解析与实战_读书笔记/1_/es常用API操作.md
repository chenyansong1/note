常用API操作

|http方法|数据处理|说明|
|--------|--------|-----|
|POST|Create|新增一个没有ID的资源|
|GET|Read|获取一个资源|
|PUT|Upate|更新一个资源，或新增一个含ID的资源（如果ID不存在）|
|DELETE|Delete|删除一个资源|



#  新增索引

```
PUT /secisland?pretty

#返回如下的内容，表示创建库完成：
{
  "acknowledged": true
}

```


# 查看所有的索引

```
#查看所有的索引的信息（v表示有表头）
GET _cat/indices?v

health status index     pri rep docs.count docs.deleted store.size pri.store.size 
yellow open   megacorp    5   1          1            0      4.7kb          4.7kb 
yellow open   my_index    1   1          4            0      3.3kb          3.3kb 
yellow open   index_cys   5   1          0            0       795b           795b 
yellow open   .kibana     1   1          1            0      3.1kb          3.1kb 


GET _cat/indices

yellow open megacorp  5 1 1 0 4.7kb 4.7kb 
yellow open my_index  1 1 4 0 3.3kb 3.3kb 
yellow open index_cys 5 1 0 0  795b  795b 
yellow open .kibana   1 1 1 0 3.1kb 3.1kb 

```

以index_cys索引为例，主分片是5个，监看度是黄色，文档数为0，状态是活动

也可以在head插件中查看
![](/images/es/cat_indices.jpg)


# 插入document数据

```
PUT /secisland/secilog/1/
{
  "computer":"secisland",
  "message":"secisland is an security company!"
}

#返回结果：
{
  "_index": "secisland",
  "_type": "secilog",
  "_id": "1",
  "_version": 1,
  "_shards": {
    "total": 2,
    "successful": 1,
    "failed": 0
  },
  "created": true #表示创建成功
}

```


# 修改文档

```
POST /secisland/secilog/1/_update/
{
  "doc":{
    "computer":"secisland",
    "message":"seciland is an security computer , It provides log analysis products"
  }
}

#注意：doc是关键字

#返回结果如下：
{
  "_index": "secisland",
  "_type": "secilog",
  "_id": "1",
  "_version": 2,
  "_shards": {
    "total": 2,
    "successful": 1,
    "failed": 0
  }
}

```

# 查询文档

```
GET /secisland/secilog/1

#返回结果如下：
{
  "_index": "secisland",
  "_type": "secilog",
  "_id": "1",
  "_version": 2,
  "found": true,
  "_source": {
    "computer": "secisland",
    "message": "seciland is an security computer , It provides log analysis products"
  }
}

#注意，原文档的内容都在_source字段中
```


# 删除文档

```
DELETE /secisland/secilog/1

#返回结果如下：
{
  "found": true,
  "_index": "secisland",
  "_type": "secilog",
  "_id": "1",
  "_version": 3,
  "_shards": {
    "total": 2,
    "successful": 1,
    "failed": 0
  }
}

```


# 删除索引

```
DELETE /secisland

#返回结果如下：
{
  "acknowledged": true
}
```






