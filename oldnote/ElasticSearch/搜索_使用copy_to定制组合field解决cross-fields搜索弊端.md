---
title: 搜索_使用copy_to定制组合field解决cross-fields搜索弊端
categories: elasticsearch   
toc: true  
tag: [elasticsearch]
---





上一讲，我们其实说了，用most_fields策略，去实现cross-fields搜索，有3大弊端，而且搜索结果也显示出了这3大弊端

第一个办法：用copy_to，将多个field组合成一个field

问题其实就出在有多个field，有多个field以后，就很尴尬，我们只要想办法将一个标识跨在多个field的情况，合并成一个field即可。比如说，一个人名，本来是first_name，last_name，现在合并成一个full_name，不就ok了吗。。。。。

```
PUT /forum/_mapping/article
{
  "properties": {
      "new_author_first_name": {
          "type":     "string",
          "copy_to":  "new_author_full_name" 
      },
      "new_author_last_name": {
          "type":     "string",
          "copy_to":  "new_author_full_name" 
      },
      "new_author_full_name": {
          "type":     "string"
      }
  }
}

#new_author_full_name会作为一个隐藏的字段，使用_search去搜索的时候是不会显示，他就像_all字段一样

```

用了这个copy_to语法之后，就可以将多个字段的值拷贝到一个字段中，并建立倒排索引

```
POST /forum/article/_bulk
{ "update": { "_id": "1"} }
{ "doc" : {"new_author_first_name" : "Peter", "new_author_last_name" : "Smith"} }		--> Peter Smith
{ "update": { "_id": "2"} }	
{ "doc" : {"new_author_first_name" : "Smith", "new_author_last_name" : "Williams"} }		--> Smith Williams
{ "update": { "_id": "3"} }
{ "doc" : {"new_author_first_name" : "Jack", "new_author_last_name" : "Ma"} }			--> Jack Ma
{ "update": { "_id": "4"} }
{ "doc" : {"new_author_first_name" : "Robbin", "new_author_last_name" : "Li"} }			--> Robbin Li
{ "update": { "_id": "5"} }
{ "doc" : {"new_author_first_name" : "Tonny", "new_author_last_name" : "Peter Smith"} }		--> Tonny Peter Smith

#使用copy_to去搜索
GET /forum/article/_search
{
  "query": {
    "match": {
      "new_author_full_name":       "Peter Smith"
    }
  }
}
```


问题1：只是找到尽可能多的field匹配的doc，而不是某个field完全匹配的doc --> 解决，最匹配的document被最先返回

问题2：most_fields，没办法用minimum_should_match去掉长尾数据，就是匹配的特别少的结果 --> 解决，可以使用minimum_should_match去掉长尾数据

问题3：TF/IDF算法，比如Peter Smith和Smith Williams，搜索Peter Smith的时候，由于first_name中很少有Smith的，所以query在所有document中的频率很低，得到的分数很高，可能Smith Williams反而会排在Peter Smith前面 --> 解决，Smith和Peter在一个field了，所以在所有document中出现的次数是均匀的，不会有极端的偏差



