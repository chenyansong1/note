[TOC]



# vim目录结构说明

```shell
#如果没有这些目录就自己创建
~/.vim/plugin
~/.vim/doc
~/.vim/syntax
~/.vimrc   #file

```

![1583980359992](C:\Users\landun\AppData\Roaming\Typora\typora-user-images\1583980359992.png)

# vim编程常用命令

| 快捷键 | description                              |
| ------ | ---------------------------------------- |
| %      | 跳转到配对的括号去，多按几次可以来回切换 |
| [[     |                                          |
|        |                                          |
|        |                                          |
|        |                                          |
|        |                                          |
|        |                                          |



# 需要安装的依赖

## ctags install

```shell
#download： https://sourceforge.net/projects/ctags/files/ctags/5.6/

tar zxvf ctags-5.8.tar.gz 
cd ctags-5.8
#一定要执行，网上哪些没有这步，后面make报错
./configure 
make
make install
#查看是否安装成功
which ctags
```

让后进入到源码目录中（如果源码是多层，就去最上层的目录）,执行`ctags -R`，此时就会在该目录下生成一个车tags目录

## 快捷键

```shell
#光标自动跳转到setmouse()函数的定义处
<C-]>

#跳回到上一次的位置
<C-t>

```

