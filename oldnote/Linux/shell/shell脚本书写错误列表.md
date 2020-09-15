



# 运行shell脚本时报错"[[ : not found"解决方法

```shell
在运行shell脚本时报错，命令为：

sh test.sh

#报错"[[ : not found"
```



test.sh脚本功能就是判断两个字符串是否有包含关系，但是在运行至判断表达式时报错，sh命令无法识别"[[]]"表达式。

最终也找到了问题的解决办法：bash与sh是有区别的，两者是不同的命令，且bash是sh的增强版，而"[[]]"是bash脚本中的命令，因此在执行时，使用sh命令会报错，将sh替换为bash命令即可



```shell
bash test.sh
```

















