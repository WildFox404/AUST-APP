package com.example.newapp.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    // 显示短时间的Toast消息
    public static void showToastShort(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // 显示长时间的Toast消息
    public static void showToastLong(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}