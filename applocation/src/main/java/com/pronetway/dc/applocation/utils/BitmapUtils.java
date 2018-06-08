package com.pronetway.dc.applocation.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

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

        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setDither(true);
        textPaint.setColor(color);
        textPaint.setTextSize(textSize);

        Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setDither(true);
        shadowPaint.setColor(Color.parseColor("#4c3c3c3c"));

        Canvas canvas = new Canvas(ret);
        //绘制底部阴影
        canvas.drawRect(0, retHeight - shadowHeight, retWidth, retHeight, shadowPaint);
        //绘制文字
//        Rect bounds = new Rect();
//        textPaint.getTextBounds(content, 0, content.length(), bounds);
        StaticLayout layout = new StaticLayout(content, textPaint, 700, Layout.Alignment.ALIGN_NORMAL, 1f, 0f, true);
        canvas.save();
        canvas.translate(paddingLeft, retHeight - paddingBottom - 2 * textSize);
        layout.draw(canvas);
        canvas.restore();

        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * 判断bitmap对象是否为空
     *
     * @param src 源图片
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isEmptyBitmap(final Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }
}
