## Redis安全性配置



最近Redis刚爆出一个安全性漏洞，我的服务器就“光荣的”中招了。黑客攻击的基本方法是：

- 扫描Redis端口，直接登录没有访问控制的Redis
- 修改Redis存盘配置：config set dir /root/.ssh/; config set dbfilename /root/.ssh/authorized_keys
- 添加key：crackit，将其值设置为新的公钥。然后就可以为所欲为了。

不影响客户端程序的修复办法：

- 重命名config命令：rename-command CONFIG DO_NOT_USE_CONFIG
- 重启Redis

现在我们就来认识一下[Redis安全性](http://redis.io/topics/security)方面的一些知识。

首先，Redis从设计上来说是用来被可信的客户端访问的，这就意味着不适于暴露给外部环境里的非可信客户端访问。最佳的实践方法是在Redis前面加一个访问控制层，校验用户请求。

其次，Redis本身提供了一些简单的配置以满足基本的安全控制。

1. ip绑定。如果不需要直接对外提供服务，bind 127.0.0.1就行了，切忌bind 0.0.0.0！

2. 端口设置。修改默认的6379，一定程度上避免被扫描。

3. 设置密码。Redis的密码是通过requirepass以明文的形式配置在conf文件里的，所以要尽可能得长和复杂，降低被破解的风险。

4. 不要用root去启动redis，可以给redis新建一个用户用于启动redis

5. 重命名或禁用某些高危操作命令。向config、flushall、flushdb这些操作都是很关键的，不小心就会导致数据库不可用。可以在配置文件中通过rename-command重命名或禁用这些命令。

   ```
   ##如下，将禁止掉config命令：rename-command CONFIG ""
   ################################## SECURITY ###################################
   
   # Require clients to issue AUTH <PASSWORD> before processing any other
   # commands.  This might be useful in environments in which you do not trust
   # others with access to the host running redis-server.
   #
   # This should stay commented out for backward compatibility and because most
   # people do not need auth (e.g. they run their own servers).
   #
   # Warning: since Redis is pretty fast an outside user can try up to
   # 150k passwords per second against a good box. This means that you should
   # use a very strong password otherwise it will be very easy to break.
   #
   requirepass redis@ssa
   
   # Command renaming.
   #
   # It is possible to change the name of dangerous commands in a shared
   # environment. For instance the CONFIG command may be renamed into something
   # hard to guess so that it will still be available for internal-use tools
   # but not available for general clients.
   #
   # Example:
   #
   # rename-command CONFIG b840fc02d524045429941cc15f59e41cb7be6c52
   #
   # It is also possible to completely kill a command by renaming it into
   # an empty string:
   #
   # rename-command CONFIG ""
   rename-command CONFIG ""
   #
   # Please note that changing the name of commands that are logged into the
   # AOF file or transmitted to slaves may cause problems.
   
   ################################### LIMITS ####################################
   ```

   





另一篇关于安全性方面的文章：

<https://www.digitalocean.com/community/tutorials/how-to-secure-your-redis-installation-on-ubuntu-14-04>

