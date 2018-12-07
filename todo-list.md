todo-list


- [ ] 需要整理的blog


  - mmap : https://blog.csdn.net/u014630431/article/details/72844501
  - Linux 中的零拷贝技术 https://www.ibm.com/developerworks/cn/linux/l-cn-zerocopy2/index.html
  - https://www.ibm.com/developerworks/cn/linux/l-cn-zerocopy1/index.html
  - sendfile  http://xiaorui.cc/2015/06/24/%E6%89%AF%E6%B7%A1nginx%E7%9A%84sendfile%E9%9B%B6%E6%8B%B7%E8%B4%9D%E7%9A%84%E6%A6%82%E5%BF%B5/



- [ ] 写一个更新的脚本
  - 1.路径替换：github中的图片是可以在typora中显示了，但是需要将原来所有的本地静态路径改过来
  - 2.提交



```
 find . -type f -name "*.md"|grep "Linux内存映射"
 
```



![](https://github.com/chenyansong1/note/blob/master/img/bigdata/kafka/conf/conf.png?raw=true)



1. 找到所有的png，jpg图片
2. 图片的url可能是Windows，Mac，七牛的HTTP的url
3. 将所有的url替换为：conf.png?raw=true
4. 因为图片的目录放在了两个目录下面，所有，
   1. 如果是七牛的图片需要改成：
   2. 如果是images的图片（不管是Windows的还是Mac的地址），需要改成：
   3. 



```


https://github.com/chenyansong1/note/blob/master/img/bigdata/kafka/conf/conf.png

#七牛图片改成

![](https://github.com/chenyansong1/note/blob/master/img/bigdata/kafka/conf/conf.png?raw=true)


#Windows和Mac图片，改成：

![](https://github.com/chenyansong1/note/blob/master/images/bigdata/kafka/conf/conf.png?raw=true)


img png or img jpg

images png or images jpg



```



