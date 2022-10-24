package com.asiainfo.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.asiainfo.mapper.OltMapper;
import com.asiainfo.model.chart.ChartDatas;
import com.asiainfo.model.chart.ChartPoint;
import com.asiainfo.model.chartData.AlarmInfo;
import com.asiainfo.model.chartData.Olt;
import com.asiainfo.model.chartData.OltHistory;
import com.asiainfo.model.chartData.OltMemory;
import com.asiainfo.model.report.ReportFieldInfo;
import com.asiainfo.service.LinebindMonitor;
import com.asiainfo.util.common.DateUtil;
import com.asiainfo.util.common.EnumUtil;
import com.asiainfo.util.common.FileUtil;
import com.asiainfo.util.page.Page;
import com.asiainfo.util.poi.ExcelDocument;
import com.yahoo.egads.Asiainfo;
import com.yahoo.egads.models.asiainfo.TimeSeriesData;
import com.yahoo.egads.models.asiainfo.TimeSeriesResult;
import com.yahoo.egads.utilities.FileUtils;

@Service
public class LinebindMonitorImpl implements LinebindMonitor {
    private static final Logger logger = LoggerFactory.getLogger(LinebindMonitorImpl.class);

    private static final String FORMATMONTH = "MM";

    private static final String FORMATYEARMONTH = "yyyy-MM";

    private static final String FORMATTIME = "yyyy-MM-dd HH24:mi:ss";

    private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final int POOL_SIZE = 600;// 线程池大小

    private static final int AUTO_POOL_SIZE = 5;// 自动线程池大小

    private static final long TIME_STAMP_DIFFERENCE = 600;// 时间戳间隔

    private static final int AUTO_DATE = 7;// 设置自动的时间的数据（天）

    private static final int SHOW_DATE = 7;// 展示折线图时间的数据（天）

    private static final int FREQUENCY = 7;// 未采集到值的告警周期

    public static final OltMemory OLT_MEMORY = new OltMemory();// 自动缓存到内存中的OLT原始数据

    private static final String ISNOTCOLLECTION = "isnotcollection";

    public static final String MAPEE_FUNCTION = "平均绝对百分比误差函数（按预期值缩放）";

    public static final String MAE_FUNCTION = "平均绝对偏差函数";

    public static final String SMAPE_FUNCTION = "对称平均绝对百分比误差函数";

    public static final String MAPE_FUNCTION = "平均绝对百分比误差函数";

    public static final String MASE_FUNCTION = "平均绝对比例误差函数";

    @Autowired
    private OltMapper oltMapper;

    @Override
    public List<ChartDatas> getOltEquipmentInfo(String oltip) {
        List<ChartDatas> ChartDatasList = new ArrayList<ChartDatas>();
        String month = DateUtil.getMinusMonths(FORMATMONTH, 0);
        Olt olt = oltMapper.getOltEquipmentInfo(month, oltip);
        ChartDatas datas = new ChartDatas();
        datas.setTitle("OLT设备信息");
        List<ChartPoint> data = new ArrayList<>();
        data.add(new ChartPoint("总用户数",
                olt.getUserTotalNum() == null ? "--" : olt.getUserTotalNum()));
        data.add(new ChartPoint("质差终端数",
                olt.getBadQualityNum() == null ? "--" : olt.getBadQualityNum()));
        data.add(new ChartPoint("普通路由器认证失败质差数量",
                olt.getRouterAuthFailNum() == null ? "--" : olt.getRouterAuthFailNum()));
        data.add(new ChartPoint("普通路由器频繁上下线质差数量", olt.getRouterUpDownFrequentNum() == null ? "--"
                : olt.getRouterUpDownFrequentNum()));
        data.add(new ChartPoint("普通路由器异常掉线质差数量",
                olt.getRouterAbnormalDropNum() == null ? "--" : olt.getRouterAbnormalDropNum()));
        data.add(new ChartPoint("智能网关认证失败质差数量",
                olt.getGatewayAuthFailNum() == null ? "--" : olt.getGatewayAuthFailNum()));
        data.add(new ChartPoint("智能网关频繁上下线质差数量", olt.getGatewayUpDownFrequentNum() == null ? "--"
                : olt.getGatewayUpDownFrequentNum()));
        data.add(new ChartPoint("智能网关异常掉线质差数量",
                olt.getGatewayAbnormalDropNum() == null ? "--" : olt.getGatewayAbnormalDropNum()));
        datas.setData(data);
        ChartDatasList.add(datas);
        return ChartDatasList;
    }

    @Override
    public List<ChartDatas> getOltInfo(String oltip) {
        List<ChartDatas> ChartDatasList = new ArrayList<ChartDatas>();
        String month = DateUtil.getMinusMonths(FORMATMONTH, 0);
        Olt olt = oltMapper.getOltMidValue(month, oltip);
        Olt olt1 = oltMapper.getOltAlarmNum(month, oltip);
        Olt olt2 = oltMapper.getOltAlarmNums(month, oltip);
        ChartDatas datas = new ChartDatas();
        datas.setTitle("OLT设备信息");
        List<ChartPoint> data = new ArrayList<>();
        data.add(new ChartPoint("绑定用户数", olt.getBind_users() == null ? "--" : olt.getBind_users()));
        data.add(new ChartPoint("在线用户数中值", olt.getMid_value() == null ? "--" : olt.getMid_value()));
        data.add(new ChartPoint("累计告警数", olt1.getAlarm_num() == null ? "--" : olt1.getAlarm_num()));
        data.add(new ChartPoint("近24小时告警数",
                olt2.getAlarm_nums() == null ? "--" : olt2.getAlarm_nums()));
        datas.setData(data);
        ChartDatasList.add(datas);
        return ChartDatasList;
    }

    @Override
    public List<Olt> getUserOnline(String oltip, String time) {
        String month = DateUtil.getMinusMonthsTime(FORMATMONTH, 0, time);
        String preMonth = DateUtil.getMinusMonthsTime(FORMATMONTH, 1, time);
        List<Olt> oltListResult = new ArrayList<Olt>();
        List<String> curTimeStampList = DateUtil.getCutTimeStamp(DateUtil.StrToDate(time),
                SHOW_DATE, TIME_STAMP_DIFFERENCE);
        List<Olt> oltList = oltMapper.getUserOnline(month, preMonth, oltip, curTimeStampList.get(0),
                time);
        List<Olt> oltAlarmList = oltMapper.getUserOnlineAlarm(curTimeStampList.get(0),
                curTimeStampList.get(curTimeStampList.size() - 1), oltip);
        for (String curTimeStamp : curTimeStampList) {
            Boolean isnotexist = true;
            for (Olt olt : oltList) {
                if (olt.getName().equals(curTimeStamp)) {
                    isnotexist = false;
                    olt.setIscollection("1");
                    olt.setName(curTimeStamp.substring(5, 16));
                    oltListResult.add(olt);
                }
            }
            if (isnotexist) {
                for (Olt oltAlarm : oltAlarmList) {
                    if (oltAlarm.getOltip().equals(oltip)
                            && oltAlarm.getName().equals(curTimeStamp)) {
                        Olt o = new Olt();
                        o.setName(curTimeStamp.substring(5, 16));
                        o.setValue("0");
                        o.setUpperValue("0");
                        o.setLowerValue("0");
                        o.setIsalarm("1");
                        o.setIscollection("0");
                        oltListResult.add(o);
                    }
                }
            }
        }
        return oltListResult;
    }

    @Override
    public List<Olt> getAuthFailRate(String oltip) {
        String month = DateUtil.getMinusMonths(FORMATMONTH, 0);
        String preMonth = DateUtil.getMinusMonths(FORMATMONTH, 1);
        List<Olt> oltList = oltMapper.getAuthFailRate(month, preMonth, oltip);
        return oltList;
    }

    @Override
    public String judgeExistByAccount(String account) {
        String month = DateUtil.getMinusMonths(FORMATMONTH, 0);
        return oltMapper.judgeExistByAccount(month, account);
    }

