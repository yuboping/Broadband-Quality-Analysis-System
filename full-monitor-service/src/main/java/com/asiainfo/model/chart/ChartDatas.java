package com.asiainfo.model.chart;

import java.util.ArrayList;
import java.util.List;

/**
 * 图表数据
 * 
 * @author luohuawuyin
 *
 */
public class ChartDatas {
    private String title;
    private String group;
    private List<ChartPoint> data = new ArrayList<ChartPoint>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<ChartPoint> getData() {
        return data;
    }

    public void setData(List<ChartPoint> data) {
        this.data = data;
    }

}
