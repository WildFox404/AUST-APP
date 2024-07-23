package com.example.newapp;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static int getDayOfWeek(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = sdf.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            // 获取当前日期是所在周的第几天（1表示星期日，2表示星期一，以此类推）
            Log.d("getDayOfWeek", dateStr+":星期"+String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)-1));
            return calendar.get(Calendar.DAY_OF_WEEK)-1;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return -1; // 返回-1表示出错
    }

    public static String getFirstDayOfWeek(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = sdf.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            // 获取当前日期是所在周的第几天
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            // 计算当前日期距离本周第一天（星期日）的天数差
            int daysToSubtract = dayOfWeek - Calendar.SUNDAY;

            // 将 calendar 设置为本周第一天
            if (daysToSubtract > 0) {
                calendar.add(Calendar.DAY_OF_MONTH, -daysToSubtract);
            }

            return sdf.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}

