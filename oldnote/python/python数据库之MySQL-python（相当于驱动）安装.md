---
title:  python数据库之MySQL-python（相当于驱动）安装
categories: python   
toc: true  
tags: [python]
---




# 1.下载MySQL-python

Linux平台可以访问：https://pypi.python.org/pypi/MySQL-python  从这里可选择适合您的平台的安装包

```
$ gunzip MySQL-python-1.2.2.tar.gz
$ tar -xvf MySQL-python-1.2.2.tar
$ cd MySQL-python-1.2.2
$ python setup.py build                                    #其实一般的python模块，下载下来都是执行下面的两步进行安装
$ python setup.py install
```


# 2.安装过程中出现的问题
 
1.ImportError: No module named setuptools怎么办？
 
解决方法
```
wget https://pypi.python.org/packages/source/s/setuptools/setuptools-18.0.1.tar.gz --no-check-certificate
tar zxvf setuptools-18.0.1.tar.gz
cd setuptools-18.0.1
python setup.py build
python setup.py install
```

 
2.EnvironmentError: mysql_config not found    ？
 
解决方法
```
yum install python-devel
yum install mysql-devel
```



