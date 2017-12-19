---
title: 第十五章 样本类和模式匹配
categories: scala   
tags: [scala,scala编程]
---


# 1.简单的例子
```
abstract class Expr
case class Var(name: String) extends Expr
case class Number(num: Double) extends Expr
case class UnOP(operator: String, arg: Expr) extends Expr
case class BinOp(operator: String, left: Expr, right: Expr) extends Expr

/*
上述每个class类都有一个case修饰符,带有这种修饰符的类被称为样本类(case class) ,这种修饰符可以让scala的编译器自动为你的类添加一些语句上的便捷设定
1.他会添加与类名一致的工厂方法,比如:写成Var("x")来构造Var对象以替代稍长一些的new Var("x")
  val v = Var("x")

2.样本类参数列表中的所有参数隐式获得了val前缀,因此他被当做你字段维护
v.name
op.left

3.编译器为你的类添加了方法toString,hashCode和equals的自然实现,他们能够打印,哈希和比较由类及其所有参数组成的整棵树,因为scala里的==始终直接转到equals,这也就特别意味着样本类的元素一直是在做结构化的比较
val op = BinOp("+", Number(1), v)
println(op)   //BinOp(+, Number(1.0), Var(x))
op.right == Var("x")   //true

*/

/*
所有这些转换以极低的代价带来了大量的便利,代价就是必须写case修饰符并且你的类和对象都会变得稍微大一点,变大的原因是因为产生了附加的方法及对于每个构造器参数添加了隐含的字段,不过样本类最大的好处还在于它们能够支持模式匹配
*/


```

<!--more-->


 模式匹配
&emsp;假如你想简化之前看到的数学表达式,可能有许多的简化规则,以下三条规则只是作为演示:
```
Unop("-", Unop("-", e))    => e    //双重负号
BinOp("+", e, Number(0))  => e    //加0
BinOp("*" , e, Number(1)) =>e     //乘1


//在函数方面的应用
def simplifyTop(expr: Expr): Expr = expr match {
  case UnOp("-", UnOp("-", e)) => e //双重负号
  case BinOp("+", e, Number(0)) => e  //加0
  case BinOp("*", e, Number(1)) => e   //乘1
  case _ => expr
}

/*
simplifyTop右侧的部分组成了match表达式,match对应于Java里的switch,不过他写在选择器表达式之后,也就是说:
//java
选择器 match {备选项}

//scala
switch {选择器} {备选项}
*/

/*
一个模式匹配包含了一系列备选项,每个都开始于关键字case,每个备选项都包含了一个模式及一到多个表达式,他们将在模式匹配过程中被计算,箭头符号 => 隔开了模式和表达式
*/

```

 match与switch的比较
```

/*
匹配表达式可以被看做java风格switch的泛化,当每个模式都是常量并且最后一个模式可以是通配(表示为switch的default情况)的时候,java风格的switch可以被自然的表达为match表达式,需要记住三点
1.match是scala的表达式,也就是说,他始终以值作为结果
2.scala的备选项表达式永远不会"掉到" 下一个case,这一点不同于java的switch
3.如果没有模式匹配,MatchError异常会被抛出,所以对模式匹配至少添加一个默认情况
*/
expr match {
    case BinOp(op, left, right) => println(expr + "is a binary operation")
    case _ =>
}
/*
上述代码中第二个情况是必须的,否则的话,match表达式将在每个expr参数不是BinOp的时候抛出MatchError,在这个例子里,对于第二种情况没有指定代码,因此如果跑到了这里就什么都不做,每个情况的结果都是unit值 () ,因此这也就是整个match表达式的结果
*/

```


# 2.模式的种类
 通配模式

```
//通配模式(_)匹配任意对象,他被用作默认的"全匹配"的备选项
expr match {
  case BinOp(op, left, right) => println(expr + "is a binary operatoin")
  case _ =>
}

//通配模式还可以用来忽略对象中你不关心的部分,如,前一个例子实际上并不关心二元操作符的元素是什么,只是检查是否为二元操作符,因此使用了通配符指代如下:
expr match {
  case BinOp(_, _, _) => println(expr + "is a binary operatoin")//并没有使用匹配到的元素,所以使用了通配符去
  case _ =>println("it's something else ")
}

```

 常量模式
