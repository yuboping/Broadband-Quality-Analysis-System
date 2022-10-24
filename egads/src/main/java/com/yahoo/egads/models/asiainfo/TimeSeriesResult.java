package com.yahoo.egads.models.asiainfo;

import java.util.List;
import java.util.Map;

public class TimeSeriesResult {

    private List<TimeSeriesData> resultDataList;

    private Map<String, Object> threshold;

    private Integer mesCode;

    private String mesgDesc;

    public List<TimeSeriesData> getResultDataList() {
        return resultDataList;
    }

    public void setResultDataList(List<TimeSeriesData> resultDataList) {
        this.resultDataList = resultDataList;
    }

    public Integer getMesCode() {
        return mesCode;
    }

    public void setMesCode(Integer mesCode) {
        this.mesCode = mesCode;
    }

    public String getMesgDesc() {
        return mesgDesc;
    }

    public void setMesgDesc(String mesgDesc) {
        this.mesgDesc = mesgDesc;
    }

    public Map<String, Object> getThreshold() {
        return threshold;
    }

    public void setThreshold(Map<String, Object> threshold) {
        this.threshold = threshold;
    }
}
