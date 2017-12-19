---
title: 第七章 内建控制结构
categories: scala   
tags: [scala,scala编程]
---


&emsp;scala的几乎所有的控制结构都会产生某个值,这是函数式语言所采用的方式scala的if可以向Java的三元操作符一样产生值,同样for, try, match也产生值, 程序员能够用结果值来简化代码,就如同用函数的返回值那样,如果没有这种机制,程序员就必须创建零时变量来保存控制结构中的计算结果

<!--more-->

# 1.if表达式
```
var filename = "default.txt"
if(!args.isEmpty)
  filename = args(0)

//改进代码
val filename =
  if (!args.isEmpty) args(0)
  else "default.txt"

/*
使用val是函数式的风格,并且具有与Java的final变量类型类似的效果
*/

```


# 2.while循环
```
def gcdLoop(x: Long, y: Long): Long = {
  var a = x
  var b = y
  while (a!=0){
    val tmp = a
    a = b % a
    b = tmp
  }
  b //返回b
}


//或者do ..while
var line = ""  
do{
  line = readLine()
  println("Head: " + line)
}while(line != "")
/*while 和do-while结构之所以被称为"循环",而不是表达式,是因为他们不能产生有意义的结果,结果的类型是Unit,是表名存在并且唯一存在类型为Unit的值,称为unit value, 写成() */


scala> def greet(){println("hi")}
greet: ()Unit

scala> greet() == ()                     ^
hi
res0: Boolean = true

/*
对var再赋值等式本身也是unit值,这是另一种与此类似的架构,比如:
*/

var line = ""
while ((line = readLine()) != "")
  println("read: " + line)
/*
虽然在Java里,赋值语句可以返回被赋予的那个值,本例中是标准输入读取的文本行,但同样的情况下scala的赋值语句只能得到unit的值(),因此赋值语句" line = readLine() " 的值将永远返回 ()  而不是"" ,结果是这个循环的状态将永远不会是假,循环永远无法结束
*/


/*
由于while循环不产生值,因此他经常被纯函数式语言所舍弃,这种语言只有表达式,没有循环,但是while的结构对指令式的程序员来说更容易读懂
而对于函数式风格的话,只能使用递归实现,或许对某些代码的读者来说这就不是那么显而易见的了
*/
def    gcd(x: Long, y: Long): Long = 
    if (y == 0) x else gcd()

/*
上述代码是函数式风格,采用了递归写法,相对于指令式风格,函数式不需要var,因为while的指令式编程,所以建议在代码中更为审慎的使用while循环
*/

```


# 3.for表达式
&emsp;for表达式可以让你用不同的方式把若干简单的成分组合起来以表达各种各样的枚举,例如:枚举整数序列,枚举不同类型的多个集合,使用任意条件过滤元素,制造新的集合

 枚举集合类
 
```
/*
for能做的最简单的事情就是把集合中所有元素都枚举一遍
*/
object Rational{
  def main(args: Array[String]): Unit = {
    val filesHere = (new File(".")).listFiles
    for (file <- filesHere)
      println(file)
  }
}

/*
new File(".") 创建指向当前目录的对象
listFiles 返回File对象数组,每个元素都代表目录或文件
file <- filesHere 这样的语法被称之为发生器,我们遍历了filesHere的元素
每一次遍历,名为file的新的val就被元素值初始化,编译器能够推断出file的类型是File,因为filesHere是Array[File]的,所以能够推断出file的类型是File
*/
```


 for中添加过滤条件
 
```
/*
有时你并不想要枚举集合的全部元素,而只想过滤出某个子集,这就可以通过for表达式的括号中添加过滤器(filter),即 if字句来实现
*/
val filesHere = (new File(".")).listFiles
for (file <- filesHere if file.getName.endsWith(".scala"))
  println(file)

/*也可以有多个if条件过滤*/
val filesHere = (new File(".")).listFiles
for (
  file <- filesHere
  if file.isFile;
  if file.getName.endsWith(".scala")
) println(file)
/*
如果发生器中加入超过一个过滤器,if字句必须用分号,如上代码所示
*/

```

 嵌套枚举
 
