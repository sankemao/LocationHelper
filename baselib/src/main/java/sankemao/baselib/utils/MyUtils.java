package sankemao.baselib.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;

/**
 * Description:TODO
 * Create Time: 2018/6/6.14:19
 * Author:jin
 * Email:210980059@qq.com
 */
public class MyUtils {

    public static boolean isAppDebug() {
        if (StringUtils.isSpace(Utils.getApp().getPackageName())) return false;
        try {
            PackageManager pm = Utils.getApp().getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(Utils.getApp().getPackageName(), 0);
            return ai != null && (ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
