package com.example.newapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class BottomNavigationView extends AppCompatActivity {
    private ConsultationFragment consultationFragment;
    private HomeFragment homeFragment;
    private SettingFragment settingFragment;
    private UserFragment userFragment;
    private ServiceFragment serviceFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment); // 设置活动的布局文件
        consultationFragment = new ConsultationFragment();
        homeFragment = new HomeFragment();
        settingFragment = new SettingFragment();
        userFragment = new UserFragment();
        serviceFragment = new ServiceFragment();
        // 设置默认显示的Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();

        // 在容器中找到 BottomNavigationView 控件
//        View bottomNavigationView = bottomNavigationContainer.findViewById(R.id.bottom_navigation);
        LinearLayout consultation = findViewById(R.id.consultation_button);
        LinearLayout home = findViewById(R.id.home_button);
        LinearLayout setting = findViewById(R.id.setting_button);
        LinearLayout user = findViewById(R.id.user_button);
        LinearLayout service = findViewById(R.id.service_button);
        Log.d("UserCreation", consultation.toString());
        // 为每个ImageView设置点击监听器，并使用Lambda表达式进行Fragment的切换
        consultation.setOnClickListener(view -> getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, consultationFragment).commit());

        home.setOnClickListener(view -> getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit());

        setting.setOnClickListener(view -> getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, settingFragment).commit());

        user.setOnClickListener(view -> getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, userFragment).commit());

        service.setOnClickListener(view -> getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, serviceFragment).commit());
    }
}
