package com.asiainfo.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.chartData.ComplaintFigure;

public interface CesBpppComplaintCharMapper {
    @Select("select l.*,ce.CITY_NAME "
            + " from CES_COMPLAINT_CHARACTER_${month} l left join CES_CITY_CODE ce on l.CITY_CODE=ce.CITY_CODE "
            + " where user_name=#{account} and rownum = 1")
    ComplaintFigure getUserInfo(@Param("account") String account, @Param("month") String month);
}
