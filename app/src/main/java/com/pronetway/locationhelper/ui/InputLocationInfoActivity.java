package com.pronetway.locationhelper.ui;

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
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.pronetway.locationhelper.R;
import com.pronetway.locationhelper.app.Constant;
import com.pronetway.locationhelper.bean.LocationInfo;
import com.pronetway.locationhelper.db.dbutils.LocationDbUtils;
import com.pronetway.locationhelper.utils.BitmapUtils;
import com.pronetway.locationhelper.utils.ExcelUtils;
import com.pronetway.locationhelper.utils.MacTextWatcher;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import sankemao.baselib.mvp.BaseActivity;
import sankemao.baselib.mvp.PresenterManager;
import sankemao.baselib.ui.navigation.DefaultNavigationBar;

import static com.pronetway.locationhelper.app.Constant.Path.PHOTO_PATH;

public class InputLocationInfoActivity extends BaseActivity {
    public static final String ADDRESS = "_address";
    public static final String LATITUDE = "_latitude";
    public static final String LONGITUDE = "_longitude";
    public static final String TIME = "_time";
    public static final String BUNDLE = "_bundle";
    public static final int REQUEST_CODE_CAMERA = 101;

    @BindView(R.id.iv_photo)
    ImageView mIvPhoto;
    @BindView(R.id.et_mac)
    EditText mEtMac;
    @BindView(R.id.et_place)
    EditText mEtPlace;
    @BindView(R.id.et_address)
    EditText mEtAddress;
    @BindView(R.id.et_remark)
    EditText mEtRemark;
    private Uri mImageUri;
    //temp图片文件
    private File mImageFile;
    private String mAddress;
    private String mLatitude;
    private String mLongitude;
    private String mTime;
    private String mWaterMarkedImageName;


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
                        finish();
                    }
                })
                .setText(R.id.tv_title, "录入信息")
                .build();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(BUNDLE);
        mAddress = bundle.getString(ADDRESS);
        mLatitude = bundle.getString(LATITUDE);
        mLongitude = bundle.getString(LONGITUDE);
        mTime = bundle.getString(TIME);

        mEtMac.addTextChangedListener(new MacTextWatcher(mEtMac));
        mEtAddress.setText(mAddress);

        mWaterMarkedImageName = mLatitude + mLongitude + ".jpg";
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    public PresenterManager attachPresenters() {
        return null;
    }

    /**
     * 拍照
     */
    @OnClick(R.id.btn_take_photo)
    public void onTakePhoto() {
        //先检测, 用以添加水印的信息
        String mac = mEtMac.getText().toString().trim();
        String place = mEtPlace.getText().toString().trim();
        if (!check(mac, place)) {
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
        //file:///storage/emulated/0/com.pronetway.locationhelper/%E7%85%A7%E7%89%87/test.jpg
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mImageUri = FileProvider.getUriForFile(this, "com.pronetway.locationhelper.fileprovider", mImageFile);
        } else {
            mImageUri = Uri.fromFile(mImageFile);
        }
        mImageUri = Uri.fromFile(mImageFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri); // set the image file name
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    /**
     * 保存mac信息
     */
    @OnClick(R.id.btn_save)
    public void onSaveClick() {
        String mac = mEtMac.getText().toString().trim();
        String place = mEtPlace.getText().toString().trim();
        String remark = mEtRemark.getText().toString().trim();
        if (!check(mac, place)) {
            return;
        }

        String address = mEtAddress.getText().toString().trim();
        LocationInfo locationInfo = new LocationInfo(mac, place, address, mLatitude, mLongitude, remark, mTime);
        //保存到excel
        ExcelUtils.getInstance().writeLocationInfo(locationInfo, Constant.Path.EXCEL_NAME);
        //保存到db.
        LocationDbUtils.getInstance().insertLocation(locationInfo);
        ToastUtils.showShort("保存成功");
    }

    /**
     * 检测mac以及场所名称的合法性
     */
    public boolean check(String mac, String place) {
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
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CAMERA && mImageUri != null) {
            //从文件中获取缩放后的bitmap, 并按比例缩放
            Bitmap scaledBitmap = ImageUtils.getBitmap(mImageFile, 720, 720);
            //添加水印
            Bitmap watermarkedBitmap = BitmapUtils.addTextWatermark(scaledBitmap, "我是2b", ConvertUtils.sp2px(20), Color.WHITE, 100, 90, false);
            //保存图片到本地
            if (watermarkedBitmap == null) {
                ToastUtils.showShort("添加水印失败");
            } else {
                ImageUtils.save(watermarkedBitmap, Constant.Path.PHOTO_PATH + File.separator + mWaterMarkedImageName, Bitmap.CompressFormat.JPEG, false);
                mIvPhoto.setImageBitmap(watermarkedBitmap);
            }
        }
    }

}
