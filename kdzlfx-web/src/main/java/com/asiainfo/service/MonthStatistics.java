package com.asiainfo.service;

import java.util.List;

import com.asiainfo.model.chart.ChartDatas;
import com.asiainfo.model.chartData.OltInfo;

/**
 * 月统计表数据查询
 * 
 * @author luohuawuyin
 *
 */
public interface MonthStatistics {
    /**
     * 用户分布：注册、活跃、静默、停机用户数
     * 
     * @return
     */
    List<ChartDatas> userScatterForCity();

    /**
     * 区域对比：注册用户、新增用户、销户用户、净增用户、活跃用户、静默用户、停机用户
     * 
     * @param month
     * @return
     */
    List<ChartDatas> allkindsNumForCity(String month);

    /**
     * 区域发展：注册用户、新增用户、销户用户、净增用户、活跃用户、静默用户、停机用户
     * 
     * @param cityCode
     * @param queryDate
     * @return
     */
    List<ChartDatas> allkindsNumForDate(String cityCode, String queryDate);

    /**
     * 静默用户数趋势
     * 
     * @param cityCode
     * @return
     */
    List<ChartDatas> silenceNumForDate(String cityCode);

    /**
     * 停机用户数趋势
     * 
     * @param cityCode
     * @return
     */
    List<ChartDatas> stopNumForDate(String cityCode);

    /**
     * 二级、三级预警用户
     * 
     * @param cityCode
     * @return
     */
    List<ChartDatas> warningUser(String cityCode);

    /**
     * 
     * @Title: userOlt
     * @Description: TODO(olt)
     * @param @return 参数
     * @return List<ChartDatas> 返回类型
     * @throws
     */
    List<OltInfo> userOlt();

    /**
     * 
     * @Title: userOltByOltip
     * @Description: TODO(oltip查询olt)
     * @param @param oltip
     * @param @return 参数
     * @return List<OltInfo> 返回类型
     * @throws
     */
    List<OltInfo> userOltByOltip(String oltip);
}
