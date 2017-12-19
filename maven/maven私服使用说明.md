# 1.准备

本地需要安装maven，jdk，可以[参见](http://wiki.jikexueyuan.com/project/maven/environment-setup.html)

# 2.配置

在项目的 pom.xml 中配置私库地址，pom.xml 的下面添加：

```
<!-- 私有仓库 -->
<repositories>
    <repository>
        <id>central</id>
        <name>219.135.99.163-Nexus</name>
        <url>http://219.135.99.163:8081/nexus/content/groups/public/</url>
    </repository>
</repositories>

<!-- 打包发布 -->
<distributionManagement>
    <repository>
        <id>releases</id><!--这个ID需要与你的release仓库的Repository ID一致-->
        <url>http://219.135.99.163:8081/nexus/content/repositories/releases/</url>
    </repository>

    <snapshotRepository>
        <id>snapshots</id><!--这个ID需要与你的snapshots仓库的Repository ID一致-->
        <url>http://219.135.99.163:8081/nexus/content/repositories/snapshots</url>
    </snapshotRepository>
</distributionManagement>

```


在maven中有 settings.xml 文件（我的maven安装在 D:\install_soft\apache-maven-3.5.0\conf），需要配置 server 账户信息：
```
<servers>
   <server>
      <id>releases</id>
      <username>deployment</username>
      <password>dev123</password>
  </server>
  <server>
      <id>snapshots</id>
      <username>deployment</username>
      <password>dev123</password>
  </server>
</servers>

```

同时在 settings.xml 文件中配置镜像
```
<mirror>
  <id>central</id>
  <mirrorOf>*</mirrorOf>
  <name>Human Readable Name for this Mirror.</name>
  <url>http://219.135.99.163:8081/nexus/content/groups/public/</url>
</mirror>
```


**需要说明一点：**
当pom.xml中同时配置了releases仓库和snapshots仓库时。
pom.xml文件开头的版本配置1.0.0-SNAPSHOT为build到snapshots库，pom.xml文件开头的版本配置1.0.0 (不带-SNAPSHOT) 的会build到releases库，如果只配置了releases库而版本号写的是带-SNAPSHOT的，build到最后一步会报400错误，因为它找不到对应的库。

# 3.引入引用

例如：我要引入log4j的引用

## 3.1.搜索找到引用

进入 http://search.maven.org/ 搜索log4j

![](/note/images/nexus/use_1.jpg)

![](/note/images/nexus/use_2.jpg)

将引用粘贴到maven工程的pom.xml文件中，就会自动完成下载
![](/note/images/nexus/use_3.jpg)

![](/note/images/nexus/use_4.jpg)

![](/note/images/nexus/use_5.jpg)


## 3.2.发布

我本地使用maven的图形界面，点击发布，会报错，所以我使用的是maven的命令行方式发布，如下：

![](/note/images/nexus/use_6.jpg)

然后登陆maven的web界面，就可以看到发布的工程

![](/note/images/nexus/use_7.jpg)
![](/note/images/nexus/use_8.jpg)
![](/note/images/nexus/use_9.jpg)


