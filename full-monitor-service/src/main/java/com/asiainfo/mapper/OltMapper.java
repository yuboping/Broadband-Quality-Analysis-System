package com.asiainfo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import com.asiainfo.model.chartData.AlarmInfo;
import com.asiainfo.model.chartData.Olt;
import com.asiainfo.model.chartData.OltHistory;
import com.asiainfo.model.chartData.OltInfo;
import com.asiainfo.util.page.Page;

public interface OltMapper {

    @Select("select oltip,onlinenum from (select * from OLT_ONLINE_${month} order by STA_DATE desc,ONLINENUM desc ) where rownum <= 10")
    List<OltInfo> userOlt(@Param("month") String month);

    @Select("select * from OLT_ONLINE_${month} where OLTIP = #{oltip} and (SELECT t.STA_DATE-12/24 from (select c.STA_DATE from OLT_ONLINE_${month} c where c.OLTIP = #{oltip} ORDER BY c.STA_DATE DESC ) t where ROWNUM <=1 ) <=STA_DATE order by STA_DATE")
    List<OltInfo> userOltByOltip(@Param("oltip") String oltip, @Param("month") String month);

    @Select("select oltip, oltgeox, oltgeoy, isalarm, max(value) AS value from (select oi.oltip, oi.oltgeox, oi.oltgeoy, CASE cai.alarm_name when 'oltMap' THEN '1' ELSE '0' END AS isalarm, CASE when (m.onlinemidnum < 1567 and cai.alarm_name = 'oltMap') THEN '120' when (m.onlinemidnum > 1567 and m.onlinemidnum < 2311 and cai.alarm_name = 'oltMap') THEN '150' when (m.onlinemidnum > 2311 and cai.alarm_name = 'oltMap') THEN '180' ELSE '20' END AS value, m.onlinemidnum from OLT_INFO oi left join (select * from (select alarm_name, alarm_time, alarm_dimension1, row_number() over(partition by alarm_dimension1 order by alarm_time desc) as rn from CES_ALARM_INFO where alarm_name = 'oltMap' and (SYSDATE - 1) <= alarm_time and alarm_time <= sysdate and (delete_state IS NULL or delete_state != '1')) where rn = 1) cai on (cai.alarm_dimension1 = oi.oltip) left join OLT_MID_ONLINE m on (oi.oltip = m.oltip and TRUNC(m.sta_date) = TRUNC(SYSDATE)) where m.onlinemidnum > 1000 group by oi.oltip, oi.oltgeox, oi.oltgeoy, cai.alarm_name, m.onlinemidnum) tem where rownum <= 1000 or tem.isalarm = 1 group by oltip, oltgeox, oltgeoy, isalarm order by isalarm desc")
    List<Olt> getOltMap(@Param("month") String month);

    @Select("select count(1) from OLT_INFO")
    Integer getOltMapTotalNum();

    @Select("select count(1) from (select count(1) from CES_ALARM_INFO a left join OLT_MID_ONLINE m on (a.alarm_dimension1 = m.oltip and TRUNC(m.sta_date) = TRUNC(SYSDATE)) where a.alarm_name = 'oltMap' and (((sysdate - 1) <= a.alarm_time and a.alarm_time <= sysdate) and (delete_state IS NULL or delete_state != '1')) and m.onlinemidnum > 1000 group by a.alarm_dimension1)")
    Integer getOltMapAlarmNumTFH();

    @Select("select count(1) from (select count(1) from CES_ALARM_INFO a left join OLT_MID_ONLINE m on (a.alarm_dimension1 = m.oltip and TRUNC(m.sta_date) = TRUNC(SYSDATE)) where a.alarm_name = 'oltMap' and (((sysdate - 1) <= a.alarm_time and a.alarm_time <= sysdate) and (delete_state IS NULL or delete_state != '1')) and m.onlinemidnum > 1000 group by a.alarm_dimension1)")
    Integer getOltMapCurAlarmNum();

    @Select("select a.anid as oltip, (select count(1) from (select lc.user_name from CES_LINE_CHARACTER_${month} lc where lc.anid = #{oltip} group by lc.user_name)) as userTotalNum, (select count(1) from (select lc.user_name from CES_LINE_CHARACTER_${month} lc left join CES_TERMINAL_CHARACTER_${month} tc on (lc.user_name = tc.user_name and lc.city_code = tc.city_code) where lc.anid = #{oltip} and tc.badquality = 1 group by lc.user_name)) as badQualityNum, (select count(1) from (select lc.user_name from CES_LINE_CHARACTER_${month} lc left join CES_TERMINAL_CHARACTER_${month} tc on (lc.user_name = tc.user_name and lc.city_code = tc.city_code) where lc.anid = #{oltip} and tc.authfail_flag = 1 and tc.inteligentgateway = 0 group by lc.user_name)) as routerAuthFailNum, (select count(1) from (select lc.user_name from CES_LINE_CHARACTER_${month} lc left join CES_TERMINAL_CHARACTER_${month} tc on (lc.user_name = tc.user_name and lc.city_code = tc.city_code) where lc.anid = #{oltip} and tc.oftendown_flag = 1 and tc.inteligentgateway = 0 group by lc.user_name)) as routerUpDownFrequentNum, (select count(1) from (select lc.user_name from CES_LINE_CHARACTER_${month} lc left join CES_TERMINAL_CHARACTER_${month} tc on (lc.user_name = tc.user_name and lc.city_code = tc.city_code) where lc.anid = #{oltip} and tc.abnormaldown_flag = 1 and tc.inteligentgateway = 0 group by lc.user_name)) as routerAbnormalDropNum, (select count(1) from (select lc.user_name from CES_LINE_CHARACTER_${month} lc left join CES_TERMINAL_CHARACTER_${month} tc on (lc.user_name = tc.user_name and lc.city_code = tc.city_code) where lc.anid = #{oltip} and tc.authfail_flag = 1 and tc.inteligentgateway = 1 group by lc.user_name)) as gatewayAuthFailNum, (select count(1) from (select lc.user_name from CES_LINE_CHARACTER_${month} lc left join CES_TERMINAL_CHARACTER_${month} tc on (lc.user_name = tc.user_name and lc.city_code = tc.city_code) where lc.anid = #{oltip} and tc.oftendown_flag = 1 and tc.inteligentgateway = 1 group by lc.user_name)) as gatewayUpDownFrequentNum, (select count(1) from (select lc.user_name from CES_LINE_CHARACTER_${month} lc left join CES_TERMINAL_CHARACTER_${month} tc on (lc.user_name = tc.user_name and lc.city_code = tc.city_code) where lc.anid = #{oltip} and tc.abnormaldown_flag = 1 and tc.inteligentgateway = 1 group by lc.user_name)) as gatewayAbnormalDropNum from CES_LINE_CHARACTER_11 a where a.anid = #{oltip} and rownum <= 1")
    Olt getOltEquipmentInfo(@Param("month") String month, @Param("oltip") String oltip);

