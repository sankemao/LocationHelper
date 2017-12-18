package com.pronetway.loactionhelper.app;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import com.pronetway.loactionhelper.utils.AMLocationUtil;

/**
 * Description:TODO
 * Create Time: 2017/12/18.13:38
 * Author:jin
 * Email:210980059@qq.com
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        AMLocationUtil.init(this);
    }

}
