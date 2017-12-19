---
title: mongodb的CRUD
categories: mongodb   
toc: true  
tags: [mongodb]
---



mongodb的官方文档
```
#在文档上都有详细的示例
https://docs.mongodb.com/manual/
```

# 1.增加
```
#语法
db.collectionName.insert(document)

#增加单篇文档
> db.student.insert({sn:'001',name:'xiaoming'})
WriteResult({ "nInserted" : 1 })
> db.student.find()
{ "_id" : ObjectId("57ff1837abf4c31796f90449"), "sn" : "001", "name" : "xiaoming" }
>


#增加单篇文档,并指定_id
> db.student.insert({_id:2,sn:'002',name:'xiaogang'})
WriteResult({ "nInserted" : 1 })
> db.student.find()
{ "_id" : ObjectId("57ff1837abf4c31796f90449"), "sn" : "001", "name" : "xiaoming" }
{ "_id" : 2, "sn" : "002", "name" : "xiaogang" }
>

#增加多个文档
添加：一个数组对象
> db.student.insert([{_id:3,sn:'003',name:'zhangfei'},{sn:'004',name:'guanyu'}])
BulkWriteResult({
        "writeErrors" : [ ],
        "writeConcernErrors" : [ ],
        "nInserted" : 2,
        "nUpserted" : 0,
        "nMatched" : 0,
        "nModified" : 0,
        "nRemoved" : 0,
        "upserted" : [ ]
})
> db.student.find()
{ "_id" : ObjectId("57ff1837abf4c31796f90449"), "sn" : "001", "name" : "xiaoming" }
{ "_id" : 2, "sn" : "002", "name" : "xiaogang" }
{ "_id" : 3, "sn" : "003", "name" : "zhangfei" }
{ "_id" : ObjectId("57ff18d2abf4c31796f9044a"), "sn" : "004", "name" : "guanyu" }
> 


#插入复杂的结构
> db.student.insert([{_id:4,sn:'003',name:'zhangfei',class:['math','chinese']}])
> db.student.find()
{ "_id" : ObjectId("57ff1837abf4c31796f90449"), "sn" : "001", "name" : "xiaoming" }
{ "_id" : 2, "sn" : "002", "name" : "xiaogang" }
{ "_id" : 3, "sn" : "003", "name" : "zhangfei" }
{ "_id" : ObjectId("57ff18d2abf4c31796f9044a"), "sn" : "004", "name" : "guanyu" }
{ "_id" : 4, "sn" : "003", "name" : "zhangfei", "class" : [ "math", "chinese" ] }
>

```
 


