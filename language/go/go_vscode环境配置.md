转自：https://zhuanlan.zhihu.com/p/156803344


[toc]

## Go安装及基本配置

### Window

#### Go 安装

Go 的 安装比较简单，在[https://gomirrors.org/](https://link.zhihu.com/?target=https%3A//gomirrors.org/)这个网站上下载需要的版本安装。



![img](https://pic3.zhimg.com/80/v2-cc8590c8b364081166afb8a0f38ad5f6_720w.jpg)image-20200705141903092



#### Go的基本环境配置

这个主要是配置GOPATH，GOPROXY到环境变量中。

GOPATH

"此电脑" >> "属性" >> "高级系统配置" >> "环境变量" >> "系统变量" >> "新建"



![img](https://pic3.zhimg.com/80/v2-da9e5f73be7f4ab8bff0bd32c95fdcce_720w.jpg)image-20200703201246484



> - 变量名：GOPATH
> - 变量值：是你准备编辑GO程序源代码的父目录
>
> 例如：这里我的GO源代码放在D:\DevBench\Golang\golearn\src下，这里的变量值就是D:\DevBench\Golang\golearn\

最后一步是把 `%GOPATH%\bin`加到Path里。



![img](https://pic1.zhimg.com/80/v2-22eba59d72205148db63d917a0c3e04c_720w.jpg)image-20200703204029955



GOPROXY的配置

代理的配置可以在[https://goproxy.io/zh/](https://link.zhihu.com/?target=https%3A//goproxy.io/zh/)这个网站上找到说明。



![img](https://pic3.zhimg.com/80/v2-420c1bae749b031251f3ffaca526d296_720w.jpg)image-20200703201855497





![img](https://pic3.zhimg.com/80/v2-65f62b2111315d8bbd7290fbfc1c1c9a_720w.jpg)image-20200703201947206





![img](https://pic1.zhimg.com/80/v2-3363a93046998852f226f28d37424560_720w.jpg)image-20200703202107177



### Linux

TODO。

## VS Code安装及插件配置

### Window

#### VS Code安装

VS Code的安装也是比较简单的，在[https://code.visualstudio.com/](https://link.zhihu.com/?target=https%3A//code.visualstudio.com/)这个网站下载需要的版本安装。



![img](https://pic3.zhimg.com/80/v2-151c124925c3d3123844546733c5eb42_720w.jpg)image-20200705142216499



#### VS Code 上安装插件

一般地，我们只需要安装“Go Team at Google"发布的GO插件就可以，在我们新建一个go的源文件时，会弹出一些依赖的安装，我们直接点击"install all"就可以了。



![img](https://pic3.zhimg.com/80/v2-336ec6ccd577e63287528d9b8b1f434e_720w.jpg)image-20200703202412425





![img](https://pic3.zhimg.com/80/v2-d1166a8c2580ef73d41f2544d7601e22_720w.jpg)go_plugin



新建一个hello.go。



![img](https://pic4.zhimg.com/80/v2-c26f3404e7f103c0b8a51a1a4886000f_720w.jpg)image-20200703203206075



随便选个插件点击"install All", 就可以了。【其他的插件notification通知不用管，后面如果有插件安装失败了，可以在点下其他插件的"install All"，继续安装下】然后就安装下列插件：

> gocode gopkgs go-outline go-symbols guru gorename gotests gomodifytags impl fillstruct goplay godoctor dlv gocode-gomod godef goimports golint

基本都可以安装成功。

> Tools environment: GOPATH=D:\DevBench\Golang\golearn Installing 17 tools at D:\DevBench\Golang\golearn\bin in module mode. gocode gopkgs go-outline go-symbols guru gorename gotests gomodifytags impl fillstruct goplay godoctor dlv gocode-gomod godef goimports golint
>
> Installing [http://github.com/uudashr/gopkgs/v2/cmd/gopkgs](https://link.zhihu.com/?target=http%3A//github.com/uudashr/gopkgs/v2/cmd/gopkgs) SUCCEEDED Installing [http://github.com/ramya-rao-a/go-outline](https://link.zhihu.com/?target=http%3A//github.com/ramya-rao-a/go-outline) SUCCEEDED Installing [http://github.com/acroca/go-symbols](https://link.zhihu.com/?target=http%3A//github.com/acroca/go-symbols) SUCCEEDED Installing [http://golang.org/x/tools/cmd/guru](https://link.zhihu.com/?target=http%3A//golang.org/x/tools/cmd/guru) SUCCEEDED Installing [http://golang.org/x/tools/cmd/gorename](https://link.zhihu.com/?target=http%3A//golang.org/x/tools/cmd/gorename) SUCCEEDED Installing [http://github.com/cweill/gotests/..](https://link.zhihu.com/?target=http%3A//github.com/cweill/gotests/..). SUCCEEDED Installing [http://github.com/fatih/gomodifytags](https://link.zhihu.com/?target=http%3A//github.com/fatih/gomodifytags) SUCCEEDED Installing [http://github.com/josharian/impl](https://link.zhihu.com/?target=http%3A//github.com/josharian/impl) SUCCEEDED Installing [http://github.com/davidrjenni/reftools/cmd/fillstruct](https://link.zhihu.com/?target=http%3A//github.com/davidrjenni/reftools/cmd/fillstruct) SUCCEEDED Installing [http://github.com/haya14busa/goplay/cmd/goplay](https://link.zhihu.com/?target=http%3A//github.com/haya14busa/goplay/cmd/goplay) SUCCEEDED Installing [http://github.com/godoctor/godoctor](https://link.zhihu.com/?target=http%3A//github.com/godoctor/godoctor) SUCCEEDED Installing [http://github.com/go-delve/delve/cmd/dlv](https://link.zhihu.com/?target=http%3A//github.com/go-delve/delve/cmd/dlv) SUCCEEDED Installing [http://github.com/stamblerre/gocode](https://link.zhihu.com/?target=http%3A//github.com/stamblerre/gocode) SUCCEEDED Installing [http://github.com/rogpeppe/godef](https://link.zhihu.com/?target=http%3A//github.com/rogpeppe/godef) SUCCEEDED Installing [http://golang.org/x/tools/cmd/goimports](https://link.zhihu.com/?target=http%3A//golang.org/x/tools/cmd/goimports) SUCCEEDED Installing [http://golang.org/x/lint/golint](https://link.zhihu.com/?target=http%3A//golang.org/x/lint/golint) SUCCEEDEDfile:///home/fcome/Pictures/imge_bin_exe.png
>
> 1 tools failed to install.
>
> gocode: Failed to close gocode process: Error: Command failed: D:\DevBench\Golang\golearn\bin\gocode.exe close 2020/07/03 20:48:21 dial tcp 127.0.0.1:37373: connectex: No connection could be made because the target machine actively refused it. .

在bin目录下会生成exe的执行文件:

![img](https://pic1.zhimg.com/80/v2-8030ca352e1c3937edc6e1eba8fc2d88_720w.jpg)bin_exe



### Linux

TODO。

## 常见报错的问题处理

TODO。
