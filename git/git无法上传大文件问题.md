[toc]



## Getting Started

0. 下载

   官网：https://git-lfs.github.com/

1. [Download](https://github.com/git-lfs/git-lfs/releases/download/v2.11.0/git-lfs-windows-v2.11.0.exe) and install the Git command line extension. Once downloaded and installed, set up Git LFS for your user account by running:

   ```
   git lfs install
   ```

   You only need to run this once per user account.

2. In each Git repository where you want to use Git LFS, select the file types you'd like Git LFS to manage (or directly edit your .gitattributes). You can configure additional file extensions at anytime.

   ```
   git lfs track "*.psd"
   ```

   Now make sure .gitattributes is tracked:

   ```
   git add .gitattributes
   ```

   Note that defining the file types Git LFS should track will not, by itself, convert any pre-existing files to Git LFS, such as files on other branches or in your prior commit history. To do that, use the [git lfs migrate[1\]](https://github.com/git-lfs/git-lfs/blob/master/docs/man/git-lfs-migrate.1.ronn?utm_source=gitlfs_site&utm_medium=doc_man_migrate_link&utm_campaign=gitlfs) command, which has a range of options designed to suit various potential use cases.

3. There is no step three. Just commit and push to GitHub as you normally would.

   ```
   git add file.psd
   git commit -m "Add design file"
   git push origin master
   ```



4. lfs的常见操作

   ```c
   常用命令
   
   git lfs help // 查看git lfs的帮助
   
   git lfs version  // 查看git lfs的版本号
   
   git lfs track // 查看git lfs的文件追踪信息
   
   git lfs track '*.dll' // dll文件用lfs来管理，会在根目录的.gitattributes文件中添加：*.dll filter=lfs diff=lfs merge=lfs -text
   
   git lfs track "*.a" "*.dylib" "*.so" "*.lib" "*.dll"  // a、dylib、so、lib、dll文件用lfs来管理，会在根目录的.gitattributes文件中添加
   
   *.dylib filter=lfs diff=lfs merge=lfs -text
   *.so filter=lfs diff=lfs merge=lfs -text
   *.lib filter=lfs diff=lfs merge=lfs -text
   *.dll filter=lfs diff=lfs merge=lfs -text
   *.a filter=lfs diff=lfs merge=lfs -text
   
   git lfs track 'Guid.upk' // Guid.upk文件用lfs来管理，会在根目录的.gitattributes文件中添加：Guid.upk filter=lfs diff=lfs merge=lfs -text
   
   git lfs track 'maps/*' // 根目录下maps文件夹中的所有文件用lfs来管理，会在根目录的.gitattributes文件中添加：maps/* filter=lfs diff=lfs merge=lfs -text
   
   git lfs untrack 'Guid.upk' // Guid.upk文件不再使用lfs来管理
   
   git lfs status  // 查看当前git lfs对象的状态
   
   git lfs ls-files  // 查看当前哪些文件是使用lfs管理的
   
   git lfs clone https://github.com/kekec/Test.git // 克隆包含Git LFS的远程仓库到本地
   
   git lfs env  // 查看环境信息
   
    
   ```

   

参考：

https://www.cnblogs.com/nfuquan/p/12325159.html

一些错误，参见：

https://www.jianshu.com/p/d53b34e7d28b

lfs的api操作：

https://www.cnblogs.com/kekec/p/10556189.html