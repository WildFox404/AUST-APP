package com.example.courseapp;

import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ConfigFragment configFragment;
    private ClassFragment classFragment;
    private GradeFragment gradeFragment;
    private TestFragment testFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configFragment = new ConfigFragment();
        classFragment = new ClassFragment();
        gradeFragment = new GradeFragment();
        testFragment = new TestFragment();

        // 设置默认显示的Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, configFragment).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_config) {
                selectedFragment = configFragment;
            } else if (itemId == R.id.navigation_class) {
                selectedFragment = classFragment;
            } else if (itemId == R.id.navigation_grade) {
                selectedFragment = gradeFragment;
            } else if (itemId == R.id.navigation_test) {
                selectedFragment = testFragment;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            return true;
        });
    }
}