---
title: 搜索_基于dis_max实现best-fields策略进行多字段搜索
categories: elasticsearch   
toc: true  
tag: [elasticsearch]
---




1、为帖子数据增加content字段

```
POST /forum/article/_bulk
{ "update": { "_id": "1"} }
{ "doc" : {"content" : "i like to write best elasticsearch article"} }
{ "update": { "_id": "2"} }
{ "doc" : {"content" : "i think java is the best programming language"} }
{ "update": { "_id": "3"} }
{ "doc" : {"content" : "i am only an elasticsearch beginner"} }
{ "update": { "_id": "4"} }
{ "doc" : {"content" : "elasticsearch and hadoop are all very good solution, i am a beginner"} }
{ "update": { "_id": "5"} }
{ "doc" : {"content" : "spark is best big data solution based on scala ,an programming language similar to java"} }
```

2、搜索title或content中包含java或solution的帖子

下面这个就是multi-field搜索，多字段搜索

```
GET /forum/article/_search
{
    "query": {
        "bool": {
            "should": [
                { "match": { "title": "java solution" }},
                { "match": { "content":  "java solution" }}
            ]
        }
    }
}
```

3、结果分析

期望的是doc5，结果是doc2,doc4排在了前面

计算每个document的relevance score：每个query的分数，乘以matched query数量，除以总query数量

算一下doc4的分数

{ "match": { "title": "java solution" }}，针对doc4，是有一个分数的
{ "match": { "content":  "java solution" }}，针对doc4，也是有一个分数的

所以是两个分数加起来，比如说，1.1 + 1.2 = 2.3
matched query数量 = 2
总query数量 = 2

2.3 * 2 / 2 = 2.3

算一下doc5的分数

{ "match": { "title": "java solution" }}，针对doc5，是没有分数的
{ "match": { "content":  "java solution" }}，针对doc5，是有一个分数的

所以说，只有一个query是有分数的，比如2.3
matched query数量 = 1
总query数量 = 2

2.3 * 1 / 2 = 1.15

doc5的分数 = 1.15 < doc4的分数 = 2.3


4、best fields策略，dis_max

best fields策略，就是说，搜索到的结果，应该是某一个field中匹配到了尽可能多的关键词，被排在前面；而不是尽可能多的field匹配到了少数的关键词，排在了前面

dis_max语法，直接取多个query中，分数最高的那一个query的分数即可

{ "match": { "title": "java solution" }}，针对doc4，是有一个分数的，1.1
{ "match": { "content":  "java solution" }}，针对doc4，也是有一个分数的，1.2
取最大分数，1.2

{ "match": { "title": "java solution" }}，针对doc5，是没有分数的
{ "match": { "content":  "java solution" }}，针对doc5，是有一个分数的，2.3
取最大分数，2.3

然后doc4的分数 = 1.2 < doc5的分数 = 2.3，所以doc5就可以排在更前面的地方，符合我们的需要

```
GET /forum/article/_search
{
    "query": {
        "dis_max": {
            "queries": [
                { "match": { "title": "java solution" }},
                { "match": { "content":  "java solution" }}
            ]
        }
    }
}

```


5、dis_max只取某一个query最大的分数，完全不考虑其他query的分数

6、使用tie_breaker将其他query的分数也考虑进去

tie_breaker参数的意义，在于说，将其他query的分数，乘以tie_breaker，然后综合与最高分数的那个query的分数，综合在一起进行计算
除了取最高分以外，还会考虑其他的query的分数
tie_breaker的值，在0~1之间，是个小数，就ok

```
GET /forum/article/_search
{
    "query": {
        "dis_max": {
            "queries": [
                { "match": { "title": "java beginner" }},
                { "match": { "body":  "java beginner" }}
            ],
            "tie_breaker": 0.3
        }
    }
}
```



```
GET /forum/article/_search
{
  "query": {
    "multi_match": {
        "query":                "java solution",
        "type":                 "best_fields", 
        "fields":               [ "title^2", "content" ],
        "tie_breaker":          0.3,
        "minimum_should_match": "50%" 
    }
  } 
}

GET /forum/article/_search
{
  "query": {
    "dis_max": {
      "queries":  [
        {
          "match": {
            "title": {
              "query": "java beginner",
              "minimum_should_match": "50%",
	      "boost": 2
            }
          }
        },
        {
          "match": {
            "body": {
              "query": "java beginner",
              "minimum_should_match": "30%"
            }
          }
        }
      ],
      "tie_breaker": 0.3
    }
  } 
}
```

minimum_should_match，主要是用来干嘛的？
去长尾，long tail
长尾，比如你搜索5个关键词，但是很多结果是只匹配1个关键词的，其实跟你想要的结果相差甚远，这些结果就是长尾
minimum_should_match，控制搜索结果的精准度，只有匹配一定数量的关键词的数据，才能返回





