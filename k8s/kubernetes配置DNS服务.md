[TOC]

参见：https://jimmysong.io/posts/configuring-kubernetes-kube-dns/

Kubernetes集群DNS服务器基于 [SkyDNS](https://github.com/skynetservices/skydns) 库。它支持正向查找（A 记录），服务查找（SRV 记录）和反向 IP 地址查找（PTR 记录）

# kube-dns 支持的 DNS 格式

kube-dns 将分别为 service 和 pod 生成不同格式的 DNS 记录。

**Service**

- A记录：生成`my-svc.my-namespace.svc.cluster.local`域名，解析成 IP 地址，分为两种情况：
  - 普通 Service：解析成 ClusterIP
  - Headless Service：解析为指定 Pod 的 IP 列表
- SRV记录：为命名的端口（普通 Service 或 Headless Service）生成 `_my-port-name._my-port-protocol.my-svc.my-namespace.svc.cluster.local` 的域名

**Pod**

- A记录：生成域名 `pod-ip.my-namespace.pod.cluster.local`