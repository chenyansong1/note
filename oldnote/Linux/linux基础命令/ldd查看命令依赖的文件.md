```
[webuser@VM_0_4_centos ~]$ ldd /usr/bin/bash
        linux-vdso.so.1 =>  (0x00007ffcaf19b000)
        libtinfo.so.5 => /lib64/libtinfo.so.5 (0x00007f2b110e1000)
        libdl.so.2 => /lib64/libdl.so.2 (0x00007f2b10edd000)
        libc.so.6 => /lib64/libc.so.6 (0x00007f2b10b1b000)
        /lib64/ld-linux-x86-64.so.2 (0x00007f2b11314000)
[webuser@VM_0_4_centos ~]$ which python
/usr/bin/python
[webuser@VM_0_4_centos ~]$ python -V
Python 2.7.5
[webuser@VM_0_4_centos ~]$ ldd /usr/bin/python
        linux-vdso.so.1 =>  (0x00007ffcd4b00000)
        libpython2.7.so.1.0 => /lib64/libpython2.7.so.1.0 (0x00007fe908bc2000)
        libpthread.so.0 => /lib64/libpthread.so.0 (0x00007fe9089a6000)
        libdl.so.2 => /lib64/libdl.so.2 (0x00007fe9087a1000)
        libutil.so.1 => /lib64/libutil.so.1 (0x00007fe90859e000)
        libm.so.6 => /lib64/libm.so.6 (0x00007fe90829c000)
        libc.so.6 => /lib64/libc.so.6 (0x00007fe907eda000)
        /lib64/ld-linux-x86-64.so.2 (0x00007fe908f96000)
[webuser@VM_0_4_centos ~]$ 


```

