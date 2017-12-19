---
title:  python数据类型之字典
categories: python   
toc: true  
tags: [python]
---



字典当成是无序的集合，是通过键来存取的，而不是通过偏移存取，python字典的主要属性如下：
* 通过键而不是偏移量来读取
* 任意对象的无序集合
* 可变长、异构、任意嵌套

常见的字典常量和操作

|表达式|含义|
|-|-|
|D = {}	|空字典                             |              |
|D = {“name":"zhangsan", "age":33}	      |               |
|D = { “name":"zhangsan", "food":{"ham":1,"egg":3}}	嵌套 |
|D = dict(name="Bob", age=43)	             |            |
|D["eggs"]	|  通过键索引值                               |
|D["food"]["egg"]	                                     |
|'egg' in D	|  判断键是否存在                               |
|D.keys()	|  获取所有的键视图                             |
|D.values()	|  获取所有的值视图                             |
|D.items()	|  获取所有的键+值视图                          |
|D.copy()	          |                                     |
|D.get(key, default)	|  根据键获取值，没有返回默认       |
|D.update(D2)	|  合并                                     |
|D.pop(key)	|  删除等                                       |
|len(D)	|  长度                                             |
|D[key] = 33	|  赋值                                     |
|del D[key] 	|  根据键删除条目                           |
|list(D.keys())	|  字典视图转成列表                         |
|D = {x : x*2 for x in range(10)}	|  初始化字典           |


# 1.赋值
```
>> D = {"name“:"zhangsan", "age":22}
>> D['name'] = ["firstName","lastName"]
>>D
{"name“:["firstName","lastName"], "age":22}
```





# 2.取值(根据键，get， for)
```
>> D = {"name“:"zhangsan", "age":22}
>>D["name"]
'zhangsan'

>>D.get("name")                            #如果不存在返回None
'zhangsan'

>>D.get("birthday", "no have name")       #如果不存在就返回字符串”no have name"
”no have name"


>> D = {"name“:"zhangsan", "age":22}
>> for key in D                     #直接遍历的是字典的key
......     print(key, '\t', D[key])



>> D = {"name“:"zhangsan", "age":22}
>> for key in D.keys()                                   #先获取key的集合，然后遍历集合
......     print(key, '\t', D[key])


```

# 3.获取不存在的值（避免missing-key错误）
```
#方式1
if key in D :
    print (D[key])
else:
    print（0）


#方式2
try:
    print (D[key])
except KeyError:
    print（0）


#方式3
D.get(key,defalut)                  #不存在就给一个默认的值

```



# 4.len 长度
```
>> D = {"name“:"zhangsan", "age":22}
>>len(D)
2
```

# 5.删除pop / del
```
>> D = {"name“:"zhangsan", "age":22}
del D["name"]
>>D
{"age":22}


>> D = {"name“:"zhangsan", "age":22}
>>D.pop("name")                                    #从字典中删除一个键并返回他的值

>> D = {"name“:"zhangsan", "age":22}
>>D.pop()                                    #删除并返回最后一个的值

>> D = {"name“:"zhangsan", "age":22}
>>D.pop(1)                                    #删除指定的一个，并返回值

```

# 6.判断键存在in
```
>> D = {"name“:"zhangsan", "age":22}
>> "name" in D                                 #检查某个键是否存在字典中
True
```

# 7.创建（初始化）字典的方法
```
#方式1
D = {"name“:"zhangsan", "age":22}

#方式2
D = {}
D['name'] = "zhangsan"
D['age'] = 33

#方式3
D = dict(name="zhangsan", age=33)

#方式4
D = dict([('name','zhangsan'), ('age',3)])

#方式5
D = dict.fromkeys(['a','b'],0)            #传入一个键列表，以及所有键的初始值（默认值为空）
{'a':0, 'b':0}        

D = {k:0 for k in ['a', 'b', 'c']}
>>D
{'a':0, 'b':0, 'c':0}

D = {k:None for k in ['a', 'b', 'c']}
>>D
{'a':None, 'b':None, 'c':None}

D =dict.fromkeys('abc')
>>D
{'a':None, 'b':None, 'c':None}
          
```


# 8.视图（keys / vlaues / items）
```
>> D = dict(a=1, b=2, c=3)
>> D
{'a':1, 'b':2, 'c':3}

>>K = D.keys()
>>K
>>list(K)
['a', 'b', 'c']

>>list(D.values())
[1,2,3]

>>list(D.items())
[('a',1), ('b',2), ('c', 3)]


#python3中字典视图并非创建后不能改变——他们可以动态的反应在视图创建之后对字典做出的修改
>> D = dict(a=1, b=2, c=3)
>> K = D.keys()
>>V = D.values()
>>list(K)
['a', 'b', 'c']

>>list(V)
[1,2,3]

>>del D['a']

>>list(K)
[ 'b', 'c']

>>list(V)
[2,3]

```



# 9.排序字典键
因为keys不会返回一个list，所以我们要排序，必须要通过手动转换为一个列表的方式，
```
>> D = dict(a=1, b=2, c=3)
>>ks = list(D.keys())
ks.sort()                                                            #列表排序
for k in ks:
    print(k,D[k])



>> D = dict(a=1, b=2, c=3)
>>ks = D.keys()
for k in sorted(ks)                                           #调用排序函数
    print(k,D[k])


```



