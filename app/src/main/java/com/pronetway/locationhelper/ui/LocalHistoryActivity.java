package com.pronetway.locationhelper.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.pronetway.locationhelper.R;
import com.pronetway.locationhelper.app.Constant;
import com.pronetway.locationhelper.bean.LocationInfo;
import com.pronetway.locationhelper.db.dbutils.LocationDbUtils;
import com.pronetway.locationhelper.utils.CommonUtils;
import com.pronetway.locationhelper.utils.MacTextWatcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import sankemao.baselib.mvp.BaseActivity;
import sankemao.baselib.mvp.PresenterManager;
import sankemao.baselib.recyclerview.LoadRefreshRecyclerView;
import sankemao.baselib.recyclerview.RefreshRecyclerView;
import sankemao.baselib.recyclerview.decoration.MyItemDecoration;
import sankemao.baselib.recyclerview.headfootview.DefaultLoadMoreCreator;
import sankemao.baselib.recyclerview.headfootview.DefaultRefreshCreator;
import sankemao.baselib.ui.dialog.AlertDialog;
import sankemao.baselib.ui.navigation.AbsNavigationBar;
import sankemao.baselib.ui.navigation.DefaultNavigationBar;


public class LocalHistoryActivity extends BaseActivity {

    @BindView(R.id.rv_locations)
    LoadRefreshRecyclerView mRvLocations;

    private final int EDIT_REQUEST_CODE = 1001;
    private int offset = 0;
    private AlertDialog mSelectDialog;

    private View mCurrentSelectItem;
    private AlertDialog mEditDialog;
    private AlertDialog mConfirmDialog;
    private LocationHistoryAdapter mLocationHistoryAdapter;
    private TextView mTvCancel;
    private TextView mTvMultiSelect;
    private TextView mTvUp;
    private int mCurrentModifiedPosition;

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
        AbsNavigationBar navigationBar = new DefaultNavigationBar.Builder(this, R.layout.navigationbar_common, null)
                .setOnClickListener(R.id.iv_back, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                })
                .setText(R.id.tv_title, "位置记录")
                .setText(R.id.tv_multi_select, "多选")
                .setText(R.id.tv_left, "取消")
                .setText(R.id.tv_up, "上传")
                .setOnClickListener(R.id.tv_multi_select, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLocationHistoryAdapter.enableMultiSelect();
                        mTvCancel.setVisibility(View.VISIBLE);
                        mTvUp.setVisibility(View.VISIBLE);
                    }
                })
                .setOnClickListener(R.id.tv_left, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLocationHistoryAdapter.cancelMutiSelect();
                        mTvCancel.setVisibility(View.GONE);
                        mTvUp.setVisibility(View.GONE);
                    }
                })
                .setOnClickListener(R.id.tv_up, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //批量上传图片
                        List<File> fileList = mLocationHistoryAdapter.getFileList();
                        if (fileList.size() <= 0) {
                            ToastUtils.showShort("请先选择需要分享的图片");
                            return;
                        }
                        CommonUtils.shareImages(LocalHistoryActivity.this, fileList);
                    }
                })
                .build();

        mTvCancel = (TextView) navigationBar.getViewById(R.id.tv_left);
        mTvCancel.setVisibility(View.GONE);
        mTvMultiSelect = (TextView) navigationBar.getViewById(R.id.tv_multi_select);
        mTvMultiSelect.setVisibility(View.VISIBLE);

        mTvUp = (TextView) navigationBar.getViewById(R.id.tv_up);
        mTvUp.setVisibility(View.GONE);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mRvLocations.setLayoutManager(new LinearLayoutManager(this));
        mLocationHistoryAdapter = new LocationHistoryAdapter(this, null, R.layout.rv_item_location);
        mLocationHistoryAdapter.setNormalClick(new LocationHistoryAdapter.NormalClick() {
            @Override
            public void onNormalClick(int position, LocationInfo locationInfo, View currentSelectItemView) {
                currentSelectItemView.setSelected(true);
                mCurrentSelectItem = currentSelectItemView;
                showSelectDialog(position, locationInfo);
            }
        });
        mRvLocations.setAdapter(mLocationHistoryAdapter);
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
                .setWidthAndHeight(ConvertUtils.dp2px(260), -2)
                .setOnClickListener(R.id.tv_right, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showConfirmDialog(position, info);
                        dissmissSelectDialog();
                    }
                })
                .setOnClickListener(R.id.tv_edit, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        showEditDialog(info);
                        InputLocationInfoActivity.go(LocalHistoryActivity.this, info, EDIT_REQUEST_CODE);
                        mCurrentModifiedPosition = position;
                        dissmissSelectDialog();
                    }
                })
                .setOnClickListener(R.id.tv_left, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dissmissSelectDialog();
                    }
                })
                .setOnClickListener(R.id.tv_share_photo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String imagePath = Constant.Path.PHOTO_PATH + File.separator + info.getTime() + ".jpg";
                        boolean fileExists = FileUtils.isFileExists(imagePath);
                        if (!fileExists) {
                            ToastUtils.showShort("没有相关图片");
                        } else {
                            List<File> files = new ArrayList<>();
                            files.add(new File(imagePath));
                            CommonUtils.shareImages(LocalHistoryActivity.this, files);
                        }
                        mSelectDialog.dismiss();
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
                .show();
    }

    /**
     * 删除确认弹窗
     */
    private void showConfirmDialog(final int position, final LocationInfo info) {
        mConfirmDialog = new AlertDialog.Builder(this)
                .setContentView(R.layout.dialog_confirm)
                .setWidthAndHeight(ConvertUtils.dp2px(260), -2)
                .setText(R.id.tv_content, "设备mac为:\r\n" + info.getMac() + "\r\n确定删除这条记录?")
                .setOnClickListener(R.id.tv_left, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mConfirmDialog.dismiss();
                    }
                })
                .setOnClickListener(R.id.tv_right, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LocationDbUtils.getInstance().deleteLocation(info);
                        mLocationHistoryAdapter.removeItem(position);
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
                .setOnClickListener(R.id.tv_left, new View.OnClickListener() {
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
                        mLocationHistoryAdapter.notifyDataSetChanged();
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
            mLocationHistoryAdapter.refreshAllData(infos);
        } else {
            mLocationHistoryAdapter.addAllData(infos);
        }

        mRvLocations.stopRefreshLoad(Constant.Db.DB_LIMIT);
        offset = offset + Constant.Db.DB_LIMIT;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //修改成功后刷新列表
        if (requestCode == EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            ToastUtils.showShort("修改成功");
            LocationInfo locationInfo = data.getParcelableExtra("locationInfo");
            mLocationHistoryAdapter.notifyItemDataChanged(mCurrentModifiedPosition, locationInfo);
        }
    }
}
