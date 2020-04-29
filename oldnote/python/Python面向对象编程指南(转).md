---
title:  Python面向对象编程指南(转)
categories: python   
toc: true  
tags: [python]
---



转自:
[Python面向对象编程指南](http://blog.csdn.net/zhoudaxia/article/details/23341261)

　抽象是隐藏多余细节的艺术。在面向对象的概念中，抽象的直接表现形式通常为类。虽然Python是解释性语言，但是它是面向对象的，从设计之初就已经是一门面向对象的语言。Python基本上提供了面向对象编程语言的所有元素，如果你已经至少掌握了一门面向对象语言，那么利用Python进行面向对象程序设计将会相当容易。下面就来了解一下如何在Python中进行对象编程。


# 1.如何定义一个类

在进行python面向对象编程之前，先来了解几个术语：类，类对象，实例对象，属性，函数和方法。
类是对现实世界中一些事物的封装，定义一个类可以采用下面的方式来定义：
```
class className: 
    block  

```

　　注意类名后面有个冒号，在block块里面就可以定义属性和方法了。当一个类定义完之后，就产生了一个类对象。类对象支持两种操作：引用和实例化。引用操作是通过类对象去调用类中的属性或者方法，而实例化是产生出一个类对象的实例，称作实例对象。比如定义了一个people类：

```
class people: 
    name = 'jack'       #定义了一个属性 
    #定义了一个方法 
    def printName(self): 
        print self.name 
 
```
 people类定义完成之后就产生了一个全局的类对象，可以通过类对象来访问类中的属性和方法了。当通过people.name（至于为什么可以直接这样访问属性后面再解释，这里只要理解类对象这个概念就行了）来访问时，people.name中的people称为类对象，这点和C++中的有所不同。当然还可以进行实例化操作，p=people( )，这样就产生了一个people的实例对象，此时也可以通过实例对象p来访问属性或者方法了(p.name).
　　理解了类、类对象和实例对象的区别之后，我们来了解一下Python中属性、方法和函数的区别。
　　在上面代码中注释的很清楚了，name是一个属性，printName( )是一个方法，与某个对象进行绑定的函数称作为方法。一般在类里面定义的函数与类对象或者实例对象绑定了，所以称作为方法；而在类外定义的函数一般没有同对象进行绑定，就称为函数。


# 2.属性

在类中我们可以定义一些属性，比如：
```
class people: 
    name = 'jack' 
    age = 12 
 
p = people() 
print p.name,p.age 
 
```

　　定义了一个people类，里面定义了name和age属性，默认值分别为'jack'和12。在定义了类之后，就可以用来产生实例化对象了，这句p = people( )实例化了一个对象p，然后就可以通过p来读取属性了。这里的name和age都是公有的，可以直接在类外通过对象名访问，如果想定义成私有的，则需在前面加2个下划线 ' __'。
```
class people: 
    __name = 'jack' 
    __age = 12 
 
p = people() 
print p.__name,p.__age 
 
```
这段程序运行会报错：
```
Traceback (most recent call last): 
  File "C:/PycharmProjects/FirstProject/oop.py", line 6, in <module> 
    print p.__name,p.__age 
AttributeError: people instance has no attribute '__name  

```
　　提示找不到该属性，因为私有属性是不能够在类外通过对象名来进行访问的。在Python中没有像C++中public和private这些关键字来区别公有属性和私有属性，它是以属性命名方式来区分，如果在属性名前面加了2个下划线'__'，则表明该属性是私有属性，否则为公有属性（方法也是一样，方法名前面加了2个下划线的话表示该方法是私有的，否则为公有的）。


# 3.方法

　　在类中可以根据需要定义一些方法，定义方法采用def关键字，在类中定义的方法至少会有一个参数，，一般以名为'self'的变量作为该参数（用其他名称也可以），而且需要作为第一个参数。下面看个例子：
```
class people: 
    __name = 'jack' 
    __age = 12 
 
    def getName(self): 
        return self.__name 
    def getAge(self): 
        return self.__age 
 
p = people() 
print p.getName(),p.getAge()  

```

　　如果对self不好理解的话，可以把它当做C++中类里面的this指针一样理解，就是对象自身的意思，在用某个对象调用该方法时，就将该对象作为第一个参数传递给self。


# 4.类中内置的方法

　　在Python中有一些内置的方法，这些方法命名都有比较特殊的地方（其方法名以2个下划线开始然后以2个下划线结束）。类中最常用的就是构造方法和析构方法。
　　构造方法__init__(self,....)：在生成对象时调用，可以用来进行一些初始化操作，不需要显示去调用，系统会默认去执行。构造方法支持重载，如果用户自己没有重新定义构造方法，系统就自动执行默认的构造方法。
　　析构方法__del__(self)：在释放对象时调用，支持重载，可以在里面进行一些释放资源的操作，不需要显示调用。
　　还有其他的一些内置方法，比如 __cmp__( ), __len( )__等。下面是常用的内置方法：

|内置方法|说明|
|-|-|
| __init__(self,...)	| 初始化对象，在创建新对象时调用             |
| __del__(self)	 |释放对象，在对象被删除之前调用                     |
| __new__(cls,*args,**kwd)	 |实例的生成操作                         |
| __str__(self)	 |在使用print语句时被调用                            |
| __getitem__(self,key)	 |获取序列的索引key对应的值，等价于seq[key]  |
| __len__(self)	 |在调用内联函数len()时被调用                        |
| __cmp__(stc,dst)	 |比较两个对象src和dst                           |
| __getattr__(s,name)	| 获取属性的值                               |
| __setattr__(s,name,value)	 |设置属性的值                           |
| __delattr__(s,name)	| 删除name属性                               |
| __getattribute__()	| __getattribute__()功能与__getattr__()类似  |
| __gt__(self,other)	| 判断self对象是否大于other对象              |
| __lt__(slef,other)	| 判断self对象是否小于other对象              |
| __ge__(slef,other)	 |判断self对象是否大于或者等于other对象      |
| __le__(slef,other)	 |判断self对象是否小于或者等于other对象      |
| __eq__(slef,other)	 |判断self对象是否等于other对象              |
| __call__(self,*args)	 |把实例对象作为函数调用                     |


　　__init__():__init__方法在类的一个对象被建立时，马上运行。这个方法可以用来对你的对象做一些你希望的初始化。注意，这个名称的开始和结尾都是双下划线。代码例子:

```
# Filename: class_init.py 
class Person: 
    def __init__(self, name): 
        self.name = name 
    def sayHi(self): 
        print 'Hello, my name is', self.name 
 
p = Person('Swaroop') 
p.sayHi() 
 
输出： 
Hello, my name is Swaroop 
 
```

　　__new__():__new__()在__init__()之前被调用，用于生成实例对象。利用这个方法和类属性的特性可以实现设计模式中的单例模式。单例模式是指创建唯一对象吗，单例模式设计的类只能实例化一个对象。
```
class Singleton(object): 
    __instance = None                       # 定义实例 
 
    def __init__(self): 
        pass 
 
    def __new__(cls, *args, **kwd):         # 在__init__之前调用 
        if Singleton.__instance is None:    # 生成唯一实例 
            Singleton.__instance = object.__new__(cls, *args, **kwd) 
        return Singleton.__instance  
```
　　__getattr__()、__setattr__()和__getattribute__():当读取对象的某个属性时，python会自动调用__getattr__()方法。例如，fruit.color将转换为fruit.__getattr__(color)。当使用赋值语句对属性进行设置时，python会自动调用__setattr__()方法。__getattribute__()的功能与__getattr__()类似，用于获取属性的值。但是__getattribute__()能提供更好的控制，代码更健壮。注意，python中并不存在__setattribute__()方法。代码例子：
```
# -*- coding: UTF-8 -*- 
 
class Fruit(object): 
    def __init__(self, color="red", price=0): 
        self.__color = color 
        self.__price = price 
 
    def __getattribute__(self, item):              # <span style="font-family:宋体;font-size:12px;">获取属性的方法</span> 
        return object.__getattribute__(self, item) 
 
    def __setattr__(self, key, value): 
        self.__dict__[key] = value 
 
if __name__ == "__main__": 
    fruit = Fruit("blue", 10) 
    print fruit.__dict__.get("_Fruit__color")    # <span style="font-family:宋体;font-size:12px;">获取color属性</span> 
    fruit.__dict__["_Fruit__price"] = 5 
    print fruit.__dict__.get("_Fruit__price")    # <span style="font-family:宋体;font-size:12px;">获取price属性</span>  
```

Python不允许实例化的类访问私有数据，但你可以使用object._className__attrName访问这些私有属性。
　　__getitem__():如果类把某个属性定义为序列，可以使用__getitem__()输出序列属性中的某个元素.假设水果店中销售多钟水果，可以通过__getitem__()方法获取水果店中的没种水果。代码例子：
```
# -*- coding: UTF-8 -*- 
 
class FruitShop: 
     def __getitem__(self, i):      # 获取水果店的水果 
         return self.fruits[i]       
 
if __name__ == "__main__": 
    shop = FruitShop() 
    shop.fruits = ["apple", "banana"] 
    print shop[1] 
    for item in shop:               # 输出水果店的水果 
        print item,  
```

输出
```
banana 
apple banana  
```

　　__str__():__str__()用于表示对象代表的含义，返回一个字符串.实现了__str__()方法后，可以直接使用print语句输出对象，也可以通过函数str()触发__str__()的执行。这样就把对象和字符串关联起来，便于某些程序的实现，可以用这个字符串来表示某个类。代码例子：
```
# -*- coding: UTF-8 -*- 
 
class Fruit:      
    '''''Fruit类'''               #为Fruit类定义了文档字符串 
    def __str__(self):          # 定义对象的字符串表示 
        return self.__doc__ 
 
if __name__ == "__main__": 
    fruit = Fruit() 
    print str(fruit)            # 调用内置函数str()触发__str__()方法，输出结果为:Fruit类 
    print fruit                 #直接输出对象fruit,返回__str__()方法的值，输出结果为:Fruit类  
```

　__call__():在类中实现__call__()方法，可以在对象创建时直接返回__call__()的内容。使用该方法可以模拟静态方法。代码例子:
```
# -*- coding: UTF-8 -*- 
 
class Fruit: 
    class Growth:        # 内部类 
        def __call__(self): 
            print "grow ..." 
 
    grow = Growth()      # 调用Growth()，此时将类Growth作为函数返回,即为外部类Fruit定义方法grow(),grow()将执行__call__()内的代码 
if __name__ == '__main__': 
    fruit = Fruit() 
    fruit.grow()         # 输出结果：grow ... 
    Fruit.grow()         # 输出结果：grow ...  
```

# 5.类属性、实例属性、类方法、实例方法以及静态方法

　　在了解了类基本的东西之后，下面看一下python中这几个概念的区别。
　　先来谈一下类属性和实例属性
　　在前面的例子中我们接触到的就是类属性，顾名思义，类属性就是类对象所拥有的属性，它被所有类对象的实例对象所共有，在内存中只存在一个副本，这个和C++中类的静态成员变量有点类似。对于公有的类属性，在类外可以通过类对象和实例对象访问。
```
class people: 
    name = 'jack'  #公有的类属性 
    __age = 12     #私有的类属性 
 
p = people() 
 
print p.name             #正确 
print people.name        #正确 
print p.__age            #错误，不能在类外通过实例对象访问私有的类属性 
print people.__age       #错误，不能在类外通过类对象访问私有的类属性
 
```

　实例属性是不需要在类中显示定义的，比如：
```
class people: 
    name = 'jack' 
 
p = people() 
p.age =12 
print p.name    #正确 
print p.age     #正确 
 
print people.name    #正确 
print people.age     #错误  

```

　　在类外对类对象people进行实例化之后，产生了一个实例对象p，然后p.age = 12这句给p添加了一个实例属性age，赋值为12。这个实例属性是实例对象p所特有的，注意，类对象people并不拥有它（所以不能通过类对象来访问这个age属性）。当然还可以在实例化对象的时候给age赋值。
```
class people: 
    name = 'jack' 
 
    #__init__()是内置的构造方法，在实例化对象时自动调用 
    def __init__(self,age): 
        self.age = age 
 
p = people(12) 
print p.name    #正确 
print p.age     #正确 
 
print people.name    #正确 
print people.age     #错误  

```


　　如果需要在类外修改类属性，必须通过类对象去引用然后进行修改。如果通过实例对象去引用，会产生一个同名的实例属性，这种方式修改的是实例属性，不会影响到类属性，并且之后如果通过实例对象去引用该名称的属性，实例属性会强制屏蔽掉类属性，即引用的是实例属性，除非删除了该实例属性。

```
class people: 
    country = 'china' 
 
 
print people.country 
p = people() 
print p.country 
p.country = 'japan'  
print p.country      #实例属性会屏蔽掉同名的类属性 
print people.country 
del p.country    #删除实例属性 
print p.country
 
```

　　下面来看一下类方法、实例方法和静态方法的区别。
　　类方法：是类对象所拥有的方法，需要用修饰器"@classmethod"来标识其为类方法，对于类方法，第一个参数必须是类对象，一般以"cls"作为第一个参数（当然可以用其他名称的变量作为其第一个参数，但是大部分人都习惯以'cls'作为第一个参数的名字，就最好用'cls'了），能够通过实例对象和类对象去访问。
```
class people: 
    country = 'china' 
 
    #类方法，用classmethod来进行修饰 
    @classmethod 
    def getCountry(cls): 
        return cls.country 
 
p = people() 
print p.getCountry()    #可以用过实例对象引用 
print people.getCountry()    #可以通过类对象引用 

```

类方法还有一个用途就是可以对类属性进行修改：
```
class people: 
    country = 'china' 
 
    #类方法，用classmethod来进行修饰 
    @classmethod 
    def getCountry(cls): 
        return cls.country 
 
    @classmethod 
    def setCountry(cls,country): 
        cls.country = country 
 
 
p = people() 
print p.getCountry()    #可以用过实例对象引用 
print people.getCountry()    #可以通过类对象引用 
 
p.setCountry('japan')    
 
print p.getCountry()    
print people.getCountry() 

#运行结果：
china 
china 
japan 
japan  
 
```


　结果显示在用类方法对类属性修改之后，通过类对象和实例对象访问都发生了改变。
　实例方法：在类中最常定义的成员方法，它至少有一个参数并且必须以实例对象作为其第一个参数，一般以名为'self'的变量作为第一个参数（当然可以以其他名称的变量作为第一个参数）。在类外实例方法只能通过实例对象去调用，不能通过其他方式去调用。
```
class people: 
    country = 'china' 
 
    #实例方法 
    def getCountry(self): 
        return self.country 
 
 
p = people() 
print p.getCountry()         #正确，可以用过实例对象引用 
print people.getCountry()    #错误，不能通过类对象引用实例方法 
 
```

　静态方法：需要通过修饰器"@staticmethod"来进行修饰，静态方法不需要多定义参数。
```
class people: 
    country = 'china' 
 
    @staticmethod 
    #静态方法 
    def getCountry(): 
        return people.country 
 
 
print people.getCountry()  
```


　对于类属性和实例属性，如果在类方法中引用某个属性，该属性必定是类属性，而如果在实例方法中引用某个属性（不作更改），并且存在同名的类属性，此时若实例对象有该名称的实例属性，则实例属性会屏蔽类属性，即引用的是实例属性，若实例对象没有该名称的实例属性，则引用的是类属性；如果在实例方法更改某个属性，并且存在同名的类属性，此时若实例对象有该名称的实例属性，则修改的是实例属性，若实例对象没有该名称的实例属性，则会创建一个同名称的实例属性。想要修改类属性，如果在类外，可以通过类对象修改，如果在类里面，只有在类方法中进行修改。


# 6.继承和多重继承

　上面谈到了类的基本定义和使用方法，这只体现了面向对象编程的三大特点之一：封装。下面就来了解一下另外两大特征：继承和多态。
　在Python中，如果需要的话，可以让一个类去继承一个类，被继承的类称为父类或者超类、也可以称作基类，继承的类称为子类。并且Python支持多继承，能够让一个子类有多个父类。
　Python中类的继承定义基本形式如下：
```
#父类 
class superClassName: 
    block 
 
#子类 
class subClassName(superClassName): 
    block  

```

　　在定义一个类的时候，可以在类名后面紧跟一对括号，在括号中指定所继承的父类，如果有多个父类，多个父类名之间用逗号隔开。以大学里的学生和老师举例，可以定义一个父类UniversityMember，然后类Student和类Teacher分别继承类UniversityMember：

```
# -*- coding: UTF-8 -*- 
 
class UniversityMember: 
 
    def __init__(self,name,age): 
        self.name = name 
        self.age = age 
 
    def getName(self): 
        return self.name 
 
    def getAge(self): 
        return self.age 
 
class Student(UniversityMember): 
 
    def __init__(self,name,age,sno,mark): 
        UniversityMember.__init__(self,name,age)     #注意要显示调用父类构造方法，并传递参数self 
        self.sno = sno 
        self.mark = mark 
 
    def getSno(self): 
        return self.sno 
 
    def getMark(self): 
        return self.mark 
 
 
 
class Teacher(UniversityMember): 
 
    def __init__(self,name,age,tno,salary): 
        UniversityMember.__init__(self,name,age) 
        self.tno = tno 
        self.salary = salary 
 
    def getTno(self): 
        return self.tno 
 
    def getSalary(self): 
        return self.salary  
```

　　在大学中的每个成员都有姓名和年龄，而学生有学号和分数这2个属性，老师有教工号和工资这2个属性，从上面的代码中可以看到：
　　1）在Python中，如果父类和子类都重新定义了构造方法__init( )__，在进行子类实例化的时候，子类的构造方法不会自动调用父类的构造方法，必须在子类中显示调用。
　　2）如果需要在子类中调用父类的方法，需要以”父类名.方法“这种方式调用，以这种方式调用的时候，注意要传递self参数过去。
　　对于继承关系，子类继承了父类所有的公有属性和方法，可以在子类中通过父类名来调用，而对于私有的属性和方法，子类是不进行继承的，因此在子类中是无法通过父类名来访问的。
　　Python支持多重继承。对于多重继承，比如
　　class SubClass(SuperClass1,SuperClass2)
　　此时有一个问题就是如果SubClass没有重新定义构造方法，它会自动调用哪个父类的构造方法？这里记住一点：以第一个父类为中心。如果SubClass重新定义了构造方法，需要显示去调用父类的构造方法，此时调用哪个父类的构造方法由你自己决定；若SubClass没有重新定义构造方法，则只会执行第一个父类的构造方法。并且若SuperClass1和SuperClass2中有同名的方法，通过子类的实例化对象去调用该方法时调用的是第一个父类中的方法。


# 7.多态

　　多态即多种形态，在运行时确定其状态，在编译阶段无法确定其类型，这就是多态。Python中的多态和Java以及C++中的多态有点不同，Python中的变量是弱类型的，在定义时不用指明其类型，它会根据需要在运行时确定变量的类型（个人觉得这也是多态的一种体现），并且Python本身是一种解释性语言，不进行预编译，因此它就只在运行时确定其状态，故也有人说Python是一种多态语言。在Python中很多地方都可以体现多态的特性，比如内置函数len(object)，len函数不仅可以计算字符串的长度，还可以计算列表、元组等对象中的数据个数，这里在运行时通过参数类型确定其具体的计算过程，正是多态的一种体现。这有点类似于函数重载（一个编译单元中有多个同名函数，但参数不同），相当于为每种类型都定义了一个len函数。这是典型的多态表现。有些朋友提出Python不支持多态，我是完全不赞同的。
　　本质上，多态意味着可以对不同的对象使用同样的操作，但它们可能会以多种形态呈现出结果。len(object)函数就体现了这一点。在C++、Java、C#这种编译型语言中，由于有编译过程，因此就鲜明地分成了运行时多态和编译时多态。运行时多态是指允许父类指针或名称来引用子类对象，或对象方法，而实际调用的方法为对象的类类型方法，这就是所谓的动态绑定。编译时多态有模板或范型、方法重载（overload）、方法重写（override）等。而Python是动态语言，动态地确定类型信息恰恰体现了多态的特征。在Python中，任何不知道对象到底是什么类型，但又需要对象做点什么的时候，都会用到多态。
　　能够直接说明多态的两段示例代码如下：
## 多态方法
```
# -*- coding: UTF-8 -*- 
 
_metaclass_=type # 确定使用新式类 
class calculator: 
 
    def count(self,args): 
        return 1 
 
calc=calculator() #自定义类型 
 
from random import choice 
obj=choice(['hello,world',[1,2,3],calc]) #obj是随机返回的 类型不确定 
print type(obj) 
print obj.count('a') #方法多态  
```


　　对于一个临时对象obj，它通过Python的随机函数取出来，不知道具体类型（是字符串、元组还是自定义类型），都可以调用count方法进行计算，至于count由谁（哪种类型）去做怎么去实现我们并不关心。
　　有一种称为”鸭子类型（duck typing）“的东西，讲的也是多态：
```
_metaclass_=type # 确定使用新式类 
class Duck: 
    def quack(self):  
        print "Quaaaaaack!" 
    def feathers(self):  
        print "The duck has white and gray feathers." 
 
class Person: 
    def quack(self): 
        print "The person imitates a duck." 
    def feathers(self):  
        print "The person takes a feather from the ground and shows it." 
 
def in_the_forest(duck): 
    duck.quack() 
    duck.feathers() 
 
def game(): 
    donald = Duck() 
    john = Person() 
    in_the_forest(donald) 
    in_the_forest(john) 
 
game()  

```

　　就in_the_forest函数而言，参数对象是一个鸭子类型，它实现了方法多态。但是实际上我们知道，从严格的抽象来讲，Person类型和Duck完全风马牛不相及。


## 多态运算符
```
def add(x,y): 
    return x+y 
 
print add(1,2) #输出3 
 
print add("hello,","world") #输出hello,world 
 
print add(1,"abc") #抛出异常 TypeError: unsupported operand type(s) for +: 'int' and 'str'  
```

　　上例中，显而易见，Python的加法运算符是”多态“的，理论上，我们实现的add方法支持任意支持加法的对象，但是我们不用关心两个参数x和y具体是什么类型。
　　Python同样支持运算符重载，实例如下：
```
class Vector: 
   def __init__(self, a, b): 
      self.a = a 
      self.b = b 
 
   def __str__(self): 
      return 'Vector (%d, %d)' % (self.a, self.b) 
 
   def __add__(self,other): 
      return Vector(self.a + other.a, self.b + other.b) 
 
v1 = Vector(2,10) 
v2 = Vector(5,-2) 
print v1 + v2  

```

　　一两个示例代码当然不能从根本上说明多态。普遍认为面向对象最有价值最被低估的特征其实是多态。我们所理解的多态的实现和子类的虚函数地址绑定有关系，多态的效果其实和函数地址运行时动态绑定有关。在C++, Java, C#中实现多态的方式通常有重写和重载两种，从上面两段代码，我们其实可以分析得出Python中实现多态也可以变相理解为重写和重载。在Python中很多内置函数和运算符都是多态的。

参考文献：
http://www.cnblogs.com/dolphin0520/archive/2013/03/29/2986924.html
http://www.cnblogs.com/jeffwongishandsome/archive/2012/10/06/2713258.html