```
/*
如果加入多个<-字句,你就得到了嵌套的"循环",如下,外层的循环是循环枚举filesHere ,内层的遍历的是所有以.scala结尾的文件
*/
def fileLines(file: java.io.File) =
  scala.io.Source.fromFile(file).getLines().toList

def grep(pattern: String) = {
  val filesHere = (new File(".")).listFiles
  for {
    file <- filesHere
    if file.getName.endsWith(".scala")
    line <- fileLines(file)
    if line.trim.matches(pattern)    //找到匹配的行
  } println(file + ":" + line.trim)

//使用
grep(".*gcd.*")

/*
你可以使用花括号代替小括号包裹发生器和过滤器,使用花括号的好处是可以省略使用小括号时必须加的分号
*/

```

 流间(mid-stream)变量绑定

```
/*
前面的代码中重复出现的表达式line.trim 或许你希望只计算一遍,用等号(=)把结果绑定到新变量实现,绑定的变量被当做val引入和使用,不过不带关键字val
*/
def grep(pattern: String) = {
  val filesHere = (new File(".")).listFiles
  for {
    file <- filesHere
    if file.getName.endsWith(".scala")
    line <- fileLines(file)
    trimmed = line.trim
    if trimmed.matches(pattern)
  } println(file + ":" + trimmed)

}

/*
代码中,名为trimmed的变量被从半路引入for表达式,并被初始化为line.trim的结果值,于是之后的for表达式在两个地方使用了新的变量,一次在if中,另一次在println中
*/
```
 制造新集合
 
```
/*
上述的例子都只是对枚举值进行操作然后就释放,你还可以创建一个值去记住每一次的迭代,只要在for表达式之前加上关键字yield,比如:下面的函数鉴别出.scala文件并保存在数组里
*/
def scalaFiles(pattern: String) = {
  val filesHere = (new File(".")).listFiles
  for (
    file <- filesHere
    if file.getName.endsWith(".scala");
  ) yield file
}
/*
for表达式在每次执行的时候都会产生一个新值,本例中是file,当for表达式完成的时候,结果将是包含了所有值的集合对象,本例中结果为Array[File]
*/
//for-yield表达式的语法是这样的
for {子句} yield {循环体}

//以下写法是错误的
for (file <- fileHere if file.getName.endsWith(".scala")){
  yield file //语法错误
}



```


# 4.使用try表达式处理异常
&emsp;scala的异常和许多其他的语言一样,方法除了能以通常的方式返回值以外,还可以通过抛出异常中止执行,方法的调用者要么可捕获并处理这个异常,或者也可以只是简单的中止掉,并把异常上升到调用者处,异常以这种方式上升,逐层释放调用堆栈,直到某个方法接手处理或不再剩下其他的方法

抛出异常

```
//首先创建一个异常对象,然后用throw关键字抛出
throw new IllegalArgumentException

//下面代码的意思是:如果n是偶数,half将被初始化为n的一半,如果n不是偶数,那么异常将在half被初始化为任何值之前被抛出
val half =
  if (n%2 == 0)
    n / 2
  else
    throw new RuntimeException("n must be event!")


```


 捕获异常
 
```
import java.io.FileReader
import java.io.FileNotFoundException
import java.io.IOException

try {
  val f = new FileReader("input.txt")
  //使用了,但是并未关闭文件
} catch {
  case ex: FileNotFoundException => //处理丢失的文件
  case ex: IOException => //处理其他IO错误
}
/*
这个try-catch表达式的处理方式与其他语言中的异常处理一致,首先执行程序体,如果抛出异常,则依次尝试每个catch子句,本例中,如果异常是FileNotFoundException,那么第一个字句将被执行,如果是IOException类型,第二个字句将被执行,如果都不是,那么try-catch将终结并把异常上升出去
注意:scala与Java的区别在于scala里不需要捕获异常,或者把他们声明在throws子句中
*/

```


 finally子句

