package com.wen.zxing;

import android.graphics.Bitmap;

/**
 * 扫描监听
 */

public interface OnScanListener {
    void onScanSucceed(String result, Bitmap bitmap);
}
