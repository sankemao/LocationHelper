package sankemao.baselib.base;

import android.support.annotation.Keep;

/**
 * Description:TODO
 * Create Time: 2018/6/6.15:56
 * Author:jin
 * Email:210980059@qq.com
 */
@Keep
public interface IApplicationDelegate {
    void onCreate();

    void onTerminate();

    void onLowMemory();

    void onTrimMemory(int level);
}