# 2.删除
```
#语法
db.collection.remove(查询表达式, 选项)
#选项是指: {justOne: true/ false}    是否只删一行,默认是false

> db.student.find()
{ "_id" : ObjectId("57ff1837abf4c31796f90449"), "sn" : "001", "name" : "xiaoming" }
{ "_id" : 2, "sn" : "002", "name" : "xiaogang" }
{ "_id" : 3, "sn" : "003", "name" : "zhangfei" }
{ "_id" : ObjectId("57ff18d2abf4c31796f9044a"), "sn" : "004", "name" : "guanyu" }
{ "_id" : 4, "sn" : "003", "name" : "zhangfei", "class" : [ "math", "chinese" ] }
>

> db.student.remove({sn:'002'})        #删除student表中sn属性值为"002"的文档
WriteResult({ "nRemoved" : 1 })
> db.student.find()
{ "_id" : 3, "sn" : "003", "name" : "zhangfei" }
{ "_id" : ObjectId("57ff18d2abf4c31796f9044a"), "sn" : "004", "name" : "guanyu" }
{ "_id" : 4, "sn" : "003", "name" : "zhangfei", "class" : [ "math", "chinese" ] }
>




#删除student表中gender属性为m的文档,只删除1行
> db.student.find();
{ "_id" : ObjectId("58a9088ab1ba35a73d3b4275"), "sn" : "002", "name" : "xiaoming2", "gender" : "m" }
{ "_id" : ObjectId("58a9088db1ba35a73d3b4276"), "sn" : "001", "name" : "xiaoming1", "gender" : "m" }
{ "_id" : ObjectId("58a9089db1ba35a73d3b4277"), "sn" : "003", "name" : "xiaoming3", "gender" : "f" }
> db.student.remove({gender:"m"},true)
WriteResult({ "nRemoved" : 1 })
> db.student.find();
{ "_id" : ObjectId("58a9088db1ba35a73d3b4276"), "sn" : "001", "name" : "xiaoming1", "gender" : "m" }
{ "_id" : ObjectId("58a9089db1ba35a73d3b4277"), "sn" : "003", "name" : "xiaoming3", "gender" : "f" }
>

 

#删除所有的记录
> db.student.find();
{ "_id" : ObjectId("58a8fe12b1ba35a73d3b426e"), "sn" : "001", "name" : "xiaoming" }
{ "_id" : ObjectId("58a903a9b1ba35a73d3b426f"), "sn" : "002", "name" : "xiaoming2" }
{ "_id" : ObjectId("58a903c3b1ba35a73d3b4270"), "sn" : "003", "name" : "xiaoming3", "gender" : "m" }
{ "_id" : ObjectId("58a903ccb1ba35a73d3b4271"), "sn" : "004", "name" : "xiaoming4", "gender" : "m" }
 
> db.student.remove({});
WriteResult({ "nRemoved" : 4 })
> db.student.find();
>


#drop是删除整个表,而remove是删除表中的记录
> db.student.find();
{ "_id" : ObjectId("58a90683b1ba35a73d3b4272"), "sn" : "004", "name" : "xiaoming4", "gender" : "m" }
{ "_id" : ObjectId("58a9068db1ba35a73d3b4273"), "sn" : "001", "name" : "xiaoming1", "gender" : "m" }
{ "_id" : ObjectId("58a90693b1ba35a73d3b4274"), "sn" : "002", "name" : "xiaoming2", "gender" : "m" }
> show collections;
student
user
> db.student.drop();
true
> db.student.find();
> show collections;
user
> 

/*
To delete all records from a table, uses db.tablename.remove().
To drop the table, uses db.tablename.drop().
*/

```

注意:
* 查询表达式依然是个json对象
* 查询表达式匹配的行,将被删除
* 如果不写查询表达式,collections中的所有文档将被删除 




# 3.更新

