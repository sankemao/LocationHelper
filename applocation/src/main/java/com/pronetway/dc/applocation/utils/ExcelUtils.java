package com.pronetway.dc.applocation.utils;

import android.graphics.Bitmap;

import com.pronetway.dc.applocation.app.Constant;
import com.pronetway.dc.applocation.bean.LocationInfo;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by jin on 2016/12/21.
 *
 */

public class ExcelUtils {
    private String dir = Constant.Path.APP_PATH;

    private static ExcelUtils sExcelUtils = new ExcelUtils();

    private ExcelUtils() {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static ExcelUtils getInstance() {
        return sExcelUtils;
    }

    /**
     * 写入单条location信息
     */
    public void writeLocationInfo(LocationInfo info, String excelName) {
        File excelFile = new File(dir, excelName);
        createExcelIfNotExists(excelFile);
        if (info == null) {
            return;
        }
        try {
            FileInputStream fis = new FileInputStream(excelFile);
            POIFSFileSystem PS = new POIFSFileSystem(fis);
            HSSFWorkbook wb = new HSSFWorkbook(PS);
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow row;

            //1. 先获取最后一行.
            int lastRow = sheet.getLastRowNum();

            FileOutputStream fos = new FileOutputStream(excelFile);

            row = sheet.createRow(lastRow + 1);
            row.createCell(0).setCellValue(info.getMac());
            row.createCell(1).setCellValue(info.getPlace());
            row.createCell(2).setCellValue(info.getAddress());
            row.createCell(3).setCellValue(info.getLatitude());
            row.createCell(4).setCellValue(info.getLongitude());
            row.createCell(5).setCellValue(info.getRemark());
            row.createCell(6).setCellValue(info.getTime());

            fos.flush();
            wb.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量写入地址信息集合
     */
    public void writeLocationInfos(List<LocationInfo> infos, String excelName) {
        File excelFile = new File(dir, excelName + ".xls");
        createExcelIfNotExists(excelFile);
        if (infos.size() == 0) {
            return;
        }

        try {
            FileInputStream fis = new FileInputStream(excelFile);
            POIFSFileSystem PS = new POIFSFileSystem(fis);
            HSSFWorkbook wb = new HSSFWorkbook(PS);
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow row;

            //1. 先获取最后一行.
            int lastRow = sheet.getLastRowNum();

            FileOutputStream fos = new FileOutputStream(excelFile);
            for (int i = 0; i < infos.size(); i++) {
                lastRow = lastRow + 1;
                row = sheet.createRow(lastRow);
                LocationInfo info = infos.get(i);
                row.createCell(0).setCellValue(info.getMac());
                row.createCell(1).setCellValue(info.getPlace());
                row.createCell(2).setCellValue(info.getAddress());
                row.createCell(3).setCellValue(info.getLatitude());
                row.createCell(4).setCellValue(info.getLongitude());
                row.createCell(5).setCellValue(info.getRemark());
                row.createCell(6).setCellValue(info.getTime());
            }
            fos.flush();
            wb.write(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果该excel不存在, 则创建, 同时初始化sheet以及表头
     * @param excelFile excel文件
     */
    private void createExcelIfNotExists(File excelFile) {
        if (!excelFile.exists()) {
            //生成目录.
            new File(Constant.Path.APP_PATH).mkdirs();
            FileOutputStream mOs = null;
            try {
                mOs = new FileOutputStream(excelFile);
                HSSFWorkbook workbook = new HSSFWorkbook();
                HSSFSheet sheet = workbook.createSheet("sheet1");
                sheet.setColumnWidth(0, 20 * 200);
                sheet.setColumnWidth(1, 20 * 200);
                sheet.setColumnWidth(2, 20 * 300);
                sheet.setColumnWidth(3, 20 * 140);
                sheet.setColumnWidth(4, 20 * 140);
                sheet.setColumnWidth(5, 20 * 200);
                sheet.setColumnWidth(6, 20 * 180);
//                HSSFCellStyle style = getStyle(workbook);
                HSSFRow row = sheet.createRow(0);
                row.createCell(0).setCellValue("MAC");
                row.createCell(1).setCellValue("场所名称");
                row.createCell(2).setCellValue("场所地址");
                row.createCell(3).setCellValue("纬度");
                row.createCell(4).setCellValue("经度");
                row.createCell(5).setCellValue("备注");
                row.createCell(6).setCellValue("记录时间");
                workbook.write(mOs);
                mOs.flush();
                mOs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写入图片到excel
     */
    private void writeImage(Bitmap bitmap) {
        FileOutputStream fileOut = null;
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("sheet1");
        HSSFRow row = sheet.createRow(0);

        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();

        HSSFCell cell = row.createCell(0);

//        this.getClass().getResource();
    }

    /**
     * excel格式设置
     */
    public HSSFCellStyle getStyle(HSSFWorkbook workbook) {
        //设置样式;
        HSSFCellStyle style = workbook.createCellStyle();
        //设置底边框;
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        //设置底边框颜色;
        style.setBottomBorderColor(HSSFColor.BLUE_GREY.index);
        //设置左边框;
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        //设置左边框颜色;
        style.setLeftBorderColor(HSSFColor.BLUE_GREY.index);
        //设置右边框;
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        //设置右边框颜色;
        style.setRightBorderColor(HSSFColor.BLUE_GREY.index);
        //设置顶边框;
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        //设置顶边框颜色;
        style.setTopBorderColor(HSSFColor.BLUE_GREY.index);
        //设置自动换行;
        style.setWrapText(false);
        //设置水平对齐的样式为居中对齐;
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        // 设置单元格字体
        HSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        style.setFont(font);
        return style;
    }
}