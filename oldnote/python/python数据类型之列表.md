---
title:  python数据类型之列表
categories: python   
toc: true  
tags: [python]
---



&emsp;列表可以包含任何种类的对象：数字、字符串、其他列表，列表时可变对象，支持在原地修改，常用列表常量和操作

|表达式|含义|
|-|-|
|L = []	|一个空的列表|
|L = [0, 1, 2, 3, 4]||	
|L = [1,3,[4,5]]	|列表中嵌套列表|
|L = list('spam')	|
|L[i]	|索引某一个元素|
|L[i][j]	|索引的索引|
|L[i:j]	|分片|
|len(L)	|求长度|
|L1 + L2	|合并|
|L * 3	|重复|
|for x in L : print(x)	|迭代|
|3 in L 	|检测|
|L.append(4)	|增长|
|L.extend([4,5,7])	||
|L.insert(1)	|插入|
|L.index("zhansan")	|求索引|
|L.sort()	|排序|
|L.reverse()	|反转|
|del L[k]	|删除|
|del L[i:j]	|删除一片|
|L.pop()	|移除最后一个|
|L.remove(2)	|移除指定的一个|
|L[i:j] = []	|清空指定的分片|
|L[i:j] = [4,5,6]	|分片赋值|



# 1.列表的CRUD和其他的操作
## 1.1.求列表长度
```
>>len([1,3,5])
3
```

## 1.2.合并列表
```
>>[1,2,3] + [4,5,7]
[1,2,3,4,5,7]

#注意+两边必须是同种类型的序列，否则江湖报错
>> str([1,3,4]) + "33"
'1,3,4]33'

>>[1,2] + list("33")
[1,2,3,3]

```

## 1.3.重复
```
>>['Ni'] * 4
['Ni', 'Ni', 'Ni', 'Ni']

>>res = [c * 4 for c in 's
>pam']
>>res 
['ssss', 'pppp', 'aaaa', 'mmmm']
#等价于
>>res = []
>>for c in 'spam':
...    res.append(c * 4)
>>res
['ssss', 'pppp', 'aaaa', 'mmmm']
```

## 1.4.判断in
```
>> 3 in [1,2,3]
True

>>for x in [1,2,3]:
print(x)

```

## 1.5.索引、分片、矩阵
```
>> L = ['spam', 'Spam', 'SPAM']   
>> L[2]                                             #索引
'SPAM'

>>L[1:]                                             #分片
[ 'Spam', 'SPAM']


>>matrix = [[1,2,3], [4,5,6], [7,8,9]]
>>matrix[1]                                       #矩阵
[4,5,.6]

>>matrix[1][1]
5

```


## 1.6.赋值
当使用列表的时候，可以将它赋值给一个特定项（偏移）或整个片段（分片）来改变他的内容，索引和分片的赋值都是原地修改，他们对列表进行直接修改，而不是·生成一个新的里诶包作为结果
```
>> L = ['spam', 'Spam', 'SPAM']
>> L[1] = 'eggs'                                                   #特定项
>> L
['spam', 'eggs', 'SPAM']


>>L[0:2] = ['eat', 'more']                                    #整个片段
>>L
['eat', 'more', 'SPAM']               
```


分片赋值最好分成两步来理解
1. 删除，删除等号左边指定的分片
2. 插入，将包含在等号右边对象中的片段插入旧分片被删除的位置
实际情况并非如此，但是这有助于我们理解为什么插入元素的数目不需要与删除的数目相匹配，例如：已知一个列表L的值为【1,3,4】，赋值操作L[1:2] = [4,5] 会把L修改成列表【1,4，5,4】,python会先删除3（单项分片），然后在删除3的位置插入4,5，这也解释了为什么L[1:2] = [] 实际上是删除操作——python删除分片，之后什么也不插入



## 1.7.添加append / extend / insert
```
>>L.append('please')                            #在末尾添加
>>L
['a','b','please']                  


>> L = [1,3]
>> L.extend([4,5,6])                           #在末端插入多个元素
>> L
[1,3,4,5,6]


>>L.insert(1,"toast")                         #在指定位置插入元素
>> L
[1,'toast',3,4,5,6]

```



## 1.8.排序sort
```
>>L = ["aA", "dC","ab"]
>>L.sort()                                       #按照字符的ASCII码进行排序
>>L
["aA","ab", "dC"]      


>>L.sort(key=str.lower)                   #转化成为小写之后进行排序
>>L
[“aA", "ab", "dC"]

>>L.sort(key=str.lower，reverse = True)                   #转化成为小写之后，降序排列
>>L
["dc", "ab", "aA"]


#通过sorted 内置函数可以实现
>> L = ['abc', 'ABD','bBe']
>> sorted(L, key=str.lower, reverse = True)
['aBe,'ABD, 'abc']

```

要当心append和sort原处修改相关的列表对象，而结果并没有返回列表（从技术上来讲，两者返回的是None），如果编辑类似的L=L.append(x）的语句，将不会得到L修改后的值（实际上，会失去整个列表的引用）


## 1.9.删除pop / remove / del
```
>> L = ["zhangsan", "lisi", "wangwu"]
>> L.pop()                            #删除最后一个元素
>> L
["zhangsan", "lisi"]

>> L = ["zhangsan", "lisi", "wangwu"]
>>L.pop(1)                                            #删除指定的元素
>>L
 ["zhangsan",  "wangwu"]


>> L = ["zhangsan", "lisi", "wangwu"]
>>L.remove("zhangsna")                        #移除  指定值  的元素
>>L
["lisi", "wangwu"]


>> L = ["zhangsan", "lisi", "wangwu"]
>> del L[0]                                             #删除指定的元素
>> L
["lisi", "wangwu"]

>> L = ["zhangsan", "lisi", "wangwu"]
>>del L[1:]                                          #删除指定的分片
>>L
 ["zhangsan"]


>> L = ["zhangsan", "lisi", "wangwu"]
>> L[1:] = []                                         #清空指定的分片
>>L
 ["zhangsan"]

```



## 1.10.反转

```
>> L = [1,2,3]
>>L.reverse()
>>L
[3,2.1]
```






