---
title: elasticsearch的嵌套聚合，下钻分析，聚合分析
categories: elasticsearch   
toc: true  
tag: [elasticsearch]
---

# 分组

第一个分析需求：计算每个tag下的商品数量
```
GET /ecommerce/product/_search
{
  "aggs": {//aggregates聚合
    "group_by_tags": {//取的名字
      "terms": { "field": "tags" }//分组的字段
    }
  }
}


```

将文本field的fielddata属性设置为true
```
PUT /ecommerce/_mapping/product
{
  "properties": {
    "tags": {
      "type": "text",
      "fielddata": true
    }
  }
}

```


```
GET /ecommerce/product/_search
{
  "size": 0,//为0就不会打印一些详细的信息了
  "aggs": {
    "group_by_tags": {
      "terms": { "field": "tags" }
    }
  }
}

//打印结果
{
  "took": 20,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "failed": 0
  },
  "hits": {
    "total": 4,
    "max_score": 0,
    "hits": []
  },
  "aggregations": {
    "group_by_tags": {
      "doc_count_error_upper_bound": 0,
      "sum_other_doc_count": 0,
      "buckets": [	//这里是每个组的结果
        {
          "key": "fangzhu",
          "doc_count": 2
        },
        {
          "key": "meibai",
          "doc_count": 2
        },
        {
          "key": "qingxin",
          "doc_count": 1
        }
      ]
    }
  }
}

```

# 对匹配的结果进行分组


第二个聚合分析的需求：对名称中包含yagao的商品，计算每个tag下的商品数量

```
GET /ecommerce/product/_search
{
  "size": 0,
  "query": {
    "match": {
      "name": "yagao"
    }
  },
  "aggs": {
    "all_tags": {
      "terms": {
        "field": "tags"
      }
    }
  }
}

```

# 求组内的平均值

第三个聚合分析的需求：先分组，再算每组的平均值，计算每个tag下的商品的平均价格
```
GET /ecommerce/product/_search
{
    "size": 0,
    "aggs" : {
        "group_by_tags" : {//先分组
            "terms" : { "field" : "tags" },
            "aggs" : {
                "avg_price" : {//在组内进行求avg
                    "avg" : { "field" : "price" }
                }
            }
        }
    }
}


//打印结果
{
  "took": 8,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "failed": 0
  },
  "hits": {
    "total": 4,
    "max_score": 0,
    "hits": []
  },
  "aggregations": {
    "group_by_tags": {
      "doc_count_error_upper_bound": 0,
      "sum_other_doc_count": 0,
      "buckets": [//分组的结果
        {
          "key": "fangzhu",
          "doc_count": 2,
          "avg_price": {//组内的平均值
            "value": 27.5
          }
        },
        {
          "key": "meibai",
          "doc_count": 2,
          "avg_price": {
            "value": 40
          }
        },
        {
          "key": "qingxin",
          "doc_count": 1,
          "avg_price": {
            "value": 40
          }
        }
      ]
    }
  }
}

```

# 对分组之后的数据进行排序(组与组间排序)

第四个数据分析需求：计算每个tag下的商品的平均价格，并且按照平均价格降序排序

```
GET /ecommerce/product/_search
{
    "size": 0,
    "aggs" : {
        "all_tags" : {
            "terms" : { 
				"field" : "tags", 
				"order": { //指定组内排序的字段
					"avg_price": "desc" 
				} 
			},
            "aggs" : {
                "avg_price" : {//组内排序
                    "avg" : { "field" : "price" }
                }
            }
        }
    }
}

```

# 

第五个数据分析需求：按照指定的价格范围区间进行分组，然后在每组内再按照tag进行分组，最后再计算每组的平均价格

```
GET /ecommerce/product/_search
{
  "size": 0,
  "aggs": {
    "group_by_price": {
      "range": {
        "field": "price",
        "ranges": [//指定分组的区间
          {
            "from": 0,
            "to": 20
          },
          {
            "from": 20,
            "to": 40
          },
          {
            "from": 40,
            "to": 50
          }
        ]
      },
      "aggs": {
        "group_by_tags": {//每个分组内又使用tags字段进行分组
          "terms": {
            "field": "tags"
          },
          "aggs": {//组内求平均值
            "average_price": {
              "avg": {
                "field": "price"
              }
            }
          }
        }
      }
    }
  }
}

```
