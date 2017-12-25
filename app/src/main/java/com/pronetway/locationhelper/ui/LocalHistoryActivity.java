package com.pronetway.locationhelper.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.pronetway.locationhelper.R;
import com.pronetway.locationhelper.app.Constant;
import com.pronetway.locationhelper.bean.LocationInfo;
import com.pronetway.locationhelper.db.dbutils.LocationDbUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import sankemao.baselib.mvp.BaseActivity;
import sankemao.baselib.mvp.PresenterManager;
import sankemao.baselib.recyclerview.JViewHolder;
import sankemao.baselib.recyclerview.JrecyAdapter;
import sankemao.baselib.recyclerview.LoadRefreshRecyclerView;
import sankemao.baselib.recyclerview.RefreshRecyclerView;
import sankemao.baselib.recyclerview.decoration.MyItemDecoration;
import sankemao.baselib.recyclerview.headfootview.DefaultLoadMoreCreator;
import sankemao.baselib.recyclerview.headfootview.DefaultRefreshCreator;

public class LocalHistoryActivity extends BaseActivity {

    @BindView(R.id.rv_locations)
    LoadRefreshRecyclerView mRvLocations;

    private JrecyAdapter<LocationInfo> mLocationAdapter;

    private int offset = 0;

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
        return R.layout.activity_local_history;
    }

    @Override
    protected void initNavigationBar() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mRvLocations.setLayoutManager(new LinearLayoutManager(this));
        mLocationAdapter = new JrecyAdapter<LocationInfo>(this, null, R.layout.item_rv_location) {
            @Override
            protected void convert(JViewHolder holder, LocationInfo info, int position) {
                holder.setText(R.id.tv_mac, String.format(getString(R.string.device_mac), info.getMac()))
                        .setText(R.id.tv_place, String.format(getString(R.string.place_name), info.getPlace()))
                        .setText(R.id.tv_address, String.format(getString(R.string.address), info.getAddress()))
                        .setText(R.id.tv_longitude, String.format(getString(R.string.longitude), info.getLongitude()))
                        .setText(R.id.tv_latitude, String.format(getString(R.string.latitude), info.getLatitude()))
                        .setText(R.id.tv_remark, String.format(getString(R.string.remark), info.getRemark()))
                        .setText(R.id.tv_time, String.format(getString(R.string.time), info.getTime()))
                        .setText(R.id.tv_position, String.valueOf(position));
            }
        };
        mRvLocations.setAdapter(mLocationAdapter);
        mRvLocations.addLoadViewCreator(new DefaultLoadMoreCreator());
        mRvLocations.addRefreshViewCreator(new DefaultRefreshCreator());

        //加载更多
        mRvLocations.setOnLoadMoreListener(new LoadRefreshRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoad() {
                query();
            }
        });
        //刷新
        mRvLocations.setOnRefreshListener(new RefreshRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                offset = 0;
                query();
            }
        });

        mRvLocations.addItemDecoration(new MyItemDecoration(ConvertUtils.dp2px(1), Color.parseColor("#3c3c3c")));
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        query();
    }

    private void query() {
        List<LocationInfo> infos = LocationDbUtils.getInstance().queryLocations(offset, Constant.Db.DB_LIMIT);
        if (infos.isEmpty()) {
            ToastUtils.showShort("没有数据");
        }

        if (offset == 0) {
            mLocationAdapter.refreshAllData(infos);
        } else {
            mLocationAdapter.addAllData(infos);
        }

        mRvLocations.stopRefreshLoad(Constant.Db.DB_LIMIT);
        offset = offset + Constant.Db.DB_LIMIT;
    }

    @OnClick(R.id.btn_test)
    public void onViewClicked() {
        LocationInfo locationInfo = new LocationInfo("11:11:11:11:11:11", "abc", "上海新网程测试", "121.222222", "21.222222", "备注测试", "12.25-18:10");
        //保存到db.
        LocationDbUtils.getInstance().insertLocation(locationInfo);
        mLocationAdapter.notifyDataSetChanged();
    }
}
