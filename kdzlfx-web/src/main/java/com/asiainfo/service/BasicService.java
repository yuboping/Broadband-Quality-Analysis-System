package com.asiainfo.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.asiainfo.initdata.InitDataListener;
import com.asiainfo.model.chart.ChartDatas;
import com.asiainfo.model.chart.ChartPoint;
import com.asiainfo.model.chartData.MonitorInfo;
import com.asiainfo.model.chartData.ParamRelation;
import com.asiainfo.model.system.CesCityCode;
import com.asiainfo.util.common.ConfigUtil;
import com.asiainfo.util.common.DateUtil;
import com.asiainfo.util.common.Judgment;

/**
 * service父类，提供公用方法
 * 
 * @author luohuawuyin
 *
 */
public abstract class BasicService {
    @Inject
    private InitDataListener initdata;
    protected static final String DAYFORMAT = "yyyyMMdd";
    protected static final String MONTHFORMAT = "yyyyMM";
    protected static final String MMFORMAT = "MM";
    /**
     * 数据查询结果为空时构造结果为0的数据返回给前台
     * 
     * @param dates
     * @return
     */
    protected List<ChartDatas> makeEmptyDate(List<String> titles, List<ParamRelation> attrs) {
        List<ChartDatas> ret = new ArrayList<>();
        titles.forEach(title -> {
            ChartDatas datas = emptyChartDatas(title, attrs);
            ret.add(datas);
        });
        return ret;
    }

    /**
     * 一行空数据
     * 
     * @param title
     * @param attrs
     * @return
     */
    protected ChartDatas emptyChartDatas(String title, List<ParamRelation> attrs) {
        ChartDatas datas = new ChartDatas();
        datas.setTitle(title);
        List<ChartPoint> data = new ArrayList<ChartPoint>();
        attrs.forEach(attr -> {
            ChartPoint point = new ChartPoint();
            point.setName(attr.getShow());
            point.setValue("0");
            data.add(point);
        });
        datas.setData(data);
        return datas;
    }

    /**
     * 根据attr匹配查询到的数据，若某attr对应的数据为空，返回0
     * 
     * @param title
     * @param retdata
     * @param name
     * @return
     */
    protected abstract String getValue(String title, List<?> retdata, String attr);

    /**
     * 近1年，从上月开始算起(展示的月不包括年)
     * 
     * @return
     */
    protected List<ParamRelation> getPre1year() {
        List<ParamRelation> attrs = new ArrayList<>();
        for (int i = 12; i > 0; i--) {
            String month = DateUtil.getMinusMonths(MONTHFORMAT, i);
            ParamRelation attr = new ParamRelation();
            attr.setShow(month.substring(2, 4) + "/" + month.substring(4, 6));
            attr.setValue(month);
            attrs.add(attr);
        }
        return attrs;
    }

    /**
     * 最近12个月，从当月开始算起
     * 
     * @return
     */
    protected List<ParamRelation> getNear12Month() {
        List<ParamRelation> attrs = new ArrayList<>();
        for (int i = 11; i >= 0; i--) {
            String month = DateUtil.getMinusMonths(MONTHFORMAT, i);
            ParamRelation attr = new ParamRelation();
            attr.setShow(month.substring(2, 4) + "/" + month.substring(4, 6));
            attr.setValue(month);
            attrs.add(attr);
        }
        return attrs;
    }

    /**
     * 近一年或两年的日期
     * 
     * @return
     */
    protected List<ParamRelation> getQueryDate(String year) {
        List<ParamRelation> attrs = new ArrayList<>();
        int month = 12;
        if ("two".equals(year)) {
            month = 24;
        }
        for (int i = month; i > 0; i--) {
            String day = DateUtil.getMinusMonths(MONTHFORMAT, i);
            ParamRelation attr = new ParamRelation();
            attr.setShow(day.substring(2, 4) + "/" + day.substring(4, 6));
            attr.setValue(day);
            attrs.add(attr);
        }
        return attrs;
    }

    /**
     * 所有地市（排除省中心）
     * 
     * @return
     */
    protected List<ParamRelation> getCitys() {
        List<CesCityCode> citys = initdata.getAllCitys();
        List<ParamRelation> attrs = new ArrayList<>();
        citys.forEach(city -> {
            ParamRelation attr = new ParamRelation();
            attr.setShow(city.getCityName() + "市");
            attr.setValue(city.getCityCode());
            attrs.add(attr);
        });
        return attrs;
    }

