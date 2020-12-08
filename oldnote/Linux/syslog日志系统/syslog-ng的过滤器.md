[toc]



syslog-ng-filter的使用

官网：https://www.syslog-ng.com/technical-documents/doc/syslog-ng-open-source-edition/3.26/administration-guide/54#TOPIC-1431108



syslog-ng的常量

https://www.syslog-ng.com/technical-documents/doc/syslog-ng-open-source-edition/3.18/administration-guide/60

```shell
destination d_prog { program("/bin/script" template("<${PRI}>${DATE} ${HOST} ${MESSAGE}\n") ); };



The following shell script writes the incoming messages into the /tmp/testlog file.

#!/bin/bash
while read line ; do
echo $line >> /tmp/testlog
done
```









destination 执行脚本

https://www.syslog-ng.com/technical-documents/doc/syslog-ng-open-source-edition/3.22/administration-guide/43

http://www.361way.com/syslog-ng-filter-sourceip/5670.html







参考：

https://www.cnblogs.com/wudonghang/p/d68887c5363edd894e2a78e5ed60c15c.html