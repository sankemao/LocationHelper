package com.pronetway.locationhelper.utils;

import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;

/**
 * Description:TODO
 * Create Time: 2017/12/18.17:37
 * Author:jin
 * Email:210980059@qq.com
 */
public class AMUtils {
    /**
     * 把LatLng对象转化为LatLonPoint对象
     */
    public static LatLonPoint convertToLatLonPoint(LatLng latlng) {
        return new LatLonPoint(latlng.latitude, latlng.longitude);
    }
}
