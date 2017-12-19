# 1.安装准备

添加aphadoop用户，并切换到该用户下

```
sudo su - aphadoop

```

# 2.nexus安装

下载：http://www.sonatype.org/nexus/go/

```

tar -zxvf nexus-2.14.4-03-bundle.tar.gz -C /base_installed

```


如果你要修改默认的端口：
```
# vim /base_installed/nexus/conf/nexus.properties

application-port=8081
```

nexus本身不建议在root用户下使用，如果我们需要在root用户下启动服务，要先配置 bin/nexus 文件中的 RUN_AS_USER=root
这里我们为nexus配置的用户为aphadoop

```
# vim /base_installed/nexus/bin/nexus

RUN_AS_USER="aphadoop"

```



# 3.私服的启动和配置

**启动**
```
[aphadoop@hdp-node-01 ~]$ /base_installed/nexus/bin/nexus start
Starting Nexus OSS...
Started Nexus OSS.
[aphadoop@hdp-node-01 ~]$ 
```

启动后访问首页： http://hdp-node-01:8081/nexus/

**登录默认账号/密码 admin/admin123**

![](/images/nexus/install_1.png)


**打开 Repositories 将列表中所有Type为proxy 的项目的 Configuration 中的 Download Remote Indexes 设置为True **


![](/images/nexus/install_2.png)

**将Releases仓库的Deployment Policy设置为*Allow ReDeploy**


![](/images/nexus/install_3.png)


**设置 deployment 账户密码**

![](/images/nexus/install_4.png)


**第三方jar**


当然我们也避免不了会使用到一些第三方的 jar ，而这些jar包也不存在于互联网上的maven中央仓库中，这时我们可以手工添加jar 到我们的私服中。

添加第三方 jar 如下：

![](/images/nexus/install_5.png)


**手动设置索引**


配置maven中心仓库的索引，完全可以通过在线更新索引的方式来做，但所消耗的时间较长，下面介绍一种简单、可行的方式来手动更新索引文件。


访问http://repo.maven.apache.org/maven2/.index/  下载中心仓库最新版本的索引文件，在一长串列表中，我们需要下载如下两个文件（一般在列表的末尾位置）


```
nexus-maven-repository-index.gz
nexus-maven-repository-index.properties
```

下载完成之后最好是通过md5或者sha1校验一下文件是否一致，因为服务器并不在国内，网络传输可能会造成文件损坏。
下面就是解压这个索引文件，虽然后缀名为gz，但解压方式却比较特别，我们需要下载一个jar包indexer-cli-5.1.1.jar，我们需要通过这个特殊的jar来解压这个索引文件

注：indexer-cli-5.1.1.jar是专门用来解析和发布索引的工具，关于它的详细信息请见这里。前往maven中央仓库下载indexer-cli-5.1.1.jar, [下载](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.apache.maven.indexer%22%20AND%20a%3A%22indexer-cli%22)

将上面三个文件（.gz & .properties & .jar）放置到同一目录下，运行如下命令
```
java -jar indexer-cli-5.1.1.jar -u nexus-maven-repository-index.gz -d indexer  
```

等待程序运行完成之后可以发现indexer文件夹下出现了很多文件，将这些文件(indexer目录下的文件)放置到{nexus-home}/sonatype-work/nexus/indexer/central-ctx目录下，重新启动nexus

```
[aphadoop@hdp-node-01 ~]$ /base_installed/nexus/bin/nexus restart
```

![](/images/nexus/install_6.jpg)


<h1 id="chapter_4">4.本地项目配置引用私服</h1>

本地需要安装maven，jdk

maven的安装如下：http://wiki.jikexueyuan.com/project/maven/environment-setup.html

在项目的 pom.xml 中配置私库地址，pom.xml 的下面添加：

```
<!-- 私有仓库 -->
<repositories>  
    <repository>  
        <id>public</id>  <!--这个ID需要与你的组group ID一致--> 
        <name>Public Repository</name>
        <url>http://192.168.153.201:8081/nexus/content/groups/public</url> <!-- 这个是组对应的Repository地址-->  
    </repository>  
</repositories> 

<!-- 打包发布 -->
<distributionManagement>
    <repository>
        <id>releases</id><!--这个ID需要与你的release仓库的Repository ID一致-->
        <url>http://192.168.153.201:8081/nexus/content/repositories/releases</url>
    </repository>

    <snapshotRepository>
        <id>snapshots</id><!--这个ID需要与你的snapshots仓库的Repository ID一致-->
        <url>http://192.168.19.130:8081/nexus/content/repositories/snapshots</url>
    </snapshotRepository>
</distributionManagement>

```


在maven中有 settings.xml 文件（D:\install_soft\apache-maven-3.5.0\conf），需要配置 server 账户信息：
```
<servers>
   <server>
      <id>releases</id>
      <username>deployment</username>
      <password>dev123</password><!--这个密码就是你设置的密码-->
  </server>
  <server>
      <id>snapshots</id>
      <username>deployment</username>
      <password>dev123</password><!--这个密码就是你设置的密码-->
  </server>
</servers>

```

同时在 settings.xml 中配置镜像
```
<mirror>
  <id>central</id>
  <mirrorOf>*</mirrorOf>
  <name>Human Readable Name for this Mirror.</name>
  <url>http://192.168.153.201:8081/nexus/content/groups/public/</url>
</mirror>
```

需要说明一点：
当pom.xml中同时配置了releases仓库和snapshots仓库时。
pom.xml文件开头的版本配置1.0.0-SNAPSHOT为build到snapshots库，pom.xml文件开头的版本配置1.0.0 (不带-SNAPSHOT) 的会build到releases库，如果只配置了releases库而版本号写的是带-SNAPSHOT的，build到最后一步会报400错误，因为它找不到对应的库。



# 5.测试

1、新建一个简单的maven项目，随便写个类。

在pom.xml 文件按上面 [本地项目配置引用私服](#chapter_4) 方法添加 私有仓库和打包发布配置


![](/images/nexus/install_7.jpg)

![](/images/nexus/install_8.jpg)

![](/images/nexus/install_9.jpg)


2、再新建一个项目，或者使用已有的maven项目（最好使用别人的环境不同的电脑）。
在pom.xml 中和第1步一样先配置私库地址，然后添加第1步发布后的 dependency 后，就可以看到jar 被正常加载到工程中了。

![](/images/nexus/install_10.jpg)


**注意**

一般公司的maven私服都是在内网中的，那么需要配置访问内网中maven的防火墙


参考：

https://yq.aliyun.com/articles/7427#

http://blog.csdn.net/shawyeok/article/details/23564681