    @Select("select to_char(a.STA_DATE, 'yyyy-MM-dd HH24:mi:ss') as name, a.ONLINENUM as value, a.upper_value as upperValue, a.lower_value as lowerValue, CASE cai.alarm_name when 'oltUserOnline' THEN '1' ELSE '0' END AS isalarm from (select oo.*, oot.upper_value, oot.lower_value from OLT_ONLINE_${preMonth} oo left join OLT_ONLINE_THRESHOLD_${preMonth} oot on (oot.oltip = oo.OLTIP and oot.cal_time = oo.STA_DATE) where to_date(#{beginTime}, 'yyyy-MM-dd HH24:mi:ss') <= oo.STA_DATE and oo.STA_DATE <= to_date(#{endTime}, 'yyyy-MM-dd HH24:mi:ss') and oo.OLTIP = #{oltip} union all select oo.*, oot.upper_value, oot.lower_value from OLT_ONLINE_${month} oo left join OLT_ONLINE_THRESHOLD_${month} oot on (oot.oltip = oo.OLTIP and oot.cal_time = oo.STA_DATE) where to_date(#{beginTime}, 'yyyy-MM-dd HH24:mi:ss') <= oo.STA_DATE and oo.STA_DATE <= to_date(#{endTime}, 'yyyy-MM-dd HH24:mi:ss') and oo.OLTIP = #{oltip}) a left join CES_ALARM_INFO cai on (cai.alarm_name = 'oltUserOnline' and cai.alarm_time = a.STA_DATE and cai.alarm_dimension1 = a.OLTIP and (cai.delete_state IS NULL or cai.delete_state != '1')) order by STA_DATE")
    List<Olt> getUserOnline(@Param("month") String month, @Param("preMonth") String preMonth,
            @Param("oltip") String oltip, @Param("beginTime") String beginTime,
            @Param("endTime") String endTime);

    @Select("select alarm_dimension1 as oltip,to_char(alarm_time, 'yyyy-MM-dd HH24:mi:ss') as name from CES_ALARM_INFO where alarm_dimension1 = #{oltip} and alarm_name = 'oltUserOnline' and to_date(#{beginTime},'yyyy-MM-dd HH24:mi:ss') <= alarm_time and to_date(#{endTime},'yyyy-MM-dd HH24:mi:ss') >= alarm_time ")
    List<Olt> getUserOnlineAlarm(@Param("beginTime") String beginTime,
            @Param("endTime") String endTime, @Param("oltip") String oltip);

    @Select("select to_char(a.STA_DATE, 'HH24:mi') as name, a.AUTHFAILRATIO as value, CASE cai.alarm_name when 'oltAuthFailRate' THEN '1' ELSE '0' END AS isalarm from (select * from OLT_USER_AUTH_${preMonth} where trunc(sysdate - 1) <= STA_DATE and STA_DATE <= trunc(sysdate) and OLTIP = #{oltip} union all select * from OLT_USER_AUTH_${month} where trunc(sysdate - 1) <= STA_DATE and STA_DATE <= trunc(sysdate) and OLTIP = #{oltip}) a left join CES_ALARM_INFO cai on (cai.alarm_name = 'oltAuthFailRate' and cai.alarm_time = a.STA_DATE and cai.alarm_dimension1 = a.OLTIP and (cai.delete_state IS NULL or cai.delete_state != '1')) order by STA_DATE")
    List<Olt> getAuthFailRate(@Param("month") String month, @Param("preMonth") String preMonth,
            @Param("oltip") String oltip);

    @Select("select anid from CES_LINE_CHARACTER_${month} where user_name = #{account}")
    String judgeExistByAccount(@Param("month") String month, @Param("account") String account);

    @Select("select oltip from OLT_ONLINE_${month} where to_char(STA_DATE, #{format}) = #{time} group by oltip")
    List<Olt> getAllOltIp(@Param("month") String month, @Param("format") String format,
            @Param("time") String time);

    @Select("select to_char(STA_DATE, 'yyyy-MM-dd HH24:mi:ss') as NAME,OLTIP,ONLINENUM as VALUE from OLT_ONLINE_${month} where oltip = #{oltip} and to_char(STA_DATE, 'yyyy-MM') = #{yearmonth} and to_date(#{beginTime},'yyyy-MM-dd HH24:mi:ss') < STA_DATE and to_date(#{endTime},'yyyy-MM-dd HH24:mi:ss') >= STA_DATE order by STA_DATE")
    List<Olt> getOltOnlineByOltIp(@Param("month") String month, @Param("oltip") String oltip,
            @Param("yearmonth") String yearmonth, @Param("beginTime") String beginTime,
            @Param("endTime") String endTime);

    @Select("select to_char(STA_DATE, 'yyyy-MM-dd HH24:mi:ss') as NAME,OLTIP,ONLINENUM as VALUE from OLT_ONLINE_${month} where to_char(STA_DATE, 'yyyy-MM') = #{yearmonth} and to_date(#{beginTime},'yyyy-MM-dd HH24:mi:ss') < STA_DATE and to_date(#{endTime},'yyyy-MM-dd HH24:mi:ss') >= STA_DATE order by STA_DATE")
    List<Olt> getOltOnlineByStaDate(@Param("month") String month,
            @Param("yearmonth") String yearmonth, @Param("beginTime") String beginTime,
            @Param("endTime") String endTime);

    @Select("<script> select to_char(STA_DATE, 'yyyy-MM-dd HH24:mi:ss') as NAME, OLTIP, ONLINENUM as VALUE from OLT_ONLINE_${month} where to_char(STA_DATE, 'yyyy-MM-dd HH24:mi:ss') in <foreach collection='result' item='item' open='(' separator=',' close=')'> #{item} </foreach> </script>")
    List<Olt> getOltOnlineByStaDateTimes(@Param("month") String month,
            @Param(value = "result") List<String> data);

    @Select("select to_char(CAL_TIME, 'yyyy-MM-dd HH24:mi:ss') as NAME,OLTIP from OLT_ONLINE_THRESHOLD_${month} where to_date(#{beginTime}, 'yyyy-MM-dd HH24:mi:ss') <= cal_time and to_date(#{endTime}, 'yyyy-MM-dd HH24:mi:ss') >= cal_time and isalarm = 1 order by CAL_TIME")
    List<Olt> getOltOnlineThresholdAlarmByCalDateByTime(@Param("month") String month,
            @Param("beginTime") String beginTime, @Param("endTime") String endTime);

    @Select("select to_char(CAL_TIME, 'yyyy-MM-dd HH24:mi:ss') as NAME,OLTIP from OLT_ONLINE_THRESHOLD_${month} where to_char(CAL_TIME, #{format}) = #{time} and isalarm = 1 order by CAL_TIME")
    List<Olt> getOltOnlineThresholdAlarmByCalDate(@Param("month") String month,
            @Param("format") String format, @Param("time") String time);

    @Select("select to_char(CAL_TIME, 'yyyy-MM-dd HH24:mi:ss') as NAME,OLTIP from OLT_ONLINE_THRESHOLD_${month} where to_date(#{beginTime}, 'yyyy-MM-dd HH24:mi:ss') <= cal_time and to_date(#{endTime}, 'yyyy-MM-dd HH24:mi:ss') >= cal_time and oltip = #{oltip} and isalarm = 1 order by CAL_TIME")
    List<Olt> getOltOnlineThresholdAlarmByCalDateByOltIpByTime(@Param("month") String month,
            @Param("beginTime") String beginTime, @Param("endTime") String endTime,
            @Param("oltip") String oltip);

    @Select("select to_char(CAL_TIME, 'yyyy-MM-dd HH24:mi:ss') as NAME,OLTIP from OLT_ONLINE_THRESHOLD_${month} where to_char(CAL_TIME, #{format}) = #{time} and oltip = #{oltip} and isalarm = 1 order by CAL_TIME")
    List<Olt> getOltOnlineThresholdAlarmByCalDateByOltIp(@Param("month") String month,
            @Param("format") String format, @Param("time") String time,
            @Param("oltip") String oltip);

