package com.asiainfo.model.excelReport;

public class ExcelCell {
    private String value;

    private int rowspan;
    private int colspan;

    public ExcelCell() {
    }

    public ExcelCell(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getRowspan() {
        return rowspan;
    }

    public void setRowspan(int rowspan) {
        this.rowspan = rowspan;
    }

    public int getColspan() {
        return colspan;
    }

    public void setColspan(int colspan) {
        this.colspan = colspan;
    }
}
