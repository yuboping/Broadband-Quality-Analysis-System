package com.asiainfo.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asiainfo.initdata.InitDataListener;
import com.asiainfo.mapper.CesMonthAnalysisMapper;
import com.asiainfo.mapper.OltMapper;
import com.asiainfo.model.chart.ChartDatas;
import com.asiainfo.model.chartData.OltInfo;
import com.asiainfo.model.chartData.ParamRelation;
import com.asiainfo.model.chartData.UserKeyfigures;
import com.asiainfo.model.system.CesCityCode;
import com.asiainfo.service.BasicService;
import com.asiainfo.service.MonthStatistics;
import com.asiainfo.util.common.DateUtil;
import com.asiainfo.util.common.Judgment;

@Service
public class MonthStatisticsImpl extends BasicService implements MonthStatistics {
    @Autowired
    private InitDataListener initdata;
    @Inject
    private CesMonthAnalysisMapper analysis;
    @Inject
    private OltMapper olt;

    /**
     * 上月各地市用户分布情况
     */
    @Override
    public List<ChartDatas> userScatterForCity() {
        String premonth = DateUtil.getMinusMonths(MONTHFORMAT, 1);
        List<UserKeyfigures> retdata = analysis.userScatterForCity(premonth);
        List<ParamRelation> attrs = null;
        if (!Judgment.listIsNull(retdata)) {
            attrs = makeOrderedCity(retdata);
        } else {
            attrs = getCitys();
        }
        List<String> titles = Arrays.asList("用户数", "活跃", "静默", "停机");
        return useSameMarkData(titles, retdata, attrs);
    }

    /**
     * 按照注册用户数降序排列地市顺序
     * 
     * @param retdata
     * @return
     */
    private List<ParamRelation> makeOrderedCity(List<UserKeyfigures> retdata) {
        List<ParamRelation> attrs = new ArrayList<>();
        for (UserKeyfigures e : retdata) {
            ParamRelation attr = new ParamRelation();
            attr.setShow(initdata.getCityName(e.getAttr()) + "市");
            attr.setValue(e.getAttr());
            attrs.add(attr);
        }
        if (attrs.size() == retdata.size()) {
            return attrs;
        }
        List<CesCityCode> citys = initdata.getAllCitys();
        citys.forEach(city -> {
            if (!findCityDataExist(retdata, city.getCityCode())) {
                ParamRelation attr = new ParamRelation();
                attr.setShow(city.getCityName() + "市");
                attr.setValue(city.getCityCode());
                attrs.add(attr);
            }
        });
        return attrs;
    }

    /**
     * 该地市是否已有数据
     * 
     * @param retdata
     * @param city
     * @return
     */
    private boolean findCityDataExist(List<UserKeyfigures> retdata, String cityCode) {
        for (UserKeyfigures e : retdata) {
            if (cityCode.equals(e.getAttr())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected String getValue(String title, List<?> retdata, String attr) {
        @SuppressWarnings("unchecked")
        List<UserKeyfigures> datas = (List<UserKeyfigures>) retdata;
        for (UserKeyfigures figures : datas) {
            if (attr.equals(figures.getAttr())) {
                if (title.equals("用户数") || title.equals("注册")) {
                    return figures.getRegNum() == null ? "0" : figures.getRegNum();
                } else if (title.equals("活跃")) {
                    return figures.getActiveNum() == null ? "0" : figures.getActiveNum();
                } else if (title.equals("静默")) {
                    return figures.getSilentNum() == null ? "0" : figures.getSilentNum();
                } else if (title.equals("停机")) {
                    return figures.getStopNum() == null ? "0" : figures.getStopNum();
                } else if (title.equals("新增")) {
                    return figures.getAddNum() == null ? "0" : figures.getAddNum();
                } else if (title.equals("销户")) {
                    return figures.getCancelNum() == null ? "0" : "-" + figures.getCancelNum();// 将销户数变为负数
                } else if (title.equals("净增")) {
                    return figures.getOwnNum() == null ? "0" : figures.getOwnNum();
                } else if (title.equals("二级预警用户数")) {
                    return figures.getSecondOffnetNum() == null ? "0" : figures
                            .getSecondOffnetNum();
                } else if (title.equals("三级预警用户数")) {
                    return figures.getThirdOffnetNum() == null ? "0" : figures.getThirdOffnetNum();
                }
            }
        }
        return "0";
    }

    @Override
    public List<ChartDatas> allkindsNumForCity(String month) {
        if (Judgment.stringIsNull(month)) {
            month = DateUtil.getMinusMonths(MONTHFORMAT, 1);
        }
        List<UserKeyfigures> retdata = analysis.userScatterForCity(month);
        List<String> titles = Arrays.asList("注册", "净增", "销户", "新增", "活跃", "静默", "停机");// 顺序不可调换
        List<ParamRelation> attrs = null;
        if (!Judgment.listIsNull(retdata)) {
            attrs = makeOrderedCity(retdata);
        } else {
            attrs = getCitys();
        }
        return useSameMarkData(titles, retdata, attrs);
    }

    @Override
    public List<ChartDatas> allkindsNumForDate(String cityCode, String queryDate) {
        List<ParamRelation> dates = getQueryDate(queryDate);
        List<UserKeyfigures> retdata = analysis.userScatterForDate(cityCode, dates.get(0)
                .getValue(), dates.get(dates.size() - 1).getValue());
        List<String> titles = Arrays.asList("注册", "净增", "销户", "新增", "活跃", "静默", "停机");// 顺序不可调换
        return useSameMarkData(titles, retdata, dates);
    }

    @Override
    public List<ChartDatas> silenceNumForDate(String cityCode) {
        List<ParamRelation> dates = getQueryDate(null);
        List<UserKeyfigures> retdata = analysis.userScatterForDate(cityCode, dates.get(0)
                .getValue(), dates.get(dates.size() - 1).getValue());
        List<String> titles = Arrays.asList("二级预警用户数");
        return useSameMarkData(titles, retdata, dates);
    }

    @Override
    public List<ChartDatas> stopNumForDate(String cityCode) {
        List<ParamRelation> dates = getQueryDate(null);
        List<UserKeyfigures> retdata = analysis.userScatterForDate(cityCode, dates.get(0)
                .getValue(), dates.get(dates.size() - 1).getValue());
        List<String> titles = Arrays.asList("三级预警用户数");
        return useSameMarkData(titles, retdata, dates);
    }

    @Override
    public List<ChartDatas> warningUser(String cityCode) {
        List<ParamRelation> dates = getPre1year();
        List<UserKeyfigures> retdata = analysis.userScatterForDate(cityCode, dates.get(0)
                .getValue(), dates.get(dates.size() - 1).getValue());
        List<String> titles = Arrays.asList("二级预警用户数", "三级预警用户数");
        return useSameMarkData(titles, retdata, dates);
    }

    /**
     * OLT
     */
    @Override
    public List<OltInfo> userOlt() {
        String month = DateUtil.getMinusMonths(MMFORMAT, 0);
        return olt.userOlt(month);
    }

    /**
     * OLT
     */
    @Override
    public List<OltInfo> userOltByOltip(String oltip) {
        String month = DateUtil.getMinusMonths(MMFORMAT, 0);
        return olt.userOltByOltip(oltip, month);
    }

}
