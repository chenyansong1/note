[toc]

目的：反编译查看源码



```shell

#将文件定位到指定的文件中，这样方便查看，以及方便编译
jad --source-only com.example.demo.arthas.user.UserController > /tmp/UserController.java


#只反编译某个方法
[arthas@2847]$ jad --source-only com.bluedon.dataMatch.EventMatch eventMatchBySystem > /tmp/UserController.java

```



参考：http://hengyunabc.github.io/arthas-online-hotswap/







