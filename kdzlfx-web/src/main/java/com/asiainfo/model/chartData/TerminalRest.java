package com.asiainfo.model.chartData;

public class TerminalRest {
    private Integer dimension_type;
    private Integer data_type;
    private String  attr_code;
    private String  attr_name;
    private Integer mval;
    
    public Integer getDimension_type() {
        return dimension_type;
    }
    public TerminalRest setDimension_type(Integer dimension_type) {
        this.dimension_type = dimension_type;
        return this;
    }
    public Integer getData_type() {
        return data_type;
    }
    public TerminalRest setData_type(Integer data_type) {
        this.data_type = data_type;
        return this;
    }
    public String getAttr_code() {
        return attr_code;
    }
    public TerminalRest setAttr_code(String attr_code) {
        this.attr_code = attr_code;
        return this;
    }
    public String getAttr_name() {
        return attr_name;
    }
    public TerminalRest setAttr_name(String attr_name) {
        this.attr_name = attr_name;
        return this;
    }
    public Integer getMval() {
        return mval;
    }
    public TerminalRest setMval(Integer mval) {
        if(null == mval) {
            this.mval = 0;
        } else {
            this.mval = mval;
        }
        return this;
    }
}
