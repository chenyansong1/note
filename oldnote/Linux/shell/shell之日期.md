[TOC]


shell脚本中以0开头的数字字符串怎样转换为数字？

```shell
a="09"
echo $a
b=`expr $a + 0`
echo $b
```

另外一种方式

![1568702105712](E:\git-workspace\note\images\linux\shell\1568702105712.png)