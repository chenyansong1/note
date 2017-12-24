http://www.jianshu.com/p/f7f4142a1556

git pull push没有指定branch报错的解决方法
git 执行git push 和git pull的操作时候，经常看到下面的提示：

在高版本的 git下面，也许会看见这样的提示：

```
There is no tracking information for the current branch.

Please specify which branch you want to merge with.

See git-pull(1) for details

git pull <remote> <branch>

If you wish to set tracking information for this branch you can do so with

git branch --set-upstream master origin/<branch>
```

看到第二个提示，我们现在知道了一种解决方案。也就是指定当前工作目录工作分支，跟远程的仓库，分支之间的链接关系。

比如我们设置master对应远程仓库的master分支

```
git branch --set-upstream master origin/master
```

这样在我们每次想push或者pull的时候，只需要 输入git push 或者git pull即可。

在此之前，我们必须要指定想要push或者pull的远程分支。

```
git push origin master

git pull origin master
```
