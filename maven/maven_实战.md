以下笔记来自：《maven实战》



[TOC]

#1.maven的安装和配置

## 1.1.maven的目录结构

```
[root@soc22 workspace]# tree -L 1 ./apache-maven-3.5.4
./apache-maven-3.5.4
├── bin		#该目录包含了mvn运行的脚本，这些脚本用来配置java命令
├── boot	#只包含一个文件，是一个类加载器框架
├── conf	#一个非常重要的配置文件，直接修改该配置文件，就能在机器上全局定制maven的行为，一般情况下，我们更偏向于复制该文件至 ~/.m2/目录下，然后修改该文件，在用户范围内定制maven的行为
├── lib		#该目录包含了所有maven运行时需要的java类库
├── LICENSE
├── NOTICE
└── README.txt

```



> maven默认的本地仓库是：~/.m2/repository，但是大多数情况下，需要复制MVN_HOME/conf/settings.xml文件到~/.m2/settings.xml中，在某个用户范围内定制Maven的行为



## 1.2.设置http代理

有时候你所在的公司基于安全因素的考虑，要求你使用通过安全认证的代理访问因特网，这种情况下就需要为maven配置HTTP代理，才能让他正常访问外部仓库，以下载所需要的资源（**此时访问外网，都需要走代理**）

```
  <proxies>
    <!-- proxy
     | Specification for one proxy, to be used in connecting to the network.
     |
    <proxy>
      <id>optional</id>
      <active>true</active>
      <protocol>http</protocol>
      <username>proxyuser</username>
      <password>proxypass</password>
      <host>proxy.host.net</host>
      <port>80</port>
      <nonProxyHosts>local.net|some.host.com</nonProxyHosts>
    </proxy>
    -->
  </proxies>
  
  
  ##################################
  proxies下可以有多个proxy元素，如果声明了多个proxy元素，则默认情况下第一个被激活的proxy会生效，这里声明了一个id为optional的代理，active值为true表示激活该代理，protocol表示使用的代理协议，这里是http，当然，最重要的是制定正确的主机名（host元素）和端口（port元素），当代理服务需要认证的时候，就需要配置username和password，nonProxyHost元素用来指定哪些主机名不需要代理，可以使用“|”符号分隔多个主机名，此外该配置项支持通配符，如：*.google.com表示所有以google.com结尾的域名访问都不要通过代理
  
  ##################################
```



> maven的默认仓库：http://repo1.maven.org/maven2





## 1.3.maven的安装最佳实践

### 1.3.1.设置MAVEN_OPTS环境变量

运行mvn命令实际上执行了Java命令，既然是运行java，那么运行java命令可用的参数当然也应该在运行mvn命令时可用，这时MAVEN_OPTS环境变量就能派上用场



> 通常需要设置MAVEN_OPTS的值为：-Xms128m -Mmx512m, 因为java默认的最大可用内存往往不能够满足Maven运行的需要，比如在项目较大的时候，使用maven生成项目站点需要占用大量的内存，如果没有改配置，则容易得到java.lang.OutOfMemoryError，因此一开始就配置该变量是推荐的做法（**尽量不要修改Maven安装目录下的文件来修改这个变量**）





### 1.3.2.配置用户范围的settings.xml

maven用户可以选择配置$M2_HOME/conf/settings.xml或者 ~/.m2/settigns.xml，**前者是全局范围的，整台机器上的所有用户都会直接受到改配置的影响，而后者是用户范围的，只有当前用户才会受到改配置的影响**





### 1.3.3.不要使用IDE内嵌的Maven



# 2.maven的使用入门



## 2.1.编写pom

Maven项目的核心是pom.xml，POM（Project Object Model，项目对象模型）定义了项目的基本信息，用于描述项目如何构建，声明项目依赖等等，以下是一个最简单的pom.xml文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.juvenxu.mvnbook</groupId>
    <artifactId>hello-world</artifactId>
    <version>1.0-SNAPSHOT</version>
	<name>Manven Hello World Project</name>
	
</project>


###############################
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<!--指定了当前POM模型的版本，对于Maven2及Maven3来说，他只能是4.0.0-->
    <modelVersion>4.0.0</modelVersion>
    
    <!--groupId定义了项目属于哪个组，这个组合项目所在的组织或公司存在关联，比如goolecode上建立了一个名为myapp的项目，那么groupId为com.googlecode.myapp,如果你的公司是mycom,有一个项目为myapp，那么groupId为com.mycom.myapp,本书中所有的代码都是基于groupId 为com.juvenxu.mvnbook-->
    <groupId>com.juvenxu.mvnbook</groupId>
    <!--artifactId定义了当前maven项目在组中的唯一的ID，我们为这个Hello World项目定义artifactId为hello-world,本书其他的章节代码会分配其他的artifactId,而在前面的groupId为com.googlecode.myapp的例子中，你可能会为不同的子项目（模块）分配artifact,如：myapp-util, myapp-domain, myapp-web-->
    <artifactId>hello-world</artifactId>
    <!--指定了Hello World项目当前的版本-->
    <version>1.0-SNAPSHOT</version>
	<!--一个用于用户更为友好的项目名称，虽然这不是必须的，但是还是推荐为每个POM声明name，以方便信息交流-->
    <name>Manven Hello World Project</name>
	
