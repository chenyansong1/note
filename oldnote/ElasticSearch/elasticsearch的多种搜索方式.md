---
title: elasticsearch的多种搜索方式
categories: elasticsearch   
toc: true  
tag: [elasticsearch]
---




# query string search

```
#搜索全部商品(使用_search的API)
GET /ecommerce/product/_search


took：耗费了几毫秒
timed_out：是否超时，这里是没有
_shards：数据拆成了5个分片，所以对于搜索请求，会打到所有的primary shard（或者是它的某个replica shard也可以）
hits.total：查询结果的数量，3个document
hits.max_score：score的含义，就是document对于一个search的相关度的匹配分数，越相关，就越匹配，分数也高
hits.hits：包含了匹配搜索的document的详细数据

{
  "took": 2,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "failed": 0
  },
  "hits": {
    "total": 3,
    "max_score": 1,
    "hits": [
      {
        "_index": "ecommerce",
        "_type": "product",
        "_id": "2",
        "_score": 1,
        "_source": {
          "name": "jiajieshi yagao",
          "desc": "youxiao fangzhu",
          "price": 25,
          "producer": "jiajieshi producer",
          "tags": [
            "fangzhu"
          ]
        }
      },
      {
        "_index": "ecommerce",
        "_type": "product",
        "_id": "1",
        "_score": 1,
        "_source": {
          "name": "gaolujie yagao",
          "desc": "gaoxiao meibai",
          "price": 30,
          "producer": "gaolujie producer",
          "tags": [
            "meibai",
            "fangzhu"
          ]
        }
      },
      {
        "_index": "ecommerce",
        "_type": "product",
        "_id": "3",
        "_score": 1,
        "_source": {
          "name": "zhonghua yagao",
          "desc": "caoben zhiwu",
          "price": 40,
          "producer": "zhonghua producer",
          "tags": [
            "qingxin"
          ]
        }
      }
    ]
  }
}

```

搜索商品名称中包含"yagao"的商品，而且按照售价降序排序

```
GET /ecommerce/product/_search?q=name:yagao&sort=price:desc
```

query string search的由来，因为search参数都是以http请求的query string来附带的

适用于临时的在命令行使用一些工具，比如curl，快速的发出请求，来检索想要的信息；但是如果查询请求很复杂，是很难去构建的
在生产环境中，几乎很少使用query string search

------------------------

## query DSL

DSL：Domain Specified Language，特定领域的语言
http request body：请求体，可以用json的格式来构建查询语法，比较方便，可以构建各种复杂的语法，比query string search肯定强大多了

查询所有的商品
```


GET /ecommerce/product/_search
{
  "query": { "match_all": {} }
}
//match_all 表示查询所有

```

查询名称包含yagao的商品，同时按照价格降序排序

```
GET /ecommerce/product/_search
{
    "query" : {
        "match" : {
            "name" : "yagao"
        }
    },
    "sort": [
        { "price": "desc" }	//指定排序的字段,这里可以有多个字段
    ]
}

```


分页查询商品，总共3条商品，假设每页就显示1条商品，现在显示第2页，所以就查出来第2个商品

```
GET /ecommerce/product/_search
{
  "query": { "match_all": {} },//首先使用match_all查询所有的数据
  "from": 1,//从第一个结果开始(结果从0开始统计的)
  "size": 1//取一条记录
}

```

指定要查询出来商品的名称和价格就可以

```
GET /ecommerce/product/_search
{
  "query": { "match_all": {} },
  "_source": ["name", "price"]	//只是查询指定的字段
}
```
更加适合生产环境的使用，可以构建复杂的查询

-------------------

# query filter

搜索商品名称包含yagao，而且售价大于25元的商品
```
GET /ecommerce/product/_search
{
    "query" : {
        "bool" : {	//bool中可以封装多个查询条件
            "must" : {
                "match" : {
                    "name" : "yagao" 
                }
            },
            "filter" : {
                "range" : {//过滤的是范围
                    "price" : { "gt" : 25 } 
                }
            }
        }
    }
}

```



# full-text search（全文检索）

```
GET /ecommerce/product/_search
{
    "query" : {
        "match" : {
            "producer" : "yagao producer"//会将yagao producer拆分开来,然后查询匹配到这两个词的所有的记录,这里有一个匹配的相关度的概念
        }
    }
}
```


producer这个字段，会先被拆解，建立倒排索引

word		|ids
------------|--------
special		|4
yagao		|4
producer	|1,2,3,4
gaolujie	|1
zhognhua	|3
jiajieshi	|2

yagao producer ---> yagao和producer

```
{
  "took": 4,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "failed": 0
  },
  "hits": {
    "total": 4,
    "max_score": 0.70293105,
    "hits": [
      {
        "_index": "ecommerce",
        "_type": "product",
        "_id": "4",
        "_score": 0.70293105,	//相关度分数
        "_source": {
          "name": "special yagao",
          "desc": "special meibai",
          "price": 50,
          "producer": "special yagao producer",
          "tags": [
            "meibai"
          ]
        }
      },
      {
        "_index": "ecommerce",
        "_type": "product",
        "_id": "1",
        "_score": 0.25811607,//相关度分数
        "_source": {
          "name": "gaolujie yagao",
          "desc": "gaoxiao meibai",
          "price": 30,
          "producer": "gaolujie producer",
          "tags": [
            "meibai",
            "fangzhu"
          ]
        }
      },
      {
        "_index": "ecommerce",
        "_type": "product",
        "_id": "3",
        "_score": 0.25811607,
        "_source": {
          "name": "zhonghua yagao",
          "desc": "caoben zhiwu",
          "price": 40,
          "producer": "zhonghua producer",
          "tags": [
            "qingxin"
          ]
        }
      },
      {
        "_index": "ecommerce",
        "_type": "product",
        "_id": "2",
        "_score": 0.1805489,
        "_source": {
          "name": "jiajieshi yagao",
          "desc": "youxiao fangzhu",
          "price": 25,
          "producer": "jiajieshi producer",
          "tags": [
            "fangzhu"
          ]
        }
      }
    ]
  }
}

```


# phrase search（短语搜索）

跟全文检索相对应，相反，全文检索会将输入的搜索串拆解开来，去倒排索引里面去一一匹配，只要能匹配上任意一个拆解后的单词，就可以作为结果返回
phrase search，要求输入的搜索串，必须在指定的字段文本中，**完全包含一模一样的**，才可以算匹配，才能作为结果返回

```
GET /ecommerce/product/_search
{
    "query" : {
        "match_phrase" : {
            "producer" : "yagao producer"
        }
    }
}

//打印结果
{
  "took": 11,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "failed": 0
  },
  "hits": {
    "total": 1,
    "max_score": 0.70293105,
    "hits": [
      {
        "_index": "ecommerce",
        "_type": "product",
        "_id": "4",
        "_score": 0.70293105,
        "_source": {
          "name": "special yagao",
          "desc": "special meibai",
          "price": 50,
          "producer": "special yagao producer",
          "tags": [
            "meibai"
          ]
        }
      }
    ]
  }
}

```


# highlight search（高亮搜索结果）

```
GET /ecommerce/product/_search
{
    "query" : {
        "match" : {
            "producer" : "producer"
        }
    },
    "highlight": {
        "fields" : {
            "producer" : {}
        }
    }
}

```