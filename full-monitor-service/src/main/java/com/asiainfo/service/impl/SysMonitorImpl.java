package com.asiainfo.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asiainfo.mapper.CesMonitorMonthMapper;
import com.asiainfo.model.chart.ChartDatas;
import com.asiainfo.model.chartData.MonitorInfo;
import com.asiainfo.model.chartData.ParamRelation;
import com.asiainfo.service.BasicService;
import com.asiainfo.service.SysMonitor;

@Service
public class SysMonitorImpl extends BasicService implements SysMonitor {
    @Autowired
    private CesMonitorMonthMapper monitor;
    @Override
    public List<ChartDatas> aaaForMonth(String cityCode) {
        List<ParamRelation> dates = getQueryDate(null);
        String start = dates.get(0).getValue();
        String end = dates.get(dates.size() - 1).getValue();
        List<String> titles = Arrays.asList("订购表条数", "注册表条数", "历史表条数", "话单表条数");
        List<List<MonitorInfo>> retdatas = Arrays.asList(
                monitor.monitorForMonth("AAA_SUB", cityCode, start, end),
                monitor.monitorForMonth("AAA_REG", cityCode, start, end),
                monitor.monitorForMonth("AAA_HIS", cityCode, start, end),
                monitor.monitorForMonth("AAA_DET", cityCode, start, end));
        return useDifferentData(titles, retdatas, dates);
    }

    @Override
    public List<ChartDatas> crmForMonth(String cityCode) {
        List<ParamRelation> dates = getQueryDate(null);
        String start = dates.get(0).getValue();
        String end = dates.get(dates.size() - 1).getValue();
        List<String> titles = Arrays.asList("CRM条数");
        List<List<MonitorInfo>> retdatas = Arrays
                .asList(monitor.monitorForMonth("CRM_INFO", cityCode, start, end));
        return useDifferentData(titles, retdatas, dates);
    }

    @Override
    public List<ChartDatas> faultForMonth(String cityCode) {
        List<ParamRelation> dates = getQueryDate(null);
        String start = dates.get(0).getValue();
        String end = dates.get(dates.size() - 1).getValue();
        List<String> titles = Arrays.asList("报障条数", "工单条数");
        List<List<MonitorInfo>> retdatas = Arrays.asList(
                monitor.monitorForMonth("PC_FAUIT", cityCode, start, end),
                monitor.monitorForMonth("PC_TASK", cityCode, start, end));
        return useDifferentData(titles, retdatas, dates);
    }

    @Override
    public List<ChartDatas> lineForMonth(String cityCode) {
        List<ParamRelation> dates = getQueryDate(null);
        String start = dates.get(0).getValue();
        String end = dates.get(dates.size() - 1).getValue();
        List<String> titles = Arrays.asList("线路条数");
        List<List<MonitorInfo>> retdatas = Arrays.asList(
                monitor.monitorForMonth("LINENO", cityCode, start, end));
        return useDifferentData(titles, retdatas, dates);
    }

    @Override
    public List<ChartDatas> behaviourForMonth(String cityCode) {
        List<ParamRelation> dates = getQueryDate(null);
        String start = dates.get(0).getValue();
        String end = dates.get(dates.size() - 1).getValue();
        List<String> titles = Arrays.asList("行为条数");
        List<List<MonitorInfo>> retdatas = Arrays.asList(
                monitor.monitorForMonth("BEHAVIOR", cityCode, start, end));
        return useDifferentData(titles, retdatas, dates);
    }

    @Override
    public List<ChartDatas> complaintForMonth(String cityCode) {
        List<ParamRelation> dates = getQueryDate(null);
        String start = dates.get(0).getValue();
        String end = dates.get(dates.size() - 1).getValue();
        List<String> titles = Arrays.asList("投诉条数");
        List<List<MonitorInfo>> retdatas = Arrays.asList(
                monitor.monitorForMonth("COMPLAINT", cityCode, start, end));
        return useDifferentData(titles, retdatas, dates);
    }

    @Override
    protected String getValue(String title, List<?> retdata, String attr) {
        @SuppressWarnings("unchecked")
        List<MonitorInfo> datas = (List<MonitorInfo>) retdata;
        for (MonitorInfo figures : datas) {
            if (attr.equals(figures.getAttr())) {
                return figures.getValue() == null ? "0" : figures.getValue();
            }
        }
        return "0";
    }

    @Override
    public List<ChartDatas> featureNumForMonth(String cityCode) {
        List<ParamRelation> dates = getQueryDate(null);
        String start = dates.get(0).getValue();
        String end = dates.get(dates.size() - 1).getValue();
        List<String> titles = Arrays.asList("特征表数量");
        List<List<MonitorInfo>> retdatas = Arrays
                .asList(monitor.monitorForMonth("CHARA_REC", cityCode, start, end));
        return useDifferentData(titles, retdatas, dates);
    }

    @Override
    public List<ChartDatas> markingNumForMonth(String cityCode) {
        List<ParamRelation> dates = getQueryDate(null);
        String start = dates.get(0).getValue();
        String end = dates.get(dates.size() - 1).getValue();
        List<String> titles = Arrays.asList("销户数", "开户数", "静默数", "活跃数");
        List<List<MonitorInfo>> retdatas = Arrays.asList(
                monitor.monitorForMonth("CHARA_CANCEL", cityCode, start, end),
                monitor.monitorForMonth("CHARA_OPEN", cityCode, start, end),
                monitor.monitorForMonth("CHARA_SILENT", cityCode, start, end),
                monitor.monitorForMonth("CHARA_ACTIVE", cityCode, start, end));
        return useDifferentData(titles, retdatas, dates);
    }

    @Override
    public List<ChartDatas> correlationRateForMonth(String cityCode) {
        List<ParamRelation> dates = getQueryDate(null);
        String start = dates.get(0).getValue();
        String end = dates.get(dates.size() - 1).getValue();
        List<String> titles = Arrays.asList("CRM", "报障", "线路", "行为", "投诉");
        List<List<MonitorInfo>> retdatas = Arrays.asList(
                monitor.correlationRate("RELA_CRM", "CRM_INFO", cityCode, start, end),
                monitor.correlationRate("RELA_PC", "CHARA_PC", cityCode, start, end),
                monitor.correlationRate("RELA_LINENO", "CHARA_LINENO", cityCode, start, end),
                monitor.correlationRate("RELA_BEHAVIOR", "CHARA_BEHAVIOR", cityCode, start, end),
                monitor.correlationRate("RELA_COMPLAINT", "CHARA_COMPLAINT", cityCode, start, end));
        return useDifferentData(titles, retdatas, dates);
    }

}
