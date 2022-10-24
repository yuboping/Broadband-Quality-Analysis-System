package com.asiainfo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.chartData.UserMaintainInfo;

public interface CesMaintainNumMapper {
    @Select("<script>select sta_date attr,sum(dispatch_num) dispatch_num,sum(wx_success_num) wx_success_num,"
            + " sum(wx_total) wx_total,round(sum(wx_total)/sum(dispatch_num)*100,2) dispatch_rate,"
            + " round(sum(wx_success_num)/sum(dispatch_num)*100,2) success_rate,sum(dispatch_num_4th) dispatch_num_4th,"
            + " 0 as wx_success_num_4th,0 as wx_total_4th,0 as dispatch_rate_4th,0 as success_rate_4th"
            + " from CES_WX_FK_STATISTICS "
            + " where sta_date <![CDATA[>=]]> #{startDate} and sta_date <![CDATA[<=]]> #{endDate}"
            + "<if test=\"city_code != '0000' \"> and city_code=#{city_code}</if>"
            + " group by sta_date"
            + "</script>")
    List<UserMaintainInfo> maintainForCity(@Param("city_code") String city_code,
            @Param("startDate") String startDate, @Param("endDate") String endDate);

}
