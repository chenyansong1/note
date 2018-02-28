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




