---
title:  python语法之迭代器和解析
categories: python   
toc: true  
tags: [python]
---




# 1.迭代器(Iterator)和可迭代对象(Iterable)
## 1.1判断对象是否可迭代
可以使用isinstance()判断一个对象是否是Iterator对象
```
>>> from collections import Iterator
>>> isinstance((x for x in range(10)), Iterator)
True
>>> isinstance([], Iterator)
False
>>> isinstance({}, Iterator)
False
>>> isinstance('abc', Iterator)
False
```

## 1.2.Iterator 和Iterable区别
* 凡是可作用于for循环的对象都是Iterable类型；
* 凡是可作用于next()函数的对象都是Iterator类型，它们表示一个惰性计算的序列；
* 集合数据类型如list、dict、str等是Iterable但不是Iterator，不过可以通过iter()函数获得一个Iterator对象。
* Python的for循环本质上就是通过不断调用next()函数实现的



# 2.迭代器原理
```
>>L = [1,2,3]
>>I = iter(L)                                    #第一步：使用iter()函数将可迭代的对象转化成为一个迭代器
>>I.next()
1
>>I.next()                                        #第二部：使用迭代器的next()方法( 或者_next_()方法 )进行迭代
2
>>I.next()
3
>>I.next()
....more ommitted
StopIteration                                    #第三部：在迭代过程中遇到StopItreation 异常就结束迭代


```

# 3.迭代器原理举例
```
#Example1（列表迭代的过程）
>>L = [1,2,3]
>>for x in L:
    print(x, end=' ')
.......
1 2 3


#等价于
>>L = [1,2,3]
>>I = iter(L)                                       #获取迭代器
while True:
    try:
        x = next(I)                                  #进行迭代
    except StopIteration:                      #迭代结束
        break
    print(x , end=' ')
.......
1 2 3



#Example2（字典迭代）
>>D = {'a':1, 'b':2}
>>for key in D:
    print(key, D[key])

#等价于
>>D = {'a':1, 'b':2}
>>I = iter(D)
>>next(I)                                #对字典的迭代取出的是字典中的key
'a'
>>next(I)
'b'
>>next(I)
Traceback 
......more omitted...
StopIteration                            #迭代结束


#Example3（enumerate迭代）
>>E = enumerate('spam')
>>I = iter(E)                        #得到迭代器
>>next(I)
(0,'s')
>>next(I)
(1,'p')
......

>>list( enumerate('spam'))
[(0,'s') ,(1,'p') ,(2,'a') ,(3,'m')]


```

# 4.列表解析
## 4.1基本格式

![](http://ols7leonh.bkt.clouddn.com//assert/img/python/iterable/1.png)

## 4.2举例
```
#Example1
L = [x + 1  for x in L]

#Example2
lines = [line.rstrip()   for line in lines]            #去掉每一行后面的\n

#Example3
[line.upper()  for line in opne('data.txt')]      #将每一行转成大写
['AAA', 'BBB', 'CCC']


#Example4（if条件）
lines = [line.rstrip()  for line in open('data.txt')  if line[0] == 'p' ]        #在列表中只留下以‘p'开头的行


#Example5（多个for）
[x +y for x in 'abc'  for y in '123']
['a1', 'b2', 'c3']


#Example6(生成字典)
{ix:line  for ix,line in enumerate(open('data.txt'))}
{0:'AAA', 1:'BBB', 2:'CCC'}


```



# 5.支持可迭代协议的函数
<font color=red>这样的函数会在内部调用itre（可迭代对象）进行循环</font>
```
#list
list(open('data.txt'))            #循环可迭代对象的每一行
['AAA', 'BBB', 'CCC']

#sorted                        #返回的是list
sorted(open('data.txt'))
['AAA', 'BBB', 'CCC']



#sum
sum( [1,3,4,18] )            #循环可迭代对象list，求sum
26

#max/min                     #循环可迭代对象list，找出max
max( [1,3,4,18] )    
18


#any                     #如果一个迭代对象中的任何或所有项都为真，返回True  ?
any([2,3,4])
True


#tuple
tuple(open('data.txt'))                 #tuple循环可迭代对象
('AAA', 'BBB', 'CCC')



#join
"&&".join(open('data.txt'))        #join循环可迭代对象,然后在其中加入“&&”
’AAA&&BBB&&CCC‘



#set

#zip
zip(['a', 'b', 'c'], [1,2,3])                         #返回的是可迭代对象
list( zip(['a', 'b', 'c'], [1,2,3]) )                #在list中进行循环迭代，生成list列表
[('a', 1) ,('b', 2), ('c', 3)]



#enumerate
enumerate(open('data.txt'))                    #返回的是可迭代对象
list( enumerate(open('data.txt')) )            #在list中进行循环迭代，生成list列表



#range


```







# 6.多个迭代器VS单个迭代器
```
#range 产生的多个迭代器
>>R = range(3)
>>next(R)
TypeError: range object is not an iterator

>>I1 = iter(R)
>>I2 = iter(R)                    #可以产生多个互不影响的迭代器
>>next(I1)                         #遍历迭代器1
0
>>next(I1)
1
>>next(I2)                          #迭代器2
0
>>next(I2)
1




#zip 、map、filter 产生的单迭代器
>>Z = zip([1,2,3], [10,11,12])
>>I1 = iter(Z)
>>I2 = iter(Z) 
>>next(I1)
(1,10)
>>next(I1)
(2,11)
>>next(I2)                        #他会接着上一个迭代器循环
(3,12)


```

# 7.字典视图迭代器
字典有针对key的迭代器，keys、values、items返回都是可迭代的视图对象（可迭代对象）
```
>>D = {'a':1, 'b':2 , 'c':3}
>>K = D.keys()
>>next(K)
TypeError:dict_key object is not an iterator
.....

>>I = iter(K)
>>next(I)
'a'
>>next(I)
'b'

for key in D.keys():
    print(key,D[key])



#因为字典也是可迭代对象
>>D = {'a':1, 'b':2 , 'c':3}
>>I = iter(D)                     #返回对key的迭代器
>>next(I)
'a'

for key in D:                  #迭代字典的key
    print(key,D[key])

```


