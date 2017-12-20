package com.pronetway.locationhelper.app;

import android.app.Application;
import android.content.Context;

import com.blankj.utilcode.util.Utils;
import com.pronetway.locationhelper.utils.AMLocationUtil;

/**
 * Description:TODO
 * Create Time: 2017/12/18.13:38
 * Author:jin
 * Email:210980059@qq.com
 */
public class MyApplication extends Application {
    public static Context cxt;

    @Override
    public void onCreate() {
        super.onCreate();
        cxt = this;
        Utils.init(this);
        AMLocationUtil.init(this);
    }

}
