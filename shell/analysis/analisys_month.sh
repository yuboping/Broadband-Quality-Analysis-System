#!/bin/bash

##月数据汇集脚本，每个月月初统计上个月的数据(上个月的日统计都计算完之后进行当前脚本的统计)
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

last_month=`date -d "$rundate -1 month" +%Y%m`;
last_mm=`date -d "$rundate -1 month" +%m`;


sqlplus -s ${datasource} <<!
    --根据CES_CITY_CODE中的地市编码给统计表中各地市添加默认值
    merge into CES_MONTH_ANALYSIS t1 using (select '${last_month}' as statistics_date,city_code as areano from CES_CITY_CODE
    where city_code!='0000') t2 on (t1.statistics_date=t2.statistics_date and t1.city_code=t2.areano)when not matched then
    insert(statistics_date,city_code,reg_num,active_num,silent_num,stop_num)values(t2.statistics_date,areano,0,0,0,0);
    commit;
    
    -- 注册用户数 REG_NUM
    --统计用户表的总数量
    merge into CES_MONTH_ANALYSIS t1 using (select '${last_month}' as statistics_date,areano,count(*) as nums 
    from USER_INFO group by areano) t2 on (t1.statistics_date=t2.statistics_date 
    and t1.city_code=t2.areano)  when matched then update set t1.reg_num = t2.nums
    when not matched then insert(statistics_date,city_code,reg_num) values(t2.statistics_date,areano,nums);
    commit;
    
    --活跃用户数 ACTIVE_NUM
    --汇总用户账单数量
    merge into CES_MONTH_ANALYSIS t1 using (select '${last_month}' as statistics_date,areano,count(distinct username) as nums 
    from user_bill_${last_mm} group by areano) t2 on (t1.statistics_date=t2.statistics_date 
    and t1.city_code=t2.areano)  when matched then update set t1.active_num = t2.nums
    when not matched then insert(statistics_date,city_code,active_num) values(t2.statistics_date,areano,nums);
    commit;
    
    --静默用户数 SILENT_NUM
    --注册用户数-活跃用户数
    update ces_month_analysis set silent_num=reg_num-active_num where statistics_date='${last_month}';
    commit;
    
    --停机用户数
    --统计日汇总表中当月所有天数的停机用户数
    merge into CES_MONTH_ANALYSIS t1 using (select '${last_month}' as statistics_date,city_code as areano,sum(stop_num) as nums 
    from CES_DAY_ANALYSIS where statistics_date like '${last_month}%' group by city_code) t2 on (t1.statistics_date=t2.statistics_date 
    and t1.city_code=t2.areano)  when matched then update set t1.stop_num = t2.nums
    when not matched then insert(statistics_date,city_code,stop_num) values(t2.statistics_date,areano,nums);
 
!