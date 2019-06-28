# python操作redis

转自：https://blog.csdn.net/luanpeng825485697/article/details/79363490



```python
#python3.6操作redis数据库。使用前请先确保redis服务已经打开

print("=====================redis数据库=====================")

import redis

# 连接数据库
r = redis.Redis(host='127.0.0.1', port=6379,db=0)
# 使用连接池连接数据库。这样就可以实现多个Redis实例共享一个连接池
pool = redis.ConnectionPool(host='127.0.0.1', port=6379)
r = redis.Redis(connection_pool=pool)

# =============================1、String 操作===============================

#在Redis中设置值，默认不存在则创建，存在则修改
r.set('name', 'zhangsan')
r.mset(name1='zhangsan', name2='lisi')  #批量设置值
r.mset({"name3":'zhangsan1', "name4":'lisi1'})   #批量设置值
# 字符串局部更新。setrange(name, offset, value)
r.setrange("name",1,"z")   #修改字符串内容，从指定字符串索引开始向后替换
r.setrange("name",6,"zzzzzzz")   #如果新值太长时，则向后添加


# 参数：set(name, value, ex=None, px=None, nx=False, xx=False)
#      ex，过期时间（秒）
#      px，过期时间（毫秒）
#      nx，如果设置为True，则只有name不存在时，当前set操作才执行,同setnx(name, value)
#      xx，如果设置为True，则只有name存在时，当前set操作才执行

# r.setex(name, value, time)
# #设置过期时间（秒）
#
# r.psetex(name, time_ms, value)
# #设置过期时间（豪秒）

# 字符串转化为整型，再自增属性mount对应的值，当属性mount不存在时，则创建mount＝amount，否则，则自增,amount为自增数(整数)
print(r.incr("mount",amount=2))
print(r.incr("mount"))
print(r.incr("mount",amount=3))
print(r.incr("mount",amount=6))
print(r.get("mount")) #输出:12
# 字符串转化为浮点数，再自增。incrbyfloat(self, name, amount=1.0)
print(r.incrbyfloat('mount', amount=1.0))
#自减name对应的值,当name不存在时,则创建name＝amount，否则，则自减，amount为自增数(整数)
print(r.decr("mount",amount=6))

#在name对应的值后面追加内容
r.set("name","zhangsan")
r.append("name","lisi")
print(r.get("name"))    #输出:zhangsanlisi

# 获取值
print(r.get('name')) # 获取值
print(r.mget("name1","name2"))   #批量获取
print(r.mget(["name3","name4"]))   #批量获取

# 读取后重设  getset(name, value)
#设置新值，打印原值
print(r.getset("name1","wangwu")) #输出:zhangsan
print(r.get("name1")) #输出:wangwu

#根据字节获取子序列  getrange(key, start, end)
print(r.getrange("name",0,3))#输出:zzan

print(r.strlen("name")) #返回name对应值的字节长度（一个汉字3个字节）





# =============================2、Hash 操作===============================
# 增改操作
# hset(name, key, value) #name对应的hash中设置一个键值对（不存在，则创建，否则，修改）
r.hset("dic_name","a1","aa")
r.hmset("dic_name",{"a1":"aa","b1":"bb"})   #在name对应的hash中批量设置键值对,mapping:字典

#自增hash中key对应的值，不存在则创建key=amount(amount为整数)
print(r.hincrby("dic_name","a",amount=2))
#自增hash中key对应的值，不存在则创建key=amount(amount为浮点数)
print(r.hincrbyfloat("dic_name","a",amount=1.0))

# 查询操作
print(r.hget("dic_name","a1"))  #在name对应的hash中根据key获取value
print(r.hmget("dic_name",["a1","b1"]))     # 在name对应的hash中获取多个key的值
print(r.hmget("dic_name","a1","b1"))  # 在name对应的hash中获取多个key的值
print(r.hgetall("dic_name"))  #获取name对应hash的所有键值
print(r.hlen("dic_name"))  #hlen(name) 获取hash中键值对的个数
print(r.hkeys("dic_name"))  #hkeys(name) 获取hash中所有的key的值
print(r.hvals("dic_name"))  #hvals(name) 获取hash中所有的value的值

print(r.hexists("dic_name","a1")) # 检查name对应的hash是否存在当前传入的key


# 删除
r.hdel("dic_name","a1")  #删除指定name对应的key所在的键值对

# =============================3、List 操作===============================
#增改操作
# 在name对应的list中添加元素，每个新的元素都添加到列表的最左边.不存在列表是创建列表
r.lpush("list_name",2)
r.lpush("list_name",3,4,5)#保存在列表中的顺序为5，4，3，2
# rpush(name,values)#同lpush，但每个新的元素都添加到列表的最右边
# lpushx(name,value)#在name对应的list中添加元素，只有name已经存在时，值添加到列表的最左边
# rpushx(name,value)#在name对应的list中添加元素，只有name已经存在时，值添加到列表的最右边

# linsert(name, where, refvalue, value))  其中where: BEFORE（前）或AFTER（后）,refvalue: 列表内的值 value: 要插入的数据
r.linsert("list_name","BEFORE","2","SS") #在列表内找到第一个元素2，在它前面插入SS
r.lset("list_name",0,"bbb") #对list中的某一个索引位置重新赋值
r.lpop("list_name")  #移除列表的左侧第一个元素，返回值则是第一个元素
r.rpoplpush('list1_name', 'list2_name')  # 从一个列表取出最右边的元素，同时将其添加至另一个列表的最左边

# 查询操作
print(r.llen("list_name"))  # name对应的list元素的个数
print(r.lindex("list_name",1))  #根据索引获取列表内元素
print(r.lrange("list_name",0,-1))  #分片获取元素


# 删除操作
r.lrem("list_name","SS",num=0)   #r.lrem(name, value, num)  其中value: 要删除的值   num:   num=0 删除列表中所有的指定值；num=2 从前到后，删除2个；num=-2 从后向前，删除2个'''
r.ltrim("list_name",0,2)  #移除列表内没有在该索引之内的值

print(r.blpop(["list_name","list_name1"],timeout=0))  #将多个列表排列,按照从左到右去移除各个列表内的元素。timeout: 超时时间，获取完所有列表的元素之后，阻塞等待列表内有数据的时间（秒）, 0 表示永远阻塞
# r.brpop(keys, timeout)  #同blpop，将多个列表排列,按照从右向左去移除各个列表内的元素

# =============================4、set 操作===============================
# 增改操作
#给name对应的集合中添加元素
r.sadd("set_name","aa")
r.sadd("set_name","aa","bb")
r.smove("set_name1", "set_name2", 'aa')  #将某个元素从一个集合中移动到另外一个集合
r.spop("set_name")  #从集合的右侧移除一个元素，并将其返回



# 查询操作
print(r.smembers('set_name'))   #获取name对应的集合的所有成员
print(r.scard("set_name")) #获取name对应的集合中的元素个数
print(r.sdiff("set_name","set_name1","set_name2"))# 在第一个name对应的集合中且不在其他name对应的集合的元素集合
# sdiffstore(dest, keys, *args) #相当于把sdiff获取的值加入到dest对应的集合中

print(r.sinter("set_name","set_name1","set_name2")) # 获取多个name对应集合的交集
# sinterstore(dest, keys, *args) #获取多个name对应集合的交集，再将其加入到dest对应的集合中
print(r.sunion("set_name","set_name1","set_name2"))  #获取多个name对应的集合的并集
# sunionstore(dest,keys, *args)#获取多个name对应的集合的并集，并将结果保存到dest对应的集合中

print(r.srandmember("set_name2",2))  # 从name对应的集合中随机获取numbers个元素

print(r.sismember("set_name", 'aa'))  #检查value是否是name对应的集合内的元素

# 删除操作
r.srem("set_name2","bb","dd")  #删除name对应的集合中的某些值

# =============================5、有序集合 操作===============================
# 在集合的基础上，为每元素排序，元素的排序需要根据另外一个值来进行比较，所以，对于有序集合，每一个元素有两个值，即：值和分数，分数专门用来做排序。
# 增改操作
r.zadd("zset_name", "a1", 6, "a2", 2,"a3",5)   # 在name对应的有序集合中添加元素
r.zadd('zset_name1', b1=10, b2=5)  # 在name对应的有序集合中添加元素



# 查询操作
print(r.zcard("zset_name"))  #获取有序集合内元素的数量
print(r.zcount("zset_name",1,5))  #获取有序集合中分数在[min,max]之间的个数

r.zincrby("zset_name","a1",amount=2)#自增zset_name对应的有序集合里a1对应的分数

# 按照索引范围获取name对应的有序集合的元素.start起始索引,end终点索引,desc排序规则，默认按照分数从小到大排序,withscores是否获取元素的分数，默认只获取元素的值,score_cast_func 对分数进行数据转换的函数
aa=r.zrange("zset_name",0,1,desc=False,withscores=True,score_cast_func=int)


print(r.zscore("zset_name","value1")) #获取name对应有序集合中 value 对应的分数
print(r.zrank("zset_name", "value1"))  #获取value值在name对应的有序集合中的排行位置（从0开始）
print(r.zrevrank("zset_name", "value1"))#从大到小排序

# 删除操作

r.zrem("zset_name","value1","value2") #删除name对应的有序集合中值是values的成员
r.zremrangebyrank("zset_name", 3, 5)  #根据排行范围删除
r.zremrangebyscore("zset_name", 3, 5) #根据分数范围删除
r.zinterstore("zset_dest",("zset_name1","zset_name2"),aggregate="MAX")  # 获取两个有序集合的交集并放入dest集合，如果遇到相同值不同分数，则按照aggregate进行操作:aggregate的值为: SUM  MIN  MAX
# r.zunionstore(dest, keys, aggregate=None)  #获取两个有序集合的并集并放入dest集合，其他同zinterstore，


# =============================6、通用操作===============================
r.delete('name')  #根据name删除redis中的任意数据类型
print(r.exists('name'))  #检测redis的name是否存在
print(r.keys(pattern='*'))  #根据* ？等通配符匹配获取redis的name
print(r.expire('name' ,time=3000))  # 为某个name设置超时时间
r.rename('name', 'name1')  # 重命名
r.move('name', 'db1')  # 将redis的某个值移动到指定的db下
print(r.randomkey())  #随机获取一个redis的name（不删除）
print(r.type('name'))  # 获取name对应值的类型

# redis-py默认在执行每次请求都会创建（连接池申请连接）和断开（归还连接池）一次连接操作，
# 如果想要在一次请求中指定多个命令，则可以使用pipline实现一次请求指定多个命令，并且默认情况下一次pipline 是原子性操作。
pipe = r.pipeline(transaction=True)

r.set('name', 'zhangsan')
r.set('name', 'lisi')

pipe.execute()
```

