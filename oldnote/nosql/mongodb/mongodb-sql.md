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




        # 查询限定数据量(默认500条数据)中的指定字段占比的top10
        mog_results = mgo.get_assets_data_clt().aggregate([
            {"$match": {"$and": query_arry}},
            {"$limit": total_nums},  # 查询限定数量
            {"$group": {"_id": f"${field}", "counts": {"$sum": 1}}},  # 分组聚合查询
            {"$sort": {"counts": -1}},  # 正序排序
            {"$limit": 10}  # top10排名
        ])
```

