[toc]



转自：https://zhuanlan.zhihu.com/p/64533566

参考：https://www.jianshu.com/p/f803bbba0ba5

https://www.jianshu.com/p/849d6b21e02e

## vimium简介

Vimium是一款 Chrome 插件，它继承了 Vim 中的常用操作，让我们在使用 Chrome 的过程中，无论是浏览网页、切换标签、搜索以至于任何其它操作，**全都可以只通过键盘完成**

[Vimium 安装地址](https://chrome.google.com/webstore/detail/vimium/dbepggeogbaibhgnhhndojpepiihcmeb)

## 快捷键大全

对于ctrl + x，meta + x和alt + x，修饰键分别指定为<c-x>，<m-x>和<a-x>。对于shift + x和ctrl-shift-x，只需输入 X 和<c-X>

## 默认快捷键列表

- 通过**<shift-?>** 随时展示快捷键列表

![img](https://pic2.zhimg.com/80/v2-d3fec21affb0169e16b7fb6c7cd17f8d_720w.jpg)

## 当前页面操作

在浏览器当前页面的所有操作

```text
?       显示帮助对话框以获取所有可用键的列表
h       向左滚动一点
j       向下滚动一点
k       向上滚动一点
l       向右滚动一点
gg      滚动到页面顶部
G       滚动到页面底部
d       向下滚动半页
u       向上滚动半页
f       打开元素定位器，是在当前标签页打开
F       打开元素定位器，是在新标签页打开
r       刷新
gs      查看源码
i       进入插入模式 - 在您按Esc退出之前，将忽略所有命令
yy      将当前网址复制到剪贴板
yf      将链接URL复制到剪贴板
gf      循环到下一帧(尤其在选择网页内置视频的时候很管用)
gF      聚焦主/顶框架
```

### 新页面操作

```text
o   从URL、书签、历史记录中搜索地址，回车打开
O   从URL、书签、历史记录中搜索地址，回车在新标签页中打开
b   仅从书签搜索地址，回车打开
B   仅从书签搜索地址，回车新标签页中打开
T   搜索当前浏览器的所有标签
```

### 使用搜索

```text
/       进入查找模式 - 输入您的搜索查询并按Enter键进行搜索，或按Esc键取消
n       查找下一个匹配项
N       查找上一个匹配项
```

### 浏览历史记录

```text
H       回到历史，也就是回到前一页
L       在历史上前进，也就是回到后一页
```

### 标签操作

```text
J, gT   跳到左标签
K, gt   跳到右标签
g0      跳转到第一个标签(根据不同的数字跳到第几个标签)
g$      跳转到最后一个标签
^       回到上一个访问的标签
t       创建一个新的标签
yt      复制当前页面，在新标签页打开
x       关闭当前标签
X       恢复关闭的上一个标签
p       在当前标签页打开剪切板中的URL，如不是URL则默认引擎搜索
P       在新标签页打开剪切板中的URL，如不是URL则默认引擎搜索
T       在当前打开的标签中搜索
W       将当前标签移动到新窗口
<a-p>   pin/unpin current tab
```

### 标记（锚点）

```text
ma      设置本地标记 a
mA      设置全局标记 A 
`a      跳转到本地标记 a
`A      跳转到q全局标记 a
``      跳回到跳转之前的位置 (也就是说，在执行gg，G，n，N，或/ a 之前的位置）
```

### 其他高级浏览命令

```text
<<      当前标签页向左移动  
>>      当前标签页向右移动  
<a-f>   在新标签中打开多个链接
gi      聚焦页面上的第一个（或第n个）文本输入框
gu      跳转到URL层次的父类(xxx.com/yyy/zzz 跳转到 xxx.com/yyy)
gU      转到URL层次结构的根目录(也就是 xxx.com)
ge      编辑当前URL
gE      编辑当前URL并在新选项卡中打开
zH      向左滚动
zL      向右滚动
v       进入预览模式;使用p / P粘贴，然后使用y来拷贝 （需要按下两次v：第一次v来确定是block模式，第二次v是确定可以选择连续的块）
V       enter visual line mode
<a-m>   开/关静音 
<a-p>   固定标签栏 
```

## 预览模式（visual mode）

预览模式跟 vim 很类似

```text
先用 / 定位，找到想要选择的字符
    再按 v ,进入模式
    然后使用
        j：向下一行
        k：向上一行
        h：向左一个字符或标点（数字+h，可以移动多个字符）
        l：向右一个字符或标点（数字+l，可以移动多个字符）
        w：下一个标点符号后位置，包括看不见的换行符
        e：下一个标点符号前位置
        b：取消选中上一个字符，字符和标点算一个字符
```

##  vimium绝招

只用敲三下，打开当前页面上任意一个链接
任意一个页面上，哪所有再多链接，你也不用鼠标，最多只需要敲三个键，你就可以迅速打开任意一个链接

- 你只需要按一下「f」，然后当前页面会显示所有可点击的元素，vimium 会生成一个对应的快捷键给这些链接。比如我要进入【话题】，我只需要输入 「JA」就完成了。vimium 会自动跳转，你只敲了三个键就打开了【话题】。

![img](https://pic2.zhimg.com/80/v2-20963e10b8ce491495bea1ea40754461_720w.jpg)

## 打开新页面 

- **复制一段链接**：经常在网页上看到一段链接文字，但却是不可点的。原来你需要先复制，然后新建标签页，再粘贴，敲回车后才能打开。现在呢？你只需要把要打开的链接复制一下，直接按「p」或「P」就可以打开了，小写的 p 是在当前标签页打开，大写的 P 则新建标签页打开。
- **从收藏夹、历史记录打开**：是不是之前看过什么网页，现在又想看了，还需要再打开历史记录找？或者想打开收藏夹里的某个链接？现在，直接按下「o」，输入对应的关键字后，会一起搜索你的历史记录和收藏夹，如果你输的是一个网址，回车还能直接打开。

![img](https://pic4.zhimg.com/80/v2-69b5fcc3acb4061fe67d842d10b9c653_720w.jpg)

## 显示当前所有的标签页，并快速切换

- 有时候在查找信息、翻阅资料时，经常会一口气打开几十个网站，东西一多，Chrome 会自动将每个标签页的宽度缩小，几乎就看不到它们的标题了。用了 Vimium，你可以按一下大写的「T」，就可以显示当前打开的所有标签页，并支持快捷搜索和跳转。

![img](https://pic3.zhimg.com/80/v2-c2ed0a6b6059ba7a1e58e0fa58ce4262_720w.jpg)

## 自定义搜索引擎

配置自定义搜索引擎，通过快捷键 o/O 调起搜索框，输入搜索引擎简写，再输入空格，再输入搜索词回车，则会调用对应的搜索引擎进行搜索

```text
搜索引擎简写 搜索引擎地址 搜索引擎名字
例如：BZ https://search.bilibili.com/all?keyword=%s b站
```

- **唤醒搜索引擎**

![img](https://pic4.zhimg.com/80/v2-845b4b4c27f8b5c18a8c94f833d0345b_720w.jpg)

- **如何进行搜索**

![img](https://pic2.zhimg.com/80/v2-e64308f73fd56f00d6997db590ccd78d_720w.png)

- **自定义搜索引擎配置**

![img](https://pic4.zhimg.com/80/v2-c55f9a0537f8ef13c2521ff678899c17_720w.jpg)

- **我常用的搜索引擎配置**

```text
w: https://www.wikipedia.org/w/index.php?title=Special:Search&search=%s Wikipedia

# More examples.
#
# (Vimium supports search completion Wikipedia, as
# above, and for these.)
#
g: https://www.google.com/search?q=%s Google
G: https://www.google.com/search?q=%s Google
zh: https://www.zhihu.com/search?type=content&q=%s 知乎
ZH: https://www.zhihu.com/search?type=content&q=%s 知乎
tb https://s.taobao.com/search?q=%s 淘宝
TB https://s.taobao.com/search?q=%s 淘宝
jd https://search.jd.com/Search?keyword=%s 京东
JD https://search.jd.com/Search?keyword=%s 京东
bd: https://www.baidu.com/s?wd=%s 百度
BD: https://www.baidu.com/s?wd=%s 百度
bz https://search.bilibili.com/all?keyword=%s b站
BZ https://search.bilibili.com/all?keyword=%s b站
az: https://www.amazon.com/s/?field-keywords=%s Amazon
AZ: https://www.amazon.com/s/?field-keywords=%s Amazon
aqy https://so.iqiyi.com/so/q_%s 爱奇艺
AQY https://so.iqiyi.com/so/q_%s 爱奇艺
tm https://list.tmall.com/search_product.htm?q=%s 天猫
TM https://list.tmall.com/search_product.htm?q=%s 天猫
yk https://so.youku.com/search_video/q_%s 优酷
YK https://so.youku.com/search_video/q_%s 优酷
db https://www.douban.com/search?q=%s 豆瓣
DB https://www.douban.com/search?q=%s 豆瓣
y: https://www.youtube.com/results?search_query=%s Youtube
Y: https://www.youtube.com/results?search_query=%s Youtube
# l: https://www.google.com/search?q=%s&btnI I'm feeling lucky...
# gm: https://www.google.com/maps?q=%s Google maps
# b: https://www.bing.com/search?q=%s Bing
# d: https://duckduckgo.com/?q=%s DuckDuckGo
# qw: https://www.qwant.com/?q=%s Qwant
```