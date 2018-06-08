package debug;

import sankemao.baselib.base.BaseApplication;

/**
 * Description:TODO
 * Create Time: 2017/12/18.13:38
 * Author:jin
 * Email:210980059@qq.com
 */
public class MyApplication extends BaseApplication {

    /**
     * 单独组件化的时候，ApplicationDelegate内初始化不能满足时，可再次进行额外初始化
     * 如模拟登录等操作。
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }

}
