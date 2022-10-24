package com.asiainfo.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asiainfo.mapper.HealThCountMapper;
import com.asiainfo.model.chart.ChartDatas;
import com.asiainfo.model.chart.ChartPoint;
import com.asiainfo.model.chartData.BusinessRet;
import com.asiainfo.service.BasicService;
import com.asiainfo.util.ToolsUtils;

@Service
public class HealThCountImpl extends BasicService {
    public static final int BUSINESS_TYPE_AAA = 1;
    public static final int BUSINESS_TYPE_TERMINAL = 2;
    public static final int BUSINESS_TYPE_COMPLAINT = 3;
    @Autowired
    private HealThCountMapper healThCountMapper;

    @Override
    protected String getValue(String title, List<?> retdata, String attr) {
        return null;
    }

    public void healthCount(String month) {
        if (ToolsUtils.StringIsNull(month)) {
            // month 为空取当前月数据
            // month = DateUtil.getMonth(0);
        }
        // 计算 AAA特征数据
        countAAAData(month);
        // 汇总终端数据
        countTerminalData(month);
        // 汇总 投诉告障数据
        countComplaintData(month);
        // 汇总行为特征数据
        countBehaviourData(month);
        // 上网线路数据
        countLineData(month);
    }

    private void countAAAData(String month) {
        // 删除 AAA 特征汇总数据
        healThCountMapper.deleteBusinessRet(BUSINESS_TYPE_AAA);
        // 新增AAA特征汇总数据
        // 地市数据
        List<BusinessRet> ret = new ArrayList<BusinessRet>();
        List<BusinessRet> areaData = healThCountMapper.getUserCharacterHealthRet(month);
        if (!ToolsUtils.ListIsNull(areaData)) {
            ret.addAll(areaData);
        }
        // 每个地市排名最低前十位用户数据
        for (BusinessRet businessRet : areaData) {
            List<BusinessRet> userDate = healThCountMapper.getUserCharacterHealthRetByArea(month,
                    businessRet.getMark());
            if (!ToolsUtils.ListIsNull(userDate)) {
                ret.addAll(userDate);
            }
        }
        if (!ToolsUtils.ListIsNull(ret)) {
            healThCountMapper.insertBusinessRet(ret);
        }
    }

    private void countTerminalData(String month) {
        // 删除告障信息
        healThCountMapper.deleteBusinessRet(BUSINESS_TYPE_TERMINAL);
        List<BusinessRet> ret = new ArrayList<BusinessRet>();
        // 汇总地市数据
        List<BusinessRet> areaData = healThCountMapper.getTerminalCharacterHealthRet(month);
        if (!ToolsUtils.ListIsNull(areaData)) {
            ret.addAll(areaData);
        }
        // 汇总用户数据 每个地市排名最低前十位用户数据
        for (BusinessRet businessRet : areaData) {
            List<BusinessRet> userDate = healThCountMapper
                    .getTerminalCharacterHealthRetByArea(month, businessRet.getMark());
            if (!ToolsUtils.ListIsNull(userDate)) {
                ret.addAll(userDate);
            }
        }
        if (!ToolsUtils.ListIsNull(ret)) {
            healThCountMapper.insertBusinessRet(ret);
        }
    }

    private void countComplaintData(String month) {
        // 删除告障信息
        healThCountMapper.deleteBusinessRet(BUSINESS_TYPE_COMPLAINT);
        List<BusinessRet> ret = new ArrayList<BusinessRet>();
        // 汇总地市数据
        List<BusinessRet> areaData = healThCountMapper.getComplaintCharacterHealthRet(month);
        if (!ToolsUtils.ListIsNull(areaData)) {
            ret.addAll(areaData);
        }
        // 汇总用户数据 每个地市排名最低前十位用户数据
        for (BusinessRet businessRet : areaData) {
            List<BusinessRet> userDate = healThCountMapper
                    .getComplaintCharacterHealthRetByArea(month, businessRet.getMark());
            if (!ToolsUtils.ListIsNull(userDate)) {
                ret.addAll(userDate);
            }
        }
        if (!ToolsUtils.ListIsNull(ret)) {
            healThCountMapper.insertBusinessRet(ret);
        }
    }

    private void countBehaviourData(String month) {
        countCharacterData(month, "CES_BEHAVIOUR_CHARACTER", 5);
    }

    private void countLineData(String month) {
        countCharacterData(month, "CES_LINE_CHARACTER", 4);
    }

    private void countCharacterData(String month, String table, int businessType) {
        // 删除告障信息
        healThCountMapper.deleteBusinessRet(businessType);
        List<BusinessRet> ret = new ArrayList<BusinessRet>();
        // 汇总地市数据
        List<BusinessRet> areaData = healThCountMapper.getCharacterHealthRet(month, table,
                businessType);
        if (!ToolsUtils.ListIsNull(areaData)) {
            ret.addAll(areaData);
        }
        // 汇总用户数据 每个地市排名最低前十位用户数据
        for (BusinessRet businessRet : areaData) {
            List<BusinessRet> userDate = healThCountMapper.getCharacterHealthRetByArea(month, table,
                    businessRet.getMark(), businessType);
            if (!ToolsUtils.ListIsNull(userDate)) {
                ret.addAll(userDate);
            }
        }
        if (!ToolsUtils.ListIsNull(ret)) {
            // for (BusinessRet o : ret) {
            // if(ToolsUtils.StringIsNull(o.getName()) ||
            // o.getBusiness_type()==null || o.getDimension_type()==null
            // ) {
            // System.out.println(o.toString());
            // }
            // }
            healThCountMapper.insertBusinessRet(ret);
        }
    }

    public ChartDatas getMonitorAreaData(String type) {
        int a = Integer.parseInt(type);
        ChartDatas ret = new ChartDatas();
        List<ChartPoint> data = healThCountMapper.getBusinessRetAreaInfo(a);
        ret.setTitle("地市健康度均分");
        ret.setData(data);
        return ret;
    }

    public ChartDatas getMonitorUserData(String type, String cityCode) {
        int a = Integer.parseInt(type);
        ChartDatas ret = new ChartDatas();
        List<ChartPoint> data = healThCountMapper.getBusinessRetUserInfo(a, cityCode);
        ret.setData(data);
        return ret;
    }

}