```
/*
常量模式仅匹配自身,任何字面量都可以用作常量.例如:5, true 还有"hello" 都是常量模式,还有任何val或单例对象也可以被用作常量,例如单例对象Nil是只匹配空列表的模式
*/
def describe(x: Any) = x match {
  case 5 => "five"
  case true => "truth"
  case "hello" => "hit"
  case Nil => "the empty list"
  case _ => "something else"
}

//打印结果

scala> describe(5)
res0: String = five

scala> describe(true)
res1: String = truth

scala> describe("hello")
res2: String = hit

scala> describe(Nil)
res3: String = the empty list

scala> describe(List(2,3))
res4: String = something else

```


 变量模式
```
/*
变量模式类似于通配符,可以匹配任意对象,scala把变量绑定在匹配的对象上,因此可以使用这个变量操作对象
*/
expr match {
  case 0 => "zero"
  case somethingElse => "not zero" + somethingElse
}

//常量模式也可以有符号名,我们使用Nil表示空列表,以下是一个与之相关的例子,这里模式匹配采用了常量E(2.71828)和常量Pi(3.1415926)
import Math.{E,PI}
E match {
  case PI => "strange math ? pi = " + PI
  case  _ => "Ok"
}
/*
scala用小写的字母开始的简单名被当做是模式变量,所有其他的引用被认为是常量
*/

val pi = Math.PI
E match {
    case pi  => "strange math? Pi=" + pi
    case _ => "ok"
}


<console>:13:error: unreachable code due to variable pattern 'pi' on line 12
                  case _ => "ok"
                            ^
/*
在这里编译器甚至都不会让你添加默认情况,因为pi是变量模式,他可以匹配任意输入,因为之后的_ 将永远不能访问到
*/

/*
解决的方式有两种:
1.如果常量是某个对象的字段,可以在其之上用限定符前缀,例如:pi是变量模式,但是this.pi 或obj.pi虽然都开始于小写字母但都是常量,
2.如果这不起作用(比如说,pi是本地变量) 还可以反引号包住变量名,例如: `pi`会再次被解释为常量,而不是变量
*/

E match {
  case `pi` => "strange math ? pi = " + PI
  case  _ => "Ok"
}
```

 构造器模式
```
/*
这种模式意味着scala模式支持深度匹配,这种模式不只是检查顶层对象是否一致,还会检查对象的内容是否匹配内层的模式,由于额外的模式自身可以形成构造器模式,因此可以使用它们检查到对象内部的任意深度,例如:如下的代码,检查了对象的顶层是BinOp ,以及他的第三个构造器参数是Number ,以及他的值为数字0,这个模式仅有一行却能够检查三层深度
*/

expr match {
  case BinOp("+", e, Number(0)) => println("a deep match")
  case _ =>
}
```

 序列模式
```
//你也可以像匹配样本类那样匹配如List, 或 Array这样的序列类型,不过同样的语法现在可以指定模式内任意数量的元素,例如:下面的代码展示了检查开始于零的三元素列表的模式

expr match {
  case List(0, _, _) => println("found list")
  case _ =>
}

//如果你想匹配一个不指定长度的序列,可以指定_*作为模式的最后元素,这种古怪的模式能匹配序列中零到任意数量的元素,如下代码,展示了匹配由零开始,不计长度的任意序列的模式

expr match {
  case List(0, _*) => println("found list")
  case _ =>
}
```

 元组模式
```
//如下,你可以匹配类似(a,b,c)这样的模式可以匹配任意的3-元组

def tupleDemo(expr: Any) = expr match {
  case (a, b, c) => println("matched" + a + b + c)
  case _ =>
}
```

 类型模式
