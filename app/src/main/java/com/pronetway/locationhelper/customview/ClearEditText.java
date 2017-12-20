package com.pronetway.locationhelper.customview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.pronetway.locationhelper.R;


/**
 * Created by jin on 2016/12/28.
 *
 */
public class ClearEditText extends AppCompatEditText implements View.OnFocusChangeListener {

    private Drawable mClearDrawable;
    private boolean hasFocus;
    private int mHeight;

    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mClearDrawable = getCompoundDrawables()[2];//获取该控件右边位置的图片
        if (mClearDrawable == null) {
            mClearDrawable = getResources().getDrawable(R.drawable.icon_et_close);
        }
        this.measure(0,0);

        mHeight = this.getMeasuredHeight();

        mClearDrawable.setBounds(-mHeight *2 / 3, 0, 0, mHeight * 2 / 3);
        setClearIconVisible(false);

        setOnFocusChangeListener(this);

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (hasFocus) {
                    setClearIconVisible(s.length() > 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mClearDrawable != null) {
                int x = (int)event.getX();//这个x坐标是相对于这个控件本身的.
//                Rect rBounds = this.mClearDrawable.getBounds();
                if (x > getWidth() - mHeight * 2) {
                    this.setText("");
                    event.setAction(MotionEvent.ACTION_CANCEL);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],getCompoundDrawables()[1],right,getCompoundDrawables()[3]);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFocus = hasFocus;
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
    }
}
