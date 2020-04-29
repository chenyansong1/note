---
title: 第二章 scala入门初探
categories: scala   
tags: [scala,scala编程]
---

# 1.变量定义
先写变量后写类型

```
scala> val msg: java.lang.String = "hello world!"
msg: String = hello world!

//或者
scala> val msg: String = "hello world!"        #先写变量名称,再写变量类型
msg: String = hello world!    #定义了一个名称为msg的变量,类型为String, 值为 "hello world!"

scala>

```

<!--more-->


自动类型推断

```
#也可以省略类型的定义,scala会自动推断出变量的类型
scala> val msg = "hello world!"    #因为值的类型是String,所以可以推断出msg的类型是String,所以在定义变量的时候可以省略对msg类型的推断
msg: String = hello world!        

scala>

```

val类型变量不能修改,var可以

```
#val类型的变量不能再次对其进行赋值操作

scala> msg = "goodby cruel world!"
<console>:8: error: reassignment to val
       msg = "goodby cruel world!"
           ^


#var可以重复赋值

scala> var msg_var: String = "hello world!"
msg_var: String = hello world!

#对变量重新赋值
scala> msg_var = "hello 2222 world!"
msg_var: String = hello 2222 world!

scala>

```


# 2.函数定义
函数的基本结构

```
def max(x: Int, y:Int): Int = {
  if(x > y) x else y
}
#scala的条件表达式可以像Java的三元操作符那样生成结果值
if(x > y) x else y      #scala中的if/else不仅控制语句的执行流程,同时有返回值,所以他不等同于Java中的if/else
#等同于Java中的:
(x > y)? x: y
```

