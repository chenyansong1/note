maven添加本地jar


```
# 1.添加jar到本地的maven仓库

mvn install:install-file -Dfile=proxool-cglib.jar -DgroupId=proxool -DartifactId=proxool -Dversion=0.9.1 -Dpackaging=jar


# 2.在pom.xml文件中添加如下：

<dependency>
  <groupId>proxool-cglib</groupId>
  <artifactId>proxool-cglib</artifactId>
  <version>1.0</version>
</dependency>

```





mvn install:install-file -Dfile=jai_core-1.1.3.jar -DgroupId=javax.media  -DartifactId=jai_core -Dversion=1.1.3 -Dpackaging=jar



```
<dependency>
    <groupId></groupId>
    <artifactId></artifactId>
    <version></version>
</dependency>
```