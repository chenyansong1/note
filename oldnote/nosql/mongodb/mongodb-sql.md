[toc]

# where - group - having

```sql
SELECT cust_id,
       SUM(price) as total
FROM orders
WHERE status = 'A'
GROUP BY cust_id
HAVING total > 250
 
db.orders.aggregate( [
   { $match: { status: 'A' } },
   {
     $group: {
        _id: "$cust_id",
        total: { $sum: "$price" }
     }
   },
   { $match: { total: { $gt: 250 } } }
] )



---------------------


    { "$group": {
        "_id": {
            "addr": "$addr",
            "book": "$book"
        },
        "bookCount": { "$sum": 1 }
    }},
   
   select * from books group by add, book;
   


------------------------


        # 查询限定数据量(默认500条数据)中的指定字段占比的top10
        mog_results = mgo.get_assets_data_clt().aggregate([
            {"$match": {"$and": query_arry}},
            {"$limit": total_nums},  # 查询限定数量
            {"$group": {"_id": f"${field}", "counts": {"$sum": 1}}},  # 分组聚合查询
            {"$sort": {"counts": -1}},  # 正序排序
            {"$limit": 10}  # top10排名
        ])
        
        
        
 # 以数组中的一个字段作为查询的条件
 db.inventory.insertMany([
   { item: "journal", qty: 25, tags: ["blank", "red"], dim_cm: [ 14, 21 ] },
   { item: "notebook", qty: 50, tags: ["red", "blank"], dim_cm: [ 14, 21 ] },
   { item: "paper", qty: 100, tags: ["red", "blank", "plain"], dim_cm: [ 14, 21 ] },
   { item: "planner", qty: 75, tags: ["blank", "red"], dim_cm: [ 22.85, 30 ] },
   { item: "postcard", qty: 45, tags: ["blue"], dim_cm: [ 10, 15.25 ] }
]);
 
 The following example queries for all documents where the second element in the array dim_cm is greater than 25:
 
 db.inventory.find( { "dim_cm.1": { $gt: 25 } } )
 
 
 
 
 # select arr.1/arr.0 as result2 from tb ;

 agg:{
	$project:{
         {
          "a":{"$arrayElemAt":["$cpu",1]},
          "b":{"$arrayElemAt":["$cpu",0]},
          result2:{"$divide":[{"$arrayElemAt":["$cpu",1]}, {"$arrayElemAt":["$cpu",0]}]}
        }
	}

 }

 
 # select arr.1/arr.0 as result2 from tb where result2 > 0.2;
 agg:{
	$project:{
         {
          "a":{"$arrayElemAt":["$cpu",1]},
          "b":{"$arrayElemAt":["$cpu",0]},
          result2:{"$divide":[{"$arrayElemAt":["$cpu",1]}, {"$arrayElemAt":["$cpu",0]}]}
        }
	},
	$match:{
		result2:{"$gt":22}
	}

 }
```



![image-20210429141343245](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20210429141343245.png)



![image-20210429141606212](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20210429141606212.png)

![image-20210429144641897](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20210429144641897.png)

















