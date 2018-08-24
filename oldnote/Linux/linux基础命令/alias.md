**alias用来将一些复杂的命令简化成自定义的命令。**这个很好的功能



[TOC]





# windows下alias在git Bash中的使用



1、查看当前的自定义命令列表

```
alias
```

2、添加一条自定义的命令

```
alias bs='npm --register=http://10.10.81.140:4873'
```

3、删除一条自定义的命令

```
unalias bs
```

上面的方法只能在这一次使用，如果关掉了bash，下一次打开就会失效。
我们这就需要在.bashrc中设置一个alias

```
vim ~/.bashrc
alias bs='cnpm'
source ~/.bashrc
```

如果设置后不生效的话，我们应该新增一个.bash_profile，并且在这个文件中添加一个

```
source ~/.bashrc
```



