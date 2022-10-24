package com.asiainfo.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.chartData.FaultFigure;

public interface CesBpppPcCharMapper {
    @Select("select disabled_num,call_back_ave,is_send_urg,source_com,group_error_flag_num,list_time_out,diff_ratio,"
            + " fixed_ratio,deal_num_ave,total_num_ave,service_type_disabled,rep_roce_times,cust_hurry_times,"
            + " inner_hurry_times,report_times"
            + " from CES_BPPP_PC_CHAR_${month}"
            + " where busi_nbr=#{account}")
    FaultFigure getUserInfo(@Param("account") String account, @Param("month") String month);
}
