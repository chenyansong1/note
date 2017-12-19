---
title:  python语法之格式简介
categories: python   
toc: true  
tags: [python]
---



# 1.python中增加了什么？
python中新的语法成分是冒号（：），所有python的复合语句都有相同的一般形式，也就是首行以冒号结尾，首行下一行嵌套的代码往往按缩进的格式书写，如下所示：
```
Header line:                                            #首行以冒号结尾
    Nested statement block                       #缩进的格式书写
```



# 2.python删除了什么？
## 2.1括号是可选的
```
if(x<y)

if x<y                                #推荐使用
```

## 2.2.终止行就是终止语句（不需要分号）
python中需要要像C语言那样用分号终止一行
```
x = 1;
```
字python中一行的结束会自动终止在该行的语句，即，可以省略分号
```
x = 1                                     #推荐不使用分号
```

## 2.3.缩进的结束就是代码块的结束
python不在乎怎么缩进，缩进多少，语法规则只不过是给定<font color=red>一个单独的嵌套块中所有的语句都必须缩颈相同的距离</font>
```
#C语言写法
if (x>y){
    x = 1;
    y = 2;
}

#python写法
if x>y:
    x = 1
    y =2

```




