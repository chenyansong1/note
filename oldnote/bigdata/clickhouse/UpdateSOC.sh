#!/usr/bin/env bash
# soc服务器nginx、openssl更新脚本 by hww 2020-06-03
filedir=`pwd`


function restart_tomcat () {
    cd /home/workspace/apache-tomcat-8.0.37/bin
    ps -ef|grep tomcat |grep -v grep |awk '{print $2}' |xargs kill -9
    ./startup.sh
    service AnalyzeServer restart
    sleep 10s
    echo "----------------openssl nginx 漏洞补丁更新完成，服务重启完成-------------"

}

#升级ssh，修复ssh远程代码执行漏洞
function openssl_update () {
	echo "----------------openssl 升级 start-------------"
	tar -zxvf $filedir/openssh-8.4p1.tar.gz
	cd openssh-8.4p1/
	yum install pam-devel libselinux-devel zlib-devel openssl-devel -y
	./configure  --prefix=/usr --with-md5-passwords --with-pam --with-selinux --with-privsep-path=/var/lib/sshd/ --sysconfdir=/etc/ssh
	make && make install

	service sshd restart 
	echo "----------------openssl 升级到openssh-8.4 end-------------"
	sleep 5s
}

#升级nginx，修复nginx拒绝服务漏洞
function nginx_update () {
	echo "----------------nginx 升级 start-------------"
	tar -zxvf $filedir/nginx-1.19.1.tar.gz
	cd nginx-1.19.1
	./configure --prefix=/usr/local/nginx  --with-http_ssl_module  --add-module=/opt/ModSecurity-nginx_refactoring/nginx/modsecurity/
	make && make install

	killall -9 nginx
	/usr/local/nginx/sbin/nginx -c /usr/local/nginx/conf/nginx.conf 
	echo "----------------nginx1.13 升级到nginx1.19.1 end-------------"
	sleep 5s
}



function main () {
    openssl_update
    nginx_update
    restart_tomcat
}

main




