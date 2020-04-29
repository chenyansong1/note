---
title: 第五章 基本类型和操作
categories: scala   
tags: [scala,scala编程]
---



# 1.基本类型

 
在scala中有如下的基本类型:Byte,Short,Int,Long,Char,String, Float, Double, Boolean,除了String归于java.lang包之外,其余所有的基本类型都是包scala的成员,如,Int的全名是scala.Int,然而,**由于包scala和Java.lang的所有成员都被每个scala源文件自动引用,因此可以在任何地方只用简化名(也就是说,直接写成Boolean,Char或String)**

目前实际上scala值类型可以使用与Java的原始类型一致的小写化名称,比如,scala程序里可以用int替代Int,但请记住他们都是一回事,scala.Int, scala的社区实践提出的推荐风格是一直使用大写形式,这也是本书推荐的,将来scala的版本可能不再支持乃至移除小写化名称,因此跟随社区的趋势,在scala代码中使用Int而非int才是明智之举


<!--more-->

# 2.字面量

字面量就是直接下载代码里的常量

```
/*整数字面量
类型Int,Long, Short和Byte的整数字面量有三种格式,十进制,十六进制和八进制,整数字面量的开头方式说明了数值的进制,如果数开始于0x或0X,那他是十六进制,并且可能包含从0到9,及大写或小写的从A到F的数字,如下:
*/
val hex = 0x5
val hex2 = 0x00FF
val magic = 0xcafebabe


/*
八进制
如果数开始于0,就是八进制的,并且只可以包含数字0到7
*/
val oct = 035
val no = 0777
val dec = 0321


/*
十进制
如果数字开始于非零数字,并且没有被修饰过,就是十进制的,例如:
*/
val dec1 = 11
val dec2 = 255

/*
长整形:Long
如果整数字面量结束于L或者l
*/
val prog = 0xcafebael
val tower = 35L
val of = 31l    //小写l

/*Short, Byte
如果Int类型的字面量被赋值给Short或Byte类型的变量,字面量就会被当做是赋值的类型,以便让字面量值处于有效范围内,如:
*/
val little: Short = 367
val little: Byte = 38


/*
浮点数字面量
浮点数字面量是由十进制数字,可选的小数点,可选的E或e及指数部分组成的,举例如下:
*/
val big = 1.2345
val bigger = 1.2343L
val biggerStill = 123E45    //123E45就是123乘以10的45次幂


/*
如果浮点数字面量以F或f结束,就是Float类型的,否则就是Double类型的,可选的,Double浮点数字面量也可以是D或者d结尾的,举例如下:
*/
val little = 1.234F    //1.234
val littleBigger = 3e5f    //300000.0
val yetAnother = 3e5D


/*
字符字面量,可以是在单引号之间的任何Unicode字符,如:
*/
val a = 'A'
val c = '\101'  //单引号之间除了可以摆放字符之外,还可以提供一个前缀反斜杠的八进制或十六进制的表示字符编码号的数字,八进制数必须在'\0' 和'\377'之间
val d = '\u0041'     //\u表示十六进制

```
 特殊的转义字符
 
```
/*
\n        换行
\b        回退
\t        制表符
\f        换页
\r        回车
\"        双引号
\'        单引号
\\    反斜杠

*/

val backslash = '\\'


```

```
/*
字符串字面量:字符串字面量是由双引号包括的字符组成
*/

val escapes = "\\\"\'"    //字符串: \"'

/*
三引号:以一行里的三个引号作为开始和结束,内部的原始字符串可以包含无论何种任意字符,包括新行,引号,和特殊字符,举例如下:
*/

println("""
Welcome to Ultamix 3000.
Type "Help" for help.
"""
)

/*
#输出如下:
Welcome to Ultamix 3000.
      Type "Help" for help.
    
第二行的前导的空格被包含在了字符串里,为了解决这个常见情况,字符串类引入了stripMargin方法,使用的方式是,把管道符号(|)放在每行前面,然后对整个字符串调用stripMargin,如下:
*/
println(
  """
    |Welcome to Ultamix 3000.
    |Type "help" for help
  """.stripMargin)

/*输出如下:
Welcome to Ultamix 3000.
Type "help" for help
*/

```

# 3.操作符和方法
```
val sum = 1+2    //scala调用了(1).+(2)

/*
实际上+包含了各种类型的重载方法,像+这样的操作符叫做中缀操作符,这样的操作符还有indexOf
*/
val s = "hello, world!"
s indexOf '0'    //调用s.indexOf('o')

//另外String还提供了重载的indexOf方法,带两个参数,分别是要搜索的字符和从哪个索引开始搜索
s indexOf ('0', 5)    //调用了s.indexOf('0', 5)


/*
任何方法都可以是操作符
scala里的操作符不是特殊的语法,任何方法都可以是操作符到底是方法还是操作符取决于你如何使用它,如果写成s.indexOf('o') , indexOf是方法,但是如果写成s indexOf 'o' ,那么indexOf就是操作符,因为你以操作符标注方式使用它
*/

/*前缀标注和后缀标注
方法名放在调用的对象之前,如, -7里的"-" 
后缀标注中,方法放在对象之后,如 7 toLong 里的"toLong"
*/



/*
方法名在操作符上前缀"unary_",例如scala表达式-2.0 转换成方法调用 (2.0).unary_-  

操作符中能作为前缀操作符用的有值+.-,!,和~ 因此,如果对类型定义了名为unary_! 的方法,就可以对值或变量用!p这样的前缀操作符方式调用方法,但是即使定义了名为unary_*的方法,也没有办法将其使用成操作符了,因为*不是四种可以当做前缀操作符用的标识符之一,你可以向平时那样调用它,如:p.unary_* ,但是如果尝试像*p这么调用,scala就会把它理解为*.p ,者或许就不是你所期望的了

*/



/*
后缀操作符是不用点或括号调用的不带任何参数的方法,在scala里,方法调用的空括号可以省略,惯例是如果方法带有副作用就加上括号,如println(), 如果没有副作用就去掉括号,如String 的toLowerCase
*/
val s = "hello, world"
s.toLowerCase        //方法里没带参数,因此还可以去掉点,采用后缀操作符标注方式
s toLowerCase        //toLowerCase被当做操作数s的后缀操作符

```


