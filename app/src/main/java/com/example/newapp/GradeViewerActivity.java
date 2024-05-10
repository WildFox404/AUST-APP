package com.example.newapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;
import java.util.Map;


public class GradeViewerActivity extends AppCompatActivity {

    private static final String KEY_GRADE_YEAR = "gradeyear";
    private static final String KEY_GRADE_TERM = "gradeterm";
    private List<Map<String, String>> grade_result;
    private SharedViewModel viewModel;
    private User user;
    private MyDBHelper myDBHelper;
    int term=0;
    int year=0;
    String savedYear; // 从SharedPreferences中获取字符串值
    String savedTerm;

    private String savedUsername;
    private String savedPassword;
    private SharedPreferences sharedPreferences;
    private void saveSelection(String key, String value) {
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gradeview);
        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        savedUsername = sharedPreferences.getString("username", "");
        savedPassword = sharedPreferences.getString("password", "");

        User user=viewModel.getUser();
        myDBHelper = new MyDBHelper(this); // 实例化 MyDBHelper 对象

        ImageView exitButton =findViewById(R.id.exitButton);

        Spinner yearSpinner = findViewById(R.id.classYearSpinner);
        Spinner termSpinner = findViewById(R.id.classTermSpinner);

        savedYear = sharedPreferences.getString(KEY_GRADE_YEAR, "0"); // 从SharedPreferences中获取字符串值
        savedTerm = sharedPreferences.getString(KEY_GRADE_TERM, "0");
        yearSpinner.setSelection(getIndex(yearSpinner, savedYear));
        termSpinner.setSelection(getIndex(termSpinner, savedTerm));

        String firstFourChars;
        if (savedYear.length() >= 5) {
            firstFourChars = savedYear.substring(0,4);
            // 继续你的代码
        } else {
            // 处理字符串长度不足的情况
            firstFourChars = "2024";
        }
        int number = Integer.parseInt(firstFourChars); // 转换成int类型
        year = number;
        if (savedTerm.equals("第一学期")) {
            term = 1;
        } else if (savedTerm.equals("第二学期")) {
            term = 2;
        } else {
            // 其他情况的处理
        }