    @Select("select alarm_time,alarm_dimension1,trunc(dbms_random.value(1,4)) as alarm_level from CES_ALARM_INFO where (alarm_name = 'oltUserOnline' or alarm_name = 'oltAuthFailRate') and (delete_state IS NULL or delete_state != '1') and to_date(#{beginTime}, 'yyyy-MM-dd HH24:mi:ss') <= alarm_time and to_date(#{endTime}, 'yyyy-MM-dd HH24:mi:ss') >= alarm_time")
    List<AlarmInfo> getCesAlarmInfoOltMapByTime(@Param("beginTime") String beginTime,
            @Param("endTime") String endTime);

    @Select("select alarm_time,alarm_dimension1,trunc(dbms_random.value(1,4)) as alarm_level from CES_ALARM_INFO where (alarm_name = 'oltUserOnline' or alarm_name = 'oltAuthFailRate') and (delete_state IS NULL or delete_state != '1') and to_char(alarm_time, #{format}) = #{time}")
    List<AlarmInfo> getCesAlarmInfoOltMap(@Param("format") String format,
            @Param("time") String time);

    @Select("select alarm_time,alarm_dimension1,trunc(dbms_random.value(1,4)) as alarm_level from CES_ALARM_INFO where (alarm_name = 'oltUserOnline' or alarm_name = 'oltAuthFailRate') and (delete_state IS NULL or delete_state != '1') and to_date(#{beginTime}, 'yyyy-MM-dd HH24:mi:ss') <= alarm_time and to_date(#{endTime}, 'yyyy-MM-dd HH24:mi:ss') >= alarm_time and alarm_dimension1 = #{oltip}")
    List<AlarmInfo> getCesAlarmInfoOltMapByOltIpByTime(@Param("beginTime") String beginTime,
            @Param("endTime") String endTime, @Param("oltip") String oltip);

    @Select("select alarm_time,alarm_dimension1,trunc(dbms_random.value(1,4)) as alarm_level from CES_ALARM_INFO where (alarm_name = 'oltUserOnline' or alarm_name = 'oltAuthFailRate') and (delete_state IS NULL or delete_state != '1') and to_char(alarm_time, #{format}) = #{time} and alarm_dimension1 = #{oltip}")
    List<AlarmInfo> getCesAlarmInfoOltMapByOltIp(@Param("format") String format,
            @Param("time") String time, @Param("oltip") String oltip);

    @Select("select OLTIP from (select oo.* from OLT_ONLINE_${preMonth} oo where trunc(to_date(#{time}, 'yyyy-MM-dd HH24:mi:ss') - 7) <= oo.STA_DATE and oo.STA_DATE < trunc(to_date(#{time}, 'yyyy-MM-dd HH24:mi:ss')) union all select oo.* from OLT_ONLINE_${month} oo where trunc(to_date(#{time}, 'yyyy-MM-dd HH24:mi:ss') - 7) <= oo.STA_DATE and oo.STA_DATE < trunc(to_date(#{time}, 'yyyy-MM-dd HH24:mi:ss'))) group by OLTIP")
    List<Olt> getMidOltIp(@Param("month") String month, @Param("preMonth") String preMonth,
            @Param("time") String time);

    @Select("select round(avg(c.onlinenum)) as value from (select rownum as id, b.* from (select a.onlinenum from (select oo.* from OLT_ONLINE_${preMonth} oo where trunc(to_date(#{time}, 'yyyy-MM-dd HH24:mi:ss') - 7) <= oo.STA_DATE and oo.STA_DATE < trunc(to_date(#{time}, 'yyyy-MM-dd HH24:mi:ss')) and oo.oltip = #{oltip} union all select oo.* from OLT_ONLINE_${month} oo where trunc(to_date(#{time}, 'yyyy-MM-dd HH24:mi:ss') - 7) <= oo.STA_DATE and oo.STA_DATE < trunc(to_date(#{time}, 'yyyy-MM-dd HH24:mi:ss')) and oo.oltip = #{oltip}) a order by a.onlinenum) b) c where id = (select floor((count(1) / 2) + 0.5) from (select oo.* from OLT_ONLINE_${preMonth} oo where trunc(to_date(#{time}, 'yyyy-MM-dd HH24:mi:ss') - 7) <= oo.STA_DATE and oo.STA_DATE < trunc(to_date(#{time}, 'yyyy-MM-dd HH24:mi:ss')) and oo.oltip = #{oltip} union all select oo.* from OLT_ONLINE_${month} oo where trunc(to_date(#{time}, 'yyyy-MM-dd HH24:mi:ss') - 7) <= oo.STA_DATE and oo.STA_DATE < trunc(to_date(#{time}, 'yyyy-MM-dd HH24:mi:ss')) and oo.oltip = #{oltip})) or id = (select ceil((count(1) / 2) + 0.5) from (select oo.* from OLT_ONLINE_${preMonth} oo where trunc(to_date(#{time}, 'yyyy-MM-dd HH24:mi:ss') - 7) <= oo.STA_DATE and oo.STA_DATE < trunc(to_date(#{time}, 'yyyy-MM-dd HH24:mi:ss')) and oo.oltip = #{oltip} union all select oo.* from OLT_ONLINE_${month} oo where trunc(to_date(#{time}, 'yyyy-MM-dd HH24:mi:ss') - 7) <= oo.STA_DATE and oo.STA_DATE < trunc(to_date(#{time}, 'yyyy-MM-dd HH24:mi:ss')) and oo.oltip = #{oltip}))")
    Olt getMidOltNum(@Param("month") String month, @Param("preMonth") String preMonth,
            @Param("oltip") String oltip, @Param("time") String time);

    @Select("select alarm_time from (select to_char(alarm_time, 'yyyy-MM-dd HH24:mi:ss') as alarm_time from CES_OLT_HISTORY order by alarm_time desc) where rownum <= 1")
    Olt getCesOltHistoryNewAlarmTime();

    @Delete("DELETE FROM OLT_ONLINE_THRESHOLD_${month} WHERE to_date(#{beginTime}, 'yyyy-MM-dd HH24:mi:ss') <= cal_time and to_date(#{endTime}, 'yyyy-MM-dd HH24:mi:ss') >= cal_time")
    void deleteOltOnlineThresholdByTime(@Param("month") String month,
            @Param("beginTime") String beginTime, @Param("endTime") String endTime);

    @Delete("DELETE FROM OLT_ONLINE_THRESHOLD_${month} WHERE to_char(cal_time, #{format}) = #{time}")
    void deleteOltOnlineThreshold(@Param("month") String month, @Param("format") String format,
            @Param("time") String time);

    @Delete("DELETE FROM OLT_ONLINE_THRESHOLD_${month} WHERE to_char(cal_time, #{format}) = #{time} and OLTIP = #{oltip}")
    void deleteOltOnlineThresholdByOltip(@Param("month") String month,
            @Param("format") String format, @Param("time") String time,
            @Param("oltip") String oltip);

    @Delete("DELETE FROM OLT_ONLINE_THRESHOLD_${month} WHERE to_date(#{beginTime}, 'yyyy-MM-dd HH24:mi:ss') <= cal_time and to_date(#{endTime}, 'yyyy-MM-dd HH24:mi:ss') >= cal_time and OLTIP = #{oltip}")
    void deleteOltOnlineThresholdByOltipByTime(@Param("month") String month,
            @Param("beginTime") String beginTime, @Param("endTime") String endTime,
            @Param("oltip") String oltip);

    @Delete("DELETE FROM CES_ALARM_INFO WHERE to_date(#{beginTime}, 'yyyy-MM-dd HH24:mi:ss') <= ALARM_TIME and to_date(#{endTime}, 'yyyy-MM-dd HH24:mi:ss') >= ALARM_TIME and ALARM_NAME = 'oltUserOnline'")
    void deleteCesAlarmInfoOltUserOnlineByTime(@Param("beginTime") String beginTime,
            @Param("endTime") String endTime);

