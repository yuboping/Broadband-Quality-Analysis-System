package com.asiainfo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.chartData.MonitorInfo;

public interface CesPredictOffnetMapper {

    @Select("select '00_30' attr,count(1) value from CES_PREDICT_OFFNET_${month} where PROB > 0.7"
            + "union all select '30_60' attr,count(1) value from CES_PREDICT_OFFNET_${month} where PROB <= 0.7 AND PROB > 0.4 "
            + "union all select '60_90' attr,count(1) value from CES_PREDICT_OFFNET_${month} where PROB <= 0.4 AND PROB > 0.1"
            + "union all select '90_100' attr,count(1) value from CES_PREDICT_OFFNET_${month} where PROB <= 0.1")
    List<MonitorInfo> userStructure(@Param("month") String month);

    @Select("<script>select statistics_date attr,sum(attr_value) value from CES_MONTH_ITEM_ANALYSIS"
            + " where sta_item=#{item} and sta_attr=#{attr}"
            + " and statistics_date <![CDATA[>=]]> #{startDate} and statistics_date <![CDATA[<=]]> #{endDate}"
            + " <if test=\"city_code != '0000' \"> and city_code=#{city_code}</if>"
            + " group by statistics_date" + "</script>")
    List<MonitorInfo> expiringUser(@Param("item") String item, @Param("attr") String attr,
            @Param("startDate") String startDate, @Param("endDate") String endDate,
            @Param("city_code") String city_code);

}