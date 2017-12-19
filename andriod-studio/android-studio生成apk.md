android-studio生成apk的步骤如下：

* 1.在Android studio的菜单栏上点击“Build”-->“Generate Signed APK”,在"Key store path:"右侧的框里输入“D:\Key.store”,   这里路径不能在C盘上，如“C:\Key.store”,这样导出apk会出现错误,可以是其它盘。    

* 2.点击“Create new...”，"Key store path:"右侧框的内容同样是“D:\Key.store”。  Password:右侧框输入“123456”，这里密码随便输入，“confirm:"右侧框也输入"123456"，在Alias内随便输入内容，如“hello”。下面的第二个Password:右侧框输入“123456”，下面的第二个“confirm:"右侧框也输入"123456"。 

* 3.在Certificate下面的内容中选一个"City or Locality"，在它的右侧框输入“beijing”,点击"OK",在“Generate Signed APK”点击“OK”，再点击“finish”就导出apk，此时apk已经在某个文件夹下了，但具体在哪个文件下不好找。   

* 4.在当前文件的右上角有提示“Generate Signed APK APK(s)generated successfully.Show in Explorer”，Show in Explorer是蓝色的，点击“Show in Explorer”就可以进入一个名为app文件夹下，可以看到“app-release.apk”,这个apk文件可以在真机上安装。


参考：

http://blog.csdn.net/BianHuanShiZhe/article/details/73433877