    @Delete("DELETE FROM CES_ALARM_INFO WHERE to_char(ALARM_TIME, #{format}) = #{time} and ALARM_NAME = 'oltUserOnline'")
    void deleteCesAlarmInfoOltUserOnline(@Param("format") String format,
            @Param("time") String time);

    @Delete("DELETE FROM CES_ALARM_INFO WHERE to_date(#{beginTime}, 'yyyy-MM-dd HH24:mi:ss') <= ALARM_TIME and to_date(#{endTime}, 'yyyy-MM-dd HH24:mi:ss') >= ALARM_TIME and ALARM_NAME = 'oltUserOnline' and ALARM_DIMENSION1 = #{oltip}")
    void deleteCesAlarmInfoOltUserOnlineByOltipByTime(@Param("beginTime") String beginTime,
            @Param("endTime") String endTime, @Param("oltip") String oltip);

    @Delete("DELETE FROM CES_ALARM_INFO WHERE to_char(ALARM_TIME, #{format}) = #{time} and ALARM_NAME = 'oltUserOnline' and ALARM_DIMENSION1 = #{oltip}")
    void deleteCesAlarmInfoOltUserOnlineByOltip(@Param("format") String format,
            @Param("time") String time, @Param("oltip") String oltip);

    @Delete("DELETE FROM CES_ALARM_INFO WHERE to_date(#{beginTime}, 'yyyy-MM-dd HH24:mi:ss') <= ALARM_TIME and to_date(#{endTime}, 'yyyy-MM-dd HH24:mi:ss') >= ALARM_TIME and ALARM_NAME = 'oltMap'")
    void deleteCesAlarmInfoOltMapByTime(@Param("beginTime") String beginTime,
            @Param("endTime") String endTime);

    @Delete("DELETE FROM CES_ALARM_INFO WHERE to_char(ALARM_TIME, #{format}) = #{time} and ALARM_NAME = 'oltMap'")
    void deleteCesAlarmInfoOltMap(@Param("format") String format, @Param("time") String time);

    @Delete("DELETE FROM CES_ALARM_INFO WHERE to_date(#{beginTime}, 'yyyy-MM-dd HH24:mi:ss') <= ALARM_TIME and to_date(#{endTime}, 'yyyy-MM-dd HH24:mi:ss') >= ALARM_TIME and ALARM_NAME = 'oltMap' and ALARM_DIMENSION1 = #{oltip}")
    void deleteCesAlarmInfoOltMapByOltipByTime(@Param("beginTime") String beginTime,
            @Param("endTime") String endTime, @Param("oltip") String oltip);

    @Delete("DELETE FROM CES_ALARM_INFO WHERE to_char(ALARM_TIME, #{format}) = #{time} and ALARM_NAME = 'oltMap' and ALARM_DIMENSION1 = #{oltip}")
    void deleteCesAlarmInfoOltMapByOltip(@Param("format") String format, @Param("time") String time,
            @Param("oltip") String oltip);

    @Delete("DELETE FROM OLT_MID_ONLINE WHERE OLTIP = #{oltip}")
    void deleteOltMidOnlineByOltip(@Param("oltip") String oltip);

    @Insert("<script> insert all" + "  <foreach collection='result' item='item'> "
            + " into OLT_ONLINE_THRESHOLD_${month} (CAL_TIME,OLTIP,UPPER_VALUE,LOWER_VALUE,FORECAST_VALUE,ISALARM) values (to_date(#{item.name},'yyyy-MM-dd HH24:mi:ss'),#{item.oltip},#{item.upperValue},#{item.lowerValue},#{item.forecastValue},#{item.isalarm}) \n"
            + "  </foreach>  select 1 from dual </script>")
    Boolean insertOltOnlineThreshold(@Param("month") String month,
            @Param(value = "result") List<Olt> data);

    @Insert("<script> insert all" + "  <foreach collection='result' item='item'> "
            + " into CES_ALARM_INFO (ALARM_ID, ALARM_NAME, ALARM_TIME, ALARM_DIMENSION1, ALARM_DIMENSION2, CREATE_TIME) values (sys_guid(),#{alarmName},to_date(#{item.name},'yyyy-MM-dd HH24:mi:ss'),#{item.oltip},#{alarmDimension2},SYSDATE) \n"
            + "  </foreach>  select 1 from dual </script>")
    Boolean insertCesAlarmInfoByOlt(@Param(value = "result") List<Olt> data,
            @Param("alarmName") String alarmName, @Param("alarmDimension2") String alarmDimension2);

    @Insert("<script> insert all" + "  <foreach collection='result' item='item'> "
            + " into CES_ALARM_INFO (ALARM_ID, ALARM_NAME,ALARM_LEVEL, ALARM_TIME, ALARM_DIMENSION1, CREATE_TIME) values (sys_guid(),#{alarmName},#{item.alarm_level},#{item.alarm_time},#{item.alarm_dimension1},SYSDATE) \n"
            + "  </foreach>  select 1 from dual </script>")
    Boolean insertCesAlarmInfo(@Param(value = "result") List<AlarmInfo> data,
            @Param("alarmName") String alarmName);

    @Insert("insert into OLT_MID_ONLINE (OLTIP,ONLINEMIDNUM,STA_DATE) values (#{oltip},#{value},TRUNC(to_date(#{time}, 'yyyy-MM-dd HH24:mi:ss')))")
    Boolean insertMidOlt(@Param("oltip") String oltip, @Param("value") String value,
            @Param("time") String time);

    // olt设备最近20条告警
    @Select("select * from (select * from CES_OLT_HISTORY order by alarm_time desc, mid_value desc) where rownum <= 20")
    List<Olt> oltList();

    // 查询总条数
    @SelectProvider(method = "getOltHistoryCountNew", type = SqlProvider.class)
    Integer getOltHistoryCount(@Param("oltHistory") OltHistory oltHistory,
            @Param("month") String month);

    // 查询olt历史告警（分页）
    @SelectProvider(method = "getOltHistoryListNew", type = SqlProvider.class)
    List<Olt> getOltHistoryList(@Param("oltHistory") OltHistory oltHistory,
            @Param("page") Page page, @Param("month") String month);

    // 查询olt历史告警（不分页）
    @SelectProvider(method = "getOltHistoryListNotPage", type = SqlProvider.class)
    List<Olt> getOltHistoryListNotPage(@Param("oltHistory") OltHistory oltHistory);

    // 在线用户数中值
    @Select("select onlinemidnum as mid_value\n" + "  from OLT_MID_ONLINE\n"
            + " where oltip = #{oltip}\n" + "   and rownum <= 1\n" + " order by sta_date desc")
    Olt getOltMidValue(@Param("month") String month, @Param("oltip") String oltip);

    // 累计告警数
    @Select("select count(1) as alarm_num\n" + "  from CES_ALARM_INFO\n"
            + " where alarm_name = 'oltUserOnline'\n" + "   and alarm_dimension1 = #{oltip}\n"
            + "   and (delete_state IS NULL or delete_state != '1')")
    Olt getOltAlarmNum(@Param("month") String month, @Param("oltip") String oltip);

    // 近24小时告警数
    @Select("select count(1) as alarm_nums\n" + "  from CES_ALARM_INFO\n"
            + " where alarm_name = 'oltUserOnline'\n" + "   and alarm_dimension1 = #{oltip}\n"
            + "   and ((sysdate - 1) <= alarm_time and alarm_time <= sysdate)\n"
            + "   and (delete_state IS NULL or delete_state != '1')")
    Olt getOltAlarmNums(@Param("month") String month, @Param("oltip") String oltip);