# 4.对象相等性

```
/*如果要比较一下两个对象是否相等,可以使用==,或者他的反义 != */
1 == 2    //false
1 != 2 //true
2 == 2 //true
//以上这些操作对所有对象都起作用,而不仅仅是基本类型,

List(1, 2, 3) == List(1, 2, 3)   //true
List(1, 2, 3) == List(4,5,6)   //false
1 == 1.0    //true
List(1, 2, 3) == "hello"   //false
List(1, 2, 3) == null   //false
null == List(1, 2, 3)   //false
/*
== 已经被仔细加工过,因此多数情况下都可以实现合适的相等性比较,这种比较遵循一种非常简单的规则,首先检查左侧是否为null,如果不是调用左操作数的equals方法,而精确的比较取决于左操作数的equals方法定义,由于有了自动的null检查,因此不需要手动再检查一次了

这种比较即使发生在不同的对象之间也会产生true,只要比较的两者内容相同并且equals方法是基于内容编写的,例如,以下是恰好都有五个同样字母的两个字符串的比较:
("he" + "llo") == "hello"        //true
*/

```

 scala的==与Java的有何区别
 
```
/*
Java里==既可以比较原始类型也可以比较引用类型,对于原始类型,Java的==比较值的相等性,与scala一致,而对于引用类型,Java的==比较了引用相等性,也就是说比较的是这两个变量是否都指向JVM堆里的同一个对象,scala也提供了这种机制,scala也提供了这种机制,名字为eq, 不过eq和他的反义词ne,仅仅应用于可以直接映射到Java的对象
*/
```

# 5.操作符的优先级和相关性

```
/*
操作符的优先级决定了表达式的哪个部分先于其他部分被评估,举例来说,表达式2+2 * 7 计算得16,而不是28,因为*操作符比+操作符有更高的优先级,

由于scala没有操作符,实际上,操作符只是方法的一种表达方式, 对于操作符形式使用的方法,scala根据操作符的第一个字符判断方法的优先级(这个有个例外) 比方说,如果方法名开始于* ,那么就比开始于+的方法有更高的优先级,因此2+2*7将被评估为2+(2*7), 而a+++b***c 将被看做是a+++(b***c) ,因为***方法比+++方法有更高的优先级
*/

```

 操作符优先级
 
```
/*
(所有其他的特殊字符)
* / %
+
:
=  !
<>
&
^
|
(所有字母)
(所有赋值操作符)


以上以降序方式列举了以方法第一个字符判断的优先级,同一行的字符具有相同的优先级
*/

2 << 2+2    //+比<<的优先级高,表达式也要先调用了+方法之后在调用<<方法,如: 2 << (2+2)

//特例: *=的操作符的优先级与赋值符号(=)相同,也就是说,他比任何其他操作符的优先级都低,类似的操作符还有 += -= /= ,虽然*比+的优先级高,但是我们这里将 *= 当做是一个整体
x *= y + 1


```

```
/*
当同样优先级的多个操作符并列出现在表达式里时,操作符的关联性决定了操作符分组的方式,scala里操作符的关联性取决于他的最后一个字符,例如:任何以":" 字符结尾的方法由他的右操作数调用,并传入左操作数,以其他字符结尾的方法与之相反,他们都是被左操作数调用,并传入右操作数的,因此a*b 变成 a.*(b) 但是a:::b 变成b.:::(a)
*/




/*
然而不管操作符具有什么样的关联性,它的操作数总是从左到右评估的,如:a:::b将会被当做是:
{val x = a; b.:::(x) }
在这个代码块中,a仍然在b之前被评估,然后评估结果被当做操作数传给b的:::方法

a:::b:::c        // a:::(b:::c)
a*b*c        //(a*b)*c
*/
```



# 6.富包装类      

下面方法的使用要通过隐式转换,现在需要知道的是本章介绍过的每个基本类型,都对应着一个"富包装器"提供的许多额外的方法

代码				|结果		|基本类型	|富包装
:--------:|:-----:|:---------:|:----:
0 max 5				|5			|Byte		|scala.runtime.RichByte
0 min 5				|0			|Short		|scala.runtime.RichShort
-2.7 abs			|2.7		|Int		|scala.runtime.RichInt
-2.7 round			|-3L		|Long	|scala.runtime.RichLong
1.5 isInfinity		|false		|Char		|scala.runtime.RichChar
(1.0/0) isInfinity	|true		|String		|scala.runtime.RichString
4 to 6				|Range(4,5,6)	|Float	|scala.runtime.RichFloat
"bob" capitalize 	|"Bob"		|Double		|scala.runtime.RichDouble
"robet" drop 2		|"bert'		|Boolean		|scala.runtime.RichBoolean












