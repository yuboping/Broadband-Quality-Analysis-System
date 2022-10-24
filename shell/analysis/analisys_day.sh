#!/bin/bash

##�����ݻ㼯�ű���ÿ��aaa���ݼ��ص����ݿ�֮�����ͳ��
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

current_date=$rundate
last_date=`date -d "$current_date -1 day" +%Y%m%d`;


sqlplus -s ${datasource} <<!
    --����CES_CITY_CODE�еĵ��б����ͳ�Ʊ��и��������Ĭ��ֵ
    merge into CES_DAY_ANALYSIS t1 using (select '${last_date}' as statistics_date,city_code as areano from CES_CITY_CODE
    where city_code!='0000') t2 on (t1.statistics_date=t2.statistics_date and t1.city_code=t2.areano)when not matched then
    insert(statistics_date,city_code,add_num,cancel_num,own_num,stop_num,addup_num)values(t2.statistics_date,areano,0,0,0,0,0);
    commit;
    
    -- �����û���
    --ͳ���û��� status=0 and opendate��ĳһ��ļ�¼
    merge into CES_DAY_ANALYSIS t1 using (select '${last_date}' as statistics_date,areano,count(*) as nums 
    from USER_INFO where opendate between to_date('${last_date}','YYYYMMDD')
    and to_date('${current_date}','YYYYMMDD') and status=0 group by areano) t2 on (t1.statistics_date=t2.statistics_date 
    and t1.city_code=t2.areano)  when matched then update set t1.add_num = t2.nums
    when not matched then insert(statistics_date,city_code,add_num) values(t2.statistics_date,areano,nums);
    commit;
    
    --�����û���
    --ͳ����ʷ�� otype=3 and itime��ĳһ��ļ�¼(��Ҫ���û���ȥ��)
    merge into CES_DAY_ANALYSIS t1 using (select '${last_date}' as statistics_date,areano,count(distinct username) as nums 
    from USER_HIS where itime between to_date('${last_date}','YYYYMMDD')and to_date('${current_date}','YYYYMMDD')
    and otype=3 group by areano) t2 on (t1.statistics_date=t2.statistics_date 
    and t1.city_code=t2.areano)  when matched then update set t1.cancel_num = t2.nums
    when not matched then insert(statistics_date,city_code,cancel_num) values(t2.statistics_date,areano,nums);
    commit;
    
    --�����û���
    --�����û���-�����û���
    update CES_DAY_ANALYSIS set own_num=add_num-cancel_num where statistics_date='${last_date}';
    commit;
    
    --ͣ���û���
    --ͳ���û��� status=1 �� pausedate��ĳһ��ļ�¼
    merge into CES_DAY_ANALYSIS t1 using (select '${last_date}' as statistics_date,areano,count(*) as nums 
    from USER_INFO where pausedate between to_date('${last_date}','YYYYMMDD')and to_date('${current_date}','YYYYMMDD')
    and status=1 group by areano) t2 on (t1.statistics_date=t2.statistics_date 
    and t1.city_code=t2.areano)  when matched then update set t1.stop_num = t2.nums
    when not matched then insert(statistics_date,city_code,stop_num) values(t2.statistics_date,areano,nums);
    
    --�ۼ��û���
    --ͳ�Ƶ�������û��������
    merge into CES_DAY_ANALYSIS t1 using (select '${last_date}' as statistics_date,areano,count(*) as nums 
    from USER_INFO group by areano) t2 on (t1.statistics_date=t2.statistics_date 
    and t1.city_code=t2.areano)  when matched then update set t1.addup_num = t2.nums
    when not matched then insert(statistics_date,city_code,addup_num) values(t2.statistics_date,areano,nums);
!