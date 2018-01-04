package com.pronetway.locationhelper.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;

/**
 * Description:TODO
 * Create Time: 2018/1/4.14:45
 * Author:jin
 * Email:210980059@qq.com
 */
public class BitmapUtils {
    /**
     * 添加水印
     */
    public static Bitmap addTextWatermark(final Bitmap src,
                                          final String content,
                                          final float textSize,
                                          @ColorInt final int color,
                                        final float paddingLeft,
                                          final float paddingBottom,
                                          final boolean recycle) {
        if (isEmptyBitmap(src) || content == null) return null;
        Bitmap ret = src.copy(src.getConfig(), true);
        int retWidth = ret.getWidth();
        int retHeight = ret.getHeight();
        //底部阴影高度
        int shadowHeight = 200;

        Paint fontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fontPaint.setDither(true);
        fontPaint.setColor(color);
        fontPaint.setTextSize(textSize);

        Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setDither(true);
        shadowPaint.setColor(Color.parseColor("#4c3c3c3c"));

        Canvas canvas = new Canvas(ret);
        //绘制底部阴影
        canvas.drawRect(0, retHeight - shadowHeight, retWidth, retHeight, shadowPaint);
        //绘制文字
        Rect bounds = new Rect();
        fontPaint.getTextBounds(content, 0, content.length(), bounds);
        canvas.drawText(content, paddingLeft, retHeight - paddingBottom, fontPaint);

        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * 判断bitmap对象是否为空
     *
     * @param src 源图片
     * @return {@code true}: 是<br>{@code false}: 否
     */
    private static boolean isEmptyBitmap(final Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }
}
