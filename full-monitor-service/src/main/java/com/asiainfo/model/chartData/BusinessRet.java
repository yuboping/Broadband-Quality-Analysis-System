package com.asiainfo.model.chartData;

public class BusinessRet {
    private Integer dimension_type;
    private Integer business_type;
    private String name;
    private float value;
    private String mark;
    public Integer getDimension_type() {
        return dimension_type;
    }
    public void setDimension_type(Integer dimension_type) {
        this.dimension_type = dimension_type;
    }
    public Integer getBusiness_type() {
        return business_type;
    }
    public void setBusiness_type(Integer business_type) {
        this.business_type = business_type;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getMark() {
        return mark;
    }
    public void setMark(String mark) {
        this.mark = mark;
    }
    public float getValue() {
        return value;
    }
    public void setValue(float value) {
        this.value = value;
    }
}
