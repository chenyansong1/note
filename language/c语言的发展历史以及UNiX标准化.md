[toc]



# c语言的发展历史以及UNiX标准化



## C发展史：K&R C/C89/C99/C11 以及 C++发展史： C++98/C++03/C++11




文章转载来自http://blog.csdn.net/kingcat666/article/details/44984711



### C语言的发展阶段

C语言之所以命名为C，是因为 C语言源自Ken Thompson发明的B语言，而 B语言则源自BCPL语言。

1. 1967年，剑桥大学的Martin Richards对CPL语言进行了简化，于是产生了BCPL（Basic Combined Programming Language）语言。
2. 20世纪60年代，美国AT&T公司贝尔实验室（AT&T Bell Laboratory）的研究员Ken Thompson闲来无事，手痒难耐，想玩一个他自己编的，模拟在太阳系航行的电子游戏——Space Travel。他背着老板，找到了台空闲的机器——PDP-7。但这台机器没有操作系统，而游戏必须使用操作系统的一些功能，于是他着手为PDP-7开发操作系统。后来，这个操作系统被命名为——UNIX。
3. 1970年，美国贝尔实验室的 Ken Thompson，以BCPL语言为基础，设计出很简单且很接近硬件的B语言（取BCPL的首字母）。并且他用B语言写了第一个UNIX操作系统。
4. 1971年，同样酷爱Space Travel的Dennis M.Ritchie为了能早点儿玩上游戏，加入了Thompson的开发项目，合作开发UNIX。他的主要工作是改造B语言，使其更成熟。
5. 1972年，美国贝尔实验室的 D.M.Ritchie 在B语言的基础上最终设计出了一种新的语言，他取了BCPL的第二个字母作为这种语言的名字，这就是C语言。
6. 1973年初，C语言的主体完成。Thompson和Ritchie迫不及待地开始用它完全重写了UNIX。此时，编程的乐趣使他们已经完全忘记了那个"Space Travel"，一门心思地投入到了UNIX和C语言的开发中。随着UNIX的发展，C语言自身也在不断地完善。直到今天，各种版本的UNIX内核和周边工具仍然使用C语言作为最主要的开发语言，其中还有不少继承Thompson和Ritchie之手的代码。
7. 在开发中，他们还考虑把UNIX移植到其他类型的计算机上使用。C语言强大的移植性（Portability）在此显现。机器语言和汇编语言都不具有移植性，为x86开发的程序，不可能在Alpha,SPARC和ARM等机器上运行。而C语言程序则可以使用在任意架构的处理器上，只要那种架构的处理器具有对应的C语言编译器和库，然后将C源代码编译、连接成目标二进制文件之后即可运行。
8. 1977年，Dennis M.Ritchie发表了不依赖于具体机器系统的C语言编译文本《可移植的C语言编译程序》。

C语言继续发展，在1982年，很多有识之士和美国国家标准协会为了使这个语言健康地发展下去，决定成立C标准委员会，建立C语言的标准。委员会由硬件厂商，编译器及其他软件工具生产商，软件设计师，顾问，学术界人士，C语言作者和应用程序员组成。1989年，ANSI发布了第一个完整的C语言标准——ANSI X3.159—1989，简称“ ***\*C89\****”，不过人们也习惯称其为“ ***\*ANSI C\****”。C89在1990年被国际标准组织ISO(International Organization for Standardization)一字不改地采纳，ISO官方给予的名称为：ISO/IEC 9899，所以ISO/IEC9899: 1990也通常被简称为“C90”。1999年，在做了一些必要的修正和完善后，ISO发布了新的C语言标准，命名为ISO/IEC 9899：1999，简称“ ***\*C99\****”。 在2011年12月8日，ISO又正式发布了新的标准，称为ISO/IEC9899: 2011，简称为“C11”。

#### C语言标准

##### K&R C

