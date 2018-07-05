```
# 这里为Kafka broker配置了两个listeners，一个是明文传输；另一个使用SSL加密进行数据传输
listeners=PLAINTEXT://:9092,SSL://:9093   

# 因为是云上环境，如果clients通过公网（或外网）去连接broker，那advertiesd.listeners就必须配置成所在机器的公网IP
advertised.listeners=PLAINTEXT://公网IP:9092,SSL://公网IP:9093  

```



参考：

https://hacpai.com/article/1517731232605

https://blog.csdn.net/miqi770/article/details/77776213