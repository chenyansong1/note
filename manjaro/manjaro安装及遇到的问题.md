



# 安装



重启之后遇到的一个问题：因为是虚拟机安装，所以每次默认的boot都是iso那个镜像的boot，这样每次都是进入镜像的boot的安装界面，其实在启动的时候需要选择`虚拟机的硬盘的boot启动`







# 配置软件源

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





# 分区

https://linux.cn/article-11487-1.html





# vim 报错

会报glibc的version不对，解决的方式

````shell
sudo pacman -Syyu  #整个更新
````



# shell ---zsh

https://zhuanlan.zhihu.com/p/19556676

