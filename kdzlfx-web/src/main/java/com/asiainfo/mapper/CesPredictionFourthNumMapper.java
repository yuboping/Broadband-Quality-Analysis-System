package com.asiainfo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.chartData.UserPredictionInfo;

public interface CesPredictionFourthNumMapper {
    /**
     * 各地市数据
     * 
     * @param city_code
     * @param startDate
     * @param endDate
     * @return
     */
    @Select("select collect_cycle attr,round(off_rate,2) off_rate,percent_80_85,percent_85_90,percent_90_95,"
            + " percent_95_100,round(off_num/all_num*100,2) as notrenewed from CES_PREDICTION_FOURTH_NUM"
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
    @Select("select collect_cycle attr,round(sum(off_num)/sum(all_num)*100,2) off_num,sum(percent_80_85) percent_80_85,"
            + " sum(percent_85_90) percent_85_90,sum(percent_90_95) percent_90_95,sum(percent_95_100) percent_95_100,"
            + " round(sum(off_num)/sum(all_num)*100,2) as notrenewed from CES_PREDICTION_FOURTH_NUM"
            + " where collect_cycle >= #{startDate} and collect_cycle <= #{endDate} "
            + " group by collect_cycle")
    List<UserPredictionInfo> predictionForProvince(@Param("startDate") String startDate,
            @Param("endDate") String endDate);
}