```
//你可以把类型模式当做类型测试和类型转换的简易替代

def generalSize(x: Any) = x match {
  case s: String => s.length
  case m:Map[_, _] => m.size
  case _ => 1
}

//使用
scala> generalSize("abc")
res8: Int = 3

scala> generalSize(Map(1->'a', 2 ->'b'))
res9: Int = 2
/*
在上面的例子中,尽管x和s是指代了同样的值,不过x是Any而s是String,因此可以在模式对应的备选项表达式中写成s.length但是不能写成x.length,因为Any类型没有length成员
*/

//替代模式匹配的例子,类型转换
if (x.isInstanceOf[String]){    //isInstanceOf 类型测试
  val s = x.asInstanceOf[String]    //asInstanceOf 类型转换
  s.length
}else   //...

/*
上面的代码中,scala里类型测试和转换的代码真的很冗长,所以我们并不鼓励这样去做,使用带有类型模式的模式匹配通常就能够满足你的需求,尤其在需要同时做类型测试和转换的场景时,因为这两种操作都被融入一个模式匹配之中了
*/

```

 类型擦除
```
//特定元素类型的映射能匹配吗?比如说测试给定值是否是从Int到Int的映射,让我们试一下
def isIntToIntMap(x: Any) = x match {
  case m: Map[Int, Int] => true
  case - => false
}

//在scala命令行测试结果如下:
<console>:9: warning: non-variable type argument Int in type pattern Map[Int,Int
] is unchecked since it is eliminated by erasure
         case m: Map[Int, Int] => true
                 ^

/*
scala使用了泛型的擦除模式,就如java那样,也就是说类型参数信息没有保留到运行期,因此运行期没有办法判断给定的Map对象创建时带了两个Int参数,还是带了两个其他类型的参数,系统所能做的只是判断这个值是某种任意类型参数的Map

擦除规则的唯一例外就是数组,因为在scala里和java里,他们都被特殊处理了,数组的元素类型与数组值保存在一起,因此他可以做模式匹配
*/
def isStringArray(x: Any) = x match {
  case a: Array[String] => "yes"
  case _ => "no"
}

//测试结果
scala> val as = Array("abc")
as: Array[String] = Array(abc)
scala> isStringArray(as)
res11: String = yes

scala> val al = Array(1,3,5)
al: Array[Int] = Array(1, 3, 5)
scala> isStringArray(al)
res12: String = no
```

 变量绑定
```
/*
除了独立的模式变量模式之外,你还可以对任何其他模式添加变量,只要简单的写上变量名,一个@符号,以及这个模式,这种写法创造了变量绑定模式,这种模式的意义在于他能够像通常的那样做模式匹配,并且如果匹配成功,则把变量设置成匹配的对象,就像使用简单的变量模式那样
*/
expr match {
  case UnOp("abc", e @ UnOp("abc", _)) => e
  case _ =>
}
/*
上述代码中使用e作为变量及UnOp("abc", _) 作为模式的变量绑定模式,如果整个模式匹配成功,那么符合UNOp("abc",_) 的部分就可以使用e指代
*/




```



# 3.模式守卫
```
/*
需求:如果你想要简化e+e的操作,写成 e*2 ,表示成Expr树的语言,就是表达式
*/
BinOp("+", Var("x"), Var("x"))
//简化为
BinOp("*", Var("x"), Number(2))

//获取你尝试如下的方式定义规则:
def simplifyAdd(e: Expr) = e match {
  case BinOp("+", x, x) => BinOp("*", x, Number(2))
}
//编译器会报如下的错误:
<console>: 10: error: x is already defined as value x 
        case BinOp("+", x, x) => BinOp("*", x, Number(2))
                     ^
/*
模式变量仅允许在模式中出现一次,也就是说 case BinOp("+", x, x) 中第二个x是重复出现的,不过你可以使用模式守卫重新定制这个匹配规则
*/
def simplifyAdd(e: Expr) = e match {
  case BinOp("+", x, y) if x==y => BinOp("*", x, Number(2))
}
/*
模式守卫接在模式之后,开始于if,守卫可以是任意的引用模式中变量的布尔表达式,如果存在模式守卫,那么只有在守卫返回true的时候匹配才成功,
*/

//下面是模式匹配的其他例子
case n: Int if 0<n => ....    //仅仅匹配正整数
case s: String if s(0) == 'a' => ...    //仅仅匹配以字母'a'开始的字符串
```


