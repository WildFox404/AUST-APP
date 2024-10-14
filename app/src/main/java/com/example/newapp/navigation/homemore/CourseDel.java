package com.example.newapp.navigation.homemore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.newapp.R;
import com.example.newapp.db.MyDBHelper;
import com.example.newapp.entries.User;
import com.example.newapp.navigation.RoundedColorTextView;

public class CourseDel extends AppCompatActivity {
    private static final String KEY_YEAR = "class_year";
    private static final String KEY_TERM = "class_term";
    int term=0;
    int year=0;
    String savedYear; // 从SharedPreferences中获取字符串值
    String savedTerm;
    private MyDBHelper myDBHelper;
    private SharedPreferences sharedPreferences;
    private User user = User.getInstance();
    Spinner yearSpinner;
    Spinner termSpinner;
    LinearLayout linearlayout_content;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coursedel);
        myDBHelper = new MyDBHelper(this); // 实例化 MyDBHelper 对象

        sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // 从 SharedPreferences 中加载上次存储的数据
        savedYear = sharedPreferences.getString(KEY_YEAR, "0"); // 从SharedPreferences中获取字符串值
        savedTerm = sharedPreferences.getString(KEY_TERM, "0");

        ImageView exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        linearlayout_content = findViewById(R.id.linearlayout_content);

        yearSpinner = findViewById(R.id.classYearSpinner);
        termSpinner = findViewById(R.id.classTermSpinner);

        ArrayAdapter<String> adapter = null;
        if (user != null && user.getYears() != null) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, user.getYears());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
        if (adapter != null && yearSpinner != null) {
            yearSpinner.setAdapter(adapter);
            yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedYear = (String) parent.getItemAtPosition(position);
                    String savedYear = sharedPreferences.getString(KEY_YEAR, "0");
                    if (!selectedYear.equals(savedYear)) { // 判断选择后的选项和之前保存的选项是否一致
                        saveSelection(KEY_YEAR, selectedYear);
                        String firstFourChars = selectedYear.substring(0, 4); // 获取前四个字符
                        int number = Integer.parseInt(firstFourChars); // 转换成int类型
                        year = (number - 2020) * 40 + 100;
                        UpdateLinearLayout();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // 当没有选择任何项时触发此方法
                }
            });
        }

        termSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTerm = (String) parent.getItemAtPosition(position);
                String savedTerm = sharedPreferences.getString(KEY_TERM, "0");

                if (!selectedTerm.equals(savedTerm)) { // 判断选择后的选项和之前保存的选项是否一致
                    saveSelection(KEY_TERM, selectedTerm);

                    if (selectedTerm.equals("第一学期")) {
                        term=0;
                    } else if (selectedTerm.equals("第二学期")) {
                        term=20;
                    } else {
                        term=0;
                    }
                    Log.d("开始更新课表", "开始更新课表: ");
                    UpdateLinearLayout();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有选择任何项时触发此方法
            }
        });

        yearSpinner.setSelection(getIndex(yearSpinner, savedYear));
        termSpinner.setSelection(getIndex(termSpinner, savedTerm));

        int number;
        try {
            String firstFourChars = savedYear.substring(0, 4); // 获取前四个字符
            number = Integer.parseInt(firstFourChars); // 转换成int类型
        }catch (Exception e){
            Log.d("考试安排", "考试安排初始化出现问题");
            number=user.getCoverage_year();
        }
        year=(number-2020)*40+100;

        if (savedTerm.equals("第一学期")) {
            term = 0;
        } else if (savedTerm.equals("第二学期")) {
            term = 20;
        } else {
            term = 0;
        }
        UpdateLinearLayout();
    }

    private void UpdateLinearLayout() {
        linearlayout_content.removeAllViews();
        // 在Activity中获取LayoutInflater实例
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Cursor cursor = myDBHelper.getCourseByUsernameAndSemesterId(user.getUsername(), String.valueOf(year+term));
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // 从 cursor 中提取数据
                String idContent = cursor.getString(cursor.getColumnIndex("id"));
                String courseName = cursor.getString(cursor.getColumnIndex("course_name"));
                String weeksContent = cursor.getString(cursor.getColumnIndex("weeks"));
                String startUnit = cursor.getString(cursor.getColumnIndex("start_unit"));
                String courseAddress = cursor.getString(cursor.getColumnIndex("course_address"));
                String courseTeacher = cursor.getString(cursor.getColumnIndex("course_teacher"));
                String weekNum = cursor.getString(cursor.getColumnIndex("week_num"));
                // 处理获取到的数据
                Log.d("HomeFragment", "courseName: "+idContent.toString());
                Log.d("HomeFragment", "courseName: "+courseName.toString());
                Log.d("HomeFragment", "weeks: "+weeksContent.toString());
                Log.d("HomeFragment", "startUnit: "+startUnit.toString());
                Log.d("HomeFragment", "courseAddress: "+courseAddress.toString());
                Log.d("HomeFragment", "courseTeacher: "+courseTeacher.toString());
                Log.d("HomeFragment", "week_num: "+weekNum.toString());

                View view =new View(this);
                view = inflater.inflate(R.layout.coursedelcontent, null);
                TextView course_name =view.findViewById(R.id.course_name);
                course_name.setText(courseName);
                TextView address =view.findViewById(R.id.address);
                address.setText(courseAddress);
                TextView teacher =view.findViewById(R.id.teacher);
                teacher.setText(courseTeacher);
                TextView id =view.findViewById(R.id.id);
                id.setText("id:"+idContent);
                TextView weeks =view.findViewById(R.id.weeks);
                weeks.setText("周数:"+weeksContent);
                TextView week_num =view.findViewById(R.id.week_num);
                week_num.setText("星期几:"+weekNum);
                TextView section =view.findViewById(R.id.section);
                section.setText("节课:"+startUnit);
                TextView delete_button = view.findViewById(R.id.delete_button);
                delete_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(myDBHelper.deleteCourseById(idContent)){
                            UpdateLinearLayout();
                        }
                    }
                });

                linearlayout_content.addView(view);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    private void saveSelection(String key, String value) {
        Log.d("CourseAdd", "saveSelection: "+key+":"+value);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private int getIndex(Spinner spinner, String item) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(item)) {
                return i;
            }
        }
        return 0; // 默认返回第一个选项
    }
}
