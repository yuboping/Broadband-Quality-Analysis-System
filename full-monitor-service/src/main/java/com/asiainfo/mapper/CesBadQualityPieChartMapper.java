package com.asiainfo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.chartData.BadQualityPieChartVO;

public interface CesBadQualityPieChartMapper {

//    @Select("SELECT inteligentgateway AS name, COUNT(*) AS value FROM CES_TERMINAL_CHARACTER_${month} GROUP BY inteligentgateway")
//    List<BadQualityPieChartVO> getInnerPieData(@Param("month") String month);
//
//    @Select("SELECT inteligentgateway AS innerValue, badquality AS name, COUNT(*) AS value FROM CES_TERMINAL_CHARACTER_${month} GROUP BY inteligentgateway,badquality")
//    List<BadQualityPieChartVO> getOutterPieData(@Param("month") String month);

    @Select("SELECT STA_ATTR AS name, SUM(ATTR_VALUE) AS value FROM CES_MONTH_ITEM_ANALYSIS WHERE STATISTICS_DATE = #{month} AND ATTR_NAME = 'cycle_inner' GROUP BY STA_ATTR ORDER BY STA_ATTR DESC")
    List<BadQualityPieChartVO> getInnerPieData(@Param("month") String month);

    @Select("SELECT STA_ATTR AS innerValue, STA_ITEM AS name, SUM(ATTR_VALUE) AS value FROM CES_MONTH_ITEM_ANALYSIS WHERE STATISTICS_DATE = #{month} AND ATTR_NAME = 'cycle_outer' GROUP BY STA_ATTR,STA_ITEM ORDER BY STA_ATTR DESC,STA_ITEM DESC")
    List<BadQualityPieChartVO> getOutterPieData(@Param("month") String month);
}
