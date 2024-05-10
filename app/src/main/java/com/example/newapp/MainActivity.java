package com.example.newapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 延迟3秒后执行以下操作
                Intent intent;
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

                if (sharedPreferences.contains("ids")) {
                    intent = new Intent(MainActivity.this, BottomNavigationView.class);
                } else {
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                }

                startActivity(intent);
                finish(); // 结束当前Activity，防止用户按返回键回到MainActivity
            }
        }, 1000); // 延迟3秒执行
    }

}