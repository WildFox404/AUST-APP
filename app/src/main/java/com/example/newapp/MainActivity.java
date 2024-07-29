package com.example.newapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.TypedValue;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.work.*;
import com.example.newapp.backgroundprocess.MyWorker;
import com.example.newapp.db.MyDBHelper;
import com.example.newapp.entries.User;
import com.example.newapp.navigation.BottomNavigationViewActivity;
import com.example.newapp.utils.DeviceDataUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private User user;
    private String savedUsername;
    private String savedPassword;
    private MyDBHelper myDBHelper;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
//        myDBHelper = new MyDBHelper(this); // 实例化 MyDBHelper 对象
//        SQLiteDatabase db = myDBHelper.getWritableDatabase(); // 获取可写的数据库对象
//        myDBHelper.onUpgrade(db, 1, 2); // 调用onUpgrade方法进行数据库升级
        getDeviceResolution(this);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        Data inputData = new Data.Builder()
                .putString("username", sharedPreferences.getString("username", ""))
                .putString("password", sharedPreferences.getString("password", ""))
                .build();

//        Constraints constraints = new Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.UNMETERED)  // 设置为 UNMETERED 表示只有在 Wi-Fi 网络上才能运行 Worker
//                .build();
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                MyWorker.class,
                1, TimeUnit.MINUTES)
                .setInputData(inputData)
                .setInitialDelay(15, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(this).enqueue(workRequest);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 延迟1秒后执行以下操作
                Intent intent;

                if (sharedPreferences.contains("username") && sharedPreferences.contains("password")) {
                    savedUsername = sharedPreferences.getString("username", "");
                    savedPassword = sharedPreferences.getString("password", "");
                    user = User.getInstance(savedUsername, savedPassword);

                    // 使用 AsyncTask 执行登录操作
                    new LoginTask().execute(user);
                } else {
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // 结束当前Activity，防止用户按返回键回到MainActivity
                }
            }
        }, 1000); // 延迟1秒执行
    }
    public void getDeviceResolution(Activity activity){
        float marginDp = 1; // 10dp 的距离
        float marginPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, marginDp, getResources().getDisplayMetrics());
        int margin_width=Math.round(marginPx);

        WindowManager windowManager = activity.getWindow().getWindowManager();
        Point point = new Point();
        windowManager.getDefaultDisplay().getRealSize(point);
        //屏幕实际宽度（像素个数）
        int width = point.x;
        //屏幕实际高度（像素个数）
        int height = point.y;
        DeviceDataUtils.getInstance(width,height,margin_width);
    }

    private class LoginTask extends AsyncTask<User, Void, Boolean> {
        private String token;

        @Override
        protected Boolean doInBackground(User... users) {
            try {
                users[0].login(); // 执行登录操作
                token = users[0].getToken(); // 获取token
                return !token.equals(""); // 返回登录是否成功
            } catch (IOException e) {
                e.printStackTrace();
                return false; // 登录失败
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            Intent intent;
            if (success) {
                intent = new Intent(MainActivity.this, BottomNavigationViewActivity.class);
            } else {
                intent = new Intent(MainActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish(); // 结束当前Activity，防止用户按返回键回到MainActivity
        }
    }
}