# 4.模式重叠
```
def simplifyAll(expr: Expr): Expr = expr match {
  case UnOp("-", UnOp("-", e)) => simplifyAll(e)
  case BinOp("+", e, Number(0)) => simplifyAll(e)
  case BinOp("*", e, Number(1)) => simplifyAll(e)
  case UnOp(op,e) => UnOp(op,simplifyAll(e))
  case BinOp(op, left, right) => BinOp(op, simplifyAll(left), simplifyAll(right))
  case _ => expr
}

//全匹配的样本要跟在更具体的简化方法之后,例如下面的match表达式不会编译成功,因为一地个样本匹配任何能匹配第二个样本的东西
def simplifyBad(expr: Expr): Expr = expr match {
  case UnOp(op, e) => UnOp(op,simplifyBad(e))
  case UnOp("-", UnOp("-", e)) => e
}
```


# 5.封闭类
```
/*
一旦你写好了模式匹配,你就需要确信已经考虑了所有可能的情况,有些时候你可以通过在匹配的最后添加默认处理做到这点,不过这仅仅在的确有一个合理的默认行为的情况下有效,如果没有默认的情况该怎么办?你怎样才能保证包括了所有的情况呢?

可选的方案就是让样本类的超类被封闭,封闭类除了类定义所在的文件之外不能再添加任何新的子类,这意味着你仅需要关心你已经知道的子类即可
*/

/*
如果你打算做模式匹配的类层级,你应当考虑封闭他们,只要把关键字sealed放在最顶层类的前边即可,如下
*/
sealed abstract class Expr
case class Var(name: String) extends Expr
case class Number(num: Double) extends Expr
case class UnOp(operator: String, arg: Expr) extends Expr
case class BinOp(operator: String, left: Expr, right: Expr) extends Expr

//使用实例
def describe(e: Expr): String = e match {
  case Number(_) => "is a number"
  case Var(_) => "is a variable"
}

//执行结果
warning: match may not be exhaustive.
missing combination     UnOp
missing combination     BinOp

/*
这样的警告向你表明你的代码会有产生MatchError异常的风险,因为某些可能的模式(UnOp, BinOp)没有被处理,警告指出了潜在的运行期故障的源头,因此他通常能够帮助你正确的编程
*/
                                       ^
/*
然而,有些时候你或许会碰到这样的情况,编译器弹出太过挑剔的警告,例如:或许可以通过上下文知道你只会把上面的describe方法应用在仅仅是Number或Var的表达式上,因此你知道实际上不会产生MatchError,要让这些警告不再发生,你可以为方法添加用作全匹配的第三个样本,如下:
*/
def describe(e: Expr): String = e match {
  case Number(_) => "is a number"
  case Var(_) => "is a variable"
  case _ => throw new RuntimeException  //不会发生
}

/*
上面的做法的确有效,但是不理想,你或许对被迫添加不会被执行(至少你是这么认为的),而只是让编译器闭嘴的代码感到不爽,更轻量级的做法是给匹配的选择器表达式添加 @unchecked 注解,如下
*/
def describe(e: Expr): String = (e: @unchecked) match {
  case Number(_) => "is a number"
  case Var(_) => "is a variable"
}
//如果match的选择器表达式带有这个注解,那么对于随后的模式的穷举性检查将被抑制掉
```

# 6.option类型
```
/*
scala为可选值定义了一个名为Option的标准类型,这种值可以有两种形式,可以是Some(x) 的形式,其中x是实际值,或者也可以是None对象,代表缺失的值
*/

scala> val capitals = Map("France"->"Pairs", "Japan"->"Tokyo")
capitals: scala.collection.immutable.Map[String,String] = Map(France -> Pairs, Japan -> Tokyo)

scala> capitals get "France"
res13: Option[String] = Some(Pairs)

scala> capitals get "North Pole"
res14: Option[String] = None


//分离可选值,可以通过模式匹配进行
def show(x: Option[String]) = x match {
  case Some(s) => s
  case None => "?"
}

//在scala命令行的执行结果如下
scala> show(capitals get "Japan")
res15: String = Tokyo

scala> show(capitals get "North Pole")
res16: String = ?

/*
java里最常用的是代表没有值的null,例如,java.util.HashMap的get方法要么返回存储在HashMap里的值,要么每找到返回null,这种方式对java起效,不过可能会隐藏错误,因为很难实际记得程序中哪个变量可以允许是null,如果变量允许为null,那么你就必须记住每次使用它的时候检查是否为null,一旦你忘记了检查,就难以避免运行时发生NullPointerException异常,又因为这种异常可能不是经常发生,所以想要通过测试发现故障是非常困难的,
对于scala来说,这种方式根本不起作用,因为可以在哈希映射中存储值类型,而null不是值类型的合法元素,举例来说,HashMap[Int,Int] 不能返回null以表明"没有元素"
*/

```

