转自：http://lxs647.iteye.com/blog/2084375

今天在写一个磁盘空间满了的时候的报警脚本。 步骤如下：
1.在window下用notepad++写好了脚本
2.通过cygwin上传到centos server 端。
3.然后 chmod u+x test.sh更改其可执行权限。
4.然后又用命令：
```
sh test.sh
```


执行该脚本文件以验证其语法是否正确，没想到问题一大堆：
: command not found: line2:
: command not found: line12:
: command not found: line15:
: command not found: line18:
: command not found: line23:
: command not found: line26:
 
一看上面的这几行，出错的可都是空行啊、怎么空行也报错？于是千搜万搜，总算搜出结果来了，是由于一下原因引起的：
在cygwin下编写shell script是，script在执行的时候，其中包含的空行会提示 /r. command not found错误信息。这是win dos与*nix文本编辑方式不同造成的。可以使用cygwin工具dos2unix将script改为unix格式。
参考这个页面 http://www.tamilramasamy.com/2008/07/r-command-not-found-in-cygwin.html


原文如下：
: $'\r': command not found in Cygwin
If you happen to have this kind of error for no reason, please make sure you convert your shell script file to UNIX format using any text editor like Notepad++. Initially it seems like a weird error but yet another valid issue with Cygwin + XP editor. 

Here is an example, If you create a script in Notepad++ 

-------------------------------------------------------------------
#!/bin/bash
#
# My Cygwin weird error
#
clear
echo "Knowledge is Power"
-------------------------------------------------------------------

When you run this script directly from Cygwin (by editing it from Notepad) you might forget to set the file format to Unix. This often result in following error. 

-------------------------------------------------------------------

./print_a_line.sh: line 1: $'\r': command not found
Knowledge is Power

-------------------------------------------------------------------

Or, 

You can convert them to UNIX format using shell utility dos2unix 
所以最后的解决办法就是：
$ dos2unix test.sh
dos2unix: converting file test.sh to UNIX format ...
$ sh test.sh