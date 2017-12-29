http://leizhu.blog.51cto.com/3758740/1699307

import datetime
now = datetime.datetime.now()
startTimeStr = now.strftime("%Y-%m-%d %H:%M:%S")



#时间戳

http://blog.csdn.net/w657395940/article/details/46817891

1、10时间戳获取方法：

```
>>> import time
>>> t = time.time()
>>> print t
1436428326.76
>>> print int(t)
1436428326
>>> 

```

强制转换是直接去掉小数位。

2、13位时间戳获取方法：

（1）默认情况下python的时间戳是以秒为单位输出的float

```
>>> 
>>> import time
>>> time.time()
1436428275.207596
>>> 

```
通过把秒转换毫秒的方法获得13位的时间戳：

```
import time
millis = int(round(time.time() * 1000))
print millis
round()是四舍五入。
（2）
import time

current_milli_time = lambda: int(round(time.time() * 1000))
Then:
>>> current_milli_time()
1378761833768

```