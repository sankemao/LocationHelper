/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wen.zxing;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.google.zxing.ResultPoint;

import java.util.Collection;
import java.util.HashSet;


public final class ViewfinderView extends View {

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 100L;
    private static final int OPAQUE = 0xFF;

    private final Paint paint;
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    private final int frameColor;
    private final int laserColor;
    private final int resultPointColor;
    private int scannerAlpha;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;

    private static int SPEEN_DISTANCE = 8;
    private int slideTop;
    private int slideBottom;
    private boolean isFirst;
    private static int line_distance = 1;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setAntiAlias(true);
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        frameColor = resources.getColor(R.color.qr_code_flash_light_text_color);
        laserColor = resources.getColor(R.color.viewfinder_laser);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        scannerAlpha = 0;
        possibleResultPoints = new HashSet<>(5);
        line_distance = dip2px(1);
    }

    private boolean isAdd = true;

    @Override
    public void onDraw(Canvas canvas) {
        // 中间的扫描框，你要修改扫描框的大小，去CameraManager里面修改
        Rect frame = CameraManager.get().getFramingRect();
        if (frame == null) {
            return;
        }
        // 获取屏幕的宽和高
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        //画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面
        //扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        if (resultBitmap != null) {
            paint.setAlpha(OPAQUE);
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {
            //画扫描框边上的角，总共8个部分
            paint.setColor(frameColor);
            canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, paint);
            canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, paint);
            canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, paint);
            canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, paint);


            //画扫描框下面的字
            paint.setColor(laserColor);
            paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
            scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
            if (!isFirst) {
                isFirst = true;
                slideTop = frame.top;
                slideBottom = frame.bottom;
            }
            if (slideTop >= frame.bottom - line_distance - dip2px(1)) {
                isAdd = false;
            } else if (slideTop <= frame.top) {
                isAdd = true;
            }
            if (isAdd) {
                slideTop += SPEEN_DISTANCE;
            } else {
                slideTop -= SPEEN_DISTANCE;
            }
            canvas.drawRect(frame.left + 2, slideTop, frame.right, slideTop + line_distance, paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(dip2px(14));
            String text = "二维码/条形码放入显示框";
            float textWidth = paint.measureText(text);

            canvas.drawText(
                    text,
                    (width - textWidth) / 2,
                    (float) (frame.bottom + dip2px(14 + 5)),
                    paint);

            Collection<ResultPoint> currentPossible = possibleResultPoints;
            Collection<ResultPoint> currentLast = lastPossibleResultPoints;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new HashSet<>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(OPAQUE);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentPossible) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);
                }
            }
            if (currentLast != null) {
                paint.setAlpha(OPAQUE / 2);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentLast) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
                }
            }

            //只刷新扫描框的内容，其他地方不刷新
            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
        }
    }

    private int dip2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }

    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }

}
