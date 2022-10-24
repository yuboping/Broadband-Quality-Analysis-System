package com.asiainfo.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.asiainfo.mapper.CesDayAnalysisMapper;
import com.asiainfo.model.chart.ChartDatas;
import com.asiainfo.model.chart.ChartPoint;
import com.asiainfo.model.chartData.ParamRelation;
import com.asiainfo.model.chartData.UserKeyfigures;
import com.asiainfo.service.BasicService;
import com.asiainfo.service.DayStatistics;
import com.asiainfo.util.common.DateUtil;

@Service
public class DayStatisticsImpl extends BasicService implements DayStatistics {
    @Inject
    private CesDayAnalysisMapper dayAnalysis;
    @Override
    public List<ChartDatas> yesKeyfigures() {
        String yesterday = DateUtil.getMinusDays(DAYFORMAT, 1);
        UserKeyfigures figures = dayAnalysis.dayKeyfigures(yesterday);
        if (figures == null) {
            figures = UserKeyfigures.empty();
        }
        List<ChartDatas> ret = new ArrayList<>();
        ChartDatas datas = new ChartDatas();
        datas.setTitle("昨日关键指标");
        List<ChartPoint> data = new ArrayList<ChartPoint>();
        ChartPoint add = new ChartPoint();
        add.setName("新增用户数");
        add.setValue(figures.getAddNum() == null ? "0" : figures.getAddNum());
        data.add(add);
        ChartPoint cancel = new ChartPoint();
        cancel.setName("销户用户数");
        cancel.setValue(figures.getCancelNum() == null ? "0" : figures.getCancelNum());
        data.add(cancel);
        ChartPoint own = new ChartPoint();
        own.setName("净增用户数");
        own.setValue(figures.getOwnNum() == null ? "0" : figures.getOwnNum());
        data.add(own);
        ChartPoint stop = new ChartPoint();
        stop.setName("停机用户数");
        stop.setValue(figures.getStopNum() == null ? "0" : figures.getStopNum());
        data.add(stop);
        ChartPoint cumulative = new ChartPoint();
        cumulative.setName("累计用户数");
        cumulative.setValue(figures.getCumulativeNum() == null ? "0" : figures.getCumulativeNum());
        data.add(cumulative);
        datas.setData(data);
        ret.add(datas);
        return ret;
    }

    @Override
    public List<ChartDatas> nearly30DaysKeyfigures() {
        List<ParamRelation> dates = get30Days();
        List<UserKeyfigures> retdata = dayAnalysis.nearly30Days(dates.get(0).getValue(),
                dates.get(dates.size() - 1).getValue());
        List<String> titles = Arrays.asList("近30日新增用户数", "近30日销户用户数", "近30日净增用户数", "近30日停机用户数");
        return useSameMarkData(titles, retdata, dates);
    }

    /**
     * 根据日期匹配查询到的数据，若某日期数据为空，返回0
     * 
     * @param name
     * @param retdata
     * @param day
     * @return
     */
    protected String getValue(String title, List<?> retdata, String attr) {
        @SuppressWarnings("unchecked")
        List<UserKeyfigures> datas = (List<UserKeyfigures>) retdata;
        for (UserKeyfigures figures : datas) {
            if (attr.equals(figures.getAttr())) {
                if (title.equals("近30日新增用户数")) {
                    return figures.getAddNum() == null ? "0" : figures.getAddNum();
                } else if (title.equals("近30日销户用户数")) {
                    return figures.getCancelNum() == null ? "0" : figures.getCancelNum();
                } else if (title.equals("近30日净增用户数")) {
                    return figures.getOwnNum() == null ? "0" : figures.getOwnNum();
                } else if (title.equals("近30日停机用户数")) {
                    return figures.getStopNum() == null ? "0" : figures.getStopNum();
                }
            }
        }
        return "0";
    }

}
