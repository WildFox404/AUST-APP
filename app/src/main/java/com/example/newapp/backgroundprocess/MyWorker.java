package com.example.newapp.backgroundprocess;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.newapp.db.MyDBHelper;
import com.example.newapp.entries.User;
import com.example.newapp.utils.DateUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

public class MyWorker extends Worker {
    private MyDBHelper myDBHelper;
    private Context mContext;
    private NotificationHelper mNotificationHelper;
    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
        mNotificationHelper = new NotificationHelper(mContext);
    }
    private String username;
    private String password;
    @NonNull
    @Override
    public Result doWork() {
        Log.d("doWork", "doWork启动");
        username = getInputData().getString("username");
        password = getInputData().getString("password");
        if(username.equals("")&&password.equals("")){
            //用户未登录
            //不执行操作
            Log.d("doWork", "用户未登录不执行操作");
            return Result.failure();
        }else{
            myDBHelper = new MyDBHelper(getApplicationContext());

            // 在这里执行网络请求和数据处理逻辑
            User user = User.getInstance(username,password); // 假设User类已经实现
            try {
                user.login();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Integer update_counts=0;
            // 执行获取学期信息的网络请求
            JsonObject semesters_result = null;
            try {
                semesters_result = user.update_semester();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (semesters_result != null) {
                JsonArray semesters=semesters_result.get("semesters").getAsJsonArray();
                for (JsonElement semester : semesters) {
                    JsonObject test_object = semester.getAsJsonObject();
                    String semester_id = test_object.get("id").getAsString();
                    String start_date = test_object.get("start_date").getAsString();
                    String end_date = test_object.get("end_date").getAsString();
                    String week_start_day = test_object.get("week_start_day").getAsString();

                    if(myDBHelper.isSemesterExists(semester_id)){
                        Log.d("doWork", semester_id+"已经存在");
                    }else{
                        Log.w("doWork", semester_id+"不存在,即将插入");
                        JsonObject return_result = DateUtils.getWeekStartAndEndDates(start_date,end_date);
                        if(return_result.has("weeks")){
                            Integer weeks=return_result.get("weeks").getAsInt();
                            JsonArray weekArray=return_result.get("weekArray").getAsJsonArray();
                            JsonObject jsonData = new JsonObject();
                            jsonData.addProperty("firstWeekday", week_start_day);
                            jsonData.addProperty("weeks", weeks);
                            jsonData.add("weekArray",weekArray);
                            SQLiteDatabase db = myDBHelper.getWritableDatabase();
                            myDBHelper.semesterDBInsertData(db,semester_id,jsonData);
                            update_counts+=1;
                        }else{
                            Log.d("doWork", "未排课");
                        }
                    };
                }

                if(update_counts>0){
                    //开始消息推送
                    Log.d("doWork", "有新的消息推送");
                    // 当需要发送通知时
                    mNotificationHelper.createNotification("课表更新", "课表更新学期数量:"+update_counts);
                }
                // 处理学期信息的逻辑
                // ...
                return Result.success();
            } else {
                return Result.failure();
            }
        }
    }
}