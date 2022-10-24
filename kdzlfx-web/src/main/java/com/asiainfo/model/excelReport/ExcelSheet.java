package com.asiainfo.model.excelReport;

import java.util.List;

public class ExcelSheet {
    private String sheetName;
    private List<ExcelRow> headerList;
    private List<ExcelRow> infoList;
    private List<ExcelRow> footerList;

    public ExcelSheet() {
    }

    public ExcelSheet(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public List<ExcelRow> getHeaderList() {
        return headerList;
    }

    public void setHeaderList(List<ExcelRow> headerList) {
        this.headerList = headerList;
    }

    public List<ExcelRow> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<ExcelRow> infoList) {
        this.infoList = infoList;
    }

    public List<ExcelRow> getFooterList() {
        return footerList;
    }

    public void setFooterList(List<ExcelRow> footerList) {
        this.footerList = footerList;
    }
}
