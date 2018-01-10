
三. 三种配置环境变量的方法1. 修改/etc/profile文件 
如果你的计算机仅仅作为开发使用时推荐使用这种方法，因为所有用户的shell都有权使用这些环境变量，可能会给系统带来安全性问题。 
·用文本编辑器打开/etc/profile 
·在profile文件末尾加入： 
export JAVA_HOME=/usr/share/jdk1.6.0_14 
export PATH=$JAVA_HOME/bin:$PATH 
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar 

·重新登录 
·注解 
a. 你要将 /usr/share/jdk1.6.0_14改为你的jdk安装目录 
b. linux下用冒号“:”来分隔路径 
c. $PATH / $CLASSPATH / $JAVA_HOME 是用来引用原来的环境变量的值 
在设置环境变量时特别要注意不能把原来的值给覆盖掉了，这是一种 
常见的错误。 
d. CLASSPATH中当前目录“.”不能丢,把当前目录丢掉也是常见的错误。 
e. export是把这三个变量导出为全局变量。 
f. 大小写必须严格区分。 

2. 修改.bash_profile文件 

这种方法更为安全，它可以把使用这些环境变量的权限控制到用户级别，如果你需要给某个用户权限使用这些环境变量，你只需要修改其个人用户主目录下的.bash_profile文件就可以了。 
·用文本编辑器打开用户目录下的.bash_profile文件 
·在.bash_profile文件末尾加入： 

export JAVA_HOME=/usr/share/jdk1.6.0_14 
export PATH=$JAVA_HOME/bin:$PATH 
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar 

·重新登录 

3. 直接在shell下设置变量 
不赞成使用这种方法，因为换个shell，你的设置就无效了，因此这种方法仅仅是临时使用，以后要使用的时候又要重新设置，比较麻烦。 
只需在shell终端执行下列命令： 
export JAVA_HOME=/usr/share/jdk1.6.0_14 
export PATH=$JAVA_HOME/bin:$PATH 
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar 
