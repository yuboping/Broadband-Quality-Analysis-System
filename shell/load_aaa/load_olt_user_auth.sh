#!/bin/bash
##OLT用户认证失败率导入
##例：sh load_olt_user_auth.sh 20191001
DBConnStr=kdzlfx/kdzlfx@OLTSER
rundate=$1
if [ ! -n "$rundate" ];then
    rundate=`date -d "yesterday" +%Y%m%d`;
fi

month=`date -d "$rundate 0 month" +%m`;
staMonth=`date -d "$rundate 0 month" +%Y%m`;

echo "`date` load_olt_user_auth start..."
echo "CES month:${month}"
sqlplus $DBConnStr <<!
	delete from OLT_USER_AUTH_${month} where to_char(sta_date,'yyyyMMdd')='$rundate';
	commit;
    insert into OLT_USER_AUTH_${month} (sta_date, oltip, authfailnum, authsuccessnum, authfailratio) select c.sta_date, c.oltip, c.authfailnum, c.authsuccessnum, ROUND(case when (c.authfailnum + c.authsuccessnum) > 0 then (c.authfailnum / (c.authfailnum + c.authsuccessnum)) * 100 else 0 end, 2) as authfailratio from (select nvl(a.sta_date, b.sta_date) sta_date, nvl(a.oltip, b.oltip) oltip, nvl(a.authsuccessnum, 0) authsuccessnum, nvl(b.authfailnum, 0) authfailnum from (select sta_date, anid as oltip, count(1) as authsuccessnum from (select case when ((ceil(to_char(authdate, 'sssss') / 600) - 1) / 144) < 0 then trunc(authdate, 'dd') else trunc(authdate, 'dd') + (ceil(to_char(authdate, 'sssss') / 600) - 1) / 144 end as sta_date, SUBSTR(SUBSTR(accattr, INSTR(accattr, 'anid', 1, 1) + 5), 0, INSTR(SUBSTR(accattr, INSTR(accattr, 'anid', 1, 1) + 5), ',', 1, 1) - 1) as anid from CES_AUTH_SUCCESS) group by sta_date, anid) a FULL JOIN (select sta_date, anid as oltip, count(1) as authfailnum from (select case when ((ceil(to_char(authdate, 'sssss') / 600) - 1) / 144) < 0 then trunc(authdate, 'dd') else trunc(authdate, 'dd') + (ceil(to_char(authdate, 'sssss') / 600) - 1) / 144 end as sta_date, SUBSTR(SUBSTR(accattr, INSTR(accattr, 'anid', 1, 1) + 5), 0, INSTR(SUBSTR(accattr, INSTR(accattr, 'anid', 1, 1) + 5), ',', 1, 1) - 1) as anid from CES_AUTH_FAIL) group by sta_date, anid) b on a.sta_date = b.sta_date and a.oltip = b.oltip) c where c.oltip is not null;
	commit;
!
echo "`date` load_olt_user_auth end!"