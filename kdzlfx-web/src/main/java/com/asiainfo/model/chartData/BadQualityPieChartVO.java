package com.asiainfo.model.chartData;

public class BadQualityPieChartVO {

    private Integer innerValue;
    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getInnerValue() {
        return innerValue;
    }

    public void setInnerValue(Integer innerValue) {
        this.innerValue = innerValue;
    }
}
