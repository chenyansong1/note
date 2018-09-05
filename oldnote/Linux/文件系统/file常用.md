
[TOC]



* basename, dirname





```
[webuser@VM_0_4_centos ~]$ basename /tmp/setRps.log 
setRps.log
[webuser@VM_0_4_centos ~]$ dirname /tmp/xx/sss
/tmp/xx
[webuser@VM_0_4_centos ~]$ 
[webuser@VM_0_4_centos ~]$ basename /tmp/x.log
x.log
```



$0是执行脚本时的脚本路径及名称

这样我们在shell脚本中拿到脚本的名称就很简单了：

```
basename $0
```

