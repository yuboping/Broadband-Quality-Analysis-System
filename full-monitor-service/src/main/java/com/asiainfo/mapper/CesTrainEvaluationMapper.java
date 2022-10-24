package com.asiainfo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.asiainfo.model.chartData.Evaluation;

public interface CesTrainEvaluationMapper {
    @Select("select collect_cycle attr,precision*100 precision,recall*100 recall,"
            + " f1_score*100 f1_score  from CES_TRAINING_EVALUATION"
            + " where prob_range=#{prob_range} and model=#{model}"
            + " and collect_cycle >= #{startDate} and collect_cycle <= #{endDate}")
    List<Evaluation> evaluationForMonth(@Param("prob_range") String prob_range,
            @Param("model") String model, @Param("startDate") String startDate,
            @Param("endDate") String endDate);
}