    @Select("select * from (select * from (select * from (select nma.alarm_time, nma.alarm_dimension1 as oltip, m.onlinemidnum as mid_value, nma.onlinenum as now_value, m.onlinemidnum - nma.onlinenum as down_value, round(((m.onlinemidnum - nma.onlinenum) / m.onlinemidnum) * 100, 2) as down_per from (select a.alarm_time, a.alarm_dimension1, coalesce(o1.onlinenum, o2.onlinenum, o3.onlinenum, o4.onlinenum, o5.onlinenum, o6.onlinenum, o7.onlinenum, o8.onlinenum, o9.onlinenum, o10.onlinenum, o11.onlinenum, o12.onlinenum) as onlinenum from CES_ALARM_INFO a left join OLT_ONLINE_01 o1 on (a.alarm_time = o1.sta_date and a.alarm_dimension1 = o1.oltip) left join OLT_ONLINE_02 o2 on (a.alarm_time = o2.sta_date and a.alarm_dimension1 = o2.oltip) left join OLT_ONLINE_03 o3 on (a.alarm_time = o3.sta_date and a.alarm_dimension1 = o3.oltip) left join OLT_ONLINE_04 o4 on (a.alarm_time = o4.sta_date and a.alarm_dimension1 = o4.oltip) left join OLT_ONLINE_05 o5 on (a.alarm_time = o5.sta_date and a.alarm_dimension1 = o5.oltip) left join OLT_ONLINE_06 o6 on (a.alarm_time = o6.sta_date and a.alarm_dimension1 = o6.oltip) left join OLT_ONLINE_07 o7 on (a.alarm_time = o7.sta_date and a.alarm_dimension1 = o7.oltip) left join OLT_ONLINE_08 o8 on (a.alarm_time = o8.sta_date and a.alarm_dimension1 = o8.oltip) left join OLT_ONLINE_09 o9 on (a.alarm_time = o9.sta_date and a.alarm_dimension1 = o9.oltip) left join OLT_ONLINE_10 o10 on (a.alarm_time = o10.sta_date and a.alarm_dimension1 = o10.oltip) left join OLT_ONLINE_11 o11 on (a.alarm_time = o11.sta_date and a.alarm_dimension1 = o11.oltip) left join OLT_ONLINE_12 o12 on (a.alarm_time = o12.sta_date and a.alarm_dimension1 = o12.oltip) where a.alarm_name = 'oltUserOnline' and TRUNC(a.alarm_time) not in (select TRUNC(m.sta_date) from OLT_MID_ONLINE m where m.oltip = a.alarm_dimension1) order by a.alarm_time desc) nma left join (select * from (select oltip, sta_date, onlinemidnum, row_number() over(partition by oltip order by sta_date desc) as rn from OLT_MID_ONLINE) where rn = 1) m on (m.oltip = nma.alarm_dimension1)) where mid_value > 1000 union all select * from (select alarm_time, alarm_dimension1 as oltip, onlinemidnum as mid_value, onlinenum as now_value, onlinemidnum - onlinenum as down_value, round(((onlinemidnum - onlinenum) / onlinemidnum) * 100, 2) as down_per from (select a.alarm_time, a.alarm_dimension1, m.onlinemidnum, coalesce(o1.onlinenum, o2.onlinenum, o3.onlinenum, o4.onlinenum, o5.onlinenum, o6.onlinenum, o7.onlinenum, o8.onlinenum, o9.onlinenum, o10.onlinenum, o11.onlinenum, o12.onlinenum) as onlinenum from CES_ALARM_INFO a left join OLT_MID_ONLINE m on (m.oltip = a.alarm_dimension1 and TRUNC(a.alarm_time) = TRUNC(m.sta_date)) left join OLT_ONLINE_01 o1 on (a.alarm_time = o1.sta_date and a.alarm_dimension1 = o1.oltip) left join OLT_ONLINE_02 o2 on (a.alarm_time = o2.sta_date and a.alarm_dimension1 = o2.oltip) left join OLT_ONLINE_03 o3 on (a.alarm_time = o3.sta_date and a.alarm_dimension1 = o3.oltip) left join OLT_ONLINE_04 o4 on (a.alarm_time = o4.sta_date and a.alarm_dimension1 = o4.oltip) left join OLT_ONLINE_05 o5 on (a.alarm_time = o5.sta_date and a.alarm_dimension1 = o5.oltip) left join OLT_ONLINE_06 o6 on (a.alarm_time = o6.sta_date and a.alarm_dimension1 = o6.oltip) left join OLT_ONLINE_07 o7 on (a.alarm_time = o7.sta_date and a.alarm_dimension1 = o7.oltip) left join OLT_ONLINE_08 o8 on (a.alarm_time = o8.sta_date and a.alarm_dimension1 = o8.oltip) left join OLT_ONLINE_09 o9 on (a.alarm_time = o9.sta_date and a.alarm_dimension1 = o9.oltip) left join OLT_ONLINE_10 o10 on (a.alarm_time = o10.sta_date and a.alarm_dimension1 = o10.oltip) left join OLT_ONLINE_11 o11 on (a.alarm_time = o11.sta_date and a.alarm_dimension1 = o11.oltip) left join OLT_ONLINE_12 o12 on (a.alarm_time = o12.sta_date and a.alarm_dimension1 = o12.oltip) where a.alarm_name = 'oltUserOnline' and m.onlinemidnum > 1000))) order by alarm_time desc, mid_value desc)")
    List<Olt> getAllOltHistoryList();

    @Delete("DELETE FROM CES_OLT_HISTORY")
    void deleteCesOltHistory();

    @Delete("DELETE FROM CES_OLT_HISTORY WHERE alarm_time = to_date(#{beginTime},'yyyy-MM-dd HH24:mi:ss')")
    void deleteCesOltHistoryByTime(@Param("beginTime") String beginTime);

    @Delete("DELETE FROM CES_OLT_HISTORY WHERE alarm_time >= to_date(#{beginTime},'yyyy-MM-dd HH24:mi:ss') and alarm_time <= to_date(#{endTime},'yyyy-MM-dd HH24:mi:ss')")
    void deleteCesOltHistoryByPart(@Param("beginTime") String beginTime,
            @Param("endTime") String endTime);

    @Insert("insert into CES_OLT_HISTORY (ALARM_TIME,OLTIP,MID_VALUE,NOW_VALUE,DOWN_VALUE,DOWN_PER) values (#{oltHistory.alarm_time},#{oltHistory.oltip},#{oltHistory.mid_value},#{oltHistory.now_value},#{oltHistory.down_value},#{oltHistory.down_per})")
    Boolean insertOltHistory(@Param("oltHistory") Olt oltHistory);

