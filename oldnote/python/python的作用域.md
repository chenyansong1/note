---
title:  python的作用域
categories: python   
toc: true  
tags: [python]
---


# 1.简介
python创建、改变、查找变量名都是在所谓的命名空间中进行的，在代码中变量名被赋值的位置决定了这个变量名能被访问的位置。
可以在三个地方定义变量
* 在def函数内定义
* 在嵌套的def中赋值，对于嵌套的函数来说，他是非本地变量
* 如果在def之外赋值，他就是整个文件全局的


# 2.作用域法则
* 内嵌的模块是全局作用域
    每一个模块都是一个全局的作用域，对于外部的全局变量就是一个模块对象的属性，但是在一个模块中能够像简单的变量一样使用
* 全局作用域的作用范围仅限于单个文件（即模块），在python中听到“全局”，你就应该想到“模块”
* 每次对函数的调用都创建了一个新的本地作用域
* 赋值的变量名除非声明为全局变量或非本地变量，否则均为本地变量
    如果需要给一个在函数内部，却位于模块文件顶层的变量名赋值，需要在函数内部通过global语句声明，如果需要给一个嵌套在def中的名称赋值，可以通过一条nonlocal语句声明来做到
* 其他所有的变量名都可以归纳为本地、全局或者内置的
    在def内部为**本地变量**
    在一个模块的命名空间内部的顶层为**全局变量**
    有python的预定义的_buildin_模块提供额为**内置变量**

# 3.变量解析：LEGB原则
## 3.1.三条简单的原则：
* 变量名引用分为三个作用域进行查找：**首先是在本地、之后是函数内（如果有的话）、之后是全局、最后是内置**
* 在默认情况下，变量名赋值会创建或改变本地变量
* 全局申明和非本地声明将赋值的变量名映射到模块文件内部的作用域
## 3.2.LEGB原则
* 当在函数中使用未认证的变量名时，python搜索4个作用域：本地作用域【L】，之后是上一层结构中def或lambda的本地作用域【E】，之后是全局作用域【G】，最后是内置作用域【B】，如果还是没有找到就会报错，因为变量在使用之前必须先要赋值的
* 当在函数中给一个变量名赋值的时候，python总是创建或者使用本地作用域的变量名，除非该变量名已经在函数中声明为全局变量
    
## 3.3.LEGB四个作用域的关系

![](http://ols7leonh.bkt.clouddn.com//assert/img/python/legb/1.png)
 

## 3.4.作用域实例
```
#Global scope                    #全局变量名：X 、func (def 语句在这个模块的顶层将一个函数对象赋值给了变量名func)
X = 99
def func(Y):
    #local scope                   #本地变量名：Y、Z
    Z = X + Y
    return Z

func(1)                    #100



#def作用域
def f1():
    x = 88                        #def变量名：x 、f2
    def f2(x=x):
        print(x)                  #本地变量名：x
    f2()

..
f1()       #print 88

```




# 4.global语句
## 4.1.全局变量名总结
* 全局变量名时位于模块文件内部的顶层的变量名
* 全局变量名如果要在函数内部被赋值的话，必须经过global的声明
* 全局变量名在函数的内部不经过声明是可以被引用的（但是不会对变量做原处修改）

## 4.2.对全局变量在本地域中做原处修改
```
#1
x = 99
def func():
    x = 100
    print(x)

func()           #100
print(x)        #99

#2
x = 99
def func():
    global x                #通过global语句使自己明确地映射到了模块的作用域，如果没有使用global语句的话，x将会由于赋值而被认为是本地变量
    x = 100
    print(x)

func()           #100
print(x)      #100

```


# 5.导入模块对象
```
#first.py
X = 99

#second.py
import first.py
print(first.X)
first.X = 88

#一个模块文件的全局变量一个被导入就成为了这个模块对象的一个属性

```



# 6.工厂函数
产生函数的工厂
```
def maker(N):
    def action(X):
        return X ** N
    return action                    #将里面的函数返回了


#调用上面的函数
f = maker(2)
f(3)            #返回9
f(4)            #返回16

g = maker(3)
g(3)            #返回27                内嵌的函数记住了整数N ,这里是3
f(4)            #返回16                  内嵌的函数记住了整数N ,这里是2       


```

# 7.nonlocal语句
nonlocal应用于一个嵌套的函数的作用域中的一个名称，而不是所有def之外的全局模块作用域，而且，在声明nonlocal名称的时候，他必须已经存在于嵌套函数的作用域中——他们可能只存在于一个嵌套函数中，并且不能由一个嵌套的def中的第一次赋值创建
## 7.1.语法格式
```
def func():
    nonlocal name1, name2,...
```

* 当一个函数def嵌套在另一个函数中，嵌套的函数引用嵌套的def的作用域中的赋值所定义的任何名称，但是不能修改他们，此时nonlocal就派上用场了
* nonlocal使得对该语句中列出的名称的查找从嵌套的def的作用域开始，而不是从本地作用域开始，也就是说：nonlocal也意味着“完全略过我的本地作用域”，所以在一个嵌套的def中必须提前定义过nonlocal的变量名
* nonlocal的作用域查找只限定在嵌套的def中，作用域查找不会到全局作用域或者是内置作用域

## 7.2.应用举例
```
#被nonlocal使用的变量只能且必须出现在def嵌套中
spam = 99
def tester():
    def nested():
        nonlocal spam
        print('Current=', spam)
        spam + = 1
    return nested

...
SyntaxError: no binding for nonlocal 'spam' found 
#nonlocal限制作用域查找仅为嵌套的def，nonlocal不会在嵌套的模块的全局作用域或者是所有def之外的内置作用域中查找



#默认情况下，不允许修改def作用域中的名称
def tester(str):
    state  = str
    def nested(lable, state):
        print(label, state)
        state += 1                        #因为将state当做是函数nested的本地变量,state在使用之前没有赋值,所以此处是不能使用的
    return nested
..
UnboundLocalError:local variable 'state' referenced before assignment



#nonlocal语句允许在内存中保持可变状态的多个副本(使用nonlocal进行修改)
def tester(str):
    state  = str        #赋值操作
    def nested(lable, state):
        nonlocal state    #声明为nolocal,那么就去嵌套的def中找
        print(label, state)
        state += 1                        
    return nested

...
F = tester(0)                              #F函数记住了内嵌函数中的state，初始化状态为0
F('spam')
>>spam 0
F('ham')
>>ham 1

G = tester(42)                                 #G函数记住了内嵌函数中的state，初始化状态为42
G('spam')
>>spam 42
G('ham')
>>ham 43

F('bacon')
>>bacon 3


#因为F函数和G函数分别记住了内嵌函数的state的不同的初始化值，所以存在多个副本的情况

```





