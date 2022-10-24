package com.asiainfo.model.excelReport;

import java.util.List;

public class ExcelReport {
    private List<ExcelSheet> sheetList;
    private String reportName;

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public List<ExcelSheet> getSheetList() {
        return sheetList;
    }

    public void setSheetList(List<ExcelSheet> sheetList) {
        this.sheetList = sheetList;
    }

}
