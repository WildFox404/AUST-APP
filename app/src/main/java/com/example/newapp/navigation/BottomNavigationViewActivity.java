package com.example.newapp.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.newapp.*;
import com.example.newapp.entries.User;

public class BottomNavigationViewActivity extends AppCompatActivity {
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private HomeFragment homeFragment;
    private UserFragment userFragment;
    private ServiceFragment serviceFragment;
    private SecondClassActivity secondClassActivity;
    private Fragment currentFragment; // 用于跟踪当前显示的Fragment
    private User user = User.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment); // 设置活动的布局文件
        String user_name = user.getUser_name();

        homeFragment = new HomeFragment();
        userFragment = new UserFragment();
        serviceFragment = new ServiceFragment();

        // 设置默认显示的Fragment
        currentFragment = homeFragment; // 设置当前显示的Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();

        // 在容器中找到 BottomNavigationView 控件
        LinearLayout home = findViewById(R.id.home_button);
        LinearLayout user = findViewById(R.id.user_button);
        LinearLayout service = findViewById(R.id.service_button);
        LinearLayout second_class = findViewById(R.id.second_class_button);

        // 为每个ImageView设置点击监听器，并使用Lambda表达式进行Fragment的切换
        home.setOnClickListener(view -> showFragment(homeFragment));
        user.setOnClickListener(view -> showFragment(userFragment));
        service.setOnClickListener(view -> showFragment(serviceFragment));
        second_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(BottomNavigationViewActivity.this,SecondClassActivity.class);
                startActivity(intent);
            }
        });

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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 用户触摸屏幕时重置计时器
        resetTimer();
        return super.dispatchTouchEvent(ev);
    }

    private void startTimer() {
        mHandler.postDelayed(mRunnable, 10000); // 20秒后执行runnable中的操作
    }

    private void resetTimer() {
        mHandler.removeCallbacks(mRunnable); // 移除之前的回调，相当于重置计时器
        startTimer(); // 重新开始计时
    }
}
