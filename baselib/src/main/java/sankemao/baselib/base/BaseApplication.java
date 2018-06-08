package sankemao.baselib.base;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

import java.util.List;

/**
 * Description:TODO
 * Create Time: 2018/6/6.14:18
 * Author:jin
 * Email:210980059@qq.com
 */
public class BaseApplication extends Application {

    public static final String ROOT_PACKAGE = "com.pronetway.dc";
    private List<IApplicationDelegate> mAppDelegateList;

    private BaseApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Utils.init(this);

        mAppDelegateList = ClassUtils.getObjectsWithInterface(this, IApplicationDelegate.class, ROOT_PACKAGE);
        for (IApplicationDelegate delegate : mAppDelegateList) {
            delegate.onCreate();
        }
    }
}
