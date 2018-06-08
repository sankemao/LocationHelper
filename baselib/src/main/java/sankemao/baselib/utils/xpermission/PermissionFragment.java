package sankemao.baselib.utils.xpermission;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

/**
 * Description:TODO
 * Create Time: 2018/6/8.9:57
 * Author:jin
 * Email:210980059@qq.com
 */
public class PermissionFragment extends Fragment {
    private static final int CODE = 66;
    private static final int REQUEST_PERMISSION_SETTING = 55;

    private PermissionListener mListener;
    private Activity mActivity;

    public static PermissionFragment newInstance() {
        return new PermissionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * 设置权限申请成功监听
     */
    public void setListener(PermissionListener listener) {
        this.mListener = listener;
    }

    /**
     * 申请权限
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestPermissions(Activity activity, String[] permissions) {
        this.mActivity = activity;
        requestPermissions(permissions, CODE);
    }

    /**
     * 授权回调
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE) {
            // 获取未申请的权限列表
            List<String> deniedPermissions = PermissionUtils.getDeniedPermissions(mActivity, Arrays.asList(permissions));
            if (deniedPermissions.size() > 0) {
                // 执行失败的方法
                onFailed(permissions);
            } else {
                // 执行成功的方法
                onSucceed();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void onFailed(String[] permissions) {
        for (int i = 0; i < permissions.length; i++) {
            // 用户拒绝是true   用户选择不再提示是：false
            if (!shouldShowRequestPermissionRationale(permissions[i])) {
                new AlertDialog.Builder(mActivity)
                        .setTitle("权限被拒绝")
                        .setMessage("权限管理-->打开拒绝的权限")
                        .setPositiveButton("去打开", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                openSetting();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();


                return;
            }
        }
        Toast.makeText(mActivity, "权限被拒绝", Toast.LENGTH_LONG).show();
    }

    /**
     * 打开应用设置界面
     */
    private void openSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mActivity.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
    }

    public void onSucceed() {
        if (mListener != null) {
            mListener.onSucceed();
        }
    }

    @Override
    public void onDestroy() {
        mActivity = null;
        mListener = null;
        super.onDestroy();
    }
}
