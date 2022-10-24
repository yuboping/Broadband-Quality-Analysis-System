package com.asiainfo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.chartData.Evaluation;

public interface CesModelEvaluationMapper {
    @Select("<script>select collect_cycle attr,round(precision,2) precision,round(recall,2) recall,"
            + " round(f1_score,2) f1_score  from CES_MODEL_EVALUATION"
            + " where prob_range=#{prob_range}"
            + " and collect_cycle <![CDATA[>=]]> #{startDate} and collect_cycle <![CDATA[<=]]> #{endDate}"
            + " and city_code=#{city_code}"
            + " </script>")
    List<Evaluation> evaluationForMonth(@Param("prob_range") String prob_range,
            @Param("city_code") String city_code, @Param("startDate") String startDate,
            @Param("endDate") String endDate);
}
