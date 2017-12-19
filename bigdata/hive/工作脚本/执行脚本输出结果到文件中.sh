#!/bin/bash

exe_hive="/usr/local/hive/bin/hive "

HQL="select a.r_bid, a.cnt from 
(
select r_bid,count(1) as cnt
from adw.tb_ext_cdb_subscribe
where status=0 group by r_bid 
) as a
where a.cnt>=500 order by a.cnt desc ;"

$exe_hive -e "$HQL" > tb_bychenyansong.data
