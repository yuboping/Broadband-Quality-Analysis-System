package com.asiainfo.service;

import java.util.List;

import com.asiainfo.model.chart.ChartDatas;

/**
 * 日统计表数据查询
 * 
 * @author luohuawuyin
 *
 */
public interface DayStatistics {
    /**
     * 昨天关键指标：新增、销户、净增、停机及累计用户数
     * 
     * @return
     */
    List<ChartDatas> yesKeyfigures();

    /**
     * 近30天各用户数情况
     * 
     * @return
     */
    List<ChartDatas> nearly30DaysKeyfigures();

}
