---
title:  python数据类型之字符串
categories: python   
toc: true  
tags: [python]
---


&emsp;python和c语言不一样，**没有单个字符的数据类型**，取而代之的是一个字符的字符串，**python的字符串被划分为不可变序列**，意味着这些字符串所包含的字符串存在从左到右的位置顺序，并且他们不可以在原处修改。

下表是常见的字符串和表达式

|表达式|含义|
|-|-|
|s = ''|	空字符串|
|s = "sam" 或 s = 'sam'	| 单引号和双引号相同|
|s = ''' abcde ''' |	三引号字符串（单引号和双引号都可）|
|s = "\temp \n dfsdfsf"	| 其中包含转义字符|
|s1 + s2 |	合并|
|s * 3	| 重复|
|s[i]	|索引|
|s[i:j]	|分片|
|len(s)	|取长度|
|“my name is {0} ,age is {1}".format(name,age)	|字符串格式化表达式 |
|s.find("aaaa")	|搜素|
|s.rstrip()	|移除空格|
|s.split(',')	|加入分隔符|
|s.isdigit()	|内容测试（是否是digit） |   
|s.lower()	|大小写转换|
|s.endwith("spam") |	结束测试|
|”spam".join(strlist)	|插入分隔符|




# 1.字符串常量
* 单引号： ‘spa"m’
* 双引号：“spa'm"
* 三引号：''' .......spam...'''     /          """........spam......."""

单引号和双引号是可以互换的，可以在一个单引号字符串中嵌入一个双引号，反之，亦然。
```
>>'knsdfsfsi"ssss' , "dfsfldsfsldf'sdfsdf"
```

Python会自动的合并相邻的字符串常量，尽管可以简单的在他们之间增加+操作符来说明这一合并操作
```
title = "meaning" 'of' "life"
>>title
meaning of life
```
# 2.转义字符
&emsp;转义序列让我们可以在字符串中嵌入不容易通过键盘输入的字节，字符串常量中字符“\”，以及在它后面的一个或者多个字符，在最终字符串对象中会被一个单个字符所替代，这个字符通过转义序列定义了一个二进制值例如：有一个五个字符的字符串，其中嵌入了一个换行符和一个制表符
```
>>s = 'a\nb\tc'

>>print(s)
a
b    c

>>len(s)
5
```
**注意原始的反斜杠字符并不真正的和字符串一起存在在内存中**

下面是常见的反斜杠字符表


|表达式|含义|
|-|-|
|\newline |	忽视连续|
|\\	|反斜杠|
|\'	|单引号|
|\"	|双引号|
|\a	|响铃|
|\n	|换行|
|\t	|水平制表符|
|\v	|垂直制表符|
|\xhh	|十六进制值|
|\ooo	|八进制值|
|\0	|Null（不是字符串结尾）|



```
>>s = 'a\ob\oc'
>>s
'a\x00b\x00c'

>>len(s)
5
```

如果python没有作为一个合法的转义编码识别出在“\"后的字符，他就直接在最终的字符串中保留反斜杠
```
>>x = "C:\py\code"
>>x
'C:\\py\\code'

>>len(x)
10
```


# 3.raw 转义抑制
&emsp;有时我们会像这样打开一个文件
```
myfile = open('C:\new\test.data','w')                        #因为有转义的存在，所以最终显示的是：   C:(换行）ew(制表符）est.data      的文件
```
&emsp;&emsp;为了解决上面的问题，使用字母r（大写或小写）放在字符串的第一个引号的前面，这样会自动的关闭转义机制
```
myfile = open(r'C:\new\test.data','w')               # r会抑制转义
```
另外一种方法是，加入反斜杠的转义序列，如下：
```
myfile = open('C:\\new\\test.data','w')     
```

# 4.三引号
作用：
* 编写多行字符串
```
>> a = ''' my name is 
chenyansong , age is 24 '''
```
* 作为多行注释
```
""" this is print result"""                                    #表示注释
print(s)
```

# 5.字符串的基本操作
## 5.1.+
```
>>'abc' + 'def'               #字符串合并，两个字符串对象相加创建了一个新的字符串对象
'abcdef'
```
## 5.2.*
```
>>print('------------ ...more....-----------')               #90个横线
>>print( '-' * 90)                                                  #90个横线，这就是重复的作用，可以避免我们冗余的操作
```
注意：在应用于数字时，执行加法和乘法的相同的操作符+和* ，python不允许你在+表达式中混合数字和字符串：‘aaa'+9会跑出一个异常

