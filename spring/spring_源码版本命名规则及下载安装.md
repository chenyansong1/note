[TOC]



# 首先看看某些常见软件的版本号

 Linux Kernel: 0.0.1,1.0.0,2.6.32,3.0.18...,若用 X.Y.Z 表示，则偶数 Y 表示稳定版本，奇 数 Y 表示开发版本。 

Windows: Windows 98,Windows 2000,Windows xp,Windows 7...,最大的特点是杂乱无章，毫无规 律。
 SSH Client: 0.9.8。
 OpenStack: 2014.1.3,2015.1.1.dev8。 从上可以看出，不同的软件版本号风格各异，随着系统的规模越大，依赖的软件越多，如果这些软件没 有遵循一套规范的命名风格，容易造成 Dependency Hell。所以当我们发布版本时，版本号的命名需 要遵循某种规则，其中 Semantic Versioning 2.0.0 定义了一套简单的规则及条件来约束版本号的 配置和增长。本文根据 Semantic Versionning 2.0.0 和 Semantic Versioning 3.0.0 选择性的 整理出版本号命名规则指南。 

# 版本号命名规则指南

 版本号的格式为 X.Y.Z(又称 Major.Minor.Patch)，递增的规则为: 

X 表示主版本号，当 API 的兼容性变化时，X 需递增。
 Y 表示次版本号，当增加功能时(不影响 API 的兼容性)，Y 需递增。 Z 表示修订号，当做 Bug 修复时(不影响 API 的兼容性)，Z 需递增。 

详细的规则如下:
 X, Y, Z 必须为非负整数，且不得包含前导零，必须按数值递增，如 1.9.0 -> 1.10.0 -> 1.11.0 0.Y.Z 的版本号表明软件处于初始开发阶段，意味着 API 可能不稳定;1.0.0 表明版本已有稳定 

的 API。
 当 API 的兼容性变化时，X 必须递增，Y 和 Z 同时设置为 0;当新增功能(不影响 API 的兼容 

性)或者 API 被标记为 Deprecated 时，Y 必须递增，同时 Z 设置为 0;当进行 bug fix 时，Z 必 须递增。 

先行版本号(Pre-release)意味该版本不稳定，可能存在兼容性问题，其格式为:X.Y.Z.[a-c][正 整数]，如 1.0.0.a1，1.0.0.b99，1.0.0.c1000。 

开发版本号常用于 CI-CD，格式为 X.Y.Z.dev[正整数]，如 1.0.1.dev4。 

版本号的排序规则为依次比较主版本号、次版本号和修订号的数值，如 1.0.0 < 1.0.1 < 1.1.1 < 2.0.0;对于先行版本号和开发版本号，有:1.0.0.a100 < 1.0.0，2.1.0.dev3 < 2.1.0;当存 在字母时，以 ASCII 的排序来比较，如 1.0.0.a1 < 1.0.0.b1。 

  注意:版本一经发布，不得修改其内容，任何修改必须在新版本发布!



# 一些修饰的词



* Snapshot: 版本代表不稳定、尚处于开发中的版本 
* Alpha: 内部版本 
* Beta: 测试版
* Demo: 演示版
* Enhance: 增强版
* Free: 自由版
* Full Version: 完整版，即正式版 LTS: 长期维护版本
* Release: 发行版
* RC: 即将作为正式版发布 
* Standard: 标准版 
* Ultimate: 旗舰版 
* Upgrade: 升级版 



> Release 版本则代表稳定的版本  
>
> GA 版本则代表广泛可用的稳定版(General Availability)  
>
> M 版本则代表里程碑版(M 是 Milestone 的意思)具有一些全新的功能或是具有里程碑意义的版本。 
>
> RC 版本即将作为正式版发布  



# Spring5 源码下载及导入idea



第一步: https://github.com/spring-projects/spring-framework/archive/v5.0.2.RELEASE.zip 

第二步:下载 gradle  http://downloads.gradle.org/distributions/gradle-1.6-bin.zip  

第三步:解压,配置 GRADLE_HOME 和 Path  

第四步:验证 gradle -v，环境变量是否正确  (mac使用 ~/.bash_profile)

第五步:点击 gradlew.bat 构建项目 (Mac 使用 sparing-src/gradlew)

上述过程可能需要翻墙



最后的截图如下

![image-20180728181317101](/Users/chenyansong/Documents/note/images/spring/spring-install.png)

第六步:点击 import-into-eclipse.bat （Mac环境下，执行 ./import-into-eclipse.sh ）,此时需要按五次回车，最后成功如下图：



![image-20180728182715345](/Users/chenyansong/Documents/note/images/spring/spring-install2.png)



然后就可以在idea中打开了





