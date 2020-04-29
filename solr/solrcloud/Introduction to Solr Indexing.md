solr索引的数据源有很多种：xml，cvs,数据库数据表，words，PDF等

# curl添加doc

generating a HTTP POST request

通过curl添加索引的方式,这种方式不会立即被搜索到
```
curl http://localhost:8983/solr/my_collection/update -H "Content-Type: text/xml" --data-binary '
<add>
  <doc>
    <field name="authors">Patrick Eagar</field>
    <field name="subject">Sports</field>
    <field name="dd">796.35</field>
    <field name="isbn">0002166313</field>
    <field name="yearpub">1982</field>
    <field name="publisher">Collins</field>
  </doc>
</add>'
```

类似的还有将xml内容放入文件中

```
curl http://localhost:8983/solr/my_collection/update -H "Content-Type: text/xml" --data-binary @myfile.xml
```

# json添加doc

```
curl -X POST -H 'Content-Type: application/json' 'http://localhost:8983/solr/my_collection/update/json/docs' --data-binary '
{
  "id": "1",
  "title": "Doc 1"
}'
```

添加多个json文档

```
curl -X POST -H 'Content-Type: application/json' 'http://localhost:8983/solr/my_collection/update' --data-binary '
[
  {
    "id": "1",
    "title": "Doc 1"
  },
  {
    "id": "2",
    "title": "Doc 2"
  }
]'

```

```
curl 'http://localhost:8983/solr/techproducts/update?commit=true' --data-binary @example/exampledocs/books.json -H 'Content-type:application/json'
```

发送一组命令

```

curl -X POST -H 'Content-Type: application/json' 'http://localhost:8983/solr/my_collection/update' --data-binary '
{
  "add": {
    "doc": {
      "id": "DOC1",
      "my_field": 2.3,
      "my_multivalued_field": [ "aaa", "bbb" ]   
    }
  },
  "add": {
    "commitWithin": 5000, 
    "overwrite": false,  
    "doc": {
      "f1": "v1", 
      "f1": "v2"
    }
  },

  "commit": {},
  "optimize": { "waitSearcher":false },

  "delete": { "id":"ID" },  
  "delete": { "query":"QUERY" } 
}'
```

添加子文档的形式

```

[
  {
    "id": "1",
    "title": "Solr adds block join support",
    "content_type": "parentDocument",
    "_childDocuments_": [
      {
        "id": "2",
        "comments": "SolrCloud supports it too!"
      }
    ]
  },
  {
    "id": "3",
    "title": "New Lucene and Solr release is out",
    "content_type": "parentDocument",
    "_childDocuments_": [
      {
        "id": "4",
        "comments": "Lots of new features"
      }
    ]
  }
]

```

















