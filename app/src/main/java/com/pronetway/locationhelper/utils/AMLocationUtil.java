package com.pronetway.locationhelper.utils;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.blankj.utilcode.util.LogUtils;

/**
 *
 * @author jin
 * @date 2017/2/19
 */
public class AMLocationUtil {

    private static AMapLocationClient sLocationClient;
    private static AMapLocationClientOption sLocationOption;
    private static AMapLocation sAMapLocation;

    public static void init(Context context) {
        sLocationClient = new AMapLocationClient(context);
        sLocationOption = new AMapLocationClientOption();
        sLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //定位间隔
        sLocationOption.setInterval(5 * 1000);
        sLocationClient.setLocationOption(sLocationOption);
    }

    public interface MyLocationListener {
        void result(AMapLocation location);
    }

    /**
     * 在调用该方法之前, 必须先调用init()初始化.
     * 如果之前已定位过, 直接获取之前定位的结果, 否则进行一次定位.
     *
     * @param listener
     */
    public static void getLocation(MyLocationListener listener) {
        if (sAMapLocation == null) {
            getCurrentLocation(listener);
        } else {
            listener.result(sAMapLocation);
        }
    }

    /**
     * 立即进行一次定位
     *
     * @param listener
     */
    public static void getCurrentLocation(final MyLocationListener listener) {
        if (sLocationClient == null) {
            return;
        }
        sLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        //定位成功.则停止定位.
                        sLocationClient.stopLocation();
                        sAMapLocation = aMapLocation;
                        listener.result(aMapLocation);
                    } else {
                        LogUtils.d("定位失败, 错误代码为: " + aMapLocation.getErrorCode());
                    }
                }
            }
        });

        sLocationClient.startLocation();
    }


    /**
     * 销毁定位
     */
    public static void destory() {
        if (sLocationClient != null) {
            sLocationClient.onDestroy();
        }
        sLocationClient = null;
        sLocationOption = null;
    }
}
