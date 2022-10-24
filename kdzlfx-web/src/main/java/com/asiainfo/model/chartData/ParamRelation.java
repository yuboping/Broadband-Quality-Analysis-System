package com.asiainfo.model.chartData;

/**
 * 查询值与展示值对应关系
 * 
 * @author luohuawuyin
 *
 */
public class ParamRelation {
    private String value;// 实际值
    private String show;// 显示值

    public ParamRelation() {

    }

    public ParamRelation(String value, String show) {
        this.value = value;
        this.show = show;
    }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

}
