package com.example.newapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class BottomNavigationView extends AppCompatActivity {
    private ConsultationFragment consultationFragment;
    private HomeFragment homeFragment;
    private SettingFragment settingFragment;
    private UserFragment userFragment;
    private ServiceFragment serviceFragment;
    private Fragment currentFragment; // 用于跟踪当前显示的Fragment

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
        currentFragment = homeFragment; // 设置当前显示的Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();

        // 在容器中找到 BottomNavigationView 控件
        LinearLayout home = findViewById(R.id.home_button);
        LinearLayout user = findViewById(R.id.user_button);
        LinearLayout service = findViewById(R.id.service_button);

        // 为每个ImageView设置点击监听器，并使用Lambda表达式进行Fragment的切换
        home.setOnClickListener(view -> showFragment(homeFragment));
        user.setOnClickListener(view -> showFragment(userFragment));
        service.setOnClickListener(view -> showFragment(serviceFragment));
    }

    private void showFragment(Fragment fragment) {
        if (fragment != currentFragment) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (currentFragment != null) {
                fragmentTransaction.hide(currentFragment); // 隐藏当前的Fragment
            }
            if (!fragment.isAdded()) {
                fragmentTransaction.add(R.id.fragment_container, fragment); // 如果没有添加过，则添加
            } else {
                fragmentTransaction.show(fragment); // 如果已经添加过，则显示
            }
            currentFragment = fragment; // 更新当前显示的Fragment
            fragmentTransaction.addToBackStack(null); // 添加到返回栈
            fragmentTransaction.commit();
        }
    }
}
