[hadoop@es ~]$ cat  /usr/lib/systemd/system/elasticsearch.service 
[Unit]
Description=elasticsearch service
After=syslog.target network.target

[Service]
User=hadoop
Group=root
TimeoutStartSec=10min
Type=forking
SyslogIdentifier=hadoop
WorkingDirectory=/home/hadoop/elasticsearch-2.4.3
Environment="JAVA_HOME=/home/hadoop/jdk1.8.0_101"
#Environment
ExecStart=/home/hadoop/elas.sh start
ExecStop=/home/hadoop/elas.sh stop
RestartSec=300
Restart=always
LimitCORE=infinity
LimitNOFILE=1048576
LimitNPROC=1048576


[Install]
WantedBy=multi-user.target
[hadoop@es ~]$ 
