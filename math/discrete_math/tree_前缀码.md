[TOC]

![image-20190613194318731](/Users/chenyansong/Documents/note/images/discrete_math/image-20190613194318731.png)







# 定长编码

我们使用3位对上面的8个字符进行编码，

各个字符出现的次数及码字如下：

![image-20190613194412139](/Users/chenyansong/Documents/note/images/discrete_math/image-20190613194412139.png)

![image-20190613194606500](/Users/chenyansong/Documents/note/images/discrete_math/image-20190613194606500.png)



解码是，我们每次3位截取，如下：

![image-20190613194643862](/Users/chenyansong/Documents/note/images/discrete_math/image-20190613194643862.png)



我们用二叉位置树表示如下：

![image-20190613194722222](/Users/chenyansong/Documents/note/images/discrete_math/image-20190613194722222.png)



> 所有叶子都在同一层，所有的符号编码之后，长度是相同，并且固定，称之为定长编码



# 变长编码

方式1：左孩子边标记为0，右孩子边标记为1

![image-20190613194809075](/Users/chenyansong/Documents/note/images/discrete_math/image-20190613194809075.png)

根据上面对各个字符的表示，我们可以进行如下的编码：

![image-20190613194858029](/Users/chenyansong/Documents/note/images/discrete_math/image-20190613194858029.png)

![image-20190613194914947](/Users/chenyansong/Documents/note/images/discrete_math/image-20190613194914947.png)



解码的方式：从第一个字符开始读，然后查编码表(树)，如果没有继续读取下一个字符，然后再查表，直到查询到字符串(即：能够解码)

![image-20190613194941452](/Users/chenyansong/Documents/note/images/discrete_math/image-20190613194941452.png)

![image-20190613194957411](/Users/chenyansong/Documents/note/images/discrete_math/image-20190613194957411.png)



方式2：

并不是所有的符号对应的顶点都是叶子顶点，有些符号在分枝顶点上

![image-20190613195044926](/Users/chenyansong/Documents/note/images/discrete_math/image-20190613195044926.png)

各个字符表示的方式如下：

![image-20190613195114275](/Users/chenyansong/Documents/note/images/discrete_math/image-20190613195114275.png)

![image-20190613195136814](/Users/chenyansong/Documents/note/images/discrete_math/image-20190613195136814.png)

各种表示方式的问题：

解码的时候会出现多种情况

![image-20190613195224621](/Users/chenyansong/Documents/note/images/discrete_math/image-20190613195224621.png)



# 前缀码

![image-20190613195306213](/Users/chenyansong/Documents/note/images/discrete_math/image-20190613195306213.png)

![image-20190613195404193](/Users/chenyansong/Documents/note/images/discrete_math/image-20190613195404193.png)

> 其中不存在着一个串是另一个串的前缀，叫做无前缀码，历史上称作**前缀码**，但是表示的是没有前缀性质



一个编码方案也可以使用二叉位置树来表示

* 对于树中的分枝点，令与他左孩子关联的边标记为0，与右孩子关联的边标记为1
* 对每个顶点而言，其编码就是由**根到该顶点的道路**中各边标号依次构成的序列



一般的讲，任何一个二元前缀码编码方案的二叉位置树表示中 **表示符号的顶点都一定是叶子**

