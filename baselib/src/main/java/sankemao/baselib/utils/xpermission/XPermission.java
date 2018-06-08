package sankemao.baselib.utils.xpermission;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description:TODO
 * Create Time: 2018/6/8.10:01
 * Author:jin
 * Email:210980059@qq.com
 */
public class XPermission {

    public Activity mActivity;
    private String TAG_FG = PermissionFragment.class.getName();
    private PermissionFragment mFragment;
    private List<String> mPermissionList;

    public XPermission(Activity activity) {
        this.mActivity = activity;
        createFragment(activity);
    }


    /**
     * 创建申请权限的fragment
     */
    private void createFragment(Activity activity) {
        final FragmentManager fm = activity.getFragmentManager();
        mFragment = (PermissionFragment) fm.findFragmentByTag(TAG_FG);
        if (mFragment == null) {
            mFragment = PermissionFragment.newInstance();
            fm.beginTransaction().add(mFragment, TAG_FG)
                    .commitAllowingStateLoss();
            fm.executePendingTransactions();
        }
    }

    /**
     * 要申请的权限
     */
    public XPermission permissions(String... permissions) {
        if (mPermissionList == null) {
            mPermissionList = new ArrayList<>();
        }
        mPermissionList.addAll(Arrays.asList(permissions));
        return this;
    }

    public void request(PermissionListener listener) {
        mFragment.setListener(listener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //获取为申请的权限列表
            List<String> deniedPermissions = PermissionUtils.getDeniedPermissions(mActivity, mPermissionList);
            int permissionSize = deniedPermissions.size();
            if (permissionSize > 0) {
                //申请权限
                mFragment.requestPermissions(mActivity, deniedPermissions.toArray(new String[permissionSize]));
            } else {
                mFragment.onSucceed();
            }
        } else {
            mFragment.onSucceed();
        }
    }

}
