package com.asiainfo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.chartData.UserPredictionInfo;

public interface CesPredictionNumMapper {
    /**
     * 各地市数据
     * 
     * @param city_code
     * @param startDate
     * @param endDate
     * @return
     */
    @Select("select collect_cycle attr,ROUND(off_rate,2) off_rate,percent_50_55,percent_55_60,percent_60_65,"
            + " percent_65_70,percent_70_75,percent_75_80,percent_80_85,percent_85_90,percent_90_95,"
            + " percent_95_100 from CES_PREDICTION_NUM"
            + " where collect_cycle >= #{startDate} and collect_cycle <= #{endDate} "
            + " and city_code = #{city_code}")
    List<UserPredictionInfo> predictionForCity(@Param("city_code") String city_code,
            @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 全省数据
     * 
     * @param city_code
     * @param startDate
     * @return
     */
    @Select("select collect_cycle attr,ROUND(sum(off_num)/sum(all_num)*100,2) off_num,sum(percent_50_55) percent_50_55,"
            + " sum(percent_55_60) percent_55_60,sum(percent_60_65) percent_60_65,sum(percent_65_70) percent_65_70,"
            + " sum(percent_70_75) percent_70_75,sum(percent_75_80) percent_75_80,sum(percent_80_85) percent_80_85,"
            + " sum(percent_85_90) percent_85_90,sum(percent_90_95) percent_90_95,sum(percent_95_100) percent_95_100"
            + " from CES_PREDICTION_NUM"
            + " where collect_cycle >= #{startDate} and collect_cycle <= #{endDate} "
            + " group by collect_cycle")
    List<UserPredictionInfo> predictionForProvince(@Param("startDate") String startDate,
            @Param("endDate") String endDate);
}
