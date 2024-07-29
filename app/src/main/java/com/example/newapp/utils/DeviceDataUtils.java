package com.example.newapp.utils;

import android.util.Log;

public class DeviceDataUtils {
    private int width;
    private int height;
    private int margin_1dp;
    private static DeviceDataUtils instance;

    public DeviceDataUtils(int width, int height,int margin_1dp){
        this.width=width;
        this.height=height;
        this.margin_1dp=margin_1dp;
        Log.d("DeviceData", "成功获取设备数据"+width+"x"+height);
    }

    public static synchronized DeviceDataUtils getInstance() {
        if (instance == null) {
            synchronized (DeviceDataUtils.class) {
                if (instance == null) {
                    return null;
                }
            }
        }
        return instance;
    }

    public static synchronized DeviceDataUtils getInstance(int width, int height,int margin_1dp) {
        if (instance == null) {
            synchronized (DeviceDataUtils.class) {
                if (instance == null) {
                    instance = new DeviceDataUtils(width,height,margin_1dp);
                }
            }
        }
        return instance;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMargin_1dp() {
        return margin_1dp;
    }

    public void setMargin_1dp(int margin_1dp) {
        this.margin_1dp = margin_1dp;
    }
}
