package com.asiainfo.service;

import java.util.List;

import com.asiainfo.model.chart.ChartDatas;

public interface MonthItemAnalysis {
    /**
     * 用户融合类型结构、用户状态结构、用户健康度分布结构
     * 
     * @return
     */
    List<ChartDatas> structure();

    List<ChartDatas> expiringUser(String cityCode);

}
