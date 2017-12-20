package com.pronetway.locationhelper.bean;

/**
 * Description:TODO
 * Create Time: 2017/12/19.10:23
 * Author:jin
 * Email:210980059@qq.com
 */
public class LocationInfo {

    public LocationInfo(String mac, String place, String address, String latitude, String longitude, String time) {
        this.mac = mac;
        this.place = place;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public String mac;

    public String place;

    public String address;

    public String longitude;

    public String latitude;

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
}
