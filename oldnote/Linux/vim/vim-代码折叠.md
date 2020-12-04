[toc]

转自：https://www.cnblogs.com/abeen/archive/2010/08/06/1794197.html

**VIM 代码折叠**
VIM代码折叠方式可以用"foldmethod"选项来设置，如: set foldmethod=indent

有6种方式来折叠代码

\1. manual //手工定义折叠
\2. indent //用缩进表示折叠
\3. expr　 //用表达式来定义折叠
\4. syntax //用语法高亮来定义折叠
\5. diff  //对没有更改的文本进行折叠
\6. marker //用标志折叠

 

vim 配置文件分为**全局配置文件**和**用户配置文件**

\1. 全局配置文件查找，在.vim 中输入:echo $VIM,一般为：/usr/share/vim/vimrc
\2. 用户配置文件在用户主目录下，如：/home/abeen/.vimrc

在vimrc文件中设置折叠方式：如set foldmethod=indent

 

选定折叠方式后，就可以使用折叠命令进行折叠操作了．常用的折叠方式为indent和marker．

**indent折叠命令**

操作：za，打开或关闭当前折叠；zM，关闭所有折叠；zR，打开所有折叠

indent方式，vim会自动利用缩进进行折叠，我们可以使用现成的折叠成果．
我们可以在(括号)折叠处输入以下命令：

zc 折叠
zC 对所在范围内所有嵌套的折叠点进行折叠
zo 展开折叠
zO 对所在范围内所有嵌套的折叠点展开
[z 到当前打开的折叠的开始处。
]z 到当前打开的折叠的末尾处。
zj 向下移动。到达下一个折叠的开始处。关闭的折叠也被计入。
zk 向上移动到前一折叠的结束处。关闭的折叠也被计入。

 

**marker折叠命令**
marker折叠方式时，我们需要用标计来标识代码的折叠，系统默认是{{{和}}}
折叠命令

zf　创建折叠，比如在marker方式下：
    zf56G，创建从当前行起到56行的代码折叠；
    10zf或10zf+或zf10↓，创建从当前行起到后10行的代码折叠。
    10zf-或zf10↑，创建从当前行起到之前10行的代码折叠。
    在括号处zf%，创建从当前行起到对应的匹配的括号上去（（），{}，[]，<>等）。
zd 删除 (delete) 在光标下的折叠。仅当 'foldmethod' 设为 "manual" 或 "marker" 时有效。
zD 循环删除 (Delete) 光标下的折叠，即嵌套删除折叠。仅当 'foldmethod' 设为 "manual" 或 "marker" 时有效。
zE 除去 (Eliminate) 窗口里“所有”的折叠。仅当 'foldmethod' 设为 "manual" 或 "marker" 时有效。

 

**常用的用法---indent折叠命令**

```shell
# vim /home/abeen/.vimrc

set foldmethod=indent

set foldlevel=1 #指定级别折叠缩进
set foldlevelstart=99 #避免启动编辑器代码自动折叠


#然后indent的使用方法见上面的说明

```





折叠后效果图

![](https://pic002.cnblogs.com/img/abeen/201008/2010080620260412.jpg)



参考：

https://www.cnblogs.com/abeen/archive/2010/08/06/1794197.html

https://www.yupengsir.com/topic/content?i=140

https://www.cnblogs.com/huanlei/archive/2012/04/03/2430633.html



