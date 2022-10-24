package com.asiainfo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import com.asiainfo.model.chartData.AAAFigure;
import com.asiainfo.model.chartData.ParamRelation;
import com.asiainfo.model.chartData.UserFigureInfo;

public interface UserBillMapper {
    @Select("select TIMES dial_sum, timelen timelen_sum,INOCTETS inoctets_sum ,OUTOCTETS outoctets_sum ,OUTPACKETS outpackets_sum ,INPACKETS inpackets_sum "
            + "from USER_BILL_${month} where username=#{account} and rownum = 1 ")
    AAAFigure getUserInfo(@Param("account") String account, @Param("month") String month);

    @SelectProvider(method = "getOffnetTendency", type = SqlProvider.class)
    List<UserFigureInfo> getUserTendency(@Param("account") String account,
            @Param("months") List<ParamRelation> months);

    @SelectProvider(method = "getOffnetTendencyHealth", type = SqlProvider.class)
    List<UserFigureInfo> getUserTendencyHealth(@Param("account") String account,
            @Param("months") List<ParamRelation> months);
}
