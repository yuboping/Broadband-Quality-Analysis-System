package com.asiainfo.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.chartData.LineFigure;

public interface CesBpppLinenoCharMapper {
    @Select("select l.*,ce.CITY_NAME from CES_LINE_CHARACTER_${month} l left join CES_CITY_CODE ce on l.CITY_CODE=ce.CITY_CODE"
    		+ " where user_name=#{account} and rownum = 1")
    LineFigure getUserInfo(@Param("account") String account, @Param("month") String month);
}
