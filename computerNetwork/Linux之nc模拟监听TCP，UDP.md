[toc]

# Linux之nc模拟监听TCP，UDP



有时候需要模拟监控TCP或者UDP的端口，幸好linux提供了好用的工具nc



```undefined
yum install nc -y
```

## 监听TCP端口与测试



```bash
# 监听在tcp的3307端口 : -l listen,默认不指定是tcp
nc -lv 3307
```



```bash
# 往tcp的3307端口发送消息
nc localhost 3307
```

![img](https:////upload-images.jianshu.io/upload_images/426671-65a4866207c1aa76.png?imageMogr2/auto-orient/strip|imageView2/2/w/584/format/webp)

1564389178131

![img](https:////upload-images.jianshu.io/upload_images/426671-c4cc7f295f17ef2e.png?imageMogr2/auto-orient/strip|imageView2/2/w/672/format/webp)

1564389166005

## 监听UDP端口与测试



```bash
# 监听在UDP的3307端口
nc -lvu 3307
```



```ruby
# 往UDP端口发送消息
echo -n "hello world,UDP" >/dev/udp/localhost/3307
```

![img](https:////upload-images.jianshu.io/upload_images/426671-0113e28f425f8f80.png?imageMogr2/auto-orient/strip|imageView2/2/w/579/format/webp)

1564389272251

![img](https:////upload-images.jianshu.io/upload_images/426671-d551985d63d70893.png?imageMogr2/auto-orient/strip|imageView2/2/w/838/format/webp)

