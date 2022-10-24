package com.asiainfo.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.chartData.CrmFigure;

public interface CesCrmInfoMapper {
    @Select("select charge_type,fee_value,bb_fee,product_begin_time,product_end_time,order_type,order_time,"
            + " phone,bandwidth_down,bandwidth_up,iptv_flag,customer_name,address"
            + " from CES_CRM_INFO_${month}"
            + " where user_name=#{account}")
    CrmFigure getUserInfo(@Param("account") String account, @Param("month") String month);
}
