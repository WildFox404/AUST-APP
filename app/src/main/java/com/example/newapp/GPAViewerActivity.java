package com.example.newapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

public class GPAViewerActivity extends AppCompatActivity {


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

    float gpaTotal=0;

    float creditTotal=0;
    float scoreTotal=0;
    private String savedUsername;
    private String savedPassword;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gpaview);
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
        GetGpaTable();

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void GetGpaTable() {
        TableLayout gpaViewer = findViewById(R.id.gpa_tableLayout);
        TableLayout headertable = findViewById(R.id.headerRow);
        int i =0 ;
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
        for (int y = year+4; y >= year; y--) {
            for (int term = 2; term >= 1; term--) {
                // 创建线程并启动
                String yearString = String.valueOf(y);
                String termString = String.valueOf(term);
                grade_result=myDBHelper.getGradeData(savedUsername.toString(),yearString,termString);
                if (grade_result != null && grade_result.size() > 1) {
                    TextView headCategory = new TextView(this);
                    headCategory.setText(String.valueOf(y)+"-"+String.valueOf(y+1)+'\t'+"第"+termString+"学期");
                    headCategory.setTextSize(20); // 设置字号为12号
                    headCategory.setTextColor(Color.parseColor("#344CAA"));
                    gpaViewer.addView(headCategory);
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

                        float totalScore_result = totalScore != null ? Float.parseFloat(totalScore) : 0.0f; // 将学分转换为浮点数，如果为null则设为0.0
                        if(totalScore_result>=60){
                            float credit_result = credit != null ? Float.parseFloat(credit) : 0.0f; // 将学分转换为浮点数，如果为null则设为0.0
                            float GPA_result = GPA != null ? Float.parseFloat(GPA) : 0.0f; // 将绩点转换为浮点数，如果为null则设为0.0
                            gpaTotal+=GPA_result*credit_result;
                            creditTotal+=credit_result;
                            scoreTotal+=totalScore_result*credit_result;
                        }
                        // 设置偶数行背景色为#BCBCBC
                        if (i % 2 == 1) {
                            row.setBackgroundColor(Color.parseColor("#22BFFFFF"));
                        }
                        // 将表格行添加到表格中
                        gpaViewer.addView(row);
                        i+=1;
                    }
                }
            }
        }

        TextView gpaResult = findViewById(R.id.gpaResult);
        float gpaAverage = gpaTotal / (float) creditTotal; // 计算平均GPA
        float scoreAverage = scoreTotal / (float) creditTotal; // 计算平均GPA
        DecimalFormat df = new DecimalFormat("0.00"); // 格式化为两位小数的 DecimalFormat 对象
        String formattedGPA = df.format(gpaAverage); // 格式化结果为字符串
        String formattedScore = df.format(scoreAverage); // 格式化结果为字符串
        gpaResult.setText("当前GPA: " + formattedGPA+"\n加权平均分:"+formattedScore);
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
