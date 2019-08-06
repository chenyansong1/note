[TOC]

# 安装及使用

转自：https://www.cnblogs.com/ym123/p/4629735.html

## install

ab的全称是Apache Bench，是Apache自带的网络压力测试工具，相比于LR、JMeter，是我所知道的 Http 压力测试工具中最简单、最通用的。

ab命令对发出负载的计算机要求很低，不会占用很高CPU和内存，但也能给目标服务器产生巨大的负载，能实现基础的压力测试。

在进行压力测试时，最好与服务器使用交换机直连，以获取最大的网络吞吐量。

ab的安装很简单，安装Apache会自动安装，如果要单独安装ab，可以使用yum安装：

```
yum -y install httpd-tools
```

## ab命令选项

------

ab命令最基本的参数是-n和-c：

```
-n 执行的请求数量
-c 并发请求个数
```

其他参数：

```
-t 测试所进行的最大秒数
-p 包含了需要POST的数据的文件
-T POST数据所使用的Content-type头信息
-k 启用HTTP KeepAlive功能，即在一个HTTP会话中执行多个请求，默认时，不启用KeepAlive功能
```

命令示例：

```
ab -n 1000 -c 100 http://www.baidu.com/
```

## ab性能指标

------

使用ab命令测试的结果，可以参考其中的中文解释：

```shell
Document Path:          /  ###请求的资源
Document Length:        50679 bytes  ###文档返回的长度，不包括相应头


Concurrency Level:      3000   ###并发个数
Time taken for tests:   30.449 seconds   ###总请求时间
Complete requests:      3000     ###总请求数
Failed requests:        0     ###失败的请求数
Write errors:           0
Total transferred:      152745000 bytes
HTML transferred:       152037000 bytes
Requests per second:    98.52 [#/sec] (mean)      ###平均每秒的请求数
Time per request:       30449.217 [ms] (mean)     ###平均每个请求消耗的时间
Time per request:       10.150 [ms] (mean, across all concurrent requests)  ###上面的请求除以并发数
Transfer rate:          4898.81 [Kbytes/sec] received   ###传输速率


Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        2   54  27.1     55      98
Processing:    51 8452 5196.8   7748   30361
Waiting:       50 6539 5432.8   6451   30064
Total:         54 8506 5210.5   7778   30436


Percentage of the requests served within a certain time (ms)
  50%   7778   ###50%的请求都在7778Ms内完成
  66%  11059
  75%  11888
  80%  12207
  90%  13806
  95%  18520
  98%  24232
  99%  24559
 100%  30436 (longest request)
```

对压力测试的结果重点关注吞吐率（Requests per second）、用户平均请求等待时间（Time per request）指标：

### 1、吞吐率（Requests per second）

服务器并发处理能力的量化描述，单位是reqs/s，指的是在某个并发用户数下单位时间内处理的请求数。某个并发用户数下单位时间内能处理的最大请求数，称之为最大吞吐率。

记住：吞吐率是基于并发用户数的。这句话代表了两个含义：

a、吞吐率和并发用户数相关

b、不同的并发用户数下，吞吐率一般是不同的

计算公式：总请求数/处理完成这些请求数所花费的时间，即

Request per second=Complete requests/Time taken for tests

必须要说明的是，这个数值表示当前机器的整体性能，值越大越好。

### 2、用户平均请求等待时间（Time per request）

计算公式：处理完成所有请求数所花费的时间/（总请求数/并发用户数），即：

Time per request=Time taken for tests/（Complete requests/Concurrency Level）

### 3、服务器平均请求等待时间（Time per request:across all concurrent requests）

计算公式：处理完成所有请求数所花费的时间/总请求数，即：

Time taken for/testsComplete requests

可以看到，它是吞吐率的倒数。

同时，它也等于用户平均请求等待时间/并发用户数，即

Time per request/Concurrency Level