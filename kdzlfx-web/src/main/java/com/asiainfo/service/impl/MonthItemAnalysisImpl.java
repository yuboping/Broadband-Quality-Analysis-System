package com.asiainfo.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asiainfo.mapper.CesMonthAnalysisMapper;
import com.asiainfo.mapper.CesMonthItemAnalysisMapper;
import com.asiainfo.model.chart.ChartDatas;
import com.asiainfo.model.chartData.MonitorInfo;
import com.asiainfo.model.chartData.ParamRelation;
import com.asiainfo.service.BasicService;
import com.asiainfo.service.MonthItemAnalysis;
import com.asiainfo.util.common.DateUtil;

@Service
public class MonthItemAnalysisImpl extends BasicService implements MonthItemAnalysis {
    @Autowired
    private CesMonthItemAnalysisMapper mapper;
    @Autowired
    private CesMonthAnalysisMapper monthanalysisMapper;

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
    public List<ChartDatas> structure() {
        String month = DateUtil.getDate(MONTHFORMAT);
        List<List<ParamRelation>> attrs = Arrays.asList(attrsParam("USER_HEALTH", month),
                attrsParam("BROADBAND_TYPE", month));
        List<String> titles = Arrays.asList("用户健康度分布结构", "用户宽带类型结构");
        List<List<MonitorInfo>> retdatas = Arrays.asList(
                mapper.userStructure("USER_HEALTH", month),// 预测离网应取当月数据
                mapper.userStructure("BROADBAND_TYPE", month));
        return useDifferentMarkData(titles, retdatas, attrs);
    }

    /**
     * 
     * @Title: attrsParam
     * @Description: TODO(获取attrs显示转换)
     * @param @param item
     * @param @param date
     * @param @return 参数
     * @return List<ParamRelation> 返回类型
     * @throws
     */
    private List<ParamRelation> attrsParam(String item, String date) {
        List<ParamRelation> paramRelationList = new ArrayList<>();
        List<MonitorInfo> retattrs = mapper.userStructureAttrs(item, date);
        for (MonitorInfo monitorInfo : retattrs) {
            paramRelationList.add(new ParamRelation(monitorInfo.getAttr(), monitorInfo.getValue()));
        }
        return paramRelationList;
    }

    @Override
    public List<ChartDatas> expiringUser(String cityCode) {
        List<ParamRelation> dates = getQueryDate(null);
        // String start = dates.get(0).getValue();
        // String end = dates.get(dates.size() - 1).getValue();
        List<String> titles = Arrays.asList("销户用户", "停机用户", "活跃用户", "静默用户");

        // TODO 3.21演示需要，暂时不从数据库中取数据
        List<List<MonitorInfo>> retdatas = Collections.emptyList();
        // List<List<MonitorInfo>> retdatas = Arrays.asList(
        // mapper.expiringUser("EXPUSER_STRU", "cancel", start, end, cityCode),
        // mapper.expiringUser("EXPUSER_STRU", "stop", start, end, cityCode),
        // mapper.expiringUser("EXPUSER_STRU", "active", start, end, cityCode),
        // mapper.expiringUser("EXPUSER_STRU", "silent", start, end, cityCode));
        return useDifferentData(titles, retdatas, dates);
    }

}
