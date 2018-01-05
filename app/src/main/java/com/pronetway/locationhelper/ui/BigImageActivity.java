package com.pronetway.locationhelper.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.github.chrisbanes.photoview.PhotoView;
import com.pronetway.locationhelper.R;
import com.pronetway.locationhelper.app.Constant;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import sankemao.baselib.mvp.BaseActivity;
import sankemao.baselib.mvp.PresenterManager;
import sankemao.baselib.ui.utils.StatusbarUtil;

/**
 * 查看大图
 */
public class BigImageActivity extends BaseActivity {

    @BindView(R.id.photo_view)
    PhotoView mPhotoView;
    @BindView(R.id.loading)
    ProgressBar mLoading;
    @BindView(R.id.rl_title_bar)
    RelativeLayout mRlTitleBar;

    public static void go(Context context, String photoName) {
        Intent intent = new Intent(context, BigImageActivity.class);
        intent.putExtra("photoName", photoName);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_big_image;
    }

    @Override
    protected void initNavigationBar() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        StatusbarUtil.setActivityTranslucent(this);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mRlTitleBar.getLayoutParams();
        params.topMargin = StatusbarUtil.getStatusBarHeight(this);
        mRlTitleBar.setLayoutParams(params);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String photoName = intent.getStringExtra("photoName");
        Bitmap bitmap = ImageUtils.getBitmap(new File(Constant.Path.PHOTO_PATH, photoName));
        if (bitmap != null && bitmap.getWidth() != 0 && bitmap.getHeight() != 0) {
            mPhotoView.setImageBitmap(bitmap);
            mLoading.setVisibility(View.INVISIBLE);
            ViewGroup parent = (ViewGroup) mLoading.getParent();
            if (parent != null) {
                parent.removeView(mLoading);
            }
        } else {
            ToastUtils.showShort("图片加载失败");
        }
    }

    @Override
    public PresenterManager attachPresenters() {
        return null;
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