    @Override
    public List<Olt> forecastOltOnline(String format, String time, String beginTime, String endTime,
            String oltip, List<Olt> oltAllCurrentOriginalList, Boolean isGetUseDB,
            Boolean isSetUseDB) {
        logger.info("LinebindMonitorImpl forecastOltOnline parametertime is " + time + " Start");
        logger.info("LinebindMonitorImpl forecastOltOnline parametertime is " + time + " format:"
                + format + ";beginTime:" + beginTime + ";endTime:" + endTime + ";oltip:" + oltip
                + ";isGetUseDB:" + isGetUseDB + ";isSetUseDB:" + isSetUseDB);
        if (oltAllCurrentOriginalList == null) {
            oltAllCurrentOriginalList = new ArrayList<Olt>();
        }
        if (!isGetUseDB
                && (oltAllCurrentOriginalList == null || oltAllCurrentOriginalList.isEmpty())) {
            isGetUseDB = true;
            logger.info(
                    "LinebindMonitorImpl forecastOltOnline oltAllCurrentOriginalList isEmpty isUseDB should be true");
        }
        List<Olt> resultOltList = new ArrayList<Olt>();
        long startTime = System.nanoTime();
        try {
            if (format == null || format.isEmpty() || "".equals(format)) {
                format = FORMATTIME;
            }
            if (beginTime == null || beginTime.isEmpty() || "".equals(beginTime) || endTime == null
                    || endTime.isEmpty() || "".equals(endTime)) {
                switch (format) {
                case FORMATYEARMONTH:
                    SimpleDateFormat sdfmm = new SimpleDateFormat(FORMATYEARMONTH);
                    SimpleDateFormat sdfdmm = new SimpleDateFormat(DEFAULT_FORMAT);
                    Date datemm = sdfmm.parse(time);
                    Calendar calendarmm = Calendar.getInstance();
                    calendarmm.setTime(datemm);
                    beginTime = sdfdmm.format(calendarmm.getTime());
                    calendarmm.add(Calendar.MONTH, 1);
                    endTime = sdfdmm.format(calendarmm.getTime());
                    break;
                case FORMATTIME:
                    SimpleDateFormat sdfdd = new SimpleDateFormat(DEFAULT_FORMAT);
                    Date datedd = sdfdd.parse(time);
                    Calendar calendardd = Calendar.getInstance();
                    calendardd.setTime(datedd);
                    endTime = time;
                    calendardd.add(Calendar.DATE, -(AUTO_DATE));
                    beginTime = sdfdd.format(calendardd.getTime());
                    break;
                }
            }
            ArrayList<String> monthArr = getMonthArr(beginTime, endTime);
            if (oltip == null || oltip.isEmpty() || "".equals(oltip)) {
                List<Olt> oltTimeCurrentOriginalList = new ArrayList<Olt>();
                // 是否需要查询数据库
                if (isGetUseDB) {
                    for (int i = 0; i < monthArr.size(); i++) {
                        String yearmonth = monthArr.get(i);
                        String month = yearmonth.substring(5, 7);
                        List<Olt> oltCurrentOriginalList = new ArrayList<Olt>();
                        oltCurrentOriginalList = oltMapper.getOltOnlineByStaDate(month, yearmonth,
                                beginTime, endTime);
                        if (month.equals(time.substring(5, 7))) {
                            oltTimeCurrentOriginalList = oltCurrentOriginalList;
                        }
                        if (oltCurrentOriginalList != null && !oltCurrentOriginalList.isEmpty()) {
                            oltAllCurrentOriginalList.addAll(oltCurrentOriginalList);
                        }
                    }
                    if (oltTimeCurrentOriginalList == null
                            || oltTimeCurrentOriginalList.isEmpty()) {
                        oltTimeCurrentOriginalList = oltMapper.getOltOnlineByStaDate(
                                time.substring(5, 7), time.substring(0, 7), beginTime, endTime);
                    }
                } else {
                    oltTimeCurrentOriginalList = oltAllCurrentOriginalList;
                }
                List<String> oltIpList = new ArrayList<String>();
                for (Olt oltTimeCurrentOriginal : oltTimeCurrentOriginalList) {
                    String oltNameByFormat = oltTimeCurrentOriginal.getName();
                    switch (format) {
                    case FORMATYEARMONTH:
                        oltNameByFormat = oltTimeCurrentOriginal.getName().substring(0, 7);
                        break;
                    case FORMATTIME:
                        oltNameByFormat = oltTimeCurrentOriginal.getName();
                        break;
                    }
                    if (time.equals(oltNameByFormat)) {
                        String oltIpTimeCurrent = oltTimeCurrentOriginal.getOltip();
                        if (!oltIpList.contains(oltIpTimeCurrent)) {
                            oltIpList.add(oltIpTimeCurrent);
                        }
                    }
                }
                long startTimeThreadPool = System.nanoTime();
                logger.info("LinebindMonitorImpl forecastOltOnline parametertime is " + time
                        + " getOltOriginalList time consuming : "
                        + (startTimeThreadPool - startTime) + "ns");
                ExecutorService fixedThreadPool = Executors.newFixedThreadPool(POOL_SIZE);
                int thread_id = 1;
                // list线程安全初始化
                List<Olt> forecastCurrentAllOltList = Collections
                        .synchronizedList(new ArrayList<Olt>());
                for (String oltIp : oltIpList) {
                    String formatThread = format;
                    int threadIdThread = thread_id;
                    List<Olt> oltAllCurrentOriginalListThread = oltAllCurrentOriginalList;
                    fixedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                List<Olt> forecastCurrentOltList = egadsForecastOlt(
                                        oltAllCurrentOriginalListThread, formatThread, time, oltIp);
                                if (forecastCurrentOltList != null
                                        && !forecastCurrentOltList.isEmpty()) {
                                    forecastCurrentAllOltList.addAll(forecastCurrentOltList);
                                }
                            } catch (Exception e) {
                                logger.error(
                                        "LinebindMonitorImpl forecastOltOnline parametertime is "
                                                + time + "threadIdThread is " + threadIdThread
                                                + ": " + e.getMessage());
                            }
                        }
                    });
                    thread_id++;
                }
                fixedThreadPool.shutdown();
                fixedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
                long finishTimeThreadPool = System.nanoTime();
                logger.info("LinebindMonitorImpl forecastOltOnline parametertime is " + time
                        + " ThreadPool egadsForecastOlt time consuming : "
                        + (finishTimeThreadPool - startTimeThreadPool) + "ns");
                resultOltList = forecastCurrentAllOltList;
                // 是否需要提交数据库
                if (isSetUseDB) {
                    long startTimedeleteOltOnlineThreshold = System.nanoTime();
                    oltMapper.deleteOltOnlineThreshold(time.substring(5, 7), format, time);
                    long finishTimedeleteOltOnlineThreshold = System.nanoTime();
                    logger.info("LinebindMonitorImpl forecastOltOnline parametertime is " + time
                            + " deleteOltOnlineThreshold time consuming : "
                            + (finishTimedeleteOltOnlineThreshold
                                    - startTimedeleteOltOnlineThreshold)
                            + "ns");
                    if (forecastCurrentAllOltList != null && !forecastCurrentAllOltList.isEmpty()) {
                        long startTimeinsertOltOnlineThreshold = System.nanoTime();
                        int insertCount = 1;
                        List<Olt> insertOltList = new ArrayList<Olt>();
                        for (Olt forecastCurrentAllOlt : forecastCurrentAllOltList) {
                            insertOltList.add(forecastCurrentAllOlt);
                            // 200条数据的时候提交一次
                            if ((insertCount % 200) == 0) {
                                oltMapper.insertOltOnlineThreshold(time.substring(5, 7),
                                        insertOltList);
                                insertOltList.clear();
                            }
                            insertCount++;
                        }
                        if (insertOltList != null && !insertOltList.isEmpty()) {
                            oltMapper.insertOltOnlineThreshold(time.substring(5, 7), insertOltList);
                        }
                        long finishTimeinsertOltOnlineThreshold = System.nanoTime();
                        logger.info("LinebindMonitorImpl forecastOltOnline parametertime is " + time
                                + " insertOltOnlineThreshold time consuming : "
                                + (finishTimeinsertOltOnlineThreshold
                                        - startTimeinsertOltOnlineThreshold)
                                + "ns");
                    }
                    List<Olt> oltOnlineThresholdList = oltMapper
                            .getOltOnlineThresholdAlarmByCalDate(time.substring(5, 7), format,
                                    time);
                    oltMapper.deleteCesAlarmInfoOltUserOnline(format, time);
                    if (oltOnlineThresholdList != null && !oltOnlineThresholdList.isEmpty()) {
                        long startTimeinsertCesAlarmInfo = System.nanoTime();
                        int insertThresholdCount = 1;
                        List<Olt> insertOltThresholdList = new ArrayList<Olt>();
                        for (Olt oltOnlineThreshold : oltOnlineThresholdList) {
                            insertOltThresholdList.add(oltOnlineThreshold);
                            // 200条数据的时候提交一次
                            if ((insertThresholdCount % 200) == 0) {
                                oltMapper.insertCesAlarmInfoByOlt(insertOltThresholdList,
                                        "oltUserOnline", "");
                                insertOltThresholdList.clear();
                            }
                            insertThresholdCount++;
                        }
                        if (insertOltThresholdList != null && !insertOltThresholdList.isEmpty()) {
                            oltMapper.insertCesAlarmInfoByOlt(insertOltThresholdList,
                                    "oltUserOnline", "");
                        }
                        long finishTimeinsertCesAlarmInfo = System.nanoTime();
                        logger.info("LinebindMonitorImpl forecastOltOnline parametertime is " + time
                                + " insertCesAlarmInfo oltUserOnline time consuming : "
                                + (finishTimeinsertCesAlarmInfo - startTimeinsertCesAlarmInfo)
                                + "ns");
                    }
                    List<AlarmInfo> alarmInfoList = oltMapper.getCesAlarmInfoOltMap(format, time);
                    oltMapper.deleteCesAlarmInfoOltMap(format, time);
                    if (alarmInfoList != null && !alarmInfoList.isEmpty()) {
                        long startTimeinsertCesAlarmInfoOltMap = System.nanoTime();
                        int insertCountOltMap = 1;
                        List<AlarmInfo> insertOltList = new ArrayList<AlarmInfo>();
                        for (AlarmInfo oltAlarmInfo : alarmInfoList) {
                            insertOltList.add(oltAlarmInfo);
                            // 200条数据的时候提交一次
                            if ((insertCountOltMap % 200) == 0) {
                                oltMapper.insertCesAlarmInfo(insertOltList, "oltMap");
                                insertOltList.clear();
                            }
                            insertCountOltMap++;
                        }
                        if (insertOltList != null && !insertOltList.isEmpty()) {
                            oltMapper.insertCesAlarmInfo(insertOltList, "oltMap");
                        }
                        long finishTimeinsertCesAlarmInfoOltMap = System.nanoTime();
                        logger.info("LinebindMonitorImpl forecastOltOnline parametertime is " + time
                                + " insertCesAlarmInfo oltMap time consuming : "
                                + (finishTimeinsertCesAlarmInfoOltMap
                                        - startTimeinsertCesAlarmInfoOltMap)
                                + "ns");
                    }
                }
            } else {
                List<Olt> oltOriginalList = new ArrayList<Olt>();
                // 是否需要查询数据库
                if (isGetUseDB) {
                    for (int i = 0; i < monthArr.size(); i++) {
                        String yearmonth = monthArr.get(i);
                        String month = yearmonth.substring(5, 7);
                        List<Olt> oltCurrentOriginalList = oltMapper.getOltOnlineByOltIp(month,
                                oltip, yearmonth, beginTime, endTime);
                        oltOriginalList.addAll(oltCurrentOriginalList);
                    }
                } else {
                    oltOriginalList = oltAllCurrentOriginalList;
                }
                List<Olt> forecastCurrentOltList = egadsForecastOlt(oltOriginalList, format, time,
                        oltip);
                resultOltList = forecastCurrentOltList;
                // 是否需要提交数据库
                if (isSetUseDB) {
                    oltMapper.deleteOltOnlineThresholdByOltip(time.substring(5, 7), format, time,
                            oltip);
                    if (forecastCurrentOltList != null && !forecastCurrentOltList.isEmpty()) {
                        oltMapper.insertOltOnlineThreshold(time.substring(5, 7),
                                forecastCurrentOltList);
                    }
                    List<Olt> oltOnlineThresholdList = oltMapper
                            .getOltOnlineThresholdAlarmByCalDateByOltIp(time.substring(5, 7),
                                    format, time, oltip);
                    oltMapper.deleteCesAlarmInfoOltUserOnlineByOltip(format, time, oltip);
                    if (oltOnlineThresholdList != null && !oltOnlineThresholdList.isEmpty()) {
                        oltMapper.insertCesAlarmInfoByOlt(oltOnlineThresholdList, "oltUserOnline",
                                "");
                    }
                    List<AlarmInfo> alarmInfoList = oltMapper.getCesAlarmInfoOltMapByOltIp(format,
                            time, oltip);
                    oltMapper.deleteCesAlarmInfoOltMapByOltip(format, time, oltip);
                    if (alarmInfoList != null && !alarmInfoList.isEmpty()) {
                        oltMapper.insertCesAlarmInfo(alarmInfoList, "oltMap");
                    }
                }
            }
        } catch (InterruptedException e) {
            logger.error("LinebindMonitorImpl forecastOltOnline parametertime is " + time
                    + " errorMessage:" + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (ParseException e) {
            logger.error("LinebindMonitorImpl forecastOltOnline parametertime is " + time
                    + " errorMessage:" + e.getMessage());
        }
        long finishTime = System.nanoTime();
        logger.info("LinebindMonitorImpl forecastOltOnline parametertime is " + time
                + " time consuming : " + (finishTime - startTime) + "ns");
        logger.info("LinebindMonitorImpl forecastOltOnline parametertime is " + time + " End");
        return resultOltList;
    }

    public static ArrayList<String> getMonthArr(String minDate, String maxDate) {
        ArrayList<String> monthArr = new ArrayList<String>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(FORMATYEARMONTH);
            Calendar min = Calendar.getInstance();
            Calendar max = Calendar.getInstance();
            min.setTime(sdf.parse(minDate));
            min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
            max.setTime(sdf.parse(maxDate));
            max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
            Calendar curr = min;
            while (curr.before(max)) {
                monthArr.add(sdf.format(curr.getTime()));
                curr.add(Calendar.MONTH, 1);
            }
        } catch (ParseException e) {
            logger.error("LinebindMonitorImpl getMonthArr errorMessage:" + e.getMessage());
        }
        return monthArr;
    }

    public List<Olt> egadsForecastOlt(List<Olt> oltAllCurrentOriginalList, String format,
            String time, String oltIp) {
        String path = this.getClass().getResource("/").getPath() + "config/sample_config.ini";
        List<TimeSeriesData> timeSeriesDataOriginalList = new ArrayList<TimeSeriesData>();
        boolean isfirst = true;
        long beforeTimestamp = 0;
        TimeSeriesData beforeTimeSeriesData = new TimeSeriesData();
        List<Object> completionTimeSeriesList = new ArrayList<Object>();
        // 根据IP筛选OLT并且补全数据并转换实体类
        for (Olt oltAllCurrentOriginal : oltAllCurrentOriginalList) {
            if (oltIp.equals(oltAllCurrentOriginal.getOltip())) {
                TimeSeriesData timeSeriesDataOriginal = new TimeSeriesData();
                long timestamp = DateUtil.stringChangeTimeStamp(oltAllCurrentOriginal.getName(),
                        DEFAULT_FORMAT);
                float value = Float.parseFloat(oltAllCurrentOriginal.getValue());
                timeSeriesDataOriginal.setTimestamp(timestamp);
                timeSeriesDataOriginal.setValue(value);
                if (isfirst) {
                    isfirst = false;
                } else {
                    // 根据时间戳补全数据
                    while ((beforeTimestamp + TIME_STAMP_DIFFERENCE) < timestamp) {
                        beforeTimestamp = beforeTimestamp + TIME_STAMP_DIFFERENCE;
                        TimeSeriesData completionTimeSeriesData = new TimeSeriesData();
                        completionTimeSeriesData.setTimestamp(beforeTimestamp);
                        completionTimeSeriesData.setValue(beforeTimeSeriesData.getValue());
                        completionTimeSeriesList.add(beforeTimestamp);
                        timeSeriesDataOriginalList.add(completionTimeSeriesData);
                    }
                }
                beforeTimestamp = timestamp;
                beforeTimeSeriesData = timeSeriesDataOriginal;
                timeSeriesDataOriginalList.add(timeSeriesDataOriginal);
            }
        }
        TimeSeriesResult timeSeriesResult = new TimeSeriesResult();
        try {
            timeSeriesResult = Asiainfo.egadsTimeSeries(timeSeriesDataOriginalList, path);
        } catch (Exception e) {
            logger.info(
                    "LinebindMonitorImpl forecastOltOnline egadsForecastOlt kdzlfx.forecastOlt parametertime is "
                            + time + " OltIp : " + oltIp);
            logger.error("LinebindMonitorImpl forecastOltOnline egadsForecastOlt parametertime is "
                    + time + " errorMessage:" + e.getMessage());
        }
        List<Olt> forecastCurrentOltList = new ArrayList<Olt>();
        if (timeSeriesResult.getResultDataList() != null
                && !timeSeriesResult.getResultDataList().isEmpty()) {
            for (TimeSeriesData timeSeriesDataforecast : timeSeriesResult.getResultDataList()) {
                // 忽略补全的数据
                if (!completionTimeSeriesList.contains(timeSeriesDataforecast.getTimestamp())) {
                    String oltName = DateUtil.timeStampChangeString(
                            timeSeriesDataforecast.getTimestamp(), DEFAULT_FORMAT);
                    String oltNameByFormat = oltName;
                    switch (format) {
                    case FORMATYEARMONTH:
                        oltNameByFormat = oltNameByFormat.substring(0, 7);
                        break;
                    case FORMATTIME:
                        break;
                    }
                    // 筛选出需要预测时间点的OLT
                    if (time.equals(oltNameByFormat)) {
                        // 预测的数据四舍五入取整
                        DecimalFormat df = new DecimalFormat("##");
                        String value = df.format(timeSeriesDataforecast.getValue());
                        String forecastValue = df.format(timeSeriesDataforecast.getForecastValue());
                        String isalarm = "0";
                        if (timeSeriesDataforecast.getIsalarm()) {
                            isalarm = "1";
                        }
                        Olt forecastCurrentOlt = new Olt();
                        forecastCurrentOlt.setOltip(oltIp);
                        forecastCurrentOlt.setName(oltName);
                        forecastCurrentOlt.setValue(value);
                        forecastCurrentOlt.setIsalarm(isalarm);
                        forecastCurrentOlt.setUpperValue(forecastValue);
                        forecastCurrentOlt.setLowerValue(forecastValue);
                        forecastCurrentOlt.setForecastValue(forecastValue);
                        forecastCurrentOltList.add(forecastCurrentOlt);
                    }
                }
            }
        }
        return forecastCurrentOltList;
    }

    @Override
    public void forecastOltOnlineAuto(String beginTime, String endTime, String oltip) {
        logger.info("LinebindMonitorImpl forecastOltOnlineAuto beginTime:" + beginTime + ";endTime:"
                + endTime + ";oltip:" + oltip);
        long beginTimeL = DateUtil.stringChangeTimeStamp(beginTime, DEFAULT_FORMAT);
        long endTimeL = DateUtil.stringChangeTimeStamp(endTime, DEFAULT_FORMAT);
        if (beginTimeL <= endTimeL) {
            try {
                long startTime = System.nanoTime();
                SimpleDateFormat sdfdd = new SimpleDateFormat(DEFAULT_FORMAT);
                Date datedd = sdfdd.parse(beginTime);
                Calendar calendardd = Calendar.getInstance();
                calendardd.setTime(datedd);
                calendardd.add(Calendar.DATE, -(AUTO_DATE));
                String beginTimeMonthArr = sdfdd.format(calendardd.getTime());
                ArrayList<String> monthArr = getMonthArr(beginTimeMonthArr, endTime);
                List<Olt> oltAllTimeOriginalList = new ArrayList<Olt>();
                for (int i = 0; i < monthArr.size(); i++) {
                    String yearmonth = monthArr.get(i);
                    String month = yearmonth.substring(5, 7);
                    List<Olt> oltCurrentOriginalList = new ArrayList<Olt>();
                    if (oltip == null || oltip.isEmpty() || "".equals(oltip)) {
                        oltCurrentOriginalList = oltMapper.getOltOnlineByStaDate(month, yearmonth,
                                beginTimeMonthArr, endTime);
                    } else {
                        oltCurrentOriginalList = oltMapper.getOltOnlineByOltIp(month, oltip,
                                yearmonth, beginTimeMonthArr, endTime);
                    }
                    if (oltCurrentOriginalList != null && !oltCurrentOriginalList.isEmpty()) {
                        oltAllTimeOriginalList.addAll(oltCurrentOriginalList);
                    }
                }
                // list线程安全初始化
                List<Olt> forecastCurrentAllOltList = Collections
                        .synchronizedList(new ArrayList<Olt>());
                // list线程安全初始化
                List<Olt> isCesOltCollectAllList = Collections
                        .synchronizedList(new ArrayList<Olt>());
                long startTimeThreadPool = System.nanoTime();
                logger.info(
                        "LinebindMonitorImpl forecastOltOnlineAuto getOltOriginalList time consuming : "
                                + (startTimeThreadPool - startTime) + "ns");
                ExecutorService fixedThreadPool = Executors.newFixedThreadPool(AUTO_POOL_SIZE);
                int thread_id = 1;
                // 自动获取时间段
                while (beginTimeL <= endTimeL) {
                    int threadIdThread = thread_id;
                    long beginTimeLThread = beginTimeL;
                    fixedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String time = DateUtil.timeStampChangeString(beginTimeLThread,
                                        DEFAULT_FORMAT);
                                SimpleDateFormat sdfddThread = new SimpleDateFormat(DEFAULT_FORMAT);
                                Date dateddThread = sdfddThread.parse(time);
                                Calendar calendarddThread = Calendar.getInstance();
                                calendarddThread.setTime(dateddThread);
                                String endTimeThread = time;
                                calendarddThread.add(Calendar.DATE, -(AUTO_DATE));
                                Date beginTimeThreadDate = calendarddThread.getTime();
                                String beginTimeThread = sdfddThread.format(beginTimeThreadDate);
                                List<Olt> oltAllCurrentOriginalList = new ArrayList<Olt>();
                                // 根据时间段获取olt原始数据
                                for (Olt oltAllTimeOriginal : oltAllTimeOriginalList) {
                                    Date nowTime = DateUtil.StrToDate(oltAllTimeOriginal.getName());
                                    boolean isEffectiveDate = DateUtil.isEffectiveDate(nowTime,
                                            beginTimeThreadDate, dateddThread);
                                    if (isEffectiveDate) {
                                        oltAllCurrentOriginalList.add(oltAllTimeOriginal);
                                    }
                                }
                                List<Olt> forecastOltList = forecastOltOnline("", time,
                                        beginTimeThread, endTimeThread, oltip,
                                        oltAllCurrentOriginalList, false, false);
                                forecastCurrentAllOltList.addAll(forecastOltList);
                                List<Olt> isCesOltCollectList = isCesOltCollect("", time,
                                        beginTimeThread, endTimeThread, oltip,
                                        oltAllCurrentOriginalList, false, false);
                                isCesOltCollectAllList.addAll(isCesOltCollectList);
                            } catch (Exception e) {
                                logger.error(
                                        "LinebindMonitorImpl forecastOltOnlineAuto threadIdThread is "
                                                + threadIdThread + ": " + e.getMessage());
                            }
                        }
                    });
                    thread_id++;
                    beginTimeL = beginTimeL + TIME_STAMP_DIFFERENCE;
                }
                fixedThreadPool.shutdown();
                fixedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
                long finishTimeThreadPool = System.nanoTime();
                logger.info("LinebindMonitorImpl forecastOltOnlineAuto time consuming : "
                        + (finishTimeThreadPool - startTimeThreadPool) + "ns");
                // 操作数据库
                if (oltip == null || oltip.isEmpty() || "".equals(oltip)) {
                    oltMapper.deleteCesAlarmInfoOltUserOnlineByTime(beginTime, endTime);
                } else {
                    oltMapper.deleteCesAlarmInfoOltUserOnlineByOltipByTime(beginTime, endTime,
                            oltip);
                }
                for (int i = 0; i < monthArr.size(); i++) {
                    String yearmonth = monthArr.get(i);
                    String month = yearmonth.substring(5, 7);
                    List<Olt> forecastMonthOltList = new ArrayList<Olt>();
                    for (Olt forecastCurrentAllOlt : forecastCurrentAllOltList) {
                        if (yearmonth.equals(forecastCurrentAllOlt.getName().substring(0, 7))) {
                            forecastMonthOltList.add(forecastCurrentAllOlt);
                        }
                    }
                    long startTimedeleteOltOnlineThreshold = System.nanoTime();
                    if (oltip == null || oltip.isEmpty() || "".equals(oltip)) {
                        oltMapper.deleteOltOnlineThresholdByTime(month, beginTime, endTime);
                    } else {
                        oltMapper.deleteOltOnlineThresholdByOltipByTime(month, beginTime, endTime,
                                oltip);
                    }
                    long finishTimedeleteOltOnlineThreshold = System.nanoTime();
                    logger.info("LinebindMonitorImpl forecastOltOnlineAuto parameteryearmonth is "
                            + yearmonth + " deleteOltOnlineThreshold time consuming : "
                            + (finishTimedeleteOltOnlineThreshold
                                    - startTimedeleteOltOnlineThreshold)
                            + "ns");
                    if (forecastMonthOltList != null && !forecastMonthOltList.isEmpty()) {
                        long startTimeinsertOltOnlineThreshold = System.nanoTime();
                        int insertCount = 1;
                        List<Olt> insertOltList = new ArrayList<Olt>();
                        for (Olt forecastCurrentAllOlt : forecastMonthOltList) {
                            insertOltList.add(forecastCurrentAllOlt);
                            // 200条数据的时候提交一次
                            if ((insertCount % 200) == 0) {
                                oltMapper.insertOltOnlineThreshold(month, insertOltList);
                                insertOltList.clear();
                            }
                            insertCount++;
                        }
                        if (insertOltList != null && !insertOltList.isEmpty()) {
                            oltMapper.insertOltOnlineThreshold(month, insertOltList);
                        }
                        long finishTimeinsertOltOnlineThreshold = System.nanoTime();
                        logger.info(
                                "LinebindMonitorImpl forecastOltOnlineAuto parameteryearmonth is "
                                        + yearmonth + " insertOltOnlineThreshold time consuming : "
                                        + (finishTimeinsertOltOnlineThreshold
                                                - startTimeinsertOltOnlineThreshold)
                                        + "ns");
                    }
                    List<Olt> oltOnlineThresholdList = new ArrayList<Olt>();
                    if (oltip == null || oltip.isEmpty() || "".equals(oltip)) {
                        oltOnlineThresholdList = oltMapper
                                .getOltOnlineThresholdAlarmByCalDateByTime(month, beginTime,
                                        endTime);
                    } else {
                        oltOnlineThresholdList = oltMapper
                                .getOltOnlineThresholdAlarmByCalDateByOltIpByTime(month, beginTime,
                                        endTime, oltip);
                    }
                    if (oltOnlineThresholdList != null && !oltOnlineThresholdList.isEmpty()) {
                        long startTimeinsertCesAlarmInfo = System.nanoTime();
                        int insertThresholdCount = 1;
                        List<Olt> insertOltThresholdList = new ArrayList<Olt>();
                        for (Olt oltOnlineThreshold : oltOnlineThresholdList) {
                            insertOltThresholdList.add(oltOnlineThreshold);
                            // 200条数据的时候提交一次
                            if ((insertThresholdCount % 200) == 0) {
                                oltMapper.insertCesAlarmInfoByOlt(insertOltThresholdList,
                                        "oltUserOnline", "");
                                insertOltThresholdList.clear();
                            }
                            insertThresholdCount++;
                        }
                        if (insertOltThresholdList != null && !insertOltThresholdList.isEmpty()) {
                            oltMapper.insertCesAlarmInfoByOlt(insertOltThresholdList,
                                    "oltUserOnline", "");
                        }
                        long finishTimeinsertCesAlarmInfo = System.nanoTime();
                        logger.info(
                                "LinebindMonitorImpl forecastOltOnlineAuto parameteryearmonth is "
                                        + yearmonth
                                        + " insertCesAlarmInfo oltUserOnline time consuming : "
                                        + (finishTimeinsertCesAlarmInfo
                                                - startTimeinsertCesAlarmInfo)
                                        + "ns");
                    }
                }
                // 插入未采集到值的告警数据
                for (Olt oltisCesOltCollect : isCesOltCollectAllList) {
                    List<Olt> oltOnlineThresholdList = new ArrayList<Olt>();
                    oltOnlineThresholdList.add(oltisCesOltCollect);
                    if (oltOnlineThresholdList != null && !oltOnlineThresholdList.isEmpty()) {
                        oltMapper.insertCesAlarmInfoByOlt(oltOnlineThresholdList, "oltUserOnline",
                                ISNOTCOLLECTION);
                    }
                }
                List<AlarmInfo> alarmInfoList = new ArrayList<AlarmInfo>();
                if (oltip == null || oltip.isEmpty() || "".equals(oltip)) {
                    alarmInfoList = oltMapper.getCesAlarmInfoOltMapByTime(beginTime, endTime);
                    oltMapper.deleteCesAlarmInfoOltMapByTime(beginTime, endTime);
                } else {
                    alarmInfoList = oltMapper.getCesAlarmInfoOltMapByOltIpByTime(beginTime, endTime,
                            oltip);
                    oltMapper.deleteCesAlarmInfoOltMapByOltipByTime(beginTime, endTime, oltip);
                }
                if (alarmInfoList != null && !alarmInfoList.isEmpty()) {
                    long startTimeinsertCesAlarmInfoOltMap = System.nanoTime();
                    int insertCountOltMap = 1;
                    List<AlarmInfo> insertOltList = new ArrayList<AlarmInfo>();
                    for (AlarmInfo oltAlarmInfo : alarmInfoList) {
                        insertOltList.add(oltAlarmInfo);
                        // 200条数据的时候提交一次
                        if ((insertCountOltMap % 200) == 0) {
                            oltMapper.insertCesAlarmInfo(insertOltList, "oltMap");
                            insertOltList.clear();
                        }
                        insertCountOltMap++;
                    }
                    if (insertOltList != null && !insertOltList.isEmpty()) {
                        oltMapper.insertCesAlarmInfo(insertOltList, "oltMap");
                    }
                    long finishTimeinsertCesAlarmInfoOltMap = System.nanoTime();
                    logger.info("LinebindMonitorImpl forecastOltOnlineAuto parameterbeginTime is "
                            + beginTime + "parameterendTime" + endTime
                            + " insertCesAlarmInfo oltMap time consuming : "
                            + (finishTimeinsertCesAlarmInfoOltMap
                                    - startTimeinsertCesAlarmInfoOltMap)
                            + "ns");
                }
                logger.info("LinebindMonitorImpl forecastOltOnlineAuto End");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error(
                        "LinebindMonitorImpl forecastOltOnlineAuto errorMessage:" + e.getMessage());
            } catch (ParseException e1) {
                logger.error("LinebindMonitorImpl forecastOltOnlineAuto errorMessage:"
                        + e1.getMessage());
            }
        }
    }

    @Override
    public List<Olt> forecastOltOnlineGUI(String format, String time, String beginTime,
            String endTime, String oltip, List<Olt> oltAllCurrentOriginalList, Boolean isUseDB) {
        logger.info("LinebindMonitorImpl forecastOltOnline parametertime is " + time + " Start");
        logger.info("LinebindMonitorImpl forecastOltOnline parametertime is " + time + " format:"
                + format + ";beginTime:" + beginTime + ";endTime:" + endTime + ";oltip:" + oltip);
        List<Olt> resultOltList = new ArrayList<Olt>();
        long startTime = System.nanoTime();
        try {
            if (format == null || format.isEmpty() || "".equals(format)) {
                format = FORMATTIME;
            }
            if (beginTime == null || beginTime.isEmpty() || "".equals(beginTime) || endTime == null
                    || endTime.isEmpty() || "".equals(endTime)) {
                switch (format) {
                case FORMATYEARMONTH:
                    SimpleDateFormat sdfmm = new SimpleDateFormat(FORMATYEARMONTH);
                    SimpleDateFormat sdfdmm = new SimpleDateFormat(DEFAULT_FORMAT);
                    Date datemm = sdfmm.parse(time);
                    Calendar calendarmm = Calendar.getInstance();
                    calendarmm.setTime(datemm);
                    beginTime = sdfdmm.format(calendarmm.getTime());
                    calendarmm.add(Calendar.MONTH, 1);
                    endTime = sdfdmm.format(calendarmm.getTime());
                    break;
                case FORMATTIME:
                    SimpleDateFormat sdfdd = new SimpleDateFormat(DEFAULT_FORMAT);
                    Date datedd = sdfdd.parse(time);
                    Calendar calendardd = Calendar.getInstance();
                    calendardd.setTime(datedd);
                    endTime = time;
                    calendardd.add(Calendar.DATE, -(AUTO_DATE));
                    beginTime = sdfdd.format(calendardd.getTime());
                    break;
                }
            }
            ArrayList<String> monthArr = getMonthArr(beginTime, endTime);
            List<Olt> oltOriginalList = new ArrayList<Olt>();
            // 是否需要数据库
            if (isUseDB) {
                for (int i = 0; i < monthArr.size(); i++) {
                    String yearmonth = monthArr.get(i);
                    String month = yearmonth.substring(5, 7);
                    List<Olt> oltCurrentOriginalList = oltMapper.getOltOnlineByOltIp(month, oltip,
                            yearmonth, beginTime, endTime);
                    oltOriginalList.addAll(oltCurrentOriginalList);
                }
            } else {
                oltOriginalList = oltAllCurrentOriginalList;
            }
            List<Olt> forecastCurrentOltList = egadsForecastOltGUI(oltOriginalList, format, time,
                    oltip);
            resultOltList = forecastCurrentOltList;
        } catch (ParseException e) {
            logger.error("LinebindMonitorImpl forecastOltOnline parametertime is " + time
                    + " errorMessage:" + e.getMessage());
        }
        long finishTime = System.nanoTime();
        logger.info("LinebindMonitorImpl forecastOltOnline parametertime is " + time
                + " time consuming : " + (finishTime - startTime) + "ns");
        logger.info("LinebindMonitorImpl forecastOltOnline parametertime is " + time + " End");
        return resultOltList;
    }

    public List<Olt> egadsForecastOltGUI(List<Olt> oltAllCurrentOriginalList, String format,
            String time, String oltIp) {
        String path = this.getClass().getResource("/").getPath() + "config/sample_config.ini";
        List<TimeSeriesData> timeSeriesDataOriginalList = new ArrayList<TimeSeriesData>();
        boolean isfirst = true;
        long beforeTimestamp = 0;
        TimeSeriesData beforeTimeSeriesData = new TimeSeriesData();
        List<Object> completionTimeSeriesList = new ArrayList<Object>();
        // 根据IP筛选OLT并且补全数据并转换实体类
        for (Olt oltAllCurrentOriginal : oltAllCurrentOriginalList) {
            if (oltIp.equals(oltAllCurrentOriginal.getOltip())) {
                TimeSeriesData timeSeriesDataOriginal = new TimeSeriesData();
                long timestamp = DateUtil.stringChangeTimeStamp(oltAllCurrentOriginal.getName(),
                        DEFAULT_FORMAT);
                float value = Float.parseFloat(oltAllCurrentOriginal.getValue());
                timeSeriesDataOriginal.setTimestamp(timestamp);
                timeSeriesDataOriginal.setValue(value);
                if (isfirst) {
                    isfirst = false;
                } else {
                    // 根据时间戳补全数据
                    while ((beforeTimestamp + TIME_STAMP_DIFFERENCE) < timestamp) {
                        beforeTimestamp = beforeTimestamp + TIME_STAMP_DIFFERENCE;
                        TimeSeriesData completionTimeSeriesData = new TimeSeriesData();
                        completionTimeSeriesData.setTimestamp(beforeTimestamp);
                        completionTimeSeriesData.setValue(beforeTimeSeriesData.getValue());
                        completionTimeSeriesList.add(beforeTimestamp);
                        timeSeriesDataOriginalList.add(completionTimeSeriesData);
                    }
                }
                beforeTimestamp = timestamp;
                beforeTimeSeriesData = timeSeriesDataOriginal;
                timeSeriesDataOriginalList.add(timeSeriesDataOriginal);
            }
        }
        TimeSeriesResult timeSeriesResult = new TimeSeriesResult();
        try {
            timeSeriesResult = Asiainfo.egadsTimeSeriesGUI(timeSeriesDataOriginalList, path);
        } catch (Exception e) {
            logger.info(
                    "LinebindMonitorImpl forecastOltOnline egadsForecastOlt kdzlfx.forecastOlt parametertime is "
                            + time + " OltIp : " + oltIp);
            logger.error("LinebindMonitorImpl forecastOltOnline egadsForecastOlt parametertime is "
                    + time + " errorMessage:" + e.getMessage());
        }
        List<Olt> forecastCurrentOltList = new ArrayList<Olt>();
        if (timeSeriesResult.getResultDataList() != null
                && !timeSeriesResult.getResultDataList().isEmpty()) {
            for (TimeSeriesData timeSeriesDataforecast : timeSeriesResult.getResultDataList()) {
                // 忽略补全的数据
                if (!completionTimeSeriesList.contains(timeSeriesDataforecast.getTimestamp())) {
                    String oltName = DateUtil.timeStampChangeString(
                            timeSeriesDataforecast.getTimestamp(), DEFAULT_FORMAT);
                    String oltNameByFormat = oltName;
                    switch (format) {
                    case FORMATYEARMONTH:
                        oltNameByFormat = oltNameByFormat.substring(0, 7);
                        break;
                    case FORMATTIME:
                        break;
                    }
                    // 筛选出需要预测时间点的OLT
                    if (time.equals(oltNameByFormat)) {
                        // 预测的数据四舍五入取整
                        DecimalFormat df = new DecimalFormat("##");
                        String value = df.format(timeSeriesDataforecast.getValue());
                        String forecastValue = df.format(timeSeriesDataforecast.getForecastValue());
                        String isalarm = "0";
                        if (timeSeriesDataforecast.getIsalarm()) {
                            isalarm = "1";
                        }
                        Olt forecastCurrentOlt = new Olt();
                        forecastCurrentOlt.setOltip(oltIp);
                        forecastCurrentOlt.setName(oltName);
                        forecastCurrentOlt.setValue(value);
                        forecastCurrentOlt.setIsalarm(isalarm);
                        forecastCurrentOlt.setUpperValue(forecastValue);
                        forecastCurrentOlt.setLowerValue(forecastValue);
                        forecastCurrentOlt.setForecastValue(forecastValue);
                        forecastCurrentOltList.add(forecastCurrentOlt);
                    }
                }
            }
        }
        return forecastCurrentOltList;
    }

    /**
     * olt设备最近20条告警
     * 
     * @return
     */
    @Override
    public List<Olt> oltList() {
        return oltMapper.oltList();
    }

    /**
     * 查询OLT设备历史告警
     * 
     * @param oltHistory
     * @param pageNumber
     * @return
     */
    public Page getOltHistoryList(OltHistory oltHistory, int pageNumber) {
        String month = DateUtil.getMinusMonths(FORMATMONTH, 0);

        Page page = new Page(0);
        page.setPageNumber(pageNumber);
        page.setPageSize(20);
        int totalCount = oltMapper.getOltHistoryCount(oltHistory, month); // 查询全部数据
        page.setTotalCount(totalCount);

        if (totalCount > 0) {
            List<Olt> list = oltMapper.getOltHistoryList(oltHistory, page, month); // 查询分页数据
            page.setPageList(list);
        }

        return page;
    }

    @Override
    public void refreshCesOltHistory(String type, String beginTime, String endTime) {
        refreshOltMap();
        // 刷新OLT设备历史告警数据
        switch (type) {
        case EnumUtil.REFRESH_CES_OLT_HISTORY_ALL:
            Olt olt = oltMapper.getCesOltHistoryNewAlarmTime();
            if (olt != null) {
                oltMapper.insertOltHistoryByBeginTime(olt.getAlarm_time());
            } else {
                oltMapper.insertAllOltHistory();
            }
            break;
        case EnumUtil.REFRESH_CES_OLT_HISTORY_TIME:
            oltMapper.deleteCesOltHistoryByTime(beginTime);
            oltMapper.insertOltHistoryByTime(beginTime);
            break;
        case EnumUtil.REFRESH_CES_OLT_HISTORY_PART:
            oltMapper.deleteCesOltHistoryByPart(beginTime, endTime);
            oltMapper.insertOltHistoryByPart(beginTime, endTime);
            break;
        }
    }

    public void refreshOltMap() {
        String month = DateUtil.getMinusMonths(FORMATMONTH, 0);
        String path = this.getClass().getResource("/").getPath().replaceAll("WEB-INF/classes/", "")
                + "static/js/map/oltmapinfo";
        // oltmap信息
        Map<String, Integer> retMap = new HashMap<String, Integer>();
        retMap.put("totalNum", oltMapper.getOltMapTotalNum());
        retMap.put("alarmNumTFH", oltMapper.getOltMapAlarmNumTFH());
        // 当前周期是近一周
        retMap.put("curAlarmNum", oltMapper.getOltMapCurAlarmNum());
        String jsonString = JSON.toJSONString(retMap);
        FileUtil.writeFile(path + "/oltmapinfo.json", jsonString, "UTF-8");
        // map地图数据
        List<Olt> oltList = oltMapper.getOltMap(month);
        JSONArray array = JSONArray.parseArray(JSON.toJSONString(oltList));
        FileUtil.writeFile(path + "/oltmap.json", array.toString(), "UTF-8");
    }

    // 导出EXCEL报表
    @Override
    public HSSFWorkbook exportReport(OltHistory oltHistory) {
        List<ReportFieldInfo> fieldInfoList = new ArrayList<ReportFieldInfo>();
        List<String> fieldNameList = new ArrayList<>(
                Arrays.asList("告警时间", "OLT设备IP", "在线用户中值", "当前用户数", "陡降用户数", "陡降百分比"));
        for (String fieldName : fieldNameList) {
            ReportFieldInfo fieldInfo = new ReportFieldInfo();
            fieldInfo.setShowName(fieldName);
            fieldInfoList.add(fieldInfo);
        }
        List<Olt> oltList = oltMapper.getOltHistoryListNotPage(oltHistory);
        String[][] data = new String[oltList.size()][fieldNameList.size()];
        int i = 0;
        for (Olt olt : oltList) {
            data[i][0] = olt.getAlarm_time();
            data[i][1] = olt.getOltip();
            data[i][2] = olt.getMid_value();
            data[i][3] = olt.getNow_value();
            data[i][4] = olt.getDown_value();
            data[i][5] = olt.getDown_per();
            i++;
        }
        Map<String, Object> exportMap = new HashMap<String, Object>();
        exportMap.put("data", data);
        exportMap.put("tableName", "OLT设备历史告警");
        exportMap.put("fieldInfoList", fieldInfoList);
        HSSFWorkbook wb = ExcelDocument.mkExcel(exportMap);
        return wb;
    }

    @Override
    public void egadsForecastOltRefreshOltList(String time, String beginTime, String endTime) {
        logger.info("LinebindMonitorImpl egadsForecastOltRefreshOltList start");
        long startTimeProgram = System.nanoTime();
        List<String> curTimeStampList = new ArrayList<String>();
        if (time == null || time.isEmpty() || "".equals(time)) {
            time = DateUtil.getDate();
        }
        if (beginTime == null || beginTime.isEmpty() || "".equals(beginTime) || endTime == null
                || endTime.isEmpty() || "".equals(endTime)) {
            curTimeStampList = DateUtil.getCutTimeStamp(DateUtil.StrToDate(time), AUTO_DATE,
                    TIME_STAMP_DIFFERENCE);
        } else {
            curTimeStampList = DateUtil.getCutTimeStamp(beginTime, endTime, TIME_STAMP_DIFFERENCE);
        }
        List<Olt> curOltList = new ArrayList<Olt>();
        List<Map<String, List<Olt>>> curOltMapList = new ArrayList<Map<String, List<Olt>>>();
        List<Map<String, Object>> needUseDBList = new ArrayList<Map<String, Object>>();
        List<Map<String, List<Olt>>> oltMapList = OLT_MEMORY.getOltMapList();
        List<Olt> dbOltList = new ArrayList<Olt>();
        for (String curTimeStamp : curTimeStampList) {
            Boolean isUseDB = true;
            if (oltMapList != null && !oltMapList.isEmpty()) {
                for (Map<String, List<Olt>> oltMap : oltMapList) {
                    if (oltMap.containsKey(curTimeStamp)) {
                        isUseDB = false;
                        break;
                    }
                }
            }
            if (isUseDB) {
                Boolean notExist = true;
                for (Map<String, Object> needUseDBListMap : needUseDBList) {
                    if (needUseDBListMap.containsValue(curTimeStamp.substring(5, 7))) {
                        @SuppressWarnings("unchecked")
                        List<String> oltTimeList = (List<String>) needUseDBListMap
                                .get("oltTimeList");
                        if (!oltTimeList.contains(curTimeStamp)) {
                            oltTimeList.add(curTimeStamp);
                        }
                        notExist = false;
                        break;
                    }
                }
                if (notExist) {
                    Map<String, Object> needUseDBMap = new HashMap<String, Object>();
                    needUseDBMap.put("month", curTimeStamp.substring(5, 7));
                    List<String> oltTimeList = new ArrayList<String>();
                    oltTimeList.add(curTimeStamp);
                    needUseDBMap.put("oltTimeList", oltTimeList);
                    needUseDBList.add(needUseDBMap);
                }
            }
        }
        for (Map<String, Object> needUseDBListMap : needUseDBList) {
            String month = (String) needUseDBListMap.get("month");
            @SuppressWarnings("unchecked")
            List<String> oltTimeList = (List<String>) needUseDBListMap.get("oltTimeList");
            if (oltTimeList != null && !oltTimeList.isEmpty()) {
                logger.info(
                        "LinebindMonitorImpl egadsForecastOltRefreshOltList start getOltOnlineByStaDateTimes month : "
                                + month);
                long startTimegetOltOnlineByStaDateTimes = System.nanoTime();
                int getOltOnlineByStaDateTimesCount = 1;
                List<String> getOltOnlineByStaDateTimesList = new ArrayList<String>();
                for (String s : oltTimeList) {
                    getOltOnlineByStaDateTimesList.add(s);
                    // 80条数据的时候提交一次
                    if ((getOltOnlineByStaDateTimesCount % 80) == 0) {
                        dbOltList.addAll(oltMapper.getOltOnlineByStaDateTimes(month,
                                getOltOnlineByStaDateTimesList));
                        getOltOnlineByStaDateTimesList.clear();
                    }
                    getOltOnlineByStaDateTimesCount++;
                }
                if (getOltOnlineByStaDateTimesList != null
                        && !getOltOnlineByStaDateTimesList.isEmpty()) {
                    dbOltList.addAll(oltMapper.getOltOnlineByStaDateTimes(month,
                            getOltOnlineByStaDateTimesList));
                }
                long endTimegetOltOnlineByStaDateTimes = System.nanoTime();
                logger.info(
                        "LinebindMonitorImpl egadsForecastOltRefreshOltList getOltOnlineByStaDateTimes month : "
                                + month + " time consuming : " + (endTimegetOltOnlineByStaDateTimes
                                        - startTimegetOltOnlineByStaDateTimes)
                                + "ns");
            }
        }
        List<String> logNoDBCurOltMapList = new ArrayList<String>();
        List<String> logUseDBCurOltMapList = new ArrayList<String>();
        for (String curTimeStamp : curTimeStampList) {
            Boolean isUseDB = true;
            if (oltMapList != null && !oltMapList.isEmpty()) {
                for (Map<String, List<Olt>> oltMap : oltMapList) {
                    if (oltMap.containsKey(curTimeStamp)) {
                        curOltList.addAll(oltMap.get(curTimeStamp));
                        curOltMapList.add(oltMap);
                        logNoDBCurOltMapList.add(curTimeStamp);
                        isUseDB = false;
                        break;
                    }
                }
            }
            if (isUseDB) {
                List<Olt> oltList = new ArrayList<Olt>();
                for (Olt olt : dbOltList) {
                    if (olt.getName().equals(curTimeStamp)) {
                        oltList.add(olt);
                    }
                }
                curOltList.addAll(oltList);
                Map<String, List<Olt>> oltMap = new HashMap<String, List<Olt>>();
                oltMap.put(curTimeStamp, oltList);
                curOltMapList.add(oltMap);
                logUseDBCurOltMapList.add(curTimeStamp);
            }
        }
        logger.info("LinebindMonitorImpl egadsForecastOltRefreshOltList logNoDBCurOltMapList : "
                + logNoDBCurOltMapList.toString());
        logger.info("LinebindMonitorImpl egadsForecastOltRefreshOltList logUseDBCurOltMapList : "
                + logUseDBCurOltMapList.toString());
        if (oltMapList != null && !oltMapList.isEmpty()) {
            OLT_MEMORY.getOltMapList().clear();
        }
        if (OLT_MEMORY.getOltList() != null && !OLT_MEMORY.getOltList().isEmpty()) {
            OLT_MEMORY.getOltList().clear();
        }
        OLT_MEMORY.setOltList(curOltList);
        OLT_MEMORY.setOltMapList(curOltMapList);
        long endTimeProgram = System.nanoTime();
        logger.info("LinebindMonitorImpl egadsForecastOltRefreshOltList time consuming : "
                + (endTimeProgram - startTimeProgram) + "ns");
        logger.info("LinebindMonitorImpl egadsForecastOltRefreshOltList end");
    }

    @Override
    public List<Olt> isCesOltCollect(String format, String time, String beginTime, String endTime,
            String oltip, List<Olt> oltAllCurrentOriginalList, Boolean isGetUseDB,
            Boolean isSetUseDB) {
        logger.info("LinebindMonitorImpl isCesOltCollect parametertime is " + time + " Start");
        logger.info("LinebindMonitorImpl isCesOltCollect parametertime is " + time + " format:"
                + format + ";beginTime:" + beginTime + ";endTime:" + endTime + ";oltip:" + oltip
                + ";isGetUseDB:" + isGetUseDB + ";isSetUseDB:" + isSetUseDB);
        List<Olt> resultOltList = new ArrayList<Olt>();
        List<String> logResultOltIpList = new ArrayList<String>();
        if (oltAllCurrentOriginalList == null) {
            oltAllCurrentOriginalList = new ArrayList<Olt>();
        }
        if (!isGetUseDB
                && (oltAllCurrentOriginalList == null || oltAllCurrentOriginalList.isEmpty())) {
            isGetUseDB = true;
            logger.info(
                    "LinebindMonitorImpl isCesOltCollect oltAllCurrentOriginalList isEmpty isUseDB should be true");
        }
        long startTime = System.nanoTime();
        try {
            if (format == null || format.isEmpty() || "".equals(format)) {
                format = FORMATTIME;
            }
            endTime = time;
            // 用endTime获取需要检测的未采集周期
            List<String> curTimeStampList = DateUtil.getCutTimeStampByFrequency(
                    DateUtil.StrToDate(time), FREQUENCY, TIME_STAMP_DIFFERENCE);
            beginTime = DateUtil.getCutTimeStampByFrequency(DateUtil.StrToDate(time), FREQUENCY + 1,
                    TIME_STAMP_DIFFERENCE).get(0);
            ArrayList<String> monthArr = getMonthArr(beginTime, endTime);
            if (oltip == null || oltip.isEmpty() || "".equals(oltip)) {
                List<Olt> oltTimeCurrentOriginalList = new ArrayList<Olt>();
                // 是否需要查询数据库
                if (isGetUseDB) {
                    for (int i = 0; i < monthArr.size(); i++) {
                        String yearmonth = monthArr.get(i);
                        String month = yearmonth.substring(5, 7);
                        List<Olt> oltCurrentOriginalList = new ArrayList<Olt>();
                        oltCurrentOriginalList = oltMapper.getOltOnlineByStaDate(month, yearmonth,
                                beginTime, endTime);
                        if (month.equals(time.substring(5, 7))) {
                            oltTimeCurrentOriginalList = oltCurrentOriginalList;
                        }
                        if (oltCurrentOriginalList != null && !oltCurrentOriginalList.isEmpty()) {
                            oltAllCurrentOriginalList.addAll(oltCurrentOriginalList);
                        }
                    }
                    if (oltTimeCurrentOriginalList == null
                            || oltTimeCurrentOriginalList.isEmpty()) {
                        oltTimeCurrentOriginalList = oltMapper.getOltOnlineByStaDate(
                                time.substring(5, 7), time.substring(0, 7), beginTime, endTime);
                    }
                } else {
                    for (Olt oltAllCurrentOriginal : oltAllCurrentOriginalList) {
                        String curTime = oltAllCurrentOriginal.getName();
                        for (String curTimeStamp : curTimeStampList) {
                            if (curTime.equals(curTimeStamp)) {
                                oltTimeCurrentOriginalList.add(oltAllCurrentOriginal);
                            }
                        }
                    }
                }
                List<String> oltIpList = new ArrayList<String>();
                for (Olt oltTimeCurrentOriginal : oltTimeCurrentOriginalList) {
                    String oltIpTimeCurrent = oltTimeCurrentOriginal.getOltip();
                    if (!oltIpList.contains(oltIpTimeCurrent)) {
                        oltIpList.add(oltIpTimeCurrent);
                    }
                }
                List<Olt> oltisCesOltCollectList = new ArrayList<Olt>();
                for (String oip : oltIpList) {
                    Boolean isCesOltCollect = false;
                    Boolean isAllCesOltCollect = false;
                    for (Olt oltOriginal : oltTimeCurrentOriginalList) {
                        if (oltOriginal.getOltip().equals(oip)) {
                            isAllCesOltCollect = true;
                            if (oltOriginal.getName().equals(time)) {
                                isCesOltCollect = true;
                            }
                        }
                    }
                    if (!isCesOltCollect && isAllCesOltCollect) {
                        Olt oltCollect = new Olt();
                        oltCollect.setName(time);
                        oltCollect.setOltip(oip);
                        oltisCesOltCollectList.add(oltCollect);
                        logResultOltIpList.add(oip);
                    }
                }
                resultOltList = oltisCesOltCollectList;
                // 是否需要提交数据库
                if (isSetUseDB) {
                    for (Olt oltisCesOltCollect : oltisCesOltCollectList) {
                        List<Olt> oltOnlineThresholdList = new ArrayList<Olt>();
                        oltOnlineThresholdList.add(oltisCesOltCollect);
                        oltMapper.deleteCesAlarmInfoOltUserOnlineByOltip(format, time,
                                oltisCesOltCollect.getOltip());
                        if (oltOnlineThresholdList != null && !oltOnlineThresholdList.isEmpty()) {
                            oltMapper.insertCesAlarmInfoByOlt(oltOnlineThresholdList,
                                    "oltUserOnline", ISNOTCOLLECTION);
                        }
                        List<AlarmInfo> alarmInfoList = oltMapper.getCesAlarmInfoOltMapByOltIp(
                                format, time, oltisCesOltCollect.getOltip());
                        oltMapper.deleteCesAlarmInfoOltMapByOltip(format, time,
                                oltisCesOltCollect.getOltip());
                        if (alarmInfoList != null && !alarmInfoList.isEmpty()) {
                            oltMapper.insertCesAlarmInfo(alarmInfoList, "oltMap");
                        }
                    }
                }
            } else {
                List<Olt> oltOriginalList = new ArrayList<Olt>();
                // 是否需要查询数据库
                if (isGetUseDB) {
                    for (int i = 0; i < monthArr.size(); i++) {
                        String yearmonth = monthArr.get(i);
                        String month = yearmonth.substring(5, 7);
                        List<Olt> oltCurrentOriginalList = oltMapper.getOltOnlineByOltIp(month,
                                oltip, yearmonth, beginTime, endTime);
                        oltOriginalList.addAll(oltCurrentOriginalList);
                    }
                } else {
                    for (Olt oltAllCurrentOriginal : oltAllCurrentOriginalList) {
                        String curTime = oltAllCurrentOriginal.getName();
                        for (String curTimeStamp : curTimeStampList) {
                            if (curTime.equals(curTimeStamp)) {
                                oltOriginalList.add(oltAllCurrentOriginal);
                            }
                        }
                    }
                }
                Boolean isCesOltCollect = false;
                Boolean isAllCesOltCollect = false;
                List<Olt> oltOnlineThresholdList = new ArrayList<Olt>();
                for (Olt oltOriginal : oltOriginalList) {
                    if (oltOriginal.getOltip().equals(oltip)) {
                        isAllCesOltCollect = true;
                        if (oltOriginal.getName().equals(time)) {
                            isCesOltCollect = true;
                        }
                    }
                }
                if (!isCesOltCollect && isAllCesOltCollect) {
                    Olt oltCollect = new Olt();
                    oltCollect.setName(time);
                    oltCollect.setOltip(oltip);
                    oltOnlineThresholdList.add(oltCollect);
                    logResultOltIpList.add(oltip);
                }
                resultOltList = oltOnlineThresholdList;
                // 是否需要提交数据库
                if (isSetUseDB) {
                    if (oltOnlineThresholdList != null && !oltOnlineThresholdList.isEmpty()) {
                        oltMapper.deleteCesAlarmInfoOltUserOnlineByOltip(format, time, oltip);
                        oltMapper.insertCesAlarmInfoByOlt(oltOnlineThresholdList, "oltUserOnline",
                                ISNOTCOLLECTION);
                    }
                    List<AlarmInfo> alarmInfoList = oltMapper.getCesAlarmInfoOltMapByOltIp(format,
                            time, oltip);
                    oltMapper.deleteCesAlarmInfoOltMapByOltip(format, time, oltip);
                    if (alarmInfoList != null && !alarmInfoList.isEmpty()) {
                        oltMapper.insertCesAlarmInfo(alarmInfoList, "oltMap");
                    }
                }
            }
            logger.info("LinebindMonitorImpl isCesOltCollect parametertime is " + time
                    + " logResultOltIpList : " + logResultOltIpList.toString());
        } catch (Exception e) {
            logger.error("LinebindMonitorImpl isCesOltCollect parametertime is " + time
                    + " errorMessage:" + e.getMessage());
        }
        long finishTime = System.nanoTime();
        logger.info("LinebindMonitorImpl isCesOltCollect parametertime is " + time
                + " time consuming : " + (finishTime - startTime) + "ns");
        logger.info("LinebindMonitorImpl isCesOltCollect parametertime is " + time + " End");
        return resultOltList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> getUserOnlineMapInfo(String oltip, String time) {
        Map<String, Object> map = new HashMap<String, Object>();
        String alarmState = "数据正常未告警";
        String month = DateUtil.getMinusMonthsTime(FORMATMONTH, 0, time);
        String preMonth = DateUtil.getMinusMonthsTime(FORMATMONTH, 1, time);
        List<String> curTimeStampList = DateUtil.getCutTimeStamp(DateUtil.StrToDate(time),
                SHOW_DATE, TIME_STAMP_DIFFERENCE);
        List<Olt> oltList = oltMapper.getUserOnline(month, preMonth, oltip, curTimeStampList.get(0),
                time);
        List<Olt> oltListResult = new ArrayList<Olt>();
        List<Olt> forecastDataListResult = new ArrayList<Olt>();
        List<Olt> oltAlarmList = oltMapper.getUserOnlineAlarm(curTimeStampList.get(0),
                curTimeStampList.get(curTimeStampList.size() - 1), oltip);
        // 获取预测在线用户数数据和函数数据
        getEgadsForecastOltOnlineMapInfo(oltList, oltip, time, map);
        List<Olt> forecastDataList = (List<Olt>) map.get("forecastDataList");
        // 检测的函数结果
        String mapee = "0";
        String mae = "0";
        String smape = "0";
        String mape = "0";
        String mase = "0";
        Float mapeeFinity = null;
        Float maeFinity = null;
        Float smapeFinity = null;
        Float mapeFinity = null;
        Float maseFinity = null;
        Map<String, Object> threshold = (Map<String, Object>) map.get("threshold");
        Boolean isexistthreshold = false;
        Boolean isexistmapeeFinity = false;
        Boolean isexistmaeFinity = false;
        Boolean isexistsmapeFinity = false;
        Boolean isexistmapeFinity = false;
        Boolean isexistmaseFinity = false;
        if (null != threshold) {
            isexistthreshold = true;
            Map<String, Object> finity = (Map<String, Object>) threshold.get("finity");
            mapeeFinity = (Float) finity.get("mapee");
            if (null != mapeeFinity) {
                isexistmapeeFinity = true;
            }
            maeFinity = (Float) finity.get("mae");
            if (null != maeFinity) {
                isexistmaeFinity = true;
            }
            smapeFinity = (Float) finity.get("smape");
            if (null != smapeFinity) {
                isexistsmapeFinity = true;
            }
            mapeFinity = (Float) finity.get("mape");
            if (null != mapeFinity) {
                isexistmapeFinity = true;
            }
            maseFinity = (Float) finity.get("mase");
            if (null != maseFinity) {
                isexistmaseFinity = true;
            }
        }
        for (String curTimeStamp : curTimeStampList) {
            // 补全原始数据的空值
            Boolean isnotexist = true;
            for (Olt olt : oltList) {
                if (olt.getName().equals(curTimeStamp)) {
                    isnotexist = false;
                    olt.setIscollection("1");
                    olt.setName(curTimeStamp.substring(5, 16));
                    oltListResult.add(olt);
                    if (time.equals(curTimeStamp) && "1".equals(olt.getIsalarm())) {
                        alarmState = "数据异常告警";
                    }
                }
            }
            if (isnotexist) {
                for (Olt oltAlarm : oltAlarmList) {
                    if (oltAlarm.getOltip().equals(oltip)
                            && oltAlarm.getName().equals(curTimeStamp)) {
                        Olt o = new Olt();
                        o.setName(curTimeStamp.substring(5, 16));
                        o.setValue("0");
                        o.setUpperValue("0");
                        o.setLowerValue("0");
                        o.setIsalarm("1");
                        o.setIscollection("0");
                        oltListResult.add(o);
                        if (time.equals(curTimeStamp)) {
                            alarmState = "未采集到数据告警";
                        }
                    }
                }
            }

            // 补全预测数据的空值
            Boolean isnotexistforecast = true;
            for (Olt olt : forecastDataList) {
                if (olt.getName().equals(curTimeStamp)) {
                    isnotexistforecast = false;
                    olt.setIscollection("1");
                    olt.setName(curTimeStamp.substring(5, 16));
                    forecastDataListResult.add(olt);
                    if (time.equals(curTimeStamp)) {
                        mapee = olt.getMapee();
                        mae = olt.getMae();
                        smape = olt.getSmape();
                        mape = olt.getMape();
                        mase = olt.getMase();
                    }
                }
            }
            if (isnotexistforecast) {
                for (Olt oltAlarm : oltAlarmList) {
                    if (oltAlarm.getOltip().equals(oltip)
                            && oltAlarm.getName().equals(curTimeStamp)) {
                        Olt o = new Olt();
                        o.setName(curTimeStamp.substring(5, 16));
                        o.setValue("0");
                        o.setMapee("0");
                        o.setMae("0");
                        o.setSmape("0");
                        o.setMape("0");
                        o.setMase("0");
                        o.setUpperValue("0");
                        o.setLowerValue("0");
                        o.setIsalarm("1");
                        o.setIscollection("0");
                        forecastDataListResult.add(o);
                        if (time.equals(curTimeStamp)) {
                            mapee = o.getMapee();
                            mae = o.getMae();
                            smape = o.getSmape();
                            mape = o.getMape();
                            mase = o.getMase();
                        }
                    }
                }
            }
        }
        map.put("forecastDataList", forecastDataListResult);
        map.put("originalDataList", oltListResult);
        Map<String, Object> propertiesThresholdMap = (Map<String, Object>) map
                .get("propertiesThresholdMap");
        Map<String, String> functionShownameMap = new HashMap<String, String>();
        functionShownameMap.put("mapee", MAPEE_FUNCTION);
        functionShownameMap.put("mae", MAE_FUNCTION);
        functionShownameMap.put("smape", SMAPE_FUNCTION);
        functionShownameMap.put("mape", MAPE_FUNCTION);
        functionShownameMap.put("mase", MASE_FUNCTION);
        map.put("functionShownameMap", functionShownameMap);
        List<ChartDatas> ChartDatasList = new ArrayList<ChartDatas>();
        ChartDatas datas = new ChartDatas();
        datas.setTitle("OLT在线用户数信息分析");
        List<ChartPoint> data = new ArrayList<>();
        data.add(new ChartPoint("OLTIP", oltip == null ? "--" : oltip));
        data.add(new ChartPoint("分析时间点", time == null ? "--" : time));
        data.add(new ChartPoint("告警状态", alarmState == null ? "--" : alarmState));
        if (isexistthreshold) {
            DecimalFormat df = new DecimalFormat("##.##");
            Float infinity = 1.0f / 0.0f;
            if (isexistmapeeFinity) {
                String mapeeFinityString = df.format(mapeeFinity);
                if (mapeeFinity.equals(infinity)) {
                    mapeeFinityString = "无穷大";
                }
                data.add(new ChartPoint(functionShownameMap.get("mapee"), mapee == null ? "--"
                        : "函数值：" + mapee + " / " + "告警值：" + mapeeFinityString));
            } else {
                data.add(new ChartPoint(functionShownameMap.get("mapee"), mapee == null ? "--"
                        : "函数值：" + mapee + " / " + "告警值：" + propertiesThresholdMap.get("mapee")));
            }
            if (isexistmaeFinity) {
                String maeFinityString = df.format(maeFinity);
                if (maeFinity.equals(infinity)) {
                    maeFinityString = "无穷大";
                }
                data.add(new ChartPoint(functionShownameMap.get("mae"),
                        mae == null ? "--" : "函数值：" + mae + " / " + "告警值：" + maeFinityString));
            } else {
                data.add(new ChartPoint(functionShownameMap.get("mae"), mae == null ? "--"
                        : "函数值：" + mae + " / " + "告警值：" + propertiesThresholdMap.get("mae")));
            }
            if (isexistsmapeFinity) {
                String smapeFinityString = df.format(smapeFinity);
                if (smapeFinity.equals(infinity)) {
                    smapeFinityString = "无穷大";
                }
                data.add(new ChartPoint(functionShownameMap.get("smape"), smape == null ? "--"
                        : "函数值：" + smape + " / " + "告警值：" + smapeFinityString));
            } else {
                data.add(new ChartPoint(functionShownameMap.get("smape"), smape == null ? "--"
                        : "函数值：" + smape + " / " + "告警值：" + propertiesThresholdMap.get("smape")));
            }
            if (isexistmapeFinity) {
                String mapeFinityString = df.format(mapeFinity);
                if (mapeFinity.equals(infinity)) {
                    mapeFinityString = "无穷大";
                }
                data.add(new ChartPoint(functionShownameMap.get("mape"),
                        mape == null ? "--" : "函数值：" + mape + " / " + "告警值：" + mapeFinityString));
            } else {
                data.add(new ChartPoint(functionShownameMap.get("mape"), mape == null ? "--"
                        : "函数值：" + mape + " / " + "告警值：" + propertiesThresholdMap.get("mape")));
            }
            if (isexistmaseFinity) {
                String maseFinityString = df.format(maseFinity);
                if (maseFinity.equals(infinity)) {
                    maseFinityString = "无穷大";
                }
                data.add(new ChartPoint(functionShownameMap.get("mase"),
                        mase == null ? "--" : "函数值：" + mase + " / " + "告警值：" + maseFinityString));
            } else {
                data.add(new ChartPoint(functionShownameMap.get("mase"), mase == null ? "--"
                        : "函数值：" + mase + " / " + "告警值：" + propertiesThresholdMap.get("mase")));
            }
        }
        datas.setData(data);
        ChartDatasList.add(datas);
        map.put("chartDatasList", ChartDatasList);
        return map;
    }

    public void getEgadsForecastOltOnlineMapInfo(List<Olt> oltAllCurrentOriginalList, String oltIp,
            String time, Map<String, Object> map) {
        String path = this.getClass().getResource("/").getPath() + "config/sample_config.ini";
        Properties p = new Properties();
        try {
            File f = new File(path);
            boolean isRegularFile = f.exists();
            if (isRegularFile) {
                InputStream is = new FileInputStream(path);
                p.load(is);
            } else {
                FileUtils.initProperties(path, p);
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String propertiesThreshold = (String) p.get("THRESHOLD");
        Map<String, Object> propertiesThresholdMap = new HashMap<String, Object>();
        if (propertiesThreshold != null) {
            List<String> result = Arrays.asList(propertiesThreshold.split(","));
            for (String s : result) {
                List<String> r = Arrays.asList(s.split("#"));
                propertiesThresholdMap.put(r.get(0), r.get(1));
            }
        }
        List<TimeSeriesData> timeSeriesDataOriginalList = new ArrayList<TimeSeriesData>();
        boolean isfirst = true;
        long beforeTimestamp = 0;
        TimeSeriesData beforeTimeSeriesData = new TimeSeriesData();
        List<Object> completionTimeSeriesList = new ArrayList<Object>();
        // 根据IP筛选OLT并且补全数据并转换实体类
        for (Olt oltAllCurrentOriginal : oltAllCurrentOriginalList) {
            TimeSeriesData timeSeriesDataOriginal = new TimeSeriesData();
            long timestamp = DateUtil.stringChangeTimeStamp(oltAllCurrentOriginal.getName(),
                    DEFAULT_FORMAT);
            float value = Float.parseFloat(oltAllCurrentOriginal.getValue());
            timeSeriesDataOriginal.setTimestamp(timestamp);
            timeSeriesDataOriginal.setValue(value);
            if (isfirst) {
                isfirst = false;
            } else {
                // 根据时间戳补全数据
                while ((beforeTimestamp + TIME_STAMP_DIFFERENCE) < timestamp) {
                    beforeTimestamp = beforeTimestamp + TIME_STAMP_DIFFERENCE;
                    TimeSeriesData completionTimeSeriesData = new TimeSeriesData();
                    completionTimeSeriesData.setTimestamp(beforeTimestamp);
                    completionTimeSeriesData.setValue(beforeTimeSeriesData.getValue());
                    completionTimeSeriesList.add(beforeTimestamp);
                    timeSeriesDataOriginalList.add(completionTimeSeriesData);
                }
            }
            beforeTimestamp = timestamp;
            beforeTimeSeriesData = timeSeriesDataOriginal;
            timeSeriesDataOriginalList.add(timeSeriesDataOriginal);

        }
        TimeSeriesResult timeSeriesResult = new TimeSeriesResult();
        try {
            timeSeriesResult = Asiainfo.egadsTimeSeries(timeSeriesDataOriginalList, path);
        } catch (Exception e) {
            logger.info(
                    "LinebindMonitorImpl getUserOnlineMapInfo getEgadsForecastOltOnlineMapInfo kdzlfx.forecastOlt parametertime is "
                            + time + " OltIp : " + oltIp);
            logger.error(
                    "LinebindMonitorImpl getUserOnlineMapInfo getEgadsForecastOltOnlineMapInfo parametertime is "
                            + time + " errorMessage:" + e.getMessage());
        }
        List<Olt> forecastCurrentOltList = new ArrayList<Olt>();
        if (timeSeriesResult.getResultDataList() != null
                && !timeSeriesResult.getResultDataList().isEmpty()) {
            for (TimeSeriesData timeSeriesDataforecast : timeSeriesResult.getResultDataList()) {
                String oltName = DateUtil.timeStampChangeString(
                        timeSeriesDataforecast.getTimestamp(), DEFAULT_FORMAT);
                // 预测的数据四舍五入取整
                DecimalFormat df = new DecimalFormat("##");
                DecimalFormat df2 = new DecimalFormat("##.##");
                String forecastValue = df.format(timeSeriesDataforecast.getForecastValue());
                String mapee = df2.format(timeSeriesDataforecast.getMapee());
                String mae = df2.format(timeSeriesDataforecast.getMae());
                String smape = df2.format(timeSeriesDataforecast.getSmape());
                String mape = df2.format(timeSeriesDataforecast.getMape());
                String mase = df2.format(timeSeriesDataforecast.getMase());
                String isalarm = "0";
                if (timeSeriesDataforecast.getIsalarm()) {
                    isalarm = "1";
                }
                Olt forecastCurrentOlt = new Olt();
                forecastCurrentOlt.setOltip(oltIp);
                forecastCurrentOlt.setName(oltName);
                forecastCurrentOlt.setValue(forecastValue);
                forecastCurrentOlt.setIsalarm(isalarm);
                forecastCurrentOlt.setUpperValue(forecastValue);
                forecastCurrentOlt.setLowerValue(forecastValue);
                forecastCurrentOlt.setForecastValue(forecastValue);
                forecastCurrentOlt.setMapee(mapee);
                forecastCurrentOlt.setMae(mae);
                forecastCurrentOlt.setSmape(smape);
                forecastCurrentOlt.setMape(mape);
                forecastCurrentOlt.setMase(mase);
                forecastCurrentOltList.add(forecastCurrentOlt);
            }
        }
        map.put("forecastDataList", forecastCurrentOltList);
        map.put("threshold", timeSeriesResult.getThreshold());
        map.put("propertiesThresholdMap", propertiesThresholdMap);
    }
}
