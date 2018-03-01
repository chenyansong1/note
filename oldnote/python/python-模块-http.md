---
title:  python-模块-http
categories: python   
toc: true  
tags: [python]
---


一、python自带库----urllib2


```
import urllib2
response = urllib2.urlopen('http://localhost:8080/jenkins/api/json?pretty=true')
print response.read()
```


requests 模块

```
pip install requests

```


官网：

http://docs.python-requests.org/en/master/user/quickstart/#json-response-content