## 5.3.in
&emsp;&emsp;in表达式操作符用于对字符和子字符串进行**成员关系的测试**
```
>>myjob = 'hacker'

>> 'k' in myjob                                       
True

>>'z' in myjob
False

>>"spam" in "abcdAspam"                                    #用来测试子串在父串中是否存在
True

```

# 6.索引和分片
## 6.1.索引
* 第一个元素的偏移为0
* 负偏移索引意味着从最后或右边反向进行计数
* s[0] 获取了第一个元素
* s[-2] 获取了倒数第二个元素，（就像s[len(s)-2]一样）

```
>>s = 'spam'
>>s[0], s[-2]
('s', 'a')
```

## 6.2.分片

![](http://ols7leonh.bkt.clouddn.com//assert/img/python/str/1.png)
 
* 上边界并不包含在内
* 分片的默认边界为0和序列的长度，如果没有给出的话
* s[1:3] 获取了从偏移为1的元素，知道但不包含偏移为3的元素
* s[1:] 获取了从偏移为1直到结尾（偏移为序列长度）之间的元素
* s[:3] 获取了偏移为0直到但不包含偏移为3之间的元素
* s[:-1] 获取了从偏移为0直到但是不包含最后一个元素之间的元素
* s[:] 获取了从偏移0到末尾之间的元素，这有效的实现了顶层s拷贝

```
>>s = 'spam'
>>s[1:3],s[1:],s[:-1]
('pa', 'pam', 'spa')
```

## 6.3.分片扩展：第三个限制值
&emsp;在python2.3中，分片表达式增加了一个可选的第三个索引，用作步进，如下
x[I:J:K]  表示：索引x对象中的元素，从偏移为I直到偏移为J-1，每隔K元素索引一次，默认K=1 

```
>>s = 'abcdefg'
>>s[1:5:2]
'bdf'

>>s = 'hello'
>>s[::-1]
'olleh'                                       #步进-1表示分片将会从右至左进行而不是通常的从左至右，实际的意义就是将取得的字符串反转

```



# 7.字符串转换工具
```
>>int('42'),str(42)
(42,'42')

>>s = '42'
>>I = 1
>>s +I
TypeError:cannot concatenate 'str' and 'int' object

>>int(s) +I                                                      #转换成int
43

>>s + str(I)                                                   #转换成str
'431'


>>ord('s')                                                   #ord函数将单个字符转换成ASCII码
115

>>chr(115)                                                #chr将ASCII码转成对应的字符
's'
```




# 8.字符串方法
## 8.1.修改字符串
```
#方法1
>> s = 'spamy'
>> s = s[:3] + 'xx' + s[5:]                                          #切片+拼接
>>s
'spaxxy'


#方法2
>> s = 'spammymmy'
>> s = s.replace('mm','xx')                                    #replace直接替换，会替换匹配到的所有
>> s
'spaxxyxxy'

>> s = 'spammymmy'
>> s = s.replace('mm','xx',1)                                    #replace直接替换，只是替换第一个
>> s
'spaxxymmy'


#方法3
>> s = 'xxxxSPAMxxxxSPAMxxxx'
>> where = s.find('SPAM')
>>where
5
>>s = s[:where] + 'EGGS' + s[(where + 4):]
>>s
'xxxxEGGSxxxxSPAMxxxx'


#方法4
>> s = 'spammy'
>> L =list(s)
>> L
['s', 'p', 'a', 'm', 'm', 'y']

>L[3] = 'x'
>L[4] = 'x'
>>L
['s', 'p', 'a', 'x', 'x', 'y']

>>s = ''.join(L)                        #使用join函数去拼接
>>s
'spaxxy'



#join的用法
>> 'SPAM'.join(['eggs', 'sausage', 'ham', 'toast'])               #使用SPAM去连接，组成新的字符串
 'eggsSPAMsausageSPAMhamSPAMtoast'
```

## 8.2.split
&emsp;将一个字符串分割为一个子字符串的列表，以分隔符字符串为标准，默认的分隔符为空格
```
>> line = 'aaa bbb ccc'
>> cols = line.split()
>> cols
['aaa', 'bbb', 'ccc']



>>line = "i'mSPAMaSPAMboy"                           
>> line.split(SPAM)                                          #使用“SPAM”去分割
["i'm", 'a', 'boy']

```
## 8.3.rstrip 清除每行末尾空白
```
>>line = "the knigts who say Ni!\n"
>>line.rstrip()
"the knights who say Ni!"                           #去掉每行末尾的换行符

```

## 8.4.大小写转换
```
>>s = 'my name is cys'
>>s.upper()
'MY NAME IS CYS'
```

## 8.5.内容检测
```
>>s = 'my name is cys'
>>"cys" in s
True
```


## 8.6.检测末尾或起始字符串
```
>>s = 'my name is cys'
>>s.endswith(cys)
True

```



# 9.字符串格式化表达式%
```
>>'This is %d %s bird' % (1,'dead')
This is 1 dead bird
```
格式化字符串：
* 在%操作符的左侧放置一个需要进行格式化的字符串，这个字符串带有一个或多个嵌入的转换目标，都以%开头（例如：%d)
* 在%操作符右侧放置一个（或多个，嵌入到元组中）对象，这些对象将会插入到左侧想让python进行格式化字符串的一个或多个转换目标的位置上去
* 注意：当不止一个值待插入的时候，应该在右侧用括号把它们括起来，即将他们放入到元组中去，%格式化表达式操作符在其右侧期待一个或多个项（此时就是元组）

