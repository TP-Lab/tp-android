package com.tokenbank.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;


public class ToastUtil {

    public static void toast(Context context, String msg) {
        if (TextUtils.isEmpty(msg)) {
            Toast.makeText(context, "空信息", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
