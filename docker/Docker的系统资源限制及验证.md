[TOC]

https://docs.docker.com/config/containers/resource_constraints/

默认情况下，一个容器是没有任何资源限制的，这样就可能发生OOM，一旦发生OOM，任何进程都有可能被杀死，包括docker daemon自身，为此Docker特地调整了docker daemon的OOM优先级，避免他被内核“正法”，但容器的优先级并未被调整

OOM_adj调整分数

容器的策略

# 限制容器的内存资源

![1563430395256](E:\git-workspace\note\images\docker\1563430395256.png)

```shell
#ram(物理内存)
-m, or --memory= 4m #4g
#需要先设置-m ,swap（交换内存）
--memory-swap * 

#不会被kill
--oom-kill-disable
```

![1563430545193](E:\git-workspace\note\images\docker\1563430545193.png)



# CPU资源限制

默认是没有限制的，CFS调度器（完全公平调度器），从docker1.13之后，能够配置实时调度

![1563431243746](E:\git-workspace\note\images\docker\1563431243746.png)

```shell
#按比率切分CPU资源
--cpu-shares
#限制CPU最多使用多少个核心，1.5表示分配一个CPU+50%的另一个CPU
--cpus=<value> 

#指定进程只能运行在哪个CPU核心上
--cpuset-cpus
```

```shell
docker run --name stress -it --rm -m 256m --cpu 2 --cpuset-cpus 0,2 --cpu-shares 1024 lorel/docker-stress

docker run --name stress -it --rm --cpu-shares 1024 lorel/docker-stress
docker run --name stress -it --rm --cpu-shares 512 lorel/docker-stress
```



查看top参数

```shell
[root@spark02 img3]# docker stats
CONTAINER ID        NAME                 CPU %               MEM USAGE / LIMIT     MEM %               NET I/O             BLOCK I/O           PIDS
007a606eb1dc        nginx                0.00%               3.055MiB / 7.543GiB   0.04%               2.37kB / 589B       11MB / 0B           0
09d58fe6f743        harbor-jobservice    0.17%               5.242MiB / 7.543GiB   0.07%               44.2kB / 441kB      17MB / 0B           0
a4f19aa03c1f        harbor-ui            0.00%               6.621MiB / 7.543GiB   0.09%               10.9kB / 8.62kB     22.9MB / 0B         0
8a241bdb7c28        harbor-db            0.02%               98.24MiB / 7.543GiB   1.27%               40.9kB / 76.7kB     60.2MB / 25.2MB     0
8bab04f10afa        redis                0.12%               6.762MiB / 7.543GiB   0.09%               441kB / 40.4kB      9.3MB / 0B          0
18f399ec7d7f        registry             0.00%               7.422MiB / 7.543GiB   0.10%               3.03kB / 718B       22.7MB / 0B         0
0a729efe88b0        harbor-adminserver   0.00%               4.953MiB / 7.543GiB   0.06%               74.2kB / 42.7kB     17.8MB / 0B         0
276c6877b08e        harbor-log           0.00%               3.91MiB / 7.543GiB    0.05%               22.8kB / 6.5kB      10MB / 0B           0
^C
[root@spark02 img3]# docker stats nginx
CONTAINER ID        NAME                CPU %               MEM USAGE / LIMIT     MEM %               NET I/O             BLOCK I/O           PIDS
007a606eb1dc        nginx               0.00%               3.055MiB / 7.543GiB   0.04%               2.37kB / 589B       11MB / 0B           0
```