字符串格式化代码

|字符|含义|
|-|-|
|s	|字符串（或任何对象）     |
|r	|s,但使用repr ,而不是str  |
|c	|字符                     |
|d	|十进制                   |
|i	|整数                     |
|u	|无符号整数               |
|o	|八进制整数               |
|X	|打印大写                 |
|e	|浮点指数                 |
|f	|浮点十进制               |
                              
## 9.1.基于字典的格式化
```
>>"%(n)d %(x)s" % {"n":1, "x":"spam"}               #格式化字符串里（n)和（x）引用了右边字典中的键，并提取他们相应的值
'1 spam'


>>reply = '''
my name is %(name)s
my birthday is %(birthday)s
my work is %(work)s
'''
>>values = {"name":"cys","birthday":"1992.12.21","work":"IT worker"}

>>print(reply % values)
my name is  cys
my birthday is 1992.12.21
my work is IT worker

```

# 10.字符串格式化调用方法：format
在主体字符串中，花括号通过位置（例如：{1}）或关键字（例如：{food}）指出替换目标即将要插入的参数。
```
>>"{0}, {1}, and {2}".format('spam', 'ham', 'eggs')                  #通过位置
'spam , ham and eggs'


>>"my name is {name}, old is {age}".format( name="cys", age="24" )               #通过关键字
'my name is cys, old is 24'


>>"my name is {name}, old is {0}, and my favious is {food}".format( 24, name="cys", food=[1,3] )         #位置和关键字混合
'my name is cys, old is 24, and my favious is [1,3]'


>>import sys
>>"My {1[spam]} runs {0.platform}".format(sys, {'spam":"laptop"})                                             #1表示第一个参数，而1[spam]表示取第一个参数的属性
'My laptop runs win32'

>>"My {config[spam]} runs {sys.platform}".format(sys = sys , config = {"spam":"laptop"})            
'My laptop runs win32'


>>someList = list('SPAM')
>>parts = someList[0],someList[-1], someList[1:3]
>>"first={0},last={1}, middle={2}".format(*parts)
"first=S, last=M, middle=['P','A']"

```

具体格式化
{fieldname!conversionflag:formatspec}
* fieldname是指参数的一个数字或关键字，后面跟着可选的“.name" 或者”[index]“成分引用
* conversionflag 可以是r、s、或者是a分别表示该值上对repr,str、或ascii内置函数的一次调用
* formatspec 指定了如何表示该值，包括字段宽度、对齐方式、不零、小数点精度等细节
冒号后的formatspec 组成形式
[ [fill]align] [sign] [#] [0] [widht] [.precision] [typecode]
align可能是<   >    =    ^  表示左对齐、右对齐、一个标记字符后的补充或居中对齐


```
>> '{0:<10} = {1:>10}'.format('spam', 123,4567)
'spam      =   123.4567'


>>'{0:f}, {1:.2f}, {2:06.2f}'.format(3.14159, 3.14159, 3.14159)
'3.141590,  3.14,  003.14'


>>'{0:.{1}f}'.format(1/3.0, 4)
'0.3333'


>>format(1.2345, '.2f')
'1.23'
```





