package com.pronetway.locationhelper.ui;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.pronetway.locationhelper.R;
import com.pronetway.locationhelper.app.Constant;
import com.pronetway.locationhelper.bean.LocationInfo;
import com.pronetway.locationhelper.db.dbutils.LocationDbUtils;
import com.pronetway.locationhelper.utils.MacTextWatcher;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import sankemao.baselib.mvp.BaseActivity;
import sankemao.baselib.mvp.PresenterManager;
import sankemao.baselib.recyclerview.JViewHolder;
import sankemao.baselib.recyclerview.JrecyAdapter;
import sankemao.baselib.recyclerview.LoadRefreshRecyclerView;
import sankemao.baselib.recyclerview.RefreshRecyclerView;
import sankemao.baselib.recyclerview.decoration.MyItemDecoration;
import sankemao.baselib.recyclerview.headfootview.DefaultLoadMoreCreator;
import sankemao.baselib.recyclerview.headfootview.DefaultRefreshCreator;
import sankemao.baselib.ui.dialog.AlertDialog;
import sankemao.baselib.ui.navigation.DefaultNavigationBar;


public class LocalHistoryActivity extends BaseActivity {

    @BindView(R.id.rv_locations)
    LoadRefreshRecyclerView mRvLocations;

    private JrecyAdapter<LocationInfo> mLocationAdapter;

    private int offset = 0;
    private AlertDialog mSelectDialog;

    private View mCurrentSelectItem;
    private AlertDialog mEditDialog;
    private AlertDialog mConfirmDialog;

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
        new DefaultNavigationBar.Builder(this, R.layout.navigationbar_common, null)
                .setOnClickListener(R.id.iv_back, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                })
                .setText(R.id.tv_title, "位置记录")
                .build();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mRvLocations.setLayoutManager(new LinearLayoutManager(this));
        mLocationAdapter = new JrecyAdapter<LocationInfo>(this, null, R.layout.rv_item_location) {

            @Override
            protected void convert(JViewHolder holder, final LocationInfo info, final int position) {
                holder.setText(R.id.tv_mac, String.format(getString(R.string.device_mac), info.getMac()))
                        .setText(R.id.tv_place, String.format(getString(R.string.place_name), info.getPlace()))
                        .setText(R.id.tv_address, String.format(getString(R.string.address), info.getAddress()))
                        .setText(R.id.tv_longitude, String.format(getString(R.string.longitude), info.getLongitude()))
                        .setText(R.id.tv_latitude, String.format(getString(R.string.latitude), info.getLatitude()))
                        .setText(R.id.tv_remark, String.format(getString(R.string.remark), info.getRemark()))
                        .setText(R.id.tv_time, String.format(getString(R.string.time), info.getTime()))
                        .setText(R.id.tv_position, String.valueOf(position))
                        .setOnItemClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                v.setSelected(true);
                                mCurrentSelectItem = v;
                                showSelectDialog(position, info);
                            }
                        });
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

        mRvLocations.addItemDecoration(new MyItemDecoration(1, Color.parseColor("#999999")));
    }

    /**
     * 操作选择按钮
     */
    private void showSelectDialog(final int position, final LocationInfo info) {
        mSelectDialog = new AlertDialog.Builder(this)
                .setContentView(R.layout.dialog_select)
                .setWidthAndHeight(ConvertUtils.dp2px(300), -2)
                .setOnClickListener(R.id.tv_del, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showConfirmDialog(position, info);
                        dissmissSelectDialog();
                    }
                })
                .setOnClickListener(R.id.tv_edit, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showEditDialog(info);
                        dissmissSelectDialog();
                    }
                })
                .setOnClickListener(R.id.tv_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dissmissSelectDialog();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (mCurrentSelectItem != null) {
                            mCurrentSelectItem.setSelected(false);
                        }
                    }
                })
                .setOnClickListener(R.id.tv_look_photo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //查看照片
                        String photoName = info.getTime() + ".jpg";
                        File file = new File(Constant.Path.PHOTO_PATH, photoName);
                        if (!file.exists()) {
                            ToastUtils.showShort("未找到相关照片");
                            return;
                        }

                        BigImageActivity.go(LocalHistoryActivity.this, photoName);
                    }
                })
                .show();
    }

    /**
     * 删除确认弹窗
     */
    private void showConfirmDialog(final int position, final LocationInfo info) {
        mConfirmDialog = new AlertDialog.Builder(this)
                .setContentView(R.layout.dialog_confirm)
                .setWidthAndHeight(ConvertUtils.dp2px(300), -2)
                .setText(R.id.tv_content, "设备mac为:\r\n" + info.getMac() + "\r\n确定删除这条记录?")
                .setOnClickListener(R.id.tv_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mConfirmDialog.dismiss();
                    }
                })
                .setOnClickListener(R.id.tv_del, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LocationDbUtils.getInstance().deleteLocation(info);
                        mLocationAdapter.removeItem(position);
                        ToastUtils.showShort("删除成功");
                        mConfirmDialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * 修改数据
     */
    private void showEditDialog(final LocationInfo info) {
        mEditDialog = new AlertDialog.Builder(this)
                .setContentView(R.layout.dialog_save)
                .setWidthAndHeight(ConvertUtils.dp2px(300), -2)
                .setOnClickListener(R.id.tv_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mEditDialog.dismiss();
                    }
                })
                .setOnClickListener(R.id.tv_save, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mac = getDialogInputString(R.id.et_mac);
                        if (TextUtils.isEmpty(mac)) {
                            ToastUtils.showShort("请输入mac");
                            return;
                        } else if (mac.length() < 17) {
                            ToastUtils.showShort("mac格式不正确");
                            return;
                        }
                        String place = getDialogInputString(R.id.et_place);
                        String remark = getDialogInputString(R.id.et_remark);
                        if (TextUtils.isEmpty(place)) {
                            ToastUtils.showShort("请输入场所名称");
                            return;
                        }
                        String address = getDialogInputString(R.id.et_address);
                        info.setMac(mac);
                        info.setPlace(place);
                        info.setAddress(address);
                        info.setRemark(remark);
                        //保存到db.
                        LocationDbUtils.getInstance().updateLocation(info);
                        //列表数据更新
                        mLocationAdapter.notifyDataSetChanged();
                        mEditDialog.dismiss();
                    }
                })
                .show();
        EditText etMac = mEditDialog.getView(R.id.et_mac);
        etMac.setText(info.getMac());
        etMac.addTextChangedListener(new MacTextWatcher(etMac));

        ((EditText) mEditDialog.getView(R.id.et_place)).setText(info.getPlace());
        ((EditText) mEditDialog.getView(R.id.et_address)).setText(info.getAddress());
        ((EditText) mEditDialog.getView(R.id.et_remark)).setText(info.getRemark());
    }

    /**
     * 获取dialog中的输入
     */
    private String getDialogInputString(int etId) {
        EditText et = mEditDialog.getView(etId);
        return et.getText().toString().trim();
    }

    /**
     * 取消列表条目选择状态
     * 弹框消失
     */
    private void dissmissSelectDialog() {
        mSelectDialog.dismiss();
        if (mCurrentSelectItem != null) {
            mCurrentSelectItem.setSelected(false);
        }
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
//
//    @OnClick(R.id.btn_test)
//    public void onViewClicked() {
//        LocationInfo locationInfo = new LocationInfo("11:11:11:11:11:11", "abc", "上海新网程测试", "121.222222", "21.222222", "备注测试", "12.25-18:10");
//        //保存到db.
//        LocationDbUtils.getInstance().insertLocation(locationInfo);
//        mLocationAdapter.notifyDataSetChanged();
//    }
}
