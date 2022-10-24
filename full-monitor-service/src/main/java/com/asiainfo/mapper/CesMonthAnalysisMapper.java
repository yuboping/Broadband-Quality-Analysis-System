package com.asiainfo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.chartData.MonitorInfo;
import com.asiainfo.model.chartData.UserKeyfigures;

public interface CesMonthAnalysisMapper {

    @Select("select sum(active_num) as value,'ACTIVE' as attr from CES_MONTH_ANALYSIS where statistics_date=#{date} and city_code!='0000' union all "
            + "select sum(silent_num) as value,'SILENT' as attr from CES_MONTH_ANALYSIS where statistics_date=#{date} and city_code!='0000' union all "
            + "select sum(stop_num) as value,'STOP' as attr from CES_MONTH_ANALYSIS where statistics_date=#{date} and city_code!='0000'")
    List<MonitorInfo> userStructure(@Param("date") String date);
    
    @Select("select city_code as attr,sum(add_num) as addNum, sum(reg_num) as regNum, sum(active_num) as activeNum,"
            + " sum(silent_num) as silentNum, sum(cancel_num) as cancelNum, sum(own_num) as ownNum, sum(stop_num) as stopNum"
            + " from CES_MONTH_ANALYSIS  where statistics_date = #{date}"
            + " and city_code in(select city_code from ces_city_code where city_code!='0000')"
            + " group by city_code order by sum(reg_num) asc")
    List<UserKeyfigures> userScatterForCity(String date);

    @Select("<script>select statistics_date as attr,sum(add_num) as addNum, sum(reg_num) as regNum,"
            + " sum(active_num) as activeNum,sum(silent_num) as silentNum, sum(cancel_num) as cancelNum,"
            + " sum(own_num) as ownNum, sum(stop_num) as stopNum,sum(second_offnet_num) secondOffnetNum,"
            + " sum(third_offnet_num) thirdOffnetNum from CES_MONTH_ANALYSIS "
            + " where statistics_date <![CDATA[>=]]> #{startDate} and statistics_date <![CDATA[<=]]> #{endDate}"
            + " and city_code in(select city_code from ces_city_code where city_code!='0000')"
            + "<if test=\"city_code != '0000' \"> and city_code=#{city_code}</if>"
            + " group by statistics_date  order by sum(reg_num) asc"
            + "</script>")
    List<UserKeyfigures> userScatterForDate(@Param("city_code") String city_code,
            @Param("startDate") String startDate, @Param("endDate") String endDate);
}