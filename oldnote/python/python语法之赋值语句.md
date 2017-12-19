---
title:  python语法之赋值语句
categories: python   
toc: true  
tags: [python]
---



# 1.赋值语句的一些简单特性
* 赋值语句建立对象引用值
赋值语句总是建立对象的引用值，而不是复制对象，因此python的变量更像是指针，而不是数据存储区域
* 变量名在首次赋值时会被创建
* 变量名在引用前必须先赋值

# 2.赋值语句的形式

|表达式|含义|
|-|-|
|spam = 'spam'	|基本形式                                 |
|spam, ham = 'yum', 'YUM'	|元组赋值运算（位置性）       |
|[spam, ham] = ['yum', 'YUM']	|列表赋值运算（位置性）   |
|a, b, c, d = 'spam'	|序列赋值匀速（通用性）           |
|a, *b = 'spam'	|扩展的序列解包                           |
|spam = ham = 'lunch'	|多目标赋值运算                   |
|sapm += 43	|增强型赋值运算（相当于：spam = spam + 43)    |


# 3.序列赋值
```
#Example1
>>nudge = 1
>>wink = 2
>>A, B = nudge, wink                                                    #like A = nudge , B = wink
>>A, B
(1, 2)
>>[c, d] = [nudge, wink]
>>c, d
(1,2)


#Example2
>>str = 'SPAM'
>>a, b, c, d = str
>>a, b
('S', 'P')

>>a, b, c = str                                                   #变量和值的个数不匹配
ValueError : too many values to unpack


#Example3
>>((a, b), c ) = ('sp', 'am')                                    #嵌套赋值
>>a, b ,c 
>>('s', 'p', 'am')


#Example4   
>>for (a, b, c) in [(1 ,2 ,3), (4, 5, 6)]                            #for循环中的赋值
>>for((a, b) ,c ) in [((1,2), 3), ((4, 5), 6)]    

```

# 4.匹配赋值 *a
一个带有星号的名称可以在赋值目标中使用，以指定对于序列的一个更为通用的匹配——<font color=red>一个列表赋值给了带有星号的变量名</font>
```
#Example1
>>seq = [1,2,3,4]
>>a, *b = seq
>>a
1
>>b 
[2,3,4]

#Example2
>>seq = [1,2,3,4]
>>*b, a = seq                        #会贪婪匹配
>>a 
4
>>b
[1,2,3]


#Example3
>>seq = [1,2,3,4]
>>a, *b ,c = seq               #处于中间的贪婪匹配
>>a
1
>>b
[2,3]
>>c
4


#Example4
>>seq = [1,2,3,4]
>>a, b, c, d, *e = seq                     
>>print(a,b,c,d,e)   
1 2 3 4 []                              #没有匹配到所以只能是一个空的list


#Example5
>>seq = [1,2,3,4]
>>a, b, *c, d, e = seq        #同上
>>print(a,b,c,d,e)   
1 2 [] 3 4



#Example6
>>seq = [1,2,3,4]
>>a , *b , c , *d = seq                                 #对于 b d 不知道如何匹配，所以会报错
SyntaxError:two starred expressions in assignment


#Example7
>>seq = [1,2,3,4]
>>*a = seq
SyntaxError:starred assignment target must be in a list or tuple


#Example8
>>seq = [1,2,3,4]
>>*a , = seq                              #左侧必须是一个元组或者是list
>>a 
[1,2,3,4]

```



# 5.增强赋值语句
```
>>L = [1,2]
>>L = L + [3,4]
>>L
[1,2,3,4]

>>L.extend([7,8])
>>L
[1,2,3,4,7,8]


>>L += [9,10]                                       #python会将+= 转成extend方法，而不是转成 L = L + [9, 10] 
>>L
[1,2,3,4,7,8,9,10]


#增强型赋值被修改
>>L = [1,2]
>>M = L
>>L = L + [3,4]
>>M,L
([1,2], [1,2,3,4])                                    #可以看到L 改变了，但是M没有变化


>>L = [1,2]
>>M = L
>>L += [3,4]                                            
>>M,L
([1,2,3,4], [1,2,3,4])                            #因为+=使用的是extend进行的，所以修改是原处修改，所以M 、L 都发生了变化


```




