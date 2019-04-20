![image-20190416192023948](/Users/chenyansong/Library/Application Support/typora-user-images/image-20190416192023948.png)



* 假定G是一个文法，S是他的开始符号，如果
  $$
  S =>^*  \alpha
  $$

* 最左推导

  * 从上往下，从右向左

* 最右推导

  * 从上往下，从左向右

* 语法树

  ![image-20190416194832508](/Users/chenyansong/Library/Application Support/typora-user-images/image-20190416194832508.png)



语法树与二义性

* 二义性：如果一个文法存在某个句子对应两棵不同的语法树，则说这个文法是二义性的

  ![image-20190416195649863](/Users/chenyansong/Library/Application Support/typora-user-images/image-20190416195649863.png)

一个句型并不是唯一的对应一个语法树

![image-20190416195349730](/Users/chenyansong/Library/Application Support/typora-user-images/image-20190416195349730.png)

如果文法出现上述的情况，我们就说文法是二义性的



* 语言的二义性



# 词法分析

分解单词符号：

* 基本字:begin， repeat，for， if  … 这些字又称为关键字
* 标识符：用来表示各种名字，如变量名，数组名，过程名
* 常数：各种类型的常数
* 云算符：+，-，*，/,...
* 界符：逗号，分号，空白



正规式和正规集



NFA和DFA是等价的，这个可以通过构造的算法证明



1. 消除初态和终态上的差别

