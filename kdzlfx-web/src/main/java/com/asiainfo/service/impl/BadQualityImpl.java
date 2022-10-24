package com.asiainfo.service.impl;

import com.asiainfo.mapper.CesBadQualityPieChartMapper;
import com.asiainfo.model.chartData.BadQualityPieChartVO;
import com.asiainfo.service.BadQuality;
import com.asiainfo.util.common.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BadQualityImpl implements BadQuality {

    private static final String INNER_IS_GATEWAY = "1";
    private static final String INNER_IS_NOT_GATEWAY = "0";
    private static final String OUTER_IS_BAD_QUALITY = "1";
    private static final String OUTER_IS_NOT_BAD_QUALITY = "0";

    @Autowired
    private CesBadQualityPieChartMapper cesBadQualityPieChartMapper;

    /**
     * 获取双环图数据
     * @return
     */
    @Override
    public Map<String, List<BadQualityPieChartVO>> getBadQualityPieChartData() {
        String premonth = DateUtil.getMinusMonths("yyyyMM", 0);
        Map<String, List<BadQualityPieChartVO>> pieMap = new HashMap<>();
        List<BadQualityPieChartVO> innerPieDataTemp = cesBadQualityPieChartMapper.getInnerPieData(premonth);
        List<BadQualityPieChartVO> outterPieDataTemp = cesBadQualityPieChartMapper.getOutterPieData(premonth);
        List<BadQualityPieChartVO> innerPieData = new ArrayList<>();
        List<BadQualityPieChartVO> outerPieData = new ArrayList<>();
        for (BadQualityPieChartVO iPieData : innerPieDataTemp) {
            if (iPieData.getName().equals(INNER_IS_GATEWAY)) {
                iPieData.setName("智能网关");
            }else {
                iPieData.setName("普通路由");
            }
            innerPieData.add(iPieData);
        }
        for (BadQualityPieChartVO oPieData : outterPieDataTemp) {
            if (oPieData.getInnerValue().toString().equals(INNER_IS_GATEWAY)&&oPieData.getName().equals(OUTER_IS_BAD_QUALITY)) {
                oPieData.setName("质差智能网关");
            } else if (oPieData.getInnerValue().toString().equals(INNER_IS_NOT_GATEWAY)&&oPieData.getName().equals(OUTER_IS_BAD_QUALITY)) {
                oPieData.setName("质差普通路由");
            } else if (oPieData.getInnerValue().toString().equals(INNER_IS_GATEWAY)&&oPieData.getName().equals(OUTER_IS_NOT_BAD_QUALITY)) {
                oPieData.setName("非质差智能网关");
            } else {
                oPieData.setName("非质差普通路由");
            }
            outerPieData.add(oPieData);
        }
        if (CollectionUtils.isEmpty(innerPieData)) {
            BadQualityPieChartVO wgPieChartVO = new BadQualityPieChartVO();
            wgPieChartVO.setName("智能网关");
            wgPieChartVO.setValue(0);
            BadQualityPieChartVO ptPieChartVO = new BadQualityPieChartVO();
            ptPieChartVO.setName("普通路由");
            ptPieChartVO.setValue(0);
            innerPieData.add(wgPieChartVO);
            innerPieData.add(ptPieChartVO);
        }
        if (CollectionUtils.isEmpty(outerPieData)) {
            BadQualityPieChartVO wgPieChartVO = new BadQualityPieChartVO();
            wgPieChartVO.setName("质差智能网关");
            wgPieChartVO.setValue(0);
            BadQualityPieChartVO wgNoZcPieChartVO = new BadQualityPieChartVO();
            wgNoZcPieChartVO.setName("非质差智能网关");
            wgNoZcPieChartVO.setValue(0);
            BadQualityPieChartVO ptNoZcPieChartVO = new BadQualityPieChartVO();
            ptNoZcPieChartVO.setName("非质差普通路由");
            ptNoZcPieChartVO.setValue(0);
            BadQualityPieChartVO ptPieChartVO = new BadQualityPieChartVO();
            ptPieChartVO.setName("质差普通路由");
            ptPieChartVO.setValue(0);
            outerPieData.add(wgPieChartVO);
            outerPieData.add(ptPieChartVO);
            outerPieData.add(wgNoZcPieChartVO);
            outerPieData.add(ptNoZcPieChartVO);
        }
        pieMap.put("innerData", innerPieData);
        pieMap.put("outerData", outerPieData);

        return pieMap;
    }
}
