---
title: 第十一章 scala的层级
categories: scala   
tags: [scala,scala编程]
---


&emsp;在scala里,每个类都继承自通用的名为Any的超类,因为所有的类都是Any的子类,所以定义在Any中的方法就是"共同"的方法,他们可以被任何对象调用,scala还在层级的底端定义了一些有趣的类,如:Null,和Nothing,来扮演通用的子类,例如:如同Any是所有其他类的超类,Nothing是所有其他类的子类

<!--more-->


# 1.scala的类层级
```
/*
层级的顶端是Any类,定义了如下的方法
*/

final def ==(that: Any): Boolean
final def !=(that: Any): Boolean
def equals(that: Any): Boolean
def hashCode: Int
def toString: String

/*
因为每个类都继承自Any,所以scala程序里的每个对象都能用== , != , 或equals比较,
用hashCode来做散列,以及用toString来格式化,Any类里的等号和不等号方法被声明为final,
因此他们不能在子类里重写,实际上,==总是和equals相同,!=总是与equals相反因此独立的类可以
通过重写equals方法改变==或!=的意义
*/


/*
根类Any有两个子类,AnyVal和AnyRef,AnyVal是scala里每个内建值类的父类,
有9个这样的值类,:Byte,Short,Char, Int, Long, Float, Double, Boolean, 和Unit, 
其中的前8个都对应到Java的基本类型,他们的值在运行时表示成Java的基本类型的值,
scala里这些类的实例都写成字面量,如: 42是Int的实例, 'x' 是Char的实例,
false是Boolean的实例,你不能使用new创造这些类的实例,因为值类被定义成既是抽象的又是final的,
因此不能写成 new Int
*/
```


![](http://ols7leonh.bkt.clouddn.com//assert/img/scala_programming/11/1.png)


```
/*
另一个值类,Unit,大约对应于Java的void类型,被作用于不返回任何有趣结果的方法的结果类型,
Unit类型只有一个实例值,写成()
*/


/*
类Any的另一个子类是类AnyRef,这个是scala里所有引用类的基类,正如前面提到的,
在Java平台上AnyRef实际就是类java.lang.Object 的别名,
因此Java里写的类和scala里写的类都继承自AnyRef,推荐使用AnyRef
*/


/*
scala类与Java类的不同在于他们还继承自一个名为ScalaObject的特别的记号特质,ScalaObject只是包含了一个方法,名为$tag,在内部使用以加速模式匹配
*/
```

# 2.原始类型是如何实现的
```
//java5的自动装箱
//Java代码
boolean isEqual(int x , int y){
    return x == y
}
system.out.println(isEqual(444,444))

//打印结果
true


//现在讲int变成java.lang.Integer (或者Object 对象), 以下是Java代码
boolean isEqual(Integer  x , Integer  y){
    return x == y
}
system.out.println(isEqual(444,444))

//打印结果
false    //原因是数字444被装箱了两次,因此参数x和y是两个不同的对象,而==表示的是引用相等,所以结果为false
//上述情况说明Java不是纯粹的面向对象语言的一个方面,因为我们能够清楚的观察到基本类型和引用类型之间的差别



//scala里的尝试试验
def isEqual(x: Int, y:Int) =  x == y        //使用的是基本类型
//调用
isEquals(444,444)    //true

def isEqual(x: Any, y:Any) = x == y    //使用的是引用类型
//调用
isEquals(444,444)    //true


/*
在scala里为何基本类型和引用类型的结果是一样的?
原因是基本类型和引用类型都是继承自Any,所以结果一样
*/


/*
实际上在scala里的相等操作==被设计为对类型表达式透明,
对值类型来说,就是自然的(数学或布尔)相等,
对引用类型,==被视为继承自Object的equals方法的别名,equals就是比较的是内容
*/

/*
然而,有些情况你需要使用引用相等代替用户定义的相等,
例如:某些时候效率是首要因素,你想要把某些类散列合并然后通过引用相等比较他们的实例
*/
val x  = new String("abc")
val y =  new String("abc")
x == y        //true
x eq y        //false
x ne y         //true
/*
上述代码中,==比较的是值相等(和equals类似),而eq使用的是引用相等
*/
```

# 3.底层类型
```
/*
在scala类型层级的底部有两个雷scala.Null 和 scala.Nothing 
他们是用统一的方式处理scala面向对象类型系统的某些"边界问题" 的特殊类型

Null类是null引用对象的类型,他是每个引用类的子类,Null不兼容值类型,
如,你不能把null值赋给整数变量
*/
val i: Int = null    //error


/*
Nothing 类型在scala的类层级的最底端,他是任何其他类型的子类型,然而,根本没有这个类型的任何值,
那么要一个没有值的类型有什么意思呢?
Nothing的一个用处是他标明了不正常的终止,例如scala的标准库中的Predef对象有一个error方法,如下定义:
*/

def error(message: String): Nothing = 
    throw new RuntimeException(message)

/*
error的返回类型是Nothing,告诉用户方法不是正常返回的,因为Nothing是任何其他类型的子类,
所以你可以非常灵活的使用像error这样的方法:
*/
def divide(x: Int, y: Int): Int = 
    if(y != 0) x/y
    else error("can't divide by zero")

/*
如果执行了else,调用了error,类型为Nothing,因为Nothing是任何类型的子类型,
也是Int的子类型,所以整个状态语句的类型是Int,正如需要的那样
*/
```