```
#语法
db.collection.update(查询表达式, 新值, Option)

 
> db.student.find()
{ "_id" : 3, "name" : "zhangfei2222" }
{ "_id" : ObjectId("57ff18d2abf4c31796f9044a"), "sn" : "004", "name" : "guanyu" }
{ "_id" : 4, "sn" : "003", "name" : "zhangfei", "class" : [ "math", "chinese" ] }
 
> db.student.update({name:'zhangfei'},{name:'zhangfei2222'})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
 
> db.student.find()
{ "_id" : 3, "name" : "zhangfei2222" }
{ "_id" : ObjectId("57ff18d2abf4c31796f9044a"), "sn" : "004", "name" : "guanyu" }
{ "_id" : 4, "name" : "zhangfei2222" }                #文档中除了_id没有变之外，其他字段都被覆盖,即新文档直接替换了旧文档,而不是修改


#如果是想修改文档的某列, 使用$set关键字
> db.student.find();
{ "_id" : ObjectId("58a9088db1ba35a73d3b4276"), "sn" : "001", "name" : "xiaoming1", "gender" : "m" }
{ "_id" : ObjectId("58a9089db1ba35a73d3b4277"), "sn" : "003", "name" : "xiaoming3", "gender" : "f" }

> db.student.update({name:"xiaoming3"},{$set:{name:"QQ"}})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })

> db.student.find();
{ "_id" : ObjectId("58a9088db1ba35a73d3b4276"), "sn" : "001", "name" : "xiaoming1", "gender" : "m" }
{ "_id" : ObjectId("58a9089db1ba35a73d3b4277"), "sn" : "003", "name" : "QQ", "gender" : "f" }
> 



 /*
除了$set还有如下的选项:
$inc    #增长
$rename    #重命名
$setOnInsert    #当upsert时,设置字段的值
$set        #设置字段的新值
$unset        #删除指定的列
$push # 将一个数字存入一个数组,分为三种情况,如果该字段存在,则直接将数字存入数组.如果该字段不存在,创建字段并且将数字插入该数组.如果更新的字段不是数组,会报错的.
$pop #删除数组最后一个元素
$pull # 删除数组中的指定的元素,如果删除的字段不是数组,会报错

*/
> db.stu.find();
{ "_id" : ObjectId("58a90e08b1ba35a73d3b4278"), "name" : "wukong", "jingu" : true, "sex" : "m", "age" : 500 }
 
> db.stu.update({name:"wukong"},{$set:{name:"shitou"},$unset:{jingu:1},$rename:{sex:"gender"},$inc:{age:16}});

> db.stu.find();
{ "_id" : ObjectId("58a90e08b1ba35a73d3b4278"), "name" : "shitou", "age" : 516, "gender" : "m" }
>
 

#数组操作
# $push
db.test.find()
{ "_id" : 1, "ary" : [ 1, 2, 3, 4 ] }
{ "_id" : 2, "text" : "test" }
 
db.test.update({_id:1},{$push:{ary:5}}) # 数组存在 直接压入，但是这个地方如果是数组的话就压入一个数组，并非是合并数组中的元素
 
db.test.update({_id:1},{$push:{ary:[8,9,10]}})
 
db.test.find()
{ "_id" : 2, "text" : "test" }
{ "_id" : 1, "ary" : [ 1, 2, 3, 4, 5,[8,9,10] ] } # 由此可见push一次只能插入一个字段,如果想要批量插入的话就缓存pushAll;
 
db.test.update({_id:2},{$push:{ary:6}}) # 数组不存在,创建数组并存入
 
db.test.find()
{ "_id" : 2, "ary" : [ 6 ], "text" : "test" }
{ "_id" : 1, "ary" : [ 1, 2, 3, 4, 5 ] }
 
db.test.update({_id:2},{$push:{text:6}})  # 更新字段存在但不是数组报错
Cannot apply $push/$pushAll modifier to non-array
 
# pop
db.user.update({_id:9},{$pop:{test:0}}) # 这里的test无论传入什么值,都是删掉test数组的最后一个
 
# $pull
db.user.update({_id:9},{$pull:{test:2}}) #这里的test传什么值就删掉什么值




#Option的作用
{upsert:true/false, multi:true/false}
#upsert是指没有匹配的行就直接插入该行
#multi: 是指修改多行,默认情况下,即使查询表达式命中多行,默认也只是修改一行

#upsert
> db.student.find();
{ "_id" : ObjectId("58a9088db1ba35a73d3b4276"), "sn" : "001", "name" : "xiaoming1", "gender" : "m" }
{ "_id" : ObjectId("58a9089db1ba35a73d3b4277"), "sn" : "003", "name" : "QQ", "gender" : "f" }
{ "_id" : ObjectId("58a91258b1ba35a73d3b4279"), "sn" : "004", "name" : "xiaoming3", "gender" : "m" }

> db.student.update({_id:99},{x:123,y:456},{upsert:true})
WriteResult({ "nMatched" : 0, "nUpserted" : 1, "nModified" : 0, "_id" : 99 })
> db.student.find();
{ "_id" : ObjectId("58a9088db1ba35a73d3b4276"), "sn" : "001", "name" : "xiaoming1", "gender" : "m" }
{ "_id" : ObjectId("58a9089db1ba35a73d3b4277"), "sn" : "003", "name" : "QQ", "gender" : "f" }
{ "_id" : ObjectId("58a91258b1ba35a73d3b4279"), "sn" : "004", "name" : "xiaoming3", "gender" : "m" }
{ "_id" : 99, "x" : 123, "y" : 456 }


#没有multi的情况下:
> db.student.find();
{ "_id" : ObjectId("58a9088db1ba35a73d3b4276"), "sn" : "001", "name" : "xiaoming1", "gender" : "m" }
{ "_id" : ObjectId("58a9089db1ba35a73d3b4277"), "sn" : "003", "name" : "QQ", "gender" : "f" }
{ "_id" : ObjectId("58a91258b1ba35a73d3b4279"), "sn" : "004", "name" : "xiaoming3", "gender" : "m" }
{ "_id" : ObjectId("58a91327b1ba35a73d3b427a"), "sn" : "004", "name" : "xiaoming", "gender" : "m" }
> db.student.update({sn:"004"},{$set:{name:"xxxxx"}})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
> db.student.find();
{ "_id" : ObjectId("58a9088db1ba35a73d3b4276"), "sn" : "001", "name" : "xiaoming1", "gender" : "m" }
{ "_id" : ObjectId("58a9089db1ba35a73d3b4277"), "sn" : "003", "name" : "QQ", "gender" : "f" }
{ "_id" : ObjectId("58a91258b1ba35a73d3b4279"), "sn" : "004", "name" : "xxxxx", "gender" : "m" }        #因为sn=004的有多个,但是update之后只是修改一条记录,这是默认的情况下
{ "_id" : ObjectId("58a91327b1ba35a73d3b427a"), "sn" : "004", "name" : "xiaoming", "gender" : "m" }


#有multi的情况下:
> db.student.update({gender:"m"},{$set:{sn:"888"}},{multi:true})
WriteResult({ "nMatched" : 3, "nUpserted" : 0, "nModified" : 3 })
> db.student.find();        #update所有的gender=m的,并修改所有查询到的记录(将sn=888)
{ "_id" : ObjectId("58a9088db1ba35a73d3b4276"), "sn" : "888", "name" : "xiaoming1", "gender" : "m" }
{ "_id" : ObjectId("58a9089db1ba35a73d3b4277"), "sn" : "003", "name" : "QQ", "gender" : "f" }
{ "_id" : ObjectId("58a91258b1ba35a73d3b4279"), "sn" : "888", "name" : "xxxxx", "gender" : "m" }
{ "_id" : ObjectId("58a91327b1ba35a73d3b427a"), "sn" : "888", "name" : "xiaoming", "gender" : "m" }
>


# $setOnInsert  当upsert为true时,并且发生了insert操作时,可以补充的字段:
> db.student.find();
{ "_id" : ObjectId("58a9088db1ba35a73d3b4276"), "sn" : "888", "name" : "xiaoming1", "gender" : "m" }
{ "_id" : ObjectId("58a9089db1ba35a73d3b4277"), "sn" : "003", "name" : "QQ", "gender" : "f" }
{ "_id" : ObjectId("58a91258b1ba35a73d3b4279"), "sn" : "888", "name" : "xxxxx", "gender" : "m" }
{ "_id" : ObjectId("58a91327b1ba35a73d3b427a"), "sn" : "888", "name" : "xiaoming", "gender" : "m" }
 
> db.student.update({_id:88},{$set:{name:"AAAAAA"},$setOnInsert:{job:"java-hadoop"}},{upsert:true});    #此时更新的记录不存在,那么就insert
WriteResult({ "nMatched" : 0, "nUpserted" : 1, "nModified" : 0, "_id" : 88 })
> db.student.find();
{ "_id" : ObjectId("58a9088db1ba35a73d3b4276"), "sn" : "888", "name" : "xiaoming1", "gender" : "m" }
{ "_id" : ObjectId("58a9089db1ba35a73d3b4277"), "sn" : "003", "name" : "QQ", "gender" : "f" }
{ "_id" : ObjectId("58a91258b1ba35a73d3b4279"), "sn" : "888", "name" : "xxxxx", "gender" : "m" }
{ "_id" : ObjectId("58a91327b1ba35a73d3b427a"), "sn" : "888", "name" : "xiaoming", "gender" : "m" }
{ "_id" : 88, "name" : "AAAAAA", "job" : "java-hadoop" }

#此时更新的记录存在,那么setOnInsert就不会起作用
> db.student.update({sn:"003"},{$set:{name:"AAAAAA"},$setOnInsert:{job:"java-hadoop"}},{upsert:true});
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
> db.student.find();
{ "_id" : ObjectId("58a9088db1ba35a73d3b4276"), "sn" : "888", "name" : "xiaoming1", "gender" : "m" }
{ "_id" : ObjectId("58a9089db1ba35a73d3b4277"), "sn" : "003", "name" : "AAAAAA", "gender" : "f" }
{ "_id" : ObjectId("58a91258b1ba35a73d3b4279"), "sn" : "888", "name" : "xxxxx", "gender" : "m" }
{ "_id" : ObjectId("58a91327b1ba35a73d3b427a"), "sn" : "888", "name" : "xiaoming", "gender" : "m" }
{ "_id" : 88, "name" : "AAAAAA", "job" : "java-hadoop" }
>
 
 
```
 
 


