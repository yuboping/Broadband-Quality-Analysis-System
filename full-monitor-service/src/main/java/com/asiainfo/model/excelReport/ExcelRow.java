package com.asiainfo.model.excelReport;

import java.util.List;

public class ExcelRow {
    public ExcelRow() {
    }

    public ExcelRow(List<ExcelCell> cellList) {
        this.cellList = cellList;
    }

    private List<ExcelCell> cellList;

    public List<ExcelCell> getCellList() {
        return cellList;
    }

    public void setCellList(List<ExcelCell> cellList) {
        this.cellList = cellList;
    }

}