# 7.模式无处不在
&emsp;在scala中模式可以出现在很多的地方,而不单单在match表达式里
 模式在变量定义中
```
/*
你在定义val或var的任何时候,都可以使用模式替代简单的标识符,例如,你可以使用模式拆分元组并把其中的每个值分配给变量,如下:
*/

scala> val myTuple = (123, "abc")
myTuple: (Int, String) = (123,abc)

scala> val (number, string) = myTuple
number: Int = 123
string: String = abc

scala> number
res17: Int = 123

scala> string
res18: String = abc


//使用样本类时这种构造非常有用,如果你知道正在用的样本类的精确结构,那就可以使用模式解构他

scala> val exp = new BinOp("*", Number(5), Number(1))
exp: BinOp = BinOp(*,Number(5.0),Number(1.0))

scala> val BinOp(op, left, right) = exp
op: String = *
left: Expr = Number(5.0)
right: Expr = Number(1.0)


```

 用作偏函数的样本序列
```
/*
花括号的样本序列(就是备选项) 可以用在能够出现函数字面量的任何地方,实质上,样本序列就是函数字面两,而且只有更普遍,函数字面量只有一个入口点和参数列表,但样本序列可以有多个入口点,每个都有自己的参数列表,每个样本都是函数的一个入口点,参数也被模式化特化,每个入口点的函数体都在样本的右侧
*/
val withDefault: Option[Int] => Int = {
    case Some(x) => x
    case None => 0
}

//使用
withDefault(Some(10))
res25: Int = 10
withDefault(None)
res26: Int = 0



val second: List[Int] => Int = {
    case x::y::_ => y
}


/*
如果你编译上述代码,编译器会正确的提示说匹配并不全面,
如果你传给一个三元素的列表,他的执行没有问题,但是如果传给他一个空列表就不行了
*/
second(List(5, 6, 7))    //6
second(List())      //MatchError: List() 


/*
你必须首先告诉编译器你知道正在使用的是偏函数,类型: List[Int] => Int 包含了不管是否为偏函数的,从整数列表到整数的所有函数
仅包含从整数列表到整数的偏函数,那么应该写成PartialFunction[List[Int], Int] ,下面还是second函数,这次写成了使用偏函数类型
*/
val second: PartialFunction[List[Int], Int] = {
    case x::y::_ => y
}


/*
偏函数有一个isDefinedAt 方法,可以用来测试是否函数对某个特定值有定义,本例中,函数对任何至少两个元素的列表有定义
*/
seconde.isDefinedAt(List(1,3,4))    //true
seconde.isDefinedAt(List())    //false


/*
上面的偏函数会对模式执行两次翻译,其中一次时真实函数的实现,另一次时测试函数是否有定义的实现,例如:上面的函数字面量(case x::y::_ => x) 会被翻译成下列的偏函数值
*/
new PartialFunction[List[Int], Int] {
    def apply(xs: List[Int]) = xs match {
        case x::y::_ => y
    }
    def isDefinedAt(xs: List[Int]) = xs match {
        case x::y::_ => true
        case _ => false
    }
}
//上面的翻译只有在函数字面量的声明类型为PartialFunction的时候才有效,如果声明的类型只是Function,或者根本不存在,那么函数字面量就会带而转义为完整的函数

```

 for 表达式的模式
