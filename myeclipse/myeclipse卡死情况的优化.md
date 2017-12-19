
大多数javaman在使用myeclipse的过程中都遇到过代码提示卡死，假死机的状况。

进行下面的优化后，完全可以解决此问题。

# 1.取消自动validation
　　 validation有一堆，什么xml、jsp、jsf、js等等，我们没有必要全部都往自动校验一下，只是需要的时候才会手工校验一下！
　　取消方法：
　　 windows–>perferences–>myeclipse–>validation
　　 除开Manual下面的复选框全部选中之外，其他全部不选
　　 手工验证方法：
　　 在要验证的文件上，单击鼠标右键–>myeclipse–>run validation
　　 
# 2.取消Eclipse拼写检查
　　 1、拼写检查会给我们带来不少的麻烦，我们的方法命名都会是单词的缩写，他也会提示有错，所以最好往掉，没有多大的用处
　　windows–>perferences–>general–>validation->editors->Text Editors->spelling


# 3.取消myeclipse的启动项
　　 myeclipse会有很多的启动项，而其中很多我们都用不着，或者只用一两个，取消前面不用的就可以
　　windows–>perferences–>general–>startup and shutdown
　　 
# 4.更改jsp默认打开的方式
　　 安装了myeclipse后，编辑jsp页面，会打开他的编辑页面，同时也有预览页面，速度很慢，不适合开发。所以更改之
　　windows–>perferences–>general–>editors->file associations
　　在下方选择一种编辑器，然后点击左边的default按钮
　　 
# 5.更改代码提示快捷键
　　现在的代码提示快捷键，默以为ctrl+space，而我们输进法切换也是，所以会有冲突。谁叫myeclipse是外国人做的呢。。根本不需要切换输进法.
　　windows–>perferences–>general–>Keys
　　更改 content assist 为 alt+/
　　同时由于alt+/已经被word completion占用，所以得同时修改word completion的快捷键值
　　
# 6.封闭Quick update 功能
　　myeclipse的quick update很烦人，使用时封闭。preference->myeclipse->community essentials,勾掉search for new features at startup
　　