转自：http://blog.csdn.net/yabingshi_tech/article/details/48548855

在Linux中写脚本的时候，总免不了需要判断文件是否存在、文件内容是否为空等存在，而这些操作都可以用test 指令来实现，通过 man test 指令可以查看关于test指令的手册，手册中有如下说明： 
```
-s FILE
              FILE exists and has a size greater than zero
              如果文件存在且文件大小大于零，则返回真
-e FILE
              FILE exists
              如果文件存在，则返回真
```

在shell中通过test指令测试文件是否为空的示例脚本如下：
```
#! /bin/sh
if test -s file.txt; then
        echo "hi"
else
        echo "empty"
fi
```


在shell中，test指令还有另外一种写法，上面的脚本和下面的脚本是等价的：
```
#! /bin/sh
if [ -s file.txt ]; then
        echo "hi"
else
        echo "empty"
fi

```