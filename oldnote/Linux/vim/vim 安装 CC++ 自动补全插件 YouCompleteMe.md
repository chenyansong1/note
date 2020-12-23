转自：https://dlonng.com/posts/vim-install-ycm



参考：https://vim80.readthedocs.io/zh/latest/plugin/vundle.html



因为要学习 ROS 所以电脑装了 Ubuntu 16.04，但 vim 还没有配置好，最近用 vim 写 C/C++ 发现没有代码补全，于是在 Github 上找到个补全插件 YCM（YouCompleteMe） ，这里记录下安装过程。

![img](https://camo.githubusercontent.com/1f3f922431d5363224b20e99467ff28b04e810e2/687474703a2f2f692e696d6775722e636f6d2f304f50346f6f642e676966)

官方的[教程](https://github.com/ycm-core/YouCompleteMe#linux-64-bit)比较多，我这里挑一些重点。

## 1、安装 Vundle

vim 的插件有很多，为了方便安装，这里安装一个叫做 Vundle 的工具来管理 vim 插件。

官方的教程在这里：[Vundle](https://github.com/VundleVim/Vundle.vim)，我精简下：

### 使用 Git 下载 Vundle

如果没有安装 Git，使用 apt 即可安装：

```
sudo apt install git
```

然后从 Github 上下载 Vundle：

```
git clone https://github.com/VundleVim/Vundle.vim.git ~/.vim/bundle/Vundle.vim
```

### 配置 Vundle

把下面的代码拷贝到 `~/.vimrc` 文件的顶部；

```
set nocompatible              " be iMproved, required
filetype off                  " required

" set the runtime path to include Vundle and initialize
set rtp+=~/.vim/bundle/Vundle.vim
call vundle#begin()
" alternatively, pass a path where Vundle should install plugins
"call vundle#begin('~/some/path/here')

" let Vundle manage Vundle, required
Plugin 'VundleVim/Vundle.vim'

" The following are examples of different formats supported.
" Keep Plugin commands between vundle#begin/end.
" plugin on GitHub repo
Plugin 'tpope/vim-fugitive'
" plugin from http://vim-scripts.org/vim/scripts.html
" Plugin 'L9'
" Git plugin not hosted on GitHub
Plugin 'git://git.wincent.com/command-t.git'
" git repos on your local machine (i.e. when working on your own plugin)
Plugin 'file:///home/gmarik/path/to/plugin'
" The sparkup vim script is in a subdirectory of this repo called vim.
" Pass the path to set the runtimepath properly.
Plugin 'rstacruz/sparkup', {'rtp': 'vim/'}
" Install L9 and avoid a Naming conflict if you've already installed a
" different version somewhere else.
" Plugin 'ascenator/L9', {'name': 'newL9'}

" All of your Plugins must be added before the following line
call vundle#end()            " required
filetype plugin indent on    " required
" To ignore plugin indent changes, instead use:
"filetype plugin on
"
" Brief help
" :PluginList       - lists configured plugins
" :PluginInstall    - installs plugins; append `!` to update or just :PluginUpdate
" :PluginSearch foo - searches for foo; append `!` to refresh local cache
" :PluginClean      - confirms removal of unused plugins; append `!` to auto-approve removal
"
" see :h vundle for more details or wiki for FAQ
" Put your non-Plugin stuff after this line
```

### 安装插件

单独启动 vim：

```
vim
```

键入：

```
：PluginInstall
```

即可自动安装插件了。

以后我们要是想安装其他 vim 插件，只需要两步：

1. 在 .vimrc 文件中添加插件的名称
2. 启动 vim，运行 :PluginInstall

从此使用 Vundle 安装 vim 插件变的非常地方便，下面就来用这种方法来安装自动补全插件 YCM 吧！

## 2、安装 YCM

按照上面的 2 步来，先在 `~/.vimrc` 文件的末尾添加 YCM 插件的完整名称:

```
xxx
xxx
...
Plugin 'Valloric/YouCompleteMe'
```

然后启动 vim，安装插件：

```
:PluginInstall
```

**安装速度取决于网速，耐心等待完成**，完成后按照 vim 的退出命令 `:q` 即可退出安装界面。

## 3、配置 YCM

使用 Vundle 安装完 YCM 后，还需要配置一下，先进入 YCM 插件的主目录：

```
cd .vim/bundle/YouCompleteMe
```

安装必要的工具：

```
// Ubuntu 16.04 and later
sudo apt install build-essential cmake python3-dev

// Ubuntu 14.04:
sudo apt install build-essential cmake3 python3-dev

// Fedora 27 and later:
sudo dnf install cmake gcc-c++ make python3-devel
```

YCM 可以安装多种语言的自动补全，我平常用 C/C++ 比较多，所以就安装 C-Family 的语言补全支持：

```
python3 install.py --clangd-completer
```

安装成功信息如下：

```
xxx
xxx
Done installing Clangd
```

如果是使用其他语言，官网也给出了安装命令，不过也要安装对应的开发语言环境：

```
C#：install Mono and run python3 install.py --clangd-completer
Go：install Go and run python3 install.py --go-completer
JS and TypeScript：install Node.js and rpm and run python3 install.py --ts-completer
Rust：python3 install.py --rust-completer
Java：install JDK8 and python3 install.py --java-completer
```

如果你想一劳永逸，也可以全部安装，不过也需要把每种语言的环境都安装好才可以：

```
python3 install.py --all
```

OK！开启你的 vim 代码补全之旅吧！



