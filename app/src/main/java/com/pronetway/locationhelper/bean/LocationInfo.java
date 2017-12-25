package com.pronetway.locationhelper.bean;

import android.support.annotation.Nullable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Description:TODO
 * Create Time: 2017/12/19.10:23
 * Author:jin
 * Email:210980059@qq.com
 */
@Entity
public class LocationInfo {

    public LocationInfo(String mac, String place, String address, String latitude, String longitude, @Nullable String remark, String time) {
        this.mac = mac;
        this.place = place;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.remark = remark;
        this.time = time;
    }

    @Generated(hash = 1533880664)
    public LocationInfo(Long id, String mac, String place, String address, String longitude, String latitude, String remark, String time) {
        this.id = id;
        this.mac = mac;
        this.place = place;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.remark = remark;
        this.time = time;
    }

    @Generated(hash = 1054559726)
    public LocationInfo() {
    }

    @Id(autoincrement = true)
    private Long id;    //id, 数据库主键.

    //设备mac
    public String mac;

    //场所名称
    public String place;

    //地址
    public String address;

    //经度
    public String longitude;

    //纬度
    public String latitude;

    //备注
    public String remark;

    //加入时间
    public String time;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id + "-" + this.mac + "-" + this.place;
    }
}
