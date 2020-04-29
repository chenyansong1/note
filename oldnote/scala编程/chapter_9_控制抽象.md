---
title: 第九章 控制抽象
categories: scala   
tags: [scala,scala编程]
---



# 1.减少代码重复
```
//需求:查询当前路径下的所有的文件,找到指定字符结束的文件或目录
object FileMatcher{
  private def fileHere = (new java.io.File(".")).listFiles

  def filesEnding(query: String) =
    for (file <-fileHere if file.getName.endsWith(query))
      yield file
}

/*
由于fileHere是私有的,filesEnding方法是定义在提供用户的API,FileMatcher中唯一可以访问的方法
*/

//需求:查询当前路径下的所有的文件,找到指定字符的文件或目录(并不一定是文件结尾)
def filesContaining(query: String) =
  for (file <-fileHere if file.getName.contains(query))
    yield file


//后来客户又有了一个需求:他们要求基本正则表达式匹配文件,为了支持他们,于是你,写了下面的代码:
def filesRegex(query: String) =
  for (file <-fileHere if file.getName.matches(query))
    yield file

/*
综合上面的三个需求,他们之间唯一的不同就是最后匹配文件的方法不同,第一个是endsWith,第二个是contains,第三个是matches,所以你或许希望有这样的代码:
*/
def filesMatching(query: String, method) =
  for (file <-fileHere if file.getName.method(query))
    yield file

//具体实现如下:
def filesMatching(query: String, matcher: (String, String)=>Boolean) =
  for (file <-fileHere if matcher(file.getName, query))
    yield file

//有了新的filesMatching帮助方法,上面的三个搜索方法可以简化成如下:
def filesEnding(query: String) =
  filesMatching(query,_.endsWith(_))

def filesContaining(query: String) =
  filesMatching(query, _.contains(_))

def filesRegex(query: String) =
  filesMatching(query, _.matches(_))

/*
其实像_.endsWith(_)这样的函数是使用了占位符语法,原函数可以写成如下的形式:
*/
(fileName: String, query: String) => fileName.endsWith(query)
//filesMatching函数需要一个参数,这个参数是函数,类型为(String, String)=>Boolean,所以我们在传参的时候可以不用指定参数类型,因此写成下面的样子:
(fileName, query) => fileName.endsWith(query)

//在传参的过程中,第一个参数fileName在方法体重被第一个使用,第二个参数query被第二个使用,因此你可以使用占位符语法:
_.endsWith(_)


//更加简化的形式
object FileMatcher{
  private def fileHere = (new java.io.File(".")).listFiles

  def filesMatching( matcher: (String)=>Boolean) =
    for (file <-fileHere if matcher(file.getName))
      yield file
  def filesEnding(query: String) =
    filesMatching(_.endsWith(query))

  def filesContaining(query: String) =
    filesMatching(_.contains(query))

  def filesRegex(query: String) =
    filesMatching(_.matches(query))
}
/*
以上代码就使用了闭包的特性,其中的query就是一个自由变量
在Java中的做法就是将公共的部分抽取出来形成接口,然后对接口进行实现
*/

```


# 2.简化客户代码
```
//一个判断传入的值是否包含在集合中的方法:
#指令式编程的做法
def containsNeg(nums: List[Int]): Boolean={
  var exists = false
  for (num <- nums){
    if (num<0)
      exists = true
  }
  exists
}
//调用
containsNeg(List(1, 2, 3, 4))

//函数式编程
def containsNeg2(nums: List[Int]) = nums.exists(_ < 0)
containsNeg2(List(1, 2, 3, 4))

/*
传过去的是一个函数: _<0, 该函数值需要一个参数,
*/

```

# 3.柯里化
```
/*
柯里化的函数被应用于多个参数列表,而不仅仅一个
*/
//未被柯里化的函数
def plainOldSum(x: Int, y:Int) = x + y
//调用
plainOldSum(1, 2)


//被柯里化的函数,把这个函数应用于连个列表的各一个参数
def curriedSum(x: Int)(y: Int) = x + y
//调用
curriedSum(1)(2)

/*
这里发生的事情是当你调用curriedSum时,实际上连接调用了两个传统函数,第一个函数调用带单个的名为x的Int参数,并返回第二个函数的函数值,第二个函数带Int参数y,下面的名为first的函数实质上执行了curriedSum的第一个传统函数调用会做的事情:
*/
def first(x: Int) = (y: Int)=>x+y
//在第一个函数上应用1,会产生第二个函数
val second = first(1)
//执行第二个函数
second(2)

/*
first和second函数只是柯里化过程的一个演示,他们并不直接连接在curriedSum函数上,可以使用下面的函数来获取第二个参数的参考
*/
val onePlus = curriedSum(1) _
//调用
onePlus(2) //curriedSum(1)_里的下划线是第二个参数列表的占位符,结果及时指向一个函数的参考

```

