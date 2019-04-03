[TOC]



记一次redis报错



# 问题描述

```
127.0.0.1:6379> ping
(error) MISCONF Redis is configured to save RDB snapshots, but is currently not able to persist on disk. Commands that may modify the data set are disabled. Please check Redis logs for details about the error.
```





# 解决的方法

## 1.redis自身的解决方式



```
redis 127.0.0.1:6379> config set stop-writes-on-bgsave-error no
```



参考：

<https://github.com/antirez/redis/issues/584>

<https://stackoverflow.com/questions/19581059/misconf-redis-is-configured-to-save-rdb-snapshots>





## 2.操作系统层面的解决方式

还有一种说法是，由于系统的内存不足，导致redis写出现问题

> There might be errors during the bgsave process due to low memory. Try this (from redis background save FAQ)
>
> ```
> echo 'vm.overcommit_memory = 1' >> /etc/sysctl.conf
> sysctl vm.overcommit_memory=1
> ```



关于`overcommit_memory`的说明，参见：<https://blog.csdn.net/houjixin/article/details/46412557>





# 涉及的关联问题









