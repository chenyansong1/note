git解决冲突

```shell
# 有文件冲突需要合并
$ git pull
error: Your local changes to the following files would be overwritten by merge:
        app/views/terminal.py
        config.ini
        vars/KEYS.py
Please commit your changes or stash them before you merge.  # 这里其实已经提示了
Aborting
Updating 56bb291..a0dabab



# 缓存自己的文件
$ git stash
Saved working directory and index state WIP on master: 56bb291 调整上传资源函数

cys-PC0+Digital@cys-PC0 MINGW64 /f/gd/code/Know_CMDB_Backend (master)

# 重新pull
$ git pull
Updating 56bb291..a0dabab
Fast-forward
 app/views/sky_mirror.py                            |   4 +--
 app/views/source.py                                |  13 +++++--
 app/views/terminal.py                              |   3 +-
 app/views/visual.py                                |  38 ++++++++++++++++++---
 config.ini                                         |   8 +++--
 ...\220\350\241\250\346\250\241\346\235\2771.xlsx" | Bin 0 -> 9536 bytes
 ...2\220\350\241\250\346\250\241\346\235\277.xlsx" | Bin 0 -> 87610 bytes
 ...2\220\350\241\250\346\250\241\346\235\277.xlsx" | Bin 0 -> 9592 bytes
 ...346\250\241\346\235\277.20210331162228313.xlsx" | Bin 0 -> 122744 bytes
 ...\220\350\241\250\346\250\241\346\235\277 .xlsx" | Bin 0 -> 71547 bytes
 ...2\220\350\241\250\346\250\241\346\235\277.xlsx" | Bin 0 -> 11072 bytes
 ...2\220\350\241\250\346\250\241\346\235\277.xlsx" | Bin 0 -> 38412 bytes
 vars/KEYS.py                                       |   2 ++
 13 files changed, 56 insertions(+), 12 deletions(-)
 create mode 100644 "upload/000001-\350\265\204\346\272\220\350\241\250\346\250\241\346\235\2771.xlsx"
 create mode 100644 "upload/000002-000002-\346\242\205\345\267\236\346\200\201\345\212\277\350\265\204\346\272\220\350\241\250\346\250\241\346\235\277.xlsx"
 create mode 100644 "upload/000003-\350\265\204\346\272\220\350\241\250\346\250\241\346\235\277.xlsx"
 create mode 100644 "upload/000006-000006-\350\265\204\346\272\220\350\241\250\346\250\241\346\235\277.20210331162228313.xlsx"
 create mode 100644 "upload/000007-000007-\350\265\204\346\272\220\350\241\250\346\250\241\346\235\277 .xlsx"
 create mode 100644 "upload/000008-000008-\350\265\204\346\272\220\350\241\250\346\250\241\346\235\277.xlsx"
 create mode 100644 "upload/000012-000012-\350\265\204\346\272\220\350\241\250\346\250\241\346\235\277.xlsx"

cys-PC0+Digital@cys-PC0 MINGW64 /f/gd/code/Know_CMDB_Backend (master)


# 将我们自己的缓存和pull的文件merge
$ git stash apply 0
Auto-merging vars/KEYS.py
Auto-merging config.ini
CONFLICT (content): Merge conflict in config.ini   # 这里有冲突的文件，需要自己去文件里面手动解决
Auto-merging app/views/terminal.py

```

总结的步骤如下：

```shell
#缓存冲突的文件
git stash

#重新拉取文件
git pull

#合并冲突
git stash apply 0

#解决还有冲突文件，手动解决

```



其他的步骤如下：

```shell
git pull----------------从远程仓库拉取代码到本地,如果出现冲突
git stash---------------将你修改后的代码存储到本地(一个栈结构)-->一般会在git pull 拉取代码失败时使用
git stash pop-----------将你stash到本地的代码与重新git pull下的代码合并
git add XXX-------------将XXX文件加入到暂存区
git commit -m "注释"----将暂存区的文件提交到本地仓库
git push----------------将本地仓库的内容推送到远程仓库
```

> 之所以会有这样的效果是因为我在git pull代码之前就从来没有进行过一次git commit.



参见：

https://blog.csdn.net/nrsc272420199/article/details/85219097

