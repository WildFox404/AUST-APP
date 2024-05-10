package com.example.newapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

public class MainNextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startnext);

        // 找到 TextView
        TextView jumpStartNext = findViewById(R.id.jumpStartNext);

        // 给 TextView 设置点击事件
        jumpStartNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在点击事件中执行的操作
                // 这里可以放置您希望执行的代码，比如跳转到其他页面
                finish();
            }
        });

        // 使用Handler延迟1秒后执行finish()
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 500);
    }
}
