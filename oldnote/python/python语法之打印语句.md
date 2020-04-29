---
title:  python语法之打印语句
categories: python   
toc: true  
tags: [python]
---



# 1.python2.x 和python3.x的不同
* 在python2.x中，打印是语句，拥有自己特定的语法
* 在python3.x中，打印是一个<font color=red>内置的函数</font>，用关键字参数来表示特定的模式


# 2.python3.x中的print函数
## 2.1调用格式

print (<font color=red>[object,....]</font><font color=green> [,sep=' ']</font><font color=blue> [,end = '\n']</font> <font color=red>[, file = sys.stdout]</font>)

* sep 、end和file如果给出的话，必须使用一种特殊的“name = value"的语法来根据名称而不是位置来传递参数
* sep 在每一个对象之间插入的字符串，没有指定，默认就是空格（如果传递的是空字符串，则所有的对象文本之间将连接在一起）
* end 添加在打印文件末尾的一个字符串，如果没有传递的话， 默认是\n换行符
* file 指定了文本将要发送到的文件、标准流、或者其他类似文件的对象，默认是sys.stdout



## 2.2.Example
```
#Example1
>>print()            #打印的是空行


#Example2
>>print(x, y, z, sep=',')
spam, 99, ['eggs']                            #使用逗号去分割文本

>>print(x, y, z, sep='')                        #所有的对象文本将连接一起
spam99['eggs']



#Example3
>>print(x, y, z, end=''); print(x, y, z)                  #两次打印之间没有换行
spam 99 ['eggs']spam 99 ['eggs']   

>>print(x, y, z, end='###\n')                        #指定特殊的结束符
spam 99 ['eggs']###

#Example4
>>print(x, y, z, sep='...', file=open('data.txt','w'))                  #指定输出到一个打开的文件中去
>>print(x, y, z)
spam 99 ['eggs']

>>print(open('data.txt').read())                                        #打印文件中的内容
spam...99...['eggs']


```



# 3.打印流重定向
## 3.1.系统的打印方法
```
>>import sys
>>sys.stdout.write('hello world')
hello world

```
## 3.2.sys.stdout和print比较
```
print(x, y)
#等价于
import
sys.stdout.write(str(x) + ' ' + str(y) + '\n')

```

## 3.3.改变print的重定向流
```
import sys
sys.stdout = open('log.txt', 'a')
...
print(x, y ,z )                                #这样print将内容打印到了log.txt文件中

#这样改变的一个弊病，就是每次print的时候都是打印内容到log.txt中

```

## 3.4.自动化重定向流
```
>>import sys
>>temp = sys.stdout
>>sys.stdout = open('log.txt', 'a')
>>print('spam')
>>print(1, 2, 3)
>>sys.stdout.close() 

>>sys.stdout = temp                            #重新定向输出流
>>print("back here")
back here

>>print(open('log.txt').read())
spam
1 2 3


#有了上面的试验，我们可以知道，print的好处，我们只是使用了print的中的file可以临时的改变输出流的指向，当打印完毕之后，输出流又重新回到原来的默认的流
```

python2.x print输出到文件

```
#!/usr/bin/env python3  
#coding:utf-8
K = 10
f = open("./output/recard", 'w+')  
for i in range(K) 
    print>>f,"第{0}条数据".format(i)  
	
	

import sys
f=open('test.txt','a+')
s= '123'
abc= '456'
print >> f, s,abc
f.close()
 
 
r 读
w 写
a 追加

```





