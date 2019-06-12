[TOC]

# 根树



* 假设T是一棵有向树，若T恰有一个入度为0的顶点v，其余顶点的入度皆为1，则称T为根树，v称作T的根

* 根树中出度为0的顶点称为叶子，出度大于0的顶点称为分枝点
* 只有一个孤立顶点的平凡树叶认为是根树



![image-20190612194034960](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612194034960.png)

![image-20190612194059963](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612194059963.png)



> 定理1

在根树T中，从根到任一其他顶点都存在唯一的简单道路

> 定理2

如果有向图T中存在顶点v，使得从v到T的任一其他顶点都存在唯一的简单道路，而且不存在从v到v的简单回路，则T是一棵以v为根的根树



![image-20190612194600799](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612194600799.png)



* 在根树中，有根到顶点v的道路长度称作v的层数
* 所有顶点的层数的最大值称为根树的高度



* 在根树T中，若每个分枝点的出度最多为m，则称T为m元树或m叉树
* 如果每个分枝点的出度都等于m，则称T为完全m叉树
* 进一步，若T的全部叶子顶点的层数都相同，则称T为正则m叉树

下图中左图，右图都是二叉树，而右图又是完全二叉树，而左图不是，更进一步的，右图还是正则二叉树(所有叶子节点的层数相同)

![image-20190612195505643](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612195505643.png)



* 如果在根树T中规定了分枝点的孩子顶点之间的次序，则称T为有序树

* 若m叉树T是有序的，则称T为m叉有序树

* 对于二叉有序树而言

  * 一个分枝点v的第一个孩子顶点也称作左孩子，第二个孩子顶点称作右孩子
  * 以v的左孩子为根的子树称作v的左子树
  * 以v的右孩子为根的子树称作v的右子树

  ![image-20190612200255519](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612200255519.png)

* 如果m叉有序树T的每个顶点的孩子顶点都被规定了位置，则称T是m叉位置树

* 在二叉位置树中，一个分枝点可能只有左孩子而没有右孩子，也可能只有右孩子而没有左孩子

  ![image-20190612200532664](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612200532664.png)



> 定理

若T是完全m叉树，其叶子数为t，分枝点数为i，则(m-1)*i=t-1

证明：采用握手定理(所有顶点的出度=入度)

![image-20190612201521166](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612201521166.png)

![image-20190612201632663](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612201632663.png)



# 双链树

二叉树的应用最为广泛，事实上任何一棵有序树都可以转换为一棵二叉位置树，其方法如下：

* 将每个顶点v的第一个孩子作为他的左孩子，将v的第一个兄弟作为他的右孩子(也称之为：左第一孩子，右下一兄弟表示法)

  ![image-20190612202154659](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612202154659.png)

![image-20190612202415978](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612202415978.png)

如下森林由三棵二叉树组成

![image-20190612202524594](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612202524594.png)

转换为二叉位置树，如下：

![image-20190612202555940](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612202555940.png)

最后再组成一棵二叉位置树

![image-20190612202640461](/Users/chenyansong/Documents/note/images/discrete_math/image-20190612202640461.png)