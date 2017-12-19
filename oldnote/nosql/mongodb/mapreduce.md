---
title: mapreduce
categories: mongodb   
toc: true  
tags: [mongodb]
---


# 官网
https://docs.mongodb.com/manual/reference/method/db.collection.mapReduce/

https://docs.mongodb.com/manual/core/map-reduce/
# 1.语法
```
db.collection.mapReduce(
                         <map>,                             #map函数
                         <reduce>,                        #reduce函数
                         {
                           out: <collection>,         #指定输出结果到那张表上
                           query: <document>,        #在map之前的过滤
                           sort: <document>,
                           limit: <number>,
                           finalize: <function>,         #最终处理函数
                           scope: <document>,
                           jsMode: <boolean>,
                           verbose: <boolean>,
                           bypassDocumentValidation: <boolean>
                         }
)

```


# 2.mapreduce的处理过程

![](http://ols7leonh.bkt.clouddn.com//assert/img/nosql/mongodb/mapreduce/1.png)


# 3.参数说明
## 3.1.map函数
```
function() {
   ...
   emit(key, value);
}

#1.当遍历每一篇文档的时候，都会调用map函数
#2.map函数中维护着一个this，他就是指向文档的指针，可以通过this来引用文档的属性
#3.在map函数中调用：emit(key,value) ==========》key相当于一个分组，而value相当于一个组中的一个值
#4.map函数的输入就是每一篇文档，输出就是一个key-value键值对，一个key对应多个value（即一个数组）


#下面是一个订单的一条记录（即文档document）的格式
{
     _id: ObjectId("50a8240b927d5d8b5891743c"),
     cust_id: "abc123",                                        #订单号
     ord_date: new Date("Oct 04, 2012"),            #订单日期
     status: 'A',                                #状态
     price: 25,                                #总价格
     items: [ { sku: "mmm", qty: 5, price: 2.5 },            #具体的订单项
              { sku: "nnn", qty: 5, price: 2.5 } ]
}


#Example_1
function() {
    if (this.status == 'A')                        #在遍历所有文档（记录）的时候，都会调用一次map函数，this.status就是代表的一个文档对象的属性
        emit(this.cust_id, 1);                  #如果条件成立就向组中添加一个1（即统计一个组中的数量）
}


#Example_2（统计某种商品的购买次数）
function() {
    this.items.forEach(function(item){ emit(item.sku, 1); });
}

```

## 3.2.reduce函数
```
#格式
function(key, values) {
   ...
   return result;                            #合并每一个key对应的分组
}



```

## 3.3.finalize 最后处理函数
```
#格式
function(key, reducedValue) {        '输入是reduce处理之后的分组的key，和reduce数组之后的value'
   ...
   return modifiedObject;                '最后的输出结果'
}


```
## 3.4.选项out
```
#格式1（字符串）
out: <collectionName>


#格式2（文档对象）
out: { <action>: <collectionName>
        [, db: <dbName>]
        [, sharded: <boolean> ]
        [, nonAtomic: <boolean> ] }

<action>: Specify one of the following actions:
replace        '替换已经存在的'
                           Replace the contents of the <collectionName> if the collection with the <collectionName> exists.
merge        '合并'
                           Merge the new result with the existing result if the output collection already exists. If an existing document has the same key as the new result, overwrite that existing document.
reduce


db：'指定存入哪个库中，默认是当前库'
sharded:'是否分片'







```

## 3.5.选项query
>在map之前的过滤

# 4.实例1
```
#orders表的一条记录的格式（document）
{
     _id: ObjectId("50a8240b927d5d8b5891743c"),
     cust_id: "abc123",
     ord_date: new Date("Oct 04, 2012"),
     status: 'A',
     price: 25,
     items: [ { sku: "mmm", qty: 5, price: 2.5 },
              { sku: "nnn", qty: 5, price: 2.5 } ]
}


#map函数
var mapFunction1 = function() {
                       emit(this.cust_id, this.price);                        #以cust_id为key进行分组
                   };



#reduce函数
var reduceFunction1 = function(keyCustId, valuesPrices) {                #计算每组key对应的总的price
                          return Array.sum(valuesPrices);
                      };


#db.orders.mapReduce
db.orders.mapReduce(
                     mapFunction1,
                     reduceFunction1,
                     { out: "map_reduce_example" }                    #对应的结果输出到map_reduce_example表中
                   )

#结果
> db.map_reduce_example.find();
{ "_id" : "abc123", "value" : 25 }
>
 
```
# 5.实例2
```
#orders表的一条记录的格式（document）
{
     _id: ObjectId("50a8240b927d5d8b5891743c"),
     cust_id: "abc123",
     ord_date: new Date("Oct 04, 2012"),
     status: 'A',
     price: 25,
     items: [ { sku: "mmm", qty: 5, price: 2.5 },
              { sku: "nnn", qty: 5, price: 2.5 } ]
}
 
 
#map函数
var mapFunction2 = function() {
                       for (var idx = 0; idx < this.items.length; idx++) {
                           var key = this.items[idx].sku;
                           var value = {
                                         count: 1,
                                         qty: this.items[idx].qty
                                       };
                           emit(key, value);                    #这里的key，value都不是文档的属性，而是通过计算得到的，key是商品的名称，value是一个文档
                       }
                    };

'map函数最后输出的是key，values (是一个文档对象数组)'

#reduce函数
var reduceFunction2 = function(keySKU, countObjVals) {    '输入的是key，values (是一个文档对象数组)'
                     reducedVal = { count: 0, qty: 0 };
 
                     for (var idx = 0; idx < countObjVals.length; idx++) {
                         reducedVal.count += countObjVals[idx].count;
                         reducedVal.qty += countObjVals[idx].qty;
                     }
 
                     return reducedVal;                       '输出时一个文档对象'
                  };

#最后处理函数
var finalizeFunction2 = function (key, reducedVal) {        '对reduce函数的输出的文档对象进行最后的处理'
 
                       reducedVal.avg = reducedVal.qty/reducedVal.count;                #给reducedVal文档对象添加了一个avg属性
     
                       return reducedVal;                                        #返回文档对象
     
                    };


#db.orders.mapReduce
db.orders.mapReduce( mapFunction2,
                     reduceFunction2,
                     {
                       out: { merge: "map_reduce_example" },
                       query: { ord_date:
                                  { $gt: new Date('01/01/2012') }
                              },
                       finalize: finalizeFunction2
                     }
                   )

#结果
> db.map_reduce_example.find();
{ "_id" : "mmm", "value" : { "count" : 1, "qty" : 5, "avg" : 5 } }
{ "_id" : "nnn", "value" : { "count" : 1, "qty" : 5, "avg" : 5 } }


#如果没有：finalizeFunction2
{ "_id" : "mmm", "value" : { "count" : 1, "qty" : 5 } }
{ "_id" : "nnn", "value" : { "count" : 1, "qty" : 5 } }

```

 


