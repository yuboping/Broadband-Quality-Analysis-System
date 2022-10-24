package com.asiainfo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.chartData.MonitorInfo;

public interface CesMonthItemAnalysisMapper {

    @Select("select sta_attr attr,sum(attr_value) value from CES_MONTH_ITEM_ANALYSIS"
            + " where sta_item=#{item} and statistics_date=#{date}"
            + " group by sta_attr order by sta_attr")
    List<MonitorInfo> userStructure(@Param("item") String item, @Param("date") String date);

    @Select("<script>select statistics_date attr,sum(attr_value) value from CES_MONTH_ITEM_ANALYSIS"
            + " where sta_item=#{item} and sta_attr=#{attr}"
            + " and statistics_date <![CDATA[>=]]> #{startDate} and statistics_date <![CDATA[<=]]> #{endDate}"
            + " <if test=\"city_code != '0000' \"> and city_code=#{city_code}</if>"
            + " group by statistics_date"
            + "</script>")
    List<MonitorInfo> expiringUser(@Param("item") String item, @Param("attr") String attr,
            @Param("startDate") String startDate, @Param("endDate") String endDate,
            @Param("city_code") String city_code);

    @Select("select STA_ATTR attr,ATTR_NAME value from CES_MONTH_ITEM_ANALYSIS"
            + " where sta_item=#{item} and statistics_date=#{date}"
            + " group by STA_ATTR,ATTR_NAME order by sta_attr")
    List<MonitorInfo> userStructureAttrs(@Param("item") String item, @Param("date") String date);

}