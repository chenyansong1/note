1.IDEA Terminal替换成Git Bash
在IDEA中，打开settings，设置相应的bash路径 
settings–>Tools–>Terminal–>Shell path:C:\Program Files\Git\bin\bash.exe

2.解决git commit注释乱码的问题
在C:\Program Files\Git\etc\bash.bashrc末尾行追加如下内容：

export LANG="zh_CN.UTF-8"
export LC_ALL="zh_CN.UTF-8"

3.重启
重启IDEA或者关闭当前Terminal的session连接，然后New Session连接。