```
/*
for表达式里也可以使用模式,这个for表达式从capitals映射中获得所有的键/值对,每一对都匹配模式(country,city) 并定义了两个变量country,和city
*/
for((country, city) <- capitals)
    println("The capitals of " + country + " is " + city)


//不过模式同样也可能无法匹配产生的值
val results = List(Some("apple"), None, Some("orange"))
for(Some( fruit) <- results )
    println(fruit)

apple
orange

/*
如你在这个例子中所见,产生出来的不能匹配于模式的值被丢弃,例如:results列表里第二个元素None不匹配模式Some(Fruits) ,因此就没有出现在输出中
*/

```

# 8.一个更大的例子
```
/*
需求:写一个计算数学表达式的例子,如" x/x + 1 "这样的除法,应该能够通过把被除数放在除数的顶上这种方式,被垂直打印如下
*/
```

 
```
//另一个例子是:表达式  ( a/(b*c) + (1/n) ) /3的二维布局:
```


 第一步:集中注意力在水平布局上,结构化的表达式如:
```
BinOp("+",
  BinOp("*",
    BinOp("+", Var("x"), Var("y")),
    Var("z")
  ),
  Number(1)
)
//(x+y)*z + 1
 
/*
要想知道哪里应该加括号,代码必须知道每个操作符相对的优先级,所以首先处理他是个好主意,你可以用如下形式的映射字面量直接表达相对优先级
*/

Map(
    "|" -> 0, "||" -> 0,
    "&" -> 1, "&&" ->1, ...
)




```

 完整代码
```
package org.stairwaybook.expr
import org.stairwaybook.expr

sealed abstract class Expr
case class Var(name: String) extends Expr
case class Number(num: Double) extends Expr
case class UnOp(operator: String, arg: Expr) extends Expr
case class BinOp(operator: String, left: Expr, right: Expr) extends Expr

class ExprFormatter {
  //包含了递增优先级的组中的操作符
  private val opGroups =
    Array(
      Set("|","||"),
      Set("&", "&&"),
      Set("^"),
      Set("==", "!="),
      Set("<", "<=", ">", ">="),
      Set("+", "-"),
      Set("*", "%")
    )
  //操作符到优先级的映射
  private val precedence = {
    val assocs =
      for (
        i <- 0 until opGroups.length;
        op <- opGroups(i)
      ) yield op -> i
  }

  private val unaryPrecedence = opGroups.length
  private val fractionPrecedence = -1 // 表示 / 的优先级

  private def format(e: Expr, enclPred: Int): Element = e match {
    case Var(name) => elem(name)
    case Number(num) =>
      def stripDot(s: String) =
        if (s endsWith ".0") s.substring(0, s.length-2)
    case UnOp(op, arg) =>
      elem(op) beside format(arg, unaryPrecedence)
    case BinOp("/", left, right) =>
      val top = format(left, fractionPrecedence)
      val bot = format(right, fractionPrecedence)
      val line = elem("-", top.width max bot.wiedht, 1)
      val frac = top above line above bot
      if (enclPred != fractionPrecedence) frac
      else elem(" ") beside frac beside elem(" ")
    case BinOp(op, left, right) =>
      val opPrec = precedence(op)
      val l = format(left, opPrec)
      val r = format(right, opPrec+1)
      val oper = 1 beside elem(" "+op+" ") beside r
      if (enclPred <= opPrec) oper
      else elem("(") beside oper beside elem(")")
  }
  def format(e: Expr): Element = format(e,0)
}

```

 测试
```
import org.stairwaybook.expr._
object Express extends Application {
  val f = new ExprFormatter
  val e1 =
    BinOp("*",
      BinOp("/", Number(1), Number(2)),
      BinOp("+", Var("x"),Number(1))
    )

  val e2 =
    BinOp("+",
      BinOp("/",Var("x"),Number(2)),
      BinOp("/", Number(1.5), Var("x"))
    )

  val e3 = BinOp("/", e1, e2)
  def show(e: Expr) = println(f.format(e)+"\n\n")
  for (e <- Array(e1,e2,e3)) show(e)
}

```

 输出结果

 

![](http://ols7leonh.bkt.clouddn.com//assert/img/scala_programming/15/1.png)