# 4.编写新的控制结构
```
/*
在拥有头等函数的语言中,即使语言的语法是固定的,你也可以有效的制作新的控制结构,所有你需要做的就是创建带函数做参数的方法
*/
//下面是"双倍"控制结构,能够重复一个操作两次并返回结果
def twice(op: Double=>Double, x:Double) = op(op(x))
twice(_ + 1, 5) //7
//在这个例子里op的类型是Double=>Double,就是说他是带一个Double做参数并返回另一个Double的函数,而 _+1就是这个函数的实现,而下划线就是参数(用占位符表示)


//需求:打开一个资源,对他进行操作,然后关闭资源,如下的代码:
def withPrintWriter(file: java.io.File, op: PrintWriter =>Unit): Unit ={
  val writer = new PrintWriter(file)
  try {
    op(writer)
  } finally {
    writer.close()
  }
}

//在客户端调用方法
withPrintWriter(
  new java.io.File("data.txt"),
  writer => writer.println(new java.util.Date)   //客户端只需要去提供方法,并不需要去关心文件流的关闭与否
)

/*
这个方法的好处就是:由withPrintWriter而并非客户端代码,去确认文件在结尾被关闭,因此忘记关闭文件是不可能的,这个技巧被称为借贷模式,因为控制抽象函数,如:withPrintWriter,打开了资源并"借贷"出函数,例如,前面例子里的withPrintWriter把PrintWriter借给函数op,当函数完成的时候,他发送信号说明他不在需要"借"的资源,于是资源被关闭在finally块中,以确认其确实被关闭,而忽略函数是正常结束还是抛出了异常
*/


/*
让客户端看上去更像内建控制结构的另一种方式是:使用花括号代替小括号包围参数列表,scala的任何方法调用,如果你确实之传入一个参数,就能可选的使用花括号替代小括号包围参数
*/

println("hello, world!")
//替换为
println{"hello, world!"}    //仅在一个参数的时候有效


//在多个参数时,可以使用柯里化的方式来使用花括号构建控制抽象
val file = new File("data.txt")
withPrintWriter(file){
  writer => writer.println(new java.util.Date)
}
/*
在上述代码中,第一个参数列表包含了一个File参数,被写成包围在小括号中,第二个参数列表包含了一个函数列表,被包围在花括号中
*/

```


# 5.传名参数(by-name parameter)
```
/*
上面的描述中可以自花括号中使用参数,但是如果传入的函数的没有参数的情况下,该如何呢?
*/
var assertionsEnabled = true
def myAssert(predicate: ()=>Boolean) =
  if (assertionsEnabled && !predicate())
    throw new AssertionError

//使用
myAssert(()=> 5>3)  //看上去有点难看,或许你想写成下面的样子
myAssert(5>3) //不会有效,因为缺少()=>


/*
传名函数恰好就是为了实现上述愿望而出现的,要实现一个传名函数,要定义参数的类型开始于 =>,而不是()=> ,例如:上述代码" ()=>Boolean " 变为 "=>Boolean" 
*/
var assertionsEnabled = true
def myAssert(predicate: =>Boolean) =    //因为没有参数,所以就不写()
  if (assertionsEnabled && !predicate)
    throw new AssertionError

//使用
myAssert(5>3) 

//或许你想对上述的函数还进一步的简化,如下:
def boolAssert(predicate: Boolean) =
  if (assertionsEnabled && !predicate)
    throw new AssertionError

//调用
boolAssert(5>3) 

/*
虽然可以使用上述的方式,但是: " predicate:=>Boolean " 和 " predicate:Boolean "是两种不同的方式
因为boolAssert的参数类型是Boolean,在boolAssert(5>3)里括号中的表达式先于boolAssert的调用被评估,表达式5>3产生true,被传给boolAssert
而在myAssert的predicate参数的类型是  =>Boolean,  myAssert(5>3) 里括号中的表达式不是先于myAssert的调用被评估的,而是代之以先创建一个函数值,其apply方法将被评估 5>3 ,而这个函数值将被传递给myAssert
*/
//如果断言被禁用
var assertionsEnabled = false

myAssert(x/0 == 0)
boolAssert(x/0 == 0)//抛出异常(被0除),因为其中的表达式是先于函数被评估的,所以会先抛出异常

```













