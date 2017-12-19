---
title:  python进程之进程间通讯——命名管道
categories: python   
toc: true  
tags: [python]
---




# 1.简介
* 命名管道：无名管道的一个扩展，无名管道是程序运行时存在，命名管道是持久的，一旦创建，所有有权限进程都可以访问
* 命名管道是单向管道，只能以只读或只写方式打开，如果要实现双向通信，必须打开两个管道
* 命名管道创建：os.mkfifo(path) ，读写和操作文件一样
* 命名管道通信模型：

![](http://ols7leonh.bkt.clouddn.com//assert/img/python/pipe/5.png)

* 代码示例

```
#process1
import os
p_name = "pipe1"
os.mkfifo(p_name)  
try:
    fp=open(p_name,'w')    #写方式打开管道
except IOError:
    print "open %s Error" % p_name
else:
    msg = os.write(fp, "hello python")    #发送消息
finally:
    if (fp):
        fp.close()


#process2
import os
p_name = "pipe1"
os.mkfifo(p_name)    #创建管道,管道相当于文件描述符,所以可以像文件一样可以用open进行读写
try:
    fp=open(p_name,'r')    #读方式打开管道
except IOError:
    print "open %s Error" % p_name
else:
    msg = os.read(fp, 1024)
    print msg
finally:
    if (fp):
        fp.close()

```


* 阻塞：自行设备操作时，如果不能获得资源就会挂起进程，知道获取资源后在进行操作，被挂起的进程进入休眠状态
* 非阻塞：执行设备操作时，如果不能获取资源直接返回，可以使用轮询的方式进行设备操作
* 一个管道可以多个进程打开，是否安全


# 2.os.access测试路径的访问权
```
os.access(path, mode)
 
使用实际的uid和gid去测试路径的访问权。实际的uid和gid指的是用户登录到系统使用的uid和当前用户所在的gid，这和有效用户id和有效组id是有区别的，有效用户id和有效组id是对应于进程的。
 
mode参数指定测试路径的方式：
 
os.F_OK - 测试路径是否存在
 
os.R_OK - 测试文件是否可读
 
os.W_OK - 测试文件是否可写
 
os.X_OK - 测试文件是否可执行
 
其中的R_OK，W_OK，X_OK是可以使用OR操作合起来进行一起测试的。
 
#函数返回True如果测试成功，否则返回False。在系统的C API中可以使用access系统调用。
```

# 3.创建pipe，读写举例（以系统的open）

 如果进程以只读方式打开管道，那么会阻塞，直到有进程以只写方式打开管道为止

```
#写
[root@backup python]# cat pipe_w.py
#!/usr/bin/python
 
import os                    
 
p_name = "./pipe"                        #管道名称
if os.access(p_name,os.F_OK)==False:                #判断路径是否存在        
        os.mkfifo(p_name)                            #创建有名管道
 
print("before open")
 
fp_w = open(p_name,'w')                        #以只写的方式打开一个管道文件，返回管道对象
 
print("end open")
 
msg = ""
 
while True:
        msg1 = raw_input(">")
        fp_w.write(msg1)                            #向管道中写入数据
        fp_w.flush()                                #刷新缓存数据到管道中，因为使用的是系统的open函数打开的管道，所以相当于是C的方法，而C中是有缓存存在的
        if msg1=='q':
                break
 
fp_w.close()


---------------------------------------------
#读
[root@backup python]# cat pipe_r.py
#!/usr/bin/python
 
import os
 
p_name = "./pipe"
if os.access(p_name,os.F_OK)==False:
        os.mkfifo(p_name)                                        
 
print("before open")
 
fp_r = open(p_name,'r')                # 其实open打开的虽然是管道，但是他相当于一个文件一样，用ls可以在本地看到
 
print("end open")
 
 
while True:
        msg = fp_r.read(1)                            #每次读取一个字节
        print(msg)
        if msg == 'q':
                break
fp_r.close()


```

```
[root@backup python]# ll
#os.mkfifo(p_name)    ，会生成一个pipe的文件
prw-r--r-- 1 root root    0 10月  5 16:30 pipe

```

# 4.os.open/read/write简绍
```
In [1]: import os
 
In [2]: help(os.open)
Help on built-in function open in module posix:
 
open(...)
    open(filename, flag [, mode=0777]) -> fd                            #返回fd，即文件描述符
 
    Open a file (for low level IO).
 
#flag 有以下方式：如  os.O_WRONLY(读写)    os.O_RDONLY（只读）    os.O_WRONLY （只写）
In [3]: os.O_
os.O_APPEND     os.O_DIRECT     os.O_EXCL       os.O_NOATIME    os.O_NONBLOCK   os.O_RSYNC      os.O_WRONLY    
os.O_ASYNC      os.O_DIRECTORY  os.O_LARGEFILE  os.O_NOCTTY     os.O_RDONLY     os.O_SYNC      
os.O_CREAT      os.O_DSYNC      os.O_NDELAY     os.O_NOFOLLOW   os.O_RDWR       os.O_TRUNC     
 
#read
In [3]: help(os.read)
Help on built-in function read in module posix:
 
read(...)
    read(fd, buffersize) -> string
 
    Read a file descriptor.            #读文件描述符
 
 
#write 
In [4]: help(os.write)
Help on built-in function write in module posix:
 
write(...)
    write(fd, string) -> byteswritten
 
    Write a string to a file descriptor.    #写string 到文件描述符
 
#close
In [5]: help(os.close)
Help on built-in function close in module posix:
 
close(...)
    close(fd)
 
    Close a file descriptor (for low level IO).        #关闭一个文件描述符
 

```




# 5.创建pipe，读写举例（以os模块的open）
使用os.open的方式打开，返回的是一个文件描述符
```
#写
#!/usr/bin/python
 
import os
 
p_name = "./pipe"
if os.access(p_name,os.F_OK)==False:
        os.mkfifo(p_name)                    #没有就创建管道
 
print("before open")
 
fp_w = os.open(p_name, os.O_WRONLY)            #os.open返回一个文件描述符
 
print("end open")
 
msg = ""
 
while True:
        msg1 = raw_input(">")
        '''
        #fp_w.write(msg1)                        #如果使用的是os.open就不用刷新，os中并没有缓冲的存在，而是直接读取的。所以是写端写多少，在读端读多少
        #fp_w.flush()
        '''
        os.write(fp_name,msg1)                   #向文件描述符中写
        if msg1=='q':
                break
 
 
os.close(fp_w)                            #关闭文件描述符


---------------------------------------------------------------------

#读
[root@backup python]# cat pipe_os_r.py 
#!/usr/bin/python
 
import os
 
p_name = "./pipe"
if os.access(p_name,os.F_OK)==False:
        os.mkfifo(p_name)
 
print("before open")
 
fp_r = os.open(p_name,os.O_RDONLY)                        #打开一个文件描述符
 
print("end open")
 
 
while True:
        msg = os.read(fp_r, 1024)                            #读入文件描述符中的内容
        if msg == '':                        #如果杀掉写端的进程，那么在读端会继续读，所以我们对读取的内容进行判断，如果为空字符串，就退出
                break
        print(msg)
        if msg == 'q':
                break
 
     
os.close(fp_r)                                    #关闭文件描述符

```


