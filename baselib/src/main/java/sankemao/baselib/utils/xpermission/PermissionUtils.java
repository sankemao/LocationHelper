package sankemao.baselib.utils.xpermission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:TODO
 * Create Time: 2018/6/8.10:15
 * Author:jin
 * Email:210980059@qq.com
 */
public class PermissionUtils {

    public static List<String> getDeniedPermissions(Activity activity, List<String> permissions) {
        List<String> list = new ArrayList<>();
        if (permissions == null) {
            return list;
        }
        for (String p : permissions) {
            int permission = ContextCompat.checkSelfPermission(activity, p);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                list.add(p);
            }

        }
        return list;
    }
}
