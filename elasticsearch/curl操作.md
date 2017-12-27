http://www.ruanyifeng.com/blog/2017/08/elasticsearch.html


```
curl -H "Content-Type: application/json" -XGET
'http://localhost:9200/social-*/_search' -d '{
  "query": {
    "match": {
      "message": "myProduct"
    }
  },
  "aggregations": {
    "top_10_states": {
      "terms": {
        "field": "state",
        "size": 10
      }
    }
  }
}'

```

# 查询分页

```
curl  -XGET '172.16.14.21:9200/genlog/_search'  -d '      
{
  "query": {
    "range": {
      "firstrecvtime": {
        "gte": 1508309711000,
        "lte": 1508309711000
      }
    }
  },
  "from": 1,
  "size": 1
}'
```

# 查询包含指定字段

```
GET /genlog/_search
{
  "_source": {

        "includes": [ "recordid", "reportapp" ]

    },

  "query": {

    "range": {

      "firstrecvtime": {

        "gte": 1508309711000,

        "lte": 1508309711000

      }

    }

  },

  "from": 1,

  "size": 1

}

```