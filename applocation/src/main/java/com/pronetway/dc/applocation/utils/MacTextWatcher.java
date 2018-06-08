package com.pronetway.dc.applocation.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Description: Mac输入格式化
 * Create Time: 2017/12/26.13:49
 * Author:jin
 * Email:210980059@qq.com
 */
public class MacTextWatcher implements TextWatcher {
    private EditText mEtMac;
    private int mInputTempLength;

    public MacTextWatcher(EditText etMac) {
        this.mEtMac = etMac;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        mInputTempLength = s.toString().length();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String inputTemp = s.toString().trim();
        switch (inputTemp.length()) {
            case 2:
            case 5:
            case 8:
            case 11:
            case 14:
                if (inputTemp.length() > mInputTempLength) {
                    mEtMac.setText(inputTemp + ":");
                    mEtMac.setSelection(inputTemp.length() + 1);
                }
                break;
        }
    }
}
