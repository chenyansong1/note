[TOC]



有状态应用

三个组件；

headless service

StatefulSet 

volumeClaimTemplate:卷申请模板



![image-20190728133753997](/Users/chenyansong/Documents/note/images/docker/image-20190728133753997.png)

![image-20190728133948515](/Users/chenyansong/Documents/note/images/docker/image-20190728133948515.png)

![image-20190728134203215](/Users/chenyansong/Documents/note/images/docker/image-20190728134203215.png)

我们查看绑定的pv

![image-20190728134321195](/Users/chenyansong/Documents/note/images/docker/image-20190728134321195.png)

通过scale或者patch的方式进行扩缩容

![image-20190728135531355](/Users/chenyansong/Documents/note/images/docker/image-20190728135531355.png)

滚动更新：默认是0，现在需要打补丁，改变其值为4（这样只会更新一个Pod）

![image-20190728152223234](/Users/chenyansong/Documents/note/images/docker/image-20190728152223234.png)

![image-20190728152403404](/Users/chenyansong/Documents/note/images/docker/image-20190728152403404.png)

describe看下sts的更新策略

![image-20190728152512831](/Users/chenyansong/Documents/note/images/docker/image-20190728152512831.png)

修改Pod的image

![image-20190728152932641](/Users/chenyansong/Library/Application Support/typora-user-images/image-20190728152932641.png)

查看Pod中最后一个是否是v2版本的

```shell
kubectl get pods myapp-3 -o yaml
```

我们也可以查看其它的Pod的版本是否还是v1,如果所有的都正确，那么就可以将Partition改为0，这样让所有的Pod都更新

![image-20190728153348478](/Users/chenyansong/Documents/note/images/docker/image-20190728153348478.png)

