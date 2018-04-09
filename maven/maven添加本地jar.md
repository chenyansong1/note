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