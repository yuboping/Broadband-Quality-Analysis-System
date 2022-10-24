package com.asiainfo.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.chartData.AAAFigure;

public interface CesBpppMonthCollectMapper {
    @Select("select timelen timelen_sum,totaloctets outoctets_sum "
            + "from USER_BILL_${month} where username=#{account}")
    AAAFigure getUserInfo(@Param("account") String account, @Param("month") String month);
}
