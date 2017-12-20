package com.pronetway.locationhelper.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.pronetway.locationhelper.app.Constant;

import java.io.File;

/**
 * Description:TODO
 * Create Time: 2017/12/19.15:46
 * Author:jin
 * Email:210980059@qq.com
 */
public class CommonUtils {
    /**
     * 打开excel文件
     * 适配android 7.0
     */
    public static void openExcel(Context context, String fileName) {
        File excelFile = new File(Constant.Excel.EXCEL_PATH, fileName);
        boolean fileExists = FileUtils.isFileExists(excelFile);
        if (!fileExists) {
            ToastUtils.showShort("未保存记录");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, "com.pronetway.locationhelper.fileprovider", excelFile);
        } else {
            uri = Uri.fromFile(excelFile);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "application/vnd.ms-excel");

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            ToastUtils.showShort("无法打开");
            e.printStackTrace();
        }
    }

    /**
     * 删除excel文件
     * @param fileName
     */
    public static void delExcel(String fileName) {
        File excelFile = new File(Constant.Excel.EXCEL_PATH, fileName);
        boolean fileExists = FileUtils.isFileExists(excelFile);
        if (!fileExists) {
            ToastUtils.showShort("未保存记录");
            return;
        }
        boolean delSucceed = FileUtils.deleteFile(excelFile);
        if (delSucceed) {
            ToastUtils.showShort("删除成功");
        } else {
            ToastUtils.showShort("删除失败");
        }
    }

    public static void shareExcel(Context context, String fileName) {
        File excelFile = new File(Constant.Excel.EXCEL_PATH, fileName);
        boolean fileExists = FileUtils.isFileExists(excelFile);
        if (!fileExists) {
            ToastUtils.showShort("未保存记录");
            return;
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.addCategory("android.intent.category.DEFAULT");
        Uri uri;
        uri = Uri.fromFile(excelFile);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("application/vnd.ms-excel");

        try {
            context.startActivity(Intent.createChooser(intent, excelFile.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
