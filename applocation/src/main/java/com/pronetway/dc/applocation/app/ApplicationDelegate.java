package com.pronetway.dc.applocation.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.pronetway.dc.applocation.utils.AMLocationUtil;

import java.security.MessageDigest;
import java.util.List;
import java.util.Locale;

import sankemao.baselib.base.IApplicationDelegate;
import sankemao.baselib.imageload.ImageLoaderOptions;

/**
 * Description:TODO
 * Create Time: 2018/6/6.16:36
 * Author:jin
 * Email:210980059@qq.com
 */
public class ApplicationDelegate implements IApplicationDelegate {
    @Override
    public void onCreate() {

        AMLocationUtil.init(Utils.getApp());

        //设置图片加载策略，跳过缓存
        ImageLoaderOptions.getDefault()
                .skipMemoryCache(true)
                .setDiskStrategy(ImageLoaderOptions.DiskStrategy.NONE);

        LogUtils.e("sha1为：" + sHA1(Utils.getApp()) + "，packagename为：" + getAppProcessName(Utils.getApp())
         + "，applicationid为：" + getApplicationId(Utils.getApp()));
    }

    @Override
    public void onTerminate() {

    }

    @Override
    public void onLowMemory() {

    }

    @Override
    public void onTrimMemory(int level) {

    }

    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length()-1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAppProcessName(Context context) {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }

    public static String getApplicationId(Context context) {
        return context.getApplicationInfo().packageName;
    }
}
