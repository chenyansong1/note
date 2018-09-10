

```
fuser: 文件orsocket正在被哪个用户使用
       fuser - identify processes using files or sockets

	-v :查看某个文件上正在运行的进程
	-km:
	
fuser filename

[webuser@VM_0_4_centos etc]$ fuser -v /etc
                     用户     进程号 权限   命令
/etc:                webuser   26446 ..c.. bash
[webuser@VM_0_4_centos etc]$ 


#终止正在访问此挂载点的所有进程
fuser -km Mount_Point


 -k, --kill
              Kill processes accessing the file.  Unless changed with -SIGNAL, SIGKILL is sent.  An fuser process never kills itself, but  may  kill  other  fuser  processes.   The
              effective user ID of the process executing fuser is set to its real user ID before attempting to kill.



       -m NAME, --mount NAME
              NAME  specifies a file on a mounted file system or a block device that is mounted.  All processes accessing files on that file system are listed.  If a directory file
              is specified, it is automatically changed to NAME/.  to use any file system that might be mounted on that directory.

```