# 4.查询
```
#语法
db.collection.find(查询表达式, 查询的列)

#查询所有
> db.student.find();
{ "_id" : ObjectId("58a90683b1ba35a73d3b4272"), "sn" : "004", "name" : "xiaoming4", "gender" : "m" }
{ "_id" : ObjectId("58a9068db1ba35a73d3b4273"), "sn" : "001", "name" : "xiaoming1", "gender" : "m" }
{ "_id" : ObjectId("58a90693b1ba35a73d3b4274"), "sn" : "002", "name" : "xiaoming2", "gender" : "m" }


#指定查询表达式
db.collection.find(查询表达式, {列1:1, 列2:1})

> db.student.find();
{ "_id" : ObjectId("58a9088db1ba35a73d3b4276"), "sn" : "001", "name" : "xiaoming1", "gender" : "m" }
{ "_id" : ObjectId("58a9089db1ba35a73d3b4277"), "sn" : "003", "name" : "xiaoming3", "gender" : "f" }
> db.student.find({name:"xiaoming3"});
{ "_id" : ObjectId("58a9089db1ba35a73d3b4277"), "sn" : "003", "name" : "xiaoming3", "gender" : "f" }
> 

#指定要显示的列(0表示不显示,1表示显示)
> db.student.find();
{ "_id" : ObjectId("58a9088db1ba35a73d3b4276"), "sn" : "888", "name" : "xiaoming1", "gender" : "m" }
{ "_id" : ObjectId("58a9089db1ba35a73d3b4277"), "sn" : "003", "name" : "AAAAAA", "gender" : "f" }
{ "_id" : ObjectId("58a91258b1ba35a73d3b4279"), "sn" : "888", "name" : "xxxxx", "gender" : "m" }
{ "_id" : ObjectId("58a91327b1ba35a73d3b427a"), "sn" : "888", "name" : "xiaoming", "gender" : "m" }

> db.student.find({},{gender:1});        #_id属性默认总是显示出来
{ "_id" : ObjectId("58a9088db1ba35a73d3b4276"), "gender" : "m" }
{ "_id" : ObjectId("58a9089db1ba35a73d3b4277"), "gender" : "f" }
{ "_id" : ObjectId("58a91258b1ba35a73d3b4279"), "gender" : "m" }
{ "_id" : ObjectId("58a91327b1ba35a73d3b427a"), "gender" : "m" }

> db.student.find({},{gender:1,_id:0});
{ "gender" : "m" }
{ "gender" : "f" }
{ "gender" : "m" }
{ "gender" : "m" }
>
 
```
 