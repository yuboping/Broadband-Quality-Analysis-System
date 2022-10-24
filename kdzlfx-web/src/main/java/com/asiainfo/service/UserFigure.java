package com.asiainfo.service;

import java.util.List;

import com.asiainfo.model.chart.ChartDatas;
import com.asiainfo.model.chartData.OptHistory;

public interface UserFigure {
    /**
     * 用户画像基本信息
     * 
     * @param account
     * @return
     */
    List<ChartDatas> getUserInfo(String account);

    /**
     * 用户预测离网概率趋势、上行流量趋势、下行流量趋势
     * 
     * @param account
     * @return
     */
    List<ChartDatas> getUserTendency(String account);

    /**
     * 用户操作历史
     * 
     * @param parameter
     * @return
     */
    List<OptHistory> getHistory(String account);
}
