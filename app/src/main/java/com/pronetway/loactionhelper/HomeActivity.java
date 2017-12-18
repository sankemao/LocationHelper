package com.pronetway.loactionhelper;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.blankj.utilcode.util.ToastUtils;
import com.pronetway.loactionhelper.utils.AMLocationUtil;

import butterknife.BindView;
import butterknife.OnClick;
import sankemao.baselib.mvp.BaseActivity;
import sankemao.baselib.mvp.PresenterManager;
import sankemao.baselib.ui.dialog.AlertDialog;

import static com.pronetway.loactionhelper.utils.AMUtils.convertToLatLonPoint;

public class HomeActivity extends BaseActivity {


    @BindView(R.id.map)
    MapView mMapView;
    @BindView(R.id.iv_location)
    ImageView mIvLocation;
    @BindView(R.id.iv_save)
    ImageView mIvSave;
    @BindView(R.id.tv_latlng)
    TextView mTvLatLng;
    @BindView(R.id.tv_address)
    TextView mTvAddress;

    private AMap aMap;

    private GeocodeSearch geocoderSearch;
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
            mTvLatLng.setText("纬度:" + finalChoosePosition.latitude + ", 经度:" + finalChoosePosition.longitude);
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
                    String addressName = result.getRegeocodeAddress().getFormatAddress(); // 逆转地里编码不是每次都可以得到对应地图上的opi
                    mTvAddress.setText("地址:" + addressName);
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

    @Override
    public Context getContext() {
        return this;
    }

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
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                setMapCenter(latitude, longitude);
                mTvLatLng.setText("纬度:" + latitude + ", 经度:" + longitude);
                mTvAddress.setText("地址:" + location.getAddress());
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

    @OnClick({R.id.iv_location, R.id.iv_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_location:
                locationNow();
                break;
            case R.id.iv_save:
                mSaveDialog = new AlertDialog.Builder(this)
                        .setContentView(R.layout.dialog_save)
                        .setWidthAndHeight(ConvertUtils.dp2px(300), -2)
                        .setOnClickListener(R.id.tv_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mSaveDialog.dismiss();
                            }
                        })
                        .setOnClickListener(R.id.tv_save, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //保存数据
                                String mac = getDialogInputString(R.id.et_mac);
                                String place = getDialogInputString(R.id.et_place);
                                ToastUtils.showShort("mac为: " + mac + ", 地址为: " + place);
                                mSaveDialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();

                break;
            default:
                break;
        }
    }

    private String getDialogInputString(int etId) {
        TextView tv = mSaveDialog.getView(etId);
        return tv.getText().toString().trim();
    }
}
