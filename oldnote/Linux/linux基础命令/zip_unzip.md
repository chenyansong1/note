zip实现文件加解密



```shell
#文件加解密，指定密码
zip -P 111 test.c.zip test.c

#解密覆盖原始文件
unzip -o -P 111 test.c.zip 

```