        UpdateGradeTable();


        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedYear = (String) parent.getItemAtPosition(position);
                String savedYear = sharedPreferences.getString(KEY_GRADE_YEAR, "0");
                if (!selectedYear.equals(savedYear)) { // 判断选择后的选项和之前保存的选项是否一致
                    saveSelection(KEY_GRADE_YEAR, selectedYear);
                    String firstFourChars = selectedYear.substring(0, 4); // 获取前四个字符
                    int number = Integer.parseInt(firstFourChars); // 转换成int类型
                    year = number;
                    Log.d("开始更新课表", "开始更新课表: ");
                    UpdateGradeTable();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有选择任何项时触发此方法
            }
        });

        termSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTerm = (String) parent.getItemAtPosition(position);
                String savedTerm = sharedPreferences.getString(KEY_GRADE_TERM, "0");

                if (!selectedTerm.equals(savedTerm)) { // 判断选择后的选项和之前保存的选项是否一致
                    saveSelection(KEY_GRADE_TERM, selectedTerm);

                    if (selectedTerm.equals("第一学期")) {
                        term = 1;
                    } else if (selectedTerm.equals("第二学期")) {
                        term = 2;
                    } else {
                        // 其他情况的处理
                    }
                    Log.d("开始更新课表", "开始更新课表: ");
                    UpdateGradeTable();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有选择任何项时触发此方法
            }
        });
    }
    private String insertNewlines(String input) {
        StringBuilder output = new StringBuilder(input);
        int len = input.length();
        for (int i = 4; i < len; i += 5) {
            output.insert(i, "\n");
        }
        return output.substring(0, Math.min(output.length(), 20));
    }
    private void UpdateGradeTable() {
        String yearString = String.valueOf(year);
        String termString = String.valueOf(term);
        Log.d("成绩查询参数", "学年" + yearString + "学期" + termString);
        grade_result = myDBHelper.getGradeData(savedUsername, yearString, termString);
        // 清空之前的内容
        if (grade_result != null && grade_result.size() > 0) {
            TableLayout tableLayout = findViewById(R.id.grade_tableLayout);
            TableLayout headertable = findViewById(R.id.headerRow);
            // 其他代码...
            // 清除表格中的旧数据
            tableLayout.removeAllViews();
            headertable.removeAllViews();
            // 动态创建表头
            TableRow headerRow = new TableRow(this);
            // 创建表头的每一列
            // 创建并添加课程类别列
            TextView courseCategoryHeader = new TextView(this);
            courseCategoryHeader.setText("课程类别");
            courseCategoryHeader.setTextSize(12); // 设置字号为12号
            headerRow.addView(courseCategoryHeader);

            // 创建并添加最终成绩列
            TextView finalGradeHeader = new TextView(this);
            finalGradeHeader.setText("最终成绩");
            finalGradeHeader.setTextSize(12); // 设置字号为12号
            headerRow.addView(finalGradeHeader);

            // 创建并添加总评成绩列
            TextView totalScoreHeader = new TextView(this);
            totalScoreHeader.setText("总评成绩");
            totalScoreHeader.setTextSize(12); // 设置字号为12号
            headerRow.addView(totalScoreHeader);

            // 创建并添加课程名称列
            TextView courseNameHeader = new TextView(this);
            courseNameHeader.setText("课程名称");
            courseNameHeader.setTextSize(12); // 设置字号为12号
            headerRow.addView(courseNameHeader);

            // 创建并添加学分列
            TextView creditHeader = new TextView(this);
            creditHeader.setText("学分");
            creditHeader.setTextSize(12); // 设置字号为12号
            headerRow.addView(creditHeader);

            // 创建并添加绩点列
            TextView GPAHeader = new TextView(this);
            GPAHeader.setText("绩点");
            GPAHeader.setTextSize(12); // 设置字号为12号
            headerRow.addView(GPAHeader);

            // 将表头行添加到表格中
            headertable.addView(headerRow);
            int i=0;
            // 动态创建并填充表格行
            for (Map<String, String> data : grade_result) {
                TableRow row = new TableRow(this);

                // 创建并填充每一列
                TextView courseCategoryColumn = new TextView(this);
                String courseCategory = data.get("课程类别");
                courseCategoryColumn.setText(courseCategory != null ? insertNewlines(courseCategory) : "");
                courseCategoryColumn.setTextSize(12); // 设置字号为12号
                courseCategoryColumn.setMinHeight(200);
                row.addView(courseCategoryColumn);

                TextView finalGradeColumn = new TextView(this);
                String finalGrade = data.get("最终");
                finalGradeColumn.setText(finalGrade != null ? insertNewlines(finalGrade) : "");
                finalGradeColumn.setTextSize(12); // 设置字号为12号
                finalGradeColumn.setMinHeight(200);
                row.addView(finalGradeColumn);

                TextView totalScoreColumn = new TextView(this);
                String totalScore = data.get("总评成绩");
                totalScoreColumn.setText(totalScore != null ? insertNewlines(totalScore) : "");
                totalScoreColumn.setTextSize(12); // 设置字号为12号
                totalScoreColumn.setMinHeight(200);
                row.addView(totalScoreColumn);

                TextView courseNameColumn = new TextView(this);
                String courseName = data.get("课程名称");
                courseNameColumn.setText(courseName != null ? insertNewlines(courseName) : "");
                courseNameColumn.setTextSize(12); // 设置字号为12号
                courseNameColumn.setMinHeight(200);
                row.addView(courseNameColumn);

                TextView creditColumn = new TextView(this);
                String credit = data.get("学分");
                creditColumn.setText(credit != null ? insertNewlines(credit) : "");
                creditColumn.setTextSize(12); // 设置字号为12号
                creditColumn.setMinHeight(200);
                row.addView(creditColumn);

                TextView GPAColumn = new TextView(this);
                String GPA = data.get("绩点");
                GPAColumn.setText(GPA != null ? insertNewlines(GPA) : "");
                GPAColumn.setTextSize(12); // 设置字号为12号
                GPAColumn.setMinHeight(200);
                row.addView(GPAColumn);
                // 设置偶数行背景色为#BCBCBC
                if (i % 2 == 1) {
                    row.setBackgroundColor(Color.parseColor("#22BFFFFF"));
                }
                // 将表格行添加到表格中
                tableLayout.addView(row);
                i+=1;
            }
        } else {
            Log.d("UserCreation", "Result is null or empty");
        }
    }
}
