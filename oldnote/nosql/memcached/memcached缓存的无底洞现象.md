---
title: memcached缓存的无底洞现象
categories: memcached   
toc: true  
tags: [memcached]
---



# 1.Memcached 缓存无底洞现象
&emsp;facebook的工作人员反应的，facebook在2010年左右,memcached节点就已经达到了3000个,存储的数据进千G的数据存储。
&emsp;他们发现一个问题，memecached连接频率，效率都下降了，于是加了memcached节点.添加节点后发现因为连接频率导致的问题并没有好转。称之为“无底洞现象”。

# 2.问题分析：
以用户为例：user-133-age, usr-133-name, usr-133-height ....共有N个KEY。
&emsp;当服务器增多，133号的信息也被散落在更多的节点上。所以同样访问个人主页，要得到的个人主页信息,则节点越多，要连接的节点也越多。对于memcached的连接数，并没有随着节点的增多，而降低。导致问题出现...。
&emsp;事实上：
NoSQL和传统的RDBMS，并不是水火不容，而两者在某些设计上,是可以互相参考的,对于memcached,redis这种key value存储，key的设计可以参考MySql中表/列的设计，比如user表下，有age列，name列,身高列,对应的key,可以用user:1333:age=23,user:133:name='lisi',suer:133:height='168'

# 3.问题的解决方案：
&emsp;把某一组key，按照公同的前缀,来分布，比如 user-133-age,user-133-name,user-133-height这个3个key，在<font color=red>用分布式算法求其节点时,应该可以用user-133来计算而不是以 user-133-nage来计算</font>,这样3个关于个人的信息的key都落到了同一个节点上，点击的访问个人主页的时,值需要连接1个节点。











