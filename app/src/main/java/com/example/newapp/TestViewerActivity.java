package com.example.newapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class TestViewerActivity extends AppCompatActivity {
    private List<Map<String, String>> grade_result;
    private SharedViewModel viewModel;
    private User user;
    private MyDBHelper myDBHelper;
    int term=0;
    int year=0;
    String savedYear; // 从SharedPreferences中获取字符串值
    String savedTerm;
    List<Map<String, String>> test_result;

    private String savedUsername;
    private String savedPassword;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testview);
        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        savedUsername = sharedPreferences.getString("username", "");
        savedPassword = sharedPreferences.getString("password", "");

        ImageView exitButton =findViewById(R.id.exitButton);

        User user = viewModel.getUser();
        myDBHelper = new MyDBHelper(this); // 实例化 MyDBHelper 对象

        String firstFourChars = savedUsername.substring(0, 4); // 获取前四个字符
        int number = Integer.parseInt(firstFourChars); // 转换成int类型
        year = number;
        GetTestTable();
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void GetTestTable() {
        TableLayout testViewer = findViewById(R.id.test_tableLayout);
        TableLayout headertable = findViewById(R.id.headerRow);
        int i =0 ;
        // 动态创建表头
        TableRow headerRow = new TableRow(this);
        // 创建表头的每一列
        // 创建并添加课程类别列
        TextView courseCategoryHeader = new TextView(this);
        courseCategoryHeader.setText("课程名称");
        headerRow.addView(courseCategoryHeader);

        // 创建并添加最终成绩列
        TextView finalGradeHeader = new TextView(this);
        finalGradeHeader.setText("考试日期");
        headerRow.addView(finalGradeHeader);

        // 创建并添加总评成绩列
        TextView totalScoreHeader = new TextView(this);
        totalScoreHeader.setText("考试安排");
        headerRow.addView(totalScoreHeader);

        // 创建并添加课程名称列
        TextView courseNameHeader = new TextView(this);
        courseNameHeader.setText("考试地点");
        headerRow.addView(courseNameHeader);

        // 创建并添加学分列
        TextView creditHeader = new TextView(this);
        creditHeader.setText("座位");
        headerRow.addView(creditHeader);

        // 创建并添加绩点列
        TextView GPAHeader = new TextView(this);
        GPAHeader.setText("情况");
        headerRow.addView(GPAHeader);

        // 将表头行添加到表格中
        headertable.addView(headerRow);

        test_result=myDBHelper.getTestData(savedUsername.toString());
        for (Map<String, String> data : test_result) {
            TableRow row = new TableRow(this);

            // 创建并填充每一列
            TextView courseCategoryColumn = new TextView(this);
            String courseCategory = data.get("课程名称");
            courseCategoryColumn.setText(courseCategory != null ? insertNewlines(courseCategory) : "");
            courseCategoryColumn.setTextSize(11); // 设置字号为12号
            courseCategoryColumn.setMinHeight(200);
            row.addView(courseCategoryColumn);

            TextView finalGradeColumn = new TextView(this);
            String finalGrade = data.get("考试日期");
            finalGradeColumn.setText(finalGrade != null ? insertNewlines(finalGrade) : "");
            finalGradeColumn.setTextSize(11); // 设置字号为12号
            finalGradeColumn.setMinHeight(200);
            row.addView(finalGradeColumn);

            TextView totalScoreColumn = new TextView(this);
            String totalScore = data.get("考试安排");
            totalScoreColumn.setText(totalScore != null ? insertNewlines(totalScore) : "");
            totalScoreColumn.setTextSize(11); // 设置字号为12号
            totalScoreColumn.setMinHeight(200);
            row.addView(totalScoreColumn);

            TextView courseNameColumn = new TextView(this);
            String courseName = data.get("考试地点");
            courseNameColumn.setText(courseName != null ? insertNewlines(courseName) : "");
            courseNameColumn.setTextSize(11); // 设置字号为12号
            courseNameColumn.setMinHeight(200);
            row.addView(courseNameColumn);

            TextView creditColumn = new TextView(this);
            String credit = data.get("考场座位");
            creditColumn.setText(credit != null ? insertNewlines(credit) : "");
            creditColumn.setTextSize(11); // 设置字号为12号
            creditColumn.setMinHeight(200);
            row.addView(creditColumn);

            TextView GPAColumn = new TextView(this);
            String GPA = data.get("考试情况");
            GPAColumn.setText(GPA != null ? insertNewlines(GPA) : "");
            GPAColumn.setTextSize(11); // 设置字号为12号
            GPAColumn.setMinHeight(200);
            row.addView(GPAColumn);

            // 将表格行添加到表格中
            testViewer.addView(row);
        }
    }

    private String insertNewlines(String input) {
        StringBuilder output = new StringBuilder(input);
        int len = input.length();
        for (int i = 4; i < len; i += 5) {
            output.insert(i, "\n");
        }
        return output.substring(0, Math.min(output.length(), 20));
    }
}


