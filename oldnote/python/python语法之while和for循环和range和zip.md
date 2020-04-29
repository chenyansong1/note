---
title:  python语法之while和for循环和range和zip
categories: python   
toc: true  
tags: [python]
---




# 1.while循环
## 1.1一般格式
首行以测试表达式、有一列或者多列的缩进语句的主体，一个可选的else部分（<font color=red>控制权离开循环而又没有碰到break语句时会执行</font>）
```
while <test>:
    <statements1>
else:
    <statements2>

```

## 1.2.Example
```
#Example1
while True:
    print('ha ha')                    #一直循环打印


#Example2
x = 'spam'
while x:
    print(x, end=' ')
    x = x[1:]                        #重新指向自己的切片

。。。
spam pam am m 


#Example3
a = 0, b = 6
while a<b:
    print(a, end=' ')
    a +=1
。。。
0 1 2 3 4 5
```

## 1.3.break、continue、pass
* break 跳出最近所在的循环（跳出整个循环语句）
* continue 跳到最近循环所在的开头处（来到循环的首行）
* pass 什么事也不做，只是空占位语句
* 循环else块
只有当循环正常离开时，才会执行（也就是没有碰到break语句）


# 2.for循环
for循环语句可以用于字符串、列表、元组、其他内置可迭代对象以及之后我们能够通过类所创建的新对象
## 2.1.一般格式
```
for <target> in <object>
    <statements1>
else:
    <statements2>


#for 配合break、continue使用的例子
for <target> in <object>
    <statements1>
    if <test> : break
    if <test> :continue
else:
    <statements2>

```

## 2.2.Example
```

#Example1
for x in ['spam', 'eggs', 'ham']:               #循环遍历list
    print(x , end=' ')
...
spam eggs ham


#Example2
S = 'spam'
for char in S:                        #循环遍历str
    print(char, end=' ')            
..
s p a m 

#Example3
T = ('and', 'or' , 'okay')
for x in T:                        #循环遍历tuple
    print(x , end=' ')
...
and or okay


#Example4
T = [(1,2), (3,4), (5,6)]
for (a,b) in T:                #遍历嵌套元组
    print(a,b)
...
1 2
3 4
5 6


#Example5
D = {'a':1, 'b':2, 'c':3}
for key in D:                              #遍历dict的key
    print(key,'=>',D[key])
....
a=>1
b=>2
c=>3


for (key,value) in D.items():
    print(key,'=>',value)
....
a=>1
b=>2
c=>3


#Example6
for ((a, b), c) in [((1,2), 3),  ((4,5), 6)]:
    print(a,b,c)
1 2 3
4 5 6


for((a, b), c) in [((1, 2), 3), ('xy', 6)]:
    print(a, b, c)
....
1 2 3
x y 6



#Example7
for (a, *b, c) in [(1,2,3,4), (5, 6, 7, 8)]:
    print(a,b,c)
......
1 [2,3] 4
5 [6,7] 8


#Example8   
for x in L1:                        #嵌套for循环            
    for y in L2:
        print(xxxx)


#Example9
for line in open('test.txt'):            #循环遍历文件，文件迭代器会自动在每次循环迭代的时候读入一行
    print(line)


```

# 3.range
range函数返回一系列连续增加的整数，可用作for的索引
## 3.1.语法
```
list(range(<font color=red>x</font>, <font color=green>y</font> , <font color=blue>step</font>)

list(range(0,7,2)
[0, 2, 4, 6]
```

* x 表示整数序列的起始值（默认0开始）
* y 表示整数序列的结束值（不包含y本身）
* step 步进值（相邻元素之间的差值，默认是1）


## 3.2.例子
```
list(range(-4,4))
[-4,-3,-2,-1,0,1,2,3]                                #可以是负数序列


list(range(4,-4,-1))
[4,3,2,1,0,-1,-2,-3]                                #可以是非递增的



x = 'spam'
for i in range(len(x)):                    #和for循环配合使用
    print(x[i], end=' ')
...
s p a m



L = [1,2,3]
[x+1 for x in L]                     #生成一个新的list，其中的每一个元素都加1

```

# 4.zip
zip会取得一个或多个序列为参数，然后返回一个元组的列表，将这些序列中的并排的元素配成对。如下：
```
L1 = [1,2,3]
L2 = [a,b,c]

list(zip(L1, L2))
[(1,a), (2,b), (3,c)]



T1 = (1,2,3)
T2 = (4,5,6)
T3 = (7,8,9)

list(zip(T1, T2, T3))
[(1,4,7), (2,5,8), (3,6,9)]




S1 = 'abc'
S2 = 'xyz123'

list(zip(S1, S2))                                       #当长度不同时，zip会以最短序列的长度为准来截取所得到的元组
[('a','x'), ('b','y'), ('c', 'z')]

#使用zip构造字典
keys = ['eggs', 'toast', 'ham']
values = [1,3,5]
D2 = {}
for (k,v) in list(zip(keys,values)):
    D2[k] = v

D2
{'eggs':1, 'toast':3, 'ham':5}


#另一种方式构造字典
D3 = dict(list(zip(keys,values)))

```


# 5.偏移和元素：enumerate
```
#手动获取偏移
>>s = 'spam'
>>offset = 0
>>for item in s:
    print(item, 'appers at offset', offset)
    offset +=1
...
s appers at offset 0
p appers at offset 1
a appers at offset 2
m appers at offset 3



#通过enumerate获取偏移
>>s = 'spam'
>>for (offset, item) in enumerate(s):                     #offset记录的就是偏移量
    print(item, 'appers at offset', offset)
...
s appers at offset 0
p appers at offset 1
a appers at offset 2
m appers at offset 3



```



