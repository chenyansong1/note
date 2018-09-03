[TOC]

* SUID: 运行某程序时，相应进程的属主是程序文件的自身的属主，而不是启动者
* SGID:运行某程序时，相应进程的属组是程序文件的自身的属组，而不是启动者所属的基本组



# SUID

```
useradd hadoop
su - hadoop

cat /etc/shadow  
#启动cat的进程是hadoop属主，hadoop属组，所以当cat 来访问/etc/shadow的时候，是以hadoop用户来访问的，但是hadoop用户是没有访问/etc/shadow这个文件的权限的

#给cat加上suid

chmod u+s filename
	如果filename本身就有执行权限，则SUID显示为s,否则显示为S(大写)
chmod u+s /bin/cat
cat /etc/shadow  #此时应该可以访问shadow文件了


# 去掉s
chmod u-s filename

```



![image-20180903221114414](/Users/chenyansong/Documents/note/images/linux/command/suid.png)



# SGID

- SGID:运行某程序时，相应进程的属组是程序文件的自身的属组，而不是启动者所属的基本组



需求：在/tmp/project目录下对三个用户hadoop, hbase, hive都有创建，修改彼此文件的权限



```
#当前用户是root

mkdir /tmp/project
groupadd developteam
useradd hadoop
useradd hbase
useradd hive

#将hadoop, hbase, hive加入到developteam组中
usermod -a -G developteam hadoop
usermod -a -G developteam hbase
usermod -a -G developteam hive

#将目录的属组变成 developteam
chown -R :developteam /tmp/project

#让属组有写权限
chmod g+w /tmp/project

#此时hadoop, hbase, hive都会有写权限，但是此时每个用户创建(touch)文件都是自己的属主，属组，如hadoop用户创建的是 hadoop,hadoop的属主，属组

#那么就存在一个问题，hadoop用户创建的文件，hbase是不能编辑的

/*
为什么会出现这个问题：因为我们以hadoop用户去创建文件的时候，创建出来的文件是根据touch进程的属主，属组来确定文件的属主，属组的，此时创建的文件就是hadoop,hadoop,但是如果我们队目录加上sgid，那么使用touch创建文件的时候，此时会以程序文件的属组，也就是目录的属组来确定创建的文件，此时创建的文件的属组和目录的属组相同
*/

#给目录加上sgid

chmod g+s /tmp/project

#此时再去创建文件，那么新的文件将会以目录的属组作为新文件的属组，这样多个同属于developteam 组的用户，都会对除自身之外的用户有执行权限



# 去掉
chmod g-s dir
```



# sticky



sticky: 在一个公共目录，每一个人都可以创建自己的文件，删除自己创建的文件，但是不能删除别人的文件



```
chmod o+t dirname
chmod o-t dirname
```



![image-20180903223356707](/Users/chenyansong/Documents/note/images/linux/command/sticky.png)





# suid,sgid,sticky的mod



```
111 三位表示的是suid, sgid, sticky位

chmod 2755 /tmp/test  #2表示010即有sgid位

```