```
//某些代码无论方法如何终止都要执行的话,那么可以将表达式放在finally子句里
import java.io.FileReader

val f = new FileReader("input.txt")
try {
  //使用了,但是并未关闭文件
}finally {
  f.close() //确保关闭文件
}
/*
上面的代码演示了确保非内存资源(如:文件,套接字,或者数据库链接)被关闭的惯例方式:
首先,占有资源
然后,开始try代码块使用资源
最后,在finally代码块中关闭资源
上述方式和在Java中的一样,但是在scala中还有一种被称之为出借模式的技巧将更加的简洁来到达同样的目的,出借模式在9.4节描述
*/

```

 生成值
 
```
/*
和其他大多数scala控制结构一样,try-catch-finally也产生值,如果没有抛出异常,返回的结果是try子句中的new URL(path),如果抛出异常并被捕获,则对应于catch中的子句
*/
import java.net.URL
import java.net.MalformedURLException

def urlFor(path: String) = 
  try {
    new URL(path)
  } catch {
    case e: MalformedURLException => new URL("http://www.scala-lang.org")
  }



```



# 5.匹配(match)表达式
```
/*类似于其他语言中的switch语句,他可以提供给你在多个备选项中做选择*/

object ScalaDemo{
  def main(args: Array[String]): Unit = {
    val firstArg = if (args.length>0) args(0) else ""
    firstArg match {
      case "salt" => println("pepper")
      case "chips" => println("salsa")
      case "eggs" => println("bacon")
      case _ => println("huh?") //默认情况用下划线说明,这是常用在scala里作为占位符来表达未知值的通配符
    }
  }
}
/*
和Java不同的是scala里的每个备选项的最后并没有break,但是在scala里并不会从上一个备选项落入到下一个备选项里面去的情况发生,
*/


/*match表达式的每个备选项不但可以通过打印输出值,还可以只生成返回值而不打印*/
object ScalaDemo{
  def main(args: Array[String]): Unit = {
    val firstArg = if (args.length>0) args(0) else ""
    val friend = 
      firstArg match {
        case "salt" => "pepper"
        case "chips" => "salsa"
        case "eggs" => "bacon"
        case _ => "huh?"
      }
    println(friend)
  }
}



```



# 6.不再使用break和continue

```
//Java写法
int i = 0;
boolean foundIt = false;
while (i<args.length){
  if (args[i].startsWith("-")){
    i=i+1;
    continue;
  }
  if (args[i].endsWith(".scala")){
    foundIt = true;
    break;
  }
  i = i+1
}


//scala写法(使用递归去循环,去掉var)
def searchFrom(i: Int): Int =
  if (i>=args.length) -1
  else if (args(i).startsWith("-")) searchFrom(i+1)
  else if (args(i).endsWith(".scala")) i
  else searchFrom(i+1)

val i = searchFrom(0) //输入整数值做输入,从该值向前搜索,并返回想要的参数的索引

/*
用递归替换了循环,每个continue都被带有i+1做参数的递归调用替换掉了,并快速跳转处理下一个整数
*/




```

# 7.变量范围

```
/*
scala的变量的作用域和Java的基本相同
*/
//一旦变量被定义了,在同一个范围内就无法定义同样的名字了
val a = 1
val a = 1//编译不过

//然而,你可以在内部范围内定义与外部范围里名称相同的变量
val a = 1;
{
  val a = 2 //编译通过,仅在花括号内有效
  println(a)
}
println(a)
/*
在内部变量被认为是遮蔽了同名的外部变量,因为在内部范围内中外部变量变得不可见,
虽然可以在内部定义和外部同名的变量,但是不建议这样做,因为这样做对读者来说会很混乱,通常,选择新的,有意义的变量名比不时遮蔽外部变量的做法更为妥当
*/

```







