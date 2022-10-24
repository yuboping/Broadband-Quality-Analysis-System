package com.asiainfo.util.poi;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;

import com.asiainfo.model.excelReport.ExcelCell;
import com.asiainfo.model.excelReport.ExcelReport;
import com.asiainfo.model.excelReport.ExcelRow;
import com.asiainfo.model.excelReport.ExcelSheet;
import com.asiainfo.model.report.ReportFieldInfo;

public class ExcelDocument {
    public static HSSFWorkbook mkExcel(Map<String, Object> exportMap) {
        HSSFWorkbook wb = new HSSFWorkbook();
        String tableName = (String) exportMap.get("tableName");
        String[][] data = (String[][]) exportMap.get("data");
        @SuppressWarnings("unchecked")
        List<ReportFieldInfo> fieldInfoList = (List<ReportFieldInfo>) exportMap
                .get("fieldInfoList");

        HSSFSheet sheet = wb.createSheet(tableName);
        mkSheetFieldName(getFieldNameStyle(wb), sheet, fieldInfoList);
        mkSheetInfo(getSheetInfoStyle(wb), sheet, data, fieldInfoList.size());
        autoSheet(sheet);
        return wb;
    }

    public static HSSFWorkbook mkExcel(ExcelReport report) {
        HSSFWorkbook wb = new HSSFWorkbook();
        List<ExcelSheet> list = report.getSheetList();
        if (list == null || list.isEmpty()) {
            wb.createSheet("Sheet1");
        } else {
            for (ExcelSheet tmp_sheet : report.getSheetList()) {
                HSSFSheet sheet = wb.createSheet(tmp_sheet.getSheetName());
                mkSheetInfo(getFieldNameStyle(wb), sheet, tmp_sheet.getHeaderList(), 0);
                mkSheetInfo(getSheetInfoStyle(wb), sheet, tmp_sheet.getInfoList(),
                        sheet.getLastRowNum() + 1);
                mkSheetInfo(getSheetInfoStyle(wb), sheet, tmp_sheet.getFooterList(),
                        sheet.getLastRowNum() + 1);
                autoSheet(sheet);
            }
        }
        return wb;
    }

    // 报表业务，第一行列名显示
    private static void mkSheetFieldName(CellStyle style, HSSFSheet sheet,
            List<ReportFieldInfo> fieldInfoList) {
        HSSFRow row = sheet.createRow(0);
        int length = fieldInfoList.size();
        for (int i = 0; i < length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(fieldInfoList.get(i).getShowName());
            cell.setCellStyle(style);
        }
    }

    // 报表业务，报表行显示
    private static void mkSheetInfo(CellStyle style, HSSFSheet sheet, List<ExcelRow> infoList,
            int startNum) {
        if (infoList == null || infoList.isEmpty()) {
            return;
        }
        for (ExcelRow rowInfo : infoList) {
            HSSFRow row = sheet.createRow(startNum);
            List<ExcelCell> cellLIst = rowInfo.getCellList();
            int width = cellLIst.size();
            for (int j = 0; j < width; j++) {
                HSSFCell cell = row.createCell(j);
                cell.setCellStyle(style);
                ExcelCell cellInfo = cellLIst.get(j);
                String tmp = cellInfo.getValue();
                cell.setCellValue(tmp);
                if (cellInfo.getColspan() > 0 || cellInfo.getRowspan() > 0) {
                    CellRangeAddress cra = new CellRangeAddress(startNum,
                            startNum + cellInfo.getRowspan(), j, j + cellInfo.getColspan());
                    sheet.addMergedRegion(cra);
                }
            }
            startNum++;
        }
    }

    // 自动调整列宽
    private static void autoSheet(HSSFSheet sheet) {
        if (sheet == null) {
            return;
        }
        int rownum = sheet.getFirstRowNum();
        for (int i = sheet.getLastRowNum(); i >= rownum; i--) {
            HSSFRow row = sheet.getRow(i);
            // 宽度自适应
            int width = row.getPhysicalNumberOfCells();
            for (int j = 0; j < width; j++) {
                sheet.autoSizeColumn(j);
            }
        }
    }

    // 内容
    private static void mkSheetInfo(CellStyle style, HSSFSheet sheet, String[][] result,
            int fieldsize) {
        int row_length = result.length + 1;
        for (int i = 1; i < row_length; i++) {
            HSSFRow row = sheet.createRow(i);
            for (int j = 0; j < fieldsize; j++) {
                HSSFCell cell = row.createCell(j);
                cell.setCellValue(result[i - 1][j]);
                cell.setCellStyle(style);
            }
        }
    }

    // 第一行列名样式
    private static CellStyle getFieldNameStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        style.setWrapText(true);
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        HSSFFont font = wb.createFont();
        font.setFontName("微软雅黑");
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);// 设置字体大小
        style.setFont(font);
        return style;
    }

    // 内容样式
    private static CellStyle getSheetInfoStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        style.setWrapText(true);
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        HSSFFont font = wb.createFont();
        font.setFontName("微软雅黑");
        font.setBold(false);
        font.setFontHeightInPoints((short) 12);// 设置字体大小
        style.setFont(font);
        return style;
    }
}
