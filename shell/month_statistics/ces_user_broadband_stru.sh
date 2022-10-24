#!/bin/bash
##用户宽带类型统计
##例：sh ces_user_broadband_stru.sh 20180401
export NLS_LANG=american_america.AL32UTF8
DBConnStr=kdzlfx/kdzlfx@OLTSER
rundate=$1
if [ ! -n "$rundate" ];then
    rundate=`date +%Y%m%d`;
fi

month=`date -d "$rundate 0 month" +%m`;
staMonth=`date -d "$rundate 0 month" +%Y%m`;


echo "`date` ces_user_broadband_stru start..."
echo "CES month:${month}"
sqlplus $DBConnStr <<!
	delete from CES_MONTH_ITEM_ANALYSIS where STATISTICS_DATE='$staMonth' and STA_ITEM='BROADBAND_TYPE';
	commit;
	insert into CES_MONTH_ITEM_ANALYSIS (STATISTICS_DATE, CITY_CODE, STA_ITEM, STA_ATTR, ATTR_NAME, ATTR_VALUE, CREATE_DATE) select '$staMonth', CITY_CODE, 'BROADBAND_TYPE', STA_ATTR, ATTR_NAME, ATTR_VALUE, sysdate from ( select CITY_CODE,ottonly as STA_ATTR,'仅宽带' as ATTR_NAME,count(1) as ATTR_VALUE from CES_BEHAVIOUR_CHARACTER_${month} where ottonly = 0 group by CITY_CODE,ottonly union all select CITY_CODE,ottonly as STA_ATTR,'宽带'||'&'||'OTT' as ATTR_NAME,count(1) as ATTR_VALUE from CES_BEHAVIOUR_CHARACTER_${month} where ottonly = 1 group by CITY_CODE,ottonly union all select CITY_CODE,ottonly as STA_ATTR,'仅OTT' as ATTR_NAME,count(1) as ATTR_VALUE from CES_BEHAVIOUR_CHARACTER_${month} where ottonly = 2 group by CITY_CODE,ottonly)
    commit;
!
echo "`date` ces_user_broadband_stru end!"