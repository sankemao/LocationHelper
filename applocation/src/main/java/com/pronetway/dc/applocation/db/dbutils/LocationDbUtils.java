package com.pronetway.dc.applocation.db.dbutils;

import android.content.Context;

import com.blankj.utilcode.util.Utils;
import com.pronetway.dc.applocation.bean.LocationInfo;
import com.pronetway.dc.applocation.db.gen.DaoSession;
import com.pronetway.dc.applocation.db.gen.LocationInfoDao;

import java.util.List;

/**
 * Description:TODO
 * Create Time: 2017/12/25.15:08
 * Author:jin
 * Email:210980059@qq.com
 */
public class LocationDbUtils {

    private DaoSession mDaoSession;
    private LocationInfoDao mLocationInfoDao;
    //单例
    private static LocationDbUtils mLocationDbUtils = new LocationDbUtils(Utils.getApp());

    private LocationDbUtils(Context cxt) {
        init(cxt.getApplicationContext());
    }

    public static LocationDbUtils getInstance() {
        return mLocationDbUtils;
    }

    /**
     * 初始化
     */
    private void init(Context cxt) {
        mDaoSession = GreenDaoHelper.getDaoSession(cxt);
        //能够持久访问和查询实体类.
        //比起DaoSession有更多的持久化方法 count, loadAll, insertInt等等
        mLocationInfoDao = mDaoSession.getLocationInfoDao();
    }


    /**
     * 添加一条记录
     */
    public void insertLocation(LocationInfo info) {
        mLocationInfoDao.insert(info);
    }

    /**
     * 删除一条记录
     */
    public void deleteLocation(LocationInfo info) {
        mLocationInfoDao.delete(info);
    }

    /**
     * 修改一条记录
     */
    public void updateLocation(LocationInfo info) {
        mLocationInfoDao.update(info);
    }

    /**
     * 查询所有记录
     */
    public List<LocationInfo> queryAllLocations() {
        return mLocationInfoDao.loadAll();
    }

    /**
     * 分页查询
     * 按id倒序
     */
    public List<LocationInfo> queryLocations(int offset, int limit){
        List<LocationInfo> Locations = mLocationInfoDao.queryBuilder()
                .orderDesc(LocationInfoDao.Properties.Id)
                .offset(offset)
                .limit(limit)
                .list();
        return Locations;
    }

    /**
     * 清空数据库
     * 先删除表, 再创建
     */
    public void wipeData() {
        LocationInfoDao.dropTable(GreenDaoHelper.getDatabase(Utils.getApp()), true);
        LocationInfoDao.createTable(GreenDaoHelper.getDatabase(Utils.getApp()), true);
    }
}
