* kafka-python-生产者demo

```python
#!/usr/bin/env python
# coding=utf-8

import os
import sys
import time
import socket
from kafka import KafkaProducer

kafka_topic = 'syslog'
bootstrap_servers=['soc60:9092','soc61:9092']
producer = KafkaProducer(bootstrap_servers=bootstrap_servers, retries=3)

for i in range(2):
        producer.send(kafka_topic, "test from cys 2019333333".encode('utf-8'))
        time.sleep(0.01)
```



> 注意：不知道为什么，直接使用producer.send去发送数据的时候，在执行完这句代码之后，程序通信直接关闭了，然后数据就没有发送，
>
> 但是如果加上time.sleep之后，数据就会发送，不知道是不是kafka-python的一个bug