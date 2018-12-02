[TOC]

转：https://www.jianshu.com/p/a0aa94ef8ab2



### 1 基本语法

#### 1.1 呈现位置

- 正文(inline)中的LaTeX公式用`$...$`定义

- 语句为`$\sum_{i=0}^N\int_{a}^{b}g(t,i)\text{d}t$` 

- 显示在当前行内$\sum_{i=0}^N\int_{a}^{b}g(t,i)\text{d}t$

- 单独显示(display)的LaTeX公式用`$$...$$`定义，此时公式居中并放大显示

- 语句为$$\sum_{i=0}N\int_{a}{b}g(t,i)\text{d}t$$

- 

- 下列描述语句中若非特别指出均省略`$...$` 

### 2 希腊字母

| 显示 | 命令     | 显示 | 命令   |
| ---- | -------- | ---- | ------ |
| α    | \alpha   | β    | \beta  |
| γ    | \gamma   | δ    | \delta |
| ε    | \epsilon | ζ    | \zeta  |
| η    | \eta     | θ    | \theta |
| ι    | \iota    | κ    | \kappa |
| λ    | \lambda  | μ    | \mu    |
| ν    | \nu      | ξ    | \xi    |
| π    | \pi      | ρ    | \rho   |
| σ    | \sigma   | τ    | \tau   |
| υ    | \upsilon | φ    | \phi   |
| χ    | \chi     | ψ    | \psi   |
| ω    | \omega   |      |        |

- 若需要大写希腊字母，将命令首字母大写即可。
   \Gamma呈现为

  ![image-20181202085800548](/Users/chenyansong/Documents/note/images/math/markdown1.png)

- 若需要斜体希腊字母，将命令前加上var前缀即可。

  ![image-20181202085842534](/Users/chenyansong/Documents/note/images/math/markdown3.png)


### 3 字母修饰

##### 3.1  上下标

- 上标：`^` 

- 下标：`_` 

  ![image-20181202085940384](/Users/chenyansong/Documents/note/images/math/markdown4.png)

##### 3.2 矢量



![image-20181202090002778](/Users/chenyansong/Documents/note/images/math/markdown5.png)

##### 3.3 字体



![image-20181202090028767](/Users/chenyansong/Documents/note/images/math/markdown6.png)



##### 3.4 分组



![image-20181202090047785](/Users/chenyansong/Documents/note/images/math/markdown7.png)



##### 3.5 括号



![image-20181202090133063](/Users/chenyansong/Documents/note/images/math/markdown8.png)

##### 3.6 求和、极限与积分



![image-20181202090216880](/Users/chenyansong/Documents/note/images/math/markdown9.png)



##### 3.7 分式与根式



![image-20181202090237259](/Users/chenyansong/Documents/note/images/math/markdown10.png)

##### 3.8 特殊函数



![image-20181202090302337](/Users/chenyansong/Documents/note/images/math/markdown11.png)

##### 3.9 特殊符号



![image-20181202090325180](/Users/chenyansong/Documents/note/images/math/markdown12.png)

##### 3.10 空格



![image-20181202090350817](/Users/chenyansong/Documents/note/images/math/markdown13.png)

### 4 矩阵

##### 4.1 基本语法

起始标记`\begin{matrix}`，结束标记`\end{matrix}`
 每一行末尾标记`\\\`，行间元素之间以`&`分隔
 举例:

```
$$\begin{matrix}
1&0&0\\
0&1&0\\
0&0&1\\
\end{matrix}$$
```

呈现为：



![image-20181202090434219](/Users/chenyansong/Documents/note/images/math/markdown14.png)

##### 4.2 矩阵边框

- 在起始、结束标记处用下列词替换 `matrix` 
-  `pmatrix` ：小括号边框
-  `bmatrix` ：中括号边框
-  `Bmatrix` ：大括号边框
-  `vmatrix` ：单竖线边框
-  `Vmatrix` ：双竖线边框

##### 4.3 省略元素

- 横省略号：`\cdots` 
- 竖省略号：`\vdots` 
- 斜省略号：`\ddots`
   举例

```
$$\begin{bmatrix}
{a_{11}}&{a_{12}}&{\cdots}&{a_{1n}}\\
{a_{21}}&{a_{22}}&{\cdots}&{a_{2n}}\\
{\vdots}&{\vdots}&{\ddots}&{\vdots}\\
{a_{m1}}&{a_{m2}}&{\cdots}&{a_{mn}}\\
\end{bmatrix}$$
```

呈现为：



![image-20181202090510978](/Users/chenyansong/Documents/note/images/math/markdown15.png)

##### 4.4 阵列



![image-20181202090532976](/Users/chenyansong/Documents/note/images/math/markdown16.png)

举例

```
$$\begin{array}{c|lll}
{↓}&{a}&{b}&{c}\\
\hline
{R_1}&{c}&{b}&{a}\\
{R_2}&{b}&{c}&{c}\\
\end{array}$$
```

呈现为



![image-20181202090553634](/Users/chenyansong/Documents/note/images/math/markdown17.png)



##### 4.5 方程组

- 需要cases环境：起始、结束处以{cases}声明

举例

```
$$\begin{cases}
a_1x+b_1y+c_1z=d_1\\
a_2x+b_2y+c_2z=d_2\\
a_3x+b_3y+c_3z=d_3\\
\end{cases}
$$
```

呈现为



![image-20181202090620040](/Users/chenyansong/Documents/note/images/math/markdown18.png)



### 5 参考文献

[MathJax tutorial](https://link.jianshu.com?t=http://meta.math.stackexchange.com/questions/5020/mathjax-basic-tutorial-and-quick-reference)

