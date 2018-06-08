package sankemao.baselib.imageload.strategies;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import sankemao.baselib.imageload.ImageLoaderOptions;
import sankemao.baselib.imageload.ImageLoaderStrategyInter;

/**
 * Description:TODO
 * Create Time: 2017/11/21.17:17
 * Author:jin
 * Email:210980059@qq.com
 */
public class GlideStrategy implements ImageLoaderStrategyInter {

    @Override
    public void showImage(View container, Object imagePath, ImageLoaderOptions options) {
        DrawableTypeRequest<Object> mRequest = Glide.with(container.getContext())
                .load(imagePath);

        loadOptions(mRequest, options);

        mRequest.into((ImageView) container);

        mRequest.diskCacheStrategy(DiskCacheStrategy.NONE);
    }

    private void loadOptions(DrawableTypeRequest<Object> mRequest, ImageLoaderOptions options) {
        //裁剪方式
        if (options.getCropType() == ImageLoaderOptions.fitCenter) {
            mRequest.fitCenter();
        } else if(options.getCropType() == ImageLoaderOptions.centerCrop){
            mRequest.centerCrop();
        }

        //设置占位图
        if (options.getHolderRes() > 0) {
            mRequest.placeholder(options.getHolderRes());
        }

        //设置错误的占位图
        if (options.getErrorHolderRes() > 0) {
            mRequest.error(options.getErrorHolderRes());
        }

        //重新设置大小
        if (options.getResizeWidth() > 0 || options.getResizeHeight() > 0) {
            mRequest.override(options.getResizeWidth(), options.getResizeHeight());
        }

        //设置缓存策略
        if (options.getDiskCacheStrategy() != ImageLoaderOptions.DiskStrategy.ALL) {
            switch (options.getDiskCacheStrategy()) {
                case NONE:
                    mRequest.diskCacheStrategy(DiskCacheStrategy.NONE);
                    break;
                case SOURCE:
                    mRequest.diskCacheStrategy(DiskCacheStrategy.SOURCE);
                    break;
                case RESULT:
                    mRequest.diskCacheStrategy(DiskCacheStrategy.RESULT);
                    break;
                default:
                    break;
            }
        }

        mRequest.skipMemoryCache(options.getSkipMemoryCache());
    }

    @Override
    public void showRoundImage(View container, Object imagePath) {

    }

    @Override
    public void onPause(Context context) {
        Glide.with(context).pauseRequests();
    }

    @Override
    public void onResume(Context context) {
        Glide.with(context).resumeRequests();
    }
}
