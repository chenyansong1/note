tee



![image-20180825172938112](/Users/chenyansong/Documents/note/images/linux/command/tee.png)



读取标准输入，然后输出到标准输出和一个文件中



```
echo "hello,world." | tee /tmp/hello.out
# 控制台有打印一份；文件中有一份

```



![image-20180825172938112](/Users/chenyansong/Documents/note/images/linux/command/tee2.png)