    /**
     * 近30天日期
     * 
     * @return
     */
    protected List<ParamRelation> get30Days() {
        List<ParamRelation> attrs = new ArrayList<>();
        for (int i = 30; i > 0; i--) {
            String day = DateUtil.getMinusDays(DAYFORMAT, i);
            ParamRelation attr = new ParamRelation();
            attr.setShow(day.substring(6, 8));
            attr.setValue(day);
            attrs.add(attr);
        }
        return attrs;
    }

    /**
     * 一行实际数据
     * 
     * @param title
     * @param retdata
     * @param attrs
     * @return
     */
    protected ChartDatas actualChartDatas(String title, List<?> retdata,
            List<ParamRelation> attrs) {
        ChartDatas datas = new ChartDatas();
        datas.setTitle(title);
        List<ChartPoint> points = new ArrayList<ChartPoint>();
        attrs.forEach(attr -> {
            ChartPoint point = new ChartPoint();
            point.setName(attr.getShow());
            point.setValue(getValue(title, retdata, attr.getValue()));
            points.add(point);
        });
        datas.setData(points);
        return datas;
    }

    /**
     * 所有组别的数据都在retdata中的不同列，所有组别数据使用相同的数据点标记attrs
     * 
     * @param titles每组数据的名称，相当于echarts中的legend
     * @param retdata数据
     * @param attrs每一个数据点的标记，相当于echarts中的x轴名称
     * @return
     */
    protected List<ChartDatas> useSameMarkData(List<String> titles, List<?> retdata,
            List<ParamRelation> attrs) {
        if (Judgment.listIsNull(retdata)) {
            return makeEmptyDate(titles, attrs);
        }
        List<ChartDatas> ret = new ArrayList<>();
        titles.forEach(title -> {
            ret.add(actualChartDatas(title, retdata, attrs));
        });
        return ret;
    }

    /**
     * 每个组别title的数据都按顺序对应在retdatas列表中，所有组别数据使用相同的数据点标记attrs
     * 
     * @param titles每组数据的名称，相当于echarts中的legend
     * @param retdata数据
     * @param attrs每一个数据点的标记，相当于echarts中的x轴名称
     * @return
     */
    protected List<ChartDatas> useDifferentData(List<String> titles,
            List<List<MonitorInfo>> retdatas, List<ParamRelation> attrs) {
        List<ChartDatas> ret = new ArrayList<>();
        for (int i = 0; i < titles.size(); i++) {
            String title = titles.get(i);
            List<MonitorInfo> retdata = retdatas.get(i);
            if (Judgment.listIsNull(retdata)) {
                ret.add(emptyChartDatas(title, attrs));
            } else {
                ret.add(actualChartDatas(title, retdata, attrs));
            }
        }
        return ret;
    }

    /**
     * 每个组别title的数据都按顺序对应在retdatas列表中，同时每个组别数据的标记按顺序对应在attrs列表中
     * 
     * @param titles每组数据的名称，相当于echarts中的legend
     * @param retdata数据
     * @param attrs每一个数据点的标记，相当于echarts中的x轴名称
     * @return
     */
    protected List<ChartDatas> useDifferentMarkData(List<String> titles,
            List<List<MonitorInfo>> retdatas, List<List<ParamRelation>> attrs) {
        List<ChartDatas> ret = new ArrayList<>();
        for (int i = 0; i < titles.size(); i++) {
            String title = titles.get(i);
            List<MonitorInfo> retdata = retdatas.get(i);
            if (Judgment.listIsNull(retdata)) {
                ret.add(emptyChartDatas(title, attrs.get(i)));
            } else {
                ret.add(actualChartDatas(title, retdata, attrs.get(i)));
            }
        }
        return ret;
    }

    /**
     * 根据配置获取不同省份的model标识
     * 
     * @return
     */
    protected String getModel() {
        String model = "";
        String province = ConfigUtil.getPropertyKey("provicename");
        if ("shandong".equals(province)) {
            model = "sdcu";
        }
        return model;
    }

}
