16:00

* 语义
  * 自然语言
  * 形式描述



DSA：Data Structure + Algorithms



好算法：

* 正确
* 健壮性
* 可读性
* 效率：速度尽可能快，存储空间尽可能少



性能测度(measure)

* 运行时间+所需存储空间
* 如何度量？如何比较
  * 划分等价类

$$
T_A(n) = 用算法A求解某一问题规模为n的实例，所需的计算成本，讨论特定算法A(及其对应的问题)时，简记为T(n)
$$

$$
稳妥起见：T(n) = max{T(P)| |P|=n}
$$



特定问题+不同算法

* 同一问题通常由多个算法，如何评判其优劣?
* 实验统计是最直接的方法，但足以准确反应算法的正真效率？
* 不足够：
  * 不同的算法，可能更适用于不同的**规模**的输入
  * 不同的算法，可能更适应于不同**类型**的输入
  * 同一算法，可能由不同程序员，用不同程序语言，进不同编译器实现
  * 同一算法，可能实现并运行于不同的体系结构，操作系统

**算法的运行时间 等价于 算法需要执行的基本操作次数**



* 大O记号(big - O notation)
  $
  T(n) = O(f(n))
  $
  $
  iff \quad \exist c > 0, 当n>>2后，有T(n)<c*f(n)
  $



例如：
$$
\sqrt(5n*[3n*(n+2)+4]+6) <\sqrt(5n*[6n^2+4]+6)<\sqrt(35n^3+6)<6*n^(1.5)=O(n^(1.5))
$$

* 与T(n)相比，f(n)更为简洁，但依然反映前者的增长趋势
  * 常系数可忽略:$O(f(n)) = O(c*f(n)) ; 其中常系数c可以忽略$
  * 低次项可忽略：$O(n^a + n^b = O(n^a)), a>b>0$

![image-20190414185449244](/Users/chenyansong/Documents/note/images/data_structure/ot.png)



![image-20190414190531350](/Users/chenyansong/Documents/note/images/data_structure/ot2.png)



不含转向(循环，调用，递归)等，必顺序执行，即$O(1)$



* 常数的复杂度为$O(1)$

* 对数的复杂度

  ![image-20190414191634505](/Users/chenyansong/Documents/note/images/data_structure/ot3.png)![image-20190414192143449](/Users/chenyansong/Documents/note/images/data_structure/ot4.png)

  他是无限接近于$O(1)$



* 多项式的复杂度

  ![image-20190414193122232](/Users/chenyansong/Documents/note/images/data_structure/ot5.png)

  ![image-20190414193319237](/Users/chenyansong/Documents/note/images/data_structure/ot6.png)

* 指数复杂度

  ![image-20190414194028297](/Users/chenyansong/Documents/note/images/data_structure/ot7.png)



* 增长速度	

  ![image-20190414195022097](/Users/chenyansong/Documents/note/images/data_structure/ot8.png)

  ![image-20190414195204408](/Users/chenyansong/Documents/note/images/data_structure/ot9.png)