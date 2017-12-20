package sankemao.baselib.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.blankj.utilcode.util.LogUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jin on 2017/5/8.
 *
 */
public class PermissionUtils {

    private PermissionUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    public static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static void executeSucceedMethod(Object reflectObject, int requestCode) {
        //获取class中所有方法
        // 获取class中多有的方法
        Method[] methods = reflectObject.getClass().getDeclaredMethods();

        // 遍历找我们打了标记的方法
        for (Method method : methods) {
            // 获取该方法上面有没有打这个成功的标记
            PermissionSucceed succeedMethod = method.getAnnotation(PermissionSucceed.class);
            if (succeedMethod != null) {
                // 代表该方法打了标记
                // 并且我们的请求码必须 requestCode 一样
                int methodCode = succeedMethod.requestCode();
                if (methodCode == requestCode) {
                    // 这个就是我们要找的成功方法
                    // 反射执行该方法
                    LogUtils.e("TAG", "找到了该方法 ：" + method);
                    executeMethod(reflectObject, method);
                }
            }
        }
    }

    /**
     * 反射执行该方法
     */
    private static void executeMethod(Object reflectObject, Method method) {
        // 反射执行方法  第一个是传该方法是属于哪个类   第二个参数是反射方法的参数
        try {
            method.setAccessible(true); // 允许执行私有方法
            method.invoke(reflectObject, new Object[]{});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取没有授予的权限
     *
     * @return 没有授予过的权限
     */
    public static List<String> getDeniedPermissions(Object object, String[] requestPermissions) {
        List<String> deniedPermissions = new ArrayList<>();
        for (String requestPermission : requestPermissions) {
            //把拒绝的权限加入集合
            if (ContextCompat.checkSelfPermission(getActivity(object), requestPermission)
                    == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(requestPermission);
            }
        }
        return deniedPermissions;
    }

    /**
     * 获取上下文
     *
     * @param object
     * @return
     */
    public static Activity getActivity(Object object) {
        if (object instanceof Activity) {
            return (Activity) object;
        }
        if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        }
        return null;
    }


    /**
     * 执行失败的方法
     */
    public static void executeFailMethod(Object reflectObject, int requestCode) {
        Method[] methods = reflectObject.getClass().getDeclaredMethods();

        for (Method method : methods) {
            PermissionFail failMethod = method.getAnnotation(PermissionFail.class);
            if (failMethod != null) {
                int methodCode = failMethod.requestCode();
                if (methodCode == requestCode) {
                    LogUtils.e("找到了该方法 ：" + method);
                    executeMethod(reflectObject, method);
                }
            }
        }
    }
}