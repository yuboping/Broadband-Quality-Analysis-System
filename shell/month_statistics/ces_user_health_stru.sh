#!/bin/bash
##用户健康度结构统计
##例：sh ces_user_health_stru.sh 20180401
DBConnStr=kdzlfx/kdzlfx@OLTSER
rundate=$1
if [ ! -n "$rundate" ];then
    rundate=`date +%Y%m%d`;
fi

month=`date -d "$rundate 0 month" +%m`;
staMonth=`date -d "$rundate 0 month" +%Y%m`;


echo "`date` ces_user_health_stru start..."
echo "CES month:${month}"
sqlplus $DBConnStr <<!
	delete from CES_MONTH_ITEM_ANALYSIS where STATISTICS_DATE='$staMonth' and STA_ITEM='USER_HEALTH';
	commit;
	
	insert into CES_MONTH_ITEM_ANALYSIS (STATISTICS_DATE, CITY_CODE, STA_ITEM, STA_ATTR, ATTR_NAME, ATTR_VALUE, CREATE_DATE) 
	select '$staMonth', CITY_CODE, 'USER_HEALTH', STA_ATTR, ATTR_NAME, ATTR_VALUE, sysdate from (
	select CITY_CODE, '00_20' as STA_ATTR, '0-20' as ATTR_NAME, count(1) as ATTR_VALUE from CES_PREDICT_OFFNET_${month} 
	where PROB > 0.8 group by CITY_CODE 
	union all select CITY_CODE, '20_40' as STA_ATTR, '20-40' as ATTR_NAME, count(1) as ATTR_VALUE from CES_PREDICT_OFFNET_${month} 
	where PROB <= 0.8 AND PROB > 0.6 group by CITY_CODE 
	union all select CITY_CODE, '40_60' as STA_ATTR, '40-60' as ATTR_NAME, count(1) as ATTR_VALUE from CES_PREDICT_OFFNET_${month} 
	where PROB <= 0.6 AND PROB > 0.4 group by CITY_CODE 
	union all select CITY_CODE, '60_80' as STA_ATTR, '60-80' as ATTR_NAME, count(1) as ATTR_VALUE from CES_PREDICT_OFFNET_${month} 
	where PROB <= 0.4 AND PROB > 0.2 group by CITY_CODE 
	union all select CITY_CODE, '80_100' as STA_ATTR, '80-100' as ATTR_NAME, count(1) as ATTR_VALUE from CES_PREDICT_OFFNET_${month} 
	where PROB <= 0.2 group by CITY_CODE
	)
	commit;
!

echo "`date` ces_user_health_stru end!"