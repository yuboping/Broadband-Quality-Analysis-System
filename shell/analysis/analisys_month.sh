#!/bin/bash

##�����ݻ㼯�ű���ÿ�����³�ͳ���ϸ��µ�����(�ϸ��µ���ͳ�ƶ�������֮����е�ǰ�ű���ͳ��)
##���������rundate,ͳ�Ƽ���ǰһ�����ݣ����������rundate,������rundateΪ��ǰ����
##����rundate=20190909����ͳ��20190908���������

rundate=$1
if [ ! -n "$rundate" ];then
    rundate=`date +%Y%m%d`;
fi
tmp_date=`date -d "$rundate" +%Y%m%d`;
if [ "$rundate" != "$tmp_date" ]; then
    echo "{$rundate} format is error.date format is yyyymmdd."
    exit;
fi;

##���ݿ���Ϣ
datasource=kdzlfx/kdzlfx@OLTSER

last_month=`date -d "$rundate -1 month" +%Y%m`;
last_mm=`date -d "$rundate -1 month" +%m`;


sqlplus -s ${datasource} <<!
    --����CES_CITY_CODE�еĵ��б����ͳ�Ʊ��и��������Ĭ��ֵ
    merge into CES_MONTH_ANALYSIS t1 using (select '${last_month}' as statistics_date,city_code as areano from CES_CITY_CODE
    where city_code!='0000') t2 on (t1.statistics_date=t2.statistics_date and t1.city_code=t2.areano)when not matched then
    insert(statistics_date,city_code,reg_num,active_num,silent_num,stop_num)values(t2.statistics_date,areano,0,0,0,0);
    commit;
    
    -- ע���û��� REG_NUM
    --ͳ���û����������
    merge into CES_MONTH_ANALYSIS t1 using (select '${last_month}' as statistics_date,areano,count(*) as nums 
    from USER_INFO group by areano) t2 on (t1.statistics_date=t2.statistics_date 
    and t1.city_code=t2.areano)  when matched then update set t1.reg_num = t2.nums
    when not matched then insert(statistics_date,city_code,reg_num) values(t2.statistics_date,areano,nums);
    commit;
    
    --��Ծ�û��� ACTIVE_NUM
    --�����û��˵�����
    merge into CES_MONTH_ANALYSIS t1 using (select '${last_month}' as statistics_date,areano,count(distinct username) as nums 
    from user_bill_${last_mm} group by areano) t2 on (t1.statistics_date=t2.statistics_date 
    and t1.city_code=t2.areano)  when matched then update set t1.active_num = t2.nums
    when not matched then insert(statistics_date,city_code,active_num) values(t2.statistics_date,areano,nums);
    commit;
    
    --��Ĭ�û��� SILENT_NUM
    --ע���û���-��Ծ�û���
    update ces_month_analysis set silent_num=reg_num-active_num where statistics_date='${last_month}';
    commit;
    
    --ͣ���û���
    --ͳ���ջ��ܱ��е�������������ͣ���û���
    merge into CES_MONTH_ANALYSIS t1 using (select '${last_month}' as statistics_date,city_code as areano,sum(stop_num) as nums 
    from CES_DAY_ANALYSIS where statistics_date like '${last_month}%' group by city_code) t2 on (t1.statistics_date=t2.statistics_date 
    and t1.city_code=t2.areano)  when matched then update set t1.stop_num = t2.nums
    when not matched then insert(statistics_date,city_code,stop_num) values(t2.statistics_date,areano,nums);
 
!