起初，C语言没有官方标准。1978年由美国电话电报公司(AT&T）贝尔实验室正式发表了C语言。布莱恩·柯林汉（Brian Kernighan） 和 丹尼斯·里奇（Dennis Ritchie） 出版了一本书，名叫《The C Programming Language》。这本书被 C语言开发者们称为K&R，很多年来被当作 C语言的非正式的标准说明。人们称这个版本的 C语言为K&R C。[3]

K&R C主要介绍了以下特色：

1. ***\*结构体（struct）类型\**
   **
2. ***\*长整数（long int）类型\**
   **
3. ***\*无符号整数（unsigned int）类型\**
   **
4. **把运算符=+和=-改为+=和-=。因为=+和=-会使得编译器不知道使用者要处理i = -10还是i =- 10，使得处理上产生混淆。**

即使在后来ANSI C标准被提出的许多年后，K&R C仍然是许多编译器的最标准要求，许多老旧的编译器仍然运行K&R C的标准。

##### ANSI C / C89标准

1970到80年代，C语言被广泛应用，从大型主机到小型微机，也衍生了C语言的很多不同版本。

1983年，美国国家标准协会（ANSI）成立了一个委员会X3J11，来制定 C语言标准。

1989年，美国国家标准协会（ANSI）通过了C语言标准，被称为ANSI X3.159-1989 " **Programming Language C**"。因为这个标准是1989年通过的，所以一般简称 ***\*C89标准\****。有些人也简称 ***\*ANSI C\****，因为这个标准是美国国家标准协会（ANSI）发布的。

1990年，国际标准化组织（ISO）和国际电工委员会（IEC）把C89标准定为C语言的国际标准，命名为 **ISO/IEC 9899:1990 - Programming languages -- C**。因为此标准是在1990年发布的，所以有些人把简称作 **C90标准**。不过大多数人依然称之为C89标准，因为此标准与ANSI C89标准完全等同。

1994年，国际标准化组织（ISO）和国际电工委员会（IEC）发布了C89标准修订版，名叫ISO/IEC 9899:1990/Cor 1:1994 ，有些人简称为 ***\*C94\**** **标准**。

1995年，国际标准化组织（ISO）和国际电工委员会（IEC）再次发布了C89标准修订版，名叫ISO/IEC 9899:1990/Amd 1:1995 - C Integrity ，有些人简称为 ***\*C95标准\****。

##### C99标准

1999年1月，国际标准化组织（ISO）和国际电工委员会（IEC）发布了C语言的新标准，名叫ISO/IEC 9899:1999 - Programming languages -- C，简称 ***\*C99标准\****。这是C语言的 ***\*第二个官方标准\****。

在C99中包括的特性有：

1. 增加了对编译器的限制，比如源程序每行要求至少支持到 4095 字节，变量名函数名的要求支持到 63 字节（extern 要求支持到 31）。
2. 增强了预处理功能。例如：
3. 宏支持取可变参数 #define Macro(...) __VA_ARGS__
4. 使用宏的时候，允许省略参数，被省略的参数会被扩展成空串。
5. 支持 // 开头的单行注释（这个特性实际上在C89的很多编译器上已经被支持了）
6. 增加了新关键字 restrict, inline, _Complex, _Imaginary, _Bool
7. 支持 long long, long double _Complex, float _Complex 等类型
8. 支持不定长的数组，即数组长度可以在运行时决定，比如利用变量作为数组长度。声明时使用 int a[var] 的形式。不过考虑到效率和实现，不定长数组不能用在全局，或 struct 与 union 里。
9. 变量声明不必放在语句块的开头，for 语句提倡写成 for(int i=0;i<100;++i) 的形式，即i 只在 for 语句块内部有效。
10. 允许采用（type_name）{xx,xx,xx} 类似于 C++ 的构造函数的形式构造匿名的结构体。
11. 复合字面量：初始化结构的时候允许对特定的元素赋值，形式为：
12. struct test{int a[3]，b;} foo[] = { [0].a = {1}, [1].a = 2 };
13. struct test{int a, b, c, d;} foo = { .a = 1, .c = 3, 4, .b = 5 }; // 3,4 是对 .c,.d 赋值的
14. 格式化字符串中，利用 \u 支持 unicode 的字符。
15. 支持 16 进制的浮点数的描述。
16. printf scanf 的格式化串增加了对 long long int 类型的支持。
17. 浮点数的内部数据描述支持了新标准，可以使用 #pragma 编译器指令指定。
18. 除了已有的 __line__ __file__ 以外，增加了 __func__ 得到当前的函数名。
19. 允许编译器化简非常数的表达式。
20. 修改了 /% 处理负数时的定义，这样可以给出明确的结果，例如在C89中-22 / 7 = -3, -22% 7 = -1，也可以-22 / 7= -4, -22% 7 = 6。 而C99中明确为 -22 / 7 = -3, -22% 7 = -1，只有一种结果。
21. 取消了函数返回类型默认为 int 的规定。
22. 允许 struct 定义的最后一个数组不指定其长度，写做 [](flexible array member)。
23. const const int i 将被当作 const int i 处理。
24. 增加和修改了一些标准头文件，比如定义 bool 的 <stdbool.h> ，定义一些标准长度的 int 的 <inttypes.h> ，定义复数的 <complex.h> ，定义宽字符的 <wctype.h> ，类似于泛型的数学函数 <tgmath.h>， 浮点数相关的 <fenv.h>。 在<stdarg.h> 增加了 va_copy 用于复制 ... 的参数。里增加了 struct tmx ，对 struct tm 做了扩展。
25. 输入输出对宽字符以及长整数等做了相应的支持。

但是各个公司对C99的支持所表现出来的兴趣不同。 **GCC和其它一些商业编译器支持C99的大部分特性**， ***\*微软和Borland\****却似乎对此不感兴趣。

##### C11标准

2011年12月8日，国际标准化组织（ISO）和国际电工委员会（IEC）再次发布了C语言的新标准，名叫ISO/IEC 9899:2011 - Information technology -- Programming languages -- C ，简称 ***\*C11标准\****，原名 ***\*C1X\****。这是C语言的第三个官方标准，也是C语言的最新标准。

新的标准提高了对C++的兼容性，并增加了一些新的特性。这些新特性包括：

1. 对齐处理(Alignment)的标准化(包括_Alignas标志符，alignof运算符, aligned_alloc函数以及<stdalign.h>头文件。
2. _Noreturn 函数标记，类似于 gcc 的 __attribute__((noreturn))。
3. _Generic 关键字。
4. 多线程(Multithreading)支持，包括：
5. _Thread_local存储类型标识符，<threads.h>头文件，里面包含了线程的创建和管理函数。
6. _Atomic类型修饰符和<stdatomic.h>头文件。
7. 增强的Unicode的支持。基于C Unicode技术报告ISO/IEC TR 19769:2004，增强了对Unicode的支持。包括为UTF-16/UTF-32编码增加了char16_t和char32_t数据类型，提供了包含unicode字符串转换函数的头文件<uchar.h>.
8. 删除了 gets() 函数，使用一个新的更安全的函数gets_s()替代。
9. 增加了边界检查函数接口，定义了新的安全的函数，例如 fopen_s()，strcat_s() 等等。
10. 增加了更多浮点处理宏。
11. 匿名结构体/联合体支持。这个在gcc早已存在，C11将其引入标准。
12. 静态断言(static assertions)，_Static_assert()，在解释 #if 和 #error 之后被处理。
13. 新的 fopen() 模式，(“…x”)。类似 POSIX 中的 O_CREAT|O_EXCL，在文件锁中比较常用。
14. 新增 quick_exit() 函数作为第三种终止程序的方式。当 exit()失败时可以做最少的清理工作。



### C++语言发展大概可以分为三个阶段

以下是C++发展年代列表：

1. 在“C with Class”阶段，研制者在C语言的基础上加进去的特征主要有：类及派生类、共有和私有成员的区分、类的构造函数和析构函数、友元、内联函数、赋值运算符的重载等。
2. 1985年公布的的C++语言**1.0版**的内容中又添加了一些重要特征：***\*虚函数的概念、函数和运算符的重载、引用、常量\****（constant）等。
3. 1989年推出的**2.0版**形成了更加完善的支持面向对象程序设计的C++语言，新增加的内容包括：***\*类的保护成员、多重继承、对象的初始化与赋值的递归机制、抽象类、静态成员函数、const成员函数\****等。
4. 1993年的C++语言***\*3.0版本\****是C++语言的进一步完善，其中最重要的新特征是**模板（template）**,此外解决了多重继承产生的二义性问题和相应的构造函数与析构函数的处理等。
5. 1998年C++标准（ISO/IEC14882 Standard for the C++ Programming Language）得到了国际标准化组织（ISO）和美国标准化协会（ANSI）的批准，标准C++语言及其标准库更体现了C++语言设计的初衷。***\*名字空间的概念、标准模板库（STL）\****中增加的标准容器类、通用算法类和字符串类型等使得C++语言更为实用。此后C++是具有国际标准的编程语言，该标准通常简称***\*ANSI C++\****或***\*ISO C++ 98标准\****，以后每5年视实际需要更新一次标准。
6. 后来又在2003年通过了C++标准第二版（ISO/IEC 14882:2003）：这个新版本是一次技术性修订，对第一版进行了整理——修订错误、减少多义性等，但没有改变语言特性。这个版本常被称为***\*C++03\****。[2]
7. 此后，新的标准草案叫做C++ 0x。对于C++ 0x标准草案的最终国际投票已于2011年8月10日结束，并且所有国家都投出了赞成票，C++0x已经毫无疑义地成为正式国际标准。先前被临时命名为C++0x的新标准正式定名为ISO/IEC 14882:2011，简称ISO ***\*C++ 11标准\****。C++ 11标准将取代现行的C++标准C++98和C++03。国际标准化组织于2011年9月1日出版发布《ISO/IEC 14882:2011》，名称是：Information technology -- Programming languages -- C++ Edition: 3。

来源： < 



最初导致C++诞生的原因是在Bjarne博士等人试图去分析UNIX的内核的时候，这项工作开始于1979年4月，当时由于没有合适的工具能够有效的分析由于内核分布而造成的网络流量，以及怎样将内核模块化。同年10月，Bjarne博士完成了一个可以运行的预处理程序，称之为Cpre，它为C加上了类似Simula的类机制。在这个过程中，Bjarne博士开始思考是不是要开发一种新的语言，当时贝尔实验室对这个想法很感兴趣，就让Bjarne博士等人组成一个开发小组，专门进行研究。

来源： <http://c.biancheng.net/cpp/biancheng/view/1.html>

C语言标准：

不是叫做C

#### ***\*C++ 98 标准\****

C++标准第一版，1998年发布。正式名称为ISO/IEC 14882:1998[18] 。

绝大多数编译器都支持C++98标准。不过当时错误地引入了 ****\*export\*****关键字。由于技术上的实现难度，除了Comeau C++编译器export关键字以外，没有任何编译器支持export关键字。并且这个标准对现代的一些编译理念有相当的差距，有很多在高级语言都应当有的功能，它都没有。这也正是后来需要制定C++11标准的原因所在。

##### ***\*C++ 03 标准\****

C++标准第二版，2003年发布。正式名称为ISO/IEC 14882:2003[19] 。这个标准仅仅是C++98修订版，与 ***\*C++98几乎一样\****，没做什么修改。仅仅是对C++98做了一些“ ***\*勘误\****”，就连主流编译器（受C99标准影响）都已支持的 ***\*long long\****都没有被加入C++03标准。

##### ***\*C++ 11 标准\****

C++标准第三版，2011年8月12日发布。正式名称为ISO/IEC 14882:2011[20] 。

由C++标准委员会于2011年8月12日公布，并于2011年9月出版。2012年2月28日的国际标准草案(N3376)是最接近于现行标准的草案（编辑上的修正）。C++11包含了 ***\*核心语言的新机能\****，并且拓展C++标准程序库，并且加入了大部分的C++ Technical Report 1程序库(数学上的特殊函数除外)。此次标准为C++98发布后13年来第一次重大修正。

注意： ***\*C++11标准\****（ISO/IEC 14882:2011）与 ***\*C11标准\****（ISO/IEC 9899:2011）是两个完全不同的标准，后者是C语言的标准。

##### ***\*C++ 14 标准\****

C++标准第四版，2014年8月18日发布。正式名称为ISO/IEC 14882:2014[21] 。

2014年8月18日，ISO组织在其网站上发布文章称：

C++ 作者 Bjarne Stroustrup 称，主要的编译器开发商已经实现了 C++ 14 规格。

C++ 14 是 C++ 11 的 ***\*增量更新\****，主要是支持 ***\*普通函数的返回类型推演\****，泛型 lambda，扩展的 lambda 捕获，对 constexpr 函数限制的修订，constexpr变量模板化等等。

C++14是C++语言的最新标准，正式名称为"International Standard ISO/IEC 14882:2014(E) Programming Language C++"。C++14旨在作为C++11的一个小扩展，主要提供漏洞修复和小的改进。C++14标准的委员会草案（Committee Draft）N3690于2013年5月15日发表。工作草案（Working Draft）N3936已于2014年3月02日完成。最终的投票期结束于2014年8月15日，结果（一致通过）已于8月18日公布。

如何理解C++？《摘自Effective C++》

*C++**已经是个***\*多重范型编程语言\******(multiparadigm programming language)**，一个同事支持**过程形式****(procedural)**、**面向对象形式****(object-oriented)**、***\*函数形式\******(functional)**、**范型形式****(generic)**、**元编程形式****(metaprogramming)**的语言。*

 

如何理解这样一个语言？

*将**C++**视为一个由***\*相关语言组成的联邦而非单一语言\****，在其某个次语言中，各种守则与通例都倾向简单、直观易懂、并且容易记住。*

 

#### ***C++\**的\**4\**个次语言：\***



1. ***\*C\**** :      *C++**以**C**为基础。区块**(blocks)**、语句**(statements)**、预处理器**(preprocessor)**、内置数据类型**(built-in data types)**、数组**(arrays)**、指针**(pointers)**等都来自**C**。*
2. ***\*Object-oriented C ++\**** :    *类**(class)**、封装**(encapsulation)**、继承**(inheritance)**、多态**(polymorphism)**、虚函数**(virtual function)**等都是面向对象设计在**C++**上的最直接实施。*
3. ***\*Template C++ (generic programming)\**** :    *C++**的范型编程**(generic programming)**部分。他们带来新的编程范型**(programming paradigm)**，也就是所谓的**template meta programming(TMP**，模板元编程**)**。*
4. ***\*STL\****:    *即**template**程序库，对容器、迭代器、算法以及函数对象的规约有极佳的紧密配合与协调。*

*C++**并不是一个带有一组守则的一体语言，而是由**4**个次语言组成的联邦语言，每个次语言都有自己的规约。*



remember

*C++**高效编程守则视状况而变化，取决于你使用**C++**的哪一部分。*



来源： <http://blog.csdn.net/livelylittlefish/article/details/5729847>

**再也不敢轻易声称自己是个C++程序员了，无知者无畏，果然是呀![img]()![再见](http://static.blog.csdn.net/xheditor/xheditor_emot/default/bye.gif)**

![img]()

### C++与C的关系

C语言是C++的基础，C++和C语言在很多方面是兼容的。

C语言是一个 **结构化语言**，它的重点在于 ***\*算法与数据结构\****。C程序的设计首要考虑的是如何通过一个 ***\*过程\****，对输入（或环境条件）进行运算处理得到输出（或实现过程（事物）控制）。C++，首要考虑的是如何 ***\*构造一个对象模型\****，让这个模型能够契合与之对应的问题域，这样就可以通过 ***\*获取对象的状态信息得到输出或实现过程（事物）控制\****。所以C语言和C++的最大区别在于它们解决问题的思想方法不一样。

C++对C的“ ***\*增强\****”，表现在六个方面：

- (1) 类型检查更为严格。
- (2) 增加了面向对象的机制。
- (3) 增加了泛型编程的机制（Template）。
- (4) 增加了异常处理。
- (5) 增加了运算符重载。
- (6) 增加了标准模板库（STL）。

***\*与C不兼容之处\****

C++一般被认为是C的超集合（Superset），但这并不严谨。大部分的C代码可以很轻易的在C++中正确编译，但仍有少数差异，导致某些有效的C代码在C++中失效，或者在C++中有不同的行为。

1. 最常见的差异之一是，C允许从void*隐式转换到其它的指针类型，但C++不允许。
2. 另一个常见的可移植问题是，C++定义了新关键字，例如如new，class，它们在C程序中可以作为识别字（例：变量名）的。
3. 在C标准（C99）中去除了一些不兼容之处，也支持了一些C++的特性，如//注解，以及在代码中混合声明。不过C99也纳入几个和C++冲突的新特性（如：可变长度数组、原生复数类型和复合逐字常数）。

若要混用C和C++的代码，则所有在C++中调用的C代码，必须放在 ***\*extern "C" { /\* C代码 \*/ }\**** 内。



## ISO C 与POSIX 的关系

转自：https://blog.csdn.net/jmh1996/article/details/80459787

我们经常会看到”ISO C “以及 “POSIX 接口”，那么究竟什么是ISO C，究竟什么POSIX,它们之间有什么联系与区别呢？

### ISO C 

ISO C就是 International Organization for Standardization 国际标准化组织为了提高C语言的移植性而设立的C语言标准，里面包含两部分：**C语法及语义；C标准函数库**。**其中只是定义了C标准函数库的函数原型、函数功能，而并未定义函数的具体实现。**
其中C标准函数库的头文件包括以下：

```shell
头文件	说明
assert.h	验证程序断言
complex.h	复数运算
ctype.h	字符分类与映射
errno.h	出错码
fenv.h	浮点环境
float.h	浮点常亮以及特性
inttypes.h	整形格式变换
iso646.h	赋值、关系以及一元操作宏
limits.h	编译时限制性常量
math.h	数学函数
setjmp.h	非局部跳转
signal.h	信号
stdarg.h	可变参数
stdbool.h	bool类型
stddef.h	标准定义
stdint.h	整形
stdio.h	标准输入输出
stdlib.h	标准函数
string.h	字符串相关函数
time.h	时间和日期
wctype.h	宽字符分类与映射
wchar.h	扩充的多字节和宽字符支持
```


这个标准粗来以后有啥用处哇？主要是这样子的，时间上有很多厂商、公司都在开发自己的C编译器，可是每个厂商的实现都可能不一样哇，如果没有标准的限制 那么大家对标准库函数的定义就会千差万别，那么这样就很不利于C程序在源码级从一种编译器移植到另外一种编译器···

举个例子 张三在VS 2015中写了一段很牛逼的代码，他觉得自己的代码特别稳，他写这些代码调用了很多VS给他提供的现成函数接口 例如像printf呀。。。。然后张三把代码丢给李四，可是李四用的是什么borland公司的C编译器。。。如果没事ISO C标准，那么vs和borland就可以将用一个printf解释为不同含义···，例如VS认为printf应该是输出函数，而borland却认为这个是输出函数。

如果有两个编译器都声称他们遵循ISO C标准，那么凡是ISO C白字黑字规定了的，两个编译器都应该严格执行。

### POSIX

POSIX 是Portable Operating System Interfaces 的缩写，是由IEEE制定胡标准簇。这些标准的制定目的是为了提升应用程序在不同UNIX系统环境之间的可移植性。这个**标准是ISO C的超集**。 **它不仅包含ISO C部分，还定义了很多系统服务接口，例如：socket相关接口，pthread线程相关接口等。同样的，POSIX也只是定义接口，而不定义具体的实现。**
POSIX标准定义的头文件包括：

```shell
<aio.h>
<arpa/inet.h>
<assert.h>
<complex.h>
<cpio.h>
<ctype.h>
<dirent.h>
<dlfcn.h>
<errno.h>
<fcntl.h>
<fenv.h>
<float.h>
<fmtmsg.h>
<fnmatch.h>
<ftw.h>
<glob.h>
<grp.h>
<iconv.h>
<inttypes.h>
<iso646.h>
<langinfo.h>
<libgen.h>
<limits.h>
<locale.h>
<math.h>
<monetary.h>
<mqueue.h>
<ndbm.h>
<net/if.h>
<netdb.h>
<netinet/in.h>
<netinet/tcp.h>
<nl_types.h>
<poll.h>
<pthread.h>
<pwd.h>
<regex.h>
<sched.h>
<search.h>
<semaphore.h>
<setjmp.h>
<signal.h>
<spawn.h>
<stdarg.h>
<stdbool.h>
<stddef.h>
<stdint.h>
<stdio.h>
<stdlib.h>
<string.h>
<strings.h>
<stropts.h>
<sys/ipc.h>
<sys/mman.h>
<sys/msg.h>
<sys/resource.h>
<sys/select.h>
<sys/sem.h>
<sys/shm.h>
<sys/socket.h>
<sys/stat.h>
<sys/statvfs.h>
<sys/time.h>
<sys/times.h>
<sys/types.h>
<sys/uio.h>
<sys/un.h>
<sys/utsname.h>
<sys/wait.h>
<syslog.h>
<tar.h>
<termios.h>
<tgmath.h>
<time.h>
<trace.h>
<ulimit.h>
<unistd.h>
<utime.h>
<utmpx.h>
<wchar.h>
<wctype.h>
<wordexp.h>
```



# unix系统家族树



![img](/Users/chenyansong/Documents/note/images/c_languge/unix-family.png)