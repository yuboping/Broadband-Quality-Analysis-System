package com.asiainfo.service;

import java.util.List;

import com.asiainfo.model.chart.ChartDatas;

public interface PredictEvaluation {
    /**
     * 训练效果
     * 
     * @param cityCode
     * @return
     */
    List<ChartDatas> trainEvaluation(String cityCode);

    /**
     * 预测效果
     * 
     * @param cityCode
     * @return
     */
    List<ChartDatas> modelEvaluation(String cityCode);

}
