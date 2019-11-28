[TOC]

转自：http://www.ruanyifeng.com/blog/2016/03/systemd-tutorial-commands.html



# systemd的介绍



使用了 Systemd，就不需要再用init了。Systemd 取代了initd，成为系统的第一个进程（PID 等于 1），其他进程都是它的子进程。

```
#查看 Systemd 的版本
systemctl --version
```



## 1.systemd新特性

* 系统引导时实现服务并行启动
* 按需激活进程
* 系统状态快照
* 基于依赖关系定义服务控制逻辑




## 2.Systemd下的unit文件

Unit文件专门用于systemd下控制资源，这些资源包括服务(service)、套接字(socket)、设备(device)、挂载点(mount point)、自动挂载点(automount point)、交换文件或分区(a swap file or partition)…
所有的unit文件都应该配置[Unit]或者[Install]段.由于通用的信息在[Unit]和[Install]中描述，每一个unit应该有一个指定类型段，例如[Service]来对应后台服务类型unit.



unit 类型如下：

* service ：守护进程的启动、停止、重启和重载是此类 unit 中最为明显的几个类型。
* socket ：此类 unit 封装系统和互联网中的一个socket。当下，systemd支持流式, 数据报和连续包的AF\_INET,AF\_INET6,AF\_UNIXsocket 。也支持传统的 FIFOs 传输模式。每一个 socket unit 都有一个相应的服务 unit 。相应的服务在第一个“连接”进入 socket 或 FIFO 时就会启动(例如：nscd.socket 在有新连接后便启动 nscd.service)。
* device ：此类 unit 封装一个存在于 Linux设备树中的设备。每一个使用 udev 规则标记的设备都将会在 systemd 中作为一个设备 unit 出现。udev 的属性设置可以作为配置设备 unit 依赖关系的配置源。
* mount ：此类 unit 封装系统结构层次中的一个挂载点。
* automount ：此类 unit 封装系统结构层次中的一个自挂载点。每一个自挂载 unit 对应一个已挂载的挂载 unit (需要在自挂载目录可以存取的情况下尽早挂载)。
* target ：此类 unit 为其他 unit 进行逻辑分组。它们本身实际上并不做什么，只是引用其他 unit 而已。这样便可以对 unit 做一个统一的控制。(例如：multi-user.target 相当于在传统使用 SysV 的系统中运行级别5)；bluetooth.target 只有在蓝牙适配器可用的情况下才调用与蓝牙相关的服务，如：bluetooth 守护进程、obex 守护进程等）
* snapshot ：与 targetunit 相似，快照本身不做什么，唯一的目的就是引用其他 unit 。



## 3.认识service的unit文件

扩展名：.service

