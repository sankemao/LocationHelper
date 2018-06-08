package com.pronetway.dc.applocation.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.pronetway.dc.applocation.app.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        File excelFile = new File(Constant.Path.APP_PATH, fileName);
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
        File excelFile = new File(Constant.Path.APP_PATH, fileName);
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
        File excelFile = new File(Constant.Path.APP_PATH, fileName);
        boolean fileExists = FileUtils.isFileExists(excelFile);
        if (!fileExists) {
            ToastUtils.showShort("未保存记录");
            return;
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.addCategory("android.intent.category.DEFAULT");
        Uri uri;
//        uri = Uri.fromFile(excelFile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, "com.pronetway.locationhelper.fileprovider", excelFile);
        } else {
            uri = Uri.fromFile(excelFile);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("application/vnd.ms-excel");

        try {
            context.startActivity(Intent.createChooser(intent, excelFile.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void shareImages(Context context, List<File> files) {
        if (files == null || files.size() <= 0) {
            return;
        }

        ArrayList<Uri> uris = new ArrayList<>();

        for (File file : files) {
            Uri uri = Uri.fromFile(file);
            uris.add(uri);
        }

        boolean multiple = uris.size() > 1;
        Intent intent = new Intent();
        intent.setAction(multiple ? Intent.ACTION_SEND_MULTIPLE : Intent.ACTION_SEND);
        if (multiple) {
            intent.setType("*/*");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        } else {
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Share"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
