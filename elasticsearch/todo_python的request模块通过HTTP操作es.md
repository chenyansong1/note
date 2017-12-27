python的request模块通过HTTP操作es


import requests, json
url = 'http://172.16.14.21:9200/genlog/_search'
payload = open("request.json")
headers = {'content-type': 'application/json', 'Accept-Charset': 'UTF-8'}
r = requests.post(url, data=payload, headers=headers)


{"from": 1,"size": 1}

http://www.cnblogs.com/xing901022/p/5317698.html

http://www.ruanyifeng.com/blog/2017/08/elasticsearch.html
https://stackoverflow.com/questions/25491090/how-to-use-python-to-execute-a-curl-command

https://stackoverflow.com/questions/15930235/querying-elasticsearch-with-python-requests-not-working-fine

这个号：
https://marcobonzanini.com/2015/02/02/how-to-query-elasticsearch-with-python/
http://www.cnblogs.com/lilinwei340/p/6417689.html
https://cuiqingcai.com/2556.html

