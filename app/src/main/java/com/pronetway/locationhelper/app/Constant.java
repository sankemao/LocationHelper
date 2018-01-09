package com.pronetway.locationhelper.app;

import android.os.Environment;

import java.io.File;

/**
 * Description:TODO
 * Create Time: 2017/12/19.9:01
 * Author:jin
 * Email:210980059@qq.com
 */
public class Constant {
    public static final class Path {
        public static final String APP_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator +
                MyApplication.cxt.getPackageName()/*+ File.separator + "位置信息"*/;
        public static final String EXCEL_NAME = "位置记录.xls";
        public static final String PHOTO_PATH = APP_PATH + File.separator + "照片";
        public static final String CACHE = APP_PATH + File.separator + "缓存";
    }

    public static final class Permission {
        public static final int APP_PERSSION = 0x0011;
        public static final int CAMERA_PERSSION = 0x0012;
    }

    public static final class Db{
        public static final String DB_NAME = "location.db";
        public static final int DB_LIMIT = 20;
    }

}
