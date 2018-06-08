package com.pronetway.dc.applocation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.location.AMapLocation;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.pronetway.dc.applocation.R;
import com.pronetway.dc.applocation.app.Constant;
import com.pronetway.dc.applocation.db.dbutils.LocationDbUtils;
import com.pronetway.dc.applocation.utils.AMLocationUtil;
import com.pronetway.dc.applocation.utils.CommonUtils;

import sankemao.baselib.mvp.BaseActivity;
import sankemao.baselib.mvp.PresenterManager;
import sankemao.baselib.ui.dialog.AlertDialog;
import sankemao.baselib.utils.xpermission.Permissions;
import sankemao.baselib.utils.xpermission.PermissionListener;
import sankemao.baselib.utils.xpermission.XPermission;

import static com.pronetway.dc.applocation.utils.AMUtils.convertToLatLonPoint;
import static com.pronetway.dc.applocation.utils.GeneralUtils.TIME_FORMAT;

/**
 * Description:TODO
 * Create Time: 2017/12/18.11:10
 * Author:jin
 * Email:210980059@qq.com
 */
public class HomeActivity extends BaseActivity {

    MapView mMapView;
    ImageView mIvLocation;
    ImageView mIvSave;
    TextView mTvLatLng;
    TextView mTvAddress;

    private AMap aMap;

    private GeocodeSearch geocoderSearch;
    private String currentAddress;
    private String currentLongitude;
    private String currentLatitude;
    private long exitTime = 0;

    /**
     * 地图拖动监听回调
     */
    private AMap.OnCameraChangeListener myCameraChangeListener = new AMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            LogUtils.d("拖动地图");
        }

        @Override
        public void onCameraChangeFinish(CameraPosition cameraPosition) {
            LatLng finalChoosePosition = cameraPosition.target;
            currentLatitude = String.valueOf(finalChoosePosition.latitude);
            currentLongitude = String.valueOf(finalChoosePosition.longitude);
            mTvLatLng.setText("纬度:" + currentLatitude + ", 经度:" + currentLongitude);
            getAddress(finalChoosePosition);
        }
    };

    /**
     * 逆地理编码查询回调
     */
    private GeocodeSearch.OnGeocodeSearchListener mGeocodeSearchListener = new GeocodeSearch.OnGeocodeSearchListener() {
        @Override
        public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
            if (rCode == 1000) {
                if (result != null && result.getRegeocodeAddress() != null
                        && result.getRegeocodeAddress().getFormatAddress() != null) {
                    currentAddress = result.getRegeocodeAddress().getFormatAddress(); // 逆转地里编码不是每次都可以得到对应地图上的opi
                    mTvAddress.setText("地址:" + currentAddress);
                } else {
                    ToastUtils.showShort("未查询到地址");
                }
            } else if (rCode == 27) {
                ToastUtils.showShort("网络错误");
            } else if (rCode == 32) {
                ToastUtils.showShort("key错误");
            } else {
                ToastUtils.showShort("未知错误, 错误代码:" + rCode);
            }
        }

        @Override
        public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

        }
    };

    private AlertDialog mSaveDialog;
    private EditText mEtMac;
    private AlertDialog mShareDialog;
    private AlertDialog mConfirmDialog;

    @Override
    public PresenterManager attachPresenters() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initNavigationBar() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mMapView = getViewById(R.id.map);
        mIvLocation = getViewById(R.id.iv_location);
        mIvSave = getViewById(R.id.iv_save);
        mTvLatLng = getViewById(R.id.tv_latlng);
        mTvAddress = getViewById(R.id.tv_address);

        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        uiSetting();

        locationNow();

        aMap.setOnCameraChangeListener(myCameraChangeListener);
    }

    /**
     * 高德地图界面
     */
    private void uiSetting() {
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
    }

    /**
     * 立即进行定位并将地图移动到定位点
     */
    private void locationNow() {
        AMLocationUtil.getCurrentLocation(new AMLocationUtil.MyLocationListener() {
            @Override
            public void result(AMapLocation location) {
                //设置经纬度
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                currentLatitude = String.valueOf(latitude);
                currentLongitude = String.valueOf(longitude);
                mTvLatLng.setText("纬度:" + latitude + ", 经度:" + longitude);
                //设置地址
                currentAddress = location.getAddress();
                mTvAddress.setText("地址:" + currentAddress);
                //map镜头移动到定位处
                setMapCenter(latitude, longitude);
            }
        });
    }

    /**
     * 根据经纬度获取具体地址
     */
    private void getAddress(LatLng latLng) {
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(convertToLatLonPoint(latLng), 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    /**
     * 移动地图到定位点
     */
    private void setMapCenter(double latitude, double longitude) {
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 18));
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        /**
         * 6.0权限适配
         */
        new XPermission(this)
                .permissions(Permissions.LOCATION)
                .permissions(Permissions.STORAGE)
                .request(new PermissionListener() {
                    @Override
                    public void onSucceed() {
                        locationNow();
                    }
                });

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(mGeocodeSearchListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
        AMLocationUtil.destory();
    }

    public void onIvLocationClick(View view) {
        ARouter.getInstance().build("/scan/home").navigation();
//        locationNow();
    }

    public void onIvSaveClick(View view) {
        String time = TimeUtils.millis2String(System.currentTimeMillis(), TIME_FORMAT);
        InputLocationInfoActivity.go(this, currentAddress, currentLatitude, currentLongitude, time);
    }

    public void onIvShareClick(View view) {
        mShareDialog = new AlertDialog.Builder(this)
                .setContentView(R.layout.dialog_bottom_share)
                .fromBottom(true)
                .fullWidth()
                .setOnClickListener(R.id.share_choose, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //分享excel.
                        CommonUtils.shareExcel(HomeActivity.this, Constant.Path.EXCEL_NAME);
                        mShareDialog.dismiss();
                    }
                })
                .setOnClickListener(R.id.open_wps, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //打开excel.
                        CommonUtils.openExcel(HomeActivity.this, Constant.Path.EXCEL_NAME);
                        mShareDialog.dismiss();
                    }
                })
                .setOnClickListener(R.id.del, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //确认删除
                        showConfirmDialog();
                        mShareDialog.dismiss();
                    }
                })
                .setOnClickListener(R.id.look_over, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //查看本地记录
                        startActivity(new Intent(HomeActivity.this, LocalHistoryActivity.class));
                        mShareDialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * 确定删除弹窗
     */
    private void showConfirmDialog() {
        mConfirmDialog = new AlertDialog.Builder(this)
                .setContentView(R.layout.dialog_confirm)
                .setText(R.id.tv_content, "该操作将删除excel和本地数据库!")
                .setWidthAndHeight(ConvertUtils.dp2px(260), -2)
                .addDefaultAnimation()
                .setOnClickListener(R.id.tv_left, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mConfirmDialog.dismiss();
                    }
                })
                .setOnClickListener(R.id.tv_right, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtils.delExcel(Constant.Path.EXCEL_NAME);
                        LocationDbUtils.getInstance().wipeData();
                        mConfirmDialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * 按两次返回
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtils.showShort("再按一次退出应用");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