    @Insert("insert into CES_OLT_HISTORY select alarm_time,oltip,mid_value,now_value,down_value,trim('.' from to_char(down_per,'fm99999999990.99')) as down_per from (select * from (select * from (select nma.alarm_time, nma.alarm_dimension1 as oltip, m.onlinemidnum as mid_value, nma.onlinenum as now_value, m.onlinemidnum - nma.onlinenum as down_value, round(((m.onlinemidnum - nma.onlinenum) / m.onlinemidnum) * 100, 2) as down_per from (select a.alarm_time, a.alarm_dimension1, coalesce(o1.onlinenum, o2.onlinenum, o3.onlinenum, o4.onlinenum, o5.onlinenum, o6.onlinenum, o7.onlinenum, o8.onlinenum, o9.onlinenum, o10.onlinenum, o11.onlinenum, o12.onlinenum) as onlinenum from CES_ALARM_INFO a left join OLT_ONLINE_01 o1 on (a.alarm_time = o1.sta_date and a.alarm_dimension1 = o1.oltip) left join OLT_ONLINE_02 o2 on (a.alarm_time = o2.sta_date and a.alarm_dimension1 = o2.oltip) left join OLT_ONLINE_03 o3 on (a.alarm_time = o3.sta_date and a.alarm_dimension1 = o3.oltip) left join OLT_ONLINE_04 o4 on (a.alarm_time = o4.sta_date and a.alarm_dimension1 = o4.oltip) left join OLT_ONLINE_05 o5 on (a.alarm_time = o5.sta_date and a.alarm_dimension1 = o5.oltip) left join OLT_ONLINE_06 o6 on (a.alarm_time = o6.sta_date and a.alarm_dimension1 = o6.oltip) left join OLT_ONLINE_07 o7 on (a.alarm_time = o7.sta_date and a.alarm_dimension1 = o7.oltip) left join OLT_ONLINE_08 o8 on (a.alarm_time = o8.sta_date and a.alarm_dimension1 = o8.oltip) left join OLT_ONLINE_09 o9 on (a.alarm_time = o9.sta_date and a.alarm_dimension1 = o9.oltip) left join OLT_ONLINE_10 o10 on (a.alarm_time = o10.sta_date and a.alarm_dimension1 = o10.oltip) left join OLT_ONLINE_11 o11 on (a.alarm_time = o11.sta_date and a.alarm_dimension1 = o11.oltip) left join OLT_ONLINE_12 o12 on (a.alarm_time = o12.sta_date and a.alarm_dimension1 = o12.oltip) where a.alarm_name = 'oltUserOnline' and TRUNC(a.alarm_time) not in (select TRUNC(m.sta_date) from OLT_MID_ONLINE m where m.oltip = a.alarm_dimension1) order by a.alarm_time desc) nma left join (select * from (select oltip, sta_date, onlinemidnum, row_number() over(partition by oltip order by sta_date desc) as rn from OLT_MID_ONLINE) where rn = 1) m on (m.oltip = nma.alarm_dimension1)) where mid_value > 1000 union all select * from (select alarm_time, alarm_dimension1 as oltip, onlinemidnum as mid_value, onlinenum as now_value, onlinemidnum - onlinenum as down_value, round(((onlinemidnum - onlinenum) / onlinemidnum) * 100, 2) as down_per from (select a.alarm_time, a.alarm_dimension1, m.onlinemidnum, coalesce(o1.onlinenum, o2.onlinenum, o3.onlinenum, o4.onlinenum, o5.onlinenum, o6.onlinenum, o7.onlinenum, o8.onlinenum, o9.onlinenum, o10.onlinenum, o11.onlinenum, o12.onlinenum) as onlinenum from CES_ALARM_INFO a left join OLT_MID_ONLINE m on (m.oltip = a.alarm_dimension1 and TRUNC(a.alarm_time) = TRUNC(m.sta_date)) left join OLT_ONLINE_01 o1 on (a.alarm_time = o1.sta_date and a.alarm_dimension1 = o1.oltip) left join OLT_ONLINE_02 o2 on (a.alarm_time = o2.sta_date and a.alarm_dimension1 = o2.oltip) left join OLT_ONLINE_03 o3 on (a.alarm_time = o3.sta_date and a.alarm_dimension1 = o3.oltip) left join OLT_ONLINE_04 o4 on (a.alarm_time = o4.sta_date and a.alarm_dimension1 = o4.oltip) left join OLT_ONLINE_05 o5 on (a.alarm_time = o5.sta_date and a.alarm_dimension1 = o5.oltip) left join OLT_ONLINE_06 o6 on (a.alarm_time = o6.sta_date and a.alarm_dimension1 = o6.oltip) left join OLT_ONLINE_07 o7 on (a.alarm_time = o7.sta_date and a.alarm_dimension1 = o7.oltip) left join OLT_ONLINE_08 o8 on (a.alarm_time = o8.sta_date and a.alarm_dimension1 = o8.oltip) left join OLT_ONLINE_09 o9 on (a.alarm_time = o9.sta_date and a.alarm_dimension1 = o9.oltip) left join OLT_ONLINE_10 o10 on (a.alarm_time = o10.sta_date and a.alarm_dimension1 = o10.oltip) left join OLT_ONLINE_11 o11 on (a.alarm_time = o11.sta_date and a.alarm_dimension1 = o11.oltip) left join OLT_ONLINE_12 o12 on (a.alarm_time = o12.sta_date and a.alarm_dimension1 = o12.oltip) where a.alarm_name = 'oltUserOnline' and m.onlinemidnum > 1000))) order by alarm_time desc, mid_value desc)")
    Boolean insertAllOltHistory();

