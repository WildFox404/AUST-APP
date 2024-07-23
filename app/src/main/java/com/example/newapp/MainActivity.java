package com.example.newapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private User user;
    private String savedUsername;
    private String savedPassword;
    private MyDBHelper myDBHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
//        myDBHelper = new MyDBHelper(this); // 实例化 MyDBHelper 对象
//        SQLiteDatabase db = myDBHelper.getWritableDatabase(); // 获取可写的数据库对象
//        myDBHelper.onUpgrade(db, 1, 2); // 调用onUpgrade方法进行数据库升级
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 延迟1秒后执行以下操作
                Intent intent;
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

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
                intent = new Intent(MainActivity.this, BottomNavigationView.class);
            } else {
                intent = new Intent(MainActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish(); // 结束当前Activity，防止用户按返回键回到MainActivity
        }
    }
}