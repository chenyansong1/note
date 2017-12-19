#!/bin/bash

exe_hive="/usr/local/hive/bin/hive "

for dt in `seq -w 20170821 20170827`;do
	sql_pv=" select count(1) from adw.tb_webpageview_day where dt=$dt and url like '%?partner=shunwang%' "
	sql_uv=" select count(distinct cookieid) from adw.tb_webpageview_day where dt=$dt and url like '%?partner=shunwang%' "
	
	echo $dt >> tb_webpageview_day_bychenyansong.data
	$exe_hive -e "$sql_pv" >> tb_webpageview_day_bychenyansong.data
	$exe_hive -e "$sql_uv" >> tb_webpageview_day_bychenyansong.data
	
	echo "############" >> tb_webpageview_day_bychenyansong.data
	
done



