package com.asiainfo.model.chart;

public class ChartPoint {
    private String name;
    private String value;
    private String mark;
    public ChartPoint() {

    }

    public ChartPoint(String name, String value) {
        this.name = name;
        this.value = value;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

}