</project>
###############################

```



> pom的意义：java代码和pom的解耦



## 2.2.编写主代码

* 主代码的目录结构：默认情况下，代码位于：**src/main/java**

![](https://github.com/chenyansong1/note/blob/master/images/maven/abc1.png?raw=true)





* 包名：遵循Maven的约定：创建：**com/juvenxu/mvnbook/helloworld，一般来说，项目中Java类的包都应该基于项目的groupId和artifactId**



* 编写代码

```
package com.juvenxu.mvnbook.helloworld;

/**
 * Created by cys on 2019/1/28.
 */
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("test from hello world!");
    }
}

```

![](https://github.com/chenyansong1/note/blob/master/images/maven/abc2.png?raw=true)



* 编译代码时发生了什么

```
E:\helloworld>mvn clean compile
[INFO] Scanning for projects...
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Building hello-world 1.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO]
########## clean告诉Maven清理输出目录target/ ##########
[INFO] --- maven-clean-plugin:2.5:clean (default-clean) @ hello-world ---
[INFO] Deleting E:\helloworld\target
[INFO]
########## resources告诉Maven将配置文件copy到指定位置 ##########
[INFO] --- maven-resources-plugin:2.6:resources (default-resources) @ hello-world ---
[WARNING] Using platform encoding (GBK actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] Copying 0 resource
[INFO]
########## resources告诉Maven将项目主代码编译至target/classes目录下 ##########
[INFO] --- maven-compiler-plugin:3.1:compile (default-compile) @ hello-world ---
[INFO] Changes detected - recompiling the module!
[WARNING] File encoding has not been set, using platform encoding GBK, i.e. build is platform dependent!
[INFO] Compiling 1 source file to E:\helloworld\target\classes
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 2.259 s
[INFO] Finished at: 2019-01-28T17:34:57+08:00
[INFO] Final Memory: 14M/184M
[INFO] ------------------------------------------------------------------------

```



> 1. 测试代码只是在运行测试时用到，不会被打包
> 2. 上面提到的clean:clean，resources:resources，compiler:compiler对应了一些Maven插件及插件目标，比如：clean:clean是clean插件的clean目标, compiler:compiler是compiler插件的compile目标



## 2.3.编写测试代码

* 测试代码的目录结构

maven的项目的默认的测试代码目录是src/test/java

* JUnit依赖

```xml
    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
```



> 上述POM代码中还有一个值为test的元素scope，scope为依赖范围，若依赖范围为test，则表示该依赖只对测试代码有效，换句话说，测试代码中的import JUnit代码是没有问题的，**但是如果在主代码中用import JUnit代码，就会造成编译错误，如果不声明依赖范围，那么默认值就是compile，表示该依赖对主代码和测试代码都有效**



* 测试代码的编写流程

  1. 准备测试类及数据
  2. 执行要测试的行为
  3. 检查结果

  ```java
  import com.juvenxu.mvnbook.helloworld.HelloWorld;
  import org.junit.Assert;
  import org.junit.Test;
  
  /**
   * Created by landun on 2019/1/29.
   */
  public class HellowWorldTest {
  
      @Test
      public void testSayHello(){
          // 1.准备测试类及数据
          HelloWorld helloWorld = new HelloWorld();
          // 2.执行要测试的行为
          String result = helloWorld.sayHello();
  		// 3.检查结果（使用JUnit框架的Asset类检查结果是否为我们期望的“hello Maven”）
          Assert.assertEquals("Hello Maven", result);
  
      }
  }
  
  ```

  

* mvn clean test测试

  在Maven执行测试(test)之前,他会先自动执行：

  1. 项目主资源处理，
  2. 主代码编译，
  3. 测试资源处理，
  4. 测试代码编译
  5. surefire:test任务运行测试等工作

* 可能出现的问题

![](https://github.com/chenyansong1/note/blob/master/images/maven/abc3.png?raw=true)



出现上述原因是：由于历史原因，Maven的核心插件之一compiler插件默认只支持编译java1.3，因此需简要配置该插件使其支持Java1.8，在pom.xml文件中添加如下：

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
```



* 总的测试日志如下

