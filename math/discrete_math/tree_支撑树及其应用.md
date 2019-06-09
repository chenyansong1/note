[TOC]

# 最小支撑树



假设我们需要将水引入下面的节点

![image-20190609165106755](/Users/chenyansong/Documents/note/images/discrete_math/image-20190609165106755.png)

如果图中出现了回路，我们至少可以减少一条边，因为这样不会影响水的引入

![image-20190609165222635](/Users/chenyansong/Documents/note/images/discrete_math/image-20190609165222635.png)

若连通图G的支撑子图T是一棵树，则称T为G的生成树或支撑树

一个连通图可能有不同的支撑树

![image-20190609165512937](/Users/chenyansong/Documents/note/images/discrete_math/image-20190609165512937.png)



定理：

无向图G具有支撑树，当且仅当G是连通图

推论：

设G为一个n阶无向连通图，则其边数m>=n-1,因为无向连通图存在支撑树，而树的性质：m=n-1,所以无向连通图至少大于n-1



**最小支撑树**

给定一个无向连通赋权图，该图所有支撑树中各边权值之和最小者称为这个图的最小支撑树(minimal spanning tree ， MST)





# 如何找到最小支撑树

## 克鲁斯卡算法

先修建成本最低的水道，如果出现回路，则不用修建

![image-20190609170559866](/Users/chenyansong/Documents/note/images/discrete_math/image-20190609170559866.png)



算法的步骤：

![image-20190609170737541](/Users/chenyansong/Documents/note/images/discrete_math/image-20190609170737541.png)



## 普里姆算法

选定一个成本最小的顶点，然后以这个顶点为基础，考虑如果将水向外引，那么引向哪个节点的成本最低，则下一步指向哪个节点，然后以现有的顶点作为一个整体，考虑如果将水向外引，可以走哪条边(如果是回路，不考虑)，这样总体下来画的边的总权重肯定是最小的

![image-20190609171456434](/Users/chenyansong/Documents/note/images/discrete_math/image-20190609171456434.png)



![image-20190609171519773](/Users/chenyansong/Documents/note/images/discrete_math/image-20190609171519773.png)



下面的红圈就是将选出的顶点作为一个整体，在此基础上选择最小的权重

![image-20190609171559617](/Users/chenyansong/Documents/note/images/discrete_math/image-20190609171559617.png)



## 破圈法(反向删除)

在一个回路中，各条水道不必都修建，那么我们选择**回路**中权值最大的不去修建

![image-20190609172832378](/Users/chenyansong/Documents/note/images/discrete_math/image-20190609172832378.png)

![image-20190609173008966](/Users/chenyansong/Documents/note/images/discrete_math/image-20190609173008966.png)



不适合计算机实现



## 博鲁夫卡算法

算法的思想：

首先将每个顶点v都作为一个连通分支，之后每一步都选择最相近的两个连通分支合并，直到只剩下一个连通分支为止

![image-20190609173853277](/Users/chenyansong/Documents/note/images/discrete_math/image-20190609173853277.png)



# 最小瓶颈支撑树

设(G,W)是无向连通赋权图，G的所有支撑树中权值最大的边的权值最小的支撑树称为G的最小瓶颈支撑树



定理：

无向连通图赋权图的最小支撑树一定是最小瓶颈支撑树，但是最小瓶颈支撑树不一定是最小支撑树

如下有3个最小瓶颈支撑树，但是最小支撑树只有一个

![image-20190609174908010](/Users/chenyansong/Documents/note/images/discrete_math/image-20190609174908010.png)



# 斯坦纳树

![image-20190609175232280](/Users/chenyansong/Documents/note/images/discrete_math/image-20190609175232280.png)

当R=V时，斯坦纳树问题即是最小支撑树问题



如下图，a,b,c三个存在需要修建公路，需要找使得a,b,c存在连接起来的最小成本(d村不一定修建公路)

![image-20190609175703674](/Users/chenyansong/Documents/note/images/discrete_math/image-20190609175703674.png)