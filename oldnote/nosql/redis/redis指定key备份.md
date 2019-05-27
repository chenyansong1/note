---
title: redis指定key备份
categories: redis   
toc: true  
tags: [redis]
---


```
#备份指定的key
./redis-cli  -a 123456 --raw dump iocs | head -c-1 > back_iocs_data
#恢复到指定的newkey
cat back_iocs_data | ./redis-cli -a 123456 -x restore iocs_bakup 0

```


