[TOC]

# 配色功能

* vim版本：vim74

* 客户端：

  * 版本：SecureCRT 8.5

  * 配色方案

    ![](E:\git-workspace\note\images\vim\1571629749501.png)

    ![](E:\git-workspace\note\images\vim\1571627410970.png)

  **使用默认的配色 ：default**

# vim插件，及快捷键

```c
花了很长时间整理的，感觉用起来很方便，共享一下。

我的vim配置主要有以下优点：

1.按F5可以直接编译并执行C、C++、java代码以及执行shell脚本，按“F8”可进行C、C++代码的调试

2.自动插入文件头 ，新建C、C++源文件时自动插入表头：包括文件名、作者、联系方式、建立时间等，读者可根据需求自行更改

3.映射“Ctrl + A”为全选并复制快捷键，方便复制代码

4.按“F2”可以直接消除代码中的空行

5.“F3”可列出当前目录文件，打开树状文件目录

6. 支持鼠标选择、方向键移动

7. 代码高亮，自动缩进，显示行号，显示状态行

8.按“Ctrl + P”可自动补全

9.[]、{}、()、""、' '等都自动补全

10.其他功能读者可以研究以下文件

 vim本来就是很强大，很方便的编辑器，加上我的代码后肯定会如虎添翼，或许读者使用其他编程语言，可以根据自己的需要进行修改，配置文件里面已经加上注释。
 
 
 
```



# .vimrc配置

```shell
"自动补全
:inoremap ( ()<ESC>i
:inoremap ) <c-r>=ClosePair(')')<CR>
:inoremap { {<CR>}<ESC>O
:inoremap } <c-r>=ClosePair('}')<CR>
:inoremap [ []<ESC>i
:inoremap ] <c-r>=ClosePair(']')<CR>
:inoremap " ""<ESC>i
:inoremap ' ''<ESC>i
function! ClosePair(char)
    if getline('.')[col('.') - 1] == a:char
        return "\<Right>"
    else
        return a:char
    endif
endfunction
filetype plugin indent on 


" 自动缩进
set autoindent
set cindent
" Tab键的宽度
set tabstop=4
" 统一缩进为4
set softtabstop=4
set shiftwidth=4
" 不要用空格代替制表符
set noexpandtab
" 在行和段开始处使用制表符
set smarttab






map <F5> :call CompileRunGcc()<CR>
func! CompileRunGcc()
    exec "w" 
    if &filetype == 'c' 
        exec "!g++ % -o %<"
        exec "! ./%<"
    elseif &filetype == 'cpp'
        exec "!g++ % -o %<"
        exec "! ./%<"
    elseif &filetype == 'java' 
        exec "!javac %" 
        exec "!java %<"
    elseif &filetype == 'sh'
        :!./%
    endif
endfunc
"C,C++的调试
map <F8> :call Rungdb()<CR>
func! Rungdb()
    exec "w" 
    exec "!g++ % -g -o %<"
    exec "!gdb ./%<"
endfunc

" 设置当文件被改动时自动载入
set autoread
" quickfix模式
autocmd FileType c,cpp map <buffer> <leader><space> :w<cr>:make<cr>




```





taglist

```shell
#参见：https://www.cnblogs.com/willsonli/p/6559705.html


#tags, tagslist的使用
#https://blog.csdn.net/daniel_ustc/article/details/8299096



#taglist的使用
#https://www.cnblogs.com/luosongchao/p/3163468.html

#vim打造教程
#https://blog.csdn.net/wooin/article/details/1858917

#对于我们需要查看源码的时候，在源码的工程目录下，使用 tags -R  对当前源码生成一份 tags
#如果修改了源码，需要重新生成一份tags，不然修改的部分不会被tag到
#tag每次都要去对应的目录下重新生成，不然，找不到

#quickfix
https://vimjc.com/vim-quickfix.html

#缓冲区的概念
https://vimjc.com/vim-file-buffer.html


#自动补全快捷键
#https://blog.csdn.net/henpat/article/details/42077561


```



[转]http://blog.csdn.net/wooin/archive/2007/10/31/1858917.aspx
此时有一些快捷键可以用:

| Ctrl+P | 向前切换成员                             |
| ------ | ---------------------------------------- |
| Ctrl+N | 向后切换成员                             |
| Ctrl+E | 表示退出下拉窗口, 并退回到原来录入的文字 |
| Ctrl+Y | 表示退出下拉窗口, 并接受当前选项         |


如果你增加了一些成员变量, 全能补全还不能马上将新成员补全, 需要你重新生成一下tags文件, 但是你不用重启vim, 只是重新生成一下tags文件就行了, 这时全能补全已经可以自动补全了, 还真够"全能"吧.



```shell
#在同一个文件中，快速定位到函数定义部分快捷方式为：gd
```



