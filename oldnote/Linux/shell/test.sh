#!/system/bin/sh

#================================================================
#   Copyright (C) 2018 Sangfor Ltd. All rights reserved.
#   
#   file name  ：test.sh
#   creator    ：chenyansong
#   create_date：2018-09-11
#   description：
#
#================================================================

while True ;do
	read -p "please input str" str
	
	if [ "$str" == "quit" ];then
		break;
	else
		echo $str|tr 'a-z' 'A-Z'
	fi

done
