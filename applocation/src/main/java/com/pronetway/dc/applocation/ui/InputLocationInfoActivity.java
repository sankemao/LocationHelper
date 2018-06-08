package com.pronetway.dc.applocation.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.pronetway.dc.applocation.R;
import com.pronetway.dc.applocation.app.Constant;
import com.pronetway.dc.applocation.bean.LocationInfo;
import com.pronetway.dc.applocation.db.dbutils.LocationDbUtils;
import com.pronetway.dc.applocation.utils.BitmapUtils;
import com.pronetway.dc.applocation.utils.ExcelUtils;
import com.pronetway.dc.applocation.utils.MacTextWatcher;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.BitmapCallback;

import java.io.File;

import sankemao.baselib.imageload.ImageLoaderManager;
import sankemao.baselib.mvp.BaseActivity;
import sankemao.baselib.mvp.PresenterManager;
import sankemao.baselib.ui.dialog.AlertDialog;
import sankemao.baselib.ui.navigation.DefaultNavigationBar;
import sankemao.baselib.utils.xpermission.Permissions;
import sankemao.baselib.utils.xpermission.PermissionListener;
import sankemao.baselib.utils.xpermission.XPermission;

import static com.pronetway.dc.applocation.app.Constant.Path.PHOTO_PATH;

public class InputLocationInfoActivity extends BaseActivity {
    public static final String ADDRESS = "_address";
    public static final String LATITUDE = "_latitude";
    public static final String LONGITUDE = "_longitude";
    public static final String TIME = "_time";
    public static final String BUNDLE = "_bundle";
    public static final int REQUEST_CODE_CAMERA = 101;

    ImageView mIvPhoto;
    EditText mEtMac;
    EditText mEtPlace;
    EditText mEtAddress;
    EditText mEtRemark;
    ImageView mIvClear;

    private Uri mImageUri;
    //temp图片文件
    private File mImageFile;
    private String mLatitude;
    private String mLongitude;
    private String mTime;
    private String mMac;
    //场所名称
    private String mPlace;
    //最终保存的地址
    private String mRealAddress;
    private Bitmap mCompressedBitmap;
    private AlertDialog mConfirmDialog;
    //需要修改的locationInfo
    private LocationInfo mLocationInfo;

    private String mImagePath;
    private Intent mIntent;

    /**
     * 主页跳转过来，添加locationInfo
     */
    public static void go(Context context, String address, String latitude, String longitude, String time) {
        Intent intent = new Intent(context, InputLocationInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ADDRESS, address);
        bundle.putString(LATITUDE, latitude);
        bundle.putString(LONGITUDE, longitude);
        bundle.putString(TIME, time);
        intent.putExtra(BUNDLE, bundle);
        context.startActivity(intent);
    }

    /**
     * 历史记录页面跳转过来，修改locationInfo。
     */
    public static void go(Activity activity, LocationInfo locationInfo, int requestCode) {
        Intent intent = new Intent(activity, InputLocationInfoActivity.class);
        intent.putExtra("locationInfo", locationInfo);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_input_location_info;
    }

