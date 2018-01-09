package com.pronetway.locationhelper.ui;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pronetway.locationhelper.R;
import com.pronetway.locationhelper.app.Constant;
import com.pronetway.locationhelper.bean.LocationInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sankemao.baselib.imageload.ImageLoaderOptions;
import sankemao.baselib.recyclerview.JViewHolder;
import sankemao.baselib.recyclerview.JrecyAdapter;
import sankemao.baselib.recyclerview.helper.DefaultHolderImageLoader;

/**
 * Description:TODO
 * Create Time: 2018/1/8.18:19
 * Author:jin
 * Email:210980059@qq.com
 */
public class LocationHistoryAdapter extends JrecyAdapter<LocationInfo> {

    private SparseArray<Boolean> mArray = new SparseArray<>();

    private boolean multiSelect;

    public LocationHistoryAdapter(Context context, List<LocationInfo> showItems, int layoutId) {
        super(context, showItems, layoutId);
    }

    /**
     * 选择或取消选择.
     */
    private void toggleSelect(int position) {
        if (mArray.get(position, false)) {
            mArray.remove(position);
        } else {
            mArray.put(position, true);
        }
        notifyDataSetChanged();
    }

    /**
     * 获取所选中的文件集合
     */
    public List<File> getFileList() {
        List<File> files = new ArrayList<>();
        for (int i = 0; i < mArray.size(); i++) {
            if (mArray.valueAt(i)) {
                File file = new File(Constant.Path.PHOTO_PATH + File.separator + mShowItems.get(mArray.keyAt(i)).getTime() + ".jpg");
                files.add(file);
            }
        }
        return files;
    }

    /**
     * 开启多选.
     */
    public void enableMultiSelect() {
        multiSelect = true;
        notifyDataSetChanged();
    }

    /**
     * 退出多选.
     */
    public void cancelMutiSelect() {
        mArray.clear();
        notifyDataSetChanged();
        multiSelect = false;
    }

    @Override
    protected void convert(JViewHolder holder, final LocationInfo info, final int position) {
        final String imagePath = Constant.Path.PHOTO_PATH + File.separator + info.getTime() + ".jpg";
        final boolean fileExists = FileUtils.isFileExists(imagePath);

        holder.setText(R.id.tv_mac, String.format(mContext.getString(R.string.device_mac), info.getMac()))
                .setText(R.id.tv_place, String.format(mContext.getString(R.string.place_name), info.getPlace()))
                .setText(R.id.tv_address, String.format(mContext.getString(R.string.address), info.getAddress()))
                .setText(R.id.tv_longitude, String.format(mContext.getString(R.string.longitude), info.getLongitude()))
                .setText(R.id.tv_latitude, String.format(mContext.getString(R.string.latitude), info.getLatitude()))
                .setText(R.id.tv_remark, String.format(mContext.getString(R.string.remark), info.getRemark()))
                .setText(R.id.tv_time, String.format(mContext.getString(R.string.time), info.getTime()))
                .setText(R.id.tv_position, String.valueOf(position))
                .setOnItemClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (multiSelect) {
                            //多选
                            if (!fileExists) {
                                ToastUtils.showShort("没有相关图片");
                                return;
                            }
                            toggleSelect(position);
                        } else {
                            if (mNormalClick != null) {
                                mNormalClick.onNormalClick(position, info, v);
                            }
                        }
                    }
                });

        if (fileExists) {
            holder.setImgByUrl(R.id.iv_photo, new DefaultHolderImageLoader(imagePath, ImageLoaderOptions.newOptions().setCropType(ImageLoaderOptions.centerCrop).skipMemoryCache(true).setDiskStrategy(DiskCacheStrategy.NONE)))
                    .setOnClickListener(R.id.iv_photo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BigImageActivity.go(mContext, imagePath);
                        }
                    });
        } else {
            holder.setImgByUrl(R.id.iv_photo, new DefaultHolderImageLoader(R.drawable.icon_empty));
        }

        holder.setSelected(R.id.rl_item, mArray.get(position, false));
    }

    private NormalClick mNormalClick;

    public void setNormalClick(NormalClick normalClick) {
        this.mNormalClick = normalClick;
    }

    public void notifyItemDataChanged(int position, LocationInfo locationInfo) {
        mShowItems.set(position, locationInfo);
        this.notifyItemChanged(position);
    }

    /**
     * 非多选状态下点击事件
     */
    public interface NormalClick {
        void onNormalClick(int position, LocationInfo locationInfo, View currentSelectItemView);
    }

}
