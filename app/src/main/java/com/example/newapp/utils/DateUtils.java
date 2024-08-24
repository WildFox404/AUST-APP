package com.example.newapp.utils;
import android.util.Log;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class DateUtils {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public static int getDayOfWeek(String dateStr) {

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

    public static boolean isCurrentTimeInRange(String startTimeStr, String endTimeStr) {
        try {
            long startTime = Long.parseLong(startTimeStr);
            long endTime = Long.parseLong(endTimeStr);
            long currentTime = System.currentTimeMillis();

            return currentTime >= startTime && currentTime <= endTime;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isPastDeadline(String endTimeStr) {
        try {
            long endTime = Long.parseLong(endTimeStr);
            long currentTime = System.currentTimeMillis();

            return currentTime > endTime;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String convertTimestampToDate(String timestamp) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date(Long.parseLong(timestamp));
            return dateFormat.format(date);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static long convertDateToTimestamp(String date) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = dateFormat.parse(date);
            return parsedDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String[] getDatesForWeek(String startDate) {
        String[] dates = new String[7];
        try {
            // Parse the startDate string to a Date object
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(startDate);

            // Calendar instance to manipulate dates
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            // Reset to the first day of the week (Monday)
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                calendar.add(Calendar.DATE, -1);
            }

            // Format each day of the week
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            for (int i = 0; i < 7; i++) {
                dates[i] = dateFormat.format(calendar.getTime());
                calendar.add(Calendar.DATE, 1); // Move to the next day
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dates;
    }

    public static List<List<String>> createWeeklyData(String current_date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<List<String>> weeklyDataList = new ArrayList<>();

        try {
            Date date = sdf.parse(current_date);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);

            // 找到当前月的第一天
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            int firstDayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK))%7;
            Log.d("createWeeklyData", String.valueOf(firstDayOfWeek));

            // 找到当前月的最后一天
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            int lastDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            Log.d("createWeeklyData", String.valueOf(lastDayOfMonth));
            int day_index=1;
            List<String> first_list =new ArrayList<>();
            if(firstDayOfWeek==0){
                firstDayOfWeek=7;
            }
            for(int i = 1; i<firstDayOfWeek; i++){
                first_list.add("");
            }
            for(int j=firstDayOfWeek;j<=7;j++){
                first_list.add(String.valueOf(day_index));
                day_index++;
            }

            weeklyDataList.add(first_list);

            int week_index=0;
            int week_index_last=(lastDayOfMonth-day_index)/7;
            Log.d("createWeeklyData", String.valueOf(week_index)+"/"+String.valueOf(week_index_last));
            Log.d("createWeeklyData", first_list.toString());
            while(week_index<week_index_last){
                List<String> week_list =new ArrayList<>();
                if(lastDayOfMonth-day_index>=7){
                    for(int i=0;i<7;i++){
                        week_list.add(String.valueOf(day_index));
                        day_index++;
                    }
                }else{
                    break;
                }
                Log.d("createWeeklyData", week_list.toString());
                weeklyDataList.add(week_list);
            }
            int number =lastDayOfMonth-day_index;
            List<String> week_list =new ArrayList<>();
            for(int i=0;i<=number;i++){
                week_list.add(String.valueOf(day_index));
                day_index++;
            }
            for(int j=number+1;j<7;j++){
                week_list.add("");
            }
            weeklyDataList.add(week_list);
            Log.d("createWeeklyData", week_list.toString());
            Log.d("finalcreateWeeklyData", weeklyDataList.toString());



        } catch (ParseException e) {
            e.printStackTrace();
        }

        return weeklyDataList;
    }

    public static String[] getYearMonthArray(String startDateString, String endDateString) throws ParseException {
        Date startDate = sdf.parse(startDateString);
        Date endDate = sdf.parse(endDateString);
        Log.d("DateUtils", startDate.toString()+endDateString.toString());
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        // 将 endCalendar 往后推一个月
        endCalendar.add(Calendar.MONTH, 1);

        List<String> yearMonthList = new ArrayList<>();
        while (startCalendar.before(endCalendar) || startCalendar.equals(endCalendar)) {
            String yearMonth = sdf.format(startCalendar.getTime()).substring(0, 7);
            if (!yearMonthList.contains(yearMonth)) {
                yearMonthList.add(yearMonth);
            }
            startCalendar.add(Calendar.MONTH, 1);
        }

        String[] yearMonthArray = new String[yearMonthList.size()];
        yearMonthList.toArray(yearMonthArray);
        Log.d("DateUtils", yearMonthList.toString());
        return yearMonthArray;
    }

    public static boolean isTimeRangeInMonth(String startDate, String endDate, String month) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);

            Calendar calendarStart = Calendar.getInstance();
            calendarStart.setTime(start);
            Calendar calendarEnd = Calendar.getInstance();
            calendarEnd.setTime(end);

            // 设置为该月第一天
            calendarStart.set(Calendar.DAY_OF_MONTH, 1);
            calendarEnd.set(Calendar.DAY_OF_MONTH, 1);

            DateFormat monthFormat = new SimpleDateFormat("yyyy-MM");

            return month.equals(monthFormat.format(calendarStart.getTime())) || month.equals(monthFormat.format(calendarEnd.getTime()));
        } catch (ParseException e) {
            Log.e("DateUtils", "Date parsing error: " + e.getMessage());
            return false;
        }
    }
    public static String convertDateFormat(String inputDate) {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = inputFormat.parse(inputDate);
            DateFormat outputFormat = new SimpleDateFormat("yyyy-MM");
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // 如果解析失败，返回null
        }
    }
    public static Map<String, Integer> getNextSixDaysAndWeekday(String inputDate) {
        Map<String, Integer> result = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = dateFormat.parse(inputDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            for (int i = 0; i < 6; i++) {
                result.put(dateFormat.format(calendar.getTime()), getWeekday(calendar.get(Calendar.DAY_OF_WEEK)));
                calendar.add(Calendar.DATE, 1);
            }
        } catch (ParseException e) {
            // 处理日期解析异常
            e.printStackTrace();
        }

        return result;
    }

    private static int getWeekday(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return 7;
            case Calendar.MONDAY:
                return 1;
            case Calendar.TUESDAY:
                return 2;
            case Calendar.WEDNESDAY:
                return 3;
            case Calendar.THURSDAY:
                return 4;
            case Calendar.FRIDAY:
                return 5;
            case Calendar.SATURDAY:
                return 6;
            default:
                return 1;
        }
    }

    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(currentDate);
    }

    public static String getCurrentMonthAndDay() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        return dateFormat.format(currentDate);
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

    public static JsonObject getWeekStartAndEndDates(String start_date, String end_date) {
        JsonArray weekArray=new JsonArray();
        JsonObject result=new JsonObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();

        Integer weeks=0;
        try {
            startCal.setTime(sdf.parse(start_date));
            endCal.setTime(sdf.parse(end_date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        while (startCal.before(endCal)) {
            JsonObject weekDate = new JsonObject();
            weekDate.addProperty("startDate",sdf.format(startCal.getTime()));
            startCal.add(Calendar.DAY_OF_MONTH, 7);
            weekDate.addProperty("endDate",sdf.format(startCal.getTime()));
            weekArray.add(weekDate);
            weeks+=1;
        }
        if (!weeks.equals(0)) {
            result.addProperty("weeks", weeks);
            result.add("weekArray", weekArray);
        }
        return result;
    }
}

