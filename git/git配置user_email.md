[toc]

# How to Configure Git Username and Email Address



The first thing you should do after installing Git on your system is to configure your git username and email address. **Git associate your identity with every commit you make.**



# 配置全局

```shell
git config --global user.name "Your Name"
git config --global user.email "youremail@yourdomain.com"

git config --list

#Output
user.name=Your Name
user.email=youremail@yourdomain.com

#The command saves the values in the global configuration file, 
# ~/.gitconfig:

#cat ~/.gitconfig
[user]
    name = Your Name
    email = youremail@yourdomain.com
```

> You can also edit the file with your text editor, but it is recommended to use the `git config` command.



# 配置某个git工程

Setting Git Username and Password for a Single Repository

If you want to use a different username or email address for a specific repository, run the `git config` command without the `--global` option from within the repository directory.

Let’s say you want to set a repository-specific username and email address for a stored in the `~/Code/myapp` directory. First, switch the repository root directory:

```
cd ~/Code/myappCopy
```

Set a Git username and email address:

```
git config user.name "Your Name"git config user.email "youremail@yourdomain.com"CopyCopy
```

Verify that the changes were made correctly:

```
git config --listCopy
user.name=Your Name
user.email=youremail@yourdomain.com
Copy
```

The repository-specific setting are kept in the `.git/config` file under the root directory of the repository.