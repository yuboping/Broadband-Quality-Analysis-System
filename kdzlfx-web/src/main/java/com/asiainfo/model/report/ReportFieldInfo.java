package com.asiainfo.model.report;

public class ReportFieldInfo {
    private String id;
    private String fieldId;
    private String reportId;
    private String showName;// 报表在页面显示的字段名称
    private String sqlField;// 报表字段对应数据库表字段名称
    
    private String calculatetotal;// 是否计算合计，1:计算，默认为空，或者0，不计算
    private String formula;// 计算公式,为空则值相加，不为空，按公式计算

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getSqlField() {
        return sqlField;
    }

    public void setSqlField(String sqlField) {
        this.sqlField = sqlField;
    }

    public String getCalculatetotal() {
        return calculatetotal;
    }

    public void setCalculatetotal(String calculatetotal) {
        this.calculatetotal = calculatetotal;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }
}
