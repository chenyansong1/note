登录式shell

​	正常登录某个终端

​		su - username

​		su -l username

​	非登录式shell

​		su username

​		图形终端打开命令窗口

​		自动执行的shell脚本





非登录式shell





全局配置

​	/etc/profile, /etc/profile.d/*.sh, /etc/bashrc

个人配置

​	~/.bash_profile, ~/.bashrc 只对当前用户生效，当全局和个人的配置一样的时候，就以个人为主



profile类的文件

​	设定环境变量

​	运行命令或脚本（如：用户一登录时）



bashrc类的文件

​	设定本地变量

​	定义命令别名（这个好用：当我们的复杂的命令定义成别名来简化我们的工作量）



​	

登录式shell如何读取的配置文件？

​	/etc/profile ->   /etc/profile.d/*.sh  -> ~/.bash_profile  ->  ~/.bashrc	->   /etc/bashrc



非登录式shell如何读取的配置文件？

~/.bashrc -> /etc/bashrc   -> /etc/profile.d/*.sh

**因为非登录式shell没有 profile 的读取，所以在我们使用非登录式切换的时候，就没有对应的环境变量带过去**



我们想在用户登录的时候，输出一句话：vim ~/.bash_profile

![image-20180825162047503](/Users/chenyansong/Documents/note/images/linux/command/profile.png)