路径：

     /etc/systemd/system/*     ――――  供系统管理员和用户使用
    
      /run/systemd/system/*     ――――  运行时配置文件
    
      /usr/lib/systemd/system/*   ――――  安装程序使用（如RPM包安装）

 




# systemd的一些命令

Systemd 并不是一个命令，而是一组命令，涉及到系统管理的方方面面。



## systemctl 系统管理(开关机等)

systemctl是 Systemd 的主命令，用于管理系统。

```
# 重启系统
$ sudo systemctl reboot

# 关闭系统，切断电源
$ sudo systemctl poweroff

# CPU停止工作
$ sudo systemctl halt

# 暂停系统
$ sudo systemctl suspend

# 让系统进入冬眠状态
$ sudo systemctl hibernate

# 让系统进入交互式休眠状态
$ sudo systemctl hybrid-sleep

# 启动进入救援状态（单用户状态）
$ sudo systemctl rescue

#关机
systemctl halt
systemctl poweroff

#重启
systemctl reboot

#挂起系统
systemctl suspend

#快照
systemctl hibernate

#快照并挂起
systemctl hybird-sleep
```



## 查看服务的状态

```
# 列出正在运行的 Unit
$ systemctl list-units

# 列出所有Unit，包括没有找到配置文件的或者启动失败的
$ systemctl list-units --all

# 列出所有没有运行的 Unit
$ systemctl list-units --all --state=inactive

# 列出所有加载失败的 Unit
$ systemctl list-units --failed

# 列出所有正在运行的、类型为 service 的 Unit
$ systemctl list-units --type=service


```

## 启动和停止

```
# 立即启动一个服务
$ sudo systemctl start apache.service

# 立即停止一个服务
$ sudo systemctl stop apache.service

# 重启一个服务
$ sudo systemctl restart apache.service

# 杀死一个服务的所有子进程
$ sudo systemctl kill apache.service

# 重新加载一个服务的配置文件
$ sudo systemctl reload apache.service

# 重载所有修改过的配置文件
$ sudo systemctl daemon-reload

# 显示某个 Unit 的所有底层参数
$ systemctl show httpd.service

# 显示某个 Unit 的指定属性的值
$ systemctl show -p CPUShares httpd.service

# 设置某个 Unit 的指定属性
$ sudo systemctl set-property httpd.service CPUShares=500
```



管理系统服务



````
#查看服务是否启动    
systemctl is-active name.service

#只查看unit为Service的，有哪些处于活动状态
systemctl list-units --type service 

#查看所有的unit为Service的服务，包括已经激活和为激活的
systemctl list-units --type service --all
````



```
#centos6
chkconfig --list #查看所有
chkconfig --list name	#查看某一个服务
chkconfig name on 

#centos7 开机自动启动
#查看是否开机自启：systemctl status name.service  中的loaded的状态
#设置开机自启
systemctl enable name.service 
#关闭自启
systemctl disable name.service

#查看所有的服务是否开机自启
systemctl list-unit-files --type service

systemctl is-enabled name.service



```



## 模拟centos6的服务级别

target units 模拟实现运行级别

```
0 ==> runlevel0.target, poweroff.target
1 ==> runlevel1.target, rescue.target	#单用户模式
2 ==> runlevel2.target, multi-user.target	#和3级别一样
3 ==> runlevel3.target, multi-user.target	#同上
4 ==> runlevel3.target, multi-user.target	#同上
5 ==> runlevel5.target, graphical.target	#图形
6 ==> runlevel6.target, reboot.target		#


#切换运行级别
init N ==> systemctl isolate name.target

#查看级别
runlevel ==> systemctl list-units --type target

#查看默认运行级别
/etc/inittab  ==> systemctl get-default

#修改默认级别
/etc/inittab ==> systemctl set-default name.target

#切换至紧急救援模式
systemctl rescue

#切换至emergency模式
systemctl emergency

```





# systemd的配置文件

## 概述

每一个 Unit 都有一个配置文件，告诉 Systemd 怎么启动这个 Unit 。 
Systemd 默认从目录/etc/systemd/system/读取配置文件。但是，里面存放的大部分文件都是符号链接，指向目录/usr/lib/systemd/system/，真正的配置文件存放在那个目录。 
systemctl enable命令用于在上面两个目录之间，建立符号链接关系。

```
$ sudo systemctl enable clamd@scan.service
# 等同于
$ sudo ln -s '/usr/lib/systemd/system/clamd@scan.service' '/etc/systemd/system/multi-user.target.wants/clamd@scan.service'

```

如果配置文件里面设置了开机启动，systemctl enable命令相当于激活开机启动。 
与之对应的，systemctl disable命令用于在两个目录之间，撤销符号链接关系，相当于撤销开机启动

```
$ sudo systemctl disable clamd@scan.service
```

配置文件的后缀名，就是该 Unit 的种类，比如sshd.socket。如果省略，Systemd 默认后缀名为.service，所以sshd会被理解成sshd.service。



## 配置文件的状态

systemctl list-unit-files命令用于列出所有配置文件。

```
# 列出所有配置文件
$ systemctl list-unit-files

# 列出指定类型的配置文件
$ systemctl list-unit-files --type=service
```



这个命令会输出一个列表。

```
$ systemctl list-unit-files

UNIT FILE              STATE
chronyd.service        enabled
clamd@.service         static
clamd@scan.service     disabled

#这个列表显示每个配置文件的状态，一共有四种。
enabled：已建立启动链接
disabled：没建立启动链接
static：该配置文件没有[Install]部分（无法执行），只能作为其他配置文件的依赖
masked：该配置文件被禁止建立启动链接

```

注意，从配置文件的状态无法看出，该 Unit 是否正在运行。这必须执行前面提到的systemctl status命令。

```
$ systemctl status bluetooth.service
```

一旦修改配置文件，就要让 SystemD 重新加载配置文件，然后重新启动，否则修改不会生效。

```
$ sudo systemctl daemon-reload
$ sudo systemctl restart httpd.service
```



## 配置文件的格式



配置文件就是普通的文本文件，可以用文本编辑器打开。 
systemctl cat命令可以查看配置文件的内容。

```
$ systemctl cat atd.service

[Unit]
Description=ATD daemon

[Service]
Type=forking
ExecStart=/usr/bin/atd

[Install]
WantedBy=multi-user.target
```

从上面的输出可以看到，配置文件分成几个区块。每个区块的第一行，是用方括号表示的区别名，比如[Unit]。注意，配置文件的区块名和字段名，都是大小写敏感的。 
每个区块内部是一些等号连接的键值对。注意，键值对的等号两侧不能有空格。



## 配置文件的区块

[Unit]区块通常是配置文件的第一个区块，用来定义 Unit 的元数据，以及配置与其他 Unit 的关系。它的主要字段如下。

```
Description：简短描述
Documentation：文档地址
Requires：当前 Unit 依赖的其他 Unit，如果它们没有运行，当前 Unit 会启动失败
Wants：与当前 Unit 配合的其他 Unit，如果它们没有运行，当前 Unit 不会启动失败
BindsTo：与Requires类似，它指定的 Unit 如果退出，会导致当前 Unit 停止运行
Before：如果该字段指定的 Unit 也要启动，那么必须在当前 Unit 之后启动
After：如果该字段指定的 Unit 也要启动，那么必须在当前 Unit 之前启动
Conflicts：这里指定的 Unit 不能与当前 Unit 同时运行
Condition...：当前 Unit 运行必须满足的条件，否则不会运行
Assert...：当前 Unit 运行必须满足的条件，否则会报启动失败
[Install]通常是配置文件的最后一个区块，用来定义如何启动，以及是否开机启动。它的主要字段如下。
WantedBy：它的值是一个或多个 Target，当前 Unit 激活时（enable）符号链接会放入/etc/systemd/system目录下面以 Target 名 + .wants后缀构成的子目录中
RequiredBy：它的值是一个或多个 Target，当前 Unit 激活时，符号链接会放入/etc/systemd/system目录下面以 Target 名 + .required后缀构成的子目录中
Alias：当前 Unit 可用于启动的别名
Also：当前 Unit 激活（enable）时，会被同时激活的其他 Unit123456789101112131415
```

[Service]区块用来 Service 的配置，只有 Service 类型的 Unit 才有这个区块。它的主要字段如下。

```
Type：定义启动时的进程行为。它有以下几种值。
Type=simple：默认值，执行ExecStart指定的命令，启动主进程
Type=forking：以 fork 方式从父进程创建子进程，创建后父进程会立即退出
Type=oneshot：一次性进程，Systemd 会等当前服务退出，再继续往下执行
Type=dbus：当前服务通过D-Bus启动
Type=notify：当前服务启动完毕，会通知Systemd，再继续往下执行
Type=idle：若有其他任务执行完毕，当前服务才会运行
ExecStart：启动当前服务的命令
ExecStartPre：启动当前服务之前执行的命令
ExecStartPost：启动当前服务之后执行的命令
ExecReload：重启当前服务时执行的命令
ExecStop：停止当前服务时执行的命令
ExecStopPost：停止当其服务之后执行的命令
RestartSec：自动重启当前服务间隔的秒数
Restart：定义何种情况 Systemd 会自动重启当前服务，可能的值包括always（总是重启）、on-success、on-failure、on-abnormal、on-abort、on-watchdog
TimeoutSec：定义 Systemd 停止当前服务之前等待的秒数
Environment：指定环境变量1234567891011121314151617
```

Unit 配置文件的完整字段清单，请参考官方文档。





# example实战



## 一、开机启动

对于那些支持 Systemd 的软件，安装的时候，会自动在`/usr/lib/systemd/system`目录添加一个配置文件。

如果你想让该软件开机启动，就执行下面的命令（以`httpd.service`为例）。

> ```bash
> $ sudo systemctl enable httpd
> ```

上面的命令相当于在`/etc/systemd/system`目录添加一个符号链接，指向`/usr/lib/systemd/system`里面的`httpd.service`文件。

这是因为开机时，`Systemd`只执行`/etc/systemd/system`目录里面的配置文件。这也意味着，如果把修改后的配置文件放在该目录，就可以达到覆盖原始配置的效果。

## 二、启动服务

设置开机启动以后，软件并不会立即启动，必须等到下一次开机。如果想现在就运行该软件，那么要执行`systemctl start`命令。

> ```bash
> $ sudo systemctl start httpd
> ```

执行上面的命令以后，有可能启动失败，因此要用`systemctl status`命令查看一下该服务的状态。

> ```bash
> $ sudo systemctl status httpd
> 
> httpd.service - The Apache HTTP Server
>    Loaded: loaded (/usr/lib/systemd/system/httpd.service; enabled)
>    Active: active (running) since 金 2014-12-05 12:18:22 JST; 7min ago
>  Main PID: 4349 (httpd)
>    Status: "Total requests: 1; Current requests/sec: 0; Current traffic:   0 B/sec"
>    CGroup: /system.slice/httpd.service
>            ├─4349 /usr/sbin/httpd -DFOREGROUND
>            ├─4350 /usr/sbin/httpd -DFOREGROUND
>            ├─4351 /usr/sbin/httpd -DFOREGROUND
>            ├─4352 /usr/sbin/httpd -DFOREGROUND
>            ├─4353 /usr/sbin/httpd -DFOREGROUND
>            └─4354 /usr/sbin/httpd -DFOREGROUND
> 
> 12月 05 12:18:22 localhost.localdomain systemd[1]: Starting The Apache HTTP Server...
> 12月 05 12:18:22 localhost.localdomain systemd[1]: Started The Apache HTTP Server.
> 12月 05 12:22:40 localhost.localdomain systemd[1]: Started The Apache HTTP Server.
> ```

上面的输出结果含义如下。

> - `Loaded`行：配置文件的位置，是否设为开机启动
> - `Active`行：表示正在运行
> - `Main PID`行：主进程ID
> - `Status`行：由应用本身（这里是 httpd ）提供的软件当前状态
> - `CGroup`块：应用的所有子进程
> - 日志块：应用的日志

## 三、停止服务

终止正在运行的服务，需要执行`systemctl stop`命令。

> ```bash
> $ sudo systemctl stop httpd.service
> ```

有时候，该命令可能没有响应，服务停不下来。这时候就不得不"杀进程"了，向正在运行的进程发出`kill`信号。

> ```bash
> $ sudo systemctl kill httpd.service
> ```

此外，重启服务要执行`systemctl restart`命令。

> ```bash
> $ sudo systemctl restart httpd.service
> ```



## 四、读懂配置文件

一个服务怎么启动，完全由它的配置文件决定。下面就来看，配置文件有些什么内容。

前面说过，配置文件主要放在`/usr/lib/systemd/system`目录，也可能在`/etc/systemd/system`目录。找到配置文件以后，使用文本编辑器打开即可。

`systemctl cat`命令可以用来查看配置文件，下面以`sshd.service`文件为例，它的作用是启动一个 SSH 服务器，供其他用户以 SSH 方式登录。

> ```bash
> $ systemctl cat sshd.service
> 
> [Unit]
> Description=OpenSSH server daemon
> Documentation=man:sshd(8) man:sshd_config(5)
> After=network.target sshd-keygen.service
> Wants=sshd-keygen.service
> 
> [Service]
> EnvironmentFile=/etc/sysconfig/sshd
> ExecStart=/usr/sbin/sshd -D $OPTIONS
> ExecReload=/bin/kill -HUP $MAINPID
> Type=simple
> KillMode=process
> Restart=on-failure
> RestartSec=42s
> 
> [Install]
> WantedBy=multi-user.target
> ```

可以看到，配置文件分成几个区块，每个区块包含若干条键值对。

下面依次解释每个区块的内容。

## 五、 [Unit] 区块：启动顺序与依赖关系。

`Unit`区块的`Description`字段给出当前服务的简单描述，`Documentation`字段给出文档位置。

接下来的设置是启动顺序和依赖关系，这个比较重要。

> `After`字段：表示如果`network.target`或`sshd-keygen.service`需要启动，那么`sshd.service`应该在它们之后启动。

相应地，还有一个`Before`字段，定义`sshd.service`应该在哪些服务之前启动。

注意，`After`和`Before`字段只涉及启动顺序，不涉及依赖关系。

举例来说，某 Web 应用需要 postgresql 数据库储存数据。在配置文件中，它只定义要在 postgresql 之后启动，而没有定义依赖 postgresql 。上线后，由于某种原因，postgresql 需要重新启动，在停止服务期间，该 Web 应用就会无法建立数据库连接。

设置依赖关系，需要使用`Wants`字段和`Requires`字段。

> `Wants`字段：表示`sshd.service`与`sshd-keygen.service`之间存在"弱依赖"关系，即如果"sshd-keygen.service"启动失败或停止运行，不影响`sshd.service`继续执行。

`Requires`字段则表示"强依赖"关系，即如果该服务启动失败或异常退出，那么`sshd.service`也必须退出。

注意，`Wants`字段与`Requires`字段只涉及依赖关系，与启动顺序无关，默认情况下是同时启动的。



## 六、[Service] 区块：启动行为

`Service`区块定义如何启动当前服务。

### 6.1 启动命令

许多软件都有自己的环境参数文件，该文件可以用`EnvironmentFile`字段读取。



> `EnvironmentFile`字段：指定当前服务的环境参数文件。该文件内部的`key=value`键值对，可以用`$key`的形式，在当前配置文件中获取。

上面的例子中，sshd 的环境参数文件是`/etc/sysconfig/sshd`。

配置文件里面最重要的字段是`ExecStart`。

> `ExecStart`字段：定义启动进程时执行的命令。

上面的例子中，启动`sshd`，执行的命令是`/usr/sbin/sshd -D $OPTIONS`，其中的变量`$OPTIONS`就来自`EnvironmentFile`字段指定的环境参数文件。



与之作用相似的，还有如下这些字段。

> - `ExecReload`字段：重启服务时执行的命令
> - `ExecStop`字段：停止服务时执行的命令
> - `ExecStartPre`字段：启动服务之前执行的命令
> - `ExecStartPost`字段：启动服务之后执行的命令
> - `ExecStopPost`字段：停止服务之后执行的命令

请看下面的例子。

> ```bash
> [Service]
> ExecStart=/bin/echo execstart1
> ExecStart=
> ExecStart=/bin/echo execstart2
> ExecStartPost=/bin/echo post1
> ExecStartPost=/bin/echo post2
> ```



上面这个配置文件，第二行`ExecStart`设为空值，等于取消了第一行的设置，运行结果如下。

> ```bash
> execstart2
> post1
> post2
> ```



所有的启动设置之前，都可以加上一个连词号（`-`），表示"抑制错误"，即发生错误的时候，不影响其他命令的执行。比如，`EnvironmentFile=-/etc/sysconfig/sshd`（注意等号后面的那个连词号），就表示即使`/etc/sysconfig/sshd`文件不存在，也不会抛出错误。



### 6.2 启动类型

`Type`字段定义启动类型。它可以设置的值如下。

> - simple（默认值）：`ExecStart`字段启动的进程为主进程
> - forking：`ExecStart`字段将以`fork()`方式启动，此时父进程将会退出，子进程将成为主进程
> - oneshot：类似于`simple`，但只执行一次，Systemd 会等它执行完，才启动其他服务
> - dbus：类似于`simple`，但会等待 D-Bus 信号后启动
> - notify：类似于`simple`，启动结束后会发出通知信号，然后 Systemd 再启动其他服务
> - idle：类似于`simple`，但是要等到其他任务都执行完，才会启动该服务。一种使用场合是为让该服务的输出，不与其他服务的输出相混合

下面是一个`oneshot`的例子，笔记本电脑启动时，要把触摸板关掉，配置文件可以这样写。



> ```bash
> [Unit]
> Description=Switch-off Touchpad
> 
> [Service]
> Type=oneshot
> ExecStart=/usr/bin/touchpad-off
> 
> [Install]
> WantedBy=multi-user.target
> ```



上面的配置文件，启动类型设为`oneshot`，就表明这个服务只要运行一次就够了，不需要长期运行。

如果关闭以后，将来某个时候还想打开，配置文件修改如下。

```bash
[Unit]
Description=Switch-off Touchpad

[Service]
Type=oneshot
ExecStart=/usr/bin/touchpad-off start
ExecStop=/usr/bin/touchpad-off stop
RemainAfterExit=yes

[Install]
WantedBy=multi-user.target
```



上面配置文件中，`RemainAfterExit`字段设为`yes`，表示进程退出以后，服务仍然保持执行。这样的话，一旦使用`systemctl stop`命令停止服务，`ExecStop`指定的命令就会执行，从而重新开启触摸板。

### 6.3 重启行为

`Service`区块有一些字段，定义了重启行为。

> `KillMode`字段：定义 Systemd 如何停止 sshd 服务。

上面这个例子中，将`KillMode`设为`process`，表示只停止主进程，不停止任何sshd 子进程，即子进程打开的 SSH session 仍然保持连接。这个设置不太常见，但对 sshd 很重要，否则你停止服务的时候，会连自己打开的 SSH session 一起杀掉。

`KillMode`字段可以设置的值如下。

- control-group（默认值）：当前控制组里面的所有子进程，都会被杀掉
- process：只杀主进程
- mixed：主进程将收到 SIGTERM 信号，子进程收到 SIGKILL 信号
- none：没有进程会被杀掉，只是执行服务的 stop 命令。



接下来是`Restart`字段。

> `Restart`字段：定义了 sshd 退出后，Systemd 的重启方式。

上面的例子中，`Restart`设为`on-failure`，表示任何意外的失败，就将重启sshd。如果 sshd 正常停止（比如执行`systemctl stop`命令），它就不会重启。

`Restart`字段可以设置的值如下。

> - no（默认值）：退出后不会重启
> - on-success：只有正常退出时（退出状态码为0），才会重启
> - on-failure：非正常退出时（退出状态码非0），包括被信号终止和超时，才会重启
> - on-abnormal：只有被信号终止和超时，才会重启
> - on-abort：只有在收到没有捕捉到的信号终止时，才会重启
> - on-watchdog：超时退出，才会重启
> - always：不管是什么退出原因，总是重启



最后是`RestartSec`字段。

> `RestartSec`字段：表示 Systemd 重启服务之前，需要等待的秒数。上面的例子设为等待42秒。



## 七、[Install] 区块

`Install`区块，定义如何安装这个配置文件，即怎样做到开机启动。

> `WantedBy`字段：表示该服务所在的 Target。

`Target`的含义是服务组，表示一组服务。`WantedBy=multi-user.target`指的是，sshd 所在的 Target 是`multi-user.target`。

这个设置非常重要，因为执行`systemctl enable sshd.service`命令时，`sshd.service`的一个符号链接，就会放在`/etc/systemd/system`目录下面的`multi-user.target.wants`子目录之中。

Systemd 有默认的启动 Target。

> ```bash
> $ systemctl get-default
> multi-user.target
> ```



> ```bash
> # 查看 multi-user.target 包含的所有服务
> $ systemctl list-dependencies multi-user.target
> 
> # 切换到另一个 target
> # shutdown.target 就是关机状态
> $ sudo systemctl isolate shutdown.target
> ```

## 九、修改配置文件后重启

修改配置文件以后，需要重新加载配置文件，然后重新启动相关服务。

> ```bash
> # 重新加载配置文件
> $ sudo systemctl daemon-reload
> 
> # 重启相关服务
> $ sudo systemctl restart foobar
> ```



## 10. java 程序自启动

https://blog.csdn.net/stonexmx/article/details/73541508

我本地有一个 data-service.jar

1. 编写启动脚本  data-service-start



 编写启动脚本  data-service-start

```shell
[root@iz2ze0fq2isg8vphkpos5sz shell]# more  data-service-start
#!/bin/sh
 
export JAVA_HOME=/usr/local/jdk1.8.0_131
export PATH=$JAVA_HOME/bin:$PATH
 
java -jar /data/imgcloud/data-service.jar > /data/logs/data-service.log &
echo $! > /var/run/data-service.pid

```



2. 编写停止脚本
```shell
[root@iz2ze0fq2isg8vphkpos5sz shell]# more data-service-stop 
#!/bin/sh
PID=$(cat /var/run/data-service.pid)
kill -9 $PID
```



3. 在/usr/lib/systemd/system 下 编写 data-service.service 脚本

```shell
[root@iz2ze0fq2isg8vphkpos5sz shell]# cd /usr/lib/systemd/system
[root@iz2ze0fq2isg8vphkpos5sz system]# more data-service.service 
[Unit]
Description=data-service for mongodb
After=syslog.target network.target remote-fs.target nss-lookup.target
 
[Service]
Type=forking
ExecStart=/data/shell/data-service-start
ExecStop=/data/shell/data-service-stop
PrivateTmp=true
 
[Install]
WantedBy=multi-user.target

```

4. 相关命令

```shell
#开机自启动
systemctl  enable   data-service    

#停止
systemctl  stop  data-service  

#启动
system  start data-service
```

# systemd的日志



Systemd 统一管理所有 Unit 的启动日志。带来的好处就是，可以只用journalctl一个命令，查看所有日志（内核日志和应用日志）。日志的配置文件是/etc/systemd/journald.conf。 
journalctl功能强大，用法非常多。

```
# 查看所有日志（默认情况下 ，只保存本次启动的日志）
$ sudo journalctl

# 查看内核日志（不显示应用日志）
$ sudo journalctl -k

# 查看系统本次启动的日志
$ sudo journalctl -b
$ sudo journalctl -b -0

# 查看上一次启动的日志（需更改设置）
$ sudo journalctl -b -1

# 查看指定时间的日志
$ sudo journalctl --since="2012-10-30 18:17:16"
$ sudo journalctl --since "20 min ago"
$ sudo journalctl --since yesterday
$ sudo journalctl --since "2015-01-10" --until "2015-01-11 03:00"
$ sudo journalctl --since 09:00 --until "1 hour ago"

# 显示尾部的最新10行日志
$ sudo journalctl -n

# 显示尾部指定行数的日志
$ sudo journalctl -n 20

# 实时滚动显示最新日志
$ sudo journalctl -f

# 查看指定服务的日志
$ sudo journalctl /usr/lib/systemd/systemd

# 查看指定进程的日志
$ sudo journalctl _PID=1

# 查看某个路径的脚本的日志
$ sudo journalctl /usr/bin/bash

# 查看指定用户的日志
$ sudo journalctl _UID=33 --since today

# 查看某个 Unit 的日志
$ sudo journalctl -u nginx.service
$ sudo journalctl -u nginx.service --since today

# 实时滚动显示某个 Unit 的最新日志
$ sudo journalctl -u nginx.service -f

# 合并显示多个 Unit 的日志
$ journalctl -u nginx.service -u php-fpm.service --since today

# 查看指定优先级（及其以上级别）的日志，共有8级
# 0: emerg
# 1: alert
# 2: crit
# 3: err
# 4: warning
# 5: notice
# 6: info
# 7: debug
$ sudo journalctl -p err -b

# 日志默认分页输出，--no-pager 改为正常的标准输出
$ sudo journalctl --no-pager

# 以 JSON 格式（单行）输出
$ sudo journalctl -b -u nginx.service -o json

# 以 JSON 格式（多行）输出，可读性更好
$ sudo journalctl -b -u nginx.serviceqq
 -o json-pretty

# 显示日志占据的硬盘空间
$ sudo journalctl --disk-usage

# 指定日志文件占据的最大空间
$ sudo journalctl --vacuum-size=1G

# 指定日志文件保存多久
$ sudo journalctl --vacuum-time=1years
```





