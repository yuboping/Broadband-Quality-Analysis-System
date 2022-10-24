package com.asiainfo.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asiainfo.mapper.TerminalCountMapper;
import com.asiainfo.model.chart.ChartDatas;
import com.asiainfo.model.chart.ChartPoint;
import com.asiainfo.model.chartData.TerminalRest;
import com.asiainfo.model.chartData.TterminalCharacter;
import com.asiainfo.service.BasicService;
import com.asiainfo.util.ToolsUtils;

@Service
public class TerminalImpl extends BasicService {
    //1: 认证失败质差次数 2：短时上下线质差次数 3：频繁掉线终端质差次数
    public static final int DATA_TYPE_AUTHFAIL = 1;
    public static final int DATA_TYPE_SHOTOFTENUPDOWN = 2;
    public static final int DATA_TYPE_OFTENDOWNLINE = 3;
    
    // 1：地市 2： 用户 3：厂家
    public static final int DIMENSION_TYPE_AREA = 1;
    public static final int DIMENSION_TYPE_USER = 2;
    public static final int DIMENSION_TYPE_FACTORY = 3;
    
    @Autowired
    private TerminalCountMapper terminalCountMapper;
    @Override
    protected String getValue(String title, List<?> retdata, String attr) {
        return null;
    }
    
    public List<ChartDatas> getMonitorTerminalData(String type) {
        // 维度类型
        int dimensionType = Integer.parseInt(type);
        List<ChartDatas> datas = new ArrayList<ChartDatas>();
        List<TerminalRest> data = terminalCountMapper.getMonitorTerminalData(dimensionType);
        makeTerminalData(datas, data);
        return datas;
    }
    
    private void makeTerminalData(List<ChartDatas> datas, List<TerminalRest> data) {
        ChartDatas authFailChart = new ChartDatas();
        authFailChart.setTitle("认证失败终端质差排行");
        List<ChartPoint> authFailList = new ArrayList<ChartPoint>();
        ChartDatas shortOftenUpDownChart = new ChartDatas();
        shortOftenUpDownChart.setTitle("短时上下线终端质差排行");
        List<ChartPoint> shortOftenUpDownList = new ArrayList<ChartPoint>();
        ChartDatas oftenDownLineChart = new ChartDatas();
        oftenDownLineChart.setTitle("频繁掉线终端质差排行");
        List<ChartPoint> oftenDownLineList = new ArrayList<ChartPoint>();
        int dataType = 0;
        String name = null; 
        for (TerminalRest obj : data) {
            dataType = obj.getData_type().intValue();
            ChartPoint point = new ChartPoint();
            point.setValue(obj.getMval().toString());
            name = ToolsUtils.StringIsNull(obj.getAttr_name()) ? obj.getAttr_code():obj.getAttr_name();
            point.setName(name);
            switch (dataType) {
            case 1:
                authFailList.add(point);
                break;
            case 2:
                shortOftenUpDownList.add(point);
                break;
            case 3:
                oftenDownLineList.add(point);
                break;
            default:
                break;
            }
        }
        authFailChart.setData(authFailList);
        shortOftenUpDownChart.setData(shortOftenUpDownList);
        oftenDownLineChart.setData(oftenDownLineList);
        datas.add(authFailChart);
        datas.add(shortOftenUpDownChart);
        datas.add(oftenDownLineChart);
    }

    /**
     * 数据转换，提高前台查询效率
     * @param month
     */
    public void terminalCount(String month) {
        if(ToolsUtils.StringIsNull(month)) {
            // month 为空取当前月数据
//            month = DateUtil.getMonth(0);
        }
        // 计算 地市及用户数据
        countAreaData(month);
        countUserData(month);
        //计算厂家数据
        countFactoryData(month);
    }
    
    private void countAreaData(String month) {
        // 删除已存在汇总数据
        terminalCountMapper.deleteTerminalRet(DIMENSION_TYPE_AREA);
        //查询地市数据、用户（查询n个数据）数据
        List<TerminalRest> list = new ArrayList<TerminalRest>();
        List<TterminalCharacter> areaData = terminalCountMapper.getTerminalCharacterData(month);
        for (TterminalCharacter obj : areaData) {
            // 认证失败
            list.add(new TerminalRest().setDimension_type(DIMENSION_TYPE_AREA)
                    .setData_type(DATA_TYPE_AUTHFAIL).setAttr_name(obj.getCity_name())
                    .setAttr_code(obj.getCity_code()).setMval(obj.getAuthfail_count()));
            // 短时上下线质差次数
            list.add(new TerminalRest().setDimension_type(DIMENSION_TYPE_AREA)
                    .setData_type(DATA_TYPE_SHOTOFTENUPDOWN).setAttr_name(obj.getCity_name())
                    .setAttr_code(obj.getCity_code()).setMval(obj.getShorttime_count()));
            //频繁掉线终端质差次数
            list.add(new TerminalRest().setDimension_type(DIMENSION_TYPE_AREA)
                    .setData_type(DATA_TYPE_OFTENDOWNLINE).setAttr_name(obj.getCity_name())
                    .setAttr_code(obj.getCity_code()).setMval(obj.getOftendown_count()));
        }
        if(!ToolsUtils.ListIsNull(list)) {
            terminalCountMapper.insertTerminalRet(list);
        }
    }
    
