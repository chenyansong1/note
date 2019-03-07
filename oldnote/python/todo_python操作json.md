
#!/usr/bin/python
import json

jsonData = '{"a":1,"b":2,"c":3,"d":4,"e":5}';

text = json.loads(jsonData)
print text



json文件如下：

````
{
    "took":118,
    "timed_out":false,
    "_shards":{
        "total":5,
        "successful":5,
        "failed":0
    },
    "hits":{
        "total":589746,
        "max_score":0,
        "hits":[

        ]
    },
    "aggregations":{
        "src_ip":{
            "doc_count_error_upper_bound":1467,
            "sum_other_doc_count":485254,
            "buckets":[
                {
                    "key":"219.132.7.100",
                    "doc_count":37282
                },
                {
                    "key":"118.31.75.161",
                    "doc_count":14192
                },
                {
                    "key":"121.196.216.94",
                    "doc_count":13930
                }
            ]
        }
    }
}
````





```python
[root@es1 ~]# cat test.py 
#!/usr/bin/python
import json
import ast

with open('ES_IP.txt') as json_file:  
    data = json.loads(json_file.read())
    for p in data['aggregations']['src_ip']['buckets']:
        ip_str = p['key']
        #if ip_str.startswith("21"):
        if ip_str.startswith("172"):
                print(p['key'])

```





区分正确的json文件：单引号和双引号的区别

https://stackoverflow.com/questions/39491420/python-jsonexpecting-property-name-enclosed-in-double-quotes/39491613

https://blog.csdn.net/Sinsa110/article/details/51189456





https://python3-cookbook.readthedocs.io/zh_CN/latest/c06/p02_read-write_json_data.html

http://www.cnblogs.com/coser/archive/2011/12/14/2287739.html




