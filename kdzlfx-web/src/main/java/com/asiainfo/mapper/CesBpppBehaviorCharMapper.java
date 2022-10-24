package com.asiainfo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.chartData.BehaviourFigure;
import com.asiainfo.model.chartData.MonitorInfo;

public interface CesBpppBehaviorCharMapper {
    @Select("select l.user_name,l.city_code,case when l.ottonly=0 then '仅宽带' when l.ottonly=1 then '宽带&OTT'\n"
            + " else '仅OTT' end as ottonly,ce.CITY_NAME "
            + "from CES_BEHAVIOUR_CHARACTER_${month} l left join CES_CITY_CODE ce on l.CITY_CODE=ce.CITY_CODE "
            + " where user_name=#{account} and rownum = 1")
    BehaviourFigure getUserInfo(@Param("account") String account, @Param("month") String month);

    @Select("select ottonly attr,count(1) value from CES_BEHAVIOUR_CHARACTER_${month} group by ottonly order by ottonly desc")
    List<MonitorInfo> userBroadBandType(@Param("month") String month);
    
    @Select("select HEALTH_VAL from ${table} WHERE USER_NAME=#{account}")
    String getHealthValByAccount(@Param("table") String table,@Param("account") String account);
}