    private void countUserData(String month) {
        // 删除已存在汇总数据
        terminalCountMapper.deleteTerminalRet(DIMENSION_TYPE_USER);
        List<TerminalRest> list = new ArrayList<TerminalRest>();
        // 获取 认证失败 数据
        List<TterminalCharacter> authData = terminalCountMapper.getTerminalCharacterDataOrderByUser(month,
                "AUTHFAIL_FLAG", "AUTHFAIL_COUNT");
        makeCountUserData(list,authData,DATA_TYPE_AUTHFAIL);
        // 获取 短时上下线质差次数 数据
        List<TterminalCharacter> shortTimeData = terminalCountMapper.getTerminalCharacterDataOrderByUser(month,
                "SHORTTIME_FLAG", "SHORTTIME_COUNT");
        makeCountUserData(list,shortTimeData,DATA_TYPE_SHOTOFTENUPDOWN);
        // 获取 频繁掉线终端质差 数据
        List<TterminalCharacter> oftenDownData = terminalCountMapper.getTerminalCharacterDataOrderByUser(month,
                "OFTENDOWN_FLAG", "OFTENDOWN_COUNT");
        makeCountUserData(list,oftenDownData,DATA_TYPE_OFTENDOWNLINE);
        if(!ToolsUtils.ListIsNull(list)) {
            terminalCountMapper.insertTerminalRet(list);
        }
    }
    
    private void makeCountUserData(List<TerminalRest> list, List<TterminalCharacter> data, int dataTye) {
        for (TterminalCharacter obj : data) {
            TerminalRest rt = new TerminalRest();
            rt.setDimension_type(DIMENSION_TYPE_USER)
                    .setData_type(dataTye).setAttr_name(obj.getUser_name())
                    .setAttr_code(obj.getUser_name());
            if(dataTye == DATA_TYPE_AUTHFAIL) {
                rt.setMval(obj.getAuthfail_count());
            }else if (dataTye == DATA_TYPE_SHOTOFTENUPDOWN) {
                rt.setMval(obj.getShorttime_count());
            } else {
                rt.setMval(obj.getOftendown_count());
            }
            list.add(rt);
        }
    }
    
    private void countFactoryData(String month) {
        // 删除已存在汇总数据
        terminalCountMapper.deleteTerminalRet(DIMENSION_TYPE_FACTORY);
        List<TerminalRest> list = new ArrayList<TerminalRest>();
        // 获取 认证失败 数据
        List<TterminalCharacter> authData = terminalCountMapper.getCompanyData(month,
                "AUTHFAIL_FLAG", "AUTHFAIL_COUNT");
        makeCountFactoryData(list,authData,DATA_TYPE_AUTHFAIL);
        // 获取 短时上下线质差次数 数据
        List<TterminalCharacter> shortTimeData = terminalCountMapper.getCompanyData(month,
                "SHORTTIME_FLAG", "SHORTTIME_COUNT");
        makeCountFactoryData(list,shortTimeData,DATA_TYPE_SHOTOFTENUPDOWN);
        // 获取 频繁掉线终端质差 数据
        List<TterminalCharacter> oftenDownData = terminalCountMapper.getCompanyData(month,
                "OFTENDOWN_FLAG", "OFTENDOWN_COUNT");
        makeCountFactoryData(list,oftenDownData,DATA_TYPE_OFTENDOWNLINE);
        if(!ToolsUtils.ListIsNull(list)) {
            terminalCountMapper.insertTerminalRet(list);
        }
    }
    private void makeCountFactoryData(List<TerminalRest> list, List<TterminalCharacter> data, int dataTye) {
        for (TterminalCharacter obj : data) {
            TerminalRest rt = new TerminalRest();
            rt.setDimension_type(DIMENSION_TYPE_FACTORY)
                    .setData_type(dataTye).setAttr_name(obj.getCompany_name())
                    .setAttr_code(obj.getCompany_name());
            if(dataTye == DATA_TYPE_AUTHFAIL) {
                rt.setMval(obj.getAuthfail_count());
            } else if (dataTye == DATA_TYPE_SHOTOFTENUPDOWN) {
                rt.setMval(obj.getShorttime_count());
            } else {
                rt.setMval(obj.getOftendown_count());
            }
            list.add(rt);
        }
    }
}
