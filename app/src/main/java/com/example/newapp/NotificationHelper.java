package com.example.newapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    private Context mContext;
    private NotificationManager mNotificationManager;

    public NotificationHelper(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 创建通知渠道
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            // 设置通知渠道的描述
            channel.setDescription("Channel Description");
            // 注册通知渠道
            mNotificationManager.createNotificationChannel(channel);
        }
    }

    public void createNotification(String title, String message) {
        // 创建通知构建器
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "channel_id")
                .setSmallIcon(R.mipmap.ic_launcher_new) // 设置通知图标
                .setContentTitle(title) // 设置通知标题
                .setContentText(message); // 设置通知内容

        // 发送通知
        mNotificationManager.notify(1, builder.build());
    }
}