    @Insert("insert into CES_OLT_HISTORY select alarm_time, oltip, mid_value, now_value, down_value, trim('.' from to_char(down_per, 'fm99999999990.99')) as down_per from (select * from (select * from (select nma.alarm_time, nma.alarm_dimension1 as oltip, m.onlinemidnum as mid_value, nma.onlinenum as now_value, m.onlinemidnum - nma.onlinenum as down_value, round(((m.onlinemidnum - nma.onlinenum) / m.onlinemidnum) * 100, 2) as down_per from (select a.alarm_time, a.alarm_dimension1, coalesce(o1.onlinenum, o2.onlinenum, o3.onlinenum, o4.onlinenum, o5.onlinenum, o6.onlinenum, o7.onlinenum, o8.onlinenum, o9.onlinenum, o10.onlinenum, o11.onlinenum, o12.onlinenum) as onlinenum from CES_ALARM_INFO a left join OLT_ONLINE_01 o1 on (a.alarm_time = o1.sta_date and a.alarm_dimension1 = o1.oltip) left join OLT_ONLINE_02 o2 on (a.alarm_time = o2.sta_date and a.alarm_dimension1 = o2.oltip) left join OLT_ONLINE_03 o3 on (a.alarm_time = o3.sta_date and a.alarm_dimension1 = o3.oltip) left join OLT_ONLINE_04 o4 on (a.alarm_time = o4.sta_date and a.alarm_dimension1 = o4.oltip) left join OLT_ONLINE_05 o5 on (a.alarm_time = o5.sta_date and a.alarm_dimension1 = o5.oltip) left join OLT_ONLINE_06 o6 on (a.alarm_time = o6.sta_date and a.alarm_dimension1 = o6.oltip) left join OLT_ONLINE_07 o7 on (a.alarm_time = o7.sta_date and a.alarm_dimension1 = o7.oltip) left join OLT_ONLINE_08 o8 on (a.alarm_time = o8.sta_date and a.alarm_dimension1 = o8.oltip) left join OLT_ONLINE_09 o9 on (a.alarm_time = o9.sta_date and a.alarm_dimension1 = o9.oltip) left join OLT_ONLINE_10 o10 on (a.alarm_time = o10.sta_date and a.alarm_dimension1 = o10.oltip) left join OLT_ONLINE_11 o11 on (a.alarm_time = o11.sta_date and a.alarm_dimension1 = o11.oltip) left join OLT_ONLINE_12 o12 on (a.alarm_time = o12.sta_date and a.alarm_dimension1 = o12.oltip) where a.alarm_name = 'oltUserOnline' and TRUNC(a.alarm_time) not in (select TRUNC(m.sta_date) from OLT_MID_ONLINE m where m.oltip = a.alarm_dimension1) order by a.alarm_time desc) nma left join (select * from (select oltip, sta_date, onlinemidnum, row_number() over(partition by oltip order by sta_date desc) as rn from OLT_MID_ONLINE) where rn = 1) m on (m.oltip = nma.alarm_dimension1)) where mid_value > 1000 and (alarm_time > to_date(#{beginTime}, 'yyyy-MM-dd HH24:mi:ss')) union all select * from (select alarm_time, alarm_dimension1 as oltip, onlinemidnum as mid_value, onlinenum as now_value, onlinemidnum - onlinenum as down_value, round(((onlinemidnum - onlinenum) / onlinemidnum) * 100, 2) as down_per from (select a.alarm_time, a.alarm_dimension1, m.onlinemidnum, coalesce(o1.onlinenum, o2.onlinenum, o3.onlinenum, o4.onlinenum, o5.onlinenum, o6.onlinenum, o7.onlinenum, o8.onlinenum, o9.onlinenum, o10.onlinenum, o11.onlinenum, o12.onlinenum) as onlinenum from CES_ALARM_INFO a left join OLT_MID_ONLINE m on (m.oltip = a.alarm_dimension1 and TRUNC(a.alarm_time) = TRUNC(m.sta_date)) left join OLT_ONLINE_01 o1 on (a.alarm_time = o1.sta_date and a.alarm_dimension1 = o1.oltip) left join OLT_ONLINE_02 o2 on (a.alarm_time = o2.sta_date and a.alarm_dimension1 = o2.oltip) left join OLT_ONLINE_03 o3 on (a.alarm_time = o3.sta_date and a.alarm_dimension1 = o3.oltip) left join OLT_ONLINE_04 o4 on (a.alarm_time = o4.sta_date and a.alarm_dimension1 = o4.oltip) left join OLT_ONLINE_05 o5 on (a.alarm_time = o5.sta_date and a.alarm_dimension1 = o5.oltip) left join OLT_ONLINE_06 o6 on (a.alarm_time = o6.sta_date and a.alarm_dimension1 = o6.oltip) left join OLT_ONLINE_07 o7 on (a.alarm_time = o7.sta_date and a.alarm_dimension1 = o7.oltip) left join OLT_ONLINE_08 o8 on (a.alarm_time = o8.sta_date and a.alarm_dimension1 = o8.oltip) left join OLT_ONLINE_09 o9 on (a.alarm_time = o9.sta_date and a.alarm_dimension1 = o9.oltip) left join OLT_ONLINE_10 o10 on (a.alarm_time = o10.sta_date and a.alarm_dimension1 = o10.oltip) left join OLT_ONLINE_11 o11 on (a.alarm_time = o11.sta_date and a.alarm_dimension1 = o11.oltip) left join OLT_ONLINE_12 o12 on (a.alarm_time = o12.sta_date and a.alarm_dimension1 = o12.oltip) where a.alarm_name = 'oltUserOnline' and m.onlinemidnum > 1000 and (a.alarm_time > to_date(#{beginTime}, 'yyyy-MM-dd HH24:mi:ss'))))) order by alarm_time desc, mid_value desc)")
    Boolean insertOltHistoryByBeginTime(@Param("beginTime") String beginTime);

    @Insert("insert into CES_OLT_HISTORY select alarm_time, oltip, mid_value, now_value, down_value, trim('.' from to_char(down_per, 'fm99999999990.99')) as down_per from (select * from (select * from (select nma.alarm_time, nma.alarm_dimension1 as oltip, m.onlinemidnum as mid_value, nma.onlinenum as now_value, m.onlinemidnum - nma.onlinenum as down_value, round(((m.onlinemidnum - nma.onlinenum) / m.onlinemidnum) * 100, 2) as down_per from (select a.alarm_time, a.alarm_dimension1, coalesce(o1.onlinenum, o2.onlinenum, o3.onlinenum, o4.onlinenum, o5.onlinenum, o6.onlinenum, o7.onlinenum, o8.onlinenum, o9.onlinenum, o10.onlinenum, o11.onlinenum, o12.onlinenum) as onlinenum from CES_ALARM_INFO a left join OLT_ONLINE_01 o1 on (a.alarm_time = o1.sta_date and a.alarm_dimension1 = o1.oltip) left join OLT_ONLINE_02 o2 on (a.alarm_time = o2.sta_date and a.alarm_dimension1 = o2.oltip) left join OLT_ONLINE_03 o3 on (a.alarm_time = o3.sta_date and a.alarm_dimension1 = o3.oltip) left join OLT_ONLINE_04 o4 on (a.alarm_time = o4.sta_date and a.alarm_dimension1 = o4.oltip) left join OLT_ONLINE_05 o5 on (a.alarm_time = o5.sta_date and a.alarm_dimension1 = o5.oltip) left join OLT_ONLINE_06 o6 on (a.alarm_time = o6.sta_date and a.alarm_dimension1 = o6.oltip) left join OLT_ONLINE_07 o7 on (a.alarm_time = o7.sta_date and a.alarm_dimension1 = o7.oltip) left join OLT_ONLINE_08 o8 on (a.alarm_time = o8.sta_date and a.alarm_dimension1 = o8.oltip) left join OLT_ONLINE_09 o9 on (a.alarm_time = o9.sta_date and a.alarm_dimension1 = o9.oltip) left join OLT_ONLINE_10 o10 on (a.alarm_time = o10.sta_date and a.alarm_dimension1 = o10.oltip) left join OLT_ONLINE_11 o11 on (a.alarm_time = o11.sta_date and a.alarm_dimension1 = o11.oltip) left join OLT_ONLINE_12 o12 on (a.alarm_time = o12.sta_date and a.alarm_dimension1 = o12.oltip) where a.alarm_name = 'oltUserOnline' and TRUNC(a.alarm_time) not in (select TRUNC(m.sta_date) from OLT_MID_ONLINE m where m.oltip = a.alarm_dimension1) order by a.alarm_time desc) nma left join (select * from (select oltip, sta_date, onlinemidnum, row_number() over(partition by oltip order by sta_date desc) as rn from OLT_MID_ONLINE) where rn = 1) m on (m.oltip = nma.alarm_dimension1)) where mid_value > 1000 and (alarm_time = to_date(#{beginTime}, 'yyyy-MM-dd HH24:mi:ss')) union all select * from (select alarm_time, alarm_dimension1 as oltip, onlinemidnum as mid_value, onlinenum as now_value, onlinemidnum - onlinenum as down_value, round(((onlinemidnum - onlinenum) / onlinemidnum) * 100, 2) as down_per from (select a.alarm_time, a.alarm_dimension1, m.onlinemidnum, coalesce(o1.onlinenum, o2.onlinenum, o3.onlinenum, o4.onlinenum, o5.onlinenum, o6.onlinenum, o7.onlinenum, o8.onlinenum, o9.onlinenum, o10.onlinenum, o11.onlinenum, o12.onlinenum) as onlinenum from CES_ALARM_INFO a left join OLT_MID_ONLINE m on (m.oltip = a.alarm_dimension1 and TRUNC(a.alarm_time) = TRUNC(m.sta_date)) left join OLT_ONLINE_01 o1 on (a.alarm_time = o1.sta_date and a.alarm_dimension1 = o1.oltip) left join OLT_ONLINE_02 o2 on (a.alarm_time = o2.sta_date and a.alarm_dimension1 = o2.oltip) left join OLT_ONLINE_03 o3 on (a.alarm_time = o3.sta_date and a.alarm_dimension1 = o3.oltip) left join OLT_ONLINE_04 o4 on (a.alarm_time = o4.sta_date and a.alarm_dimension1 = o4.oltip) left join OLT_ONLINE_05 o5 on (a.alarm_time = o5.sta_date and a.alarm_dimension1 = o5.oltip) left join OLT_ONLINE_06 o6 on (a.alarm_time = o6.sta_date and a.alarm_dimension1 = o6.oltip) left join OLT_ONLINE_07 o7 on (a.alarm_time = o7.sta_date and a.alarm_dimension1 = o7.oltip) left join OLT_ONLINE_08 o8 on (a.alarm_time = o8.sta_date and a.alarm_dimension1 = o8.oltip) left join OLT_ONLINE_09 o9 on (a.alarm_time = o9.sta_date and a.alarm_dimension1 = o9.oltip) left join OLT_ONLINE_10 o10 on (a.alarm_time = o10.sta_date and a.alarm_dimension1 = o10.oltip) left join OLT_ONLINE_11 o11 on (a.alarm_time = o11.sta_date and a.alarm_dimension1 = o11.oltip) left join OLT_ONLINE_12 o12 on (a.alarm_time = o12.sta_date and a.alarm_dimension1 = o12.oltip) where a.alarm_name = 'oltUserOnline' and m.onlinemidnum > 1000 and (a.alarm_time = to_date(#{beginTime}, 'yyyy-MM-dd HH24:mi:ss'))))) order by alarm_time desc, mid_value desc)")
    Boolean insertOltHistoryByTime(@Param("beginTime") String beginTime);

