#!/bin/bash

##日数据汇集脚本，每天aaa数据加载到数据库之后进行统计
##根据输入的rundate,统计计算前一天数据，如果不传入rundate,则设置rundate为当前日期
##例：rundate=20190909，则统计20190908当天的数据

rundate=$1
if [ ! -n "$rundate" ];then
    rundate=`date +%Y%m%d`;
fi
tmp_date=`date -d "$rundate" +%Y%m%d`;
if [ "$rundate" != "$tmp_date" ]; then
    echo "{$rundate} format is error.date format is yyyymmdd."
    exit;
fi;

##数据库信息
datasource=kdzlfx/kdzlfx@OLTSER

current_date=$rundate
last_date=`date -d "$current_date -1 day" +%Y%m%d`;


sqlplus -s ${datasource} <<!
    --根据CES_CITY_CODE中的地市编码给统计表中各地市添加默认值
    merge into CES_DAY_ANALYSIS t1 using (select '${last_date}' as statistics_date,city_code as areano from CES_CITY_CODE
    where city_code!='0000') t2 on (t1.statistics_date=t2.statistics_date and t1.city_code=t2.areano)when not matched then
    insert(statistics_date,city_code,add_num,cancel_num,own_num,stop_num,addup_num)values(t2.statistics_date,areano,0,0,0,0,0);
    commit;
    
    -- 新增用户数
    --统计用户表 status=0 and opendate在某一天的记录
    merge into CES_DAY_ANALYSIS t1 using (select '${last_date}' as statistics_date,areano,count(*) as nums 
    from USER_INFO where opendate between to_date('${last_date}','YYYYMMDD')
    and to_date('${current_date}','YYYYMMDD') and status=0 group by areano) t2 on (t1.statistics_date=t2.statistics_date 
    and t1.city_code=t2.areano)  when matched then update set t1.add_num = t2.nums
    when not matched then insert(statistics_date,city_code,add_num) values(t2.statistics_date,areano,nums);
    commit;
    
    --销户用户数
    --统计历史表 otype=3 and itime在某一天的记录(需要对用户名去重)
    merge into CES_DAY_ANALYSIS t1 using (select '${last_date}' as statistics_date,areano,count(distinct username) as nums 
    from USER_HIS where itime between to_date('${last_date}','YYYYMMDD')and to_date('${current_date}','YYYYMMDD')
    and otype=3 group by areano) t2 on (t1.statistics_date=t2.statistics_date 
    and t1.city_code=t2.areano)  when matched then update set t1.cancel_num = t2.nums
    when not matched then insert(statistics_date,city_code,cancel_num) values(t2.statistics_date,areano,nums);
    commit;
    
    --净增用户数
    --新增用户数-销户用户数
    update CES_DAY_ANALYSIS set own_num=add_num-cancel_num where statistics_date='${last_date}';
    commit;
    
    --停机用户数
    --统计用户表 status=1 且 pausedate在某一天的记录
    merge into CES_DAY_ANALYSIS t1 using (select '${last_date}' as statistics_date,areano,count(*) as nums 
    from USER_INFO where pausedate between to_date('${last_date}','YYYYMMDD')and to_date('${current_date}','YYYYMMDD')
    and status=1 group by areano) t2 on (t1.statistics_date=t2.statistics_date 
    and t1.city_code=t2.areano)  when matched then update set t1.stop_num = t2.nums
    when not matched then insert(statistics_date,city_code,stop_num) values(t2.statistics_date,areano,nums);
    
    --累计用户数
    --统计当天给的用户表的数量
    merge into CES_DAY_ANALYSIS t1 using (select '${last_date}' as statistics_date,areano,count(*) as nums 
    from USER_INFO group by areano) t2 on (t1.statistics_date=t2.statistics_date 
    and t1.city_code=t2.areano)  when matched then update set t1.addup_num = t2.nums
    when not matched then insert(statistics_date,city_code,addup_num) values(t2.statistics_date,areano,nums);
!