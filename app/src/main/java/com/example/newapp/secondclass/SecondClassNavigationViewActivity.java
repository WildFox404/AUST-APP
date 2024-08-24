package com.example.newapp.secondclass;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.newapp.R;
import com.example.newapp.navigation.*;

public class SecondClassNavigationViewActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private SecondClassUserFragment secondClassUserFragment;
    private SecondClassHomeFragment secondClassHomeFragment;
    private SecondClassTodoFragment secondClassTodoFragment;
    private SecondClassSchoolFragment secondClassSchoolFragment;
    private Fragment currentFragment; // 用于跟踪当前显示的Fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondclassfragment); // 设置活动的布局文件

        secondClassHomeFragment = new SecondClassHomeFragment();
        secondClassSchoolFragment = new SecondClassSchoolFragment();
        secondClassTodoFragment = new SecondClassTodoFragment();
        secondClassUserFragment = new SecondClassUserFragment();

        // 设置默认显示的Fragment
        currentFragment = secondClassHomeFragment; // 设置当前显示的Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();

        // 在容器中找到 BottomNavigationView 控件
        LinearLayout second_class_school = findViewById(R.id.school);
        LinearLayout second_class_home = findViewById(R.id.home);
        LinearLayout second_class_todo = findViewById(R.id.todo);
        LinearLayout second_class_user = findViewById(R.id.user);
        LinearLayout second_class_exit = findViewById(R.id.exit);

        // 为每个ImageView设置点击监听器，并使用Lambda表达式进行Fragment的切换
        second_class_school.setOnClickListener(view -> showFragment(secondClassSchoolFragment));
        second_class_home.setOnClickListener(view -> showFragment(secondClassHomeFragment));
        second_class_todo.setOnClickListener(view -> showFragment(secondClassTodoFragment));
        second_class_user.setOnClickListener(view -> showFragment(secondClassUserFragment));
        second_class_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
