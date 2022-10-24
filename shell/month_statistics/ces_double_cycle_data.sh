#!/bin/bash
DBConnStr=kdzlfx/kdzlfx@OLTSER
rundate=$1
if [ ! -n "$rundate" ];then
    rundate=`date +%Y%m%d`;
fi
tmp_date=`date -d "$rundate" +%Y%m%d`;
if [ "$rundate" != "$tmp_date" ]; then
    echo "{$rundate} format is error.date format is yyyymmdd."
    exit;
fi;

month=`date -d "$rundate 0 month" +%m`;
staMonth=`date -d "$rundate 0 month" +%Y%m`;


echo "`date` ces_double_cycle_data start..."
echo "CES month:${month}"
sqlplus $DBConnStr <<!
	delete from CES_MONTH_ITEM_ANALYSIS where STATISTICS_DATE='$staMonth' and ATTR_NAME='cycle_inner';
	commit;

    delete from CES_MONTH_ITEM_ANALYSIS where STATISTICS_DATE='$staMonth' and ATTR_NAME='cycle_outer';
	commit;
	
	insert into CES_MONTH_ITEM_ANALYSIS
	select to_char(sysdate,'yyyymm') as STATISTICS_DATE,
	CITY_CODE , '0000' as STA_ITEM, inteligentgateway as STA_ATTR,
	'cycle_inner' as ATTR_NAME,  COUNT(*) as ATTR_VALUE,
	to_date(to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm-dd hh24:mi:ss') as CREATE_DATE
	from CES_TERMINAL_CHARACTER_${month}  GROUP BY CITY_CODE, inteligentgateway;
    commit;

    insert into CES_MONTH_ITEM_ANALYSIS
    select to_char(sysdate,'yyyymm') as STATISTICS_DATE,
    CITY_CODE , badquality as STA_ITEM, inteligentgateway as STA_ATTR,
    'cycle_outer' as ATTR_NAME,  COUNT(*) as ATTR_VALUE,
    to_date(to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm-dd hh24:mi:ss') as CREATE_DATE
    from CES_TERMINAL_CHARACTER_${month}  GROUP BY CITY_CODE, badquality,  inteligentgateway;
    commit;

!

echo "`date` ces_double_cycle_data end!"