![](http://ols7leonh.bkt.clouddn.com//assert/img/scala_programming/2/function.png)


函数的返回值类型

```
#函数的返回值类型可以不用写,因为可以使用函数体推断出来,但是如果函数是递归的,那么必须明确的说明返回值的类型

#尽管如此,显示的说明函数结果类型也经常是一个好主意,这种类型标注可以使代码便于阅读,因为读者不用研究函数体之后再去猜测结果类型

```

函数体

```
如果函数仅包含一个语句,那么连花括号都可以选择不写,这样max函数就可以写成:
def max2(x: Int, y:Int): Int =  if(x > y) x else y

```


没有参数和返回值的函数

```
  
scala> def max2(x: Int, y:Int): Int =  if(x > y) x else y
max2: (x: Int, y: Int)Int
 
scala> def greet() = println("Hello world!")
greet: ()Unit        #greet是函数名,  ()说明函数不带参数,  Unit是greet的结果类型,指的是函数没有有效的返回值



```

# 3.编写scala脚本

```
#在hello.scala文件中,有如下的代码:
println("hello world , from a script! ")

#执行上述脚本文件
$ scala hello.scala

#系统输出
hello world , from a script! 


#传参,scala脚本的命令行参数保存在名为args的scala数组中
println("hello world , from " + args(0) + " a script! ")      //注意scala中的数组是以()去取元素的,而不是像Java中是以[]去取元素


#再次执行脚本
$ scala hello.scala xxxx
hello world , from xxxx a script! 


```


# 4.用while循环,用if做判断

```
#在printargs.scala文件里,输入以下代码测试while
var i = 0
while (i < args.length) {
  println(args(i))
  i += 1
}

#上述代码并不是scala推荐的代码风格,在这里只是有助于解释while循环
#在scala中并没有++/--,必须写成+= / -=这样的操作


```




# 5.用foreach和for做枚举

foreach

```
#上面所写的while循环的编码风格被称之为指令式编程(即:逐条执行指令,并经常改变不同函数之间的共享状态,在Java/C++/C这些语言中常见),在scala中更偏向的是函数式编程,如下:

#在pa.scala文件中,如下代码:
args.foreach(arg=>println(arg))

#执行
$ scala pa.scala xx1 xx2 xx3

#打印
xx1
xx2
xx3

#在上述例子中,scala解释器可以推断arg的类型为String,因为String是调用foreach的那个数组的元素类型,当然也可以更明确的给args加上类型名:
args.foreach( (arg: String) =>println(arg))


#更加简洁的写法

args.foreach(println(arg))

```


![](http://ols7leonh.bkt.clouddn.com//assert/img/scala_programming/2/function_2.png)
 

for

```
#scala也是提供了像指令式的for

for (arg <- args){
  println(arg)
}
#<- 右侧是已经在前面见过的args数组,<-左侧的arg是val的名称(不是var,这里一定是val),尽管arg可能感觉像var,因为每次枚举都会得到新的值,但这的确是val,因为他不能在for表达式的函数体中被重新赋值,所以,对于args数组的每个元素,枚举的时候都会创建并初始化新的arg值,然后调用执行for的函数体



```

# 6.数组Array

```
val greetStrings = new Array[String](3)
greetStrings(0) = "hello"    //访问数组元素是使用圆括号,而不是像Java一样使用的是[]
greetStrings(1) = ","
greetStrings(2) = "world!"

for (i <- 0 to 2) {
  println(greetStrings(i))
}

#当同时使用类型和值参数化实例的时候,应该首先写方括号和类型参数,然后再写圆括号和值参数
#以上代码并不是scala推荐的创建和初始化数组的推荐方式


#因为scala的类型推断,所以有如下的代码
val greetStrings: Array[String] = new Array[String](3)// 完整定义的形式能够更有效的说明类型参数(方括号包含的类型名)是实例类型的组成部分,而值参数(圆括号包含的值)不是,即:greetString的类型是Array[String] ,不是Array[String](3)


#变量val
用val定义的变量不能被重新赋值,但变量指向的对象内部却可以改变,所在在本例中,greetString对象不能被重新赋值成别的数组,他将永远指向初始化时指定的那个Array[String]实例,但是Array[String]的内部元素始终能被修改,因此数组本省是可变的

```

scala中没有运算符重载的问题

```
scala没有操作符重载的问题,因为它根本没有传统意义上的操作符,取而代之的是,诸如:+/-/*等遮掩的字符,可以用来做方法名
因此在scala中输入 1+2的时候,实际上是在Int对象1上调用名称为+的方法,并把2当做参数传给他,如下图:

```


![](http://ols7leonh.bkt.clouddn.com//assert/img/scala_programming/2/option.png)


 
使用圆括号访问数组元素的原因

```
val greetStrings = new Array[String](3)
greetStrings(0) = "hello"    //访问数组元素是使用圆括号,而不是像Java一样使用的是[]
greetStrings(1) = ","
greetStrings(2) = "world!"
/*

数组只是类的实例(将greetStrings看做是对象),用括号传递给变量一个或多个值参数时,scala会把它转换成对apply方法的调用,于是greetString(i)转换成greetString.apply(i),所以scala里访问数组的元素也只不过是跟其他方法一样的调用,这个原则不是只对数组: 任何对于对象的值参数应用将都被转换为对apply方法的调用,当然前提是这个类型实际定义过apply方法,所以这不是特例,而是通用法则

与之相对应,当对带有括号并包含一到若干参数的变量赋值时,编译器将使用对象的update方法对括号里的参数(索引值)和等号右边的对象执行调用

*/
greetStrings(0) = "hello"
#将被转化为:
greetStrings.update(0, "hello")    #也是讲greetStrings看做是对象,update只是调用对象的方法


#综上代码可以写成:
val greetStrings = new Array[String](3)
greetStrings.update(0,"hello")
greetStrings.update(1,",")
greetStrings.update(2,"world!")

for(i <- 0.to(2)){
  println(greetStrings.apply(i))
}


```

 简介的构建数组的方式
 
```
val numNames = Array("zero", "one", "two")    //编译器根据传递的值类型(字符串)推断数组的类型是Array[String]

#实际上是调用
val numNames2 = Array.apply("zero", "one", "two")    //在Array的伴生对象中有apply方法


```

# 7.列表list

```
/*
scala数组是可变的同类对象序列,例如:Array[String]的所有对象都是String,而且尽管数组在实例化之后长度固定,但是他的元素值却是可变的,所以说数组是可变的
list是不可变的同类对象序列,scala的scala.List不同于Java的java.util.List,一旦创建了就不可改变,实际上scala的列表是为了实现函数式风格的编程而设计的,
*/
val oneTwoThree = List(1, 2, 3)

val oneTwo = List(1, 2)
val threeFour = List(3,4)
val oneTwoThreeFour = oneTwo ::: threeFour    //用新的值重建了列表然后返回

```

 向列表添加单个元素
 
```
val twoThree = List(1, 2)
val oneTwoThree = 1 :: twoThree
/*
表达式"1 :: twoThree"中, ::是右操作数twoThree的方法,即:方法的调用者是twoThree,
1是方法的传入参数,因此可以写成:twoThree.::(1)
* */

```

 
 Nil形成新的列表
 
```
//因为Nil是空列表的简写,所以可以使用::操作符把所有元素都串联起来,并以Nil作结尾来定义新列表,例如可以用以下方法产生与上文同样的输出:"List(1,2,3)"
val oneTwoThree = 1::2::3::Nil #之所以这样写，是因为Nil就是一个List，所以最终生成的是一个List   

```

 列表不支持append操作的解决方法
 
```
List类没有提供append操作,因为随着列表变长,append的耗时将呈现线性增长,而使用::做前缀则仅仅耗时固定的时间,如果你想通过添加元素来构造列表,你的选择可以如下:
1.先使用前缀(::)去构建一个list,在调用reverse
2.使用ListBuffer,一种提供append操作的可变列表,完成之后用toList


```

List 的一些方法

```
//空List
List() //或者 Nil

//创建带有三个值的新的List[String]
List("cool", "tools", "rule")

//创建带有三个值的新的List[String]
val thrill = "Will"::"fil"::"until"::Nil

//叠加两个列表(返回带"a","b", "c", "d"的新List[String] )
List("a", "b") ::: List("c", "d")

//获取list元素(返回thrill列表上索引为2的元素,从list是0开始)
thrill(2) //"until"

//count函数,计算满足条件的list的元素的个数
thrill.count(s=>s.length == 4) //计算长度为4的String元素的个数

//删除元素并返回新列表
thrill.drop(2)  //返回去掉前两个元素的thrill列表(List("until") )
thrill.dropRight(2) //返回去掉后两个元素的thrill列表( List("Will") )

//exists判断是否存在某个值
thrill.exists(s=>s=="until" )  //判断是否有值为"until"的字符串元素在thrill里

//过滤掉指定条件的元素,返回新的list
thrill.filter(s=>s.length==4) //新的列表: List("Will", "fill")

//
thrill.forall(s => s.endsWith("1"))//判断是否thrill列表里的所有元素都以"1"结尾

//遍历每一个list元素
thrill.foreach(s => print(s))
thrill.foreach(print) //上面形式的简写

//取第一个元素
thrill.head   //返回thrill列表的第一个元素( "Will" )

//返回列表的最后一个元素
thrill.last //"until"

thrill.init //返回thrill列表除最后一个以外其他元素组成的列表( List("Will", "fill") )

//返回除第一个元素之外依次组成的新列表
thrill.tail // List("fill", "until")


//判断thrill列表是否为空
thrill.isEmpty  // false

//返回列表元素的数量
thrill.length   //3

//遍历每一个元素,形成新的列表
thrill.map(s => s+"y" ) //返回由thrill列表里每一个String元素都加了"y"构成的列表 (List("Willy","filly", "untily") )

//返回由list元素组成的字符串
thrill.mkString(", ") //"Will, fill, until"

//翻转list,形成新列表
thrill.reverse  //返回翻转之后的新列表: List("until","fill","Will")


```

# 8.元组Tuple

元组也是很有用的容器对象,与列表一样,元组也是不可变的,但与列表不同元组可以返回不同类型的元素,例如列表只能写成List[Int] 或者 List[String] 但元组可以同时拥有Int和String,

元组的应用场景:如:方法里返回多个对象

```
val pair = (99, "Luftballons")
//元组的访问
println(pair._1)  //99

//元组的实际类型取决于他含有的元素数量和这些元素的类型
(99,"ddd") 的类型是Tuple2[Int, String]
('u','r',"ddd",1)的类型是Tuple4[Char,Char,String,Int]

```

 访问元组的元素
 
```
你或许想知道为什么不能用列表的方法来访问元组,如pair(0) ,那是因为列表的apply方法始终返回同样的元素,但元组的类型不尽相同, _1的结果类型可能与_2的不一致,诸如此类,因此两个的访问方法也不一样,此外,这些 _N 的索引是基于1的,而不是基于0的,这是因为对于拥有静态类型元组的其他语言,如Hashkell和ML,从1开始是传统的设定


val pair = (99, "Luftballons")
//元组的访问
println(pair._1)  //99

```

# 9.使用集(set) 和映射(map)

```
/*
在scala中的set和map来说,同样有可变和不可变,不过并非提供两种类型,而是通过继承的差别把可变差异蕴含其中

如下图HashSet类,各有一个扩展了可变的和另一个扩展不可变的Set特质,(Java里面称为"实现"接口,而在scala中称为"扩展" 或者"混入" 了特质)
*/

```

![](http://ols7leonh.bkt.clouddn.com//assert/img/scala_programming/2/set.png)
 
简单的实例代码

```
var jetSet = Set("Booing", "Airbus")
jetSet += "Lear"
println(jetSet.contains("Cessna"))
/*
第一行:
   定义了名为jetSet的新变量,并初始化为包含两个字符串"Booing" 和"Airbus"的不可变交集
scala中创建jetSet的方法与创建list和array的类似,通过调用jetSet伴生对象的apply工厂方法,
在上面的例子中,对scala.collection.immutable.Set的伴生对象调用了apply方法,返回了默认的不可变Set的实例,scala编译器推断其类型为不可变Set[String]

第二行:
    加入新变量,可以对jetset调用,并传入新元素,可变的和不可变的集都提供了+方法,但结果不同,可变集把元素加入到自身,而不可变集则创建并返回包含了添加元素的新集,上述程序中使用的是不可变集,因此+调用将产生一个全新的集,所以只有可变集提供的才是正真的+=方法,不可变集不是:
jetSet += "Lear" 实际上是下面方法的简写形式
jetSet = jetSet + "Lear"
因此这里实际是用包含了 "Booing" , "Airbus" , "Lear" 的新集重新赋值给了jetSet变量(因为jetSet变量是可变的,所以可以重新赋值)
 */

```

 定义可变Set,需要加入引用import
 
```
import scala.collection.mutable.Set
val movieSet = Set("hitch", "Poltergeist")
movieSet += "Shrok"
println(movieSet)

```



 map

map和set一样,也是采用了类继承机制,提供了可变和不可变的两种版本的额Map,如下图:

![](http://ols7leonh.bkt.clouddn.com//assert/img/scala_programming/2/map.png)
 
```
import scala.collection.mutable.Map
val treasureMap = Map[Int,String]()

treasureMap += {1 -> "Go to island."} //1 -> "Go to island." 转换为(1).->("Go to island.") ,scala的任何对象都能调用->方法,并返回包含键值对的二元组
treasureMap += {2 -> "Find big X on ground"}
treasureMap += {1 -> "Dig."}

println(treasureMap(2))


//如果没有import,那么默认使用的Map是不可变的
val romanNumberal = Map(1->"1", 2->"II", 3->"III",4->"IV", 5->"V")
println(romanNumberal(4)) //打印 "IV"

```

# 10.认识函数式风格

```
/*
首先要理解指令式编程和函数式编程在代码风格上的差异,大致上说,如果代码包含了var变量,那么他可能就是指令式的风格,如果代码根本没有var ----就是说仅仅包含val ---- 那他或许是函数式的风格,因此向函数式风格转变的方式之一,就是尝试不用任何var编程
*/

#指令式风格

def printArgs(args: Array[String]):Unit = {
  var i = 0
  while(i<args.length){
    println(args(i))
    i += 1
  }
}


#函数式风格
//通过去掉var的办法把代码变得更函数式风格
def printArgs2(args: Array[String]):Unit = {
  for(arg <- args){
    println(arg)
  }
}

//或者像这样
def printArgs3(args: Array[String]):Unit = {
  args.foreach(println)
}


/*
这个例子说明了减少使用var的一个好处,重构后的代码比原来(指令式)的代码更加的简洁,明白,也更少有机会犯错,

上述的代码有修改的余地,重构之后的printArgs方法并不是纯函数式的,因为他有副作用----本例中的副作用就是打印到标准输出流,识别函数是否有副作用的地方就在于其结果类型是否为Unit,如果某个函数不返回任何有用的值,也就是说如果返回类型为Unit,那么这个函数唯一能产生的作用就只能是通过某种副作用,而函数风格的方式应该是定义对打印的arg进行格式化的方法,不过仅返回格式化之后的字符串,如下:
*/

def formArgs(args: Array[String]) = args.mkString("\n")
/*
以上才是正真的函数式编程的风格,完全没有副作用或var的mkString方法,能在任何可枚举的集合类型上调用

没有副作用的好处:
举例来说:要测试前面给出的任何一个有副作用的printArgs方法,你将需要重新定义println,捕获传递给他的输出,在检查结果,相反,formatArgs来说你可以直接检查他的返回结果,如下:
*/
def formArgs(args: Array[String]) = args.mkString("\n")
val res = formArgs(Array("one", "two", "zero"))
assert(res == "one\ntwo\nzero") //assert方法检查传入的Boolean表达式,如果结果为假,抛出AssertionError,否则assert就什么也不做,安静的返回


```


scala程序员的平衡感

```
崇尚val, 不可变对象和没有副作用的方法
首先想到他们,只有在特定需要和并加以权衡之后才选择var, 可变对象和有副作用的方法
```


# 11.从文件里读取文本行

```
import scala.io.Source
if(args.length>0){
  //Source.fromFile(args(0))尝试打开指定的文件并返回Source对象,之后对他调用, getLines函数,返回Iterator[String] ,枚举每次提供一行文本,包括行结束符
  for(line <- Source.fromFile(args(0)).getLines()){
    println(line.length + "" +line)
  }
}else{
  Console.err.println("Please enter filename")
}

/*
toList 是必须加的,因为getLines方法返回的是枚举器,一旦完成遍历,枚举器就失效了,而通过调用toList 把它转换为List,我们把文件中的所有行全部存储在内存中,因此可以随时使用,lines变量因此就指向着包含了指定文件的文本字符串列表
*/

```
