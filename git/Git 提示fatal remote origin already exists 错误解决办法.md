# Git 提示fatal: remote origin already exists 错误解决办法 

解决办法如下

* 1.先删除远程 Git 仓库
```
    $ git remote rm origin
```

* 2.再添加远程 Git 仓库
```
    $ git remote add origin git@github.com:belongtocys/notebook
```

* 3.如果执行 git remote rm origin 报错的话，我们可以手动修改gitconfig文件的内容

```
    $ vi .git/config
```

把 [remote “origin”] 那一行删掉就好了。

```
$ cat .git/config
[core]
        repositoryformatversion = 0
        filemode = false
        bare = false
        logallrefupdates = true
        symlinks = false
        ignorecase = true
        hideDotFiles = dotGitOnly
[branch "master"]
[remote "origin"]
        url = https://github.com/belongtocys/notebook.git
        fetch = +refs/heads/*:refs/remotes/origin/*
[branch "master"]
        remote = origin
        merge = refs/heads/master

Administrator@PC238 MINGW64 /e/NOTE (master)
```
