---
title:  python语法之if
categories: python   
toc: true  
tags: [python]
---



 # 1.语法格式
```
if <test1>:
    <statement1>
elif <test2>:
    <statement2>
else:
    <statement3>

```

# 2.基本例子
```
#Example1
if 1:                                                #只有if
    print('true')

#Example2
if not 1:                                    #只有if /else
    print('true')
else:
    print('false')



#Example3
if x == 'a':                        #多路分支
    print('a')
elif x=='b':
    print('b')
else:
    print('c')


#Example4
branch = {'spam':1.23 , 'ham':2,33, 'eggs':4.33}
choice = 'ham'
if choice in branch:                     #判断dict中有没有对应的键匹配
    print(branch[choice])
else:
    print('Bad choice')

```

# 3.语法规则
* 语句是逐个执行的，除非你不这样编写
* 块和语句的边界会自动检测
* 复合语句=首行+“：”+缩进语句
* 空白行、空格、以及注释通常都会忽略
* 文档字符串会被忽略，但会保存并由工具显示





# 4.语句分割符
* 如果使用语法有<font color=red>括号对</font>,那么语句就可横跨数行，如：封闭的（）、{}、[]
* 如果语句以反斜线结尾，就可横跨多行（不推荐使用）
* 字符串常量有特殊规则，如：三引号
* 其它
使用分号终止语句，那么可以把一个以上的简单语句挤进单个行中
注释和空白行可以出现在文件的额任意之处

举例
```
#Example1
L = ['Good',
    'Bad',
    'Ugly']


#Example2
if (a ==b and c ==d 
    and d ==e and e ==f
   )

#Example3
x =1; y = 3; print(x)


#Example4
S = '''                           #也可以使用三个双引号
chenyansong is a good boy
but is a good ...
'''
```



# 5.真值测试

## 5.1总结
* 任何非零数字和非空对象都为空
* 数字零以及空对象以及特殊对象None都被认为是假
* 比较和相等测试会递归地引用到数据结构中
* 比较和相等测试会返回True 或False
* 布尔and 和or会返回真或假的<font color=red>操作对象</font>
X and Y 
    如果X和Y都为真，就返回第一个对象X
X or Y
    如果X或Y，有一个为真，就返回第一个为真的对象
not X
    如果为假，那么就返回真（表达式返回的是True或False）

## 5.2.Example
```
>>2<3, 2>3                                #比较返回True 或False
(True, False)


>>2 or 3 , 3 or 2            #布尔返回的是对象
(2, 3)

>>[] or 3
3

>>[] or {}
{}

>>2 and 3, 3 and 2
(3, 2)

>>[] and {}                    #第一个对象为空，就不会进行后面的运算，直接返回了
[]

>>3 and []             #第一个对象不为空，所以返回的是第二个对象
[]



```



# 6.if/else三元表达式
A = Y if X else Z
<font color=red>只有当X为真的时候，python才会执行表达式Y,而只有当X为假的时候，才会执行表达式Z</font>

```
>>A = 't' if 'spam' else 'f'
>>A
't'

>>A = 't' if  '' else 'f'
>>A
'f'

```




