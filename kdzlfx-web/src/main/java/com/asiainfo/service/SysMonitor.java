package com.asiainfo.service;

import java.util.List;

import com.asiainfo.model.chart.ChartDatas;

public interface SysMonitor {
    /**
     * AAA监控数据
     * 
     * @param cityCode
     * @return
     */
    List<ChartDatas> aaaForMonth(String cityCode);

    /**
     * CRM监控数据
     * 
     * @param cityCode
     * @return
     */
    List<ChartDatas> crmForMonth(String cityCode);

    /**
     * 报障数据
     * 
     * @param cityCode
     * @return
     */
    List<ChartDatas> faultForMonth(String cityCode);

    /**
     * 线路监控数据
     * 
     * @param cityCode
     * @return
     */
    List<ChartDatas> lineForMonth(String cityCode);

    /**
     * 行为监控数据
     * 
     * @param cityCode
     * @return
     */
    List<ChartDatas> behaviourForMonth(String cityCode);

    /**
     * 投诉数据
     * 
     * @param cityCode
     * @return
     */
    List<ChartDatas> complaintForMonth(String cityCode);

    /**
     * 特征表数量
     * 
     * @param cityCode
     * @return
     */
    List<ChartDatas> featureNumForMonth(String cityCode);

    /**
     * 特征表标记数量
     * 
     * @param cityCode
     * @return
     */
    List<ChartDatas> markingNumForMonth(String cityCode);

    /**
     * 表关联率
     * 
     * @param cityCode
     * @return
     */
    List<ChartDatas> correlationRateForMonth(String cityCode);
}