    @Override
    protected void initNavigationBar() {
        new DefaultNavigationBar.Builder(this, R.layout.navigationbar_common, null)
                .setOnClickListener(R.id.iv_back, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        backPressed();
                    }
                })
                .setText(R.id.tv_title, "录入信息")
                .build();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        mIvPhoto = getViewById(R.id.iv_photo);
        mEtMac = getViewById(R.id.et_mac);
        mEtPlace = getViewById(R.id.et_place);
        mEtAddress = getViewById(R.id.et_address);
        mEtRemark = getViewById(R.id.et_remark);
        mIvClear = getViewById(R.id.iv_clear);

        mIntent = getIntent();
        mLocationInfo = mIntent.getParcelableExtra("locationInfo");
        String originalAddress;
        if (mLocationInfo != null) {
            mMac = mLocationInfo.getMac();
            mPlace = mLocationInfo.getPlace();
            originalAddress = mLocationInfo.getAddress();
            String remark = mLocationInfo.getRemark();
            mTime = mLocationInfo.getTime();
            //回显
            mEtMac.setText(mMac);
            mEtPlace.setText(mPlace);
            mEtAddress.setText(originalAddress);
            mEtRemark.setText(remark);
            mImagePath = Constant.Path.PHOTO_PATH + File.separator + mLocationInfo.getTime() + ".jpg";
            if (FileUtils.isFileExists(mImagePath)) {
                ImageLoaderManager.INSTANCE.showImage(mIvPhoto, mImagePath);
                mIvClear.setVisibility(View.VISIBLE);
            }
        } else {
            Bundle bundle = mIntent.getBundleExtra(BUNDLE);
            originalAddress = bundle.getString(ADDRESS);
            mLatitude = bundle.getString(LATITUDE);
            mLongitude = bundle.getString(LONGITUDE);
            mTime = bundle.getString(TIME);
            mImagePath = Constant.Path.PHOTO_PATH + File.separator + mTime + ".jpg";
        }


        mEtMac.addTextChangedListener(new MacTextWatcher(mEtMac));
        //回显地址
        mEtAddress.setText(originalAddress);

    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    public PresenterManager attachPresenters() {
        return null;
    }

    /**
     * 图片右上角删除按钮
     */
    public void onIvClearClick(View view) {
        ImageLoaderManager.INSTANCE.showImage(mIvPhoto, R.drawable.icon_add);
        mCompressedBitmap = null;
        view.setVisibility(View.INVISIBLE);
    }

    public void onIvPhotoClick(View v) {
        if (mIvClear.getVisibility() == View.VISIBLE) {
            //查看大图
//            if (!BitmapUtils.isEmptyBitmap(mCompressedBitmap)) {
//                BigImageActivity.go(this, mImagePath);
//            } else if (FileUtils.isFileExists(mImagePath)) {
//                BigImageActivity.go(this, mImagePath);
//            }

            BigImageActivity.go(this, mImagePath);
        } else {
            new XPermission(this)
                    .permissions(Permissions.CAMERA)
                    .request(new PermissionListener() {
                        @Override
                        public void onSucceed() {
                            takePhotoNow();
                        }
                    });
        }
    }

    private void takePhotoNow() {
        //检测信息，添加水印
        mMac = mEtMac.getText().toString().trim();
        mPlace = mEtPlace.getText().toString().trim();
        mRealAddress = mEtAddress.getText().toString().trim();
        if (!check(mMac, mPlace, mRealAddress)) {
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //需要提供图片的保存路径
        File dir = new File(PHOTO_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        mImageFile = new File(PHOTO_PATH, "temp.jpg");
        //file:///storage/emulated/0/com.pronetway.locationhelper/%E7%85%A7%E7%89%87/temp.jpg
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mImageUri = FileProvider.getUriForFile(this, "com.pronetway.locationhelper.fileprovider", mImageFile);
        } else {
            mImageUri = Uri.fromFile(mImageFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri); // set the image file name
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }


    /**
     * 保存mac信息
     */
    public void onBtnSaveClick(View v) {
        String mac = mEtMac.getText().toString().trim();
        String place = mEtPlace.getText().toString().trim();
        String remark = mEtRemark.getText().toString().trim();
        mRealAddress = mEtAddress.getText().toString().trim();
        if (!check(mac, place, mRealAddress)) {
            return;
        }

        String address = mEtAddress.getText().toString().trim();
        if (mLocationInfo == null) {
            mLocationInfo = new LocationInfo(mac, place, address, mLatitude, mLongitude, remark, mTime);
            //保存到db.
            LocationDbUtils.getInstance().insertLocation(mLocationInfo);
            //保存到excel
            ExcelUtils.getInstance().writeLocationInfo(mLocationInfo, Constant.Path.EXCEL_NAME);
            ToastUtils.showShort("保存成功");
        } else {
            mLocationInfo.setMac(mac);
            mLocationInfo.setPlace(place);
            mLocationInfo.setAddress(mRealAddress);
            mLocationInfo.setRemark(remark);
            LocationDbUtils.getInstance().updateLocation(mLocationInfo);
            //TODO:同步excel
            Intent intent = new Intent();
            intent.putExtra("locationInfo", mLocationInfo);
            setResult(RESULT_OK, intent);
//            ToastUtils.showShort("修改成功");
        }
        finish();
    }

    /**
     * 检测mac以及场所名称的合法性
     */
    public boolean check(String mac, String place, String address) {
        if (TextUtils.isEmpty(mac)) {
            ToastUtils.showShort("请输入mac");
            return false;
        } else if (mac.length() < 17) {
            ToastUtils.showShort("mac格式不正确");
            return false;
        }

        if (TextUtils.isEmpty(place)) {
            ToastUtils.showShort("请输入场所名称");
            return false;
        }

        if (TextUtils.isEmpty(address)) {
            ToastUtils.showShort("请输入地址");
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CAMERA && mImageUri != null) {
            //ndk压缩图片
            Tiny.BitmapCompressOptions options = new Tiny.BitmapCompressOptions();
//            options.width = 720;
//            options.height = 0;
            Tiny.getInstance()
                    .source(mImageFile)
                    .asBitmap()
                    .withOptions(options)
                    .compress(new BitmapCallback() {
                        @Override
                        public void callback(boolean b, Bitmap bitmap, Throwable throwable) {
                            if (b) {
                                mCompressedBitmap = bitmap;
                            }
                            String waterMark = "设备MAC：" + mMac + "\r\n场所名称：" + mPlace + "\r\n安装时间：" + mTime + "\r\n安装地址：" + mRealAddress;
                            //添加水印
                            Bitmap watermarkedBitmap = BitmapUtils.addTextWatermark(bitmap, waterMark, ConvertUtils.sp2px(9), Color.WHITE, 28, 118, false);
                            //保存图片到本地
                            if (watermarkedBitmap == null) {
                                ToastUtils.showShort("添加水印失败");
                            } else {
                                ImageUtils.save(watermarkedBitmap, mImagePath, Bitmap.CompressFormat.JPEG, false);
                                mIvPhoto.setImageBitmap(watermarkedBitmap);
                                mIvClear.setVisibility(View.VISIBLE);
                            }
                        }
                    });

        }
    }

    @Override
    public void onBackPressed() {
        backPressed();
    }

    private void backPressed() {
        if (mCompressedBitmap != null && !TextUtils.isEmpty(mMac) && !TextUtils.isEmpty(mPlace)) {
            showConfirmDialog();
        } else {
            finish();
        }
    }

    /**
     * 确认退出弹窗
     */
    private void showConfirmDialog() {
        mConfirmDialog = new AlertDialog.Builder(this)
                .setContentView(R.layout.dialog_confirm)
                .setWidthAndHeight(ConvertUtils.dp2px(260), -2)
                .setText(R.id.tv_content, "输入内容未保存，是否继续退出？")
                .setText(R.id.tv_right, "确定")
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
                        mConfirmDialog.dismiss();
                        finish();
                    }
                })
                .show();
    }

}
