package com.asiainfo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.chartData.UserKeyfigures;

public interface CesDayAnalysisMapper {

    @Select("select sum(add_num) as addNum,sum(cancel_num) as cancelNum,sum(own_num) as ownNum, "
            + " sum(stop_num) as stopNum,sum(addup_num) as cumulativeNum"
            + " from CES_DAY_ANALYSIS where statistics_date = #{date} "
            + " group by statistics_date")
    UserKeyfigures dayKeyfigures(@Param("date") String date);

    /**
     * 近30天新增、销户、净增、停机用户数
     * 
     * @param start
     * @param end
     * @return
     */
    @Select("select statistics_date as attr,sum(add_num) as addNum,sum(cancel_num) as cancelNum,"
            + " sum(own_num) as ownNum, sum(stop_num) as stopNum,sum(addup_num) as cumulativeNum"
            + " from CES_DAY_ANALYSIS where statistics_date >= #{start} and statistics_date <= #{end}"
            + " group by statistics_date")
    List<UserKeyfigures> nearly30Days(@Param("start") String start, @Param("end") String end);
}