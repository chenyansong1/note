[toc]



# 下载及安装

download：https://manjaro.org/downloads/official/xfce/

重启之后遇到的一个问题：因为是虚拟机安装，所以每次默认的boot都是iso那个镜像的boot，这样每次都是进入镜像的boot的安装界面，其实在启动的时候需要选择`虚拟机的硬盘的boot启动`





# 配置软件源及更新

源的问题

https://zhuanlan.zhihu.com/p/334808120

http://mirrors.ustc.edu.cn/help/manjaro.html

由于manjaro默认的软件源在国外，使用默认的软件源会导致安装和更新程序很慢。因此第一件事就是将软件源替换为国内镜像源。

在Terminal中执行命令

```text
sudo pacman-mirrors -i -c China -m rank
```

测试国内各个镜像源的速度。

测试国内各个镜像源的速度。

![img](https://pic1.zhimg.com/80/v2-76bbdcb3c6369fa2e95c68b980e85984_1440w.jpg)

选择一个延迟最低的镜像源即可。



选择一个延迟最低的镜像源即可。

## 更新软件库

执行命令，更新本机中软件库缓存：

```text
sudo pacman -Syy
```

## 更新系统

manjaro系统是滚动更新的，所以使用镜像安装出来的系统还需要更新到最新。执行以下命令以更新系统和软件：

```text
sudo pacman -Syyu
```

建议经常执行该命令，以保持系统一直处于最新的状态。

> 此时可能会报错，如下：



ArchLinux近期更新依赖问题解决【2020.1.13】
更新时出现(xxx代表某个包)

```shell
错误：无法准备事务处理 (无法满足依赖关系)
:: 安装 xxx 破坏依赖 'xxx' （xxx 需要）
:: 安装 xxx 破坏依赖 'xxx' （xxx 需要）
```

有段时间没用Arch了，今天打开执行pacman -Syu 更新系统出现该问题，看起来应该是某些软件包新旧版本依赖的问题，此时要手动解决

此时执行如下命令即可解决(两个xxx指的是上面对应的“**xxx需要**”)

```shell
sudo pacman -Rdd xxx xxx && sudo pacman -Syu
```

参见：https://blog.csdn.net/qq_39828850/article/details/103963706





# 分区[可选]

https://linux.cn/article-11487-1.html





# vim 报错

会报glibc的version不对，解决的方式

````shell
sudo pacman -Syyu  #整个更新
````



# win manger -- i3

```shell
sudo pacman -S i3-wm
```



# 终端-alacritty

```shell
sudo pacman -S alacritty
```





# shell ---zsh

https://zhuanlan.zhihu.com/p/19556676



# 安装yay

> yay 是下一个最好的 AUR 助手。它使用 Go 语言写成，宗旨是提供最少化用户输入的 pacman 界面、yaourt 式的搜索，而几乎没有任何依赖软件。yay 的特性：yay 提供 AUR 表格补全，并且从 ABS 或 AUR 下载 PKGBUILD支持收窄搜索，并且不需要引用 PKGBUILD 源yay 的二进制文件除了 pacman 以外别无依赖

你可以从 git 克隆并编译安装。

```text
sudo pacman -S --needed base-devel git

git clone https://aur.archlinux.org/yay.git

cd yay

makepkg -si

```

使用 yay：

搜索：

```text
yay -Ss <package-name>
```

安装：

```text
yay -S <package-name>
```


# 拼音
https://blog.ruo-chen.wang/2020/05/install-fcitx5.html
https://www.youtube.com/watch?v=C2mWxeiq9Wo
输入法的切换：ctrl+space ,不是ctrl+shift

