package com.asiainfo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.chartData.MonitorInfo;

public interface CesMonitorMonthMapper {


    @Select("<script>select mon_month attr,sum(mon_value) value from CES_MONITOR_MONTH "
            + " where mon_item=#{item}"
            + " and mon_month <![CDATA[>=]]> #{startDate} and mon_month <![CDATA[<=]]> #{endDate}"
            + "<if test=\"city_code != '0000' \"> and city_code=#{city_code}</if>"
            + " group by mon_month"
            + "</script>")
    List<MonitorInfo> monitorForMonth(@Param("item") String item,
            @Param("city_code") String city_code, @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    @Select("<script>select a.mon_month attr,round(sum(b.mon_value)/sum(a.mon_value)*100,2) value"
            + " from CES_MONITOR_MONTH a inner join CES_MONITOR_MONTH b"
            + " on a.mon_month=b.mon_month and a.city_code=b.city_code"
            + " where a.mon_item=#{denominator} and b.mon_item=#{numerator}"
            + " and a.mon_month <![CDATA[>=]]> #{startDate} and a.mon_month <![CDATA[<=]]> #{endDate}"
            + "<if test=\"city_code != '0000' \"> and a.city_code=#{city_code}</if>"
            + " group by a.mon_month"
            + "</script>")
    List<MonitorInfo> correlationRate(@Param("numerator") String numerator,
            @Param("denominator") String denominator, @Param("city_code") String city_code,
            @Param("startDate") String startDate, @Param("endDate") String endDate);

}