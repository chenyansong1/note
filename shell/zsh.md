[toc]



# 安装

## 安装zsh

## 安装oh-my-zsh

在安装oh-my-zsh之前，首先需要安装好`zsh`：

```text
yum install -y zsh
```

切换shell为zsh：

```text
chsh -s /bin/zsh
```

重启终端：

```text
# 查看当前shell
echo $SHELL
```

输出`/bin/zsh`表示成功

oh-my-zsh的安装非常简单，参考官网，执行如下命令即可：

```text
# curl
sh -c "$(curl -fsSL https://raw.githubusercontent.com/robbyrussell/oh-my-zsh/master/tools/install.sh)"

# wegt 
sh -c "$(wget https://raw.githubusercontent.com/robbyrussell/oh-my-zsh/master/tools/install.sh -O -)"
```

输出如下表示成功：

![img](https://pic1.zhimg.com/80/v2-fb93443e858f4fe0a7888a0e8eb8c83c_720w.jpg)

## 配置oh-my-zsh

和`bash`不同，`zsh`的配置文件是`~/.zshrc`，实际上`oh-my-zsh`的默认配置也够我们使用了，但是这样其真正的强大之处并不能得到很好的体现，因此我们可以继续看看对应的插件和主题功能

## oh my zsh使用

- **配置文件：**完成安装后，会自动在主目录下生成一个隐藏文件~/.zshrc，此即为配置文件，也是最重要的文件。
- **主题：**oh my zsh提供了海量的主题，具体可参考[themes](https://link.zhihu.com/?target=https%3A//github.com/ohmyzsh/ohmyzsh/wiki/Themes)。找到自己心仪的主题后，只需在配置文件中将ZSH_THEME="robbyrussell"引号中主题名字替换，保存文件后重启shell（或直接在命令行中输入source ~/.zshrc使配置生效）即可。
- **插件：**丰富的插件是zsh的灵魂。可用的插件列表见链接[plugins](https://link.zhihu.com/?target=https%3A//github.com/ohmyzsh/ohmyzsh/wiki/Plugins)，每个插件文件夹下都有其对应的README文档供参考。若要使用某插件，只需在配置文件中将插件名字加入到plugins=()字段中的括号中即可。注意需以空格或换行来分隔多个插件名，而不能用逗号。修改后保存文件重启shell（或直接在命令行中输入source ~/.zshrc使配置生效）即可。

# oh my zsh



# zsh的插件





