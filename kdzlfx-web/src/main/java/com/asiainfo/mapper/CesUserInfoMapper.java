package com.asiainfo.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.chartData.UserFigureInfo;

public interface CesUserInfoMapper {
    @Select("select user_name,off_prob*100 off_prob,off_level,product_time,order_type,product_type,product_remaining_time,"
            + " subscription_status,open_time,suspend_time,reopen_time,cancel_time,"
            + " round(inoctets_sum/1024/1024,1) inoctets_sum,round(outoctets_sum /1024/1024,1) outoctets_sum "
            + " from CES_USER_INFO_${month}"
            + " where user_name=#{account} and  statistics_time=#{statistics_time} ")
    UserFigureInfo getUserInfo(@Param("account") String account, @Param("month") String month,
            @Param("statistics_time") String statistics_time);

}