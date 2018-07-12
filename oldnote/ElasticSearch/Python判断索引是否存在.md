
python 判断索引是否存在

```
>>> r = requests.head('http://172.16.14.38:9200/syslog_20180408/')
>>> r.status_code
200
>>> r = requests.head('http://172.16.14.38:9200/syslog_20180409/')
>>> r.status_code
404
>>> 

```