    @Insert("insert into CES_OLT_HISTORY select alarm_time, oltip, mid_value, now_value, down_value, trim('.' from to_char(down_per, 'fm99999999990.99')) as down_per from (select * from (select * from (select nma.alarm_time, nma.alarm_dimension1 as oltip, m.onlinemidnum as mid_value, nma.onlinenum as now_value, m.onlinemidnum - nma.onlinenum as down_value, round(((m.onlinemidnum - nma.onlinenum) / m.onlinemidnum) * 100, 2) as down_per from (select a.alarm_time, a.alarm_dimension1, coalesce(o1.onlinenum, o2.onlinenum, o3.onlinenum, o4.onlinenum, o5.onlinenum, o6.onlinenum, o7.onlinenum, o8.onlinenum, o9.onlinenum, o10.onlinenum, o11.onlinenum, o12.onlinenum) as onlinenum from CES_ALARM_INFO a left join OLT_ONLINE_01 o1 on (a.alarm_time = o1.sta_date and a.alarm_dimension1 = o1.oltip) left join OLT_ONLINE_02 o2 on (a.alarm_time = o2.sta_date and a.alarm_dimension1 = o2.oltip) left join OLT_ONLINE_03 o3 on (a.alarm_time = o3.sta_date and a.alarm_dimension1 = o3.oltip) left join OLT_ONLINE_04 o4 on (a.alarm_time = o4.sta_date and a.alarm_dimension1 = o4.oltip) left join OLT_ONLINE_05 o5 on (a.alarm_time = o5.sta_date and a.alarm_dimension1 = o5.oltip) left join OLT_ONLINE_06 o6 on (a.alarm_time = o6.sta_date and a.alarm_dimension1 = o6.oltip) left join OLT_ONLINE_07 o7 on (a.alarm_time = o7.sta_date and a.alarm_dimension1 = o7.oltip) left join OLT_ONLINE_08 o8 on (a.alarm_time = o8.sta_date and a.alarm_dimension1 = o8.oltip) left join OLT_ONLINE_09 o9 on (a.alarm_time = o9.sta_date and a.alarm_dimension1 = o9.oltip) left join OLT_ONLINE_10 o10 on (a.alarm_time = o10.sta_date and a.alarm_dimension1 = o10.oltip) left join OLT_ONLINE_11 o11 on (a.alarm_time = o11.sta_date and a.alarm_dimension1 = o11.oltip) left join OLT_ONLINE_12 o12 on (a.alarm_time = o12.sta_date and a.alarm_dimension1 = o12.oltip) where a.alarm_name = 'oltUserOnline' and TRUNC(a.alarm_time) not in (select TRUNC(m.sta_date) from OLT_MID_ONLINE m where m.oltip = a.alarm_dimension1) order by a.alarm_time desc) nma left join (select * from (select oltip, sta_date, onlinemidnum, row_number() over(partition by oltip order by sta_date desc) as rn from OLT_MID_ONLINE) where rn = 1) m on (m.oltip = nma.alarm_dimension1)) where mid_value > 1000 and alarm_time >= to_date(#{beginTime}, 'yyyy-MM-dd HH24:mi:ss') and alarm_time <= to_date(#{endTime}, 'yyyy-MM-dd HH24:mi:ss') union all select * from (select alarm_time, alarm_dimension1 as oltip, onlinemidnum as mid_value, onlinenum as now_value, onlinemidnum - onlinenum as down_value, round(((onlinemidnum - onlinenum) / onlinemidnum) * 100, 2) as down_per from (select a.alarm_time, a.alarm_dimension1, m.onlinemidnum, coalesce(o1.onlinenum, o2.onlinenum, o3.onlinenum, o4.onlinenum, o5.onlinenum, o6.onlinenum, o7.onlinenum, o8.onlinenum, o9.onlinenum, o10.onlinenum, o11.onlinenum, o12.onlinenum) as onlinenum from CES_ALARM_INFO a left join OLT_MID_ONLINE m on (m.oltip = a.alarm_dimension1 and TRUNC(a.alarm_time) = TRUNC(m.sta_date)) left join OLT_ONLINE_01 o1 on (a.alarm_time = o1.sta_date and a.alarm_dimension1 = o1.oltip) left join OLT_ONLINE_02 o2 on (a.alarm_time = o2.sta_date and a.alarm_dimension1 = o2.oltip) left join OLT_ONLINE_03 o3 on (a.alarm_time = o3.sta_date and a.alarm_dimension1 = o3.oltip) left join OLT_ONLINE_04 o4 on (a.alarm_time = o4.sta_date and a.alarm_dimension1 = o4.oltip) left join OLT_ONLINE_05 o5 on (a.alarm_time = o5.sta_date and a.alarm_dimension1 = o5.oltip) left join OLT_ONLINE_06 o6 on (a.alarm_time = o6.sta_date and a.alarm_dimension1 = o6.oltip) left join OLT_ONLINE_07 o7 on (a.alarm_time = o7.sta_date and a.alarm_dimension1 = o7.oltip) left join OLT_ONLINE_08 o8 on (a.alarm_time = o8.sta_date and a.alarm_dimension1 = o8.oltip) left join OLT_ONLINE_09 o9 on (a.alarm_time = o9.sta_date and a.alarm_dimension1 = o9.oltip) left join OLT_ONLINE_10 o10 on (a.alarm_time = o10.sta_date and a.alarm_dimension1 = o10.oltip) left join OLT_ONLINE_11 o11 on (a.alarm_time = o11.sta_date and a.alarm_dimension1 = o11.oltip) left join OLT_ONLINE_12 o12 on (a.alarm_time = o12.sta_date and a.alarm_dimension1 = o12.oltip) where a.alarm_name = 'oltUserOnline' and m.onlinemidnum > 1000 and a.alarm_time >= to_date(#{beginTime}, 'yyyy-MM-dd HH24:mi:ss') and a.alarm_time <= to_date(#{endTime}, 'yyyy-MM-dd HH24:mi:ss')))) order by alarm_time desc, mid_value desc)")
    Boolean insertOltHistoryByPart(@Param("beginTime") String beginTime,
            @Param("endTime") String endTime);
}