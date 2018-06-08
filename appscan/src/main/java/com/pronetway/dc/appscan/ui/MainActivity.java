package com.pronetway.dc.appscan.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pronetway.dc.appscan.R;
import com.wen.zxing.CaptureActivity;
import com.wen.zxing.OnScanListener;

import sankemao.baselib.mvp.BaseActivity;
import sankemao.baselib.mvp.PresenterManager;
import sankemao.baselib.utils.xpermission.Permissions;
import sankemao.baselib.utils.xpermission.PermissionListener;
import sankemao.baselib.utils.xpermission.XPermission;

@Route(path="/scan/home")
public class MainActivity extends BaseActivity {

    private TextView mResult;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initNavigationBar() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mResult = getViewById(R.id.tv_result);
        Button btn = getViewById(R.id.btn_scan);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });
    }

    public void scan() {
        new XPermission(this)
                .permissions(Permissions.CAMERA)
                .request(new PermissionListener() {
                    @Override
                    public void onSucceed() {
                        CaptureActivity.mOnScanListener = mOnScanListener;
                        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                        MainActivity.this.startActivity(intent);
                    }
                });
    }

    private OnScanListener mOnScanListener = new OnScanListener() {
        @Override
        public void onScanSucceed(String result, Bitmap bitmap) {
            mResult.setText(result);
        }
    };

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    public PresenterManager attachPresenters() {
        return null;
    }
}
