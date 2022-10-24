package com.asiainfo.service;

import com.asiainfo.model.chartData.BadQualityPieChartVO;

import java.util.List;
import java.util.Map;

public interface BadQuality {

    Map<String, List<BadQualityPieChartVO>> getBadQualityPieChartData();

}