![](https://github.com/chenyansong1/note/blob/master/images/maven/abc4.png?raw=true)



## 2.4.打包和运行



* 打包

  默认打包类型为jar，简单的执行命令mvn clean package运行打包，可以看到如下输出：

  ![](https://github.com/chenyansong1/note/blob/master/images/maven/abc5.png?raw=true)

  jar插件的jar目标将项目主代码打包成一个名为hello-world-1.0-SNAPSHOT.jar的文件，该文件也位于target/输出目录下，他是根据artifact-version.jar规则进行命名的

  

* 安装

  如果才能让其他的Maven项目直接引用这个jar呢？这就需要 mvn clean install

  

  ![](https://github.com/chenyansong1/note/blob/master/images/maven/abc6.png?raw=true)

​	

* 指定Main运行

  默认打包生成的jar是不能够直接运行的，因为带有main方法的类信息不会添加到manifest中（打开jar文件中的META-INF/MANIFEST.MF文件，将无法看到Main-Class一行），为了生成可执行的jar文件，需要借助maven-shade-plugin，配置改插件如下：

  ```xml
              <!--main-->
              <plugin>
                  <groupId>org.apache.maven.plugins</groupId>
                  <artifactId>maven-shade-plugin</artifactId>
                  <version>1.4</version>
                  <executions>
                      <execution>
                          <phase>package</phase>
                          <goals>
                              <goal>shade</goal>
                          </goals>
                          <configuration>
                              <transformers>
                                  <transformer
                                          implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                      <mainClass>com.juvenxu.mvnbook.helloworld.HelloWorld</mainClass>
                                  </transformer>
                              </transformers>
                          </configuration>
                      </execution>
                  </executions>
              </plugin>
  ```



​	打开 hello-world-1.0-SNAPSHOT.jar 的META-INF/MANIFEST.MF，可以看到他包含这样一行信息：

```
Main-Class: com.juvenxu.mvnbook.helloworld.HelloWorld
```

​	并且这个jar是可以直接运行的，如下：

```
java -jar hello-world-1.0-SNAPSHOT.jar
test from hello world!

```



## 2.5.使用archetype生成项目骨架

在Maven的约定中：在项目的根目录中放置pom.xml，在src/main/java目录中放置项目的主代码，在src/test/java中放置项目的测试代码，这些项目骨架是我们手动创建的，而Maven是可以自动创建的



如果是Maven3，简单的运行：

```
mvn archetype:generate

#每一个Archetype前面都会有一个编号，同时命令行会提示一个默认的编号，其对应的Archetype为maven-archetype-quickstart，直接回车以选择该Archetype，紧接着Maven会提示输入要创建项目的groupId，artifactId,version，以及包名package，如下输入并确认
```

![](https://github.com/chenyansong1/note/blob/master/images/maven/abc7.png?raw=true)



最终生成的目录结构如下：

![](https://github.com/chenyansong1/note/blob/master/images/maven/abc8.png?raw=true)



# 5.坐标和依赖

* 如何唯一的标识一个maven项目

  ![](https://github.com/chenyansong1/note/blob/master/images/maven/abc9.png?raw=true)

  

* 坐标详解

  maven通过下面的坐标进行唯一的定位一个组件

  1. groupId: groupId的表示方式与Java包名的表示方式类似，通常与域名反向一一对应，如groupId为org.sonatyp.nexus, org.sonatype表示Sonatype公司的网站，nexus是Nexus这一实际项目，改groupId与域名nexus.sonatype.org对应
  2. artifactId：该元素定义实际项目中的一个Maven项目（模块），**推荐的做法是使用实际项目名称作为artifactId的前缀**，比如artifactId是nexus-indexer，使用了实际项目名nexus作为前缀
  3. version：版本
  4. packaging：定义maven项目的打包方式，packaging为jar，最终的文件名为nexus-indexer-2.0.0.jar，而使用war打包方式的maven项目，最终生成的构件会有一个.war文件，不过这不是绝对的，比如packaging=maven-plugin的构件扩展名也是jar，**默认maven使用jar**
  5. classifier：该元素用来帮助定义构建输出的一些附属构件（如：nexus-indexer-2.0.0-javadoc.jar, nexus-indexer-2.0.0-sources.jar)，注意，不能直接定义项目的classifier，因为附属构件不是项目直接默认生成的，而是由附加的插件帮助生成的

  

* 依赖范围

```xml
    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
```

​	如上，依赖是使用的范围，使用scope来定义，首先要介绍三种classpath：

1. 编译classpath
2. 测试classpath
3. 运行classpath

例如上面的JUnit只会加入到**测试classpath**中，不会加入到其他另外两种classpath中

| scope    | description                                                  |
| -------- | ------------------------------------------------------------ |
| compile  | 编译依赖范围，默认就是这种范围，对编译、测试，运行三种classpath都有效 |
| test     | 测试依赖范围，只对测试classpath有效                          |
| provided | 对编译和测试classpath有效，但是对运行classpath无效，如：servlet-api，在编译和测试项目的时候需要改依赖，但是在项目运行的时候，由于容器已经提供，就不需要Maven重复的引入 |
| runtime  |                                                              |
|          |                                                              |



依懒性传递

依赖调解（依赖冲突）

最佳实现

 	1. 排除依赖
 	2. 归类依赖
 	3. 优化依赖

















