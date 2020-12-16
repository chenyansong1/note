[toc]

转自：https://blog.csdn.net/qq_37746897/article/details/110759717

# PYCURL ERROR 22 阿里yum源安装，centos 6已停止维护



描述：将repo换成阿里云之后，出现下面的报错，**原因是阿里云的centos6已经停止维护了**

```shell
http://mirrors.aliyun.com/centos/6/extras/x86_64/repodata/repomd.xml: [Errno 14] PYCURL ERROR 22 - "The requested URL returned error: 404 Not Found"
尝试其他镜像。
```

解决办法：**将repo源替换为阿里云的一个备份源**



- 参考链接： https://wiki.centos.org/zh/About/Product
- **从下面的图可以看出centos6在2020年11月30日已经停止维护了**

- ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201206202757610.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzQ2ODk3,size_16,color_FFFFFF,t_70)

  

- 尝试了163、清华源、都不行

- 阿里源也整了半天才在最下面的相关链接发现能找到源的地址 https://developer.aliyun.com/mirror/centos?spm=a2c6h.13651102.0.0.3e221b11shelh6

  ![在这里插入图片描述](https://img-blog.csdnimg.cn/20201206203015438.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzQ2ODk3,size_16,color_FFFFFF,t_70)

1. 备份

   ```
   mv /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.backup
   ```

2. 下载新的 CentOS-Base.repo 到 /etc/yum.repos.d/

   ```
   wget -O /etc/yum.repos.d/CentOS-Base.repo https://mirrors.aliyun.com/repo/Centos-6.repo
   ```

3. 运行 yum makecache 生成缓存

   ```
   [root@hadoop101 yum.repos.d]# yum makecache 
   已加载插件：fastestmirror, refresh-packagekit, security
   Repository base is listed more than once in the configuration
   Repository updates is listed more than once in the configuration
   Repository extras is listed more than once in the configuration
   Repository centosplus is listed more than once in the configuration
   Repository contrib is listed more than once in the configuration
   Determining fastest mirrors
    * base: mirrors.aliyun.com
    * extras: mirrors.aliyun.com
    * updates: mirrors.aliyun.com
   http://mirrors.aliyun.com/centos/6/os/x86_64/repodata/repomd.xml: [Errno 14] PYCURL ERROR 22 - "The requested URL returned error: 404 Not Found"
   尝试其他镜像。
   To address this issue please refer to the below knowledge base article 
   
   https://access.redhat.com/articles/1320623
   
   If above article doesn't help to resolve this issue please open a ticket with Red Hat Support.
   
   http://mirrors.aliyuncs.com/centos/6/os/x86_64/repodata/repomd.xml: [Errno 12] Timeout on http://mirrors.aliyuncs.com/centos/6/os/x86_64/repodata/repomd.xml: (28, 'connect() timed out!')
   尝试其他镜像。
   http://mirrors.cloud.aliyuncs.com/centos/6/os/x86_64/repodata/repomd.xml: [Errno 14] PYCURL ERROR 6 - "Couldn't resolve host 'mirrors.cloud.aliyuncs.com'"
   尝试其他镜像。
   错误：Cannot retrieve repository metadata (repomd.xml) for repository: base. Please verify its path and try again
   [root@hadoop101 yum.repos.d]#   
   
   1234567891011121314151617181920212223242526
   ```

4. 替换源文件配置, 使用https://mirrors.aliyun.com/centos-vault/

   1. 官方: 非阿里云ECS用户会出现 Couldn’t resolve host ‘mirrors.cloud.aliyuncs.com’ 信息，不影响使用。用户也可自行修改相关配置: eg:

      ```
      sed -i -e '/mirrors.cloud.aliyuncs.com/d' -e '/mirrors.aliyuncs.com/d' /etc/yum.repos.d/CentOS-Base.repo
      ```

   2. 替换http成https

      ```
      sed -i  's/http/https/g' /etc/yum.repos.d/CentOS-Base.repo
      ```

   3. 替换版本，$releasever替换6.8, 6.8是我用的版本

      ```
      sed -i  's/$releasever/6.8/g' /etc/yum.repos.d/CentOS-Base.repo
      ```

   4. 替换centos为centos-vault

      ```
      sed -i  's/centos/centos-vault/g' /etc/yum.repos.d/CentOS-Base.repo
      ```

5. ```
   yum clean all && yum makecache
   ```

   ```
   [root@hadoop101 yum.repos.d]# yum clean all && yum makecache
   已加载插件：fastestmirror, refresh-packagekit, security
   Cleaning repos: base extras updates
   清理一切
   Cleaning up list of fastest mirrors
   已加载插件：fastestmirror, refresh-packagekit, security
   Determining fastest mirrors
   base                                                     | 3.7 kB     00:00     
   base/group_gz                                            | 226 kB     00:00     
   base/filelists_db                                        | 6.4 MB     00:01     
   base/primary_db                                          | 4.7 MB     00:00     
   base/other_db                                            | 2.8 MB     00:00     
   extras                                                   | 3.4 kB     00:00     
   extras/filelists_db                                      |  38 kB     00:00     
   extras/prestodelta                                       | 1.3 kB     00:00     
   extras/primary_db                                        |  37 kB     00:00     
   extras/other_db                                          |  51 kB     00:00     
   updates                                                  | 3.4 kB     00:00     
   updates/filelists_db                                     | 3.5 MB     00:00     
   updates/prestodelta                                      | 390 kB     00:00     
   updates/primary_db                                       | 5.4 MB     00:01     
   updates/other_db                                         |  74 MB     00:12     
   元数据缓存已建立
   [root@hadoop101 yum.repos.d]# 
   ```

