package com.asiainfo.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.asiainfo.model.chart.ChartDatas;
import com.asiainfo.model.chartData.Olt;
import com.asiainfo.model.chartData.OltHistory;
import com.asiainfo.util.page.Page;

public interface LinebindMonitor {
    /**
     * OLT设备展示
     * 
     * @param account
     * @return
     */
    List<ChartDatas> getOltEquipmentInfo(String oltip);

    /**
     * 
     * @Title: getUserOnline @Description: TODO(获取在线用户数) @param @param
     *         oltip @param @return 参数 @return List<ChartDatas> 返回类型 @throws
     */
    List<Olt> getUserOnline(String oltip, String time);

    /**
     * 
     * @Title: getAuthFailRate @Description: TODO(获取认证失败率) @param @param
     *         oltip @param @return 参数 @return List<ChartDatas> 返回类型 @throws
     */
    List<Olt> getAuthFailRate(String oltip);

    /**
     * 
     * @Title: judgeExistByAccount @Description:
     *         TODO(根据用户名判断是否存在OLT信息) @param @param account @param @return
     *         参数 @return String 返回类型 @throws
     */
    String judgeExistByAccount(String account);

    /**
     * 
     * @Title: forecastOltOnline @Description:
     *         TODO(时间序列分解和异常检测OLT在线用户数) @param @param time @param @param
     *         format @param @return 参数 @return String 返回类型 @throws
     */
    List<Olt> forecastOltOnline(String format, String time, String beginTime, String endTime,
            String oltip, List<Olt> oltAllCurrentOriginalList, Boolean isGetUseDB,
            Boolean isSetUseDB);

    /**
     * 
     * @Title: isCesOltCollect @Description: TODO(是否采集到数据) @param @param
     *         format @param @param time @param @param beginTime @param @param
     *         endTime @param @param oltip @param @param
     *         oltAllCurrentOriginalList @param @param isGetUseDB @param @param
     *         isSetUseDB 参数 @return void 返回类型 @throws
     */
    List<Olt> isCesOltCollect(String format, String time, String beginTime, String endTime,
            String oltip, List<Olt> oltAllCurrentOriginalList, Boolean isGetUseDB,
            Boolean isSetUseDB);

    /**
     *
     * @Title: forecastOltOnlineAuto @Description:
     *         TODO(自动时间序列分解和异常检测OLT在线用户数) @param @param beginTime @param @param
     *         endTime @param @param oltip 参数 @return void 返回类型 @throws
     */
    void forecastOltOnlineAuto(String beginTime, String endTime, String oltip);

    /**
     * olt设备最近20条告警
     * 
     * @return
     */
    List<Olt> oltList();

    /**
     * 查询OLT设备历史告警
     * 
     * @param oltHistory
     * @param pageNumber
     * @return
     */
    Page getOltHistoryList(OltHistory oltHistory, int pageNumber);

    /**
     * oltIP详情
     * 
     * @param oltip
     * @return
     */
    List<ChartDatas> getOltInfo(String oltip);

    /**
     *
     * @Title: forecastOltOnlineGUI @Description:
     *         TODO(时间序列分解和异常检测OLT在线用户数GUI显示) @param @param format @param @param
     *         time @param @param beginTime @param @param endTime @param @param
     *         oltip @param @param oltAllCurrentOriginalList @param @param
     *         isUseDB @param @return 参数 @return List<Olt> 返回类型 @throws
     */
    List<Olt> forecastOltOnlineGUI(String format, String time, String beginTime, String endTime,
            String oltip, List<Olt> oltAllCurrentOriginalList, Boolean isUseDB);

    /**
     * 
     * @Title: refreshCesOltHistory @Description: TODO(刷新OLT设备历史告警数据) @param
     *         参数 @return void 返回类型 @throws
     */
    void refreshCesOltHistory(String type, String beginTime, String endTime);

    /**
     * 导出OLT设备历史告警
     * 
     * @param oltHistory
     * @param pageNumber
     * @return
     */
    public HSSFWorkbook exportReport(OltHistory oltHistory);

    /**
     * 
     * @Title: egadsForecastOltRefreshOltList @Description: TODO(刷新OLT内存) @param
     *         参数 @return void 返回类型 @throws
     */
    void egadsForecastOltRefreshOltList(String time, String beginTime, String endTime);

    /**
     * 
     * @Title: getUserOnlineMapInfo @Description:
     * TODO(OLT在线用户数信息分析数据) @param @param oltip @param @param
     * time @param @return 参数 @return Map<String,Object> 返回类型 @throws
     */
    Map<String, Object> getUserOnlineMapInfo(String oltip, String time);
}
