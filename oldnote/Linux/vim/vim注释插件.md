[toc]



# vim自带的注释功能



先介绍下vim自带的注释功能， 用熟悉的话也是非常强大的：

通过 v 进入视图模式，然后 CTRL + v ,   进入到块选择模式， 选中要注释的行后， 输入大写 i， 即I， 进入编辑模式， 此时输入要作为注释的字符 ，比如// 或者 #， 然后按两次 ESC， 即可实现注释。

同理，如果要去掉注释， 也是通过v进入视图模式，然后 CTRL + v ,   进入到块选择模式， 选中要去掉注释的列后，直接按 d ， 即可去掉注释， 注意， 要去掉//的注释，比如选中两列。



# vim的注释插件NerdCommenter

1. 下载vim的插件

   ```shell
   curl -fLo ~/.vim/plugin/NERD_Commenter.vim --create-dirs https://raw.githubusercontent.com/preservim/nerdcommenter/master/plugin/NERD_commenter.vim
   
   
   [cys@localhost c_workspace]$ ll ~/.vim/plugin/
   -rw-rw-r--. 1 cys cys 121498 3月  11 17:15 NERD_commenter.vim
   -rw-r--r--. 1 cys cys 152242 2月  26 2013 taglist.vim
   ```

   

2. 然后修改 ~/.vimrc 文件， 添加如下几行：默认的

   ```shell
   " 默认 leader 键为 \ ,如果需要可以修改如下：如下的改为： ,
   
   " Set mapleader
   " let mapleader = ","
   ```

3. 使用

   ```shell
   具体使用如下：
   
   输入 “  ,cc  ”  :   注释当前行
   
   输入 “  ,cm  ”  :   对被选区域用一对注释符注释
   
   输入 “  ,cu  ”  :   取消注释
   
   针对C和C++文件 ：可以切换注释方法， 默认为/* */ ， 可以通过
   
   输入 “  ,ca  ”  :   切换注释方法， /* */  和  //  互切
   
   
   以上即为该插件的使用方法， 更多功能请查看帮助文档！！
   ```

   





