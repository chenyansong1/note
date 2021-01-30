1. 第一种方法

```

git fetch --all
git reset --hard origin/master
git fetch下载远程最新的， 然后，git reset master分支重置
```

2.  第二种方法

```
git reset --hard HEAD
git pull
```
