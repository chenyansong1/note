[toc]

# 理解TCP/IP传输层拥塞控制算法

转自：https://mp.weixin.qq.com/s?__biz=MzI1MzYzMTI2Ng==&mid=2247484387&idx=1&sn=6ddcf24e16e1e363ff696bed6b8184a8&scene=21#wechat_redirect

[https://monkeysayhi.github.io/2018/03/07/%E6%B5%85%E8%B0%88TCP%EF%BC%881%EF%BC%89%EF%BC%9A%E7%8A%B6%E6%80%81%E6%9C%BA%E4%B8%8E%E9%87%8D%E4%BC%A0%E6%9C%BA%E5%88%B6/](https://monkeysayhi.github.io/2018/03/07/浅谈TCP（1）：状态机与重传机制/)



https://monkeysayhi.github.io/2018/03/07/浅谈TCP（1）：状态机与重传机制/

# TCP重传机制

**TCP协议通过重传机制保证所有的segment都可以到达对端，通过滑动窗口允许一定程度的乱序和丢包**（滑动窗口还具有流量控制等作用，暂不讨论）。注意，此处重传机制特指数据传输阶段，握手、挥手阶段的传输机制与此不同。

TCP是面向字节流的，*Seq与Ack的增长均以字节为单位*。在最朴素的实现中，为了减少网络传输，*接收端只回复最后一个连续包的Ack*，并相应移动窗口。比如，发送端发送1,2,3,4,5一共五份数据（假设一份数据一个字节），接收端快速收到了Seq 1, Seq 2，于是回Ack 3，并移动窗口；然后收到了Seq 4，由于在此之前未收到过Seq 3（乱序），如果仍在窗口内，则只填充窗口，但不发送Ack 5，否则丢弃Seq 3（与丢包的效果相似）；假设在窗口内，则等以后收到Seq 3时，发现Seq 4及以前的数据包都收到了，则回Ack 5，并移动窗口。



## 超时重传机制

*当发送方发现等待Seq 3的Ack（即Ack 4）**超时**后，会认为Seq 3发送“失败”，重传Seq 3*。一旦接收方收到Seq 3，会立即回Ack 4。







## 快速重传机制









流量控制

拥塞控制

Advertised-Window