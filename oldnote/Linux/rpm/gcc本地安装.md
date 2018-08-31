



```
首先到http://vault.centos.org/6.5/os/x86_64/Packages/下载用到的rpm包，包括：
cloog-ppl-0.15.7-1.2.el6.x86_64.rpm
cpp-4.4.7-4.el6.x86_64.rpm
gcc-4.4.7-4.el6.x86_64.rpm
gcc-c++-4.4.7-4.el6.x86_64.rpm
glibc-devel-2.12-1.132.el6.x86_64.rpm
glibc-headers-2.12-1.132.el6.x86_64.rpm
kernel-headers-2.6.32-431.el6.x86_64.rpm
libstdc++-devel-4.4.7-4.el6.x86_64.rpm
mpfr-2.4.1-6.el6.x86_64.rpm
ppl-0.10.2-11.el6.x86_64.rpm



安装流程如下所示：

rpm -ivh ppl-0.10.2-11.el6.x86_64.rpm
rpm -ivh cloog-ppl-0.15.7-1.2.el6.x86_64.rpm
rpm -ivh mpfr-2.4.1-6.el6.x86_64.rpm
rpm -ivh cpp-4.4.7-4.el6.x86_64.rpm --force
rpm -ivh kernel-headers-2.6.32-431.el6.x86_64.rpm
rpm -ivh glibc-headers-2.12-1.132.el6.x86_64.rpm --force
rpm -ivh glibc-devel-2.12-1.132.el6.x86_64.rpm --force
rpm -ivh gcc-4.4.7-4.el6.x86_64.rpm --force
rpm -ivh libstdc++-devel-4.4.7-4.el6.x86_64.rpm
rpm -ivh gcc-c++-4.4.7-4.el6.x86_64.rpm

说明：--force参数是试用于冲突时候强制替换的，不一定都需要。



安装完成后验证：

#gcc -v

Using built-in specs.
Target: x86_64-redhat-linux
Configured with: ../configure --prefix=/usr --mandir=/usr/share/man --infodir=/usr/share/info --with-bugurl=http://bugzilla.redhat.com/bugzilla --enable-bootstrap --enable-shared --enable-threads=posix --enable-checking=release --with-system-zlib --enable-__cxa_atexit --disable-libunwind-exceptions --enable-gnu-unique-object --enable-languages=c,c++,objc,obj-c++,java,fortran,ada --enable-java-awt=gtk --disable-dssi --with-java-home=/usr/lib/jvm/java-1.5.0-gcj-1.5.0.0/jre --enable-libgcj-multifile --enable-java-maintainer-mode --with-ecj-jar=/usr/share/java/eclipse-ecj.jar --disable-libjava-multilib --with-ppl --with-cloog --with-tune=generic --with-arch_32=i686 --build=x86_64-redhat-linux
Thread model: posix
gcc version 4.4.7 20120313 (Red Hat 4.4.7-4) (GCC)

#g++ -v

Using built-in specs.
Target: x86_64-redhat-linux
Configured with: ../configure --prefix=/usr --mandir=/usr/share/man --infodir=/usr/share/info --with-bugurl=http://bugzilla.redhat.com/bugzilla --enable-bootstrap --enable-shared --enable-threads=posix --enable-checking=release --with-system-zlib --enable-__cxa_atexit --disable-libunwind-exceptions --enable-gnu-unique-object --enable-languages=c,c++,objc,obj-c++,java,fortran,ada --enable-java-awt=gtk --disable-dssi --with-java-home=/usr/lib/jvm/java-1.5.0-gcj-1.5.0.0/jre --enable-libgcj-multifile --enable-java-maintainer-mode --with-ecj-jar=/usr/share/java/eclipse-ecj.jar --disable-libjava-multilib --with-ppl --with-cloog --with-tune=generic --with-arch_32=i686 --build=x86_64-redhat-linux
Thread model: posix
gcc version 4.4.7 20120313 (Red Hat 4.4.7-4) (GCC)

```

