package com.asiainfo.service;

import java.util.List;

import com.asiainfo.model.chart.ChartDatas;

public interface UserPrediction {
    /**
     * 一级用户离网预测
     * 
     * @param cityCode
     * @return
     */
    List<ChartDatas> firstUserPredictionCity(String cityCode);

    /**
     * 四级用户离网预测
     * 
     * @param cityCode
     * @return
     */
    List<ChartDatas> fourthUserPredictionCity(String cityCode);

    /**
     * 一级用户离网率
     * 
     * @param cityCode
     * @return
     */
    List<ChartDatas> firstOffnetRate(String cityCode);

    /**
     * 预测结果
     * 
     * @param parameter
     * @return
     */
    List<ChartDatas> predictResult(String parameter);

    /**
     * 四级用户离网率
     * 
     * @param cityCode
     * @return
     */
    List<ChartDatas> fourthOffnetRate(String cityCode);

    /**
     * 四级用户到期未续约率
     * 
     * @param cityCode
     * @return
     */
    List<ChartDatas> fourthNotrenewedRate(String cityCode);

}
