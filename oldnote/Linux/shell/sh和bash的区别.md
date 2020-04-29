---
title: sh和bash的区别
categories: shell   
toc: true  
tags: [shell]
---



# 1.脚本开头(第一行)
```
脚本开头（第一行）,由哪个程序（解释器）来执行脚本
#!/bin/bash 
#或者
#!/bin/sh
#其中开头的#!字符又称为幻数，在执行bash脚本的时候，内核会根据“#!”来选择解释器，解释器确定用哪个程序解释这个脚本中的内容。注意：这一行必须在每个脚本的第一行，如果不是第一行则为脚本注释行：

vi test1.sh
#!/bin/bash
echo “oldboy test”
#!/bin/bash //这里就是注释了

```

# 2.sh和bash的区别

```
[root@lamp01 ~]# ll /bin/sh
lrwxrwxrwx. 1 root root 4 7月   3 2016 /bin/sh -> bash

[root@lamp01 ~]# ll /bin/bash
-rwxr-xr-x 1 root root 874248 5月  11 2012 /bin/bash

```
